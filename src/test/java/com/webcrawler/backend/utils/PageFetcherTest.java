package com.webcrawler.backend.utils;

import com.webcrawler.backend.utils.exceptions.NonSuccessfulResponseException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.sun.net.httpserver.HttpServer;

import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;

class PageFetcherTest {

	private static HttpServer server;
	private static final int PORT = 8089;

	@BeforeAll
	static void startServer() throws Exception {
		server = HttpServer.create(new InetSocketAddress(PORT), 0);
		server.createContext("/success", exchange -> {
			String response = "<html>Page with Keyword</html>";
			exchange.sendResponseHeaders(200, response.length());
			try (OutputStream os = exchange.getResponseBody()) {
				os.write(response.getBytes());
			}
		});
		server.createContext("/error", exchange -> {
			exchange.sendResponseHeaders(404, -1);
		});
		server.start();
	}

	@AfterAll
	static void stopServer() {
		server.stop(0);
	}

	@Test
	void fetch_successfulResponse_returnsContent() throws Exception {
		URL url = new URL("http://localhost:" + PORT + "/success");
		PageFetcher fetcher = new PageFetcher();

		String result = fetcher.fetch(url);

		assertTrue(result.contains("keyword")); // lowercase from .toLowerCase
	}

	@Test
	void fetch_nonSuccessfulResponse_throwsException() {
		URL url;
		try {
			url = new URL("http://localhost:" + PORT + "/error");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		PageFetcher fetcher = new PageFetcher();

		Exception exception = assertThrows(NonSuccessfulResponseException.class, () -> {
			fetcher.fetch(url);
		});

		assertTrue(exception.getMessage().contains("Non-200"));
	}
}
