package com.github.youngerdryas89.view;

import com.github.youngerdryas89.model.preferences.GuiSettings;
import com.github.youngerdryas89.model.preferences.MoviescraperPreferences;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static com.github.youngerdryas89.view.GUIMainButtonPanel.initializeImageIcon;

enum FilterModes {
    HideImages(0),
    HideNFOFiles(1);

    final int value;
    FilterModes(int i) {
        value = i;
    }

    public int getValue() {
        return value;
    }
};

class FilterOptionAction extends AbstractAction{
    final GUIMain parent;
    final GuiSettings settings;
    public FilterOptionAction(FilterModes key, GUIMain parent){
        this.parent = parent;
        this.settings = parent.getGuiSettings();
        putValue("Name", symbolToString(key));
        putValue("Key", key);
        putValue(SMALL_ICON, null);
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        var key = getValue("Key");
        if(key instanceof FilterModes){
            switch (key){
                case FilterModes.HideImages -> settings.setHideImages(!settings.getHideImages());
                case FilterModes.HideNFOFiles -> settings.setHideNFOFiles(!settings.getHideNFOFiles());
                default -> throw new IllegalStateException("Unexpected value: " + key);
            }
            parent.updateFileList();
        }
    }

    String symbolToString(FilterModes mode){
        return switch (mode){
            default -> throw new IllegalStateException("Unexpected value: " + mode);
            case FilterModes.HideImages -> "Hide Images";
            case FilterModes.HideNFOFiles -> "Hide NFO Files";
        };
    }
}
public class FilterPopupMenu extends JPopupMenu {
    final GUIMain parent;
    final GuiSettings settings;
    ImageIcon enabledIcon = initializeImageIcon("Check");
    public FilterPopupMenu(GUIMain parent) {
        this.parent = parent;
        this.settings = parent.getGuiSettings();

        ActionListener filterActionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Action invoker = ((JMenuItem)e.getSource()).getAction();
                if(invoker.getValue(Action.SMALL_ICON) == null){
                    invoker.putValue(Action.SMALL_ICON, enabledIcon);
                } else {

                    invoker.putValue(Action.SMALL_ICON, null);
                }
            }
        };

        var hideImages = new FilterOptionAction(FilterModes.HideImages, parent);
        var hideNFOs = new FilterOptionAction(FilterModes.HideNFOFiles, parent);
        if(settings.getHideNFOFiles()){
            hideNFOs.putValue(Action.SMALL_ICON, enabledIcon);
        }

        if(settings.getHideImages()){
            hideImages.putValue(Action.SMALL_ICON, enabledIcon);
        }

        add(hideImages).addActionListener(filterActionListener);
        add(hideNFOs).addActionListener(filterActionListener);
    }
}
