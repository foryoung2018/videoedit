package com.wmlive.hhvideo.heihei.personal.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.wmlive.hhvideo.heihei.beans.personal.PhotoFolder;
import com.wmlive.hhvideo.utils.DeviceUtils;
import com.wmlive.hhvideo.utils.imageloader.GlideLoader;

import java.util.List;

import cn.wmlive.hhvideo.R;


/**
 * Created by XueFei on 2017/5/31.
 * <p>
 * 图片目录适配器
 */
public class FolderAdapter extends BaseAdapter {

    List<PhotoFolder> mDatas;
    Context mContext;
    int mWidth;

    public FolderAdapter(Context context, List<PhotoFolder> mDatas) {
        this.mDatas = mDatas;
        this.mContext = context;
        mWidth = DeviceUtils.dip2px(context, 90);
    }

    @Override
    public int getCount() {
        if (mDatas == null) {
            return 0;
        }
        return mDatas.size();
    }

    @Override
    public PhotoFolder getItem(int position) {
        if (mDatas == null || mDatas.size() == 0) {
            return null;
        }
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.item_folder_layout, null);
            holder.photoIV = (ImageView) convertView.findViewById(R.id.imageview_folder_img);
            holder.folderNameTV = (TextView) convertView.findViewById(R.id.textview_folder_name);
            holder.photoNumTV = (TextView) convertView.findViewById(R.id.textview_photo_num);
            holder.selectIV = (ImageView) convertView.findViewById(R.id.imageview_folder_select);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        PhotoFolder folder = getItem(position);
        if (folder == null) {
            return convertView;
        }
        if (folder.getPhotoList() == null || folder.getPhotoList().size() == 0) {
            return convertView;
        }
        holder.selectIV.setVisibility(View.GONE);
        holder.photoIV.setImageResource(R.drawable.ic_photo_loading);
        if (folder.isSelected()) {
            holder.selectIV.setVisibility(View.VISIBLE);
        }
        holder.folderNameTV.setText(folder.getName());
        holder.photoNumTV.setText(folder.getPhotoList().size() + "张");
        GlideLoader.loadImage(folder.getPhotoList().get(0).getPath(), holder.photoIV);
//        ImageLoader.getInstance().display(folder.getPhotoList().get(0).getPath(), holder.photoIV, mWidth, mWidth);
        return convertView;
    }

    private class ViewHolder {
        private ImageView photoIV;
        private TextView folderNameTV;
        private TextView photoNumTV;
        private ImageView selectIV;
    }

}
