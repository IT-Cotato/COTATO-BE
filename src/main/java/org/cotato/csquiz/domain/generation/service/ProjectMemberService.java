package org.cotato.csquiz.domain.generation.service;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.cotato.csquiz.api.project.dto.ProjectMemberRequest;
import org.cotato.csquiz.domain.generation.entity.Project;
import org.cotato.csquiz.domain.generation.entity.ProjectMember;
import org.cotato.csquiz.domain.generation.repository.ProjectMemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProjectMemberService {

    private final ProjectMemberRepository projectMemberRepository;

    @Transactional
    public void createProjectMember(Project project, List<ProjectMemberRequest> members) {
        List<ProjectMember> newMembers = members.stream()
                .map(request -> ProjectMember.of(request.position(), request.name(), project))
                .toList();

        projectMemberRepository.saveAll(newMembers);
    }
}
