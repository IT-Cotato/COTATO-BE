package org.cotato.csquiz.domain.education.service.component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.cotato.csquiz.api.quiz.dto.ChoiceResponse;
import org.cotato.csquiz.domain.education.entity.Choice;
import org.cotato.csquiz.domain.education.entity.MultipleQuiz;
import org.cotato.csquiz.domain.education.repository.ChoiceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChoiceReader {

    private final ChoiceRepository choiceRepository;

    @Transactional(readOnly = true)
    public List<ChoiceResponse> getChoicesByMultipleQuiz(MultipleQuiz quiz) {
        return choiceRepository.findAllByMultipleQuizId(quiz.getId()).stream()
                .map(ChoiceResponse::forEducation)
                .toList();
    }

    public Map<MultipleQuiz, List<Choice>> getChoicesByMultipleQuizzes(List<MultipleQuiz> quizzes) {
        return choiceRepository.findAllByMultipleQuizIdIn(quizzes.stream().map(MultipleQuiz::getId).toList()).stream()
                .collect(Collectors.groupingBy(Choice::getMultipleQuiz));
    }
}
