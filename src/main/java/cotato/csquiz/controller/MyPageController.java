package cotato.csquiz.controller;

import cotato.csquiz.config.jwt.JwtTokenProvider;
import cotato.csquiz.controller.dto.mypage.HallOfFameResponse;
import cotato.csquiz.controller.dto.mypage.MyPageMemberInfoResponse;
import cotato.csquiz.service.MyPageService;
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

    private final MyPageService myPageService;
    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping("/hall-of-fame")
    public ResponseEntity<HallOfFameResponse> findHallOfFame(@RequestParam("generationId") Long generationId,
                                                               @RequestHeader("Authorization") String authorizationHeader) {
        String accessToken = jwtTokenProvider.getBearer(authorizationHeader);
        
        return ResponseEntity.ok(myPageService.findHallOfFame(generationId, jwtTokenProvider.getMemberId(accessToken)));
    }

    @GetMapping("/info")
    public ResponseEntity<MyPageMemberInfoResponse> findUserInfo(
            @RequestHeader("Authorization") String authorizationHeader) {
        String accessToken = jwtTokenProvider.getBearer(authorizationHeader);
        
        return ResponseEntity.ok(myPageService.findMemberInfo(jwtTokenProvider.getMemberId(accessToken)));
    }
}
