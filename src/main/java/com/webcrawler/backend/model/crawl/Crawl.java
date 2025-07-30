package com.webcrawler.backend.model.crawl;

import java.time.Instant;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Represents a web crawl job with its associated metadata.
 * This class is used to track the status, matched URLs, and visited URLs during a web crawling operation.
 */

public class Crawl {
	private final String id;
	private final String keyword;
	private final Set<String> matchedUrls = ConcurrentHashMap.newKeySet();
	private final Set<String> visitedUrls = ConcurrentHashMap.newKeySet();
	private volatile CrawlStatus status = CrawlStatus.ACTIVE;
	private final Instant createdAt = Instant.now();

	public Crawl(String id, String keyword) {
		this.id = id;
		this.keyword = keyword;
	}

	public String getId() {
		return id;
	}

	public String getKeyword() {
		return keyword;
	}

	public Set<String> getMatchedUrls() {
		return matchedUrls;
	}

	public Set<String> getVisitedUrls() {
		return visitedUrls;
	}

	public CrawlStatus getStatus() {
		return status;
	}

	public void setStatus(CrawlStatus status) {
		this.status = status;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public void addMatchedUrl(String url) {
		matchedUrls.add(url);
	}

}
