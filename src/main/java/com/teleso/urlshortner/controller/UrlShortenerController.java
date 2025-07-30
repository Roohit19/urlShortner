package com.teleso.urlshortner.controller;

import com.teleso.urlshortner.exception.InvalidUrlException;
import com.teleso.urlshortner.exception.UrlNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import com.teleso.urlshortner.service.UrlShortenerService;

@Controller
public class UrlShortenerController {

	private final UrlShortenerService service;

	@Value("${app.base-url}")
	private String baseUrl;

	public UrlShortenerController(UrlShortenerService service) {
		this.service = service;
	}

	@GetMapping("/")
	public String homePage(Model model, @RequestParam(value = "token", required = false) String token) {
		if (token != null) {
			model.addAttribute("token", token);
		}
		return "index";
	}

	// CHANGED: Modified this method to accept customCode parameter
	@PostMapping("/shorten")
	public String shortenUrl(@RequestParam String originalUrl,
			@RequestParam(value = "token", required = false) String token,
			@RequestParam(value = "customCode", required = false) String customCode, Model model) {
		try {
			String shortCode;

			// CHANGED: Added logic to use custom code for authenticated users
			if (token != null && customCode != null && !customCode.trim().isEmpty()) {
				shortCode = service.shortenUrlWithCustomCode(originalUrl, customCode.trim());
			} else {
				shortCode = service.shortenUrl(originalUrl);
			}

			model.addAttribute("shortUrl", baseUrl + "/s/" + shortCode);
			if (token != null) {
				model.addAttribute("token", token);
			}
			return "result";

		} catch (InvalidUrlException e) {
			model.addAttribute("error", e.getMessage());
			if (token != null) {
				model.addAttribute("token", token);
			}
			return "index";
		}
	}

	@GetMapping("/s/{shortCode}")
	public RedirectView redirect(@PathVariable String shortCode) {
		try {
			String originalUrl = service.getOriginalUrl(shortCode);
			return new RedirectView(originalUrl);

		} catch (UrlNotFoundException | InvalidUrlException e) {
			throw new RuntimeException("Short URL not found", e);
		}
	}
}