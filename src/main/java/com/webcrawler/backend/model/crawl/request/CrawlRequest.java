package com.webcrawler.backend.model.crawl.request;

public class CrawlRequest {
	private final String keyword;

	public CrawlRequest(String keyword) {
		this.keyword = keyword;
	}

	public String getKeyword() {
		return keyword;
	}
}
