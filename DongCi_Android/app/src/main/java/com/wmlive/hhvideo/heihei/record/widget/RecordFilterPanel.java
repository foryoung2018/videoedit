package com.wmlive.hhvideo.heihei.record.widget;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;

import com.wmlive.hhvideo.heihei.beans.record.FilterInfoEntity;
import com.wmlive.hhvideo.heihei.record.adapter.FilterPanelAdapter;
import com.wmlive.hhvideo.widget.BaseCustomView;

import java.util.ArrayList;

import butterknife.BindView;
import cn.wmlive.hhvideo.R;

import static com.wmlive.hhvideo.heihei.record.manager.RecordSetting.FILTER_LIST;

/**
 * Created by lsq on 8/28/2017.
 * 滤镜调节面板
 */

public class RecordFilterPanel extends BaseCustomView {
    @BindView(R.id.rvFilter)
    RecyclerView rvFilter;
    private FilterPanelAdapter panelAdapter;

    public FilterPanelAdapter getPanelAdapter() {
        return panelAdapter;
    }

    public RecordFilterPanel(Context context) {
        super(context);
    }

    public RecordFilterPanel(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void initViews(Context context, AttributeSet attrs, int defStyle) {
        panelAdapter = new FilterPanelAdapter(new ArrayList<FilterInfoEntity>());
        rvFilter.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        rvFilter.setAdapter(panelAdapter);
    }

    @Override
    public void initData() {
        super.initData();
        panelAdapter.addData(FILTER_LIST);
    }

    public void setCurrentIndex(int index) {

        if (panelAdapter != null) {
            if (panelAdapter.setSelectItem(index)) {
                rvFilter.scrollToPosition(index);
            }
            panelAdapter.notifyDataSetChanged();
        }

    }

    public int getSelectedPosition() {
        return panelAdapter.getSelectedPosition();
    }

    public void setFilterItemSelectListener(FilterPanelAdapter.OnFilterItemSelectListener listener) {
        if (panelAdapter != null) {
            panelAdapter.setFilterItemSelectListener(listener);
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_filter_panel;
    }
}
