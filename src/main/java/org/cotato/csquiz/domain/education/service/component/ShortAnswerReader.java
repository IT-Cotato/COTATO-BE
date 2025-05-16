package org.cotato.csquiz.domain.education.service.component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.cotato.csquiz.domain.education.entity.ShortAnswer;
import org.cotato.csquiz.domain.education.entity.ShortQuiz;
import org.cotato.csquiz.domain.education.repository.ShortAnswerRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ShortAnswerReader {

    private final ShortAnswerRepository shortAnswerRepository;

    @Transactional(readOnly = true)
    public Map<ShortQuiz, List<ShortAnswer>> getAnswersByShortQuizzes(List<ShortQuiz> quizzes) {
        return shortAnswerRepository.findAllByShortQuizIdIn(quizzes.stream().map(ShortQuiz::getId).toList()).stream()
                .collect(Collectors.groupingBy(ShortAnswer::getShortQuiz));
    }
}
