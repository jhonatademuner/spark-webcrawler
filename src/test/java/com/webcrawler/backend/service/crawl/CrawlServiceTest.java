package com.webcrawler.backend.service.crawl;

import com.webcrawler.backend.model.crawl.request.CrawlRequest;
import com.webcrawler.backend.model.crawl.response.CrawlResponse;
import com.webcrawler.backend.model.crawl.response.SimplifiedCrawlResponse;
import com.webcrawler.backend.utils.exceptions.BadRequestException;
import com.webcrawler.backend.utils.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CrawlServiceTest {

	private final CrawlService service = new CrawlService();

	@Test
	void startCrawl_shouldCreateJobAndReturnId() {
		CrawlRequest request = new CrawlRequest("validKeyword");

		SimplifiedCrawlResponse response = service.startCrawl(request);

		assertNotNull(response);
		assertNotNull(response.getId());

		CrawlResponse result = service.getCrawlResults(response.getId());

		assertEquals(response.getId(), result.getId());
		assertNotNull(result.getStatus());
		assertNotNull(result.getUrls());  // this is the matched URLs set (may be empty initially)
	}

	@Test
	void startCrawl_shouldRejectNullRequest() {
		BadRequestException ex = assertThrows(BadRequestException.class, () -> {
			service.startCrawl(null);
		});
		assertTrue(ex.getMessage().toLowerCase().contains("keyword"));
	}

	@Test
	void startCrawl_shouldRejectBlankKeyword() {
		CrawlRequest request = new CrawlRequest("  ");
		assertThrows(BadRequestException.class, () -> service.startCrawl(request));
	}

	@Test
	void startCrawl_shouldRejectShortKeyword() {
		CrawlRequest request = new CrawlRequest("a");
		assertThrows(BadRequestException.class, () -> service.startCrawl(request));
	}

	@Test
	void startCrawl_shouldRejectLongKeyword() {
		CrawlRequest request = new CrawlRequest("a".repeat(40));
		assertThrows(BadRequestException.class, () -> service.startCrawl(request));
	}

	@Test
	void getCrawlResults_shouldReturnCrawlJob() {
		CrawlRequest request = new CrawlRequest("keyword");
		SimplifiedCrawlResponse created = service.startCrawl(request);

		CrawlResponse result = service.getCrawlResults(created.getId());

		assertNotNull(result);
		assertEquals(created.getId(), result.getId());
		assertNotNull(result.getStatus());
		assertNotNull(result.getUrls());
	}

	@Test
	void getCrawlResults_shouldRejectBlankId() {
		assertThrows(BadRequestException.class, () -> service.getCrawlResults(" "));
	}

	@Test
	void getCrawlResults_shouldRejectUnknownId() {
		assertThrows(ResourceNotFoundException.class, () -> service.getCrawlResults("non-existent-id"));
	}
}
