package com.wmlive.hhvideo.heihei.personal.adapter;

import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.wmlive.hhvideo.heihei.db.ProductEntity;
import com.wmlive.hhvideo.utils.KLog;
import com.wmlive.hhvideo.utils.imageloader.GlideLoader;
import com.wmlive.hhvideo.widget.refreshrecycler.RefreshAdapter;
import com.wmlive.hhvideo.widget.refreshrecycler.RefreshRecyclerView;

import java.util.List;

import cn.wmlive.hhvideo.R;

/**
 * Created by XueFei on 2017/5/27.
 * <p>
 * 草稿箱
 */

public class DraftBoxAdapter extends RefreshAdapter<DraftBoxHolder, ProductEntity> {

    public DraftBoxAdapter(List<ProductEntity> list, RefreshRecyclerView refreshView) {
        super(list, refreshView);
    }

    @Override
    public DraftBoxHolder onCreateHolder(ViewGroup parent, int viewType) {
        return new DraftBoxHolder(parent, R.layout.activity_draftbox_item);
    }

    @Override
    public void onBindHolder(DraftBoxHolder holder, final int position, final ProductEntity data) {
        GlideLoader.loadImage(data.coverPath, holder.ivBg, R.drawable.bg_video_default_4_3);
        KLog.d("封面路径", "data===" + data);
        holder.ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mOnClickFansCustom) {
                    mOnClickFansCustom.onClick(v, position, data);
                }
            }
        });
    }

    private OnClickFansCustom mOnClickFansCustom;

    public interface OnClickFansCustom {
        void onClick(View view, int position, ProductEntity productEntity);
    }

    public void setOnClickCustom(OnClickFansCustom onClickFansCustom) {
        mOnClickFansCustom = onClickFansCustom;
    }
}
