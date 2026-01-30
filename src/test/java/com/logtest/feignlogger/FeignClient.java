package com.logtest.feignlogger;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@org.springframework.cloud.openfeign.FeignClient(name = "test-external-service", url = "${test-external-service.url}")
public interface FeignClient {

    String AUTHORIZATION_HEADER = "Authorization";

    @PostMapping(value = "/test-external/get", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseDto get(
        @RequestBody RequestDto request,
        @RequestHeader(name = AUTHORIZATION_HEADER, required = false) String token
    );

    @GetMapping(value = "/test-external/accounts", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseDto getAccounts(
        @RequestParam("accountNumber") String accountNumber,
        @RequestParam("status") String status,
        @RequestHeader(name = AUTHORIZATION_HEADER, required = false) String token
    );
}

