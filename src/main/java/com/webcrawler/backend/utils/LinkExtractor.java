package com.webcrawler.backend.utils;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The LinkExtractor class is responsible for extracting links from HTML content.
 * It uses a regular expression to find anchor tags and resolves relative URLs
 * against a base URL. It also filters out links that do not belong to the same host.
 */

public class LinkExtractor {

	private static final Pattern LINK_PATTERN =
			Pattern.compile("<a\\s+[^>]*href=[\"']([^\"']+)[\"']", Pattern.CASE_INSENSITIVE);

	public Set<String> extractLinks(URL baseUrl, String html) {
		Set<String> links = new HashSet<>();
		Matcher matcher = LINK_PATTERN.matcher(html);

		while (matcher.find()) {
			String href = matcher.group(1);
			try {
				URL resolved = new URL(baseUrl, href);
				new URI(resolved.toString()); // Check if the URL is well-formed
				if (resolved.getHost().equals(baseUrl.getHost())) {
					links.add(resolved.toString());
				}
			} catch (MalformedURLException | URISyntaxException ignored) {
				// Ignore malformed URLs
			}
		}
		return links;
	}
}
