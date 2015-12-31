package com.example.ceo.lrucache.utils;

import android.content.Context;
import android.graphics.Bitmap;

/**
 * Created by roy on 2015/12/31.
 */
public class DoubleCache implements ImageCache {

    ImageCache mMemoryCache;
    ImageCache mDiskCache;

    public DoubleCache(Context context) {
        mMemoryCache = new MemoryCache();
        mDiskCache = new DiskCache(context);
    }

    @Override
    public void put(String url, Bitmap bitmap) {
        if (mMemoryCache.get(url) == null) {
            mMemoryCache.put(url, bitmap);
        }
        if (mDiskCache.get(url) == null) {
            mDiskCache.put(url, bitmap);
        }

    }

    @Override
    public Bitmap get(String url) {

        Bitmap bitmap = mMemoryCache.get(url);
        if (bitmap == null) {
            bitmap = mDiskCache.get(url);
        }
        return bitmap;
    }
}
