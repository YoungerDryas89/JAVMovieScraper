package moviescraper.doctord.model;

import javafx.util.Pair;

import java.awt.*;
import java.lang.ref.SoftReference;
import java.net.URI;
import java.time.Instant;

public class ImageData {
    SoftReference<? extends Image> data;
    Pair<Integer, Integer> dimensions;
    ImageData parent;
    String hash;
    Instant lastAccessed;
    Instant created;
    long size;
    URI origin;
}
