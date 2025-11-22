package org.cotato.csquiz.domain.education.service;

import java.util.Optional;

import org.cotato.csquiz.domain.education.entity.Quiz;
import org.cotato.csquiz.domain.education.entity.Record;
import org.cotato.csquiz.domain.education.entity.Scorer;
import org.cotato.csquiz.domain.education.repository.ScorerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ScorerService {

	private final ScorerRepository scorerRepository;

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void checkAndThenUpdateScorer(Record memberReply) {
		Optional<Scorer> maybeScorer = scorerRepository.findByQuizId(memberReply.getQuiz().getId());

		maybeScorer.ifPresentOrElse(
			scorer -> {
				if (scorer.getTicketNumber() > memberReply.getTicketNumber()) {
					scorer.updateScorer(memberReply.getMemberId(), memberReply.getTicketNumber());
					scorerRepository.save(scorer);
					log.info("득점자 업데이트 : 티켓번호: {}", memberReply.getTicketNumber());
				}
			},
			() -> {
				createScorer(memberReply.getMemberId(), memberReply.getQuiz(), memberReply.getTicketNumber());
				log.info("득점자 생성 : {}, 티켓번호: {}", memberReply.getMemberId(), memberReply.getTicketNumber());
			}

		);
	}

	@Transactional
	public void createScorer(final Long memberId, final Quiz quiz, final Long ticketNumber) {
		scorerRepository.save(Scorer.of(memberId, quiz, ticketNumber));
	}
}
