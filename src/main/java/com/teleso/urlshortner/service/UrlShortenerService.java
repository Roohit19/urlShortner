package com.teleso.urlshortner.service;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UrlShortenerService {

	private static final String BASE62 = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private static final int SHORT_CODE_LENGTH = 4;

	private final Map<String, String> shortToOriginal = new ConcurrentHashMap<>();
	private final Map<String, String> originalToShort = new ConcurrentHashMap<>();
	private final Random random = new Random();

	public String shortenUrl(String originalUrl) {
		// Normalize URL (add http:// if missing)
		if (!originalUrl.startsWith("http://") && !originalUrl.startsWith("https://")) {
			originalUrl = "http://" + originalUrl;
		}

		if (originalToShort.containsKey(originalUrl)) {
			return originalToShort.get(originalUrl);
		}

		String shortCode = generateShortCode();
		shortToOriginal.put(shortCode, originalUrl);
		originalToShort.put(originalUrl, shortCode);
		return shortCode;
	}

	public String getOriginalUrl(String shortCode) {
		return shortToOriginal.get(shortCode);
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
