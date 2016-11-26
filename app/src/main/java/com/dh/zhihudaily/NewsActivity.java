package com.dh.zhihudaily;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import com.dh.zhihudaily.bean.NewsContentBean;
import com.dh.zhihudaily.utils.NewsUtil;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by dh on 16-11-25.
 */
public class NewsActivity extends AppCompatActivity {
    private Context mContext;
    private String newsAtUrl = "";
    private String newsContent = "";
    private NewsContentBean newsContentBean;

    @BindView(R.id.sv_news_image)
    SimpleDraweeView svNewsImage;
    @BindView(R.id.tv_news_title)
    TextView tvNewsTitle;
    @BindView(R.id.tv_news_image_source)
    TextView tvNewsImageSource;
    @BindView(R.id.wv_news_content)
    WebView wvNewsContent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_news);
        ButterKnife.bind(this);
        mContext = this;

        newsContentBean = new NewsContentBean();
        newsAtUrl = getIntent().getStringExtra("url");
        getNewsContent(newsAtUrl);


    }

    private void setNewsView() {
        svNewsImage.setImageURI(Uri.parse(newsContentBean.image));
        tvNewsTitle.setText(newsContentBean.title);
        tvNewsImageSource.setText(newsContentBean.imageSource);
        wvNewsContent.loadDataWithBaseURL(null, newsContentBean.body, "text/html", "UTF-8", null);
    }

    private void getNewsContent(String newsAtUrl) {
        final OkHttpClient mOkHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url(newsAtUrl)
                .build();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(mContext, "网络错误！", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                newsContent = response.body().string();
                parseNewsContentJSON(newsContent);
                if(!newsContentBean.id.isEmpty()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setNewsView();
                        }
                    });
                }
            }
        });
    }

    private void parseNewsContentJSON(String newsContent) {
        try {
            JSONObject object = new JSONObject(newsContent);
            String body = object.getString("body");
            String image_source = object.getString("image_source");
            String image = object.getString("image");
            String share_url = object.getString("share_url");
            String id = object.getString("id");
            String title = object.getString("title");

            newsContentBean.body = body;
            newsContentBean.id = id;
            newsContentBean.image = image;
            newsContentBean.imageSource = image_source;
            newsContentBean.title = title;
            newsContentBean.shareUrl = share_url;

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
