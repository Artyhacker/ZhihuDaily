package com.dh.zhihudaily.utils;

import android.content.Context;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.dh.zhihudaily.bean.NewsBean;
import com.dh.zhihudaily.dao.NewsDaoUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by dh on 16-11-24.
 */

public class NewsUtil {
    public static ArrayList<NewsBean> parseNewsJSON(Context context, String response){
        ArrayList<NewsBean> newsBeanArray = new ArrayList<>();
        try {
            JSONObject object = new JSONObject(response);
            JSONArray stories = object.getJSONArray("stories");
            for(int i = 0; i < stories.length(); i++) {
                JSONObject story = (JSONObject) stories.get(i);
                final String images = story.getString("images").replace("\\", "");
                //Log.d("Main", images);
                final int id = story.getInt("id");
                final String title = story.getString("title");

                NewsBean bean = new NewsBean(id, title, images);
                newsBeanArray.add(bean);

            }
            NewsDaoUtils newsDaoUtils = new NewsDaoUtils(context);
            newsDaoUtils.deleteNews();
            newsDaoUtils.saveNews(newsBeanArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return newsBeanArray;
    }

    public static ArrayList<NewsBean> getNewsForDatabase(Context context){
        return new NewsDaoUtils(context).getNews();
    }



}
