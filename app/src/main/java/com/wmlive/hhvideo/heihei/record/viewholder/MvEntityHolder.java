package com.wmlive.hhvideo.heihei.record.viewholder;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.wmlive.hhvideo.heihei.beans.record.MvConfigItem;
import com.wmlive.hhvideo.utils.KLog;
import com.wmlive.hhvideo.widget.CustomFontTextView;

import cn.wmlive.hhvideo.R;

/**
 * 录制页面 选中的素材
 */
public class MvEntityHolder extends RecyclerView.ViewHolder {
    public ImageView imageBg;
    public ImageView imgRefreshPlay;//刷新，重新加载
    public ProgressBar progressBar;
    public ImageView itemPlayIv;
    public ImageView grayBg;
    public CustomFontTextView fontView;
    public RelativeLayout root;

    public MvEntityHolder(View itemView) {
        super(itemView);
        imageBg = itemView.findViewById(R.id.item_mvrecord_bg);
        grayBg = itemView.findViewById(R.id.gray_bg);
        imgRefreshPlay = itemView.findViewById(R.id.item_mvrecord_refreshplay);
        progressBar =  itemView.findViewById(R.id.item_mvrecord_pb);
        itemPlayIv = itemView.findViewById(R.id.item_paly_iv);
        root =  itemView.findViewById(R.id.item_mvrecord_root);
        fontView =  itemView.findViewById(R.id.fontView);
    }

    /**
     * 根据状态显示对应的控件
     * @param state
     */
    public void setState(int state,String cover){
        switch (state){//0 未下载 1 下载中 2 下载完成 3 可播放
            case MvConfigItem.STATE_DONWLOAD_PPE://未下载
                progressBar.setVisibility(View.GONE);
                itemPlayIv.setVisibility(View.GONE);
                imgRefreshPlay.setVisibility(View.VISIBLE);
                break;
            case MvConfigItem.STATE_DONWLOADING://下载中
                imgRefreshPlay.setVisibility(View.GONE);
                itemPlayIv.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                fontView.setVisibility(View.GONE);
                break;
            case MvConfigItem.STATE_DONWLOAD_FINISH://下载完成
                progressBar.setVisibility(View.GONE);
                imgRefreshPlay.setVisibility(View.GONE);
                fontView.setVisibility(View.GONE);
                break;
            case MvConfigItem.STATE_DEFAULT://默认状态
                fontView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                imgRefreshPlay.setVisibility(View.GONE);
                break;
            case MvConfigItem.STATE_RECORDING://正在录制
                progressBar.setVisibility(View.GONE);
                imgRefreshPlay.setVisibility(View.GONE);
                itemPlayIv.setVisibility(View.GONE);
                grayBg.setVisibility(View.GONE);
                KLog.i("cover-->"+cover);
                if(TextUtils.isEmpty(cover))
                    fontView.setVisibility(View.VISIBLE);
                else {
                    fontView.setVisibility(View.GONE);
                }
                break;
            case MvConfigItem.STATE_RECORD_END://录制结束
                progressBar.setVisibility(View.GONE);
                imgRefreshPlay.setVisibility(View.GONE);
                fontView.setVisibility(View.GONE);
                break;
        }
    }

}
