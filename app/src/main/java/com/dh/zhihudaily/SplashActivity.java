package com.dh.zhihudaily;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.dh.zhihudaily.utils.StreamUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SplashActivity extends AppCompatActivity {

    private static String splashUrl = "http://news-at.zhihu.com/api/4/start-image/1080*1920";
    private String splashText = "";
    private String splashImageUrl = "";

    @BindView(R.id.iv_splash_splash)
    ImageView ivSplash;
    @BindView(R.id.tv_splash_text)
    TextView tvSplash;
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);

        new Thread(new Runnable() {

            private HttpURLConnection connection;

            @Override
            public void run() {
                try {
                    URL url = new URL(splashUrl);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(5000);
                    connection.setReadTimeout(5000);
                    InputStream is = connection.getInputStream();
                    String response = StreamUtil.streamToString(is);
                    parseSplashJson(response);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tvSplash.setText("©" + splashText);
                            ivSplash.setImageBitmap(bitmap);
                        }
                    });
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    connection.disconnect();
                }
            }
        }).start();

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

    private void parseSplashJson(String response) {
        try {
            JSONObject object = new JSONObject(response);
            splashText = object.getString("text");
            splashImageUrl = object.getString("img");
            bitmap = BitmapFactory.decodeStream(new URL(splashImageUrl).openConnection().getInputStream());

            //getBitmap(splashImageUrl);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
