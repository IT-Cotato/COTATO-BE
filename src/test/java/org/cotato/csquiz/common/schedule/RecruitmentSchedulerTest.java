package org.cotato.csquiz.common.schedule;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.concurrent.ScheduledFuture;
import org.cotato.csquiz.common.util.TimeUtil;
import org.cotato.csquiz.domain.recruitment.entity.RecruitmentInformation;
import org.cotato.csquiz.domain.recruitment.service.component.RecruitmentInformationReader;
import org.cotato.csquiz.domain.schedule.RecruitmentScheduler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class RecruitmentSchedulerTest {

    @Mock
    private TaskScheduler taskScheduler;

    @Mock
    private RecruitmentInformationReader recruitmentInformationReader;

    @Mock
    private ScheduledFuture<?> mockFuture;

    @InjectMocks
    private RecruitmentScheduler scheduler;

    private final LocalDate sampleEndDate = LocalDate.of(2025, 4, 25);

    @BeforeEach
    void setUp() {
        doReturn(mockFuture)
                .when(taskScheduler)
                .schedule(any(Runnable.class), any(Instant.class));
    }

    @Test
    void cancelCloseRecruitmentScheduler_호출시_스케줄이_취소되어야_한다() {
        // given: 스케줄이 등록된 상태
        scheduler.registerCloseRecruitmentScheduler(sampleEndDate);

        // when
        scheduler.cancelCloseRecruitmentScheduler();

        // then
        verify(mockFuture).cancel(false);
    }

    @Test
    void 스케줄_등록_성공() {
        // when
        scheduler.registerCloseRecruitmentScheduler(sampleEndDate);

        // then
        // 기대: sampleEndDate의 한국시간 자정 Instant
        LocalDateTime expectedLdt = LocalDateTime.of(sampleEndDate.plusDays(1), LocalTime.MIDNIGHT);
        Instant expectedInstant = TimeUtil.getSeoulZoneTime(expectedLdt).toInstant();

        // TaskScheduler.schedule 호출 시 Instant 캡처
        ArgumentCaptor<Instant> instantCaptor = ArgumentCaptor.forClass(Instant.class);
        verify(taskScheduler).schedule(any(Runnable.class), instantCaptor.capture());

        Instant actualInstant = instantCaptor.getValue();
        assertEquals(expectedInstant, actualInstant,
                "스케줄러에 등록된 Instant가 sampleEndDate의 한국시간 자정이어야 한다");
    }

    @Test
    void restoreScheduler_열린_모집정보가_있으면_스케줄등록() {
        // given
        RecruitmentInformation openedInfo = mock(RecruitmentInformation.class);
        when(openedInfo.isOpened()).thenReturn(true);
        when(openedInfo.getEndDate()).thenReturn(sampleEndDate);
        when(recruitmentInformationReader.findRecruitmentInformation())
                .thenReturn(openedInfo);

        // when
        scheduler.restoreScheduler();

        // then: Instant 캡처
        ArgumentCaptor<Instant> instantCaptor = ArgumentCaptor.forClass(Instant.class);
        verify(taskScheduler).schedule(any(Runnable.class), instantCaptor.capture());
        Instant scheduledInstant = instantCaptor.getValue();

        LocalDateTime expectedLdt = LocalDateTime.of(sampleEndDate.plusDays(1), LocalTime.MIDNIGHT);
        Instant expectedInstant = TimeUtil.getSeoulZoneTime(expectedLdt).toInstant();

        System.out.println(expectedInstant);

        assertEquals(expectedInstant, scheduledInstant,
                "스케줄러에 등록된 시간이 sampleEndDate 다음날 00:00:00이어야 한다");
    }

}