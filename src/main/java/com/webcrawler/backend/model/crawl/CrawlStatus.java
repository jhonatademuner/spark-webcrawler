package com.webcrawler.backend.model.crawl;

public enum CrawlStatus {
	ACTIVE("active"),
	DONE("done");

	private final String displayName;

	CrawlStatus(String displayName) {
		this.displayName = displayName;
	}

	public String getDisplayName() {
		return displayName;
	}

}
