package cotato.csquiz.service;

import cotato.csquiz.domain.entity.Education;
import cotato.csquiz.domain.entity.KingMember;
import cotato.csquiz.domain.entity.Member;
import cotato.csquiz.domain.entity.Quiz;
import cotato.csquiz.domain.entity.Scorer;
import cotato.csquiz.domain.entity.Winner;
import cotato.csquiz.repository.KingMemberRepository;
import cotato.csquiz.repository.MemberRepository;
import cotato.csquiz.repository.QuizRepository;
import cotato.csquiz.repository.ScorerRepository;
import cotato.csquiz.repository.WinnerRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class KingMemberService {

    private final MemberService memberService;
    private final MemberRepository memberRepository;
    private final QuizRepository quizRepository;
    private final KingMemberRepository kingMemberRepository;
    private final ScorerRepository scorerRepository;
    private final WinnerRepository winnerRepository;

    public List<KingMember> calculateKingMember(Education education) {
        List<Member> members = findKingMembersFromEducation(education);

        return members.stream()
                .map(member -> KingMember.of(member, education))
                .toList();
    }

    private List<Member> findKingMembersFromEducation(Education education) {
        List<Long> quizIds = quizRepository.findAllByEducationId(education.getId()).stream()
                .map(Quiz::getId)
                .toList();
        List<Scorer> scorers = scorerRepository.findAllByQuizIdsInQuery(quizIds);

        return findKingMembers(scorers);
    }

    private List<Member> findKingMembers(List<Scorer> scorers) {
        Map<Member, Long> countByMember = scorers.stream()
                .collect(Collectors.groupingBy(scorer -> memberService.findById(scorer.getMemberId()),
                        Collectors.counting()));
        Optional<Long> maxCount = countByMember.values().stream().max(Long::compareTo);
        return countByMember.entrySet().stream()
                .filter(entry -> entry.getValue().equals(maxCount.orElse(null)))
                .map(Entry::getKey)
                .toList();
    }

    @Transactional
    public void saveKingMembers(List<KingMember> kingMembers) {
        kingMemberRepository.saveAll(kingMembers);
    }

    @Transactional
    public void saveWinnerIfKingMemberIsOne(Education education) {
        List<KingMember> kingMembers = kingMemberRepository.findAllByEducation(education);
        if (kingMembers.size() == 1) {
            Member findMember = memberRepository.findById(kingMembers.get(0).getMemberId())
                    .orElseThrow(() -> new EntityNotFoundException("해당 멤버를 찾을 수 없습니다."));
            saveWinner(findMember.getId(), education);
        }
    }

    @Transactional
    public void saveWinnerIfNoWinnerExist(Quiz quiz) {
        Education education = quiz.getEducation();
        if (isWinnerExist(education)) {
            Scorer findScorer = scorerRepository.findByQuizId(quiz.getId())
                    .orElseThrow(() -> new EntityNotFoundException("해당 퀴즈엔 득점자가 존재하지 않습니다."));
            saveWinner(findScorer.getMemberId(), education);
        }
    }

    @Transactional
    public void saveWinner(final Long memberId, final Education education) {
        Winner winner = Winner.of(memberId, education);
        winnerRepository.save(winner);
    }

    public boolean isWinnerExist(Education education) {
        return winnerRepository.findByEducation(education).isPresent();
    }
}
