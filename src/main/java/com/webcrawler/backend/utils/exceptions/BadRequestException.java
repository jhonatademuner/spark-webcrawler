package com.webcrawler.backend.utils.exceptions;

public class BadRequestException extends RuntimeException {
	public BadRequestException(String message) {
		super(message);
	}
}