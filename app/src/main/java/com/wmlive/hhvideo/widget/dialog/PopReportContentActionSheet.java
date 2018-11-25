package com.wmlive.hhvideo.widget.dialog;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.wmlive.hhvideo.heihei.beans.personal.ReportType;
import com.wmlive.hhvideo.heihei.personal.adapter.ReportListAdapter;

import java.util.List;

import cn.wmlive.hhvideo.R;

/**
 * Created by XueFei on 2017/6/05.
 * <p>
 * 举报---内容pop
 */

public class PopReportContentActionSheet extends PopupWindow implements AdapterView.OnItemClickListener, View.OnClickListener {

    public interface OnSnsClickListener {
        void onSnsClick(ReportType reportType);
    }

    public void setOnSnsClickListener(OnSnsClickListener l) {
        this.l = l;
    }

    private OnSnsClickListener l;

    private View root;
    private LinearLayout menuContainer;
    private Context context;
    private List<ReportType> reportTypeList;
    private ListView lvReportList;
    private ReportListAdapter reportListAdapter;

    private TextView btn_action_sheet_cancel;

    public PopReportContentActionSheet(Context context) {
        super(context);
        this.context = context;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        root = inflater.inflate(R.layout.pop_report_content_action_sheet, null);

        lvReportList = (ListView) root.findViewById(R.id.lv_report_list);
        lvReportList.setOnItemClickListener(this);

        btn_action_sheet_cancel = (TextView) root.findViewById(R.id.btn_action_sheet_cancel);

        menuContainer = (LinearLayout) root
                .findViewById(R.id.share_action_sheet_button_container);
        btn_action_sheet_cancel.setOnClickListener(this);

        // mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
        root.setOnTouchListener(new OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {

                int height = menuContainer.getTop();
                int y = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y < height) {
                        dismiss();
                    }
                }
                return true;
            }
        });
    }

    public List<ReportType> getReportTypeList() {
        return reportTypeList;
    }

    public void setReportTypeList(List<ReportType> reportTypeList) {
        this.reportTypeList = reportTypeList;
    }

    public void show() {
        reportListAdapter = new ReportListAdapter(context, reportTypeList);
        lvReportList.setAdapter(reportListAdapter);

        // 设置SelectPicPopupWindow的View
        this.setContentView(root);
        // 设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(LayoutParams.MATCH_PARENT);
        // 设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(LayoutParams.WRAP_CONTENT);
        // 设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        // 设置SelectPicPopupWindow弹出窗体动画效果
        // this.setAnimationStyle(R.style.popwin_anim_style);
        ColorDrawable dw = new ColorDrawable(context.getResources().getColor(android.R.color.transparent));
        // 设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);
        this.showAtLocation(root, Gravity.BOTTOM
                | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    private void hide() {
        super.dismiss();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ReportType reportType = reportTypeList.get(position);
        l.onSnsClick(reportType);
    }

    @Override
    public void onClick(View v) {
        this.dismiss();
    }
}
