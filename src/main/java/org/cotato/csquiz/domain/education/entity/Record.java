package org.cotato.csquiz.domain.education.entity;

import static jakarta.persistence.FetchType.*;

import org.cotato.csquiz.common.entity.BaseTimeEntity;
import org.cotato.csquiz.domain.auth.entity.Member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Record extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "record_id")
	private Long id;

	@Column(name = "record_reply")
	private String reply;

	@Column(name = "ticket_number", nullable = false)
	private Long ticketNumber;

	@Column(name = "record_is_correct")
	private Boolean isCorrect;

	@Column(name = "member_id", nullable = false)
	private Long memberId;

	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "quiz_id")
	private Quiz quiz;

	private Record(String reply, Boolean isCorrect, Member member, Quiz quiz, Long ticketNumber) {
		this.reply = reply;
		this.isCorrect = isCorrect;
		this.memberId = member.getId();
		this.quiz = quiz;
		this.ticketNumber = ticketNumber;
	}

	public static Record of(String reply, Boolean isCorrect, Member member, Quiz quiz, Long ticketNumber) {
		return new Record(reply, isCorrect, member, quiz, ticketNumber);
	}

	public void updateCorrect(boolean isCorrect) {
		this.isCorrect = isCorrect;
	}
}
