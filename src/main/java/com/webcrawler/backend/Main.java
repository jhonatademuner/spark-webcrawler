package com.webcrawler.backend;

import com.webcrawler.backend.config.GlobalExceptionHandler;
import com.webcrawler.backend.controller.crawl.CrawlController;

import static spark.Spark.port;

public class Main {
    public static void main(String[] args) {
        port(4567); // Set the server port to 4567

        // Global exception handler for all routes
        GlobalExceptionHandler.register();

        // Initialize the CrawlController to set up routes
        new CrawlController().setupRoutes();
    }
}
