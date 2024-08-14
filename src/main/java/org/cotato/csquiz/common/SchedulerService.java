package org.cotato.csquiz.common;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cotato.csquiz.domain.auth.entity.RefusedMember;
import org.cotato.csquiz.domain.education.service.EducationService;
import org.cotato.csquiz.domain.education.service.QuizService;
import org.cotato.csquiz.domain.education.service.SocketService;
import org.cotato.csquiz.domain.auth.enums.MemberRole;
import org.cotato.csquiz.domain.auth.repository.MemberRepository;
import org.cotato.csquiz.domain.auth.repository.RefusedMemberRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class SchedulerService {

    private final RefusedMemberRepository refusedMemberRepository;
    private final MemberRepository memberRepository;
    private final EducationService educationService;

    @Transactional
    @Scheduled(cron = "0 0 0 * * *")
    public void updateRefusedMember() {
        log.info("updateRefusedMember 시작 {}", LocalDateTime.now());
        LocalDateTime now = LocalDateTime.now();
        List<RefusedMember> deleteRefusedMembers = refusedMemberRepository.findAllByCreatedAtBefore(now.minusDays(30));

        deleteRefusedMembers.forEach(refusedMember -> {
            if (refusedMember.getMember().getRole() == MemberRole.REFUSED) {
                memberRepository.delete(refusedMember.getMember());
            }
        });

        refusedMemberRepository.deleteAll(deleteRefusedMembers);
    }

    @Transactional
    @Scheduled(cron = "0 0 2 * * SAT")
    public void closeAllCsQuiz() {
        educationService.closeAllFlags();
        log.info("[ CS 퀴즈 모두 닫기 Scheduler 완료 ]");
    }
}
