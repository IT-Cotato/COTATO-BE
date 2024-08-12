package org.cotato.csquiz.domain.education.service;

import jakarta.persistence.EntityNotFoundException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cotato.csquiz.api.mypage.dto.HallOfFameInfo;
import org.cotato.csquiz.api.mypage.dto.HallOfFameResponse;
import org.cotato.csquiz.api.mypage.dto.MyHallOfFameInfo;
import org.cotato.csquiz.domain.education.entity.Education;
import org.cotato.csquiz.domain.education.entity.Quiz;
import org.cotato.csquiz.domain.education.entity.Record;
import org.cotato.csquiz.domain.education.entity.Scorer;
import org.cotato.csquiz.domain.education.repository.EducationRepository;
import org.cotato.csquiz.domain.education.repository.QuizRepository;
import org.cotato.csquiz.domain.education.repository.RecordRepository;
import org.cotato.csquiz.domain.education.repository.ScorerRepository;
import org.cotato.csquiz.domain.generation.entity.Generation;
import org.cotato.csquiz.domain.auth.entity.Member;
import org.cotato.csquiz.domain.auth.service.MemberService;
import org.cotato.csquiz.domain.generation.repository.GenerationRepository;
import org.cotato.csquiz.domain.auth.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class MyPageService {

    private static final int SHOW_PEOPLE_COUNT = 5;
    private final MemberService memberService;
    private final GenerationRepository generationRepository;
    private final QuizRepository quizRepository;
    private final MemberRepository memberRepository;
    private final EducationRepository educationRepository;
    private final RecordRepository recordRepository;
    private final ScorerRepository scorerRepository;

    public HallOfFameResponse findHallOfFame(Long generationId, Long memberId) {
        Generation findGeneration = generationRepository.findById(generationId)
                .orElseThrow(() -> new EntityNotFoundException("해당 기수를 찾을 수 없습니다."));

        log.info("============{}기에 존재하는 모든 득점자 조회================", findGeneration.getNumber());
        List<HallOfFameInfo> scorerHallOfFame = createScorerHallOfFameByGeneration(findGeneration);
        log.info("============{}기에 존재하는 모든 정답자 조회================", findGeneration.getNumber());
        List<HallOfFameInfo> answerHallOfFame = createRecordsHallOfFameInfoByGeneration(findGeneration);

        Member member = findMemberById(memberId);
        MyHallOfFameInfo myHallOfFameInfo = createMyHallOfFameInfoByGeneration(member, findGeneration);

        return HallOfFameResponse.of(scorerHallOfFame, answerHallOfFame, myHallOfFameInfo);
    }

    private List<HallOfFameInfo> createScorerHallOfFameByGeneration(Generation generation) {
        List<Scorer> scorers = findAllScorersByGeneration(generation);

        Map<Member, Long> memberScoreMap = scorers.stream()
                .collect(Collectors.groupingBy(scorer -> memberService.findById(scorer.getMemberId()),
                        Collectors.counting()));
        List<Map.Entry<Member, Long>> topMemberEntry = sortTopMemberEntry(memberScoreMap);

        return topMemberEntry.stream()
                .map(entry -> HallOfFameInfo.of(entry.getKey(), entry.getValue()))
                .toList();
    }

    private List<HallOfFameInfo> createRecordsHallOfFameInfoByGeneration(Generation generation) {
        List<Record> records = findAllCorrectRecordByGeneration(generation);

        Map<Member, Long> countByMember = records.stream()
                .collect(Collectors.groupingBy(record -> memberService.findById(record.getMemberId()),
                        Collectors.counting()));
        List<Entry<Member, Long>> sorted5MemberEntry = sortTopMemberEntry(countByMember);
        return sorted5MemberEntry.stream()
                .map(entry -> HallOfFameInfo.of(entry.getKey(), entry.getValue()))
                .toList();
    }

    private MyHallOfFameInfo createMyHallOfFameInfoByGeneration(Member member, Generation generation) {
        long scorerCount = countMyScorerByGeneration(member, generation);
        long answerCount = countMyAnswerByGeneration(member, generation);

        return MyHallOfFameInfo.of(member, scorerCount, answerCount);
    }

    private long countMyScorerByGeneration(Member member, Generation generation) {
        List<Scorer> myScorers = findAllScorersByGeneration(generation);
        return myScorers.stream()
                .filter(scorer -> scorer.getMemberId().equals(member.getId()))
                .count();
    }

    private long countMyAnswerByGeneration(Member member, Generation generation) {
        List<Record> myRecords = findAllCorrectRecordByGeneration(generation);

        return myRecords.stream()
                .filter(record -> record.getMemberId().equals(member.getId()))
                .count();
    }

    private List<Scorer> findAllScorersByGeneration(final Generation generation) {
        List<Long> educationIds = educationRepository.findAllByGenerationId(generation.getId()).stream()
                .map(Education::getId)
                .toList();
        List<Long> quizIds = quizRepository.findAllByEducationIdsInQuery(educationIds).stream()
                .map(Quiz::getId)
                .toList();
        return scorerRepository.findAllByQuizIdsInQuery(quizIds);
    }

    private List<Record> findAllCorrectRecordByGeneration(final Generation generation) {
        List<Long> educationIds = educationRepository.findAllByGenerationId(generation.getId()).stream()
                .map(Education::getId)
                .toList();
        List<Long> quizIds = quizRepository.findAllByEducationIdsInQuery(educationIds).stream()
                .map(Quiz::getId)
                .toList();

        return recordRepository.findAllByQuizIdsInQuery(quizIds).stream()
                .filter(Record::getIsCorrect)
                .toList();
    }

    private List<Entry<Member, Long>> sortTopMemberEntry(Map<Member, Long> countByMember) {
        return countByMember.entrySet().stream()
                .sorted(Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(SHOW_PEOPLE_COUNT)
                .toList();
    }

    private Member findMemberById(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("해당 id를 가진 멤버를 찾을 수 없습니다."));
    }
}
