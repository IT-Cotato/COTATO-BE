package org.cotato.csquiz.domain.recruitment.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import org.cotato.csquiz.api.recruitment.dto.RecruitmentInfoResponse;
import org.cotato.csquiz.common.schedule.RecruitmentScheduler;
import org.cotato.csquiz.domain.generation.embedded.Period;
import org.cotato.csquiz.domain.recruitment.entity.RecruitmentInformation;
import org.cotato.csquiz.domain.recruitment.service.component.RecruitmentInformationReader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class RecruitmentInformationServiceTest {

    @InjectMocks
    RecruitmentInformationService recruitmentInformationService;

    @Mock
    private RecruitmentInformationReader recruitmentInformationReader;

    @Mock
    private RecruitmentScheduler recruitmentScheduler;

    @Test
    void 모집이_열려있으면_정보를_반환한다() {
        //given
        Period period = Period.of(LocalDate.of(2025, 1, 1), LocalDate.of(2025, 6, 1));
        String url = "http://example.com";
        RecruitmentInformation info = RecruitmentInformation.builder()
                .period(period)
                .isOpened(true)
                .recruitmentUrl(url)
                .build();

        when(recruitmentInformationReader.findRecruitmentInformation()).thenReturn(info);

        //when
        RecruitmentInfoResponse response = recruitmentInformationService.findRecruitmentInfo();

        //then
        assertTrue(response.isOpened());
        assertEquals(period.getStartDate(), response.startDate());
        assertEquals(period.getEndDate(), response.endDate());
        assertEquals(url, response.recruitmentUrl());
    }

    @Test
    void 모집이_닫혀있으면_정보를_반환한다() {
        //given
        Period period = Period.of(LocalDate.of(2025, 1, 1), LocalDate.of(2025, 6, 1));
        String url = "http://example.com";
        RecruitmentInformation info = RecruitmentInformation.builder()
                .period(period)
                .isOpened(false)
                .recruitmentUrl(url)
                .build();
        when(recruitmentInformationReader.findRecruitmentInformation()).thenReturn(info);

        //when
        RecruitmentInfoResponse response = recruitmentInformationService.findRecruitmentInfo();

        //then
        assertFalse(response.isOpened());
        assertNull(response.recruitmentUrl());
        assertNull(response.startDate());
        assertNull(response.endDate());
    }

    @Test
    void 모집정보_닫기_시_스케줄_취소_및_정보_닫힘() {
        // given
        Period initial = Period.of(
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 6, 1)
        );
        RecruitmentInformation info = RecruitmentInformation.builder()
                .period(initial)
                .isOpened(true)
                .recruitmentUrl("http://initial")
                .build();
        when(recruitmentInformationReader.findRecruitmentInformation()).thenReturn(info);

        // when
        recruitmentInformationService.changeRecruitmentInfo(
                false, null, null, null
        );

        // then
        verify(recruitmentScheduler).cancelTask();
        assertFalse(info.isOpened(), "isOpened가 false로 변경되어야 한다");
        verify(recruitmentScheduler, never()).scheduleCloseTask(any(LocalDate.class));
    }

    @Test
    void 모집정보_열기_유효한_파라미터_일때_스케줄_등록_및_정보_업데이트() {
        // given
        Period initial = Period.of(
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 6, 1)
        );
        RecruitmentInformation info = RecruitmentInformation.builder()
                .period(initial)
                .isOpened(false)
                .recruitmentUrl("http://initial")
                .build();
        when(recruitmentInformationReader.findRecruitmentInformation()).thenReturn(info);

        LocalDate newStart = LocalDate.of(2025, 7, 1);
        LocalDate newEnd = LocalDate.of(2025, 12, 31);
        String newUrl = "http://updated";

        // when
        recruitmentInformationService.changeRecruitmentInfo(
                true, newStart, newEnd, newUrl
        );

        // then
        InOrder inOrder = inOrder(recruitmentScheduler);
        inOrder.verify(recruitmentScheduler).cancelTask();
        inOrder.verify(recruitmentScheduler).scheduleCloseTask(newEnd);

        assertTrue(info.isOpened(), "isOpened가 true로 변경되어야 한다");
        assertEquals(newStart, info.getPeriod().getStartDate(), "시작일이 업데이트되어야 한다");
        assertEquals(newEnd, info.getPeriod().getEndDate(), "종료일이 업데이트되어야 한다");
        assertEquals(newUrl, info.getRecruitmentUrl(), "URL이 업데이트되어야 한다");
    }
}