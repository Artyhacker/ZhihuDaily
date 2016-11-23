package com.dh.zhihudaily.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dh.zhihudaily.R;
import com.dh.zhihudaily.bean.NewsBean;
import com.dh.zhihudaily.utils.UrlUtil;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by dh on 16-11-23.
 */

public class NewsAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<NewsBean> newsBeanArray;
    public NewsAdapter(Context context, ArrayList<NewsBean> newsBeanArray) {
        this.context = context;
        this.newsBeanArray = newsBeanArray;
    }

    @Override
    public int getCount() {
        return newsBeanArray.size();
    }

    @Override
    public Object getItem(int position) {
        return newsBeanArray.get(position);
    }

    @Override
    public long getItemId(int position) {
        return Long.parseLong(newsBeanArray.get(position).id);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;
        if(convertView != null)
            view = convertView;
        else{
            view = View.inflate(context, R.layout.item_news, null);
        }
        ImageView ivItemImage = (ImageView) view.findViewById(R.id.iv_item_image);
        TextView tvItemTitle = (TextView) view.findViewById(R.id.tv_item_title);
        NewsBean bean = newsBeanArray.get(position);
        ivItemImage.setImageBitmap(bean.images);
        tvItemTitle.setText(bean.title);
        return view;
    }
}
