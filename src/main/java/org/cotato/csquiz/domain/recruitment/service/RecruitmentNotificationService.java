package org.cotato.csquiz.domain.recruitment.service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.cotato.csquiz.common.error.ErrorCode;
import org.cotato.csquiz.common.error.exception.AppException;
import org.cotato.csquiz.domain.auth.entity.Member;
import org.cotato.csquiz.domain.recruitment.email.EmailContent;
import org.cotato.csquiz.domain.recruitment.email.RecruitmentEmailFactory;
import org.cotato.csquiz.domain.recruitment.entity.RecruitmentNotification;
import org.cotato.csquiz.domain.recruitment.entity.RecruitmentNotificationEmailLog;
import org.cotato.csquiz.domain.recruitment.entity.RecruitmentNotificationRequester;
import org.cotato.csquiz.domain.recruitment.enums.SendStatus;
import org.cotato.csquiz.domain.recruitment.repository.RecruitmentNotificationEmailLogJdbcRepository;
import org.cotato.csquiz.domain.recruitment.repository.RecruitmentNotificationRepository;
import org.cotato.csquiz.domain.recruitment.repository.RecruitmentNotificationRequesterRepository;
import org.cotato.csquiz.domain.recruitment.service.component.RecruitmentNotificationRequesterReader;
import org.cotato.csquiz.domain.recruitment.service.component.RecruitmentNotificationSender;
import org.cotato.csquiz.domain.recruitment.service.component.dto.NotificationResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RecruitmentNotificationService {

    private final RecruitmentNotificationRequesterReader recruitmentNotificationRequesterReader;
    private final RecruitmentNotificationRequesterRepository recruitmentNotificationRequesterRepository;
    private final RecruitmentNotificationRepository recruitmentNotificationRepository;
    private final RecruitmentNotificationSender recruitmentNotificationSender;
    private final RecruitmentNotificationEmailLogJdbcRepository recruitmentNotificationEmailLogJdbcRepository;
    private final RecruitmentEmailFactory recruitmentEmailFactory;

    @Transactional
    public void requestRecruitmentNotification(String recruitEmail, boolean isPolicyChecked) {
        if (!isPolicyChecked) {
            throw new AppException(ErrorCode.SHOULD_AGREE_POLICY);
        }
        if (recruitmentNotificationRequesterReader.existsByEmailAndSendStatus(recruitEmail, SendStatus.NOT_SENT)) {
            throw new AppException(ErrorCode.ALREADY_REQUEST_NOTIFICATION);
        }

        recruitmentNotificationRequesterRepository.save(
                RecruitmentNotificationRequester.of(recruitEmail, isPolicyChecked)
        );
    }

    @Transactional
    public void sendRecruitmentNotificationMail(final int generationNumber, final Member member) {
        //보내야 하는 메일 목록 반환
        List<RecruitmentNotificationRequester> allNotSentOrFailRequester = recruitmentNotificationRequesterReader.findAllNotSentOrFailEmails();

        //notification 객체 생성
        RecruitmentNotification recruitmentNotification = recruitmentNotificationRepository.save(
                RecruitmentNotification.of(member, generationNumber));

        EmailContent emailContent = recruitmentEmailFactory.getRecruitmentEmailContent(generationNumber);

        //메일 전송 + 로그 작성 비동기 처리
        List<CompletableFuture<NotificationResult>> notificationTasks = allNotSentOrFailRequester.stream()
                .map(requester -> recruitmentNotificationSender.sendNotificationAsync(requester, emailContent))
                .toList();
        CompletableFuture.allOf(notificationTasks.toArray(new CompletableFuture[0])).join();

        List<NotificationResult> results = notificationTasks.stream()
                .map(CompletableFuture::join)
                .toList();

        updateRequesterSendResult(results);
        saveEmailLogs(allNotSentOrFailRequester, results, recruitmentNotification);
    }

    private void updateRequesterSendResult(List<NotificationResult> results) {
        List<Long> successIds = results.stream()
                .filter(NotificationResult::success)
                .map(NotificationResult::requestId)
                .toList();
        List<Long> failIds = results.stream()
                .filter(r -> !r.success())
                .map(NotificationResult::requestId)
                .toList();

        if (!successIds.isEmpty()) {
            recruitmentNotificationRequesterRepository.updateSendStatusByIds(SendStatus.SUCCESS, successIds);
        }
        if (!failIds.isEmpty()) {
            recruitmentNotificationRequesterRepository.updateSendStatusByIds(SendStatus.FAIL, failIds);
        }
    }

    private void saveEmailLogs(List<RecruitmentNotificationRequester> requesters,
                               List<NotificationResult> results,
                               RecruitmentNotification notification) {
        Map<Long, RecruitmentNotificationRequester> reqesterMap = requesters.stream()
                .collect(Collectors.toMap(RecruitmentNotificationRequester::getId, Function.identity()));

        List<RecruitmentNotificationEmailLog> logs = results.stream()
                .map(r -> RecruitmentNotificationEmailLog.of(
                        reqesterMap.get(r.requestId()),
                        notification,
                        r.success()
                ))
                .toList();
        recruitmentNotificationEmailLogJdbcRepository.saveAllWithBatch(logs);
    }
}
