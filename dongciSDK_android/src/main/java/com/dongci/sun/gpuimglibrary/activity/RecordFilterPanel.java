package com.dongci.sun.gpuimglibrary.activity;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;


import com.dongci.sun.gpuimglibrary.R;
import com.dongci.sun.gpuimglibrary.common.FilterInfoEntity;

import java.util.ArrayList;
//import static com.dongci.sun.gpuimglibrary.test.DcTestActivity.LIST;


/**
 * Created by lsq on 8/28/2017.
 * 滤镜调节面板
 */

public class RecordFilterPanel extends BaseCustomView {

    RecyclerView rvFilter;

    private FilterPanelAdapter panelAdapter;

    public RecordFilterPanel(Context context) {
        super(context);
    }

    public RecordFilterPanel(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void initViews(Context context, AttributeSet attrs, int defStyle) {
        panelAdapter = new FilterPanelAdapter(new ArrayList<FilterInfoEntity>());
        rvFilter = (RecyclerView)findViewById(R.id.rvFilter);
        rvFilter.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        rvFilter.setAdapter(panelAdapter);
    }

    @Override
    public void initData() {
        super.initData();
//        panelAdapter.addData(LIST);
    }

    public void setCurrentIndex(int index) {
        if (panelAdapter != null) {
            if (panelAdapter.setSelectItem(index)) {
                rvFilter.scrollToPosition(index);
            }
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
