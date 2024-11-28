package org.cotato.csquiz.api.mypage.controller;

import org.cotato.csquiz.api.mypage.dto.HallOfFameResponse;
import org.cotato.csquiz.domain.education.service.MyPageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/api/mypage")
public class MyPageController {

    private final MyPageService myPageService;

    @GetMapping("/hall-of-fame")
    public ResponseEntity<HallOfFameResponse> findHallOfFame(@RequestParam("generationId") Long generationId,
                                                             @AuthenticationPrincipal Long memberId) {
        return ResponseEntity.ok(myPageService.findHallOfFame(generationId, memberId));
    }
}
