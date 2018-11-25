package com.wmlive.hhvideo.heihei.record.widget;

import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.wmlive.hhvideo.R;

/**
 * Created by lsq on 4/23/2018 - 3:47 PM
 * 类描述：
 */
public class TopicInfoHolder {

    private LinearLayout llAddTopic;
    private TextView tvTopicLabel;
    private ImageView ivDeleteTopic;
    private CheckBox cbSaveLocal;
    private CheckBox cbAllow;

    public boolean hasTopic() {
        return llAddTopic.getTag() != null && (boolean) llAddTopic.getTag();
    }

    public void showTopic(String topic) {
        if (!TextUtils.isEmpty(topic)) {
            tvTopicLabel.setText(topic);
            ivDeleteTopic.setVisibility(View.VISIBLE);
            llAddTopic.setTag(true);
        } else {
            showAdd();
        }
    }

    public void showLoacalInfo(boolean show) {
        cbSaveLocal.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
        cbAllow.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
    }

    public void showAdd() {
        tvTopicLabel.setText(tvTopicLabel.getResources().getString(R.string.stringAddTopic));
        ivDeleteTopic.setVisibility(View.GONE);
        llAddTopic.setTag(false);
    }

    public TopicInfoHolder(LinearLayout llAddTopic, TextView tvTopicLabel,
                           ImageView ivDeleteTopic, CheckBox cbSaveLocal, CheckBox cbAllow) {
        this.llAddTopic = llAddTopic;
        this.tvTopicLabel = tvTopicLabel;
        this.ivDeleteTopic = ivDeleteTopic;
        this.cbSaveLocal = cbSaveLocal;
        this.cbAllow = cbAllow;
    }
}
