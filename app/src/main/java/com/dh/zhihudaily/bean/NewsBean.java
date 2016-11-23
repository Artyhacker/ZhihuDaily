package com.dh.zhihudaily.bean;

import android.graphics.Bitmap;

/**
 * Created by dh on 16-11-23.
 */

public class NewsBean {
    public Bitmap images;
    public String type;
    public String id;
    public String ga_prefix;
    public String title;

    public NewsBean(String id, String title, Bitmap images) {
        this.images = images;
        this.id = id;
        this.title = title;
    }
}
