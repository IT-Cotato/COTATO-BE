package org.cotato.csquiz.domain.education.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.cotato.csquiz.api.quiz.dto.ChoiceResponse;
import org.cotato.csquiz.domain.education.repository.ChoiceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChoiceService {

    private final ChoiceRepository choiceRepository;

    @Transactional(readOnly = true)
    public List<ChoiceResponse> findAllChoices(Long multipleQuizId) {
        return choiceRepository.findAllByMultipleQuizId(multipleQuizId).stream()
                .map(ChoiceResponse::forEducation)
                .toList();
    }
}
