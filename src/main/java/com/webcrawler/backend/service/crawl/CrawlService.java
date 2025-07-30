package com.webcrawler.backend.service.crawl;

import com.webcrawler.backend.model.crawl.Crawl;
import com.webcrawler.backend.model.crawl.request.CrawlRequest;
import com.webcrawler.backend.model.crawl.response.CrawlResponse;
import com.webcrawler.backend.model.crawl.response.SimplifiedCrawlResponse;
import com.webcrawler.backend.utils.IdGenerator;
import com.webcrawler.backend.utils.exceptions.BadRequestException;
import com.webcrawler.backend.utils.exceptions.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.utils.StringUtils;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.*;

/**
 * Service class for managing web crawling operations.
 * This class handles the initiation of crawl jobs and retrieval of their results.
 */

public class CrawlService {

	private static final Logger logger = LoggerFactory.getLogger(CrawlService.class);

	private static final String BASE_URL = System.getenv("BASE_URL");
	private final Map<String, Crawl> crawlJobs = new ConcurrentHashMap<>();
	private final ExecutorService executor = new ThreadPoolExecutor(
			8,                      // core pool size
			16,                     // max pool size
			60L, TimeUnit.SECONDS, // idle thread timeout
			new LinkedBlockingQueue<>(100), // bounded queue of 100 pending jobs
			new ThreadPoolExecutor.AbortPolicy() // reject tasks beyond limit
	);

	public CrawlResponse getCrawlResults(String id) {
		if (StringUtils.isBlank(id)) {
			logger.warn("Attempted to get crawl results with a blank ID.");
			throw new BadRequestException("Crawl ID cannot be null or empty.");
		}

		Crawl job = crawlJobs.get(id);
		if (job == null) {
			logger.warn("Crawl job not found for ID: {}", id);
			throw new ResourceNotFoundException("No crawl found with id: " + id);
		}

		logger.info("Retrieved crawl results for ID: {}", id);
		return new CrawlResponse(
				job.getId(),
				job.getStatus().getDisplayName(),
				job.getMatchedUrls()
		);
	}

	public SimplifiedCrawlResponse startCrawl(CrawlRequest request) {
		validateCrawlRequest(request);
		String keyword = request.getKeyword();

		String id = IdGenerator.generateId();
		Crawl job = new Crawl(id, keyword.toLowerCase(Locale.ROOT));
		crawlJobs.put(id, job);
		logger.info("Crawl job created with ID: {} and keyword: {}", id, keyword);

		try {
			executor.submit(new CrawlRunner(BASE_URL, job));
			logger.debug("Submitted crawl job {} to executor", id);
		} catch (RejectedExecutionException e) {
			logger.error("Executor rejected crawl job {} due to system overload", id);
			crawlJobs.remove(id);
			throw new BadRequestException("Crawl job could not be started due to system overload. Please try again later.");
		}

		return new SimplifiedCrawlResponse(id);
	}

	private void validateCrawlRequest(CrawlRequest request) {
		if (request == null || StringUtils.isBlank(request.getKeyword())) {
			logger.warn("Received invalid crawl request: null or blank keyword");
			throw new BadRequestException("Crawl request cannot be null and KEYWORD cannot be empty");
		}
		if (request.getKeyword().length() < 4 || request.getKeyword().length() > 32) {
			logger.warn("Keyword length out of bounds: {}", request.getKeyword());
			throw new BadRequestException("Keyword must be between 4 and 32 characters.");
		}
	}

}
