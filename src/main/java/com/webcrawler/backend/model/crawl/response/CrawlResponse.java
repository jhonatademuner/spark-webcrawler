package com.webcrawler.backend.model.crawl.response;

import java.util.Set;

public class CrawlResponse {
	private String id;
	private String status;
	private Set<String> urls;

	public CrawlResponse(String id, String status, Set<String> urls) {
		this.id = id;
		this.status = status;
		this.urls = urls;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Set<String> getUrls() {
		return urls;
	}

	public void setUrls(Set<String> urls) {
		this.urls = urls;
	}

	public void addUrl(String url) {
		this.urls.add(url);
	}
}
