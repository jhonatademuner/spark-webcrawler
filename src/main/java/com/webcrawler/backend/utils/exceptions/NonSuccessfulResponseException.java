package com.webcrawler.backend.utils.exceptions;

public class NonSuccessfulResponseException extends RuntimeException {
	public NonSuccessfulResponseException(String message) {
		super(message);
	}
}
