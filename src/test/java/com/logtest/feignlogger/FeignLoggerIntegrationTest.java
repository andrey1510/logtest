package com.logtest.feignlogger;

import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 8889)
@ActiveProfiles("test")
@ExtendWith(OutputCaptureExtension.class)
class FeignLoggerIntegrationTest {

    @LocalServerPort
    private int serverPort;

    private RestTemplate restTemplate;
    private String baseUrl;

    private static final String RESPONSE_BODY = """
        {
            "textField": "response data"
        }
        """;

    private static final String REQUEST_BODY = """
        {
            "textField": "request data"
        }
        """;

    @BeforeEach
    void setUp() {
        restTemplate = new RestTemplate();
        baseUrl = "http://localhost:" + serverPort;
    }

    @Test
    @SneakyThrows
    void testPost_maskAccountNumber(CapturedOutput output) {

        String externalPostEndpoint = "/external-post-endpoint";
        String accountNumber = "1234567890";
        String accountNumberMasked = "**********";
        String status = "active";
        String launchUrl = String.format("%s/launch-feign/test-post?accountNumber=%s&status=%s", baseUrl, accountNumber, status);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> requestEntity = new HttpEntity<>(REQUEST_BODY, headers);

        stubFor(post(urlPathEqualTo(externalPostEndpoint))
            .withQueryParam("accountNumber", equalTo(accountNumber))
            .withQueryParam("status", equalTo(status))
            .withHeader("Content-Type", equalTo("application/json"))
            .withRequestBody(equalToJson(REQUEST_BODY))
            .willReturn(aResponse()
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .withBody(RESPONSE_BODY)));

        ResponseEntity<String> response = restTemplate.exchange(
            launchUrl,
            HttpMethod.POST,
            requestEntity,
            String.class
        );

        verify(postRequestedFor(urlPathEqualTo(externalPostEndpoint))
            .withQueryParam("accountNumber", equalTo(accountNumber))
            .withQueryParam("status", equalTo(status)));

        assertEquals(HttpStatus.OK, response.getStatusCode());

        String logs = output.getAll();
        assertTrue(logs.contains(String.format("accountNumber=%s", accountNumberMasked)));
        assertFalse(logs.contains(String.format("accountNumber=%s", accountNumber)));
        assertTrue(logs.contains(String.format("status=%s", status)));
    }

    @Test
    @SneakyThrows
    void testPost_noBodyInLogsWhileHeadersLevel(CapturedOutput output) {

        String externalPostEndpoint = "/external-post-endpoint";
        String accountNumber = "1234567890";
        String status = "active";
        String launchUrl = String.format("%s/launch-feign/test-post?accountNumber=%s&status=%s", baseUrl, accountNumber, status);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> requestEntity = new HttpEntity<>(REQUEST_BODY, headers);

        stubFor(post(urlPathEqualTo(externalPostEndpoint))
            .withQueryParam("accountNumber", equalTo(accountNumber))
            .withQueryParam("status", equalTo(status))
            .withHeader("Content-Type", equalTo("application/json"))
            .withRequestBody(equalToJson(REQUEST_BODY))
            .willReturn(aResponse()
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .withBody(RESPONSE_BODY)));

        ResponseEntity<String> response = restTemplate.exchange(
            launchUrl,
            HttpMethod.POST,
            requestEntity,
            String.class
        );

        verify(postRequestedFor(urlPathEqualTo(externalPostEndpoint))
            .withQueryParam("accountNumber", equalTo(accountNumber))
            .withQueryParam("status", equalTo(status)));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("response data"));

        String logs = output.getAll();
        assertFalse(logs.contains("""
            {"textField":"request data"}
            """));
        assertFalse(logs.contains("""
            "textField": "response data"
        """));
    }

    @Test
    @SneakyThrows
    void testPost_noAuthorizationAndJwtInLogs(CapturedOutput output) {

        String externalPostEndpoint = "/external-post-endpoint";
        String accountNumber = "1234567890";
        String status = "active";
        String launchUrl = String.format("%s/launch-feign/test-post?accountNumber=%s&status=%s", baseUrl, accountNumber, status);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer test-token-123");
        headers.set("jwt", "test jwt");
        HttpEntity<String> requestEntity = new HttpEntity<>(REQUEST_BODY, headers);

        stubFor(post(urlPathEqualTo(externalPostEndpoint))
            .withQueryParam("accountNumber", equalTo(accountNumber))
            .withQueryParam("status", equalTo(status))
            .withHeader("Content-Type", equalTo("application/json"))
            .withHeader("Authorization", equalTo("Bearer test-token-123"))
            .withHeader("jwt", equalTo("test jwt"))
            .withRequestBody(equalToJson(REQUEST_BODY))
            .willReturn(aResponse()
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .withHeader("Authorization", "Bearer test-token-123")
                .withHeader("jwt", "test jwt")
                .withBody(RESPONSE_BODY)));

        ResponseEntity<String> response = restTemplate.exchange(
            launchUrl,
            HttpMethod.POST,
            requestEntity,
            String.class
        );

        System.out.println(response.getHeaders().toString());

        verify(postRequestedFor(urlPathEqualTo(externalPostEndpoint))
            .withQueryParam("accountNumber", equalTo(accountNumber))
            .withQueryParam("status", equalTo(status))
            .withHeader("Authorization", equalTo("Bearer test-token-123"))
            .withHeader("jwt", equalTo("test jwt")));

        assertEquals(HttpStatus.OK, response.getStatusCode());

        String logs = output.getAll();
        assertFalse(logs.contains("Authorization"));
        assertFalse(logs.contains("jwt"));

    }

}