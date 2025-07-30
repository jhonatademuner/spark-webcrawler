package com.webcrawler.backend.controller.crawl;

import com.webcrawler.backend.model.crawl.request.CrawlRequest;
import com.webcrawler.backend.model.crawl.response.CrawlResponse;
import com.webcrawler.backend.model.crawl.response.SimplifiedCrawlResponse;
import com.webcrawler.backend.service.crawl.CrawlService;
import com.google.gson.Gson;
import spark.Request;
import spark.Response;
import spark.utils.StringUtils;

import static spark.Spark.*;

/**
 * CrawlController handles the HTTP requests related to web crawling operations.
 * It provides endpoints to start a crawl and retrieve crawl results.
 */

public class CrawlController {

	private final Gson gson;
	private final CrawlService crawlService;

	public CrawlController() {
		this(new CrawlService(), new Gson());
	}

	// Constructor for tests
	public CrawlController(CrawlService crawlService, Gson gson) {
		this.crawlService = crawlService;
		this.gson = gson;
	}

	public void setupRoutes() {
		post("/crawl", this::startCrawl, gson::toJson);
		get("/crawl/:id", this::getCrawlResults, gson::toJson);
	}

	public SimplifiedCrawlResponse startCrawl(Request req, Response res) {
		CrawlRequest crawlRequest = gson.fromJson(req.body(), CrawlRequest.class);
		res.status(200); // OK
		return crawlService.startCrawl(crawlRequest);
	}

	public CrawlResponse getCrawlResults(Request req, Response res) {
		String id = req.params(":id");
		res.status(200); // OK
		return crawlService.getCrawlResults(id);
	}

}
