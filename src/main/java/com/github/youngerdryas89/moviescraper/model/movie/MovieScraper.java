package com.github.youngerdryas89.moviescraper.model.movie;

import com.github.youngerdryas89.moviescraper.controller.siteparsingprofile.SiteParsingProfile;
import com.github.youngerdryas89.moviescraper.model.dataitem.DefaultDataItemSource;
import com.github.youngerdryas89.moviescraper.model.dataitem.Title;
import com.github.youngerdryas89.moviescraper.view.GUIMain;

public class MovieScraper {

    public static Movie scrape(SiteParsingProfile siteToScrapeFrom, GUIMain parent){

        var prefs = parent.getPreferences();

        var movie = new Movie();

        // None of the returns values must be null
        var movieTitle = siteToScrapeFrom.scrapeTitle();
        var ogmovieTitle = siteToScrapeFrom.scrapeOriginalTitle();
        var movieId = siteToScrapeFrom.scrapeID();

        MovieUtils.cleanseTitle(movieTitle, movieId);

        if(prefs.appendIDToStartOfTitle())
            MovieUtils.appendIDToStartOfTitle(movieTitle, movieId);

        MovieUtils.removeIdFromOriginalTitle(ogmovieTitle, movieId);

        movie.setTitle(movieTitle);
        movie.setOriginalTitle(ogmovieTitle);
        movie.setId(movieId);

        movie.setSortTitle(siteToScrapeFrom.scrapeSortTitle());
        movie.setSet(siteToScrapeFrom.scrapeSet());
        movie.setRating(siteToScrapeFrom.scrapeRating());
        movie.setYear(siteToScrapeFrom.scrapeYear());
        movie.setTop250(siteToScrapeFrom.scrapeTop250());
        movie.setTrailer(siteToScrapeFrom.scrapeTrailer());
        movie.setVotes(siteToScrapeFrom.scrapeVotes());
        movie.setOutline(siteToScrapeFrom.scrapeOutline());
        movie.setPlot(siteToScrapeFrom.scrapePlot());
        movie.setTagline(siteToScrapeFrom.scrapeTagline());
        movie.setStudio(siteToScrapeFrom.scrapeStudio());
        movie.setReleaseDate(siteToScrapeFrom.scrapeReleaseDate());
        movie.setRuntime(siteToScrapeFrom.scrapeRuntime());

        var posters = siteToScrapeFrom.scrapePosters(parent.getFileDetailPanel().cropPosters());
        if(!posters.isEmpty() && posters.getFirst().hasDerivations())
            posters.set(0, posters.getFirst().derivedChild());
        movie.setPosters(posters);

        movie.setFanart(siteToScrapeFrom.scrapeFanart());
        movie.setExtraFanart(siteToScrapeFrom.scrapeExtraFanart());
        movie.setMpaa(siteToScrapeFrom.scrapeMPAA());
        movie.setActors(siteToScrapeFrom.scrapeActors());
        movie.setGenres(siteToScrapeFrom.scrapeGenres());
        movie.setTags(siteToScrapeFrom.scrapeTags());
        movie.setDirectors(siteToScrapeFrom.scrapeDirectors());

        movie.setAllDataItemSources(siteToScrapeFrom);

        String fileNameOfScrapedMovie = siteToScrapeFrom.getFileNameOfScrapedMovie();
        if (fileNameOfScrapedMovie != null && !fileNameOfScrapedMovie.trim().isEmpty()) {
            movie.setFileName(fileNameOfScrapedMovie);
        }

        if (prefs.useFilenameAsTitle() && fileNameOfScrapedMovie != null && !fileNameOfScrapedMovie.isEmpty()) {
            var title = new Title(fileNameOfScrapedMovie);
            title.setDataItemSource(new DefaultDataItemSource());
            movie.setTitle(title);
        }

        return movie;
    }
}
