package org.cotato.csquiz.domain.education.service;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cotato.csquiz.api.education.dto.WinnerInfoResponse;
import org.cotato.csquiz.api.quiz.dto.KingMemberInfo;
import org.cotato.csquiz.common.error.ErrorCode;
import org.cotato.csquiz.common.error.exception.AppException;
import org.cotato.csquiz.domain.education.entity.Education;
import org.cotato.csquiz.domain.education.entity.KingMember;
import org.cotato.csquiz.domain.education.entity.Quiz;
import org.cotato.csquiz.domain.education.entity.Scorer;
import org.cotato.csquiz.domain.education.entity.Winner;
import org.cotato.csquiz.domain.education.repository.EducationRepository;
import org.cotato.csquiz.domain.education.repository.QuizRepository;
import org.cotato.csquiz.domain.education.repository.ScorerRepository;
import org.cotato.csquiz.domain.auth.entity.Member;
import org.cotato.csquiz.domain.education.repository.KingMemberRepository;
import org.cotato.csquiz.domain.auth.repository.MemberRepository;
import org.cotato.csquiz.domain.education.repository.WinnerRepository;
import org.cotato.csquiz.domain.auth.service.MemberService;
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
    private final EducationRepository educationRepository;

    @Transactional
    public void saveKingMember(Long educationId) {
        Education education = findEducationById(educationId);

        checkKingMemberExist(education);

        List<KingMember> kingMembers = findKingMembersFromEducation(education).stream()
                .map(member -> KingMember.of(member, education))
                .toList();

        kingMemberRepository.saveAll(kingMembers);
        saveWinnerIfKingMemberIsOne(education);
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

    private void saveWinnerIfKingMemberIsOne(Education education) {
        List<KingMember> kingMembers = kingMemberRepository.findAllByEducation(education);
        if (kingMembers.size() == 1) {
            Member findMember = memberRepository.findById(kingMembers.get(0).getMemberId())
                    .orElseThrow(() -> new EntityNotFoundException("해당 멤버를 찾을 수 없습니다."));
            saveWinner(findMember.getId(), education);
        }
    }

    @Transactional
    public void calculateWinner(Long educationId) {
        Education education = findEducationById(educationId);
        if (winnerRepository.existsByEducation(education)) {
            throw new AppException(ErrorCode.WINNER_EXIST);
        }

        Quiz quiz = quizRepository.findFirstByEducationOrderByNumberDesc(education)
                .orElseThrow(() -> new EntityNotFoundException("마지막 문제가 없습니다"));
        Scorer lastQuizScorer = scorerRepository.findByQuizId(quiz.getId())
                .orElseThrow(() -> new EntityNotFoundException("마지막 퀴즈 득점자가 존재하지 않습니다."));

        saveWinner(lastQuizScorer.getMemberId(), education);
    }

    @Transactional
    public void saveWinner(final Long memberId, final Education education) {
        Winner winner = Winner.of(memberId, education);
        winnerRepository.save(winner);
    }

    public List<KingMemberInfo> findKingMemberInfo(Long educationId) {
        Education findEducation = educationRepository.findById(educationId)
                .orElseThrow(() -> new EntityNotFoundException("해당 교육을 찾을 수 없습니다."));
        List<KingMember> kingMembers = kingMemberRepository.findAllByEducation(findEducation);
        validateIsEmpty(kingMembers);
        return kingMembers.stream()
                .map(kingMember -> memberService.findById(kingMember.getMemberId()))
                .map(member -> KingMemberInfo.from(member, memberService.findBackFourNumber(member)))
                .toList();
    }

    private void validateIsEmpty(List<KingMember> kingMembers) {
        if (kingMembers.isEmpty()) {
            throw new EntityNotFoundException("아직 결승 진출자가 결정되지 않았습니다.");
        }
    }

    public WinnerInfoResponse findWinner(Long educationId) {
        Education findEducation = educationRepository.findById(educationId)
                .orElseThrow(() -> new EntityNotFoundException("해당 교육을 찾을 수 없습니다."));
        Winner findWinner = winnerRepository.findByEducation(findEducation)
                .orElseThrow(() -> new EntityNotFoundException("해당 교육의 우승자를 찾을 수 없습니다."));
        Member findMember = memberService.findById(findWinner.getMemberId());
        return WinnerInfoResponse.of(findWinner, findMember, memberService.findBackFourNumber(findMember));
    }

    private Education findEducationById(Long educationId) {
        return educationRepository.findById(educationId)
                .orElseThrow(() -> new EntityNotFoundException("해당 교육 id:" + educationId + " 찾다가 에러 발생했습니다."));
    }

    private void checkKingMemberExist(Education education) {
        if (kingMemberRepository.existsByEducation(education)) {
            throw new AppException(ErrorCode.KING_MEMBER_EXIST);
        }
    }
}
