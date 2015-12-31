package com.example.ceo.lrucache.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.ceo.lrucache.R;
import com.example.ceo.lrucache.adapter.MyListViewAdapter;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by roy on 2015/12/31.
 *
 */
public class ImageLoader2 {

    private ImageCache mImageCache;
    private ListView listView;
    private Set<MyAsyncTask> mTask;

    public ImageLoader2(Context context, ListView listView) {
//        mImageCache = new DoubleCache(context);
        this.listView = listView;
        mTask = new HashSet<>();
    }

    public void setmImageCache(ImageCache mImageCache) {
        this.mImageCache = mImageCache;
    }

    public void showImage(ImageView imageView, String url) {
        Bitmap bitmap = mImageCache.get(url);
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
            return;
        } else {
            imageView.setImageResource(R.mipmap.ic_launcher);
        }
    }

    public void loadImages(int mStart, int mEnd) {
        for (int i = mStart; i < mEnd; i++) {
            String url = MyListViewAdapter.URLS[i];
            Bitmap bitmap = mImageCache.get(url);
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

        private String url;

        @Override
        protected Bitmap doInBackground(String... params) {
            this.url = params[0];
            Log.e("roy", url);
            Bitmap bitmap = downLoadImage(url);
            mImageCache.put(url, bitmap);
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            ImageView imageView = (ImageView) listView.findViewWithTag(url);

            if (imageView != null && bitmap != null) {
                imageView.setImageBitmap(bitmap);
                Log.e("roy", "aaaaaaaaaaaaaa");
            }
            mTask.remove(this);
        }
    }

    private Bitmap downLoadImage(String url) {

        HttpURLConnection conn = null;

        try {
            URL url1 = new URL(url);
            conn = (HttpURLConnection) url1.openConnection();
//             bitmap = null;


            InputStream inputStream = conn.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void cancelAllTasks() {
        if (mTask != null) {
            for (MyAsyncTask task : mTask) {
                task.cancel(false);
            }
        }
    }
}
