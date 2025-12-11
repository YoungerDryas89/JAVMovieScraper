package com.github.youngerdryas89.moviescraper.model;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.youngerdryas89.moviescraper.controller.FileDownloaderUtilities;
public class ImageCache {
	private static final int initialCapacity = 200;

	private static final Cache<URL, Image> cache = Caffeine.newBuilder()
			.expireAfterAccess(3, TimeUnit.HOURS)
			.initialCapacity(initialCapacity)
			.build();
	private static final Cache<URL, Image> modifiedImageCache = Caffeine.newBuilder()
			.expireAfterAccess(3, TimeUnit.HOURS)
			.initialCapacity(initialCapacity)
			.build();


	public static Image getImageFromCache(URL url, boolean isImageModified, URL referrerURL) throws IOException {
		Cache<URL, Image> cacheToUse = isImageModified ? modifiedImageCache : cache;
		var mapToUse = isImageModified ? modifiedImageCache.asMap() : cache.asMap();


		//Cache already contains the item, so just return it
		if (mapToUse.containsKey(url)) {
			return cacheToUse.getIfPresent(url);
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
				System.out.println("We ran out of memory..clearing the cache. It was size " + cache.estimatedSize() + " before the clear");
				cacheToUse.cleanUp();
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
		Cache<URL, Image> cacheToUse = isImageModified ? modifiedImageCache : cache;
		//300 is arbitrary for now, but at some point we gotta boot stuff from cache or we will run out memory
		//Ideally, I would boot out old items first, but I would need a new data structure to do this, probably
		//by using a library already written that handles all the cache stuff rather than just using a map like I'm doing
		//in this class

		cacheToUse.put(url, image);
	}

	private static Image createBlankImage() {
		return new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
	}

	public static void removeImageFromCache(URL url, boolean isImageModified) {
		Cache<URL, Image> cacheToUse = isImageModified ? modifiedImageCache : cache;
		if(cacheToUse.asMap().containsKey(url))
			cacheToUse.invalidate(url);
	}

	public static boolean isImageCached(URL url, boolean isImageModified) {
		Cache<URL, Image> cacheToUse = isImageModified ? modifiedImageCache : cache;
		return cacheToUse.asMap().containsKey(url);
	}

    public static void replaceIfPresent(URL url, Image image) {
        if(cache.asMap().containsKey(url)){
			cache.invalidate(url);
			cache.put(url, image);
        }
    }

    public static void clearAllOf(URL url){
        modifiedImageCache.invalidate(url);
        cache.invalidate(url);
    }
}
