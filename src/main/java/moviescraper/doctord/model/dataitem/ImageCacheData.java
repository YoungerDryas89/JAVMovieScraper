package moviescraper.doctord.model.dataitem;

import org.apache.commons.math3.util.Pair;

import java.awt.*;

public class ImageCacheData {
    private Image image;
    private Image preview;
    private Pair<Integer, Integer> croppedCoordinates;
    private Pair<Integer, Integer> dimensions;
}
