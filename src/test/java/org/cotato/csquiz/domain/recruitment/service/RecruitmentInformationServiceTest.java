package org.cotato.csquiz.domain.recruitment.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.List;

import org.cotato.csquiz.api.recruitment.dto.RecruitmentInfoResponse;
import org.cotato.csquiz.common.error.exception.AppException;
import org.cotato.csquiz.domain.generation.embedded.Period;
import org.cotato.csquiz.domain.recruitment.entity.RecruitmentInformation;
import org.cotato.csquiz.domain.recruitment.scheduler.RecruitmentScheduler;
import org.cotato.csquiz.domain.recruitment.service.component.RecruitmentInformationReader;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@ExtendWith(SpringExtension.class)
class RecruitmentInformationServiceTest {

	@InjectMocks
	RecruitmentInformationService recruitmentInformationService;

	@Mock
	private RecruitmentInformationReader recruitmentInformationReader;

	@Mock
	private RecruitmentScheduler recruitmentScheduler;

	@BeforeEach
	void openSync() {
		// 트랜잭션 동기화 시작
		TransactionSynchronizationManager.initSynchronization();
	}

	@AfterEach
	void clearSync() {
		// 테스트가 끝나면 반드시 해제
		TransactionSynchronizationManager.clearSynchronization();
	}

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
		triggerAfterCommit();

		// then
		verify(recruitmentScheduler).cancelCloseRecruitmentScheduler();
		assertFalse(info.isOpened(), "isOpened가 false로 변경되어야 한다");
		verify(recruitmentScheduler, never()).registerCloseRecruitmentScheduler(any(LocalDate.class));
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
		triggerAfterCommit();

		// then
		InOrder inOrder = inOrder(recruitmentScheduler);
		inOrder.verify(recruitmentScheduler).cancelCloseRecruitmentScheduler();
		inOrder.verify(recruitmentScheduler).registerCloseRecruitmentScheduler(newEnd);

		assertTrue(info.isOpened(), "isOpened가 true로 변경되어야 한다");
		assertEquals(newStart, info.getPeriod().getStartDate(), "시작일이 업데이트되어야 한다");
		assertEquals(newEnd, info.getPeriod().getEndDate(), "종료일이 업데이트되어야 한다");
		assertEquals(newUrl, info.getRecruitmentUrl(), "URL이 업데이트되어야 한다");
	}

	@Test
	void 모집정보_열기_종료일이_시작일_이전_이면_예외_및_스케줄_호출_없음() {
		// given
		RecruitmentInformation info = RecruitmentInformation.builder()
			.period(Period.of(
				LocalDate.of(2025, 1, 1),
				LocalDate.of(2025, 6, 1)
			))
			.isOpened(false)
			.recruitmentUrl("http://initial")
			.build();
		when(recruitmentInformationReader.findRecruitmentInformation())
			.thenReturn(info);

		LocalDate start = LocalDate.of(2025, 7, 2);
		LocalDate end = LocalDate.of(2025, 7, 1);
		String url = "http://example.com";

		// when & then
		assertThrows(AppException.class, () ->
			recruitmentInformationService.changeRecruitmentInfo(
				true, start, end, url
			)
		);

		// 예외 시 트랜잭션이 롤백되므로 스케줄러는 전혀 호출되지 않아야 한다
		verify(recruitmentScheduler, never()).cancelCloseRecruitmentScheduler();
		verify(recruitmentScheduler, never()).registerCloseRecruitmentScheduler(any(LocalDate.class));
	}

	private void triggerAfterCommit() {
		List<TransactionSynchronization> syncs =
			TransactionSynchronizationManager.getSynchronizations();
		for (TransactionSynchronization sync : syncs) {
			sync.afterCommit();
		}
	}
}
