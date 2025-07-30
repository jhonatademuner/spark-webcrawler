package com.webcrawler.backend.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URL;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class LinkExtractorTest {

	private LinkExtractor extractor;
	private URL baseUrl;

	@BeforeEach
	void setUp() throws Exception {
		extractor = new LinkExtractor();
		baseUrl = new URL("https://example.com");
	}

	@Test
	void testExtractRelativeLinks() throws Exception {
		String html = "<html>"
				+ "<a href=\"/about.html\">About</a>"
				+ "<a href=\"/contact.html\">Contact</a>"
				+ "</html>";
		Set<String> links = extractor.extractLinks(baseUrl, html);

		assertEquals(2, links.size());
		assertTrue(links.contains("https://example.com/about.html"));
		assertTrue(links.contains("https://example.com/contact.html"));
	}

	@Test
	void testExtractAbsoluteLinksSameHost() throws Exception {
		String html = "<html>"
				+ "<a href=\"https://example.com/page1\">Page 1</a>"
				+ "<a href=\"https://example.com/page2\">Page 2</a>"
				+ "</html>";
		Set<String> links = extractor.extractLinks(baseUrl, html);

		assertEquals(2, links.size());
		assertTrue(links.contains("https://example.com/page1"));
		assertTrue(links.contains("https://example.com/page2"));
	}

	@Test
	void testIgnoreExternalLinks() throws Exception {
		String html = "<html>"
				+ "<a href=\"https://other.com/home\">External</a>"
				+ "<a href=\"/internal.html\">Internal</a>"
				+ "</html>";
		Set<String> links = extractor.extractLinks(baseUrl, html);

		assertEquals(1, links.size());
		assertTrue(links.contains("https://example.com/internal.html"));
	}

	@Test
	void testIgnoreMalformedUrls() throws Exception {
		String html = "<html>"
				+ "<a href=\"%%%\">Bad URL</a>"
				+ "<a href=\"/valid.html\">Valid</a>"
				+ "</html>";
		Set<String> links = extractor.extractLinks(baseUrl, html);

		assertEquals(1, links.size());
		assertTrue(links.contains("https://example.com/valid.html"));
	}

	@Test
	void testEmptyOrNoLinks() throws Exception {
		String html = "<html><head></head><body><p>No links here</p></body></html>";
		Set<String> links = extractor.extractLinks(baseUrl, html);

		assertTrue(links.isEmpty());
	}

	@Test
	void testDuplicateLinks() throws Exception {
		String html = "<html>"
				+ "<a href=\"/page.html\">One</a>"
				+ "<a href=\"/page.html\">Two</a>"
				+ "</html>";
		Set<String> links = extractor.extractLinks(baseUrl, html);

		assertEquals(1, links.size());
		assertTrue(links.contains("https://example.com/page.html"));
	}
}
