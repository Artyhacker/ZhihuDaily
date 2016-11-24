package com.dh.zhihudaily.bean;

import android.graphics.Bitmap;

/**
 * Created by dh on 16-11-23.
 */

public class NewsBean {
    public String images;
    public int id;
    public String title;

    public NewsBean(int id, String title, String images) {
        this.images = images;
        this.id = id;
        this.title = title;
    }
}
