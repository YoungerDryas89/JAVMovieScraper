package moviescraper.doctord.controller;

import moviescraper.doctord.view.GUIMain;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class SortDirectoryAction implements ActionListener, ItemListener {
    private final GUIMain parent;
    SortDirectoryAction(DirectorySort key, String menuTitle, GUIMain parent){
        this.parent = parent;

        JMenu menu = new JMenu();
        ButtonGroup sortRadioMenu = new ButtonGroup();
        JRadioButtonMenuItem alphabetically = new JRadioButtonMenuItem("Title");
        JRadioButtonMenuItem dateModified = new JRadioButtonMenuItem("Date Modified");
        JRadioButtonMenuItem size = new JRadioButtonMenuItem("Size");
        sortRadioMenu.add(alphabetically);
        sortRadioMenu.add(dateModified);
        sortRadioMenu.add(size);

        ButtonGroup orderMenu = new ButtonGroup();
        JRadioButtonMenuItem ascending = new JRadioButtonMenuItem("Ascending");
        ascending.setSelected(parent.getSortAsAscending());
        JRadioButtonMenuItem descending = new JRadioButtonMenuItem("Descending");
        descending.setSelected(!parent.getSortAsAscending());
        orderMenu.add(ascending);
        orderMenu.add(descending);


    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }

    @Override
    public void itemStateChanged(ItemEvent e) {

    }
}
