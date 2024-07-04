package org.cotato.csquiz.domain.generation.service;

import jakarta.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
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
import org.cotato.csquiz.api.session.dto.UpdateSessionNumberRequest;
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
        Generation findGeneration = generationRepository.findById(request.generationId())
                .orElseThrow(() -> new EntityNotFoundException("해당 기수를 찾을 수 없습니다."));

        int sessionNumber = calculateLastSessionNumber(findGeneration);
        log.info("해당 기수에 추가된 마지막 세션 : {}", sessionNumber);

        Session session = Session.builder()
                .number(sessionNumber + 1)
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

        if (request.photos() != null && !request.photos().isEmpty()) {
            AtomicInteger index = new AtomicInteger(0);

            List<SessionPhoto> sessionPhotos = new ArrayList<>();

            for (MultipartFile photoFile : request.photos()) {
                S3Info s3Info = s3Uploader.uploadFiles(photoFile, SESSION_BUCKET_DIRECTORY);

                SessionPhoto sessionPhoto = SessionPhoto.builder()
                        .session(savedSession)
                        .s3Info(s3Info)
                        .order(index.getAndIncrement())
                        .build();

                sessionPhotos.add(sessionPhoto);
            }

            sessionPhotoRepository.saveAll(sessionPhotos);
            log.info("세션 이미지 생성 완료");
        }

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
    public void updateSession(UpdateSessionRequest request) {
        Session session = findSessionById(request.sessionId());

        session.updateDescription(request.description());
        session.updateSessionTitle(request.title());
        session.updateSessionContents(SessionContents.builder()
                .csEducation(request.csEducation())
                .devTalk(request.devTalk())
                .itIssue(request.itIssue())
                .networking(request.networking())
                .build());

        sessionRepository.save(session);
    }

    @Transactional
    public AddSessionPhotoResponse additionalSessionPhoto(AddSessionPhotoRequest request) throws ImageException {
        Session session = findSessionById(request.sessionId());

        S3Info imageInfo = s3Uploader.uploadFiles(request.photo(), SESSION_BUCKET_DIRECTORY);

        Integer imageOrder = sessionPhotoRepository.findFirstBySessionOrderByOrderDesc(session)
                .map(sessionPhoto -> sessionPhoto.getOrder() + 1).orElse(0);

        SessionPhoto sessionPhoto = SessionPhoto.builder()
                .session(session)
                .s3Info(imageInfo)
                .order(imageOrder)
                .build();

        return AddSessionPhotoResponse.from(sessionPhotoRepository.save(sessionPhoto));
    }

    @Transactional
    public void deleteSessionPhoto(DeleteSessionPhotoRequest request) {
        SessionPhoto deletePhoto = sessionPhotoRepository.findById(request.photoId())
                .orElseThrow(() -> new EntityNotFoundException("해당 사진을 찾을 수 없습니다."));
        s3Uploader.deleteFile(deletePhoto.getS3Info());
        sessionPhotoRepository.delete(deletePhoto);

        List<SessionPhoto> reOrderPhotos = sessionPhotoRepository.findAllBySession(deletePhoto.getSession()).stream()
                .filter(photo -> photo.getOrder() > deletePhoto.getOrder())
                .toList();

        for (SessionPhoto sessionPhoto : reOrderPhotos) {
            sessionPhoto.decreaseOrder();
        }
    }

    @Transactional
    public void updateSessionPhotoOrder(UpdateSessionPhotoOrderRequest request) {
        Session sessionById = findSessionById(request.sessionId());
        List<UpdateSessionPhotoOrderInfoRequest> orderList = request.orderInfos();

        List<SessionPhoto> savedPhotos = sessionPhotoRepository.findAllBySession(sessionById);

        if (savedPhotos.size() != orderList.size()) {
            throw new AppException(ErrorCode.SESSION_PHOTO_COUNT_MISMATCH);
        }

        if (checkValidOrderRange(orderList)) {
            throw new AppException(ErrorCode.SESSION_ORDER_INVALID);
        }

        if (!checkOrderUnique(orderList)) {
            throw new AppException(ErrorCode.SESSION_ORDER_INVALID);
        }

        Map<Long, UpdateSessionPhotoOrderInfoRequest> orderMap = orderList.stream()
                .collect(Collectors.toMap(UpdateSessionPhotoOrderInfoRequest::photoId, Function.identity()));

        for (SessionPhoto savedPhoto : savedPhotos) {
            if (orderMap.get(savedPhoto.getId()) == null) {
                throw new AppException(ErrorCode.SESSION_PHOTO_NOT_EXIST);
            }
            savedPhoto.updateOrder(orderMap.get(savedPhoto.getId()).order());
        }
    }

    private boolean checkValidOrderRange(List<UpdateSessionPhotoOrderInfoRequest> orderList) {
        return orderList.stream().noneMatch(orderInfo ->
                orderInfo.order() < 0 || orderInfo.order() >= orderList.size());
    }

    private boolean checkOrderUnique(List<UpdateSessionPhotoOrderInfoRequest> orderList) {
        Set<Integer> uniqueOrders = new HashSet<>();
        for (UpdateSessionPhotoOrderInfoRequest orderInfo : orderList) {
            if (!uniqueOrders.add(orderInfo.order())) {
                return false;
            }
        }

        return true;
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
}
