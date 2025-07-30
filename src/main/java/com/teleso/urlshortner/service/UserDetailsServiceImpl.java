package com.teleso.urlshortner.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	private static final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
	// In-memory user store
	private final Map<String, String> users = new HashMap<>();

	public UserDetailsServiceImpl() {
		// Initialize users (username -> password)
		users.put("rohit", "1234");
		users.put("admin", "admin123");
		users.put("user", "password");
		users.put("john", "john123");
		users.put("demo", "demo");
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		String password = users.get(username);

		if (password != null) {
			return User.builder().username(username).password(passwordEncoder.encode(password)).roles("USER").build();
		} else {
			throw new UsernameNotFoundException("User not found: " + username);
		}
	}
}
