package org.cotato.csquiz.domain.recruitment.service;

import org.cotato.csquiz.api.recruitment.dto.RecruitmentInfoResponse;
import org.cotato.csquiz.domain.generation.embedded.Period;
import org.cotato.csquiz.domain.recruitment.entity.RecruitmentInformation;
import org.cotato.csquiz.domain.recruitment.service.component.RecruitmentInformationReader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class RecruitmentInformationServiceTest {

    @InjectMocks
    RecruitmentInformationService recruitmentInformationService;

    @Mock
    private RecruitmentInformationReader recruitmentInformationReader;

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
}