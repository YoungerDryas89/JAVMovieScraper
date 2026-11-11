package com.github.youngerdryas89.moviescraper;

import com.github.youngerdryas89.moviescraper.model.preferences.ScrapeSession;

import com.github.youngerdryas89.moviescraper.view.GUIMain;

import javax.swing.*;

public class Main {
	public static void main(String[] args) throws Exception {
		ScrapeSession session = new ScrapeSession();

		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		//Prevent text area font from looking different than text field font
		UIManager.getDefaults().put("TextArea.font", UIManager.getFont("TextField.font"));

		GUIMain window = new GUIMain(session);
		window.run();
	}
}
