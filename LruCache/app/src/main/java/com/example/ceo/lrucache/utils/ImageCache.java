package com.example.ceo.lrucache.utils;

import android.graphics.Bitmap;

/**
 * Created by roy on 2015/12/31.
 */
public interface ImageCache {
    void put(String url, Bitmap bitmap);

    Bitmap get(String url);
}
