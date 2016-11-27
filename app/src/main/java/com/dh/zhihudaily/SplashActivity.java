package com.dh.zhihudaily;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dh.zhihudaily.utils.StreamUtil;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SplashActivity extends AppCompatActivity {

    private final static String SPLASH_URL = "http://news-at.zhihu.com/api/4/start-image/1080*1920";
    private String splashText = "";
    private String splashImageUrl = "";
    private SharedPreferences sp;

    @BindView(R.id.iv_splash_splash)
    SimpleDraweeView ivSplash;
    @BindView(R.id.tv_splash_text)
    TextView tvSplash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_splash);

        ButterKnife.bind(this);

        sp = getSharedPreferences("newsSplash", MODE_PRIVATE);
        if(!sp.getString("text","").isEmpty())
            getSplashViewForLocal();

        getSplashViewForInternet();

        new Thread(new Runnable() {
            @Override
            public void run() {
                SystemClock.sleep(2000);
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }).start();
    }

    private void getSplashViewForLocal() {
        tvSplash.setText(sp.getString("text",""));
        ivSplash.setImageURI(Uri.parse(sp.getString("url","")));
    }

    private void getSplashViewForInternet() {
        OkHttpClient mOkHttpClient = new OkHttpClient();
        final Request request = new Request.Builder()
                .url(SPLASH_URL)
                .build();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String httpStr = response.body().string();
                parseSplashJson(httpStr);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvSplash.setText("©" + splashText);
                        ivSplash.setImageURI(Uri.parse(splashImageUrl));
                    }
                });
            }
        });
    }

    private void parseSplashJson(String response) {
        try {
            JSONObject object = new JSONObject(response);
            splashText = object.getString("text");
            splashImageUrl = object.getString("img");
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("text", splashText);
            editor.putString("url", splashImageUrl);
            editor.commit();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
