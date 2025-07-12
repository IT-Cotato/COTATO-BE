package org.cotato.csquiz.domain.education.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cotato.csquiz.domain.education.entity.MultipleQuiz;
import org.cotato.csquiz.domain.education.entity.Quiz;
import org.cotato.csquiz.domain.education.service.component.ChoiceReader;
import org.cotato.csquiz.domain.education.service.component.DiscordConnector;
import org.cotato.csquiz.domain.education.service.component.QuizReader;
import org.cotato.csquiz.domain.education.service.dto.RandomQuizResponse;
import org.cotato.csquiz.domain.education.util.DiscordUtil;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class QuizScheduler {

    private final DiscordConnector discordConnector;
    private final QuizReader quizReader;
    private final ChoiceReader choiceReader;

    @Transactional(readOnly = true)
    @Scheduled(cron = "0 0 14 * * ?")
    public void sendRandomQuiz() {
        Quiz quiz = quizReader.getRandomDiscordQuiz();
        RandomQuizResponse randomQuiz = RandomQuizResponse.from(quiz);

        discordConnector.sendMessageToTextChannel(DiscordUtil.getMultipleQuizEmbeds(randomQuiz),
                DiscordUtil.getButtons(choiceReader.getChoicesByMultipleQuiz((MultipleQuiz) quiz)));
        log.info("[디스코드 채널 문제 전송 완료: {}]", randomQuiz.id());
    }
}
