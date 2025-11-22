package org.cotato.csquiz.domain.auth.service.component;

import java.util.List;

import org.cotato.csquiz.common.error.ErrorCode;
import org.cotato.csquiz.common.error.exception.AppException;
import org.cotato.csquiz.domain.auth.entity.Member;
import org.cotato.csquiz.domain.auth.repository.MemberRepository;
import org.cotato.csquiz.domain.generation.entity.Generation;
import org.cotato.csquiz.domain.generation.entity.GenerationMember;
import org.cotato.csquiz.domain.generation.repository.GenerationMemberRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberReader {

	private final MemberRepository memberRepository;
	private final GenerationMemberRepository generationMemberRepository;

	public Member findById(Long id) {
		return memberRepository.findById(id)
			.orElseThrow(() -> new EntityNotFoundException("해당 부원을 찾을 수 없습니다."));
	}

	public List<Member> findAllGenerationMember(Generation generation) {
		return generationMemberRepository.findAllByGenerationWithMember(generation).stream()
			.map(GenerationMember::getMember)
			.toList();
	}

	public List<Member> findAllByIdsInWithValidation(List<Long> memberIds) {
		List<Member> members = memberRepository.findAllById(memberIds);
		if (members.size() != memberIds.size()) {
			throw new EntityNotFoundException("일부 부원이 존재하지 않습니다");
		}
		return members;
	}

	public Member getByEmail(final String email) {
		return memberRepository.findByEmail(email)
			.orElseThrow(() -> new AppException(ErrorCode.EMAIL_NOT_FOUND));
	}
}
