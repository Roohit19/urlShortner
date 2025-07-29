package com.teleso.urlshortner.controller;

import com.teleso.urlshortner.security.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/auth")
public class AuthController {

	private final AuthenticationManager authManager;
	private final JwtUtil jwtUtil;

	public AuthController(AuthenticationManager authManager, JwtUtil jwtUtil) {
		this.authManager = authManager;
		this.jwtUtil = jwtUtil;
	}

	@GetMapping("/login")
	public String loginPage() {
		return "login";
	}

	@PostMapping("/login")
	public String doLogin(@RequestParam String username, @RequestParam String password, Model model) {
		try {
			authManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
			String token = jwtUtil.generateToken(username);
			System.out.println("Generated token: " + token);

			// Redirect with token - using UriComponentsBuilder to handle encoding properly
			return "redirect:/?token=" + java.net.URLEncoder.encode(token, java.nio.charset.StandardCharsets.UTF_8);

		} catch (AuthenticationException ex) {
			model.addAttribute("error", "Invalid Username or Password");
			return "login";
		}
	}
}
