package com.github.youngerdryas89.moviescraper.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
/*
 * Any group of SiteParsingProfiles which return the same type of information for a given file and which
 * will be compatible for amalgamation should return the same ScraperGroupName by implementing getScraperGroupName()
 */
public enum ScraperGroupName {

    @JsonProperty("Japanese")
    JAV_CENSORED_SCRAPER_GROUP {
        @Override
        public String toString() {
            return "JAV Censored Group";
        }
    },

    @JsonProperty("American")
    AMERICAN_ADULT_DVD_SCRAPER_GROUP {
        @Override
        public String toString() {
            return "American Adult DVD";
        }
    },

    @JsonProperty("Default")
    DEFAULT_SCRAPER_GROUP {
        @Override
        public String toString() {
            return "Default Group";
        }
    }
}
