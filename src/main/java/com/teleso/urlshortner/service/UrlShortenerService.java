package com.teleso.urlshortner.service;

import com.teleso.urlshortner.exception.InvalidUrlException;
import com.teleso.urlshortner.exception.UrlNotFoundException;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UrlShortenerService {

	private static final String BASE62 = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private static final int SHORT_CODE_LENGTH = 4;
	private static final int MAX_URL_LENGTH = 2048;

	private final Map<String, String> shortToOriginal = new ConcurrentHashMap<>();
	private final Map<String, String> originalToShort = new ConcurrentHashMap<>();
	private final Random random = new Random();

	public String shortenUrl(String originalUrl) {
		validateUrl(originalUrl);
		originalUrl = normalizeUrl(originalUrl);

		if (originalToShort.containsKey(originalUrl)) {
			return originalToShort.get(originalUrl);
		}

		String shortCode = generateShortCode();
		shortToOriginal.put(shortCode, originalUrl);
		originalToShort.put(originalUrl, shortCode);
		return shortCode;
	}

	// ADDED: New method for custom short codes
	public String shortenUrlWithCustomCode(String originalUrl, String customCode) {
		validateUrl(originalUrl);
		originalUrl = normalizeUrl(originalUrl);

		// ADDED: Validate custom code first
		validateCustomCode(customCode);

		// ADDED: Check if custom code already exists
		if (shortToOriginal.containsKey(customCode)) {
			throw new InvalidUrlException("Custom code '" + customCode + "' is already taken");
		}

		// ADDED: Check if original URL already has a mapping - remove old one if using
		// custom
		if (originalToShort.containsKey(originalUrl)) {
			String oldCode = originalToShort.get(originalUrl);
			shortToOriginal.remove(oldCode);
		}

		// ADDED: Create new mapping with custom code
		shortToOriginal.put(customCode, originalUrl);
		originalToShort.put(originalUrl, customCode);

		return customCode;
	}

	// ADDED: New validation method for custom codes
	private void validateCustomCode(String customCode) {
		if (customCode.length() < 3 || customCode.length() > 20) {
			throw new InvalidUrlException("Custom code must be between 3-20 characters");
		}

		if (!customCode.matches("^[a-zA-Z0-9-_]+$")) {
			throw new InvalidUrlException("Custom code can only contain letters, numbers, hyphens and underscores");
		}

		// ADDED: Prevent conflicts with system paths
		if (customCode.equals("auth") || customCode.equals("s") || customCode.equals("shorten")) {
			throw new InvalidUrlException("Custom code cannot use reserved words");
		}
	}

	public String getOriginalUrl(String shortCode) {
		if (shortCode == null || shortCode.trim().isEmpty()) {
			throw new InvalidUrlException("Short code cannot be empty");
		}

		String originalUrl = shortToOriginal.get(shortCode);
		if (originalUrl == null) {
			throw new UrlNotFoundException("Short URL not found: " + shortCode);
		}

		return originalUrl;
	}

	private void validateUrl(String url) {
		if (url == null || url.trim().isEmpty()) {
			throw new InvalidUrlException("URL cannot be empty");
		}

		if (url.length() > MAX_URL_LENGTH) {
			throw new InvalidUrlException("URL too long. Maximum length is " + MAX_URL_LENGTH + " characters");
		}

		String normalizedUrl = normalizeUrl(url);
		System.out.println("DEBUG: Normalized URL: '" + normalizedUrl + "'");

		try {
			URL urlObj = new URL(normalizedUrl);

			String protocol = urlObj.getProtocol().toLowerCase();
			if (!protocol.equals("http") && !protocol.equals("https")) {
				throw new InvalidUrlException("Only HTTP and HTTPS URLs are allowed");
			}

			String host = urlObj.getHost();
			if (host == null || host.trim().isEmpty()) {
				throw new InvalidUrlException("URL must have a valid host");
			}

			if (!isValidHostname(host)) {
				throw new InvalidUrlException(
						"Invalid hostname format. Please enter a valid URL like https://example.com");
			}

		} catch (MalformedURLException e) {
			throw new InvalidUrlException("Invalid URL format: " + e.getMessage());
		}
	}

	private boolean isValidHostname(String hostname) {
		if (!hostname.contains(".")) {
			return false;
		}

		if (hostname.startsWith(".") || hostname.endsWith(".")) {
			return false;
		}

		if (hostname.contains("..")) {
			return false;
		}

		return hostname.matches("^[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
	}

	private String normalizeUrl(String url) {
		if (!url.startsWith("http://") && !url.startsWith("https://")) {
			return "http://" + url;
		}
		return url;
	}

	private String generateShortCode() {
		String code;
		do {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < SHORT_CODE_LENGTH; i++) {
				int index = random.nextInt(BASE62.length());
				sb.append(BASE62.charAt(index));
			}
			code = sb.toString();
		} while (shortToOriginal.containsKey(code));
		return code;
	}
}