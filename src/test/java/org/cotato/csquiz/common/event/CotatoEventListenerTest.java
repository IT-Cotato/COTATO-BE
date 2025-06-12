package org.cotato.csquiz.common.event;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import org.cotato.csquiz.common.error.exception.ImageException;
import org.cotato.csquiz.domain.auth.entity.Member;
import org.cotato.csquiz.domain.auth.event.EmailSendEvent;
import org.cotato.csquiz.domain.auth.event.EmailSendEventDto;
import org.cotato.csquiz.domain.auth.service.EmailNotificationService;
import org.cotato.csquiz.domain.generation.entity.Session;
import org.cotato.csquiz.domain.generation.event.SessionImageEvent;
import org.cotato.csquiz.domain.generation.event.SessionImageEventDto;
import org.cotato.csquiz.domain.generation.service.SessionImageService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class CotatoEventListenerTest {

    @InjectMocks
    private CotatoEventListener cotatoEventListener;

    @Mock
    private EmailNotificationService emailNotificationService;

    @Mock
    private SessionImageService sessionImageService;

    @Test
    @DisplayName("부원 가입 거절 시 이메일 발송 테스트")
    void whenApproveMember_then_sendSignUpApprovedToEmail_호출() {
        // given
        Member member = mock(Member.class);
        EmailSendEventDto dto = EmailSendEventDto.builder()
                .member(member)
                .build();
        EmailSendEvent event = new EmailSendEvent(EventType.APPROVE_MEMBER, dto);

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
        EmailSendEventDto dto = EmailSendEventDto.builder()
                .member(member)
                .build();
        EmailSendEvent event = new EmailSendEvent(EventType.REJECT_MEMBER, dto);

        // when
        cotatoEventListener.handleEmailSentEvent(event);

        // then
        verify(emailNotificationService, times(1))
                .sendSignupRejectionToEmail(member);
        verifyNoMoreInteractions(emailNotificationService);
    }

    @Test
    @DisplayName("세션 이미지 수정 이벤트 발행")
    void whenSessionImageUpdate_then_addSessionImages_호출() throws ImageException {
        // given
        SessionImageEventDto dto = mock(SessionImageEventDto.class);
        Session session = mock(Session.class);
        List<MultipartFile> images = List.of(mock(MultipartFile.class), mock(MultipartFile.class));
        when(dto.getSession()).thenReturn(session);
        when(dto.getImages()).thenReturn(images);

        SessionImageEvent event = new SessionImageEvent(EventType.SESSION_IMAGE_UPDATE, dto);

        // when
        cotatoEventListener.handleSessionImageUpdateEvent(event);

        // then
        verify(sessionImageService).addSessionImages(images, session);
        verify(dto, times(1)).getImages();
        verify(dto, times(1)).getSession();
    }
}