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
import com.facebook.common.logging.FLog;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.listener.RequestListener;
import com.facebook.imagepipeline.listener.RequestLoggingListener;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

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
        final OkHttpClient mOkHttpClient = new OkHttpClient();
        final Request request = new Request.Builder()
                .url(GET_NEWS_URL)
                .build();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(mContext, "网络错误！", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String htmlStr = response.body().string();
                newsBeanArray = NewsUtil.parseNewsJSON(mContext, htmlStr);
                Message message = new Message();
                message.what = 0;
                handler.sendMessage(message);
            }
        });
    }
}
