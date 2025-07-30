package com.teleso.urlshortner.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtil {

	@Value("${jwt.secret}")
	private String secretKey;

	private Key getSigningKey() {
		return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
	}

	private static final long EXPIRATION_MS = 1000 * 60 * 60 * 5; // 5 hours

	// Generate JWT token for a given username
	public String generateToken(String username) {
		return Jwts.builder().setSubject(username).setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_MS))
				.signWith(getSigningKey(), SignatureAlgorithm.HS256).compact();
	}

	// Extract username from token
	public String extractUsername(String token) {
		return extractClaim(token, Claims::getSubject);
	}

	// Validate token
	public boolean validateToken(String token, String username) {
		String extractedUsername = extractUsername(token);
		return (extractedUsername.equals(username) && !isTokenExpired(token));
	}

	private boolean isTokenExpired(String token) {
		Date expiration = extractClaim(token, Claims::getExpiration);
		return expiration.before(new Date());
	}

	private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		Claims claims = Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody();
		return claimsResolver.apply(claims);
	}
}
