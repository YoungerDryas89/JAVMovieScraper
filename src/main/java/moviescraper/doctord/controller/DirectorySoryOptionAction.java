package moviescraper.doctord.controller;

import moviescraper.doctord.view.GUIMain;
import moviescraper.doctord.view.SortPopupMenu;

import javax.swing.*;
import java.awt.event.ActionEvent;

import static moviescraper.doctord.view.SortPopupMenu.dotIcon;

public class DirectorySoryOptionAction extends AbstractAction {
    private final GUIMain parent;
    public DirectorySoryOptionAction(DirectorySort key, GUIMain parentGUI){
        this.parent = parentGUI;
        putValue("Name", key.symbolToString());
        putValue("Key", key);
    }

    public DirectorySoryOptionAction(DirectoryOrder key, GUIMain parentGUI){
        this.parent = parentGUI;
        putValue("Name", key.symbolToString());
        putValue("Key", key);

    }
    @Override
    public void actionPerformed(ActionEvent e) {
        var key = getValue("Key");
        if(key instanceof DirectoryOrder) {
            switch (key){
                case DirectoryOrder.Ascending -> parent.setSortAsAscending(true);
                case DirectoryOrder.Descending -> parent.setSortAsAscending(false);
                default -> throw new IllegalStateException("Unexpected value: " + key);
            }
            parent.updateFileList();
        } else{
            if (parent.getSortSetting() != getValue("Key")) {
                parent.setSortSetting((DirectorySort) getValue("Key"));
                parent.updateFileList();
            }
        }
    }
}
