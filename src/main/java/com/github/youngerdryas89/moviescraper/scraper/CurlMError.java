package com.github.youngerdryas89.moviescraper.scraper;

public interface CurlMError {
    record CurlDependencyManagerError(String message) implements CurlMError{}
    record HTTPError(String message, int status_code) implements CurlMError{}
}