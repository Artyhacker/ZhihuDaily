package com.dh.zhihudaily.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;

import static android.R.attr.bitmap;

/**
 * Created by dh on 16-11-23.
 */

public class UrlUtil {
    public static Bitmap urlToBitmap(final String url) throws IOException {
        InputStream is = new URL(url).openConnection().getInputStream();
        Bitmap bitmap = BitmapFactory.decodeStream(is);
        return bitmap;
    }
}
