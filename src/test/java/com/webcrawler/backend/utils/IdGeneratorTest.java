package com.webcrawler.backend.utils;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class IdGeneratorTest {

	@Test
	void generateId_defaultLength() {
		String id = IdGenerator.generateId();
		assertNotNull(id);
		assertEquals(8, id.length());
		assertTrue(id.matches("[A-Za-z0-9]+"));
	}

	@Test
	void generateId_customValidLength() {
		for (int len = 1; len <= 32; len++) {
			String id = IdGenerator.generateId(len);
			assertNotNull(id);
			assertEquals(len, id.length());
			assertTrue(id.matches("[A-Za-z0-9]+"));
		}
	}

	@Test
	void generateId_invalidLength_throwsException() {
		assertThrows(IllegalArgumentException.class, () -> IdGenerator.generateId(0));
		assertThrows(IllegalArgumentException.class, () -> IdGenerator.generateId(-5));
		assertThrows(IllegalArgumentException.class, () -> IdGenerator.generateId(33));
	}

	@Test
	void generateId_returnsUniqueIds() {
		Set<String> generated = new HashSet<>();
		for (int i = 0; i < 1000; i++) {
			String id = IdGenerator.generateId();
			assertFalse(generated.contains(id), "Duplicate ID found: " + id);
			generated.add(id);
		}
	}
}
