package com.wmlive.hhvideo.heihei.mainhome.adapter;

import android.text.TextUtils;
import android.view.ViewGroup;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.imagepipeline.request.ImageRequest;
import com.wmlive.hhvideo.heihei.beans.main.ShortVideoItem;
import com.wmlive.hhvideo.heihei.mainhome.viewholder.ExplosionViewHolder;
import com.wmlive.hhvideo.utils.KLog;
import com.wmlive.hhvideo.widget.refreshrecycler.RefreshAdapter;
import com.wmlive.hhvideo.widget.refreshrecycler.RefreshRecyclerView;

import java.util.List;

import cn.wmlive.hhvideo.R;

/**
 * Created by vhawk on 2017/6/1.
 * modify by lsq
 */

public class ExplosionAdapter extends RefreshAdapter<ExplosionViewHolder, ShortVideoItem> {
    private DraweeController controller;

    public ExplosionAdapter(List list, RefreshRecyclerView refreshView) {
        super(list, refreshView);
    }

    @Override
    public ExplosionViewHolder onCreateHolder(ViewGroup parent, int viewType) {
        return new ExplosionViewHolder(parent, R.layout.item_explosion);
    }

    @Override
    public void onBindHolder(ExplosionViewHolder holder, int position, ShortVideoItem data) {
        if (data == null) {
            return;
        }
        holder.tvMusicName.setText(data.getMusic_name());
        holder.tvLoveCount.setText(String.valueOf(data.getLike_count()));
        holder.tvPlayCount.setText(String.valueOf(data.getPlay_count()));
        if (!TextUtils.isEmpty(data.getOpus_gif_cover())) {
            try {
                controller = Fresco.newDraweeControllerBuilder()
                        .setLowResImageRequest(ImageRequest.fromUri(data.getOpus_small_cover()))
//                    .setOldController(holder.ivSplash.getController())
                        .setUri(data.getOpus_gif_cover())
                        .setAutoPlayAnimations(true)
                        .build();
                holder.ivSplash.setController(controller);
            } catch (Exception e) {
                KLog.i("=======加载webp出错：" + e.getMessage());
                e.printStackTrace();
            } catch (Error error) {
                KLog.i("=======加载webp出错：" + error.getMessage());
            }
            KLog.i("=====2222load webp position:" + position);
        } else {
            holder.ivSplash.setImageURI(data.getOpus_small_cover());
        }
    }
}
