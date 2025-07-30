package com.webcrawler.backend.utils;

import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Utility class for generating unique alphanumeric IDs.
 * The generated ID is a substring of a UUID, cleaned of non-alphanumeric characters.
 */

public class IdGenerator {
	private static final Pattern ALPHANUMERIC = Pattern.compile("[^A-Za-z0-9]");

	public static String generateId() {
		return generateId(8); // Default length of 8 characters
	}

	public static String generateId(int length) {
		if (length < 1 || length > 32) {
			throw new IllegalArgumentException("Length must be between 1 and 32 characters.");
		}
		String uuid = UUID.randomUUID().toString();
		return ALPHANUMERIC.matcher(uuid).replaceAll("").substring(0, length);
	}
}
