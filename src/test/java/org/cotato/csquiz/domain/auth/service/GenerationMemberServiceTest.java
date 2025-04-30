package org.cotato.csquiz.domain.auth.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.cotato.csquiz.domain.auth.service.component.GenerationMemberReader;
import org.cotato.csquiz.domain.generation.entity.GenerationMember;
import org.cotato.csquiz.domain.generation.enums.GenerationMemberRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class GenerationMemberServiceTest {

    @InjectMocks
    private GenerationMemberService generationMemberService;

    @Mock
    private GenerationMemberReader generationMemberReader;

    @Test
    @DisplayName("기수별 활동 부원 역할 수정 테스트")
    void updateGenerationMemberRoleTest() {
        // given
        Long generationMemberId = 1L;
        GenerationMemberRole targetRole = GenerationMemberRole.LEADER_TEAM;

        GenerationMember generationMember = mock(GenerationMember.class);
        ReflectionTestUtils.setField(generationMember, "id", generationMemberId);
        when(generationMemberReader.findById(generationMemberId)).thenReturn(generationMember);
        when(generationMember.getRole()).thenReturn(GenerationMemberRole.LEADER_TEAM);

        // when
        generationMemberService.updateGenerationMemberRole(generationMemberId, targetRole);

        // then
        assertEquals(GenerationMemberRole.LEADER_TEAM, generationMemberReader.findById(generationMemberId).getRole());
        verify(generationMember).updateMemberRole(targetRole);
    }
}