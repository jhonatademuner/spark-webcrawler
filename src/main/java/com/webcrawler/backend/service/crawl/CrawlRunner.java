package com.webcrawler.backend.service.crawl;

import com.webcrawler.backend.model.crawl.Crawl;
import com.webcrawler.backend.model.crawl.CrawlStatus;
import com.webcrawler.backend.utils.LinkExtractor;
import com.webcrawler.backend.utils.PageFetcher;
import com.webcrawler.backend.utils.exceptions.NonSuccessfulResponseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.time.Instant;
import java.util.Locale;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The CrawlRunner class implements Runnable to perform web crawling operations.
 * It processes URLs, extracts links, and searches for a specified keyword.
 * The class uses multiple worker threads to handle concurrent crawling tasks.
 */

public class CrawlRunner implements Runnable {

	private static final Logger logger = LoggerFactory.getLogger(CrawlRunner.class);
	private static final int WORKER_COUNT = 4;

	private final String baseUrl;
	private final Crawl job;
	private final String normalizedKeyword;

	private final PageFetcher pageFetcher = new PageFetcher();
	private final LinkExtractor linkExtractor = new LinkExtractor();

	public CrawlRunner(String baseUrl, Crawl job) {
		this.baseUrl = baseUrl;
		this.job = job;
		this.normalizedKeyword = job.getKeyword().toLowerCase(Locale.ROOT);
	}

	@Override
	public void run() {
		BlockingDeque<String> toVisit = new LinkedBlockingDeque<>();
		toVisit.add(baseUrl);

		ExecutorService workers = Executors.newFixedThreadPool(WORKER_COUNT);
		AtomicInteger activeWorkers = new AtomicInteger(0);
		CountDownLatch latch = new CountDownLatch(WORKER_COUNT);

		try {
			String baseHost = new URL(baseUrl).getHost();
			logger.info("[RunningCrawl] jobId={} | Starting on host={}", job.getId(), baseHost);

			Runnable worker = () -> {
				try {
					while (true) {
						String currentUrl = toVisit.poll();
						if (currentUrl == null) {
							if (activeWorkers.get() == 0 && toVisit.isEmpty()) {
								break; // no work left to do
							}
							continue;
						}

						activeWorkers.incrementAndGet();

						if (!job.getVisitedUrls().add(currentUrl)) {
							log(job.getId(), "Skipping already visited URL", currentUrl);
							activeWorkers.decrementAndGet();
							continue;
						}

						log(job.getId(), "Processing URL", currentUrl);

						try {
							URL url = new URL(currentUrl);
							if (!url.getHost().equals(baseHost)) {
								log(job.getId(), "Skipping external URL", currentUrl);
								continue;
							}

							String content;
							try{
								content = pageFetcher.fetch(url);
							} catch (NonSuccessfulResponseException ex){
								log(job.getId(), ex.getLocalizedMessage(), currentUrl);
								continue;
							}

							if (content.contains(normalizedKeyword)) {
								job.addMatchedUrl(currentUrl);
								log(job.getId(), "Found keyword", currentUrl);
								if(job.getMatchedUrls().size() == 100) {
									logger.info("[RunningCrawl] jobId={} | Reached 100 matched URLs in {} ms", job.getId(), Duration.between(job.getCreatedAt(), Instant.now()).toMillis());
								}
							}

							toVisit.addAll(linkExtractor.extractLinks(url, content));

						} catch (Exception e) {
							log(job.getId(), "Error processing URL", currentUrl);
						} finally {
							activeWorkers.decrementAndGet();
						}
					}
				} finally {
					latch.countDown();
				}
			};

			for (int i = 0; i < WORKER_COUNT; i++) {
				workers.submit(worker);
			}

			boolean completed = latch.await(3, TimeUnit.MINUTES);
			if (!completed) {
				logger.warn("[RunningCrawl] jobId={} | Timeout waiting for workers", job.getId());
			}
		} catch (MalformedURLException e) {	logger.error("[RunningCrawl] jobId={} | Malformed base URL: {}", job.getId(), baseUrl, e);
		} catch (Exception e) { logger.error("[RunningCrawl] jobId={} | General error during crawl", job.getId(), e);
		} finally {
			workers.shutdownNow();
			try {
				if (!workers.awaitTermination(5, TimeUnit.SECONDS)) {
					logger.warn("[RunningCrawl] jobId={} | Workers did not terminate cleanly", job.getId());
				}
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				logger.warn("[RunningCrawl] jobId={} | Interrupted while shutting down workers", job.getId());
			}
			job.setStatus(CrawlStatus.DONE);
			logger.info("[RunningCrawl] jobId={} | Crawl finished in {} ms", job.getId(), Duration.between(job.getCreatedAt(), Instant.now()).toMillis());
		}
	}

	private void log(String jobId, String message, String url) {
		if (logger.isInfoEnabled()) {
			logger.info("[RunningCrawl] jobId={} | {}: {}", jobId, message, url);
		}
	}
}
