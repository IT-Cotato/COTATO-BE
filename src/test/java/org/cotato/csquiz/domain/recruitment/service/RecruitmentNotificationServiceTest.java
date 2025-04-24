package org.cotato.csquiz.domain.recruitment.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.cotato.csquiz.common.error.ErrorCode;
import org.cotato.csquiz.common.error.exception.AppException;
import org.cotato.csquiz.domain.recruitment.entity.RecruitmentNotificationRequester;
import org.cotato.csquiz.domain.recruitment.enums.SendStatus;
import org.cotato.csquiz.domain.recruitment.repository.RecruitmentNotificationRequesterRepository;
import org.cotato.csquiz.domain.recruitment.service.component.RecruitmentNotificationRequesterReader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class RecruitmentNotificationServiceTest {

    @InjectMocks
    private RecruitmentNotificationService recruitmentNotificationService;

    @Mock
    private RecruitmentNotificationRequesterReader recruitmentNotificationRequesterReader;

    @Mock
    private RecruitmentNotificationRequesterRepository recruitmentNotificationRequesterRepository;

    private final String EMAIL = "user@example.com";

    @Test
    void policyChecked가_false이면_예외발생() {
        // given
        boolean isPolicyChecked = false;

        // when & then
        AppException ex = assertThrows(AppException.class,
                () -> recruitmentNotificationService.requestRecruitmentNotification(EMAIL, isPolicyChecked));
        assertEquals(ErrorCode.SHOULD_AGREE_POLICY, ex.getErrorCode());

        verifyNoInteractions(recruitmentNotificationRequesterRepository);
    }

    @Test
    void 이미_같은_이메일이_신청되면_예외발생() {
        // given
        boolean isPolicyChecked = true;
        when(recruitmentNotificationRequesterReader
                .existsByEmailAndSendStatus(EMAIL, SendStatus.NOT_SENT))
                .thenReturn(true);

        // when & then
        AppException ex = assertThrows(AppException.class,
                () -> recruitmentNotificationService.requestRecruitmentNotification(EMAIL, isPolicyChecked));
        assertEquals(ErrorCode.ALREADY_REQUEST_NOTIFICATION, ex.getErrorCode());

        verify(recruitmentNotificationRequesterReader).existsByEmailAndSendStatus(EMAIL, SendStatus.NOT_SENT);
        verifyNoInteractions(recruitmentNotificationRequesterRepository);
    }

    @Test
    void 모집_알림_신청시_저장() {
        // given
        boolean isPolicyChecked = true;
        when(recruitmentNotificationRequesterReader
                .existsByEmailAndSendStatus(EMAIL, SendStatus.NOT_SENT))
                .thenReturn(false);

        // when
        recruitmentNotificationService.requestRecruitmentNotification(EMAIL, isPolicyChecked);

        // then
        verify(recruitmentNotificationRequesterReader).existsByEmailAndSendStatus(EMAIL, SendStatus.NOT_SENT);
        verify(recruitmentNotificationRequesterRepository).save(
                argThat((RecruitmentNotificationRequester r) ->
                        EMAIL.equals(r.getEmail())
                                && r.getSendStatus() == SendStatus.NOT_SENT
                )
        );
    }
}
