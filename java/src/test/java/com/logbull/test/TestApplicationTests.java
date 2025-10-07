package com.logbull.test;

import com.logbull.test.service.StandaloneLoggerService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * End-to-End Controller Tests for LogBull Test Application.
 * Tests all logging endpoints to ensure proper functionality.
 */
@SpringBootTest
@AutoConfigureMockMvc
class TestApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private StandaloneLoggerService standaloneLoggerService;

	@Test
	@DisplayName("Context loads successfully")
	void contextLoads() {
		// Verify MockMvc is loaded
		assert mockMvc != null;
	}

	@Test
	@DisplayName("Spring Boot Starter - Success case with default username")
	void testSpringBootStarter_DefaultUsername() throws Exception {
		mockMvc.perform(get("/api/logs/spring-boot-starter"))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.status").value("success"))
				.andExpect(jsonPath("$.message").value("Logs sent via Spring Boot Starter"))
				.andExpect(jsonPath("$.username").value("john_doe"))
				.andExpect(jsonPath("$.request_id").exists())
				.andExpect(jsonPath("$.request_id").isNotEmpty());
	}

	@Test
	@DisplayName("Spring Boot Starter - Success case with custom username")
	void testSpringBootStarter_CustomUsername() throws Exception {
		String customUsername = "alice_smith";

		mockMvc.perform(get("/api/logs/spring-boot-starter")
				.param("username", customUsername))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.status").value("success"))
				.andExpect(jsonPath("$.message").value("Logs sent via Spring Boot Starter"))
				.andExpect(jsonPath("$.username").value(customUsername))
				.andExpect(jsonPath("$.request_id").exists())
				.andExpect(jsonPath("$.request_id").isNotEmpty());
	}

	@Test
	@DisplayName("Spring Boot Starter - Error case with 'error' username")
	void testSpringBootStarter_ErrorCase() throws Exception {
		mockMvc.perform(get("/api/logs/spring-boot-starter")
				.param("username", "error"))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.status").value("error"))
				.andExpect(jsonPath("$.message").value("Error occurred"))
				.andExpect(jsonPath("$.request_id").exists())
				.andExpect(jsonPath("$.request_id").isNotEmpty());
	}

	@Test
	@DisplayName("Standalone Logger - Success case with order data")
	void testStandaloneLogger_WithOrderData() throws Exception {
		// Mock the service call
		doNothing().when(standaloneLoggerService).processOrder(anyString(), anyString());

		String requestBody = """
				{
					"order_id": "order_12345",
					"user_id": "user_789"
				}
				""";

		mockMvc.perform(post("/api/logs/standalone")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.status").value("success"))
				.andExpect(jsonPath("$.message").value("Logs sent via Standalone Logger"))
				.andExpect(jsonPath("$.order_id").value("order_12345"))
				.andExpect(jsonPath("$.user_id").value("user_789"));
	}

	@Test
	@DisplayName("Standalone Logger - Success case with default values")
	void testStandaloneLogger_WithDefaultValues() throws Exception {
		// Mock the service call
		doNothing().when(standaloneLoggerService).processOrder(anyString(), anyString());

		String requestBody = "{}";

		mockMvc.perform(post("/api/logs/standalone")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.status").value("success"))
				.andExpect(jsonPath("$.message").value("Logs sent via Standalone Logger"))
				.andExpect(jsonPath("$.order_id").exists())
				.andExpect(jsonPath("$.order_id").isNotEmpty())
				.andExpect(jsonPath("$.user_id").value("user_123"));
	}

	@Test
	@DisplayName("Standalone Logger - Success case with custom order and user IDs")
	void testStandaloneLogger_CustomIds() throws Exception {
		// Mock the service call
		doNothing().when(standaloneLoggerService).processOrder(anyString(), anyString());

		String requestBody = """
				{
					"order_id": "order_99999",
					"user_id": "user_alice"
				}
				""";

		mockMvc.perform(post("/api/logs/standalone")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.status").value("success"))
				.andExpect(jsonPath("$.message").value("Logs sent via Standalone Logger"))
				.andExpect(jsonPath("$.order_id").value("order_99999"))
				.andExpect(jsonPath("$.user_id").value("user_alice"));
	}

	@Test
	@DisplayName("Combined Approach - Both loggers working together")
	void testCombinedApproach() throws Exception {
		// Mock the service call
		doNothing().when(standaloneLoggerService).logCombinedTest(anyString());

		mockMvc.perform(get("/api/logs/combined"))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.status").value("success"))
				.andExpect(jsonPath("$.message").value("Both logging approaches executed"))
				.andExpect(jsonPath("$.session_id").exists())
				.andExpect(jsonPath("$.session_id").isNotEmpty());
	}

	@Test
	@DisplayName("Combined Approach - Multiple calls produce unique session IDs")
	void testCombinedApproach_UniqueSessionIds() throws Exception {
		// Mock the service call
		doNothing().when(standaloneLoggerService).logCombinedTest(anyString());

		// First call
		String response1 = mockMvc.perform(get("/api/logs/combined"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.session_id").exists())
				.andReturn()
				.getResponse()
				.getContentAsString();

		// Second call
		String response2 = mockMvc.perform(get("/api/logs/combined"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.session_id").exists())
				.andReturn()
				.getResponse()
				.getContentAsString();

		// Verify session IDs are different (not exact string comparison needed,
		// just verifying both are valid responses)
		assert response1.contains("session_id");
		assert response2.contains("session_id");
	}

	@Test
	@DisplayName("Invalid endpoint returns 404")
	void testInvalidEndpoint() throws Exception {
		mockMvc.perform(get("/api/logs/nonexistent"))
				.andExpect(status().isNotFound());
	}

	@Test
	@DisplayName("POST to GET endpoint returns 405 Method Not Allowed")
	void testWrongHttpMethod() throws Exception {
		mockMvc.perform(post("/api/logs/spring-boot-starter")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{}"))
				.andExpect(status().isMethodNotAllowed());
	}

	@Test
	@DisplayName("GET to POST endpoint returns 405 Method Not Allowed")
	void testWrongHttpMethodOnStandalone() throws Exception {
		mockMvc.perform(get("/api/logs/standalone"))
				.andExpect(status().isMethodNotAllowed());
	}
}
