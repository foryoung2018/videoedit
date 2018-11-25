package com.wmlive.hhvideo.heihei.mainhome.adapter;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import cn.wmlive.hhvideo.R;

/**
 */
public class EmotionGridViewAdapter extends BaseAdapter {

    private Context context;
    private List<String> emotionNames;
    private int itemWidth;
    private int emotion_map_type;

    public EmotionGridViewAdapter(Context context, List<String> emotionNames, int itemWidth, int emotion_map_type) {
        this.context = context;
        this.emotionNames = emotionNames;
        this.itemWidth = itemWidth;
        this.emotion_map_type = emotion_map_type;
    }

    @Override
    public int getCount() {
        // +1 最后一个为删除按钮
        return emotionNames.size() + 1;
    }

    @Override
    public String getItem(int position) {
        return emotionNames.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //判断是否为最后一个item
        if (position == getCount() - 1) {
            RelativeLayout rl = new RelativeLayout(context);
            rl.setGravity(Gravity.CENTER);
            rl.setPadding(itemWidth / 8, itemWidth / 8, itemWidth / 8, itemWidth / 8);
            LayoutParams params = new LayoutParams(itemWidth, itemWidth);
            ImageView iv_emotion = new ImageView(context);
            iv_emotion.setImageResource(R.drawable.icon_emoji_repeal);
            rl.setLayoutParams(params);
            rl.addView(iv_emotion);
            return rl;
        } else {
            String emotionName = emotionNames.get(position);
            TextView tv_emoji = new TextView(context);
            tv_emoji.setPadding(itemWidth / 8, itemWidth / 8, itemWidth / 8, itemWidth / 8);
            LayoutParams params = new LayoutParams(itemWidth, itemWidth);
            tv_emoji.setGravity(Gravity.CENTER);
            tv_emoji.setLayoutParams(params);
            tv_emoji.setText(emotionName);
            tv_emoji.setTextSize(20);
            tv_emoji.setTextColor(context.getResources().getColor(R.color.bg_black));
            return tv_emoji;
        }


    }

}
