package com.example.ceo.lrucache.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.ceo.lrucache.libcore.io.DiskLruCache;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.OutputStream;

/**
 * Created by roy on 2015/12/31.
 */
public class DiskCache implements ImageCache {

    private DiskLruCache mDiskLruCache;

    public DiskCache(Context context) {

        try {
            File cacheDir = CacheUtils.getDiskCacheDir(context, "image");

            if (!cacheDir.exists()) {
                cacheDir.mkdirs();
            }
            mDiskLruCache = DiskLruCache.open(cacheDir, CacheUtils.getAppVersion(context), 1, 8 * 1024 * 1024);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void put(String url, Bitmap bitmap) {

        DiskLruCache.Snapshot snapshot = null;

        try {
            final String key = CacheUtils.hashKeyForDisk(url);
            snapshot = mDiskLruCache.get(key);
            if (snapshot == null) {
                DiskLruCache.Editor editor = mDiskLruCache.edit(key);
                if (editor != null) {
                    OutputStream outputStream = editor.newOutputStream(0);
                    if (bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)) {
                        editor.commit();
                    } else {
                        editor.abort();
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public Bitmap get(String url) {
        FileDescriptor fileDescriptor = null;
        FileInputStream fileInputStream = null;

        DiskLruCache.Snapshot snapshot = null;

        String key = CacheUtils.hashKeyForDisk(url);

        try {
            snapshot = mDiskLruCache.get(key);

            if (snapshot != null) {
                fileInputStream = (FileInputStream) snapshot.getInputStream(0);
                fileDescriptor = fileInputStream.getFD();
            }

            Bitmap bitmap = null;
            if (fileDescriptor != null) {
                bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor);
            }

            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fileDescriptor == null && fileInputStream != null) {
                CloseUtils.closeQuietly(fileInputStream);
            }
        }

        return null;
    }
}
