package com.teleso.urlshortner.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.view.RedirectView;

import com.teleso.urlshortner.dto.UrlRequest;
import com.teleso.urlshortner.service.UrlShortenerService;

@RestController
public class UrlShortenerController {

	private final UrlShortenerService service;

	@Value("${app.base-url}")
	private String baseUrl;

	public UrlShortenerController(UrlShortenerService service) {
		this.service = service;
	}

	@PostMapping("/shorten")
	public ResponseEntity<String> shortenUrl(@RequestBody UrlRequest request) {
		if (request.getUrl() == null || request.getUrl().isBlank()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "URL must not be empty");
		}

		String shortCode = service.shortenUrl(request.getUrl());
		return ResponseEntity.ok(baseUrl + "/" + shortCode);
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
