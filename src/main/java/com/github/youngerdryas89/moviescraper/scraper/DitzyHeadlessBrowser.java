package com.github.youngerdryas89.moviescraper.scraper;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;

import io.vavr.control.Either;
import net.covers1624.curl4j.CURL;
import net.covers1624.curl4j.httpapi.Curl4jEngineRequest;
import net.covers1624.curl4j.httpapi.Curl4jHttpEngine;
import net.covers1624.quack.net.httpapi.EngineResponse;
import org.jsoup.Connection;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;

import java.util.logging.Logger;
import com.github.youngerdryas89.moviescraper.model.preferences.MoviescraperPreferences;

public class DitzyHeadlessBrowser {

	private String userAgent;
	private DitzyCookies cookies;
	private final int timeout;
	private static final Logger LOGGER = Logger.getLogger(DitzyHeadlessBrowser.class.getName());
	private final CurlDependencyManager curlManager = new CurlDependencyManager();
	private final MoviescraperPreferences preferences = MoviescraperPreferences.getInstance();


	private ExecutorService executor = Executors.newSingleThreadExecutor();

	public DitzyHeadlessBrowser(String userAgent, int timeout){
		this.userAgent = userAgent;
		this.timeout = timeout;
		this.cookies = new DitzyCookies();

		CompletableFuture<Either<CurlMError, Path>> curlManagerResults = CompletableFuture.supplyAsync(
				() -> {
					try {
						var curlResult = curlManager.get();
						if (curlResult.isRight()) {
							System.out.println("Using libcurl-impersonate v" + curlManager.Version());
						} else {
							LOGGER.log(Level.WARNING, "Error on getting curl-impersonate");
							switch (curlResult.left().get()){
								case CurlMError.CurlDependencyManagerError(String message): {
									LOGGER.log(Level.WARNING, message);
									break;
								}

								case CurlMError.HTTPError(String message, int statusCode): {
									LOGGER.log(Level.WARNING, "HTTP Status: " + statusCode + " " + message);
								}
								break;
								default:
									throw new IllegalStateException("Unexpected value: " + curlResult.left().get());
							}
						}
					} catch (Exception e) {
						LOGGER.log(Level.WARNING, "Error: Interrupted or Cancelled while attempting to get libcurl-impersonate!");
						LOGGER.log(Level.WARNING, "Exception: " + e.getMessage());
					}
                    return null;
                },
				executor
		);
		LOGGER.log(Level.INFO, "Build browser with U: {0}", this.userAgent);
	}

	public DitzyHeadlessBrowser() {
		this(UserAgent.getRandomUserAgent(), 10000);
	}

	public void configure() throws IOException {
		MoviescraperPreferences preferences = MoviescraperPreferences.getInstance();
		setUserAgent(preferences.getUserAgent());
		if (preferences.getCookieJar() != null) {
			Cookies().LoadCookieJar(new File(preferences.getCookieJar()));
		}
	}

	@Override
	public String toString() {
		StringBuilder string = new StringBuilder();
		string.append(this.userAgent);
		string.append(", timeout: ");
		string.append(this.timeout);
		if (this.cookies != null) {
			string.append(", cookies: ");
			string.append(this.cookies);
		}
		return string.toString();
	}

	protected Connection connect(URL url, boolean followRedirect) throws IOException {
		Connection connection = Jsoup.connect(url.toString()).userAgent(userAgent).ignoreHttpErrors(true).timeout(timeout).followRedirects(followRedirect).method(Connection.Method.GET);

		connection = connection.cookies(this.cookies.getCookies(url)).method(Connection.Method.POST);

		return connection;
	}

	/**
	 * get a document from an URL
	 *
	 * @param url URL to get
	 * @return The server response
	 * @throws IOException Cannot parse the document
	 */
	public EngineResponse get(URL url) throws IOException {
		LOGGER.log(Level.INFO, "Get request on {0}", url.toString());
		var request = curlManager.getEngine().newRequest()
				.method("GET", null)
				.url(url);
		return request.execute();
	}

	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}

	public DitzyCookies Cookies() {
		return this.cookies;
	}

}
