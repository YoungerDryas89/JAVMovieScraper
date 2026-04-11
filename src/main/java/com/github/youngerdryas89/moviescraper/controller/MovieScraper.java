package com.github.youngerdryas89.moviescraper.controller;

import com.github.youngerdryas89.moviescraper.controller.siteparsingprofile.DetermineMovie;
import com.github.youngerdryas89.moviescraper.controller.siteparsingprofile.SecurityPassthrough;
import com.github.youngerdryas89.moviescraper.controller.siteparsingprofile.SiteParsingProfile;
import com.github.youngerdryas89.moviescraper.controller.siteparsingprofile.specific.*;
import com.github.youngerdryas89.moviescraper.model.Movie;
import com.github.youngerdryas89.moviescraper.model.RatedResult;
import com.github.youngerdryas89.moviescraper.model.SearchResult;
import com.github.youngerdryas89.moviescraper.view.FileDetailPanel;
import com.github.youngerdryas89.moviescraper.view.GUIMain;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

public class MovieScraper {

    //Version that allows us to update the GUI while scraping
    public static Movie scrapeMovie(File movieFile, SiteParsingProfile siteToParseFrom, String urlToScrapeFromDMM, boolean useURLtoScrapeFrom, @NotNull GUIMain parent) throws IOException {

        //If the user manually canceled the results on this scraper in a dialog box, just return a null movie
        if (siteToParseFrom.getDiscardResults())
            return null;

        String searchString;
        FileDetailPanel panel = parent.getFileDetailPanel();

        if (panel.shouldOverrideInferredId() && !panel.inferredId().equals("N/A"))
            searchString = siteToParseFrom.createSearchStringFromId(panel.inferredId());
        else
            searchString = siteToParseFrom.createSearchString(movieFile);

        SearchResult[] searchResults = null;
        int searchResultNumberToUse = 0;

        //no URL was passed in so we gotta figure it ourselves
        if (!useURLtoScrapeFrom) {
            searchResults = siteToParseFrom.getSearchResults(searchString);
            int levDistanceOfCurrentMatch = 999999; // just some super high number
            String idFromMovieFile;
            if (panel.shouldOverrideInferredId() && (!panel.inferredId().isEmpty() || !panel.inferredId().equals("N/A")))
                idFromMovieFile = panel.inferredId();
            else
                idFromMovieFile = DetermineMovie.findIDTagFromFile(movieFile, siteToParseFrom.isFirstWordOfFileIsID());


            if (!panel.shouldOverrideInferredId())
                panel.setInferredId(idFromMovieFile);

            if (searchResults.length == 0) {
                // TODO: Need something better and more user friendly than just simply printing this out to the console.
                System.err.println("No title could be found with the provided Id.");
            }
            //loop through search results and see if URL happens to contain ID number in the URL. This will improve accuracy!
            if (siteToParseFrom.getScraperGroupNames().contains(ScraperGroupName.JAV_CENSORED_SCRAPER_GROUP)) {
                for (int i = 0; i < searchResults.length; i++) {
                    String urltoMatch = searchResults[i].getUrlPath().toLowerCase();
                    String idFromMovieFileToMatch = idFromMovieFile.toLowerCase().replaceAll("-", "");
//System.out.println("Comparing " + searchResults[i].toLowerCase() + " to " + idFromMovieFile.toLowerCase().replaceAll("-", ""));
                    if (urltoMatch.contains(idFromMovieFileToMatch) || (searchResults.length < 2)) {
//let's do some fuzzy logic searching to try to get the "best" match in case we got some that are pretty close
//and update the variables accordingly so we know what our best match so far is
                        int candidateLevDistanceOfCurrentMatch = StringUtils.getLevenshteinDistance(urltoMatch.toLowerCase(), idFromMovieFileToMatch);
                        if ((candidateLevDistanceOfCurrentMatch < levDistanceOfCurrentMatch)) {
                            levDistanceOfCurrentMatch = candidateLevDistanceOfCurrentMatch;
                            searchResultNumberToUse = i;
                        }
                    }
                }
            } else {
                String title = siteToParseFrom.cleanseFilename(movieFile).toLowerCase();
                var rated = Stream.of(searchResults)
                        .map(result -> {
                            var levNormalized = Similarity.calculateNormalizedLevenshteinDistance(title, result.getLabel());
                            var jaccard = Similarity.calculateJaccardIndex(title, result.getLabel());
                            var swa = (0.5 * jaccard) + ((1 - 0.5) * levNormalized);
                            return new RatedResult(result, swa);
                        })
                        .sorted(Comparator.comparingDouble(RatedResult::probability))
                        .toList();
                searchResultNumberToUse = List.of(searchResults).indexOf(rated.getFirst().result());
            }
        }
        //just use the URL to parse from the parameter
        else {
            searchResults = new SearchResult[1];

            if (siteToParseFrom instanceof DmmParsingProfile)
                searchResults[0] = new SearchResult(urlToScrapeFromDMM);
            else if (siteToParseFrom instanceof Data18MovieParsingProfile || siteToParseFrom instanceof Data18WebContentParsingProfile)
                searchResults[0] = new SearchResult(urlToScrapeFromDMM);
            else if (siteToParseFrom instanceof JavLibraryParsingProfile)
                searchResults[0] = new SearchResult(((JavLibraryParsingProfile) siteToParseFrom).getOverrideURLJavLibrary());
            else if (siteToParseFrom instanceof IAFDParsingProfile)
                searchResults[0] = new SearchResult(urlToScrapeFromDMM);

            //override any of the above if we have specifically set an override url
            if (siteToParseFrom.getOverridenSearchResult() != null) {
                searchResults[0] = siteToParseFrom.getOverridenSearchResult();
                searchResultNumberToUse = 0;
            }

        }
        if (searchResults.length > 0 && !searchResults[searchResultNumberToUse].getUrlPath().isEmpty()) {
            System.out.println("Scraping this webpage for movie: " + searchResults[searchResultNumberToUse].getUrlPath());
            //for now just set the movie to the first thing found unless we found a link which had something close to the ID
            SearchResult searchResultToUse = searchResults[searchResultNumberToUse];
            var response = siteToParseFrom.downloadDocument(searchResultToUse);
            if (response == null || response.statusCode() != 200 || response.statusCode() > 399) {
                if (response != null) {
                    System.err.println("Failed to connect to: " + searchResultToUse.getUrlPath());
                    System.err.println(response.statusCode() + " " + response.statusMessage());
                    throw new RuntimeException("Failed to connect to: " + searchResultToUse.getUrlPath() + "\n" + response.statusCode() + " " + response.statusMessage());
                } else {
                    System.err.println("Unable to connect to: " + searchResultToUse.getUrlPath() + ", perhaps internet access is cut?");
                    throw new RuntimeException("Unable to connect to: " + searchResultToUse.getUrlPath() + ", perhaps internet access is cut?");
                }
            }

            Document searchMatch = response.parse();

            //Handle any captchas etc that prevent us from getting our result
            if (SecurityPassthrough.class.isAssignableFrom(siteToParseFrom.getClass())) {
                SecurityPassthrough siteParsingProfileSecurityPassthrough = (SecurityPassthrough) siteToParseFrom;
                if (siteParsingProfileSecurityPassthrough.requiresSecurityPassthrough(searchMatch)) {
                    searchMatch = siteParsingProfileSecurityPassthrough.runSecurityPassthrough(searchMatch, searchResultToUse);
                }
            }
            siteToParseFrom.setDocument(searchMatch);
            siteToParseFrom.prepareData();
            siteToParseFrom.setOverrideURLDMM(urlToScrapeFromDMM);

            return new Movie(siteToParseFrom, parent);
        } else //no movie match found
        {
            return null;
        }
    }

}
