/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.github.youngerdryas89.moviescraper.view.scrapersettings;

import com.github.youngerdryas89.moviescraper.controller.siteparsingprofile.SiteParsingProfileItem;
import com.github.youngerdryas89.moviescraper.view.GUIMain;
import com.github.youngerdryas89.moviescraper.view.renderer.ScraperRenderer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import static javax.swing.JSplitPane.HORIZONTAL_SPLIT;

/**
 *
 * @author sai
 */
public class ScraperSettingsGUI extends JFrame {
    private javax.swing.JPanel ScraperListerPanel;
    private javax.swing.JScrollPane ScraperListerScrollPanel;
    private javax.swing.JPanel SettingsPanel;
    private javax.swing.JScrollPane SettingsScrollPanel;
    private javax.swing.JList<String> jList1;
    private javax.swing.JSplitPane jSplitPane1;
    private ScraperSettingsModel ssmListerModel;
    private GUIMain parent;

    JLabel settingsHeading;

    JCheckBox scraperEnabled = new JCheckBox("Enabled");

    JCheckBox overrideHostname = new JCheckBox("Override Hostname");
    JTextField overridenHostname = new JTextField("N/A");
    JLabel overridenHostnameLabel = new JLabel("Overridden Hostname:");
    JPanel hostTextBoxPanel = new JPanel();

    JCheckBox excludefromAmalgamation = new JCheckBox("Exclude from amalgamation scraping");



    public static void main(String[] args){

        java.awt.EventQueue.invokeLater(() -> new ScraperSettingsGUI().setVisible(true));
    }

    public ScraperSettingsGUI() {
        initializeComponents();
    }

    public ScraperSettingsGUI(GUIMain parent){
        this.parent = parent;
        initializeComponents();

    }

    public ScraperSettingsModel getModel(){
        return ssmListerModel;
    }

    public JList<String> getLister(){
        return jList1;
    }

    void setupSettingsPanel() {

        settingsHeading = new JLabel("Settings for ", SwingConstants.CENTER);
        settingsHeading.setVisible(false);

        SettingsPanel.add(settingsHeading, BorderLayout.NORTH);

        overridenHostname.setColumns(25);
        overridenHostname.setEnabled(overrideHostname.isSelected());

        JPanel generalSettings = new JPanel();

        hostTextBoxPanel.setLayout(new FlowLayout());
        hostTextBoxPanel.add(Box.createHorizontalStrut(20));
        hostTextBoxPanel.add(overridenHostnameLabel);
        hostTextBoxPanel.add(overridenHostname);
        hostTextBoxPanel.setVisible(false);

        excludefromAmalgamation.setSelected(false);
        excludefromAmalgamation.setVisible(false);

        GroupLayout layout = new GroupLayout(generalSettings);
        layout.setAutoCreateContainerGaps(true);
        layout.setAutoCreateGaps(true);
        layout.setHorizontalGroup(

                layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                        .addComponent(scraperEnabled)
                        .addComponent(excludefromAmalgamation)
                        .addComponent(overrideHostname)
                        .addGroup(layout.createSequentialGroup()
//                                .addPreferredGap(overrideHostname, hostTextBoxPanel, LayoutStyle.ComponentPlacement.INDENT, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE)
                                .addComponent(hostTextBoxPanel)
                        )
        );

        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addComponent(scraperEnabled)
                        .addComponent(excludefromAmalgamation)
                        .addComponent(overrideHostname)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(hostTextBoxPanel)
        );
        generalSettings.setLayout(layout);

        scraperEnabled.setEnabled(true);
        scraperEnabled.setVisible(false);
        overrideHostname.setVisible(false);
        SettingsPanel.add(generalSettings);
        
    }


    void updateSelection(SiteParsingProfileItem selected){
        settingsHeading.setText("Settings for " + selected.getParser().getParserName());
        settingsHeading.setVisible(true);
        scraperEnabled.setVisible(true);
        scraperEnabled.setSelected(!selected.isDisabled());
        excludefromAmalgamation.setVisible(true);
        overrideHostname.setVisible(true);
        hostTextBoxPanel.setVisible(true);
    }


    void initializeComponents(){
        ScraperListerPanel = new javax.swing.JPanel();
        ScraperListerScrollPanel = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList<>();
        SettingsPanel = new javax.swing.JPanel();
        SettingsScrollPanel = new javax.swing.JScrollPane();
        jSplitPane1 = new javax.swing.JSplitPane(HORIZONTAL_SPLIT, ScraperListerPanel, SettingsPanel);

        ScraperListerPanel.setLayout(new javax.swing.BoxLayout(ScraperListerPanel, javax.swing.BoxLayout.LINE_AXIS));

        ScraperListerScrollPanel.setViewportBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        ssmListerModel = new ScraperSettingsModel();
        jList1.setModel(ssmListerModel);
        jList1.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jList1.setCellRenderer(new ScraperRenderer());
        jList1.addListSelectionListener(new ScraperSelectionListener(this));
        ScraperListerScrollPanel.setViewportView(jList1);

        ScraperListerPanel.add(ScraperListerScrollPanel);

        SettingsPanel.setLayout(new java.awt.BorderLayout());
        SettingsPanel.add(SettingsScrollPanel, java.awt.BorderLayout.CENTER);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Settings");
        setBackground(SystemColor.window);
        setMinimumSize(new java.awt.Dimension(700, 350));

        jSplitPane1.setBackground(SystemColor.window);
        jSplitPane1.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jSplitPane1.setDividerSize(7);
        getContentPane().add(jSplitPane1, java.awt.BorderLayout.CENTER);

        setupSettingsPanel();

        pack();
        setLocationRelativeTo(null);
    }
}
