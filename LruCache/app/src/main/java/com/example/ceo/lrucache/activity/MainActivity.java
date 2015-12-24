package com.example.ceo.lrucache.activity;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ListView;

import com.example.ceo.lrucache.R;
import com.example.ceo.lrucache.adapter.MyListViewAdapter;
import com.example.ceo.lrucache.bean.News;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity {

    private ListView listView;
    private static String URL = "http://www.imooc.com/api/teacher/?type=4&num=30";
    private MyListViewAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.listview);

        new MyAsyncTask().execute(URL);
    }

    private List<News> getJsonData(String url) {
        List<News> list = new ArrayList<>();
        try {
            URL uRL = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) uRL.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            int code = conn.getResponseCode();
            if (code == 200) {

                InputStream in = conn.getInputStream();
                String jsonString = readStream(in);
                JSONObject jsonObject = null;
                News news = null;
                jsonObject = new JSONObject(jsonString);
                JSONArray jsonArray = jsonObject.getJSONArray("data");
                for (int i = 0; i < jsonArray.length(); i++) {
                    jsonObject = jsonArray.getJSONObject(i);
                    news = new News();
                    news.setImg_url(jsonObject.getString("picSmall"));
                    news.setContent(jsonObject.getString("name"));
                    news.setTitle(jsonObject.getString("description"));
                    list.add(news);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    private String readStream(InputStream in) {
        InputStreamReader isr;
        String result = "";
        try {
            isr = new InputStreamReader(in, "utf-8");
            String line = "";
            BufferedReader bufferedReader = new BufferedReader(isr);
            while ((line = bufferedReader.readLine()) != null) {
                result += line;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    class MyAsyncTask extends AsyncTask<String, Void, List<News>> {

        @Override
        protected List<News> doInBackground(String... params) {
            List<News> data = getJsonData(params[0]);
            return data;
        }

        @Override
        protected void onPostExecute(List<News> news) {
            super.onPostExecute(news);
            mAdapter = new MyListViewAdapter(MainActivity.this, news,listView);
            listView.setAdapter(mAdapter);
        }
    }
}
