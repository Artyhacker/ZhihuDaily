package com.dh.zhihudaily;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
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

        WebSettings settings = wvNewsContent.getSettings();
        //settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        settings.setJavaScriptEnabled(true);
        //settings.setUseWideViewPort(true);
        //settings.setLoadWithOverviewMode(true);
        //settings.setSupportZoom(false);
        //settings.setDisplayZoomControls(false);
        //settings.supportMultipleWindows();
        //settings.setSupportMultipleWindows(true);

        //http://news-at.zhihu.com/api/4/news/9007077
        newsContentBean = new NewsContentBean();
        newsAtUrl = getIntent().getStringExtra("url");
        Log.d("News", newsAtUrl);
        getNewsContent(newsAtUrl);


    }

    private void setNewsView() {
        svNewsImage.setImageURI(Uri.parse(newsContentBean.image));
        tvNewsTitle.setText(newsContentBean.title);
        tvNewsImageSource.setText(newsContentBean.imageSource);

        String htmlData = "<link rel=\"stylesheet\" type=\"text/css\" href=\"" + newsContentBean.cssUrl + "\" />"
                +newsContentBean.body;
        //Log.d("News", "css: " + newsContentBean.cssUrl);
        wvNewsContent.loadDataWithBaseURL(null, htmlData, "text/html","UTF-8", null);
        //setWebView();
    }

    /*
    private void setWebView() {

        setUpWebViewDefaults(wvNewsContent);
        wvNewsContent.setWebViewClient(mWebViewClient);
    }

    @SuppressLint("NewApi")
    private WebViewClient mWebViewClient = new WebViewClient(){
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(url));
            startActivity(intent);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);

        }
    }

    private void setUpWebViewDefaults(WebView wvNewsContent) {

        wvNewsContent.addJavascriptInterface(new JavascriptObject(mContext), "injectedObject");
        WebSettings settings = wvNewsContent.getSettings();

        //设置缓存模式
        settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        settings.setJavaScriptEnabled(true);

        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        wvNewsContent.setVerticalScrollBarEnabled(false);
        wvNewsContent.setHorizontalScrollBarEnabled(false);

        //支持通过JS打开新的窗口
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        wvNewsContent.setWebChromeClient(new WebChromeClient(){
            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                result.cancel();
                return true;
            }

            @Override
            public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
                return true;
            }
        });
    }*/

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
/*
    private class JavascriptObject {
        private Activity mInstance;
        public JavascriptObject(Activity instance) {
            mInstance = instance;
        }
        @JavascriptInterface
        public void openImage(String uri){
            if (mInstance != null && !mInstance.isFinishing()) {
                Intent intent = new Intent(mInstance, NewsDetailImageActivity.class);
                intent.putExtra("imageUrl", url);
                mInstance.startActivity(intent);
            }
        }
    }*/
}
