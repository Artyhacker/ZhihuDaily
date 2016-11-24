package com.dh.zhihudaily.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.dh.zhihudaily.bean.NewsBean;

import java.util.ArrayList;

/**
 * Created by dh on 16-11-24.
 */

public class NewsDaoUtils {

    private NewsOpenHelper newsOpenHelper;

    public NewsDaoUtils(Context context){
        newsOpenHelper = new NewsOpenHelper(context);
    }

    public ArrayList<NewsBean> getNews(){
        ArrayList<NewsBean> list = new ArrayList<>();
        SQLiteDatabase db = newsOpenHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from news", null);
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(0);
                String title = cursor.getString(1);
                String images = cursor.getString(2);
                NewsBean bean = new NewsBean(id, title, images);
                list.add(bean);
            }
        }
        db.close();
        return list;
    }

    public void saveNews(ArrayList<NewsBean> arrayList){
        SQLiteDatabase db = newsOpenHelper.getReadableDatabase();
        for(NewsBean bean: arrayList){
            ContentValues values = new ContentValues();
            values.put("_id", bean.id);
            values.put("title", bean.title);
            values.put("images", bean.images);
            db.insert("news", null, values);
        }
        db.close();
    }

    public void deleteNews(){
        SQLiteDatabase db = newsOpenHelper.getReadableDatabase();
        db.delete("news", null, null);
        db.close();
    }
}
