package org.cotato.csquiz.domain.generation.service;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cotato.csquiz.api.session.dto.AddSessionPhotoResponse;
import org.cotato.csquiz.api.session.dto.DeleteSessionPhotoRequest;
import org.cotato.csquiz.api.session.dto.UpdateSessionPhotoOrderInfoRequest;
import org.cotato.csquiz.api.session.dto.UpdateSessionPhotoOrderRequest;
import org.cotato.csquiz.api.session.dto.AddSessionPhotoRequest;
import org.cotato.csquiz.api.session.dto.AddSessionRequest;
import org.cotato.csquiz.api.session.dto.AddSessionResponse;
import org.cotato.csquiz.api.session.dto.CsEducationOnSessionNumberResponse;
import org.cotato.csquiz.api.session.dto.SessionListResponse;
import org.cotato.csquiz.api.session.dto.UpdateSessionDescriptionRequest;
import org.cotato.csquiz.api.session.dto.UpdateSessionNumberRequest;
import org.cotato.csquiz.api.session.dto.UpdateSessionPhotoRequest;
import org.cotato.csquiz.api.session.dto.UpdateSessionRequest;
import org.cotato.csquiz.common.entity.S3Info;
import org.cotato.csquiz.common.error.ErrorCode;
import org.cotato.csquiz.common.error.exception.AppException;
import org.cotato.csquiz.domain.education.entity.Education;
import org.cotato.csquiz.domain.education.service.EducationService;
import org.cotato.csquiz.domain.generation.embedded.SessionContents;
import org.cotato.csquiz.domain.generation.entity.SessionPhoto;
import org.cotato.csquiz.domain.generation.enums.CSEducation;
import org.cotato.csquiz.domain.generation.entity.Generation;
import org.cotato.csquiz.domain.generation.entity.Session;
import org.cotato.csquiz.common.error.exception.ImageException;
import org.cotato.csquiz.common.S3.S3Uploader;
import org.cotato.csquiz.domain.generation.repository.GenerationRepository;
import org.cotato.csquiz.domain.generation.repository.SessionPhotoRepository;
import org.cotato.csquiz.domain.generation.repository.SessionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class SessionService {

    private static final String SESSION_BUCKET_DIRECTORY = "session";
    private final SessionRepository sessionRepository;
    private final GenerationRepository generationRepository;
    private final SessionPhotoRepository sessionPhotoRepository;
    private final EducationService educationService;
    private final S3Uploader s3Uploader;

    @Transactional
    public AddSessionResponse addSession(AddSessionRequest request) throws ImageException {
        S3Info s3Info = null;
        if (isImageExist(request.sessionImage())) {
            s3Info = s3Uploader.uploadFiles(request.sessionImage(), SESSION_BUCKET_DIRECTORY);
        }
        Generation findGeneration = generationRepository.findById(request.generationId())
                .orElseThrow(() -> new EntityNotFoundException("해당 기수를 찾을 수 없습니다."));

        int sessionNumber = calculateLastSessionNumber(findGeneration);
        log.info("해당 기수에 추가된 마지막 세션 : {}", sessionNumber);
        Session session = Session.builder()
                .number(sessionNumber + 1)
                .s3Info(s3Info)
                .description(request.description())
                .generation(findGeneration)
                .title(request.title())
                .sessionContents(SessionContents.builder()
                        .csEducation(request.csEducation())
                        .devTalk(request.devTalk())
                        .itIssue(request.itIssue())
                        .networking(request.networking())
                        .build())
                .build();
        Session savedSession = sessionRepository.save(session);
        log.info("세션 생성 완료");

        return AddSessionResponse.from(savedSession);
    }

    private int calculateLastSessionNumber(Generation generation) {
        List<Session> allSession = sessionRepository.findAllByGeneration(generation);
        return allSession.stream().mapToInt(Session::getNumber).max()
                .orElse(-1);
    }

    @Transactional
    public void updateSessionNumber(UpdateSessionNumberRequest request) {
        Session session = findSessionById(request.sessionId());
        session.changeSessionNumber(session.getNumber());
    }

    @Transactional
    public void updateSessionDescription(UpdateSessionDescriptionRequest request) {
        Session session = findSessionById(request.sessionId());
        session.updateDescription(request.description());
    }

    @Transactional
    public void updateSession(UpdateSessionRequest request) throws ImageException {
        Session session = findSessionById(request.sessionId());

        session.updateDescription(request.description());
        session.updateSessionTitle(request.title());
        session.updateSessionContents(SessionContents.builder()
                .csEducation(request.csEducation())
                .devTalk(request.devTalk())
                .itIssue(request.itIssue())
                .networking(request.networking())
                .build());
        if (request.isPhotoUpdated()) {
            updatePhoto(session, request.sessionImage());
        }

        sessionRepository.save(session);
    }

    @Transactional
    public void updateSessionPhoto(UpdateSessionPhotoRequest request) throws ImageException {
        Session session = findSessionById(request.sessionId());
        updatePhoto(session, request.sessionImage());
    }

    @Transactional
    public AddSessionPhotoResponse additionalSessionPhoto(AddSessionPhotoRequest request) throws ImageException {
        Session session = findSessionById(request.sessionId());

        S3Info imageInfo = s3Uploader.uploadFiles(request.sessionImage(), "session");

        Integer imageOrder = sessionPhotoRepository.findFirstBySessionOrderByOrderDesc(session)
                .map(sessionPhoto -> sessionPhoto.getOrder() + 1).orElse(1);

        SessionPhoto sessionPhoto = SessionPhoto.builder()
                .session(session)
                .s3Info(imageInfo)
                .order(imageOrder)
                .build();

        return AddSessionPhotoResponse.from(sessionPhotoRepository.save(sessionPhoto));
    }

    @Transactional
    public void deleteSessionPhoto(DeleteSessionPhotoRequest request) {
        SessionPhoto sessionPhoto = sessionPhotoRepository.findById(request.photoId())
                .orElseThrow(() -> new EntityNotFoundException("해당 사진을 찾을 수 없습니다."));
        s3Uploader.deleteFile(sessionPhoto.getS3Info());
        sessionPhotoRepository.delete(sessionPhoto);
    }

    @Transactional
    public void updateSessionPhotoOrder(UpdateSessionPhotoOrderRequest request) {
        Session session = findSessionById(request.sessionId());
        List<UpdateSessionPhotoOrderInfoRequest> orderList = request.orderInfos();

        List<SessionPhoto> savedPhotos = sessionPhotoRepository.findAllBySession(session);

        if (savedPhotos.size() != orderList.size()) {
            throw new AppException(ErrorCode.SESSION_PHOTO_COUNT_MISMATCH);
        }

        Map<Long, UpdateSessionPhotoOrderInfoRequest> orderMap = orderList.stream()
                .filter(orderInfo -> isOrderValid(orderInfo,savedPhotos.size()))
                .collect(Collectors.toMap(UpdateSessionPhotoOrderInfoRequest::photoId, Function.identity()));

        savedPhotos.forEach(sessionPhoto -> {
            updatePhotoOrder(sessionPhoto, orderMap);
        });
    }

    private boolean isOrderValid(UpdateSessionPhotoOrderInfoRequest orderInfo, int totalSize) {
        if (orderInfo.order() < 1 || orderInfo.order() > totalSize) {
            throw new AppException(ErrorCode.SESSION_ORDER_INVALID);
        }
        return true;
    }

    private static void updatePhotoOrder(SessionPhoto sessionPhoto, Map<Long, UpdateSessionPhotoOrderInfoRequest> orderMap) {
        UpdateSessionPhotoOrderInfoRequest orderInfo = orderMap.get(sessionPhoto.getId());

        if (orderInfo == null) {
            throw new AppException(ErrorCode.SESSION_PHOTO_NOT_EXIST);
        }
        sessionPhoto.updateOrder(orderInfo.order());
    }

    private void updatePhoto(Session session, MultipartFile sessionImage) throws ImageException {
        if (isImageExist(sessionImage)) {
            S3Info s3Info = s3Uploader.uploadFiles(sessionImage, SESSION_BUCKET_DIRECTORY);
            deleteOldImage(session);
            session.changePhotoUrl(s3Info);
        }
        if (!isImageExist(sessionImage)) {
            deleteOldImage(session);
            session.changePhotoUrl(null);
        }
    }

    private void deleteOldImage(Session session) {
        if (session.getPhotoS3Info() != null) {
            s3Uploader.deleteFile(session.getPhotoS3Info());
        }
    }

    public List<SessionListResponse> findSessionsByGenerationId(Long generationId) {
        Generation generation = generationRepository.findById(generationId)
                .orElseThrow(() -> new EntityNotFoundException("해당 기수를 찾을 수 없습니다."));

        List<Session> sessions = sessionRepository.findAllByGeneration(generation);

        return sessions.stream()
                .map(SessionListResponse::from)
                .toList();
    }

    public Session findSessionById(Long sessionId) {
        return sessionRepository.findById(sessionId)
                .orElseThrow(() -> new EntityNotFoundException("해당 세션을 찾을 수 없습니다."));
    }

    public List<CsEducationOnSessionNumberResponse> findAllNotLinkedCsOnSessionsByGenerationId(Long generationId) {
        Generation generation = generationRepository.findById(generationId)
                .orElseThrow(() -> new EntityNotFoundException("해당 기수를 찾을 수 없습니다."));
        List<Session> sessions = sessionRepository.findAllByGenerationAndSessionContentsCsEducation(generation, CSEducation.CS_ON);

        List<Long> educationLinkedSessionIds = educationService.findAllEducationByGenerationId(generationId).stream()
                .map(Education::getSessionId)
                .toList();

        return sessions.stream()
                .filter(session -> !educationLinkedSessionIds.contains(session.getId()))
                .map(CsEducationOnSessionNumberResponse::from)
                .toList();
    }

    private boolean isImageExist(MultipartFile sessionImage) {
        return sessionImage != null && !sessionImage.isEmpty();
    }
}
