package com.logtest.feignlogger.testData;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
        @RequestBody RequestDto request,
        @RequestHeader(value = "Authorization", required = false) String authHeader
    ) {
        ResponseDto response = testFeignClient.postExternal(request, authHeader);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/test-get")
    public ResponseEntity<ResponseDto> testGet(
        @RequestParam(name = "accountNumber") String accountNumber,
        @RequestParam(name = "status") String status,
        @RequestHeader(value = "Authorization", required = false) String authHeader
    ) {
        log.info("Getting accounts. AccountNumber: {}, Status: {}", accountNumber, status);

        ResponseDto response = testFeignClient.getExternal(accountNumber, status, authHeader);
        return ResponseEntity.ok(response);
    }

}
