package com.github.youngerdryas89.moviescraper.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;

import com.github.youngerdryas89.moviescraper.view.FavoriteTagPickerPanel;
import com.github.youngerdryas89.moviescraper.view.GUIMain;

public class ChooseFavoriteTagsAction implements ActionListener {

	GUIMain guiMain;

	public ChooseFavoriteTagsAction(GUIMain guiMain) {
		this.guiMain = guiMain;
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		FavoriteTagPickerPanel tagPickerPanel = new FavoriteTagPickerPanel();
		int result = JOptionPane.showOptionDialog(guiMain.getFrmMoviescraper(), tagPickerPanel, "Favorite Tags...", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, null, null);
		if (result == JOptionPane.OK_OPTION) {
			tagPickerPanel.storeSettingValues();
		}
	}
}
