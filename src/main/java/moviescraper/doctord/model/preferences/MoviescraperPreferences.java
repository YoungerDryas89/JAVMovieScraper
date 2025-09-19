package moviescraper.doctord.model.preferences;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import moviescraper.doctord.scraper.UserAgent;
import moviescraper.doctord.view.FavoriteGenrePickerPanel;
import org.apache.commons.lang3.SystemUtils;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class MoviescraperPreferences {

	private static MoviescraperPreferences INSTANCE;
    public boolean writeFanartAndPosters = true; //fanart and poster files will be downloaded and then written to disk when writing the movie's metadata.
    public boolean overwriteFanartAndPosters = true; //overwrites existing fanart and poster files when writing the metadata to disk
    public boolean downloadActorImagesToActorFolder = true; //creates .actor thumbnail files when writing the metadata
    public boolean extraFanartScrapingEnabled = true; //will attempt to scrape and write extrafanart
    public boolean createFolderJpg = false; //Folder.jpg will be created when writing the file. This is a copy of the movie's poster file. Used in windows to show a thumbnail of the folder in Windows Explorer.
    public boolean noMovieNameInImageFiles = false;  //fanart and poster will be called fanart.jpg and poster.jpg instead of also containing with the movie's name within the file
    public boolean writeTrailerToFile = true; //Download the trailer file from the internet and write it to a file when writing the rest of the metadata.
    public boolean nfoNamedMovieDotNfo = false; //.nfo file written out will always be called "movie.nfo"
    public boolean useIAFDForActors = false; //No longer used. Replaced by Amalgamation settings.
    public static String sanitizerForFilename = "[\\\\/:*?\"<>|\\r\\n]|[ ]+$|(?<=[^.])[.]+$|(?<=.{250})(.+)(?=[.]\\p{Alnum}{3}$)"; //Used to help remove illegal characters when renaming the file. For the most part; the user does not need to change this.
    public static String renamerString = "<TITLE> [<ACTORS>] (<YEAR>) [<ID>]"; //Renamer string set in the renamer configuration gui to apply a renamer rule to the file's name
    public static String folderRenamerString = "<BASEDIRECTORY><PATHSEPERATOR>"; // Renamer string set in the renamer configuration gui to apply a renamer rule to the file's folder name
    public boolean renameMovieFile = false; //File will be renamed according to renamer rules when writing the movie file's metadata out to disk.
    public boolean scrapeInJapanese = false; //For sites that support it; downloaded info will be in Japanese instead of English
    public boolean promptForUserProvidedURLWhenScraping = false; //Prompt user to manually provide their own url when scraping a file. Useful if search just can't find a file; but the user knows what to use anyways. Not intended to be left on all the time.
    public boolean considerUserSelectionOneURLWhenScraping = false; //Consider all selected items to be one 'movie'.  To keep from being prompted for each CD/Scene
    public boolean isFirstWordOfFileID = false; //Usually the scraper expects the last word of the file to be the ID. This option if enabled will instead look at the first word.
    public boolean appendIDToStartOfTitle = false; //Scraped ID will be put as the first word of the title if enabled. Useful for people who like to keep releases from the same company alphabetically together.
    public boolean useFilenameAsTitle = false; //Filename will be writen to the title field of the nfo file instead of using the scraped result
    public boolean selectArtManuallyWhenScraping = true; //Confirmation dialog to allow user to select art will be shown. If false; art is still picked; but it will be automatically chosen.
    public boolean selectSearchResultManuallyWhenScraping = false; //Confirmation dialog to allow user to pick which search result they want to use will be shown.
    public boolean confirmCleanUpFileNameNameBeforeRenaming = true; // Show a dialog asking the user to confirm the rename of a file each time using the File Name Cleanup feature
    public String frequentlyUsedGenres = "Adult" + FavoriteGenrePickerPanel.listSeperator + "JAV"; //Used in genre editing to store user's list of frequently used genres to aid in quickly adding genres to a movie
    public String frequentlyUsedTags = "Feature"; //Used in tag editing to store user's list of frequently used tags to aid in quickly adding tags to a movie
    public boolean writeThumbTagsForPosterAndFanartToNfo = true; //Whether to write the <thumb> tag into the nfo;
    public String userAgent = UserAgent.getRandomUserAgent(); //UserAgent to use
    public String cookieJar = null; //UserAgent to use

    @JsonIgnore
    static Path configFile = Path.of("preferences.json");

	private MoviescraperPreferences() {
	}

	public static synchronized MoviescraperPreferences getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new MoviescraperPreferences();
			INSTANCE.setSanitizerForFilename(getSanitizerForFilename());
			INSTANCE.setRenamerString(getRenamerString());

            if(!Files.exists(configFile)){
                savePreferences();
            }
		}
		return INSTANCE;
	}

	public void setOverWriteFanartAndPostersPreference(Boolean preferenceValue) {
        overwriteFanartAndPosters = preferenceValue;
        savePreferences();
	}

	public Boolean getOverWriteFanartAndPostersPreference() {
        return overwriteFanartAndPosters;
	}

	public void setWriteFanartAndPostersPreference(Boolean preferenceValue) {
        writeFanartAndPosters = preferenceValue;
        savePreferences();
	}

	public void setDownloadActorImagesToActorFolderPreference(Boolean preferenceValue) {
        downloadActorImagesToActorFolder = preferenceValue;
        savePreferences();
	}

	public Boolean getDownloadActorImagesToActorFolderPreference() {
        return downloadActorImagesToActorFolder;
	}

	public Boolean getWriteFanartAndPostersPreference() {
        return writeFanartAndPosters;
	}

	public Boolean getExtraFanartScrapingEnabledPreference() {
        return extraFanartScrapingEnabled;
	}

	public void setExtraFanartScrapingEnabledPreference(Boolean preferenceValue) {
        extraFanartScrapingEnabled = preferenceValue;
        savePreferences();
	}

	public void setCreateFolderJpgEnabledPreference(Boolean preferenceValue) {
        createFolderJpg = preferenceValue;
        savePreferences();
	}

	public Boolean getCreateFolderJpgEnabledPreference() {
        return createFolderJpg;
	}

	public Boolean getNoMovieNameInImageFiles() {
        return noMovieNameInImageFiles;
	}

	public void setNoMovieNameInImageFiles(Boolean preferenceValue) {
        noMovieNameInImageFiles = preferenceValue;
        savePreferences();
	}

	public Boolean getWriteTrailerToFile() {
        return writeTrailerToFile;
	}

	public void setWriteTrailerToFile(Boolean preferenceValue) {
        writeTrailerToFile = preferenceValue;
        savePreferences();
	}

	public Boolean getNfoNamedMovieDotNfo() {
        return nfoNamedMovieDotNfo;
	}

	public void setNfoNamedMovieDotNfo(Boolean preferenceValue) {
        nfoNamedMovieDotNfo = preferenceValue;
        savePreferences();
	}

	public Boolean getUseIAFDForActors() {
        return useIAFDForActors;
	}

	public void setUseIAFDForActors(Boolean preferenceValue) {
        useIAFDForActors = preferenceValue;
        savePreferences();
	}

	public static String getSanitizerForFilename() {
        return sanitizerForFilename;
	}

	public void setSanitizerForFilename(String preferenceValue) {
        sanitizerForFilename = preferenceValue;
        savePreferences();
	}

	public static String getRenamerString() {
        return renamerString;
	}

	public void setRenamerString(String preferenceValue) {
        renamerString = preferenceValue;
        savePreferences();
	}

	public static String getFolderRenamerString() {
        return folderRenamerString;
	}

	public void setFolderRenamerString(String preferenceValue) {
        folderRenamerString = preferenceValue;
        savePreferences();
	}

	public Boolean getRenameMovieFile() {
        return renameMovieFile;
	}

	public void setRenameMovieFile(Boolean preferenceValue) {
        renameMovieFile = preferenceValue;
        savePreferences();
	}

	public Boolean getScrapeInJapanese() {
        return scrapeInJapanese;
	}

	public void setScrapeInJapanese(Boolean preferenceValue) {
        scrapeInJapanese = preferenceValue;
        savePreferences();
	}

	public Boolean getPromptForUserProvidedURLWhenScraping() {
        return promptForUserProvidedURLWhenScraping;
	}

	public void setPromptForUserProvidedURLWhenScraping(Boolean preferenceValue) {
        promptForUserProvidedURLWhenScraping = preferenceValue;
        savePreferences();
	}

	public Boolean getConsiderUserSelectionOneURLWhenScraping() {
        return considerUserSelectionOneURLWhenScraping;
	}

	public void setConsiderUserSelectionOneURLWhenScraping(Boolean preferenceValue) {
        considerUserSelectionOneURLWhenScraping = preferenceValue;
        savePreferences();
	}

	public Boolean getIsFirstWordOfFileID() {
        return isFirstWordOfFileID;
	}

	public void setIsFirstWordOfFileID(Boolean preferenceValue) {
        isFirstWordOfFileID = preferenceValue;
        savePreferences();
	}

	public Boolean getAppendIDToStartOfTitle() {
        return appendIDToStartOfTitle;
	}

	public void setAppendIDToStartOfTitle(Boolean preferenceValue) {
        appendIDToStartOfTitle = preferenceValue;
        savePreferences();
	}

	public Boolean getUseFileNameAsTitle() {
        return useFilenameAsTitle;
	}

	public void setUseFileNameAsTitle(Boolean preferenceValue) {
        useFilenameAsTitle = preferenceValue;
        savePreferences();
	}

	public Boolean getSelectArtManuallyWhenScraping() {
        return selectArtManuallyWhenScraping;
	}

	public void setSelectArtManuallyWhenScraping(Boolean preferenceValue) {
        selectArtManuallyWhenScraping = preferenceValue;
        savePreferences();
	}

	public Boolean getSelectSearchResultManuallyWhenScraping() {
        return selectSearchResultManuallyWhenScraping;
	}

	public void setSelectSearchResultManuallyWhenScraping(Boolean preferenceValue) {
        selectArtManuallyWhenScraping = preferenceValue;
        savePreferences();
	}

	public Boolean getConfirmCleanUpFileNameNameBeforeRenaming() {
        return confirmCleanUpFileNameNameBeforeRenaming;
	}

	public void setConfirmCleanUpFileNameNameBeforeRenaming(Boolean preferenceValue) {
        confirmCleanUpFileNameNameBeforeRenaming = preferenceValue;
        savePreferences();
	}

	public String getfrequentlyUsedGenres() {
        return frequentlyUsedGenres;
	}

	public void setFrequentlyUsedGenres(String value) {
        frequentlyUsedGenres = value;
        savePreferences();
	}

	public String getfrequentlyUsedTags() {
        return frequentlyUsedTags;
	}

	public void setFrequentlyUsedTags(String value) {
        frequentlyUsedTags = value;
	}

	public Boolean getWriteThumbTagsForPosterAndFanartToNfo() {
        return writeThumbTagsForPosterAndFanartToNfo;
	}

	public void setWriteThumbTagsForPosterAndFanartToNfo(Boolean preferenceValue) {
        writeThumbTagsForPosterAndFanartToNfo = preferenceValue;
        savePreferences();
	}

	public String getUserAgent() {
        return userAgent;
	}

	public void setUserAgent(String preferenceValue) {
        userAgent = preferenceValue;
        savePreferences();
	}

	public String getCookieJar() {
        return cookieJar;
	}

	public void setCookieJar(String preferenceValue) {
        cookieJar = preferenceValue;
        savePreferences();
	}

    @Nullable
    public static MoviescraperPreferences loadPreferencesOrInitialize(){

        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);

        SimpleModule module = new SimpleModule();
        mapper.registerModule(module);
        if(!Files.exists(configFile)) {
            try (FileInputStream fis = new FileInputStream(configFile.toFile())) {
                return mapper.readValue(fis, MoviescraperPreferences.class);
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }
        return getInstance();
    }

    public static void savePreferences() {
        var instance = getInstance();

        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);

        SimpleModule module = new SimpleModule();
        mapper.registerModule(module);

        try(FileOutputStream osw = new FileOutputStream("preferences.json")) {
            var data = mapper.writeValueAsBytes(instance);
            osw.write(data);
        }catch (IOException e){
            System.err.println(e.getMessage());
        }

    }
}

