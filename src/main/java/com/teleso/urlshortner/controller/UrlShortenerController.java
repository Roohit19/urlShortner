package com.teleso.urlshortner.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
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
	public String homePage() {
		return "index"; // returns templates/index.html
	}

	@PostMapping("/shorten")
	public String shortenUrl(@RequestParam String originalUrl, Model model) {
		if (originalUrl == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "URL must not be empty");
		}

		String shortCode = service.shortenUrl(originalUrl);

		model.addAttribute("shortUrl", baseUrl + "/" + shortCode);

		return "result";
	}

	@GetMapping("/{shortCode}")
	public RedirectView redirect(@PathVariable String shortCode) {
		String originalUrl = service.getOriginalUrl(shortCode);
		if (originalUrl == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Short URL not found");
		}

		return new RedirectView(originalUrl);
	}
}
