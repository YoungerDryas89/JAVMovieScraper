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
    public Builder toBuilder() {
        return new Builder(this);
    }

    // The static Builder class
    public static class Builder {
        private boolean writeFanartAndPosters;
        private boolean overwriteFanartAndPosters;
        private boolean downloadActorImagesToActorFolder;
        private boolean extraFanartScrapingEnabled;
        private boolean createFolderJpg;
        private boolean noMovieNameInImageFiles;
        private boolean writeTrailerToFile;
        private boolean nfoNamedMovieDotNfo;
        private boolean useIAFDForActors;
        private String sanitizerForFilename;
        private String renamerString;
        private String folderRenamerString;
        private boolean renameMovieFile;
        private boolean scrapeInJapanese;
        private boolean promptForUserProvidedURLWhenScraping;
        private boolean considerUserSelectionOneURLWhenScraping;
        private boolean isFirstWordOfFileID;
        private boolean appendIDToStartOfTitle;
        private boolean useFilenameAsTitle;
        private boolean selectArtManuallyWhenScraping;
        private boolean selectSearchResultManuallyWhenScraping;
        private boolean confirmCleanUpFileNameNameBeforeRenaming;
        private String frequentlyUsedGenres;
        private String frequentlyUsedTags;
        private boolean writeThumbTagsForPosterAndFanartToNfo;
        private String userAgent;
        private String cookieJar;

        // Constructor clones the existing record state into the builder
        public Builder(MoviescraperPreferences source) {
            this.writeFanartAndPosters = source.writeFanartAndPosters();
            this.overwriteFanartAndPosters = source.overwriteFanartAndPosters();
            this.downloadActorImagesToActorFolder = source.downloadActorImagesToActorFolder();
            this.extraFanartScrapingEnabled = source.extraFanartScrapingEnabled();
            this.createFolderJpg = source.createFolderJpg();
            this.noMovieNameInImageFiles = source.noMovieNameInImageFiles();
            this.writeTrailerToFile = source.writeTrailerToFile();
            this.nfoNamedMovieDotNfo = source.nfoNamedMovieDotNfo();
            this.useIAFDForActors = source.useIAFDForActors();
            this.sanitizerForFilename = source.sanitizerForFilename();
            this.renamerString = source.renamerString();
            this.folderRenamerString = source.folderRenamerString();
            this.renameMovieFile = source.renameMovieFile();
            this.scrapeInJapanese = source.scrapeInJapanese();
            this.promptForUserProvidedURLWhenScraping = source.promptForUserProvidedURLWhenScraping();
            this.considerUserSelectionOneURLWhenScraping = source.considerUserSelectionOneURLWhenScraping();
            this.isFirstWordOfFileID = source.isFirstWordOfFileID();
            this.appendIDToStartOfTitle = source.appendIDToStartOfTitle();
            this.useFilenameAsTitle = source.useFilenameAsTitle();
            this.selectArtManuallyWhenScraping = source.selectArtManuallyWhenScraping();
            this.selectSearchResultManuallyWhenScraping = source.selectSearchResultManuallyWhenScraping();
            this.confirmCleanUpFileNameNameBeforeRenaming = source.confirmCleanUpFileNameNameBeforeRenaming();
            this.frequentlyUsedGenres = source.frequentlyUsedGenres();
            this.frequentlyUsedTags = source.frequentlyUsedTags();
            this.writeThumbTagsForPosterAndFanartToNfo = source.writeThumbTagsForPosterAndFanartToNfo();
            this.userAgent = source.userAgent();
            this.cookieJar = source.cookieJar();
        }

        // Fluent setter methods for every field
        public Builder writeFanartAndPosters(boolean val) { this.writeFanartAndPosters = val; return this; }
        public Builder overwriteFanartAndPosters(boolean val) { this.overwriteFanartAndPosters = val; return this; }
        public Builder downloadActorImagesToActorFolder(boolean val) { this.downloadActorImagesToActorFolder = val; return this; }
        public Builder extraFanartScrapingEnabled(boolean val) { this.extraFanartScrapingEnabled = val; return this; }
        public Builder createFolderJpg(boolean val) { this.createFolderJpg = val; return this; }
        public Builder noMovieNameInImageFiles(boolean val) { this.noMovieNameInImageFiles = val; return this; }
        public Builder writeTrailerToFile(boolean val) { this.writeTrailerToFile = val; return this; }
        public Builder nfoNamedMovieDotNfo(boolean val) { this.nfoNamedMovieDotNfo = val; return this; }
        public Builder useIAFDForActors(boolean val) { this.useIAFDForActors = val; return this; }
        public Builder sanitizerForFilename(String val) { this.sanitizerForFilename = val; return this; }
        public Builder renamerString(String val) { this.renamerString = val; return this; }
        public Builder folderRenamerString(String val) { this.folderRenamerString = val; return this; }
        public Builder renameMovieFile(boolean val) { this.renameMovieFile = val; return this; }
        public Builder scrapeInJapanese(boolean val) { this.scrapeInJapanese = val; return this; }
        public Builder promptForUserProvidedURLWhenScraping(boolean val) { this.promptForUserProvidedURLWhenScraping = val; return this; }
        public Builder considerUserSelectionOneURLWhenScraping(boolean val) { this.considerUserSelectionOneURLWhenScraping = val; return this; }
        public Builder isFirstWordOfFileID(boolean val) { this.isFirstWordOfFileID = val; return this; }
        public Builder appendIDToStartOfTitle(boolean val) { this.appendIDToStartOfTitle = val; return this; }
        public Builder useFilenameAsTitle(boolean val) { this.useFilenameAsTitle = val; return this; }
        public Builder selectArtManuallyWhenScraping(boolean val) { this.selectArtManuallyWhenScraping = val; return this; }
        public Builder selectSearchResultManuallyWhenScraping(boolean val) { this.selectSearchResultManuallyWhenScraping = val; return this; }
        public Builder confirmCleanUpFileNameNameBeforeRenaming(boolean val) { this.confirmCleanUpFileNameNameBeforeRenaming = val; return this; }
        public Builder frequentlyUsedGenres(String val) { this.frequentlyUsedGenres = val; return this; }
        public Builder frequentlyUsedTags(String val) { this.frequentlyUsedTags = val; return this; }
        public Builder writeThumbTagsForPosterAndFanartToNfo(boolean val) { this.writeThumbTagsForPosterAndFanartToNfo = val; return this; }
        public Builder userAgent(String val) { this.userAgent = val; return this; }
        public Builder cookieJar(String val) { this.cookieJar = val; return this; }

        // Compile it all back into a fresh, thread-safe Record
        public MoviescraperPreferences build() {
            return new MoviescraperPreferences(
                    writeFanartAndPosters, overwriteFanartAndPosters, downloadActorImagesToActorFolder,
                    extraFanartScrapingEnabled, createFolderJpg, noMovieNameInImageFiles,
                    writeTrailerToFile, nfoNamedMovieDotNfo, useIAFDForActors,
                    sanitizerForFilename, renamerString, folderRenamerString,
                    renameMovieFile, scrapeInJapanese, promptForUserProvidedURLWhenScraping,
                    considerUserSelectionOneURLWhenScraping, isFirstWordOfFileID, appendIDToStartOfTitle,
                    useFilenameAsTitle, selectArtManuallyWhenScraping, selectSearchResultManuallyWhenScraping,
                    confirmCleanUpFileNameNameBeforeRenaming, frequentlyUsedGenres, frequentlyUsedTags,
                    writeThumbTagsForPosterAndFanartToNfo, userAgent, cookieJar
            );
        }
    }
}