package com.webcrawler.backend.utils;

import com.webcrawler.backend.utils.exceptions.NonSuccessfulResponseException;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;
import java.util.Scanner;

/**
 * The PageFetcher class is responsible for fetching the content of a web page.
 * It opens a connection to the specified URL, sets appropriate request properties,
 * and reads the response. If the response code is not 200 (OK), it throws an exception.
 */

public class PageFetcher {

	public String fetch(URL parsedUrl) throws Exception {
		HttpURLConnection connection = (HttpURLConnection) parsedUrl.openConnection();
		connection.setRequestProperty("User-Agent", "Mozilla/5.0");
		connection.setConnectTimeout(3000);
		connection.setReadTimeout(3000);

		if (connection.getResponseCode() != 200) {
			throw new NonSuccessfulResponseException("Non-200 response [" + connection.getResponseCode() + "]");
		}

		try (
				InputStream stream = connection.getInputStream();
				Scanner scanner = new Scanner(stream).useDelimiter("\\A")
		) {
			return scanner.hasNext() ? scanner.next().toLowerCase(Locale.ROOT) : "";
		}
	}
}