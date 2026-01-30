package com.logtest.feignlogger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(
    classes = TestApplication.class,
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ActiveProfiles("test")
class FeignLoggerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private FeignClient feignClient;

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("test-external-service.url",
            () -> "http://localhost:" + getCurrentPort());
    }

    private static Integer currentPort = null;

    @BeforeEach
    void setUp() {
        System.out.println("Test server running on port: " + port);
        currentPort = port;
    }

    private static Integer getCurrentPort() {
        return currentPort;
    }

    @Test
    void testPostRequest() {
        RequestDto requestDto = new RequestDto();
        requestDto.setTextField("Test request");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer test-token-123");
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<RequestDto> request = new HttpEntity<>(requestDto, headers);

        ResponseEntity<ResponseDto> response = restTemplate.postForEntity(
            "http://localhost:" + port + "/test-api/test-feign",
            request,
            ResponseDto.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getTextField().contains("Ответ от mock сервиса на запрос: Test request"));
    }

    @Test
    void testGetRequestWithAccountNumber() {
        String accountNumber = "1234567890";
        String status = "ACTIVE";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer test-token-456");

        HttpEntity<Void> request = new HttpEntity<>(headers);

        String url = String.format(
            "http://localhost:%s/test-api/accounts?accountNumber=%s&status=%s",
            port, accountNumber, status
        );

        ResponseEntity<ResponseDto> response = restTemplate.exchange(
            url,
            HttpMethod.GET,
            request,
            ResponseDto.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getTextField().contains("Accounts for number: " + accountNumber));
        assertTrue(response.getBody().getTextField().contains("status: " + status));
    }

    @Test
    void testDirectFeignClientCall() {
        RequestDto requestDto = new RequestDto();
        requestDto.setTextField("Direct Feign call");

        String authHeader = "Bearer test-token-direct";

        ResponseDto response = feignClient.get(requestDto, authHeader);

        assertNotNull(response);
        assertNotNull(response.getTextField());
        assertTrue(response.getTextField().contains("Ответ от mock сервиса на запрос: Direct Feign call"));
    }

    @Test
    void testFeignClientGetAccounts() {
        String accountNumber = "9876543210";
        String status = "INACTIVE";
        String authHeader = "Bearer test-token-get-accounts";

        ResponseDto response = feignClient.getAccounts(accountNumber, status, authHeader);

        assertNotNull(response);
        assertNotNull(response.getTextField());
        assertTrue(response.getTextField().contains("Accounts for number: " + accountNumber));
        assertTrue(response.getTextField().contains("status: " + status));
    }

    @Test
    void testMultipleRequests() {
        List<String> accountNumbers = List.of("1111111111", "2222222222", "3333333333");
        String status = "PENDING";

        for (String accountNumber : accountNumbers) {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer test-token-multiple");

            HttpEntity<Void> request = new HttpEntity<>(headers);

            String url = String.format(
                "http://localhost:%s/test-api/accounts?accountNumber=%s&status=%s",
                port, accountNumber, status
            );

            ResponseEntity<ResponseDto> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                request,
                ResponseDto.class
            );

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertTrue(response.getBody().getTextField().contains(accountNumber));
            assertTrue(response.getBody().getTextField().contains(status));
        }
    }

    @Test
    void testRequestWithoutAuthHeader() {
        RequestDto requestDto = new RequestDto();
        requestDto.setTextField("No auth header");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<RequestDto> request = new HttpEntity<>(requestDto, headers);

        ResponseEntity<ResponseDto> response = restTemplate.postForEntity(
            "http://localhost:" + port + "/test-api/test-feign",
            request,
            ResponseDto.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getTextField().contains("Ответ от mock сервиса на запрос: No auth header"));
    }
}