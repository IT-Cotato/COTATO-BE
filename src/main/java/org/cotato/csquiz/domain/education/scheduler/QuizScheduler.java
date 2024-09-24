package org.cotato.csquiz.domain.education.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cotato.csquiz.domain.education.service.ChoiceService;
import org.cotato.csquiz.domain.education.service.DiscordService;
import org.cotato.csquiz.domain.education.service.QuizService;
import org.cotato.csquiz.domain.education.service.dto.RandomQuizResponse;
import org.cotato.csquiz.domain.education.util.DiscordUtil;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class QuizScheduler {

    private final DiscordService discordService;
    private final QuizService quizService;
    private final ChoiceService choiceService;

    @Transactional(readOnly = true)
    @Scheduled(cron = "0 0 14 * * ?")
    public void sendRandomQuiz() {
        RandomQuizResponse randomQuiz = quizService.pickRandomQuiz();
        discordService.sendMessageToTextChannel(DiscordUtil.getMultipleQuizEmbeds(randomQuiz),
                DiscordUtil.getButtons(choiceService.findAllChoices(randomQuiz.id())));
        log.info("[디스코드 채널 문제 전송 완료: {}]", randomQuiz.id());
    }
}
