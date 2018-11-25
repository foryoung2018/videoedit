package com.wmlive.hhvideo.heihei.record.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wmlive.hhvideo.heihei.beans.record.EditVideoModel;
import com.wmlive.hhvideo.heihei.record.viewholder.BaseViewHolder;
import com.wmlive.hhvideo.heihei.record.widget.CustomTrimMusicView;
import com.wmlive.hhvideo.heihei.record.widget.CustomTrimVideoView;
import com.wmlive.hhvideo.utils.CollectionUtil;
import com.wmlive.hhvideo.utils.DeviceUtils;
import com.wmlive.hhvideo.utils.KLog;

import java.util.List;

/**
 * Created by wenlu on 2017/9/6.
 */

public class EditVideoAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private final String TAG = "EditVideoAdapter";
    public static final int VIEW_TYPE_DRAG_HANDLE = 0;
    public static final int VIEW_TYPE_EDITING_VIDEO = 1;
    public static final int VIEW_TYPE_EDITING_MUSIC = 2;
    private List<EditVideoModel> mDataList;
    private final Context mContext;
    private OnEditVideoCallback mCallback;
    private boolean isInit = false;

    public EditVideoAdapter(Context context, List<EditVideoModel> shortVideoEntities) {
        mContext = context;
        mDataList = shortVideoEntities;
        KLog.d(TAG,"init");
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        KLog.d(TAG,"onCreateViewHolder");
        BaseViewHolder viewHolder = null;
        View view = null;
        switch (viewType) {
            case VIEW_TYPE_DRAG_HANDLE:
                TextView tv = new TextView(mContext);
                ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 50);
                tv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 50));
                tv.setText("拖动");
                parent.addView(tv);
                viewHolder = new BaseViewHolder(tv, viewType);
                break;
            case VIEW_TYPE_EDITING_VIDEO:
                view = new CustomTrimVideoView(mContext);
                parent.addView(view);
                viewHolder = new BaseViewHolder(view, viewType);
                break;
            case VIEW_TYPE_EDITING_MUSIC:
                view = new CustomTrimMusicView(mContext);
                parent.addView(view);
                viewHolder = new BaseViewHolder(view, viewType);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        KLog.d(TAG,position+"onBindViewHolder"+holder.viewType);
        switch (holder.viewType) {
            case VIEW_TYPE_DRAG_HANDLE:

                break;
            case VIEW_TYPE_EDITING_VIDEO:
                CustomTrimVideoView customTrimVideoView = (CustomTrimVideoView) holder.itemView;
                KLog.d(TAG,position+"onBindViewHolder-->"+isInit+mCallback);
                if (mCallback != null && isInit) {
                    mCallback.initVideoGallery(position, customTrimVideoView, mDataList.get(position).index);
                }
                break;
            case VIEW_TYPE_EDITING_MUSIC:
                CustomTrimMusicView customTrimMusicView = (CustomTrimMusicView) holder.itemView;
                if (mCallback != null && isInit) {
                    mCallback.initMusicView(position, customTrimMusicView);
                }
                break;
        }
    }

    @Override
    public int getItemCount() {
        if (CollectionUtil.isEmpty(mDataList)) {
            return 0;
        }
        return mDataList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mDataList.get(position).type;
    }

    public int getItemViewHeight(int position) {
        switch (getItemViewType(position)) {
            case VIEW_TYPE_DRAG_HANDLE:
                return 50;
            case VIEW_TYPE_EDITING_VIDEO:
                return DeviceUtils.dip2px(mContext, 84);
            case VIEW_TYPE_EDITING_MUSIC:
                return DeviceUtils.dip2px(mContext, 90);
        }
        return 0;
    }

    public void addData(List<EditVideoModel> list) {
        if (!CollectionUtil.isEmpty(list)) {
            mDataList.clear();
            mDataList.addAll(list);
            notifyDataSetChanged();
        }
    }

    public void setOnEditVideoCallback(OnEditVideoCallback callback) {
        this.mCallback = callback;
    }

    public void init() {
        isInit = true;
        notifyDataSetChanged();
    }

    public interface OnEditVideoCallback {

        void initVideoGallery(int position, CustomTrimVideoView customTrimVideoView, int index);

        void initMusicView(int position, CustomTrimMusicView customTrimMusicView);
    }
}
