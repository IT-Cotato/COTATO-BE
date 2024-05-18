package org.cotato.csquiz.api.socket.dto;

import org.cotato.csquiz.domain.education.enums.QuizStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class QuizStatusResponse {

    private QuizStatus status;
    private QuizStatus start;
    private Long quizId;
    private String command;
}
