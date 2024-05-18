package cotato.csquiz.controller;

import cotato.csquiz.controller.dto.AllEducationResponse;
import cotato.csquiz.controller.dto.education.CreateEducationRequest;
import cotato.csquiz.controller.dto.education.CreateEducationResponse;
import cotato.csquiz.controller.dto.education.EducationIdOfQuizResponse;
import cotato.csquiz.controller.dto.education.FindEducationStatusResponse;
import cotato.csquiz.controller.dto.education.UpdateEducationRequest;
import cotato.csquiz.controller.dto.education.WinnerInfoResponse;
import cotato.csquiz.controller.dto.quiz.KingMemberInfo;
import cotato.csquiz.service.EducationService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/api/education")
@RequiredArgsConstructor
@Slf4j
public class EducationController {

    private final EducationService educationService;

    @GetMapping
    public ResponseEntity<List<AllEducationResponse>> findEducationListByGeneration(
            @RequestParam(value = "generationId") Long generationId) {
        return ResponseEntity.ok().body(educationService.findEducationListByGeneration(generationId));
    }

    @GetMapping("/status")
    public ResponseEntity<FindEducationStatusResponse> findEducationStatus(@RequestParam(value = "educationId") Long educationId) {
        return ResponseEntity.ok().body(educationService.findEducationStatus(educationId));
    }

    @PostMapping("/add")
    public ResponseEntity<CreateEducationResponse> createEducation(@RequestBody @Valid CreateEducationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(educationService.createEducation(request));
    }

    @PatchMapping("/update")
    public ResponseEntity<Void> updateEducation(@RequestBody @Valid UpdateEducationRequest request) {
        educationService.updateSubjectAndNumber(request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/from")
    public ResponseEntity<EducationIdOfQuizResponse> findEducationId(@RequestParam("quizId") Long quizId) {
        log.info("[{} quizId의 educationId 조회 컨트롤러]", quizId);
        return ResponseEntity.ok().body(educationService.findEducationIdOfQuizId(quizId));
    }

    @GetMapping("/result/kings")
    public ResponseEntity<List<KingMemberInfo>> findFinalKingMembers(@RequestParam("educationId") Long educationId) {
        log.info("[{} 교육 결승진출자 조회 컨트롤러]", educationId);
        return ResponseEntity.ok().body(educationService.findKingMemberInfo(educationId));
    }

    @GetMapping("/result/winner")
    public ResponseEntity<WinnerInfoResponse> findWinner(@RequestParam("educationId") Long educationId) {
        log.info("[{} 교육 우승자 조회 컨트롤러]", educationId);
        return ResponseEntity.ok().body(educationService.findWinner(educationId));
    }
}
