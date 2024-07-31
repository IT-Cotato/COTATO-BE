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
import org.cotato.csquiz.api.session.dto.AddSessionImageResponse;
import org.cotato.csquiz.api.session.dto.DeleteSessionImageRequest;
import org.cotato.csquiz.api.session.dto.UpdateSessionImageOrderInfoRequest;
import org.cotato.csquiz.api.session.dto.UpdateSessionImageOrderRequest;
import org.cotato.csquiz.api.session.dto.AddSessionImageRequest;
import org.cotato.csquiz.api.session.dto.AddSessionRequest;
import org.cotato.csquiz.api.session.dto.AddSessionResponse;
import org.cotato.csquiz.api.session.dto.CsEducationOnSessionNumberResponse;
import org.cotato.csquiz.api.session.dto.SessionListResponse;
import org.cotato.csquiz.api.session.dto.UpdateSessionNumberRequest;
import org.cotato.csquiz.api.session.dto.UpdateSessionRequest;
import org.cotato.csquiz.common.entity.S3Info;
import org.cotato.csquiz.common.error.ErrorCode;
import org.cotato.csquiz.common.error.exception.AppException;
import org.cotato.csquiz.domain.attendance.entity.Attendance;
import org.cotato.csquiz.domain.attendance.service.AttendanceAdminService;
import org.cotato.csquiz.domain.education.entity.Education;
import org.cotato.csquiz.domain.education.service.EducationService;
import org.cotato.csquiz.domain.generation.embedded.SessionContents;
import org.cotato.csquiz.domain.generation.entity.SessionImage;
import org.cotato.csquiz.domain.generation.enums.CSEducation;
import org.cotato.csquiz.domain.generation.entity.Generation;
import org.cotato.csquiz.domain.generation.entity.Session;
import org.cotato.csquiz.common.error.exception.ImageException;
import org.cotato.csquiz.common.S3.S3Uploader;
import org.cotato.csquiz.domain.generation.repository.GenerationRepository;
import org.cotato.csquiz.domain.generation.repository.SessionImageRepository;
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
    private final SessionImageRepository sessionImageRepository;
    private final AttendanceAdminService attendanceAdminService;
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
                .placeName(request.placeName())
                .sessionDate(request.sessionDate())
                .sessionContents(SessionContents.builder()
                        .csEducation(request.csEducation())
                        .devTalk(request.devTalk())
                        .itIssue(request.itIssue())
                        .networking(request.networking())
                        .build())
                .build();
        Session savedSession = sessionRepository.save(session);
        log.info("세션 생성 완료");

        if (request.images() != null && !request.images().isEmpty()) {
            AtomicInteger index = new AtomicInteger(0);

            List<SessionImage> sessionImages = new ArrayList<>();

            for (MultipartFile imageFile : request.images()) {
                S3Info s3Info = s3Uploader.uploadFiles(imageFile, SESSION_BUCKET_DIRECTORY);

                SessionImage sessionImage = SessionImage.builder()
                        .session(savedSession)
                        .s3Info(s3Info)
                        .order(index.getAndIncrement())
                        .build();

                sessionImages.add(sessionImage);
            }

            sessionImageRepository.saveAll(sessionImages);
            log.info("세션 이미지 생성 완료");
        }

        attendanceAdminService.addAttendance(session, request.sessionDate() ,request.location(), request.attendanceDeadLine());

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
    public AddSessionImageResponse additionalSessionImage(AddSessionImageRequest request) throws ImageException {
        Session session = findSessionById(request.sessionId());

        S3Info imageInfo = s3Uploader.uploadFiles(request.image(), SESSION_BUCKET_DIRECTORY);

        Integer imageOrder = sessionImageRepository.findFirstBySessionOrderByOrderDesc(session)
                .map(sessionImage -> sessionImage.getOrder() + 1).orElse(0);

        SessionImage sessionImage = SessionImage.builder()
                .session(session)
                .s3Info(imageInfo)
                .order(imageOrder)
                .build();

        return AddSessionImageResponse.from(sessionImageRepository.save(sessionImage));
    }

    @Transactional
    public void deleteSessionImage(DeleteSessionImageRequest request) {
        SessionImage deleteImage = sessionImageRepository.findById(request.imageId())
                .orElseThrow(() -> new EntityNotFoundException("해당 사진을 찾을 수 없습니다."));
        s3Uploader.deleteFile(deleteImage.getS3Info());
        sessionImageRepository.delete(deleteImage);

        List<SessionImage> reorderImages = sessionImageRepository.findAllBySession(deleteImage.getSession()).stream()
                .filter(image -> image.getOrder() > deleteImage.getOrder())
                .toList();

        for (SessionImage sessionImage : reorderImages) {
            sessionImage.decreaseOrder();
        }
    }

    @Transactional
    public void updateSessionImageOrder(UpdateSessionImageOrderRequest request) {
        Session sessionById = findSessionById(request.sessionId());
        List<UpdateSessionImageOrderInfoRequest> orderList = request.orderInfos();

        List<SessionImage> savedImages = sessionImageRepository.findAllBySession(sessionById);

        if (savedImages.size() != orderList.size()) {
            throw new AppException(ErrorCode.SESSION_IMAGE_COUNT_MISMATCH);
        }

        if (!isValidOrderRange(orderList)) {
            throw new AppException(ErrorCode.SESSION_ORDER_INVALID);
        }

        if (!isOrderUnique(orderList)) {
            throw new AppException(ErrorCode.SESSION_ORDER_INVALID);
        }

        Map<Long, UpdateSessionImageOrderInfoRequest> orderMap = orderList.stream()
                .collect(Collectors.toMap(UpdateSessionImageOrderInfoRequest::imageId, Function.identity()));

        for (SessionImage savedImage : savedImages) {
            if (orderMap.get(savedImage.getId()) == null) {
                throw new EntityNotFoundException("해당 사진을 찾을 수 없습니다.");
            }
            savedImage.updateOrder(orderMap.get(savedImage.getId()).order());
        }
    }

    private boolean isValidOrderRange(List<UpdateSessionImageOrderInfoRequest> orderList) {
        return orderList.stream().noneMatch(orderInfo ->
                orderInfo.order() < 0 || orderInfo.order() >= orderList.size());
    }

    private boolean isOrderUnique(List<UpdateSessionImageOrderInfoRequest> orderList) {
        Set<Integer> uniqueOrders = new HashSet<>();
        for (UpdateSessionImageOrderInfoRequest orderInfo : orderList) {
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

        Map<Session, List<SessionImage>> imagesGroupBySession = sessionImageRepository.findAllBySessionIn(sessions).stream()
                .collect(Collectors.groupingBy(SessionImage::getSession));

        return sessions.stream()
                .map(session -> SessionListResponse.of(session,imagesGroupBySession.getOrDefault(session, List.of())))
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
