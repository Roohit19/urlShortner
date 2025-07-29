package com.teleso.urlshortner.controller;

import com.teleso.urlshortner.service.UrlShortenerService;
import com.teleso.urlshortner.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UrlShortenerController.class)
class UrlShortenerControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private UrlShortenerService service;

	@MockBean
	private JwtUtil jwtUtil;

	private String validToken;

	@BeforeEach
	void setUp() {
		// Generate or stub a valid token for "rohit"
		validToken = "dummyJwtToken";
		given(jwtUtil.extractUsername(validToken)).willReturn("rohit");
		given(jwtUtil.validateToken(validToken, "rohit")).willReturn(true);
	}

	@Test
	void homePage_withoutToken_shouldReturnIndex() throws Exception {
		mockMvc.perform(get("/")).andExpect(status().isOk()).andExpect(view().name("index"));
	}

	@Test
	void homePage_withTokenParam_shouldReturnIndexAndIncludeToken() throws Exception {
		mockMvc.perform(get("/").param("token", validToken)).andExpect(status().isOk()).andExpect(view().name("index"))
				.andExpect(model().attribute("token", validToken));
	}

	@Test
	void shortenUrl_withValidToken_shouldReturnResult() throws Exception {
		// Stub service behavior
		given(service.shortenUrl("http://example.com")).willReturn("abcd");

		ResultActions result = mockMvc.perform(post("/shorten").param("originalUrl", "http://example.com")
				.param("token", validToken).with(SecurityMockMvcRequestPostProcessors.csrf())
				.contentType(MediaType.APPLICATION_FORM_URLENCODED));

		result.andExpect(status().isOk()).andExpect(view().name("result"))
				.andExpect(model().attribute("shortUrl", "http://localhost/abcd"))
				.andExpect(model().attribute("token", validToken));
	}

	@Test
	void redirect_existingShortCode_shouldRedirectToOriginal() throws Exception {
		given(service.getOriginalUrl("abcd")).willReturn("http://example.com");

		mockMvc.perform(get("/abcd")).andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("http://example.com"));
	}

	@Test
	void redirect_nonexistentShortCode_shouldReturnNotFound() throws Exception {
		given(service.getOriginalUrl("xxxx")).willReturn(null);

		mockMvc.perform(get("/xxxx")).andExpect(status().isNotFound());
	}
}
