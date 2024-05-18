package cotato.csquiz.service;

import cotato.csquiz.domain.entity.RefusedMember;
import cotato.csquiz.domain.enums.MemberRole;
import cotato.csquiz.repository.MemberRepository;
import cotato.csquiz.repository.RefusedMemberRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final SocketService socketService;

    @Transactional
    @Scheduled(cron = "0 0 0 * * *")
    public void updateRefusedMember() {
        log.info("updateRefusedMember 시작 {}", LocalDateTime.now());
        LocalDateTime now = LocalDateTime.now();
        List<RefusedMember> deleteRefusedMembers = refusedMemberRepository.findAllByCreatedAtBefore(now.minusDays(30));

        refusedMemberRepository.deleteAll(deleteRefusedMembers);
        deleteRefusedMembers.forEach(refusedMember -> {
            if (refusedMember.getMember().getRole() == MemberRole.REFUSED) {
                memberRepository.delete(refusedMember.getMember());
            }
        });
    }

    @Transactional
    @Scheduled(cron = "0 0 2 * * SAT")
    public void closeAllCsQuiz() {
        socketService.closeAllFlags();
        log.info("[ CS 퀴즈 모두 닫기 Scheduler 완료 ]");
    }
}
