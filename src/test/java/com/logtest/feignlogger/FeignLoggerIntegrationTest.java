package com.logtest.feignlogger;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.common.Slf4jNotifier;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
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
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;


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

    @Test
    @SneakyThrows
    void testGet_masksAccountNumberInLogs(CapturedOutput output) {

        stubFor(WireMock.get(urlPathEqualTo("/external-get-endpoint"))
            .withQueryParam("accountNumber", equalTo("1234567890"))
            .withQueryParam("status", equalTo("active"))
            .willReturn(aResponse()
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .withHeader("jwt", "secret-jwt-token")
                .withBody(RESPONSE_BODY)));

        MvcResult result = mockMvc.perform(get("/launch-feign/test-get")
                .param("accountNumber", "1234567890")
                .param("status", "active")
                .header("Authorization", "Bearer real-token-123"))
            .andExpect(status().isOk())
            .andReturn();

        verify(getRequestedFor(urlPathEqualTo("/external-get-endpoint"))
            .withQueryParam("accountNumber", equalTo("1234567890"))
            .withQueryParam("status", equalTo("active"))
            .withHeader("Authorization", equalTo("Bearer real-token-123")));

        assertEquals(200, result.getResponse().getStatus());

        String logs = output.getAll();

        assertTrue(logs.contains("accountNumber=**********"));
        assertFalse(logs.contains("GET http://localhost:8889/external-get-endpoint?accountNumber=1234567890"));
        assertFalse(logs.contains("Authorization: Bearer real-token-123"));
        assertFalse(logs.contains("jwt: secret-jwt-token"));
        assertFalse(logs.contains(
                """
[TestFeignClient#getExternal] {
    "textField": "response data"
}
                """
            ));
    }








    @Test
    @SneakyThrows
    void testGet_NoAuthHeader(CapturedOutput output) {

        stubFor(WireMock.get(urlPathEqualTo("/external-get-endpoint"))
            .withQueryParam("accountNumber", equalTo("987654321"))
            .withQueryParam("status", equalTo("inactive"))
            .willReturn(aResponse()
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .withBody(RESPONSE_BODY)));

        String accountNumber = "987654321";
        String status = "inactive";

        MvcResult result = mockMvc.perform(get("/launch-feign/test-get")
                .param("accountNumber", accountNumber)
                .param("status", status))
            .andExpect(status().isOk())
            .andReturn();

        verify(getRequestedFor(urlPathEqualTo("/external-get-endpoint"))
            .withQueryParam("accountNumber", equalTo("987654321"))
            .withQueryParam("status", equalTo("inactive")));

        assertEquals(200, result.getResponse().getStatus());

        String logs = output.getAll();

        assertTrue(logs.contains("accountNumber=*********"));
        assertTrue(logs.contains("status=inactive"));
    }

    @Test
    @SneakyThrows
    void testPost_masksAccountNumberInLogs(CapturedOutput output) {

        stubFor(WireMock.post(urlPathEqualTo("/external-post-endpoint"))
            .withHeader("Authorization", equalTo("Bearer another-token"))
            .withRequestBody(containing("request data"))
            .willReturn(aResponse()
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .withBody(RESPONSE_BODY)));

        MvcResult result = mockMvc.perform(post("/launch-feign/test-post")
                .content(REQUEST_BODY)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer another-token"))
            .andExpect(status().isOk())
            .andReturn();

        verify(postRequestedFor(urlPathEqualTo("/external-post-endpoint"))
            .withHeader("Authorization", equalTo("Bearer another-token"))
            .withRequestBody(containing("request data")));

        assertEquals(200, result.getResponse().getStatus());

        String logs = output.getAll();

        assertFalse(logs.contains("accountNumber="));
        assertFalse(logs.contains("Authorization: Bearer another-token"));
    }

    @Test
    @SneakyThrows
    void testGetAccounts_ResponseJwtHeaderNotLogged(CapturedOutput output) {

        stubFor(WireMock.get(urlPathEqualTo("/external-get-endpoint"))
            .withQueryParam("accountNumber", equalTo("111222333"))
            .withQueryParam("status", equalTo("pending"))
            .willReturn(aResponse()
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .withHeader("jwt", "very-secret-jwt-response-token")
                .withHeader("custom-header", "custom-value")
                .withBody(RESPONSE_BODY)));

        String accountNumber = "111222333";
        String status = "pending";

        MvcResult result = mockMvc.perform(get("/launch-feign/test-get")
                .param("accountNumber", accountNumber)
                .param("status", status))
            .andExpect(status().isOk())
            .andReturn();

        verify(getRequestedFor(urlPathEqualTo("/external-get-endpoint"))
            .withQueryParam("accountNumber", equalTo("111222333"))
            .withQueryParam("status", equalTo("pending")));

        String responseContent = result.getResponse().getContentAsString();
        assertTrue(responseContent.contains("response data"));

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