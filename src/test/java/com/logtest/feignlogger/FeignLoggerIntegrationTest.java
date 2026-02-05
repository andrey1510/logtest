package com.logtest.feignlogger;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ExtendWith(OutputCaptureExtension.class)
class FeignLoggerIntegrationTest {

    private static MockWebServer mockWebServer;

    @Autowired
    private MockMvc mockMvc;

    @BeforeAll
    static void beforeAll() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start(8889);
    }

    @AfterAll
    static void afterAll() throws IOException {
        mockWebServer.shutdown();
    }

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("external-mock-service.url",
            () -> String.format("http://localhost:%d", mockWebServer.getPort()));
    }

    @Test
    void testGetAccounts_MasksAccountNumberInLogs(CapturedOutput output) throws Exception {

        String responseBody = """
            {
                "textField": "response data"
            }
            """;

        MockResponse mockResponse = new MockResponse()
            .setBody(responseBody)
            .addHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .addHeader("jwt", "secret-jwt-token");

        mockWebServer.enqueue(mockResponse);

        String authHeader = "Bearer real-token-123";
        String accountNumber = "1234567890";
        String status = "active";

        MvcResult result = mockMvc.perform(get("/test-api/test-get")
                .param("accountNumber", accountNumber)
                .param("status", status)
                .header("Authorization", authHeader))
            .andExpect(status().isOk())
            .andReturn();

        RecordedRequest recordedRequest = mockWebServer.takeRequest();

        assertNotNull(recordedRequest.getPath());
        assertTrue(recordedRequest.getPath().startsWith("/external-get"));
        assertTrue(recordedRequest.getPath().contains("accountNumber=1234567890"));
        assertTrue(recordedRequest.getPath().contains("status=active"));
        assertEquals("Bearer real-token-123", recordedRequest.getHeader("Authorization"));
        assertEquals(200, result.getResponse().getStatus());

        String logs = output.getAll();

        assertTrue(logs.contains("accountNumber=**********"),
            "Логи должны содержать замаскированный accountNumber (**********), но содержат: " + logs);

        assertFalse(logs.contains("accountNumber=1234567890"),
            "Логи не должны содержать незамаскированный accountNumber");

        assertFalse(logs.contains("Authorization: Bearer real-token-123"),
            "Логи не должны содержать заголовок Authorization");

        assertFalse(logs.contains("jwt: secret-jwt-token"),
            "Логи не должны содержать jwt заголовок из ответа");

        assertTrue(logs.contains("---> GET"),
            "Логи должны содержать информацию о запросе (---> GET)");
    }

    @Test
    void testGetAccounts_WithoutAuthHeader(CapturedOutput output) throws Exception {
        // Arrange
        String responseBody = """
            {
                "textField": "test data"
            }
            """;

        MockResponse mockResponse = new MockResponse()
            .setBody(responseBody)
            .addHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE);

        mockWebServer.enqueue(mockResponse);

        String accountNumber = "987654321"; // 9 символов
        String status = "inactive";

        // Act
        MvcResult result = mockMvc.perform(get("/test-api/test-get")
                .param("accountNumber", accountNumber)
                .param("status", status))
            .andExpect(status().isOk())
            .andReturn();

        // Assert
        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertNotNull(recordedRequest.getPath());
        assertTrue(recordedRequest.getPath().contains("accountNumber=987654321"));
        assertTrue(recordedRequest.getPath().contains("status=inactive"));
        assertNull(recordedRequest.getHeader("Authorization"));

        String logs = output.getAll();

        assertTrue(logs.contains("accountNumber=*********"),
            "Логи должны содержать замаскированный accountNumber");

        assertTrue(logs.contains("status=inactive"),
            "Логи должны содержать незамаскированный status");
    }

    @Test
    void testPostRequest_DoesNotMaskBody(CapturedOutput output) throws Exception {

        String responseBody = """
            {
                "textField": "post response"
            }
            """;

        MockResponse mockResponse = new MockResponse()
            .setBody(responseBody)
            .addHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE);

        mockWebServer.enqueue(mockResponse);

        String requestBody = """
            {
                "textField": "test request data"
            }
            """;

        String authHeader = "Bearer another-token";

        MvcResult result = mockMvc.perform(post("/test-api/test-post")
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", authHeader))
            .andExpect(status().isOk())
            .andReturn();

        RecordedRequest recordedRequest = mockWebServer.takeRequest();

        assertNotNull(recordedRequest.getPath());
        assertEquals("/external-post", recordedRequest.getPath());
        assertEquals("Bearer another-token", recordedRequest.getHeader("Authorization"));

        String requestBodyContent = recordedRequest.getBody().readUtf8();
        assertTrue(requestBodyContent.contains("test request data"));
        assertEquals("POST", recordedRequest.getMethod());

        String logs = output.getAll();

        assertFalse(logs.contains("accountNumber="),
            "POST запрос не должен содержать accountNumber");

        assertFalse(logs.contains("Authorization: Bearer another-token"),
            "Логи не должны содержать заголовок Authorization");

    }

    @Test
    void testGetAccounts_ResponseJwtHeaderNotLogged(CapturedOutput output) throws Exception {

        String responseBody = """
            {
                "textField": "sensitive data"
            }
            """;

        MockResponse mockResponse = new MockResponse()
            .setBody(responseBody)
            .addHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .addHeader("jwt", "very-secret-jwt-response-token")
            .addHeader("custom-header", "custom-value");

        mockWebServer.enqueue(mockResponse);

        String accountNumber = "111222333";
        String status = "pending";

        MvcResult result = mockMvc.perform(get("/test-api/test-get")
                .param("accountNumber", accountNumber)
                .param("status", status))
            .andExpect(status().isOk())
            .andReturn();

        RecordedRequest recordedRequest = mockWebServer.takeRequest();

        assertNotNull(recordedRequest.getPath());
        assertTrue(recordedRequest.getPath().contains("accountNumber=111222333"));
        assertTrue(recordedRequest.getPath().contains("status=pending"));

        String responseContent = result.getResponse().getContentAsString();
        assertTrue(responseContent.contains("sensitive data"));

        String logs = output.getAll();

        assertTrue(logs.contains("accountNumber=*********"),
            "Логи должны содержать замаскированный accountNumber");

        assertFalse(logs.contains("jwt: very-secret-jwt-response-token"),
            "Логи не должны содержать jwt заголовок из ответа");

        assertTrue(logs.contains("custom-header: custom-value"),
            "Логи должны содержать не исключенные хедеры)");

        assertTrue(logs.contains("<--- HTTP") || logs.contains("END HTTP"),
            "Логи должны содержать информацию об ответе");
    }
}