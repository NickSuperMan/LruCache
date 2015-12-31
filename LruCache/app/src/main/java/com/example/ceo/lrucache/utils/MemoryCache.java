package com.example.ceo.lrucache.utils;

import android.graphics.Bitmap;
import android.util.LruCache;

/**
 * Created by roy on 2015/12/31.
 */
public class MemoryCache implements ImageCache {

    private LruCache<String, Bitmap> mLruCache;

    public MemoryCache() {

        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int size = maxMemory / 8;

        mLruCache = new LruCache<String, Bitmap>(size) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getRowBytes() * value.getHeight() / 1024;
            }
        };
    }

    @Override
    public void put(String url, Bitmap bitmap) {
        if (mLruCache.get(url) == null) {
            mLruCache.put(url, bitmap);
        }
    }

    @Override
    public Bitmap get(String url) {
        return mLruCache.get(url);
    }
}
