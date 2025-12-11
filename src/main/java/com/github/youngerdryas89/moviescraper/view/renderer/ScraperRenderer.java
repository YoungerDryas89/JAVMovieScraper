/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package moviescraper.doctord.view.renderer;

import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import moviescraper.doctord.controller.siteparsingprofile.SiteParsingProfileItem;

/**
 *
 * @author sagit
 */
public class ScraperRenderer extends DefaultListCellRenderer {
    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus){
        Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        var profile = (SiteParsingProfileItem) value;
        JLabel label = (JLabel) c;
        label.setText(profile.toString());
        label.setIcon(profile.getParser().getProfileIcon());
        return label;
    }
}
