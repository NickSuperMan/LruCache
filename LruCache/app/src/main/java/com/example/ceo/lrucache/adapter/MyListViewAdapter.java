package com.example.ceo.lrucache.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.ceo.lrucache.R;
import com.example.ceo.lrucache.bean.News;
import com.example.ceo.lrucache.utils.ImageLoader2;
import com.example.ceo.lrucache.utils.MemoryCache;

import java.util.List;

/**
 * Created by 123 on 2015/9/7.
 */
public class MyListViewAdapter extends BaseAdapter implements AbsListView.OnScrollListener {


    private List<News> mData;
    private LayoutInflater mInflater;
    private ImageLoader2 mImageLoader;
    public static String[] URLS;
    private int mStart, mEnd;
    private boolean isFirstStart;

    public MyListViewAdapter(Context context, List<News> news, ListView listView) {
        mInflater = LayoutInflater.from(context);
        this.mData = news;
        mImageLoader = new ImageLoader2(context, listView);
        mImageLoader.setmImageCache(new MemoryCache());
        URLS = new String[mData.size()];
        for (int i = 0; i < mData.size(); i++) {
            URLS[i] = mData.get(i).getImg_url();
        }
        listView.setOnScrollListener(this);
        isFirstStart = true;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_layout, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.imageView);
            viewHolder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
            viewHolder.tv_content = (TextView) convertView.findViewById(R.id.tv_content);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.imageView.setImageResource(R.mipmap.ic_launcher);
        String url = mData.get(position).getImg_url();
        viewHolder.imageView.setTag(url);
        mImageLoader.showImage(viewHolder.imageView, url);
        viewHolder.tv_title.setText(mData.get(position).getTitle());
        viewHolder.tv_content.setText(mData.get(position).getContent());

        return convertView;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollState == SCROLL_STATE_IDLE) {
            mImageLoader.loadImages(mStart, mEnd);
        } else {
            mImageLoader.cancelAllTasks();
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        mStart = firstVisibleItem;
        mEnd = firstVisibleItem + visibleItemCount;
        //??????????????
        if (isFirstStart && visibleItemCount > 0) {
            mImageLoader.loadImages(mStart, mEnd);
            isFirstStart = false;
        }
    }


    class ViewHolder {

        ImageView imageView;
        TextView tv_content;
        TextView tv_title;
    }
}
