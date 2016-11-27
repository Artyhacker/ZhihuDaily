package com.dh.zhihudaily;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import com.dh.zhihudaily.bean.NewsContentBean;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.StoreHouseHeader;
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
    @BindView(R.id.news_ptr_frame)
    PtrFrameLayout newsPtrFrame;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_news);
        ButterKnife.bind(this);
        mContext = this;

        WebSettings settings = wvNewsContent.getSettings();
        settings.setJavaScriptEnabled(true);

        newsContentBean = new NewsContentBean();
        newsAtUrl = getIntent().getStringExtra("url");
        //Log.d("News", newsAtUrl);
        getNewsContent(newsAtUrl);

        newsPtrFrame.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame,content,header);
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                getNewsContent(newsAtUrl);
                newsPtrFrame.refreshComplete();
            }
        });

    }

    private void setNewsView() {
        svNewsImage.setImageURI(Uri.parse(newsContentBean.image));
        tvNewsTitle.setText(newsContentBean.title);
        tvNewsImageSource.setText(newsContentBean.imageSource);

        String htmlData = "<link rel=\"stylesheet\" type=\"text/css\" href=\"" + newsContentBean.cssUrl + "\" />"
                +newsContentBean.body;
        wvNewsContent.loadDataWithBaseURL(null, htmlData, "text/html","UTF-8", null);
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
            String cssUrl = object.getJSONArray("css").getString(0);

            newsContentBean.body = body;
            newsContentBean.id = id;
            newsContentBean.image = image;
            newsContentBean.imageSource = image_source;
            newsContentBean.title = title;
            newsContentBean.shareUrl = share_url;
            newsContentBean.cssUrl = cssUrl;

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
