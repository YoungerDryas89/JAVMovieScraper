/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package moviescraper.doctord.view;

import java.util.ArrayList;
import java.util.List;
import moviescraper.doctord.controller.siteparsingprofile.SiteParsingProfileItem;
import moviescraper.doctord.controller.siteparsingprofile.SpecificProfileFactory;

/**
 *
 * @author sagit
 */
public class ScraperSettingsModel extends javax.swing.AbstractListModel {
    List<SiteParsingProfileItem> scrapers = new ArrayList<>(SpecificProfileFactory.getAll());
    public ScraperSettingsModel() {
    }

    @Override
    public int getSize() {
        return scrapers.size();
    }

    @Override
    public SiteParsingProfileItem getElementAt(int index) {
        return scrapers.get(index);
    }
    
}
