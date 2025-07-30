package com.teleso.urlshortner.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(InvalidUrlException.class)
	public String handleInvalidUrlException(InvalidUrlException e, Model model) {
		model.addAttribute("error", e.getMessage());
		return "index"; // Return to home page with error
	}

	@ExceptionHandler(UrlNotFoundException.class)
	public String handleUrlNotFoundException(UrlNotFoundException e, Model model) {
		model.addAttribute("error", "Short URL not found. Please check the link and try again.");
		return "index"; // Return to home page with error
	}

	@ExceptionHandler(Exception.class)
	public String handleGenericException(Exception e, Model model) {
		model.addAttribute("error", "An unexpected error occurred. Please try again.");
		return "index";
	}
}