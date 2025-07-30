package com.webcrawler.backend.service.crawl;

import com.webcrawler.backend.model.crawl.Crawl;
import com.webcrawler.backend.model.crawl.CrawlStatus;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CrawlRunnerTest {

	private HttpServer server;
	private final int PORT = 8085;
	private final String BASE_URL = "http://localhost:" + PORT;

	@BeforeAll
	void setupServer() throws IOException {
		server = HttpServer.create(new InetSocketAddress(PORT), 0);

		// Root page with internal and external links
		server.createContext("/", exchange -> {
			String body = "<a href=\"/match\">match</a><a href=\"http://external.com\">external</a>";
			respond(exchange, body);
		});

		// Page containing the keyword
		server.createContext("/match", exchange -> {
			String body = "this page contains the KEYword";
			respond(exchange, body);
		});

		server.setExecutor(null);
		server.start();
	}

	@AfterAll
	void shutdownServer() {
		server.stop(0);
	}

	private void respond(HttpExchange exchange, String body) throws IOException {
		exchange.sendResponseHeaders(200, body.getBytes().length);
		try (OutputStream os = exchange.getResponseBody()) {
			os.write(body.getBytes());
		}
	}

	@Test
	void shouldDetectKeywordInPage() {
		Crawl job = new Crawl("test-job", "keyword");

		new CrawlRunner(BASE_URL + "/", job).run();

		assertEquals(CrawlStatus.DONE, job.getStatus());
		assertTrue(job.getMatchedUrls().stream().anyMatch(url -> url.endsWith("/match")));
		assertTrue(job.getVisitedUrls().stream().anyMatch(url -> url.endsWith("/")));
		assertTrue(job.getVisitedUrls().stream().anyMatch(url -> url.endsWith("/match")));
	}

	@Test
	void shouldSkipPagesWithoutKeyword() {
		Crawl job = new Crawl("job-no-match", "notfound");

		new CrawlRunner(BASE_URL + "/", job).run();

		assertEquals(CrawlStatus.DONE, job.getStatus());
		assertTrue(job.getMatchedUrls().isEmpty());
	}

	@Test
	void shouldIgnoreExternalLinks() {
		Crawl job = new Crawl("external-skip", "keyword");

		new CrawlRunner(BASE_URL + "/", job).run();

		Set<String> visited = job.getVisitedUrls();

		assertTrue(visited.stream().allMatch(url -> url.contains("localhost")));
		assertTrue(visited.stream().noneMatch(url -> url.contains("external.com")));
	}
}
