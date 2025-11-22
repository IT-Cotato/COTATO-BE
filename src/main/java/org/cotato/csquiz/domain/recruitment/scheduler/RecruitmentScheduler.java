package org.cotato.csquiz.domain.recruitment.scheduler;

import jakarta.annotation.PostConstruct;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.concurrent.ScheduledFuture;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.cotato.csquiz.common.util.TimeUtil;
import org.cotato.csquiz.domain.recruitment.entity.RecruitmentInformation;
import org.cotato.csquiz.domain.recruitment.service.component.RecruitmentInformationReader;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecruitmentScheduler {

	private final TaskScheduler taskScheduler;
	private final RecruitmentInformationReader recruitmentInformationReader;

	private ScheduledFuture<?> closeTask;

	@PostConstruct
	public void restoreScheduler() {
		RecruitmentInformation info = recruitmentInformationReader.findRecruitmentInformation();
		if (info.isOpened()) {
			registerCloseRecruitmentScheduler(info.getEndDate());
		}
	}

	public void registerCloseRecruitmentScheduler(LocalDate endDate) {
		LocalDateTime scheduleTime = LocalDateTime.of(endDate.plusDays(1), LocalTime.MIDNIGHT);

		log.info("register closeTask schedule scheduleTime: <{}>", scheduleTime);
		closeTask = taskScheduler.schedule(
			() -> {
				RecruitmentInformation info = recruitmentInformationReader.findRecruitmentInformation();
				if (info.isOpened()) {
					info.changeOpened(false);
				}
			},
			TimeUtil.getSeoulZoneTime(scheduleTime).toInstant()
		);
	}

	public void cancelCloseRecruitmentScheduler() {
		if (closeTask != null && !closeTask.isDone()) {
			log.info("cancel existing closeTask schedule");
			closeTask.cancel(false);
		}
	}
}
