package moviescraper.doctord.view;

import moviescraper.doctord.controller.DirectorySort;
import moviescraper.doctord.model.preferences.GuiSettings;
import moviescraper.doctord.view.renderer.FileRenderer;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class FileList extends JList<File> {
    DefaultListModel<File> internalModel = new DefaultListModel<>();

    List<FileListListener> listeners = new ArrayList<>();

    File currentlySelectedDirectory;
    final int CHAR_DELTA = 1000;
    String m_key;
    long m_time;

    GuiSettings settings = null;

    DirectorySort currentSort;

    Boolean sortAsAscending;

    public Boolean getSortAsAscending() {
        return sortAsAscending;
    }

    public void setSortAsAscending(Boolean sortAsAscending) {
        this.sortAsAscending = sortAsAscending;
    }

    public DirectorySort getCurrentSort() {
        return currentSort;
    }

    public void setCurrentSort(DirectorySort currentSort) {
        this.currentSort = currentSort;
    }



    public File getCurrentlySelectedDirectory() {
        return currentlySelectedDirectory;
    }

    public void setCurrentlySelectedDirectory(File currentlySelectedDirectory) {
        this.currentlySelectedDirectory = currentlySelectedDirectory;
    }



    public void addPreUpdateListener(FileListListener listener) {
        this.listeners.add(listener);
    }

    public void addUpdateListener(FileListListener listener){
        this.listeners.add(listener);
    }

    public FileList(GuiSettings settings){
        this.settings = settings;

        currentSort = settings.getSort();
        sortAsAscending = settings.getAscending();

        currentlySelectedDirectory = settings.getLastUsedDirectory();
        setModel(internalModel);
        //add in a keyListener so that you can start typing letters in the list and it will take you to that item in the list
        //if you type the second letter within CHAR_DELTA amount of time that will count as the Nth letter of the search
        //instead of the first
        addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent e) {
                //do nothing until the key is released
            }

            @Override
            public void keyReleased(KeyEvent e) {
                char ch = e.getKeyChar();

                // ignore searches for non alpha-numeric characters
                if (!Character.isLetterOrDigit(ch)) {
                    return;
                }

                // reset string if too much time has elapsed
                if (m_time + CHAR_DELTA < System.currentTimeMillis()) {
                    m_key = "";
                }

                m_time = System.currentTimeMillis();
                m_key += Character.toLowerCase(ch);

                // Iterate through items in the list until a matching prefix is found.
                // This technique is fine for small lists, however, doing a linear
                // search over a very large list with additional string manipulation
                // (eg: toLowerCase) within the tight loop would be quite slow.
                // In that case, pre-processing the case-conversions, and storing the
                // strings in a more search-efficient data structure such as a Trie
                // or a Ternary Search Tree would lead to much faster find.
                for (int i = 0; i < getModel().getSize(); i++) {
                    String str = getModel().getElementAt(i).getName().toString().toLowerCase();
                    if (str.startsWith(m_key)) {
                        setSelectedIndex(i); // change selected item in list
                        ensureIndexIsVisible(i); // change listbox
                        // scroll-position
                        break;
                    }

                }

            }

            @Override
            public void keyPressed(KeyEvent e) {
                //do nothing until the key is released
            }
        });

    }


    void populate() throws NullPointerException {

        List<File>  files = Arrays.asList(Objects.requireNonNull(currentlySelectedDirectory.listFiles((dir, name) -> {
            String[] imageExtensions = {
                    ".png", ".jpg", ".jpeg", ".tif", ".webp", ".gif"
            };
            return !((settings.getHideImages() && StringUtils.endsWithAny(name, imageExtensions)) ^ (settings.getHideNFOFiles() && name.endsWith(".nfo")));
        })));


        Comparator<File> comp = new Comparator<File>() {
            @Override
            public int compare(File file1, File file2) {
                // Directory before non-directory
                if (file1.isDirectory() && !file2.isDirectory()) {

                    return -1;
                }
                // Non-directory after directory
                else if (!file1.isDirectory() && file2.isDirectory()) {

                    return 1;
                }
                else {
                    switch(currentSort){
                        case Alphabetically:
                        {
                            if(sortAsAscending)
                                return file1.compareTo(file2);
                            else
                                return file2.compareTo(file1);
                        }
                        default:
                        case DateModified:
                        {
                            if(sortAsAscending)
                                return Long.compare(file1.lastModified(), file2.lastModified());
                            else
                                return Long.compare(file2.lastModified(), file1.lastModified());
                        }
                        case Size:
                        {
                            if(sortAsAscending)
                                return Long.compare(file1.length(), file2.length());
                            else
                                return Long.compare(file2.length(), file1.length());
                        }
                    }

                }
            }
        };
        files.sort(comp);
        internalModel.removeAllElements();
        internalModel.addAll(files);
    }

    public void update(boolean keepSelectionsAndReferences) {
        SwingUtilities.invokeLater(() -> {
            for(var listener : this.listeners)
                listener.preUpdate(this);
            List<File> selectValuesListBeforeUpdate = getSelectedValuesList();


            //We don't want to fire the listeners events when reselecting the items because this
            //will cause us additional IO that is not needed as the program rereads the nfo.
            //To avoid this, we can save out the old listener, remove it, select the items and then add it back
            ListSelectionListener[] fileListSelectionListener = null;
            if (keepSelectionsAndReferences) {
                fileListSelectionListener = getListSelectionListeners();
                for(var listener : fileListSelectionListener)
                    removeListSelectionListener(listener);
            }
            populate();

            //select the old values we had before we updated the list
            for (File currentValueToSelect : selectValuesListBeforeUpdate) {
                setSelectedValue(currentValueToSelect, false);
            }
            if (keepSelectionsAndReferences && fileListSelectionListener != null) {
                for(var listener : fileListSelectionListener)
                    addListSelectionListener(listener);
            }

            for(var listener : this.listeners)
                listener.Updated(this);
        });
    }

    public JScrollPane newScrollPane(){

        populate();
        setCellRenderer(new FileRenderer(false));
        //setLayoutOrientation(VERTICAL_WRAP);
        setVisibleRowCount(9);
        return new JScrollPane(this);
    }
}
