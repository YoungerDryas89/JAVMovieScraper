package com.github.youngerdryas89.moviescraper.model.preferences;
import com.github.youngerdryas89.moviescraper.scraper.UserAgent;
import com.github.youngerdryas89.moviescraper.view.FavoriteGenrePickerPanel;

public record MoviescraperPreferences(
        boolean writeFanartAndPosters,
        boolean overwriteFanartAndPosters,
        boolean downloadActorImagesToActorFolder,
        boolean extraFanartScrapingEnabled,
        boolean createFolderJpg,
        boolean noMovieNameInImageFiles,
        boolean writeTrailerToFile,
        boolean nfoNamedMovieDotNfo,
        boolean useIAFDForActors,
        String sanitizerForFilename,
        String renamerString,
        String folderRenamerString,
        boolean renameMovieFile,
        boolean scrapeInJapanese,
        boolean promptForUserProvidedURLWhenScraping,
        boolean considerUserSelectionOneURLWhenScraping,
        boolean isFirstWordOfFileID,
        boolean appendIDToStartOfTitle,
        boolean useFilenameAsTitle,
        boolean selectArtManuallyWhenScraping,
        boolean selectSearchResultManuallyWhenScraping,
        boolean confirmCleanUpFileNameNameBeforeRenaming,
        String frequentlyUsedGenres,
        String frequentlyUsedTags,
        boolean writeThumbTagsForPosterAndFanartToNfo,
        String userAgent,
        String cookieJar
) {
    public MoviescraperPreferences() {
        this(
                true,
                true,
                true,
                true,
                false,
                false,
                true,
                false,
                false,
                "[\\/:*?\"<>|\\r\\n]|[ ]+$|(?<=[^.])[.]+$|(?<=.{250})(.+)(?=[.]\\p{Alnum}{3}$)",
                "<TITLE> [<ACTORS>] (<YEAR>) [<ID>]",
                "<BASEDIRECTORY><PATHSEPERATOR>",
                false,
                false,
                false,
                false,
                false,
                false,
                false,
                true,
                false,
                true,
                "Adult" + FavoriteGenrePickerPanel.listSeperator + "JAV",
                "Feature",
                true,
                UserAgent.getRandomUserAgent(),
                null
        );
    }
}