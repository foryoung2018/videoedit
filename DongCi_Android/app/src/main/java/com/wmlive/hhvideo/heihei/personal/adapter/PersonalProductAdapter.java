package com.wmlive.hhvideo.heihei.personal.adapter;

import android.text.TextUtils;
import android.util.TypedValue;
import android.view.ViewGroup;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.request.ImageRequest;
import com.wmlive.hhvideo.heihei.beans.main.ShortVideoItem;
import com.wmlive.hhvideo.utils.imageloader.GlideLoader;
import com.wmlive.hhvideo.widget.refreshrecycler.RefreshAdapter;
import com.wmlive.hhvideo.widget.refreshrecycler.RefreshRecyclerView;

import java.util.List;

import cn.wmlive.hhvideo.R;

/**
 * Created by XueFei on 2017/5/27.
 * <p>
 * 个人--作品
 */

public class PersonalProductAdapter extends RefreshAdapter<PersonalProductHolder, ShortVideoItem> {
    private boolean isHaveDraftBox = false;

    public PersonalProductAdapter(List<ShortVideoItem> list, RefreshRecyclerView refreshView) {
        super(list, refreshView);
    }

    @Override
    public PersonalProductHolder onCreateHolder(ViewGroup parent, int viewType) {
        return new PersonalProductHolder(parent, R.layout.fragment_personal_video_item);
    }

    @Override
    public void onBindHolder(PersonalProductHolder holder, int position, ShortVideoItem data) {
        if (data == null) {
            return;
        }
        if (data.isDraft) {
            GlideLoader.loadImage(data.getOpus_small_cover(), holder.ivBg, R.drawable.bg_video_default_4_3);
            holder.ivHeart.setImageResource(R.drawable.icon_profile_draft);
            holder.tvCount.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 11);
            holder.tvCount.setText("草稿箱");
        } else {
            if (!TextUtils.isEmpty(data.getOpus_small_cover())) {
                holder.ivBg.setController(Fresco.newDraweeControllerBuilder().setUri(data.getOpus_gif_cover())
                        .setLowResImageRequest(ImageRequest.fromUri(data.getOpus_small_cover()))
                        .setOldController(holder.ivBg.getController())
                        .setAutoPlayAnimations(true)
                        .build());
            } else {
                GlideLoader.loadImage(data.getOpus_small_cover(), holder.ivBg, R.drawable.bg_video_default_4_3);
            }

            holder.ivHeart.setImageResource(R.drawable.icon_profile_play);
            holder.tvCount.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            holder.tvCount.setText(String.valueOf(data.getPlay_count()));
        }
    }

    public boolean isHaveDraftBox() {
        return isHaveDraftBox;
    }

    public void setHaveDraftBox(boolean haveDraftBox) {
        isHaveDraftBox = haveDraftBox;
    }
}
