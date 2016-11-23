package com.dh.zhihudaily;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.renderscript.ScriptGroup;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;

import com.dh.zhihudaily.adapter.NewsAdapter;
import com.dh.zhihudaily.bean.NewsBean;
import com.dh.zhihudaily.utils.StreamUtil;
import com.dh.zhihudaily.utils.UrlUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by dh on 16-11-22.
 */

public class MainActivity extends AppCompatActivity {

    private ArrayList<NewsBean> newsBeanArray = new ArrayList<>();
    private static final String GET_NEWS_URL = "http://news-at.zhihu.com/api/4/news/latest";
    private String date = "";
    @BindView(R.id.lv_main_news)
    ListView lvNewsList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        getNewsContent();

        NewsAdapter adapter = new NewsAdapter(this, newsBeanArray);
        lvNewsList.setAdapter(adapter);
    }

    private void getNewsContent() {
        new Thread(new Runnable() {
            HttpURLConnection conn = null;
            @Override
            public void run() {
                try {
                    URL url = new URL(GET_NEWS_URL);
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setConnectTimeout(5000);
                    conn.setReadTimeout(5000);
                    InputStream is = conn.getInputStream();
                    String newsResponse = StreamUtil.streamToString(is);
                    parseJSONNews(newsResponse);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void parseJSONNews(String response) {
        try {
            JSONObject object = new JSONObject(response);
            date = object.getString("date");
            //Log.d("Main", date);
            JSONArray stories = object.getJSONArray("stories");
            //Log.d("Main", stories.get(0).toString());
            for(int i = 0; i < stories.length(); i++) {
                JSONObject story = (JSONObject) stories.get(i);
                final String imagesString = story.getString("images");
                final String id = story.getString("id");
                final String title = story.getString("title");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Bitmap images = null;
                        try {
                            images = UrlUtil.urlToBitmap(imagesString);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        NewsBean bean = new NewsBean(id, title, images);
                        newsBeanArray.add(bean);
                    }
                }).start();

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
