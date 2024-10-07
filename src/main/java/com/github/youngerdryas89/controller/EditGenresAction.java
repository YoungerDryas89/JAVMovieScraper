package com.github.youngerdryas89.controller;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import com.github.youngerdryas89.view.FileDetailPanel;
import com.github.youngerdryas89.view.GenreEditorPanel;

public class EditGenresAction extends AbstractAction {

	private static final long serialVersionUID = 1L;
	FileDetailPanel fileDetailPanel;

	@Override
	public void actionPerformed(ActionEvent e) {
		GenreEditorPanel genreEditorPanel = new GenreEditorPanel(fileDetailPanel.getCurrentMovie().getGenres());
		int result = JOptionPane.showOptionDialog(fileDetailPanel.guiMain.getFrmMoviescraper(), genreEditorPanel, "Edit Genres...", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, null,
		        null);
		if (result == JOptionPane.OK_OPTION) {
			genreEditorPanel.save();
			/*
			 * GenreItemListModel listModel = (GenreItemListModel) fileDetailPanel.getGenreList().getModel();
			 * listModel.clear();
			 * for(Genre currentGenre : fileDetailPanel.getCurrentMovie().getGenres())
			 * {
			 * listModel.addElement(currentGenre);
			 * }
			 */
			fileDetailPanel.getGenreList().setText(FileDetailPanel.toGenreListFormat(fileDetailPanel.getCurrentMovie().getGenres()));
			//listModel
			fileDetailPanel.updateUI();
		}
	}

	public EditGenresAction(FileDetailPanel fileDetailPanel) {
		super();
		this.fileDetailPanel = fileDetailPanel;
	}

}
