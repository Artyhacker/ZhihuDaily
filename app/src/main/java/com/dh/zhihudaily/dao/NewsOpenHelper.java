package com.dh.zhihudaily.dao;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by dh on 16-11-24.
 */

public class NewsOpenHelper extends SQLiteOpenHelper {
    public NewsOpenHelper(Context context) {
        super(context, "news", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table news (_id integer primary key, title varchar(200), images varchar(200))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
