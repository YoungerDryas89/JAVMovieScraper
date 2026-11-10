package com.github.youngerdryas89.moviescraper.model.preferences;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public class ScrapeSession {
    private static final Logger log = LoggerFactory.getLogger(ScrapeSession.class);
    private final ObjectMapper MAPPER = new ObjectMapper();
    private final SimpleModule MODULE = new SimpleModule();

    private MoviescraperPreferences preferences = new MoviescraperPreferences();

    // our preferences file
    private final Path configFile = Path.of("preferences.json");

    boolean degradedMode = false;


    public ScrapeSession(){

        MAPPER.enable(SerializationFeature.INDENT_OUTPUT);
        MAPPER.registerModule(MODULE);

        try{
            var prefs = loadPreferences();
            if(prefs.isEmpty()){
                savePreferences();
            } else {
                preferences = prefs.get();
            }

        }catch (IOException e){
            log.warn("WARNING: Your preferences.json is corrupted or unreadable. Delete it and start again or fix it.", e);
            log.warn("WARNING: Skipping on loading preferences.json and starting with default settings.");

            if(Files.exists(configFile)){
                try {
                    Files.move(configFile, Path.of(configFile.toString() + ".old"));
                    log.warn("WARNING: Moving old configuration file to {}.old", configFile.toString());
                }catch (IOException e2) {
                    log.error("WARNING: Couldn't rename old configuration file. Disabling auto-save to disk to protect data.", e2);
                    degradedMode = true;
                }
            }
        }
    }

    public MoviescraperPreferences getPreferences(){ return preferences;}

    public void setPreferences(MoviescraperPreferences newPreferences) {
        preferences = newPreferences;
        savePreferences();
    }

    public Optional<MoviescraperPreferences> loadPreferences() throws IOException {

        if (!Files.exists(configFile))
            return Optional.empty();

        try (FileInputStream fis = new FileInputStream(configFile.toFile())) {
            return Optional.of(MAPPER.readValue(fis, MoviescraperPreferences.class));
        }
    }

    public void savePreferences() {
        if(degradedMode || preferences == null)
            return;

        try (FileOutputStream osw = new FileOutputStream(configFile.toFile())) {
            var data = MAPPER.writeValueAsBytes(preferences);
            osw.write(data);
        } catch (IOException e) {
            log.error("WARNING: Failed to save preferences.json; Disabling auto-save to protect against further errors.", e);

            SwingUtilities.invokeLater(() -> {

                JOptionPane.showMessageDialog(null, "WARNING: Failed to save preferences.json; Disabling auto-save to protect against further errors.", "WARNING", JOptionPane.WARNING_MESSAGE);
            });
            degradedMode = true;
        }
    }
}
