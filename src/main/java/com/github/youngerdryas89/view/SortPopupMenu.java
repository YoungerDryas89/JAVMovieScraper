package com.github.youngerdryas89.view;

import com.github.youngerdryas89.controller.DirectoryOrder;
import com.github.youngerdryas89.controller.DirectorySort;
import com.github.youngerdryas89.controller.DirectorySoryOptionAction;
import com.github.youngerdryas89.model.preferences.GuiSettings;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import static com.github.youngerdryas89.view.GUIMainButtonPanel.initializeImageIcon;

public class SortPopupMenu extends JPopupMenu {
    final GUIMain parent;
    final GuiSettings settings;
    public static ImageIcon dotIcon = initializeImageIcon("Dot");
    Action currentSort, currentOrder;
    public SortPopupMenu(GUIMain parent){
        this.parent = parent;
        this.settings = parent.getGuiSettings();

        ActionListener sortPopupMenuListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                var invoker_action = ((JMenuItem)e.getSource()).getAction();
                currentSort.putValue(Action.SMALL_ICON, null);
                currentSort = invoker_action;
                currentSort.putValue(Action.SMALL_ICON, dotIcon);
            }
        };

        ActionListener orderListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                var invoker_action = ((JMenuItem)e.getSource()).getAction();
                currentOrder.putValue(Action.SMALL_ICON, null);
                currentOrder = invoker_action;
                currentOrder.putValue(Action.SMALL_ICON, dotIcon);
            }
        };
        var alphabeticalSort = new DirectorySoryOptionAction(DirectorySort.Alphabetically, parent);
        var datemodifiedSort = new DirectorySoryOptionAction(DirectorySort.DateModified, parent);
        var sizeSort = new DirectorySoryOptionAction(DirectorySort.Size, parent);
        var ascendingOrder = new DirectorySoryOptionAction(DirectoryOrder.Ascending, parent);
        var descendingOrder = new DirectorySoryOptionAction(DirectoryOrder.Descending, parent);
        switch(settings.getSort()){
            case Alphabetically -> {
                alphabeticalSort.putValue(Action.SMALL_ICON, dotIcon);
                currentSort = alphabeticalSort;
            }
            case DateModified -> {
                datemodifiedSort.putValue(Action.SMALL_ICON, dotIcon);
                currentSort = datemodifiedSort;
            }
            case Size -> {
                sizeSort.putValue(Action.SMALL_ICON, dotIcon);
                currentSort = sizeSort;
            }
        }

        if(settings.getAscending()){
            ascendingOrder.putValue(Action.SMALL_ICON, dotIcon);
            currentOrder = ascendingOrder;
        } else {
            descendingOrder.putValue(Action.SMALL_ICON, dotIcon);
            currentOrder = descendingOrder;
        }

        add(alphabeticalSort).addActionListener(sortPopupMenuListener);
        add(datemodifiedSort).addActionListener(sortPopupMenuListener);
        add(sizeSort).addActionListener(sortPopupMenuListener);
        addSeparator();
        add(ascendingOrder).addActionListener(orderListener);
        add(descendingOrder).addActionListener(orderListener);

    }
}
