package com.dh.zhihudaily.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by dh on 16-11-22.
 */

public class StreamUtil {
    public static String streamToString(InputStream is) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String line;
        StringBuilder builder = new StringBuilder();
        while((line = reader.readLine()) != null){
            builder.append(line);
        }
        return builder.toString();
    }
}
