package com.wmlive.hhvideo.heihei.mainhome.widget;

import android.graphics.Point;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.wmlive.hhvideo.common.manager.gift.GiftManager;
import com.wmlive.hhvideo.heihei.beans.gifts.GiftEntity;
import com.wmlive.hhvideo.heihei.mainhome.util.GiftUtils;
import com.wmlive.hhvideo.utils.KLog;
import com.wmlive.hhvideo.utils.MyClickListener;

import java.util.ArrayList;
import java.util.List;

import cn.wmlive.hhvideo.R;

/**
 * Created by lsq on 1/8/2018.5:50 PM
 *
 * @author lsq
 * @describe 添加描述
 */

public class GiftAdapter extends RecyclerView.Adapter<GiftAdapter.GiftViewHolder> {
    public static final int MAX_RATIO = 1000;

    private static final String KEY_START = "start";
    private static final String KEY_STOP = "stop";

    private GiftItemClickListener itemClickListener;
    private List<GiftEntity> giftList;
    private CountdownView.OnCountdownListener countdownListener;

    public GiftAdapter() {
        giftList = new ArrayList<>(4);
    }

    @Override
    public GiftViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new GiftViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_flow_gift, parent, false));
    }

    @Override
    public void onBindViewHolder(GiftViewHolder holder, int position) {
        int index = position % giftList.size();
        GiftEntity giftEntity = giftList.get(index);
        String imageUrl = GiftManager.getGiftImage(giftEntity.id, giftEntity.unique_id);
        if (TextUtils.isEmpty(imageUrl)) {
            KLog.e("本地文件:" + imageUrl + "不存在：使用网络图片");
            imageUrl = giftEntity.image_url;
        } else {
            imageUrl = "file://" + imageUrl;
        }
        try {
            holder.ivGift.setController(Fresco.newDraweeControllerBuilder()
                    .setUri(imageUrl)
                    .setOldController(holder.ivGift.getController())
                    .build());
        } catch (Exception e) {
            e.printStackTrace();
        }
        holder.tvCountdown.setData(giftEntity.id, index, position, giftEntity.isFree());
        holder.tvCountdown.setVisibility(giftEntity.gold == 0 ? View.VISIBLE : View.GONE);
        holder.tvCountdown.setCountdownListener(countdownListener);
        holder.tvCountdown.setText(giftEntity.show_second + "s");
        holder.ivGift.setOnClickListener(new MyClickListener(120) {
            @Override
            protected void onMyClick(View view) {
                if (itemClickListener != null) {
                    int[] location = new int[2];
                    view.getLocationOnScreen(location);
                    Point point = new Point();
                    point.set(location[0] + (int) (view.getWidth() * 0.5f), location[1]);
                    KLog.i("====开始位置：" + point);
                    if (itemClickListener.onGiftItemClick(position, index, giftEntity, point)) {
                        holder.ivGift.startAnimation(GiftUtils.getScaleAnimation(0.8f, 100));
                    }
                }
            }
        });
//        holder.tvTitle.setVisibility(GlobalParams.Config.IS_DEBUG ? View.VISIBLE : View.GONE);
//        holder.tvTitle.setText("位置：" + position + " ,索引:" + index);
    }

    public void setCountdownListener(CountdownView.OnCountdownListener countdownListener) {
        this.countdownListener = countdownListener;
    }

    @Override
    public int getItemCount() {
        return giftList == null ? 0 : giftList.size() * MAX_RATIO;
    }

    public void addData(List<GiftEntity> giftEntityList) {
        giftList.clear();
        if (giftEntityList != null) {
            giftList.addAll(giftEntityList);
        }
        notifyDataSetChanged();
    }

    public void setItemClickListener(GiftItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public interface GiftItemClickListener {
        boolean onGiftItemClick(int position, int index, GiftEntity entity, Point centerPoint);
    }

    class GiftViewHolder extends RecyclerView.ViewHolder {
        SimpleDraweeView ivGift;
        TextView tvTitle;
        CountdownView tvCountdown;

        public GiftViewHolder(View itemView) {
            super(itemView);
            ivGift = itemView.findViewById(R.id.ivGift);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvCountdown = itemView.findViewById(R.id.tvCountdown);
        }
    }
}
