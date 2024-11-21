package org.cotato.csquiz.api.mypage.controller;

import org.cotato.csquiz.api.mypage.dto.HallOfFameResponse;
import org.cotato.csquiz.common.config.jwt.JwtTokenProvider;
import org.cotato.csquiz.domain.education.service.HallOfFameService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/api/mypage")
public class MyPageController {

    private final HallOfFameService hallOfFameService;
    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping("/hall-of-fame")
    public ResponseEntity<HallOfFameResponse> findHallOfFame(@RequestParam("generationId") Long generationId,
                                                             @RequestHeader("Authorization") String authorizationHeader) {
        String accessToken = jwtTokenProvider.getBearer(authorizationHeader);

        return ResponseEntity.ok(hallOfFameService.findHallOfFame(generationId, jwtTokenProvider.getMemberId(accessToken)));
    }
}
