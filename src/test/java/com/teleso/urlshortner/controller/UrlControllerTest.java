package com.teleso.urlshortner.controller;

import com.teleso.urlshortner.service.UrlShortenerService;
import com.teleso.urlshortner.exception.InvalidUrlException;
import com.teleso.urlshortner.exception.UrlNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@TestPropertySource(properties = { "app.base-url=http://localhost:4444",
		"jwt.secret=test-secret-for-testing-purposes-only-minimum-length" })
class UrlShortenerControllerTest {

	@Autowired
	private WebApplicationContext webApplicationContext;

	private MockMvc mockMvc;

	@MockBean
	private UrlShortenerService service;

	@BeforeEach
	void setup() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}

	@Test
	void homePage_withoutToken_shouldReturnIndex() throws Exception {
		mockMvc.perform(get("/")).andExpect(status().isOk()).andExpect(view().name("index"))
				.andExpect(model().attributeDoesNotExist("token"));
	}

	@Test
	void homePage_withTokenParam_shouldReturnIndexAndIncludeToken() throws Exception {
		String token = "dummyJwtToken";

		mockMvc.perform(get("/").param("token", token)).andExpect(status().isOk()).andExpect(view().name("index"))
				.andExpect(model().attribute("token", token));
	}

	@Test
	void shortenUrl_anonymousUser_shouldReturnRandomCode() throws Exception {
		// Mock service to return random code for anonymous user
		given(service.shortenUrl("http://example.com")).willReturn("a3Bx");

		mockMvc.perform(post("/shorten").param("originalUrl", "http://example.com")
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)).andExpect(status().isOk())
				.andExpect(view().name("result"))
				.andExpect(model().attribute("shortUrl", "http://localhost:4444/s/a3Bx"));
	}

	@Test
	void shortenUrl_withValidToken_shouldReturnRandomCodeWhenNoCustomCode() throws Exception {
		String token = "dummyJwtToken";

		// Mock service behavior for authenticated user without custom code
		given(service.shortenUrl("http://example.com")).willReturn("b5Cy");

		mockMvc.perform(post("/shorten").param("originalUrl", "http://example.com").param("token", token)
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)).andExpect(status().isOk())
				.andExpect(view().name("result"))
				.andExpect(model().attribute("shortUrl", "http://localhost:4444/s/b5Cy"))
				.andExpect(model().attribute("token", token));
	}

	@Test
	void shortenUrl_withValidTokenAndCustomCode_shouldReturnCustomCode() throws Exception {
		String token = "dummyJwtToken";

		// Mock service behavior for custom code
		given(service.shortenUrlWithCustomCode("http://example.com", "mycode")).willReturn("mycode");

		mockMvc.perform(post("/shorten").param("originalUrl", "http://example.com").param("token", token)
				.param("customCode", "mycode").contentType(MediaType.APPLICATION_FORM_URLENCODED))
				.andExpect(status().isOk()).andExpect(view().name("result"))
				.andExpect(model().attribute("shortUrl", "http://localhost:4444/s/mycode"))
				.andExpect(model().attribute("token", token));
	}

	@Test
	void shortenUrl_withInvalidUrl_shouldReturnErrorToIndex() throws Exception {
		// Mock service to throw exception for invalid URL
		given(service.shortenUrl("invalid-url")).willThrow(new InvalidUrlException("Invalid URL format"));

		mockMvc.perform(
				post("/shorten").param("originalUrl", "invalid-url").contentType(MediaType.APPLICATION_FORM_URLENCODED))
				.andExpect(status().isOk()).andExpect(view().name("index"))
				.andExpect(model().attribute("error", "Invalid URL format"));
	}

	@Test
	void redirect_withValidShortCode_shouldRedirectToOriginalUrl() throws Exception {
		// Mock service to return original URL
		given(service.getOriginalUrl("a3Bx")).willReturn("http://example.com");

		mockMvc.perform(get("/s/a3Bx")).andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("http://example.com"));
	}

	@Test
	void redirect_withInvalidShortCode_shouldThrowException() throws Exception {
		// Mock service to throw exception for invalid short code
		given(service.getOriginalUrl("invalid")).willThrow(new UrlNotFoundException("Short URL not found"));

		// Controller wraps exception in RuntimeException, caught by generic Exception
		// handler
		mockMvc.perform(get("/s/invalid")).andExpect(status().isOk()).andExpect(view().name("index"))
				.andExpect(model().attribute("error", "An unexpected error occurred. Please try again."));
	}

	@Test
	void shortenUrl_anonymousUserWithCustomCode_shouldIgnoreCustomCode() throws Exception {
		// Anonymous user tries to send custom code - should be ignored
		given(service.shortenUrl("http://example.com")).willReturn("x9Zk");

		mockMvc.perform(
				post("/shorten").param("originalUrl", "http://example.com").param("customCode", "shouldbeignored") // No
																													// token,
																													// so
																													// this
																													// should
																													// be
																													// ignored
						.contentType(MediaType.APPLICATION_FORM_URLENCODED))
				.andExpect(status().isOk()).andExpect(view().name("result"))
				.andExpect(model().attribute("shortUrl", "http://localhost:4444/s/x9Zk"))
				.andExpect(model().attributeDoesNotExist("token"));
	}
}