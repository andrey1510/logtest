package com.logtest.feignlogger.testData;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@org.springframework.cloud.openfeign.FeignClient(name = "external-mock-service", url = "${external-mock-service.url}")
public interface TestFeignClient {

    String AUTHORIZATION_HEADER = "Authorization";
    String JWT_HEADER = "jwt";

    @PostMapping(value = "/external-post-endpoint", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseDto postExternal(
        @RequestParam("accountNumber") String accountNumber,
        @RequestParam("status") String status,
        @RequestHeader(name = AUTHORIZATION_HEADER, required = false) String authToken,
        @RequestHeader(name = JWT_HEADER, required = false) String jwtToken,
        @RequestBody RequestDto request
    );
}

