package com.github.youngerdryas89.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javafx.application.Platform;
import javafx.stage.FileChooser;
import com.github.youngerdryas89.model.preferences.GuiSettings;
import com.github.youngerdryas89.view.GUIMain;

public class ChooseExternalMediaPlayerAction implements ActionListener {

	public ChooseExternalMediaPlayerAction() {
	}

	public void actionPerformed(ActionEvent arg0) {

		FileChooser chooser = createFileChooser();

		// run on javafx thread - required since our file chooser is javafx and the rest of our app is swing
		Platform.runLater(() -> {
			File returnVal = chooser.showOpenDialog(null);
			if (returnVal != null && returnVal.exists()) {
				GuiSettings.getInstance().setPathToExternalMediaPlayer(returnVal.toString());
			}
		});
	}

	private static FileChooser createFileChooser() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Choosing External Media Player");
		return fileChooser;
	}

}
