package moviescraper.doctord.model.preferences;

import java.io.File;

import moviescraper.doctord.controller.DirectorySort;
import org.apache.commons.lang3.SystemUtils;

public class GuiSettings extends Settings {

	protected final static GuiSettings INSTANCE = new GuiSettings();

	enum Key implements Settings.Key {
		lastUsedDirectory, lastUsedScraper, showOutputPanel, showToolbar, useContentBasedTypeIcons, pathToExternalMediaPlayer, fileListDividerLocation, width, height,
		HideImages,
		HideNFOFiles,
		Ordering,
		Sort
		;

		@Override
		public String getKey() {
			// prefix setting key to avoid clashing
			return "Gui:" + toString();
		}
	}

	private GuiSettings() {
		//prevent people from using this
	}

	public static synchronized GuiSettings getInstance() {
		return INSTANCE;
	}

	public boolean getShowToolbar() {
		return getBooleanValue(Key.showToolbar, Boolean.TRUE).booleanValue();
	}

	public void setShowToolbar(boolean preferenceValue) {
		setBooleanValue(Key.showToolbar, Boolean.valueOf(preferenceValue));
	}

	public boolean getShowOutputPanel() {
		return getBooleanValue(Key.showOutputPanel, false);
	}

	public void setShowOutputPanel(boolean preferenceValue) {
		setBooleanValue(Key.showOutputPanel, preferenceValue);
	}

	public File getLastUsedDirectory() {
		String lastUsedDir = getStringValue(Key.lastUsedDirectory, null);

		if (lastUsedDir != null) {
			File lastUsedDirFile = new File(lastUsedDir);
			if (lastUsedDirFile.exists())
				return lastUsedDirFile;
			else
				return new File(System.getProperty("user.home"));
		} else
			return new File(System.getProperty("user.home"));
	}

	public void setLastUsedDirectory(File lastUsedDirectoryFile) {
		setStringValue(Key.lastUsedDirectory, lastUsedDirectoryFile.getPath());
	}

	public boolean getUseContentBasedTypeIcons() {
		/*
		 * Use icons in res/mime instead of system icons.
		 * Needed for linux as system icons only show two types of icons otherwise (files and folders)
		 * There's no menu option for this preference, but you can manually modify the settings file yourself to enable it
		 * this option is also automatically enabled on linux
		 */

		// if we're on linux we want the content based icons as default        
		boolean defaultValue = SystemUtils.IS_OS_LINUX;

		return getBooleanValue(Key.useContentBasedTypeIcons, defaultValue);
	}

	public void setUseContentBasedTypeIcons(boolean preferenceValue) {
		setBooleanValue(Key.useContentBasedTypeIcons, preferenceValue);
	}

	public String getLastUsedScraper() {
		return getStringValue(Key.lastUsedScraper, null);
	}

	public void setLastUsedScraper(String preferenceValue) {
		setStringValue(Key.lastUsedScraper, preferenceValue);
	}

	public String getPathToExternalMediaPlayer() {
		return getStringValue(Key.pathToExternalMediaPlayer, null);
	}

	public void setPathToExternalMediaPlayer(String externalMediaPlayer) {
		setStringValue(Key.pathToExternalMediaPlayer, externalMediaPlayer);
	}

	public Integer getWidth() {
		return getIntegerValue(Key.width, 1045);
	}

	public void setWidth(Integer value) {
		setIntegerValue(Key.width, value);
	}

	public Integer getHeight() {
		return getIntegerValue(Key.height, 850);
	}

	public void setHeight(Integer value) {
		setIntegerValue(Key.height, value);
	}

	public Integer getFileListDividerLocation() {
		return getIntegerValue(Key.fileListDividerLocation, 850);
	}

	public void setFileListDividerLocation(Integer value) {
		setIntegerValue(Key.fileListDividerLocation, value);
	}

	public void setHideImages(boolean preferenceValue){
		setBooleanValue(Key.HideImages, preferenceValue);
	}

	public Boolean getHideImages(){
		return getBooleanValue(Key.HideImages, false);
	}

	public void setHideNFOFiles(boolean preferenceValue){
		setBooleanValue(Key.HideNFOFiles, preferenceValue);
	}

	public Boolean getHideNFOFiles(){
		return getBooleanValue(Key.HideNFOFiles, false);
	}

	public void setAscending(boolean value){
		setBooleanValue(Key.Ordering, value);
	}

	public Boolean getAscending(){
		return getBooleanValue(Key.Ordering, false);
	}

	public void setSort(DirectorySort value){
		setIntegerValue(Key.Sort, value.getValue());
	}

	public DirectorySort getSort(){
		return DirectorySort.values()[getIntegerValue(Key.Sort, DirectorySort.DateModified.getValue())];
	}
}
