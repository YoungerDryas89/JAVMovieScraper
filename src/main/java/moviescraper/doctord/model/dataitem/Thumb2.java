package moviescraper.doctord.model.dataitem;

import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.lang.ref.SoftReference;
import java.net.URL;
import java.util.Collections;
import java.util.concurrent.ConcurrentLinkedDeque;

public class Thumb2 extends MovieDataItem {
    private URL source;

    private boolean hasModifiedChildren = false;
    private boolean isLoaded = false;
    private boolean needsReload = false;

    private SoftReference<? extends Image> image;
    private SoftReference<? extends Thumb2> previewImage;
    private SoftReference<? extends ImageIcon> iconImage;

    private ConcurrentLinkedDeque<Thumb2> modifiedChildren = new ConcurrentLinkedDeque<>();
    private Thumb2 parent = null;

    private Pair<Integer, Integer> dimensions;
    private Pair<Integer, Integer> croppedCoordinates;

    public Thumb2(URL urlSource){
        this.source =  urlSource;
    }

    public Thumb2(BufferedImage data, Thumb2 parentImage) {

    }

    public URL url() { return source; }
    public boolean isDerived() { return parent != null; }
    public boolean hasModifiedChildren() { return !modifiedChildren.isEmpty(); }
    public boolean isLoaded() { return  isLoaded; }
    public boolean needsReload() { return needsReload; }
    public boolean loadedFromDisk() { return source.getProtocol().startsWith("file"); }
    @Nullable
    public Thumb2 parent() { return parent; }
    public int childrenSize() { return modifiedChildren.size(); }
    public ConcurrentLinkedDeque<Thumb2> children() { return modifiedChildren; }
    @Nullable
    public Pair<Integer, Integer> croppedCoordinates() { return croppedCoordinates; }
    @Nullable
    public Pair<Integer, Integer> dimensions() { return dimensions; }


    public Thumb2 createCroppedDerivation(int x, int y, int height, int width){
    }



    @Override
    public String toXML() {
        return "<thumb>" + source.getPath() + "</thumb>";
    }

    @Override
    public String toString() {
        return "Thumb [thumbURL=" + source + "\"" + dataItemSourceToString() + "]";
    }
}
