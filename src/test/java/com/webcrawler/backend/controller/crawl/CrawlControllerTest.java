package com.webcrawler.backend.controller.crawl;

import com.webcrawler.backend.model.crawl.request.CrawlRequest;
import com.webcrawler.backend.model.crawl.response.CrawlResponse;
import com.webcrawler.backend.model.crawl.response.SimplifiedCrawlResponse;
import com.webcrawler.backend.service.crawl.CrawlService;
import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spark.Request;
import spark.Response;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CrawlControllerTest {

	private CrawlService mockService;
	private Gson gson;
	private CrawlController controller;

	private Request mockRequest;
	private Response mockResponse;

	@BeforeEach
	void setup() {
		mockService = mock(CrawlService.class);
		gson = new Gson();
		controller = new CrawlController(mockService, gson);

		mockRequest = mock(Request.class);
		mockResponse = mock(Response.class);
	}

	@Test
	void startCrawl_shouldParseRequestAndReturnResponse() {
		CrawlRequest requestObj = new CrawlRequest("keyword");
		String jsonRequest = gson.toJson(requestObj);
		SimplifiedCrawlResponse expectedResponse = new SimplifiedCrawlResponse("job-id-123");

		when(mockRequest.body()).thenReturn(jsonRequest);
		when(mockService.startCrawl(any(CrawlRequest.class))).thenReturn(expectedResponse);

		SimplifiedCrawlResponse response = controller.startCrawl(mockRequest, mockResponse);

		verify(mockResponse).status(200);
		verify(mockService).startCrawl(any(CrawlRequest.class));
		assertEquals(expectedResponse.getId(), response.getId());
	}

	@Test
	void getCrawlResults_shouldReturnCrawlResponse() {
		String jobId = "job-456";
		CrawlResponse expectedResponse = new CrawlResponse(jobId, "DONE", Set.of("http://example.com"));

		when(mockRequest.params(":id")).thenReturn(jobId);
		when(mockService.getCrawlResults(jobId)).thenReturn(expectedResponse);

		CrawlResponse response = controller.getCrawlResults(mockRequest, mockResponse);

		verify(mockResponse).status(200);
		verify(mockService).getCrawlResults(jobId);
		assertEquals(jobId, response.getId());
		assertEquals("DONE", response.getStatus());
		assertTrue(response.getUrls().contains("http://example.com"));
	}
}
