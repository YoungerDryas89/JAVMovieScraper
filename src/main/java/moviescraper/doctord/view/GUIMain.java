package moviescraper.doctord.view;

import java.awt.*;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;

import javax.swing.JList;

import moviescraper.doctord.controller.DirectorySort;
import moviescraper.doctord.controller.SelectFileListAction;
import moviescraper.doctord.controller.amalgamation.AllAmalgamationOrderingPreferences;
import moviescraper.doctord.model.IconCache;
import moviescraper.doctord.model.Movie;
import moviescraper.doctord.model.SearchResult;
import moviescraper.doctord.model.dataitem.Actor;
import moviescraper.doctord.model.preferences.GuiSettings;
import moviescraper.doctord.model.preferences.MoviescraperPreferences;
import moviescraper.doctord.view.renderer.FileRenderer;

import java.awt.event.*;

import javax.swing.UIManager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.beans.PropertyChangeEvent;
import java.util.function.Supplier;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import javafx.embed.swing.JFXPanel;
import javafx.stage.DirectoryChooser;
import javax.swing.BoxLayout;
import javax.swing.event.ListSelectionListener;

public class GUIMain {

	//Objects Used to Keep Track of Program State
	private List<File> currentlySelectedNfoFileList;
	private List<File> currentlySelectedPosterFileList;
	private List<File> currentlySelectedFolderJpgFileList;
	private List<File> currentlySelectedFanartFileList;
	private List<File> currentlySelectedTrailerFileList;
	private List<File> currentlySelectedMovieFileList;
	private List<File> currentlySelectedActorsFolderList;
	private File currentlySelectedDirectoryList;
	private File defaultHomeDirectory;
	private MoviescraperPreferences preferences;
	private GuiSettings guiSettings;
	private AllAmalgamationOrderingPreferences allAmalgamationOrderingPreferences;

	//scraped movies
	public List<Movie> movieToWriteToDiskList;

	//Gui Elements
	private JFrame frmMoviescraper;
	protected WindowBlocker frmMovieScraperBlocker;
	private DefaultListModel<File> listModelFiles;

	private JPanel fileListPanel;
	private FileDetailPanel fileDetailPanel;

	private JScrollPane fileListScrollPane;
	private JSplitPane fileListFileDetailSplitPane;
	FileList fileList;
	private DirectoryChooser chooser;

	private MessageConsolePanel messageConsolePanel;

	private ProgressMonitor progressMonitor;

	//variables for fileList

	//Menus
	private GUIMainMenuBar menuBar;
	private String originalJavLibraryMovieTitleBeforeAmalgamate;

	//Dimensions of various elements
	private static final int minimumHeight = 500;
	private static final int minimumWidth = 300;

	private final static boolean debugMessages = false;
	private GUIMainButtonPanel buttonPanel;

	//JavaFX stuff
	//Ignore warnings about this not being used. It is used for the file browser. 
	//You can comment this variable out and you will see the file browsing no longer works :)
	@SuppressWarnings("unused")
	private final JFXPanel fxPanel = new JFXPanel(); //ensures the JavaFX library is loaded - allows us to use DirectoryChooser later on

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
					//Prevent text area font from looking different than text field font
					UIManager.getDefaults().put("TextArea.font", UIManager.getFont("TextField.font"));
					GUIMain window = new GUIMain();
					System.out.println("Gui Initialized");
					window.frmMoviescraper.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog(null, ExceptionUtils.getStackTrace(e), "Unhandled Exception", JOptionPane.ERROR_MESSAGE);

				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public GUIMain() {
		initialize();
	}

	public void debugWriter(String message) {
		if (debugMessages)
			System.out.println(message);
	}

	/**
	 * Restore amalgamation preferences from what is saved on disk
	 */
	public void reinitializeAmalgamationPreferencesFromFile() {

		allAmalgamationOrderingPreferences.initializeValuesFromPreferenceFile();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {

		preferences = MoviescraperPreferences.getInstance();
		guiSettings = GuiSettings.getInstance();

		allAmalgamationOrderingPreferences = new AllAmalgamationOrderingPreferences();

		setCurrentlySelectedNfoFileList(new ArrayList<File>());
		setCurrentlySelectedMovieFileList(new ArrayList<File>());
		setCurrentlySelectedPosterFileList(new ArrayList<File>());
		setCurrentlySelectedFolderJpgFileList(new ArrayList<File>());
		setCurrentlySelectedFanartFileList(new ArrayList<File>());
		setCurrentlySelectedTrailerFileList(new ArrayList<File>());
		currentlySelectedActorsFolderList = new ArrayList<>();
		movieToWriteToDiskList = new ArrayList<>();
		frmMoviescraper = new JFrame();
		frmMovieScraperBlocker = new WindowBlocker();
		//set up the window that sits above the frame and can block input to this frame if needed while a dialog is open
		frmMoviescraper.setGlassPane(frmMovieScraperBlocker);
		frmMoviescraper.setBackground(SystemColor.window);
		frmMoviescraper.setMinimumSize(new Dimension(minimumWidth, minimumHeight));
		frmMoviescraper.setPreferredSize(new Dimension(guiSettings.getWidth(), guiSettings.getHeight()));
		frmMoviescraper.setTitle("JAVMovieScraper v0.6.0");
		frmMoviescraper.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Add listener
		frmMoviescraper.addComponentListener(new ComponentListener() {
			@Override
			public void componentResized(ComponentEvent arg0) {
				guiSettings.setHeight(frmMoviescraper.getHeight());
				guiSettings.setWidth(frmMoviescraper.getWidth());
			}

			@Override
			public void componentMoved(ComponentEvent e) {
			}

			@Override
			public void componentShown(ComponentEvent e) {
			}

			@Override
			public void componentHidden(ComponentEvent e) {
			}
		});
		//create tree view icon provider
		IconCache.setIconProvider(getGuiSettings().getUseContentBasedTypeIcons() ? IconCache.IconProviderType.CONTENT : IconCache.IconProviderType.SYSTEM);

		//Used for icon in the title bar
		frmMoviescraper.setIconImage(GUICommon.getProgramIcon());

		//Set up the file list panel - the panel where the user picks what file to scrape
		setUpFileListPanel();

		//Set up the bottom panel - area for message panel
		messageConsolePanel = new MessageConsolePanel();
		frmMoviescraper.getContentPane().add(messageConsolePanel, BorderLayout.SOUTH);

		buttonPanel = new GUIMainButtonPanel(this);
		frmMoviescraper.getContentPane().add(buttonPanel, BorderLayout.NORTH);

		//add in the menu bar
		menuBar = new GUIMainMenuBar(this);
		frmMoviescraper.setJMenuBar(menuBar);
		frmMoviescraper.pack();
		frmMoviescraper.setLocationRelativeTo(null);
		int gap = 7;
		fileListFileDetailSplitPane.setBorder(BorderFactory.createEmptyBorder());
		fileListFileDetailSplitPane.setDividerSize(gap);
		fileListFileDetailSplitPane.setDividerLocation(guiSettings.getFileListDividerLocation());
		fileListFileDetailSplitPane.addPropertyChangeListener((PropertyChangeEvent evt) -> {
			if ("dividerLocation".equals(evt.getPropertyName())) {
				guiSettings.setFileListDividerLocation((Integer) evt.getNewValue());
			}
		});
		messageConsolePanel.setBorder(BorderFactory.createEmptyBorder(gap, 0, 0, 0));

		// restore gui state
		buttonPanel.setVisible(guiSettings.getShowToolbar());
		messageConsolePanel.setVisible(guiSettings.getShowOutputPanel());

	}

	/**
	 * @param upIcon
	 * @param browseDirectoryIcon
	 * @param refreshDirectoryIcon
	 */
	private void setUpFileListPanel() {
		fileListPanel = new JPanel();

        fileList = new FileList(guiSettings);
        fileList.addPreUpdateListener(new FileListListener() {
            @Override
            public void preHandleSelectedReferences(FileList parent) {

            }

            @Override
            public void handleSelectedReferences(FileList parent) {

            }

            @Override
            public void preUpdate(FileList parent) {
                getFrmMoviescraper().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            }

            @Override
            public void Updated(FileList parent) {
                getFrmMoviescraper().setCursor(Cursor.getDefaultCursor());
            }
        });
        fileList.addListSelectionListener(new SelectFileListAction(this));
        //add mouse listener for double click
        fileList.addMouseListener(new MouseListener() {

            @Override
            public void mouseReleased(MouseEvent e) {
                // TODO Auto-generated method stub

            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getClickCount() >= 2) {
                    @SuppressWarnings("unchecked")
                    JList<File> theList = (JList<File>) e.getSource();
                    try {
                        File doubleClickedFile = theList.getSelectedValue();
                        if (doubleClickedFile != null && doubleClickedFile.exists() && doubleClickedFile.isDirectory()) {
                            try {
                                //setCurrentlySelectedDirectoryList(doubleClickedFile);
                                fileList.setCurrentlySelectedDirectory(doubleClickedFile);
                                //frmMoviescraper.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                                updateFileListModel(true);
                            } finally {
                                guiSettings.setLastUsedDirectory(fileList.currentlySelectedDirectory);
                                //frmMoviescraper.setCursor(Cursor.getDefaultCursor());
                            }
                        } else {
                            Desktop.getDesktop().open(theList.getSelectedValue());
                        }
                    } catch (IOException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                }

            }

            @Override
            public void mouseExited(MouseEvent e) {
                // TODO Auto-generated method stub

            }

            @Override
            public void mouseEntered(MouseEvent e) {
                // TODO Auto-generated method stub

            }

            @Override
            public void mouseClicked(MouseEvent e) {
                // TODO Auto-generated method stub

            }
        });
        fileListScrollPane = fileList.newScrollPane();
        fileListPanel.add(fileListScrollPane);

		fileListPanel.setLayout(new BoxLayout(fileListPanel, BoxLayout.Y_AXIS));
		fileListPanel.add(fileListScrollPane);

		fileDetailPanel = new FileDetailPanel(getPreferences(), this);
		JScrollPane fileDetailsScrollPane = new JScrollPane();
        fileDetailsScrollPane.setViewportView(fileDetailPanel);

		fileListFileDetailSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, fileListPanel, fileDetailsScrollPane);
		fileListPanel.setMinimumSize(new Dimension(200, 50));
		fileDetailsScrollPane.setMinimumSize(new Dimension(100, 50));

		frmMoviescraper.getContentPane().add(fileListFileDetailSplitPane, BorderLayout.CENTER);
	}

	public void removeOldScrapedMovieReferences() {
		setOriginalJavLibraryMovieTitleBeforeAmalgamate(null);
		if (movieToWriteToDiskList != null)
			movieToWriteToDiskList.clear();
	}

	public void removeOldSelectedFileReferences() {
		getCurrentlySelectedNfoFileList().clear();
		getCurrentlySelectedMovieFileList().clear();
		getCurrentlySelectedActorsFolderList().clear();
		getCurrentlySelectedPosterFileList().clear();
		getCurrentlySelectedFolderJpgFileList().clear();
		getCurrentlySelectedFanartFileList().clear();
		getCurrentlySelectedTrailerFileList().clear();
		getMovieToWriteToDiskList().clear();
		removeOldScrapedMovieReferences();
	}

	public void updateFileListModel(boolean keepSelectionsAndReferences) {
        if(!keepSelectionsAndReferences){
            removeOldSelectedFileReferences();
            removeOldScrapedMovieReferences();
        }

        fileList.update(keepSelectionsAndReferences);
	}


	public void clearAllFieldsOfFileDetailPanel() {
		fileDetailPanel.clearView();
		fileDetailPanel.setTitleEditable(false);
	}

	//Update the File Detail Panel GUI so the user can see what is scraped in
	public void updateAllFieldsOfFileDetailPanel(boolean forceUpdatePoster, boolean newMovieWasSet) {
		fileDetailPanel.currentListIndexOfDisplayedMovie = 0;
		fileDetailPanel.updateView(forceUpdatePoster, newMovieWasSet);
	}

	public SearchResult showOptionPane(SearchResult[] searchResults, String siteName) {
		if (searchResults.length > 0) {

			SelectionDialog selectionDialog = new SelectionDialog(searchResults, siteName);

			int optionPicked = JOptionPane.showOptionDialog(frmMoviescraper, selectionDialog, "Select Movie to Scrape From " + siteName, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null,
			        null, null);
			if (optionPicked == JOptionPane.CANCEL_OPTION)
				return null;
			return selectionDialog.getSelectedValue();
		} else
			return null;
	}


	public void updateActorsFolder() {
		for (int movieNumberInList = 0; movieNumberInList < getCurrentlySelectedMovieFileList().size(); movieNumberInList++) {
			if (getCurrentlySelectedMovieFileList().get(movieNumberInList).isDirectory()) {
				currentlySelectedActorsFolderList.add(new File(getCurrentlySelectedMovieFileList().get(movieNumberInList).getPath() + File.separator + ".actors"));
			} else if (getCurrentlySelectedMovieFileList().get(movieNumberInList).isFile()) {
				currentlySelectedActorsFolderList.add(new File(getCurrentlySelectedDirectoryList().getPath() + File.separator + ".actors"));
			}
		}
	}

	public void setMainGUIEnabled(boolean value) {
		if (value)
			frmMovieScraperBlocker.unBlock();
		else if (!value)
			frmMovieScraperBlocker.block();
	}

	public File[] actorFolderFiles(int movieNumberInList) {
		ArrayList<File> actorFiles = new ArrayList<>();
		if (movieToWriteToDiskList != null && movieToWriteToDiskList.size() > 0 && movieToWriteToDiskList.size() > movieNumberInList && movieToWriteToDiskList.get(movieNumberInList) != null
		        && movieToWriteToDiskList.get(movieNumberInList).getActors() != null) {
			if (currentlySelectedActorsFolderList != null && currentlySelectedActorsFolderList.get(movieNumberInList).isDirectory()) {
				for (Actor currentActor : movieToWriteToDiskList.get(movieNumberInList).getActors()) {
					String currentActorNameAsPotentialFileName = currentActor.getName().replace(' ', '_');
					File[] listFiles = currentlySelectedActorsFolderList.get(movieNumberInList).listFiles();
					for (File currentFile : listFiles) {
						if (currentFile.isFile() && FilenameUtils.removeExtension(currentFile.getName()).equals(currentActorNameAsPotentialFileName)) {
							actorFiles.add(currentFile);
						}
					}
				}
			}
		}
		return actorFiles.toArray(new File[actorFiles.size()]);
	}

	public FileDetailPanel getFileDetailPanel() {
		return fileDetailPanel;
	}

	public List<File> getCurrentFile() {
		if (getCurrentlySelectedMovieFileList().size() > 0)
			return getCurrentlySelectedMovieFileList();
		return null;
	}

	public JFrame getFrmMoviescraper() {
		return frmMoviescraper;
	}

	public List<File> getCurrentlySelectedActorsFolderList() {
		return currentlySelectedActorsFolderList;
	}

	public void setCurrentlySelectedActorsFolderList(List<File> currentlySelectedActorsFolderList) {
		this.currentlySelectedActorsFolderList = currentlySelectedActorsFolderList;
	}

	public List<Movie> getMovieToWriteToDiskList() {
		return movieToWriteToDiskList;
	}

	public void setMovieToWriteToDiskList(List<Movie> movieToWriteToDiskList) {
		this.movieToWriteToDiskList = movieToWriteToDiskList;
	}

	public List<File> getCurrentlySelectedMovieFileList() {
		return this.currentlySelectedMovieFileList;
	}

	public void setCurrentlySelectedMovieFileList(List<File> currentlySelectedMovieFileList) {
		this.currentlySelectedMovieFileList = currentlySelectedMovieFileList;
	}

	public List<File> getCurrentlySelectedPosterFileList() {
		return this.currentlySelectedPosterFileList;
	}

	public void setCurrentlySelectedPosterFileList(List<File> currentlySelectedPosterFileList) {
		this.currentlySelectedPosterFileList = currentlySelectedPosterFileList;
	}

	public List<File> getCurrentlySelectedFanartFileList() {
		return this.currentlySelectedFanartFileList;
	}

	public void setCurrentlySelectedFanartFileList(List<File> currentlySelectedFanartFileList) {
		this.currentlySelectedFanartFileList = currentlySelectedFanartFileList;
	}

	public DirectoryChooser getChooser() {
		return chooser;
	}

	public void setChooser(DirectoryChooser chooser) {
		this.chooser = chooser;
	}

	public MoviescraperPreferences getPreferences() {
		return preferences;
	}

	public GuiSettings getGuiSettings() {
		return guiSettings;
	}

	public File getCurrentlySelectedDirectoryList() {
		return fileList.currentlySelectedDirectory;
	}

	public void setCurrentlySelectedDirectoryList(File currentlySelectedDirectoryList) {
		fileList.currentlySelectedDirectory = currentlySelectedDirectoryList;
	}

	public List<File> getCurrentlySelectedNfoFileList() {
		return this.currentlySelectedNfoFileList;
	}

	public void setCurrentlySelectedNfoFileList(List<File> currentlySelectedNfoFileList) {
		this.currentlySelectedNfoFileList = currentlySelectedNfoFileList;
	}

	public List<File> getCurrentlySelectedFolderJpgFileList() {
		return this.currentlySelectedFolderJpgFileList;
	}

	public void setCurrentlySelectedFolderJpgFileList(List<File> currentlySelectedFolderJpgFileList) {
        this.currentlySelectedFolderJpgFileList = currentlySelectedFolderJpgFileList;
	}

	public List<File> getCurrentlySelectedTrailerFileList() {
		return this.currentlySelectedTrailerFileList;
	}

	public void setCurrentlySelectedTrailerFileList(List<File> currentlySelectedTrailerFileList) {
		this.currentlySelectedTrailerFileList = currentlySelectedTrailerFileList;
	}

	public FileList getFileList() {
		return fileList;
	}

	public void setFileList(FileList fileList) {
		this.fileList = fileList;
	}

	public ProgressMonitor getProgressMonitor() {
		if (progressMonitor == null)
			progressMonitor = new ProgressMonitor(frmMoviescraper);
		return progressMonitor;
	}

	public void setProgressMonitor(ProgressMonitor progressMonitor) {
		this.progressMonitor = progressMonitor;
	}

	public void showMessageConsolePanel() {
		messageConsolePanel.setVisible(true);
		guiSettings.setShowOutputPanel(true);
	}

	public void hideMessageConsolePanel() {
		messageConsolePanel.setVisible(false);
		guiSettings.setShowOutputPanel(false);
	}

	public void showButtonPanel() {
		buttonPanel.setVisible(true);
		guiSettings.setShowToolbar(true);
	}

	public void hideButtonPanel() {
		buttonPanel.setVisible(false);
		guiSettings.setShowToolbar(false);
	}

	public String getOriginalJavLibraryMovieTitleBeforeAmalgamate() {
		return originalJavLibraryMovieTitleBeforeAmalgamate;
	}

	public void setOriginalJavLibraryMovieTitleBeforeAmalgamate(String originalJavLibraryMovieTitleBeforeAmalgamate) {
		this.originalJavLibraryMovieTitleBeforeAmalgamate = originalJavLibraryMovieTitleBeforeAmalgamate;
	}

	public boolean showAmalgamationSettingsDialog() {
		AmalgamationSettingsDialog dialog = new AmalgamationSettingsDialog(this, getAllAmalgamationOrderingPreferences());
		return dialog.show();
	}

	public AllAmalgamationOrderingPreferences getAllAmalgamationOrderingPreferences() {
		//rereading from file in case external program somehow decides to change this file before we get it.
		//also this fixes a bug where canceling a scrape somehow corrupted the variable and caused an error when opening the
		//amalgamation settings dialog
		allAmalgamationOrderingPreferences.initializeValuesFromPreferenceFile();
		return allAmalgamationOrderingPreferences;
	}

	public void setAllAmalgamationOrderingPreferences(AllAmalgamationOrderingPreferences allAmalgamationOrderingPreferences) {
		this.allAmalgamationOrderingPreferences = allAmalgamationOrderingPreferences;
	}

	public void enableFileWrite() {
		menuBar.enableWriteFile();
		buttonPanel.enableWriteFile();
	}

	public void disableFileWrite() {
		menuBar.disableWriteFile();
		buttonPanel.disableWriteFile();
	}

	public void updateFileList(){
		updateFileListModel(true);
	}
}