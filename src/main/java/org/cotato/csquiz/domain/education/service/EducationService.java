package org.cotato.csquiz.domain.education.service;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cotato.csquiz.api.education.dto.AllEducationResponse;
import org.cotato.csquiz.api.education.dto.CreateEducationRequest;
import org.cotato.csquiz.api.education.dto.CreateEducationResponse;
import org.cotato.csquiz.api.education.dto.EducationIdOfQuizResponse;
import org.cotato.csquiz.api.education.dto.FindEducationStatusResponse;
import org.cotato.csquiz.api.education.dto.UpdateEducationRequest;
import org.cotato.csquiz.domain.education.entity.Education;
import org.cotato.csquiz.domain.education.entity.Quiz;
import org.cotato.csquiz.domain.education.repository.EducationRepository;
import org.cotato.csquiz.domain.education.repository.QuizRepository;
import org.cotato.csquiz.domain.generation.entity.Session;
import org.cotato.csquiz.common.error.exception.AppException;
import org.cotato.csquiz.common.error.ErrorCode;
import org.cotato.csquiz.domain.generation.repository.SessionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class EducationService {

    private final EducationRepository educationRepository;
    private final QuizRepository quizRepository;
    private final SessionRepository sessionRepository;

    @Transactional
    public CreateEducationResponse createEducation(CreateEducationRequest request) {
        Session findSession = sessionRepository.findById(request.sessionId())
                .orElseThrow(() -> new EntityNotFoundException("해당 세션을 찾을 수 없습니다."));
        checkEducationExist(request.sessionId());

        Education education = Education.builder()
                .sessionId(findSession.getId())
                .subject(request.subject())
                .educationNumber(request.educationNum())
                .generationId(findSession.getGeneration().getId())
                .build();

        Education saveEducation = educationRepository.save(education);
        return CreateEducationResponse.from(saveEducation);
    }

    private void checkEducationExist(Long sessionId) {
        if (educationRepository.existsBySessionId(sessionId)) {
            throw new AppException(ErrorCode.EDUCATION_DUPLICATED);
        }
    }

    public FindEducationStatusResponse findEducationStatus(Long educationId) {
        return FindEducationStatusResponse.from(findEducation(educationId));
    }

    private Education findEducation(Long educationId) {
        return educationRepository.findById(educationId)
                .orElseThrow(() -> new EntityNotFoundException("해당 교육을 찾을 수 없습니다."));
    }

    @Transactional
    public void updateSubjectAndNumber(UpdateEducationRequest request) {
        validateNotEmpty(request.newSubject());
        Education education = educationRepository.findById(request.educationId())
                .orElseThrow(() -> new EntityNotFoundException("해당 교육을 찾을 수 없습니다."));
        education.updateSubject(request.newSubject());
        education.updateNumber(request.newNumber());
        educationRepository.save(education);
    }

    private void validateNotEmpty(String newSubject) {
        Optional.ofNullable(newSubject)
                .filter(subject -> !subject.trim().isEmpty())
                .orElseThrow(() -> new AppException(ErrorCode.SUBJECT_INVALID));
    }

    public List<AllEducationResponse> findEducationListByGeneration(Long generationId) {
        return findAllEducationByGenerationId(generationId).stream()
                .map(AllEducationResponse::from)
                .toList();
    }

    public EducationIdOfQuizResponse findEducationIdOfQuizId(Long quizId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new EntityNotFoundException("해당 문제를 찾을 수 없습니다."));
        return EducationIdOfQuizResponse.from(quiz);
    }

    public List<Education> findAllEducationByGenerationId(Long generationId) {
        return educationRepository.findAllByGenerationId(generationId);
    }
}

