package com.github.youngerdryas89.moviescraper.model;

import java.util.Objects;

public record RatedResult(SearchResult result, double probability) {
    public RatedResult {
        Objects.requireNonNull(result);
    }
}
