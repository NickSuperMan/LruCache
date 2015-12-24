package com.example.ceo.lrucache.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.LruCache;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.ceo.lrucache.R;
import com.example.ceo.lrucache.adapter.MyListViewAdapter;
import com.example.ceo.lrucache.libcore.io.DiskLruCache;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by 123 on 2015/9/7.
 */
public class ImageLoader {

    private ListView listView;
    private LruCache<String, Bitmap> lruCache;
    private Set<MyAsyncTask> mTask;
    private DiskLruCache mDiskLruCache;

    public ImageLoader(Context context, ListView listView) {
        this.listView = listView;
        mTask = new HashSet<MyAsyncTask>();
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int size = maxMemory / 8;
        lruCache = new LruCache<String, Bitmap>(size) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount();
            }
        };
        try {
            File cacheDir = CacheUtils.getDiskCacheDir(context, "img");
            if (!cacheDir.exists()) {
                cacheDir.mkdirs();
            }
            mDiskLruCache = DiskLruCache.open(cacheDir, CacheUtils.getAppVersion(context), 1, 8 * 1024 * 1024);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addBitmapToCache(String url, Bitmap value) {
        if (getBitmapFromCache(url) == null) {
            lruCache.put(url, value);
        }
    }


    private Bitmap getBitmapFromCache(String url) {
        return lruCache.get(url);
    }


    public void showImage(ImageView imageView, String url) {
        Bitmap bitmap = getBitmapFromCache(url);
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        } else {
            imageView.setImageResource(R.mipmap.ic_launcher);
        }
    }

    public void loadImages(int mStart, int mEnd) {
        for (int i = mStart; i < mEnd; i++) {
            String url = MyListViewAdapter.URLS[i];
            Bitmap bitmap = getBitmapFromCache(url);
            if (bitmap == null) {
                MyAsyncTask task = new MyAsyncTask();
                task.execute(url);
                mTask.add(task);
            } else {
                ImageView imageView = (ImageView) listView.findViewWithTag(url);
                imageView.setImageBitmap(bitmap);
            }
        }
    }

    class MyAsyncTask extends AsyncTask<String, Void, Bitmap> {

        private String uRl;

        @Override
        protected Bitmap doInBackground(String... params) {
            this.uRl = params[0];
            FileDescriptor fileDescriptor = null;
            FileInputStream fileInputStream = null;
            DiskLruCache.Snapshot snapshot = null;
            try {
                final String key = CacheUtils.hashKeyForDisk(uRl);
                snapshot = mDiskLruCache.get(key);
                if (snapshot == null) {
                    DiskLruCache.Editor editor = mDiskLruCache.edit(key);
                    if (editor != null) {
                        OutputStream outputStream = editor.newOutputStream(0);
                        if (loadImage(uRl, outputStream)) {
                            editor.commit();
                        } else {
                            editor.abort();
                        }
                    }
                }
                snapshot = mDiskLruCache.get(key);

                if (snapshot != null) {
                    fileInputStream = (FileInputStream) snapshot.getInputStream(0);
                    fileDescriptor = fileInputStream.getFD();
                }

                Bitmap bitmap = null;
                if (fileDescriptor != null) {
                    bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor);
                }

                if (bitmap != null) {
                    addBitmapToCache(uRl, bitmap);
                }
                return bitmap;
            } catch (IOException e) {
                e.printStackTrace();

            } finally {
                if (fileDescriptor == null && fileInputStream != null) {
                    try {
                        fileInputStream.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            ImageView mImageView = (ImageView) listView.findViewWithTag(uRl);
            if (mImageView != null && bitmap != null) {
                mImageView.setImageBitmap(bitmap);
            }
            mTask.remove(this);
        }
    }

    private boolean loadImage(String url, OutputStream outputStream) {
        BufferedInputStream in = null;
        HttpURLConnection conn = null;
        BufferedOutputStream out = null;
        try {
            URL url1 = new URL(url);
            conn = (HttpURLConnection) url1.openConnection();
            in = new BufferedInputStream(conn.getInputStream());
            out = new BufferedOutputStream(outputStream);

            int b;
            while ((b = in.read()) != -1) {
                out.write(b);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {

                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public void cancelAllTasks() {
        if (mTask != null) {
            for (MyAsyncTask task : mTask) {
                task.cancel(false);
            }
        }
    }
}
