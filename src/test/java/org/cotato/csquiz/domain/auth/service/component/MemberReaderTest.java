package org.cotato.csquiz.domain.auth.service.component;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.cotato.csquiz.domain.auth.entity.Member;
import org.cotato.csquiz.domain.generation.entity.Generation;
import org.cotato.csquiz.domain.generation.entity.GenerationMember;
import org.cotato.csquiz.domain.generation.repository.GenerationMemberRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class MemberReaderTest {

	@InjectMocks
	private MemberReader memberReader;

	@Mock
	private GenerationMemberRepository generationMemberRepository;

	@Test
	void whenFindAllGenerationMember_thenReturnMembers() {
		//
		Generation generation = Generation.builder()
			.number(9)
			.build();

		Member member1 = Member.defaultMember("youth@email.com", "password", "신유승", null);

		Member member2 = Member.defaultMember("gikhoon@email.com", "password", "남기훈", null);

		List<GenerationMember> generationMembers = List.of(
			GenerationMember.of(generation, member1),
			GenerationMember.of(generation, member2)
		);

		// when
		when(generationMemberRepository.findAllByGenerationWithMember(generation))
			.thenReturn(generationMembers);

		// then
		List<Member> foundMembers = memberReader.findAllGenerationMember(generation);

		assertThat(foundMembers)
			.hasSize(2)
			.extracting(Member::getName)
			.containsExactlyInAnyOrder("신유승", "남기훈");
	}
}
