package com.wmlive.hhvideo.heihei.quickcreative;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.wmlive.hhvideo.utils.KLog;
import com.wmlive.hhvideo.utils.imageloader.GlideLoader;

import java.util.ArrayList;
import java.util.List;

import cn.wmlive.hhvideo.R;

public class CreativeRecprderrvAdapter extends RecyclerView.Adapter<CreativeRecprderrvAdapter.MusicHolder> {
    private int type;
    public static final int TYPE_MUSIC = 0;
    public static final int TYPE_BG = 1;
    private Context context;
    private List<String> list = new ArrayList<>();
    private ItemCllickListener itemCllickListener;
    private int notifytype;
    public static final int NOTIFYTYPE_SELECT = 0;
    public static final int NOTIFYTYPE_LOADING = 1;


    public void setSelectIndex(int selectIndex) {
        this.selectIndex = selectIndex;
    }

    private int selectIndex;

    public void setItemCllickListener(ItemCllickListener itemCllickListener) {
        this.itemCllickListener = itemCllickListener;
    }

    public CreativeRecprderrvAdapter(Context context, int type) {
        this.context = context;
        this.type = type;

    }

    public void updateList(List listss) {
        this.list.clear();
        this.list.addAll(listss);
        notifyDataSetChanged();
    }

    @Override
    public MusicHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_creative_layout, parent, false);
        return new MusicHolder(view);
    }

    @Override
    public void onBindViewHolder(MusicHolder holder, int position, List<Object> payloads) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position);
        } else {
            boolean ifloading = (boolean) payloads.get(0);
            if (ifloading) {
                holder.rl_loading.setVisibility(View.VISIBLE);
            } else {
                holder.rl_loading.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onBindViewHolder(MusicHolder holder, int position) {
        int index = position;
        String s = list.get(index);
        KLog.d("onBindViewHolder: path==" + s);
        if (type == TYPE_BG) {
//            holder.iv_creative_item_selected.setBackgroundResource(R.drawable.icon_video_background);
            GlideLoader.loadCircleImage(s, holder.iv, R.drawable.icon_video_background);
        } else if (type == TYPE_MUSIC) {
//            holder.iv_creative_item_selected.setBackgroundResource(R.drawable.icon_video_music);
            GlideLoader.loadCornerImage(s, holder.iv, R.drawable.icon_video_music, 15);
        }

        if (index == selectIndex) {
            holder.rl_container.setVisibility(View.VISIBLE);
            holder.rl_container.setSelected(type == TYPE_BG);
        } else {
            holder.rl_container.setVisibility(View.GONE);
        }
        holder.rl_loading.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KLog.d("", "onClick: ");
            }
        });

        holder.iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemCllickListener != null && index != selectIndex) {
                    itemCllickListener.itemClick(index, s, type);
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void notifyDC(int notifytype, int index) {
        this.notifytype = notifytype;
        this.selectIndex = index;
        notifyDataSetChanged();
    }

    public void notifyPosition(boolean ifLoading, int position) {
        notifyItemChanged(position, ifLoading);
    }


    public class MusicHolder extends RecyclerView.ViewHolder {
        ImageView iv;
        ImageView iv_creative_item_selected;
        RelativeLayout rl_container;
        RelativeLayout rl_loading;

        public MusicHolder(View itemView) {
            super(itemView);
            iv = itemView.findViewById(R.id.iv_creative_item);
            iv_creative_item_selected = itemView.findViewById(R.id.iv_creative_item_selected);
            rl_container = itemView.findViewById(R.id.rl_container);
            rl_loading = itemView.findViewById(R.id.rl_loading);
        }
    }


    public interface ItemCllickListener {
        void itemClick(int position, String imagePath, int type);
    }

}
