package com.github.youngerdryas89.moviescraper.view.scrapersettings;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class ScraperSelectionListener implements ListSelectionListener {
    private ScraperSettingsGUI parent;
    public ScraperSelectionListener(ScraperSettingsGUI parent){
       this.parent = parent;
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if(!e.getValueIsAdjusting()) {
            if(parent.getLister().getSelectedIndex() == -1){
                // TODO: Clear selection
            } else {
                parent.updateSelection(parent.getModel().getElementAt(e.getFirstIndex()));
            }
        }

    }
}
