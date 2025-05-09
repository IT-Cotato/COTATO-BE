package org.cotato.csquiz.domain.recruitment.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import org.cotato.csquiz.api.recruitment.dto.RecruitmentNotificationLogResponse;
import org.cotato.csquiz.api.recruitment.dto.RecruitmentNotificationLogsResponse;
import org.cotato.csquiz.common.error.ErrorCode;
import org.cotato.csquiz.common.error.exception.AppException;
import org.cotato.csquiz.domain.recruitment.entity.RecruitmentNotification;
import org.cotato.csquiz.domain.recruitment.entity.RecruitmentNotificationEmailLog;
import org.cotato.csquiz.domain.recruitment.entity.RecruitmentNotificationRequester;
import org.cotato.csquiz.domain.recruitment.enums.SendStatus;
import org.cotato.csquiz.domain.recruitment.repository.RecruitmentNotificationRequesterRepository;
import org.cotato.csquiz.domain.recruitment.service.component.RecruitmentNotificationEmailLogReader;
import org.cotato.csquiz.domain.recruitment.service.component.RecruitmentNotificationReader;
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

    @Mock
    private RecruitmentNotificationReader notificationReader;

    @Mock
    private RecruitmentNotificationEmailLogReader emailLogReader;

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

    @Test
    void 모집_전송_기록_반환() {
        // given
        RecruitmentNotification notification = mock(RecruitmentNotification.class);
        LocalDateTime now = LocalDateTime.of(2025, 5, 8, 12, 0);
        when(notification.getId()).thenReturn(1L);
        when(notification.getSendTime()).thenReturn(now);
        when(notification.getSenderName()).thenReturn("멤버1");

        List<RecruitmentNotification> notifications = List.of(notification);
        when(notificationReader.findTop5LatestNotifications())
                .thenReturn(notifications);

        RecruitmentNotificationEmailLog log1 = mock(RecruitmentNotificationEmailLog.class);
        RecruitmentNotificationEmailLog log2 = mock(RecruitmentNotificationEmailLog.class);
        RecruitmentNotificationEmailLog log3 = mock(RecruitmentNotificationEmailLog.class);
        when(log1.getSendSuccess()).thenReturn(true);
        when(log2.getSendSuccess()).thenReturn(true);
        when(log3.getSendSuccess()).thenReturn(false);

        List<RecruitmentNotificationEmailLog> logs = List.of(log1, log2, log3);
        Map<Long, List<RecruitmentNotificationEmailLog>> grouped = Map.of(1L, logs);
        when(emailLogReader.groupByNotificationIds(notifications))
                .thenReturn(grouped);

        // when
        RecruitmentNotificationLogsResponse response = recruitmentNotificationService.findNotificationLogs();

        // then
        assertEquals(1, response.notificationLogs().size());
        RecruitmentNotificationLogResponse dto = response.notificationLogs().get(0);

        assertEquals(now, dto.sendTime());
        assertEquals("멤버1", dto.sender());
        assertEquals(3L, dto.sendCount());
        assertEquals(2L, dto.sendSuccess());
        assertEquals(1L, dto.sendFail());
    }
}
