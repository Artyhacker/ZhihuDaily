package com.dh.zhihudaily;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.dh.zhihudaily.adapter.NewsAdapter;
import com.dh.zhihudaily.bean.NewsBean;
import com.dh.zhihudaily.utils.NewsUtil;
import com.facebook.drawee.backends.pipeline.Fresco;

import java.io.IOException;
import java.util.ArrayList;

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
 * Created by dh on 16-11-22.
 */

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private ArrayList<NewsBean> newsBeanArray = new ArrayList<>();
    private static final String GET_NEWS_URL = "http://news-at.zhihu.com/api/4/news/latest";
    private static final String NEWS_ADD_URL = "http://news-at.zhihu.com/api/4/news/";
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
    @BindView(R.id.main_ptr_frame)
    PtrFrameLayout ptrFrameLayout;

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

        lvNewsList.setOnItemClickListener(this);

        ptrFrameLayout.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame,content,header);
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                getNewsForInternet();
                ptrFrameLayout.refreshComplete();
            }
        });
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
        Toast.makeText(mContext,"刷新了！", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String newAtUrl = NEWS_ADD_URL + newsBeanArray.get(position).id;
        Intent intent = new Intent(mContext,NewsActivity.class);
        intent.putExtra("url", newAtUrl);
        startActivity(intent);
    }
}
