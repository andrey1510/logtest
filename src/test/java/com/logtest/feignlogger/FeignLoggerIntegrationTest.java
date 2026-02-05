package com.logtest.feignlogger;

import com.github.tomakehurst.wiremock.client.WireMock;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.containing;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import com.sun.net.httpserver.HttpServer;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@AutoConfigureWireMock(port = 8889)
@ExtendWith(OutputCaptureExtension.class)
@TestPropertySource(properties = {
    "external-mock-service.url=http://localhost:8889"
})
class FeignLoggerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @SneakyThrows
    void testGetAccounts_MasksAccountNumberInLogs(CapturedOutput output) {

        String responseBody = """
            {
                "textField": "response data"
            }
            """;

        stubFor(WireMock.get(urlPathEqualTo("/external-get"))
            .withQueryParam("accountNumber", equalTo("1234567890"))
            .withQueryParam("status", equalTo("active"))
            .willReturn(aResponse()
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .withHeader("jwt", "secret-jwt-token")
                .withBody(responseBody)));

        String authHeader = "Bearer real-token-123";
        String accountNumber = "1234567890";
        String status = "active";

        MvcResult result = mockMvc.perform(get("/test-api/test-get")
                .param("accountNumber", accountNumber)
                .param("status", status)
                .header("Authorization", authHeader))
            .andExpect(status().isOk())
            .andReturn();

        verify(getRequestedFor(urlPathEqualTo("/external-get"))
            .withQueryParam("accountNumber", equalTo("1234567890"))
            .withQueryParam("status", equalTo("active"))
            .withHeader("Authorization", equalTo("Bearer real-token-123")));

        assertEquals(200, result.getResponse().getStatus());

        String logs = output.getAll();

        assertTrue(logs.contains("accountNumber=**********"),
            "Логи должны содержать замаскированный accountNumber (**********), но содержат: " + logs);

        assertFalse(logs.contains("GET http://localhost:8889/external-get?accountNumber=1234567890"),
            "Логи не должны содержать незамаскированный accountNumber");

        assertFalse(logs.contains("Authorization: Bearer real-token-123"),
            "Логи не должны содержать заголовок Authorization");

        assertFalse(logs.contains("jwt: secret-jwt-token"),
            "Логи не должны содержать jwt заголовок из ответа");

        assertTrue(logs.contains("---> GET"),
            "Логи должны содержать информацию о запросе (---> GET)");
    }

    @Test
    @SneakyThrows
    void testGetAccounts_WithoutAuthHeader(CapturedOutput output) {
        // Arrange
        String responseBody = """
            {
                "textField": "test data"
            }
            """;

        stubFor(WireMock.get(urlPathEqualTo("/external-get"))
            .withQueryParam("accountNumber", equalTo("987654321"))
            .withQueryParam("status", equalTo("inactive"))
            .willReturn(aResponse()
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .withBody(responseBody)));

        String accountNumber = "987654321";
        String status = "inactive";

        MvcResult result = mockMvc.perform(get("/test-api/test-get")
                .param("accountNumber", accountNumber)
                .param("status", status))
            .andExpect(status().isOk())
            .andReturn();

        verify(getRequestedFor(urlPathEqualTo("/external-get"))
            .withQueryParam("accountNumber", equalTo("987654321"))
            .withQueryParam("status", equalTo("inactive")));

        String logs = output.getAll();

        assertTrue(logs.contains("accountNumber=*********"),
            "Логи должны содержать замаскированный accountNumber");

        assertTrue(logs.contains("status=inactive"),
            "Логи должны содержать незамаскированный status");
    }

    @Test
    @SneakyThrows
    void testPostRequest_DoesNotMaskBody(CapturedOutput output) {

        String responseBody = """
            {
                "textField": "post response"
            }
            """;

        String requestBody = """
            {
                "textField": "test request data"
            }
            """;

        stubFor(WireMock.post(urlPathEqualTo("/external-post"))
            .withHeader("Authorization", equalTo("Bearer another-token"))
            .withRequestBody(containing("test request data"))
            .willReturn(aResponse()
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .withBody(responseBody)));

        String authHeader = "Bearer another-token";

        MvcResult result = mockMvc.perform(post("/test-api/test-post")
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", authHeader))
            .andExpect(status().isOk())
            .andReturn();

        verify(postRequestedFor(urlPathEqualTo("/external-post"))
            .withHeader("Authorization", equalTo("Bearer another-token"))
            .withRequestBody(containing("test request data")));

        String logs = output.getAll();

        assertFalse(logs.contains("accountNumber="),
            "POST запрос не должен содержать accountNumber");

        assertFalse(logs.contains("Authorization: Bearer another-token"),
            "Логи не должны содержать заголовок Authorization");
    }

    @Test
    @SneakyThrows
    void testGetAccounts_ResponseJwtHeaderNotLogged(CapturedOutput output) {

        String responseBody = """
            {
                "textField": "sensitive data"
            }
            """;

        stubFor(WireMock.get(urlPathEqualTo("/external-get"))
            .withQueryParam("accountNumber", equalTo("111222333"))
            .withQueryParam("status", equalTo("pending"))
            .willReturn(aResponse()
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .withHeader("jwt", "very-secret-jwt-response-token")
                .withHeader("custom-header", "custom-value")
                .withBody(responseBody)));

        String accountNumber = "111222333";
        String status = "pending";

        MvcResult result = mockMvc.perform(get("/test-api/test-get")
                .param("accountNumber", accountNumber)
                .param("status", status))
            .andExpect(status().isOk())
            .andReturn();

        verify(getRequestedFor(urlPathEqualTo("/external-get"))
            .withQueryParam("accountNumber", equalTo("111222333"))
            .withQueryParam("status", equalTo("pending")));

        String responseContent = result.getResponse().getContentAsString();
        assertTrue(responseContent.contains("sensitive data"));

        String logs = output.getAll();

        assertTrue(logs.contains("accountNumber=*********"),
            "Логи должны содержать замаскированный accountNumber");

        assertFalse(logs.contains("jwt: very-secret-jwt-response-token"),
            "Логи не должны содержать jwt заголовок из ответа");

        assertTrue(logs.contains("custom-header: custom-value"),
            "Логи должны содержать не исключенные хедеры");

        assertTrue(logs.contains("<--- HTTP") || logs.contains("END HTTP"),
            "Логи должны содержать информацию об ответе");
    }
}