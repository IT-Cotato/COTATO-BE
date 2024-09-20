package org.cotato.csquiz.api.project.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.cotato.csquiz.api.project.dto.CreateProjectImageRequest;
import org.cotato.csquiz.api.project.dto.CreateProjectRequest;
import org.cotato.csquiz.api.project.dto.CreateProjectResponse;
import org.cotato.csquiz.api.project.dto.ProjectDetailResponse;
import org.cotato.csquiz.api.project.dto.ProjectSummaryResponse;
import org.cotato.csquiz.common.error.exception.ImageException;
import org.cotato.csquiz.domain.generation.service.ProjectImageService;
import org.cotato.csquiz.domain.generation.service.ProjectService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "프로젝트 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/v2/api/projects")
public class ProjectController {

    private final ProjectService projectService;
    private final ProjectImageService projectImageService;

    @Operation(summary = "특정 프로젝트 상세 정보 조회 API")
    @GetMapping("/{projectId}")
    public ResponseEntity<ProjectDetailResponse> getProjectDetail(@PathVariable("projectId") Long projectId) {
        return ResponseEntity.ok().body(projectService.getProjectDetail(projectId));
    }

    @Operation(summary = "프로젝트 목록 조회 API")
    @GetMapping
    public ResponseEntity<List<ProjectSummaryResponse>> getAllProjectSummaries() {
        return ResponseEntity.ok(projectService.getAllProjectSummaries());
    }

    @Operation(summary = "프로젝트 등록 API")
    @PostMapping
    public ResponseEntity<CreateProjectResponse> createProject(@RequestBody @Valid CreateProjectRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(projectService.createProject(request));
    }

    @Operation(summary = "프로젝트 사진 등록 API")
    @PostMapping(value = "/images", consumes = "multipart/form-data")
    public ResponseEntity<Void> createProjectImage(@ModelAttribute CreateProjectImageRequest request)
            throws ImageException {
        projectImageService.createProjectImage(request.projectId(), request.logoImage(), request.thumbNailImage(), request.detailImages());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
