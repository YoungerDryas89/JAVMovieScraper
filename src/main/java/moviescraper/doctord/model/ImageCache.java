package moviescraper.doctord.model;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.URL;
import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javafx.util.Pair;
import moviescraper.doctord.controller.FileDownloaderUtilities;
import org.apache.commons.codec.digest.DigestUtils;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;

public class ImageCache {
	private static final int initialCapacity = 100;
	private static long cacheSize = 0;
	private static LinkedHashMap<String, ImageData> icache = LinkedHashMap.newLinkedHashMap(100);
	private static Map<URL, Image> cache = Collections.synchronizedMap(new HashMap<URL, Image>(initialCapacity));
	private static Map<URL, Image> modifiedImageCache = Collections.synchronizedMap(new HashMap<URL, Image>(initialCapacity));


	@Nullable
	public static ImageData cacheImage(InputStream istream) throws IOException {
		return cacheImage(ImageIO.read(istream));
	}

	@Nullable
	public static ImageData cacheImage(BufferedImage image){
		try {

			DataBufferByte bbuff = (DataBufferByte)image.getData().getDataBuffer();
			String hash = DigestUtils.sha256Hex(bbuff.getData());
			if(icache.containsKey(hash)){
				return icache.get(hash);
			}

			ImageData imgData = new ImageData();

			imgData.hash = hash;
			imgData.dimensions = new Pair<>(image.getHeight(), image.getHeight());

			long estimatedSize = ((long) image.getWidth() * image.getHeight() * image.getColorModel().getPixelSize()) / 8;
			cacheSize += estimatedSize;
			imgData.size = estimatedSize;

			imgData.created = Instant.now();
			imgData.lastAccessed = Instant.now();

			imgData.data = new SoftReference<>(image);

			icache.put(hash, imgData);

			return imgData;

		} catch (OutOfMemoryError e){
			System.err.println("Out of memory! Clearing image cache and calling the GC");
			System.err.println("Cache size prior to deletion: " + icache.size());

			icache.clear();
			System.out.println("Cache cleared.");

			System.gc();
			System.out.println("System GC called.");

		}
		return null;
	}
	public static Image getImageFromCache(URL url, boolean isImageModified, URL referrerURL) throws IOException {
		Map<URL, Image> cacheToUse = isImageModified ? modifiedImageCache : cache;

		//Cache already contains the item, so just return it
		if (cacheToUse.containsKey(url)) {
			return cacheToUse.get(url);
		}
		//we didn't find it, so read the Image into the cache and also return it
		else {
			try {
				if (url != null) {
					Image imageFromUrl = FileDownloaderUtilities.getImageFromUrl(url, referrerURL);
					if (imageFromUrl != null) {
						cacheToUse.put(url, imageFromUrl);
						return imageFromUrl;
					}
				}

				//we couldn't read in the image from the URL so just return a blank image

				Image blankImage = createBlankImage();
				cacheToUse.put(url, blankImage);
				return blankImage;
			} catch (OutOfMemoryError e) {
				System.out.println("We ran out of memory..clearing the cache. It was size " + cache.size() + " before the clear");
				cacheToUse.clear();
				System.gc();
				return FileDownloaderUtilities.getImageFromUrl(url);
			} catch (IOException e) {
				e.printStackTrace();
				Image blankImage = createBlankImage();
				cacheToUse.put(url, blankImage);
				return blankImage;
			}
		}
	}

	public static void putImageInCache(URL url, Image image, boolean isImageModified) {
		Map<URL, Image> cacheToUse = isImageModified ? modifiedImageCache : cache;
		//300 is arbitrary for now, but at some point we gotta boot stuff from cache or we will run out memory
		//Ideally, I would boot out old items first, but I would need a new data structure to do this, probably
		//by using a library already written that handles all the cache stuff rather than just using a map like I'm doing
		//in this class

		//TODO: purge old items instead of the whole world. get a real cache library from a 3rd party
		if (cacheToUse.size() > 300) {
			System.out.println("Clearing cache - cache had " + cacheToUse.size() + " items before clearing.");
			cacheToUse.clear();
		}
		cacheToUse.put(url, image);
	}

	private static Image createBlankImage() {
		return new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
	}

	public static void removeImageFromCache(URL url, boolean isImageModified) {
		Map<URL, Image> cacheToUse = isImageModified ? modifiedImageCache : cache;
		cacheToUse.remove(url);
	}

	public static boolean isImageCached(URL url, boolean isImageModified) {
		Map<URL, Image> cacheToUse = isImageModified ? modifiedImageCache : cache;
		return cacheToUse.containsKey(url);
	}
}
