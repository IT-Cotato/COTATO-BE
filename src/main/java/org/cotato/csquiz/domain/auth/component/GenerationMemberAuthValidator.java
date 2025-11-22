package org.cotato.csquiz.domain.auth.component;

import org.cotato.csquiz.common.error.ErrorCode;
import org.cotato.csquiz.common.error.exception.AppException;
import org.cotato.csquiz.domain.auth.entity.Member;
import org.cotato.csquiz.domain.generation.entity.Generation;
import org.cotato.csquiz.domain.generation.repository.GenerationMemberRepository;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class GenerationMemberAuthValidator {

	private final GenerationMemberRepository generationMemberRepository;

	public void checkGenerationPermission(final Member member, final Generation generation) {
		if (member.isDevTeam()) {
			return;
		}

		checkIsGenerationMember(member, generation);
	}

	private void checkIsGenerationMember(Member member, Generation generation) {
		if (!generationMemberRepository.existsByGenerationAndMember(generation, member)) {
			throw new AppException(ErrorCode.CANNOT_ACCESS_OTHER_GENERATION);
		}
	}
}
