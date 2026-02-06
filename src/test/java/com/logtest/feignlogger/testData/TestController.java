package com.logtest.feignlogger.testData;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/launch-feign")
public class TestController {

    private final TestFeignClient testFeignClient;

    @PostMapping("/test-post")
    public ResponseEntity<ResponseDto> testPost(
        @RequestParam(name = "accountNumber") String accountNumber,
        @RequestParam(name = "status") String status,
        @RequestHeader(value = "Authorization", required = false) String authHeader,
        @RequestHeader(value = "jwt", required = false) String jwtHeader,
        @RequestBody RequestDto request
    ) {
        return ResponseEntity.ok(testFeignClient.postExternal(accountNumber, status, authHeader, jwtHeader, request));
    }

}
