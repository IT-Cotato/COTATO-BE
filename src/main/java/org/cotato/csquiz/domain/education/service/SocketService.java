package org.cotato.csquiz.domain.education.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cotato.csquiz.api.socket.dto.SocketTokenDto;
import org.cotato.csquiz.common.config.jwt.JwtTokenProvider;
import org.cotato.csquiz.domain.auth.entity.Member;
import org.cotato.csquiz.domain.auth.service.MemberService;
import org.cotato.csquiz.common.websocket.WebSocketHandler;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SocketService {

    private final MemberService memberService;
    private final WebSocketHandler webSocketHandler;
    private final JwtTokenProvider jwtTokenProvider;

    public void accessQuiz(Long quizId) {
        webSocketHandler.accessQuiz(quizId);
    }

    public void startQuizSolve(Long quizId) {
        webSocketHandler.startQuiz(quizId);
    }

    public void stopQuizSolve(Long quizId) {
        webSocketHandler.stopQuiz(quizId);
    }

    public void stopEducation(Long educationId) {
        webSocketHandler.stopEducation(educationId);
    }


    public SocketTokenDto createSocketToken(final Long memberId) {
        Member member = memberService.findById(memberId);

        String socketToken = jwtTokenProvider.createSocketToken(member.getId(), member.getRole().getKey());
        log.info("[ 소켓 전용 토큰 발급 완료 ]");
        return SocketTokenDto.from(socketToken);
    }

    public void sendKingCommand(Long educationId) {
        webSocketHandler.sendKingMemberCommand(educationId);
    }

    public void sendWinnerCommand(Long educationId) {
        webSocketHandler.sendWinnerCommand(educationId);
    }
}
