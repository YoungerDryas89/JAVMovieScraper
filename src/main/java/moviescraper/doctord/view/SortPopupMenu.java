package moviescraper.doctord.view;

import moviescraper.doctord.controller.DirectorySort;
import moviescraper.doctord.controller.DirectorySoryOptionAction;
import moviescraper.doctord.view.customcomponents.KMenuItem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static moviescraper.doctord.view.GUIMainButtonPanel.initializeImageIcon;

public class SortPopupMenu extends JPopupMenu {
    private final GUIMain parent;
    public static ImageIcon dotIcon = initializeImageIcon("Dot");
    private Action chosenSort;

    public SortPopupMenu(GUIMain parent){
        this.parent = parent;
        ActionListener sortPopupMenuListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                var act = ((KMenuItem) e.getSource()).getAction();
                if(act != chosenSort){
                    chosenSort.putValue(Action.SMALL_ICON, null);
                    chosenSort = act;
                    chosenSort.putValue(Action.SMALL_ICON, SortPopupMenu.dotIcon);

                }
            }
        };
        add(new DirectorySoryOptionAction("Title", DirectorySort.Alphabetically, parent)).addActionListener(sortPopupMenuListener);
        add(new DirectorySoryOptionAction("Date Modified", DirectorySort.DateModified, parent)).addActionListener(sortPopupMenuListener);
        add(new DirectorySoryOptionAction("Size", DirectorySort.Size, parent)).addActionListener(sortPopupMenuListener);
    }
}
