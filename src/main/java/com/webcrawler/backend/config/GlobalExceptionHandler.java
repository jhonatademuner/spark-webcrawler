package com.webcrawler.backend.config;

import com.webcrawler.backend.utils.exceptions.BadRequestException;
import com.webcrawler.backend.utils.exceptions.ErrorResponse;
import com.webcrawler.backend.utils.exceptions.ResourceNotFoundException;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Response;

import static spark.Spark.*;

/**
 * GlobalExceptionHandler is responsible for handling exceptions globally in the application.
 * It registers exception handlers for common exceptions like BadRequestException and ResourceNotFoundException.
 * It also handles any unhandled exceptions by logging them and returning a generic error response.
 */

public class GlobalExceptionHandler {

	private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
	private static final Gson gson = new Gson();

	public static void register() {
		exception(Exception.class, (ex, req, res) -> {
			logger.error("Unhandled exception", ex);
			handleException(res, 500, "Internal server error");
		});

		exception(BadRequestException.class, (ex, req, res) -> {
			logger.warn("Bad request: {}", ex.getMessage());
			handleException(res, 400, ex.getMessage());
		});

		exception(ResourceNotFoundException.class, (ex, req, res) -> {
			logger.warn("Not found: {}", ex.getMessage());
			handleException(res, 404, ex.getMessage());
		});
	}

	private static void handleException(Response res, int status, String message) {
		res.status(status);
		res.type("application/json");
		res.body(gson.toJson(new ErrorResponse(status, message)));
	}

}
