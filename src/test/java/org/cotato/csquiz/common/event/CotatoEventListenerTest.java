package org.cotato.csquiz.common.event;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.cotato.csquiz.domain.auth.entity.Member;
import org.cotato.csquiz.domain.auth.service.EmailNotificationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CotatoEventListenerTest {

    @InjectMocks
    private CotatoEventListener cotatoEventListener;

    @Mock
    private EmailNotificationService emailNotificationService;

    @Test
    @DisplayName("부원 가입 거절 시 이메일 발송 테스트")
    void whenApproveMember_then_sendSignUpApprovedToEmail_호출() {
        // given
        Member member = mock(Member.class);
        EmailSendEvent event = new EmailSendEvent(EventType.APPROVE_MEMBER, member);

        // when
        cotatoEventListener.handleEmailSentEvent(event);

        // then
        verify(emailNotificationService, times(1))
                .sendSignUpApprovedToEmail(member);
        verifyNoMoreInteractions(emailNotificationService);
    }

    @Test
    void whenRejectMember_then_sendSignupRejectionToEmail_호출() {
        // given
        Member member = mock(Member.class);
        EmailSendEvent event = new EmailSendEvent(EventType.REJECT_MEMBER, member);

        // when
        cotatoEventListener.handleEmailSentEvent(event);

        // then
        verify(emailNotificationService, times(1))
                .sendSignupRejectionToEmail(member);
        verifyNoMoreInteractions(emailNotificationService);
    }

}