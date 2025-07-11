package org.cotato.csquiz.api.record.controller;

import jakarta.validation.Valid;
import javax.naming.NoPermissionException;
import lombok.RequiredArgsConstructor;
import org.cotato.csquiz.api.record.dto.RecordsAndScorerResponse;
import org.cotato.csquiz.api.record.dto.RegradeRequest;
import org.cotato.csquiz.api.record.dto.ReplyRequest;
import org.cotato.csquiz.api.record.dto.ReplyResponse;
import org.cotato.csquiz.common.role.RoleAuthority;
import org.cotato.csquiz.domain.auth.entity.Member;
import org.cotato.csquiz.domain.auth.enums.MemberRole;
import org.cotato.csquiz.domain.education.service.RecordService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/api/record")
@RequiredArgsConstructor
public class RecordController {

	private final RecordService recordService;

	@PostMapping("/reply")
	public ResponseEntity<ReplyResponse> replyToQuiz(@AuthenticationPrincipal Member member, @RequestBody @Valid ReplyRequest request) throws NoPermissionException {
		if (!member.getId().equals(request.memberId())) {
			throw new NoPermissionException("해당 문제에 답할 권한이 없습니다.");
		}
		return ResponseEntity.ok().body(recordService.replyToQuiz(request.quizId(), request.inputs(), member));
	}

	@RoleAuthority(MemberRole.MANAGER)
	@GetMapping("/all")
	public ResponseEntity<RecordsAndScorerResponse> findRecordsAndScorerByQuiz(@RequestParam("quizId") Long quizId) {
		return ResponseEntity.ok().body(recordService.findRecordsAndScorer(quizId));
	}

	@RoleAuthority(MemberRole.MANAGER)
	@PostMapping("/regrade")
	public ResponseEntity<Void> regradeQuiz(@RequestBody @Valid RegradeRequest request) {
		recordService.regradeRecords(request);
		return ResponseEntity.noContent().build();
	}
}
