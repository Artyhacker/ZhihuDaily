package com.dh.zhihudaily;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.Toast;

import com.dh.zhihudaily.adapter.NewsAdapter;
import com.dh.zhihudaily.bean.NewsBean;
import com.dh.zhihudaily.utils.NewsUtil;
import com.dh.zhihudaily.utils.StreamUtil;
import com.facebook.drawee.backends.pipeline.Fresco;

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
    private Context mContext;
    private NewsAdapter adapter;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            adapter = new NewsAdapter(mContext, newsBeanArray);
            lvNewsList.setAdapter(adapter);
        }
    };
    //private String date = "";
    @BindView(R.id.lv_main_news)
    ListView lvNewsList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Fresco.initialize(this);
        mContext = this;

        newsBeanArray = NewsUtil.getNewsForDatabase(mContext);
        if(newsBeanArray != null && newsBeanArray.size() > 0) {
            adapter = new NewsAdapter(mContext, newsBeanArray);
            lvNewsList.setAdapter(adapter);
        }
        getNewsForInternet();
    }

    private void getNewsForInternet() {
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
                    int responseCode = conn.getResponseCode();
                    if(responseCode == 200) {
                        InputStream is = conn.getInputStream();
                        String response = StreamUtil.streamToString(is);
                        newsBeanArray = NewsUtil.parseNewsJSON(mContext, response);
                        Message message = new Message();
                        message.what = 0;
                        handler.sendMessage(message);
                    } else {
                        Toast.makeText(getApplicationContext(),"网络错误!",Toast.LENGTH_SHORT).show();
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if(conn != null)
                        conn.disconnect();
                }
            }
        }).start();

    }

}
