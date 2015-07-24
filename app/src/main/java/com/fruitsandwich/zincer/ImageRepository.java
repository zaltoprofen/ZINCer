package com.fruitsandwich.zincer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.google.common.collect.Maps;

import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by nakac on 15/07/22.
 */
public class ImageRepository {
    private final Map<String, Future<Bitmap>> images = Maps.newHashMap();
    private final ExecutorService executor = Executors.newFixedThreadPool(2);

    public Future<Bitmap> getImageFuture(String url) {
        boolean exists;
        synchronized (images) {
            exists = images.containsKey(url);
            if (exists) return images.get(url);
            Future<Bitmap> future = executor.submit(new LoadTask(url));
            images.put(url, future);
            return future;
        }
    }

    private class LoadTask implements Callable<Bitmap> {
        private final String url;
        private static final String TAG = "LoadTask";

        public LoadTask(String url) {
            this.url = url;
            Log.d(TAG, "stand by: " + url);
        }

        @Override
        public Bitmap call() throws Exception {
            InputStream is = new URL(this.url).openStream();
            Log.d(TAG, "finished load image:" + url);
            return BitmapFactory.decodeStream(is);
        }
    }
}
