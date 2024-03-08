package moviescraper.doctord.controller;

import moviescraper.doctord.view.GUIMain;
import moviescraper.doctord.view.SortPopupMenu;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class DirectorySoryOptionAction extends AbstractAction {
    private final GUIMain parent;
    private Boolean iconToggle = false;
    public DirectorySoryOptionAction(String name, DirectorySort key, GUIMain parentGUI){
        this.parent = parentGUI;
        putValue("Name", name);
        putValue("Key", key);
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        if(parent.getSortAsAscending() != getValue("Key")){
            parent.setSortSetting((DirectorySort) getValue("Key"));
            parent.updateFileList();
        }
    }

    public Boolean getIconToggle(){
        return iconToggle;
    }
    public void toggleSortIcon(Boolean enable){
        if(enable)
            putValue(Action.SMALL_ICON, SortPopupMenu.dotIcon);
        else
            putValue(Action.SMALL_ICON, null);
    }
}
