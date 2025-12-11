package com.github.youngerdryas89.moviescraper.controller;

import com.github.youngerdryas89.moviescraper.model.preferences.GuiSettings;
import com.github.youngerdryas89.moviescraper.view.GUIMain;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class DirectorySoryOptionAction extends AbstractAction {
    private final GUIMain parent;
    final GuiSettings settings;
    public DirectorySoryOptionAction(DirectorySort key, GUIMain parentGUI){
        this.parent = parentGUI;
        this.settings = parent.getGuiSettings();
        putValue("Name", key.symbolToString());
        putValue("Key", key);
    }

    public DirectorySoryOptionAction(DirectoryOrder key, GUIMain parentGUI){
        this.parent = parentGUI;
        this.settings = parent.getGuiSettings();
        putValue("Name", key.symbolToString());
        putValue("Key", key);

    }
    @Override
    public void actionPerformed(ActionEvent e) {
        var key = getValue("Key");
        if(key instanceof DirectoryOrder) {
            switch (key){
                case DirectoryOrder.Ascending -> settings.setAscending(true);
                case DirectoryOrder.Descending -> settings.setAscending(false);
                default -> throw new IllegalStateException("Unexpected value: " + key);
            }
            parent.updateFileList();
        } else{
            if (settings.getSort() != getValue("Key")) {
                settings.setSort((DirectorySort) getValue("Key"));
                parent.updateFileList();
            }
        }
    }
}
