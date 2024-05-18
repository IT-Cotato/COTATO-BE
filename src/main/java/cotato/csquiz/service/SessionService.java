package cotato.csquiz.service;

import cotato.csquiz.controller.dto.session.AddSessionRequest;
import cotato.csquiz.controller.dto.session.AddSessionResponse;
import cotato.csquiz.controller.dto.session.CsEducationOnSessionNumberResponse;
import cotato.csquiz.controller.dto.session.SessionListResponse;
import cotato.csquiz.controller.dto.session.UpdateSessionDescriptionRequest;
import cotato.csquiz.controller.dto.session.UpdateSessionNumberRequest;
import cotato.csquiz.controller.dto.session.UpdateSessionPhotoRequest;
import cotato.csquiz.controller.dto.session.UpdateSessionRequest;
import cotato.csquiz.domain.entity.Education;
import cotato.csquiz.domain.entity.Generation;
import cotato.csquiz.domain.entity.Session;
import cotato.csquiz.domain.enums.CSEducation;
import cotato.csquiz.exception.ImageException;
import cotato.csquiz.global.S3.S3Uploader;
import cotato.csquiz.repository.GenerationRepository;
import cotato.csquiz.repository.SessionRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final EducationService educationService;
    private final S3Uploader s3Uploader;

    @Transactional
    public AddSessionResponse addSession(AddSessionRequest request) throws ImageException {
        String imageUrl = null;
        if (isImageExist(request.sessionImage())) {
            imageUrl = s3Uploader.uploadFiles(request.sessionImage(), SESSION_BUCKET_DIRECTORY);
        }
        Generation findGeneration = generationRepository.findById(request.generationId())
                .orElseThrow(() -> new EntityNotFoundException("해당 기수를 찾을 수 없습니다."));

        int sessionNumber = calculateLastSessionNumber(findGeneration);
        log.info("해당 기수에 추가된 마지막 세션 : {}", sessionNumber);
        Session session = Session.builder()
                .number(sessionNumber + 1)
                .photoUrl(imageUrl)
                .description(request.description())
                .generation(findGeneration)
                .itIssue(request.itIssue())
                .csEducation(request.csEducation())
                .networking(request.networking())
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
        session.updateToggle(request.itIssue(), request.csEducation(),
                request.networking());
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

    private void updatePhoto(Session session, MultipartFile sessionImage) throws ImageException {
        if (isImageExist(sessionImage)) {
            String imageUrl = s3Uploader.uploadFiles(sessionImage, SESSION_BUCKET_DIRECTORY);
            deleteOldImage(session);
            session.changePhotoUrl(imageUrl);
        }
        if (!isImageExist(sessionImage)) {
            deleteOldImage(session);
            session.changePhotoUrl(null);
        }
    }

    private void deleteOldImage(Session session) throws ImageException {
        if (session.getPhotoUrl() != null) {
            s3Uploader.deleteFile(session.getPhotoUrl());
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
        List<Session> sessions = sessionRepository.findAllByGenerationAndCsEducation(generation, CSEducation.CS_ON);

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
