package com.github.youngerdryas89.moviescraper.controller;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import com.github.youngerdryas89.moviescraper.model.preferences.MoviescraperPreferences;
import com.github.youngerdryas89.moviescraper.view.FileDetailPanel;
import com.github.youngerdryas89.moviescraper.view.TagEditorPanel;

public class EditTagsAction extends AbstractAction {

	private static final long serialVersionUID = 1L;
	private FileDetailPanel fileDetailPanel;
	MoviescraperPreferences preferences;

	@Override
	public void actionPerformed(ActionEvent e) {
		TagEditorPanel tagEditorPanel = new TagEditorPanel(preferences, fileDetailPanel.getCurrentMovie().getTags());
		int result = JOptionPane.showOptionDialog(fileDetailPanel.guiMain.getFrmMoviescraper(), tagEditorPanel, "Edit tags...", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, null,
		        null);
		if (result == JOptionPane.OK_OPTION) {
			tagEditorPanel.save();
			/*
			 * tagItemListModel listModel = (tagItemListModel) fileDetailPanel.gettagList().getModel();
			 * listModel.clear();
			 * for(tag currenttag : fileDetailPanel.getCurrentMovie().gettags())
			 * {
			 * listModel.addElement(currenttag);
			 * }
			 */
			fileDetailPanel.getTagList().setText(FileDetailPanel.toTagListFormat(fileDetailPanel.getCurrentMovie().getTags()));
			//listModel
			fileDetailPanel.updateUI();
		}

	}

	public EditTagsAction(MoviescraperPreferences preferences, FileDetailPanel fileDetailPanel) {
		super();
		this.fileDetailPanel = fileDetailPanel;
		this.preferences = preferences;
	}
}
