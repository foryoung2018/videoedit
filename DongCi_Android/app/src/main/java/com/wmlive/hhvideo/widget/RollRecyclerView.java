package com.wmlive.hhvideo.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.wmlive.hhvideo.heihei.beans.main.DcDanmaEntity;
import com.wmlive.hhvideo.heihei.mainhome.adapter.RollCommentAdapter;
import com.wmlive.hhvideo.utils.CollectionUtil;
import com.wmlive.hhvideo.utils.KLog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lsq on 5/10/2018 - 3:06 PM
 * 类描述：
 */
public class RollRecyclerView extends RecyclerView {
    private static final String TAG = RollRecyclerView.class.getSimpleName();
    private boolean rolling;
    private RollRunnable rollRunnable;
    private List<DcDanmaEntity> dataList;
    private int index = 0;

    public RollRecyclerView(Context context) {
        super(context);
        initViews(context);
    }

    public RollRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initViews(context);
    }

    public RollRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initViews(context);
    }

    private void initViews(Context context) {
        rollRunnable = new RollRunnable();
        dataList = new ArrayList<>(10);
    }

    public void startRoll() {
//        if (getAdapter() != null && getAdapter().getItemCount() > 0) {
//            RollRecyclerView.this.scrollToPosition(0);
//        }
        KLog.i(TAG, "startRoll: ");
        rolling = true;
        if (rollRunnable != null && !CollectionUtil.isEmpty(dataList)) {
            removeCallbacks(rollRunnable);
            postDelayed(rollRunnable, 500);
        }
    }

    public void stopRoll(boolean clearData) {
        rolling = false;
        KLog.i(TAG, "stopRoll clearData: " + clearData);
        RollRecyclerView.this.removeCallbacks(rollRunnable);
        if (clearData) {
            index = 0;
            if (getAdapter() instanceof RollCommentAdapter) {
                ((RollCommentAdapter) getAdapter()).clearData();
            }
        }
    }

    public void stopRoll() {
        stopRoll(false);
    }

    public boolean isRolling() {
        return rolling;
    }

    public void setDataList(List<DcDanmaEntity> list) {
        dataList.clear();
        dataList.add(new DcDanmaEntity());
        dataList.add(new DcDanmaEntity());
        dataList.add(new DcDanmaEntity());
        dataList.add(new DcDanmaEntity());
        dataList.add(new DcDanmaEntity());
        dataList.add(new DcDanmaEntity());
        RollCommentAdapter adapter = (RollCommentAdapter) RollRecyclerView.this.getAdapter();
        adapter.addData(dataList);
        if (!CollectionUtil.isEmpty(list)) {
            for (DcDanmaEntity danmaEntity : list) {
                if (danmaEntity != null
                        && danmaEntity.user != null
                        && !TextUtils.isEmpty(danmaEntity.user.getName())) {
                    dataList.add(danmaEntity);
                }
            }
        }
    }

    private class RollRunnable implements Runnable {

        @Override
        public void run() {
            if (rolling) {
                if (RollRecyclerView.this.getAdapter() instanceof RollCommentAdapter) {
                    RollCommentAdapter adapter = (RollCommentAdapter) RollRecyclerView.this.getAdapter();
                    int next = index % dataList.size();
                    while (dataList.get(next).user == null) {
                        index++;
                        next = index % dataList.size();
                    }
                    adapter.addData(dataList.get(next));
                    scrollToPosition(adapter.getItemCount() - 1);
                    index++;
//                    KLog.i("======滚动到：" + (adapter.getItemCount() - 1));
                    RollRecyclerView.this.postDelayed(this, 1200);
                }
            }
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        stopRoll(true);
        super.onDetachedFromWindow();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        return false;
    }
}
