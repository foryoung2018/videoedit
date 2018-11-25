package com.wmlive.hhvideo.heihei.personal.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wmlive.hhvideo.heihei.beans.personal.ReportType;

import java.util.List;

import cn.wmlive.hhvideo.R;

/**
 * Created by XueFei on 2017/6/5.
 * <p>
 * 举报列表
 */

public class ReportListAdapter extends BaseAdapter {
    private List<ReportType> mList;
    public LayoutInflater inflater;

    public ReportListAdapter(Context context, List<ReportType> list) {
        mList = list;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList != null && !mList.isEmpty() ? mList.get(position) : 0;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.pop_report_content_action_sheet_item, null);
            viewHolder.tvActionSheetReport = (TextView) convertView.findViewById(R.id.tv_action_sheet_report);
            viewHolder.tvActionSheetReportLine = convertView.findViewById(R.id.tv_action_sheet_report_line);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        ReportType reportType = mList.get(position);
        viewHolder.tvActionSheetReport.setText(reportType.getDesc());
        if (position == (getCount() - 1)) {
            viewHolder.tvActionSheetReportLine.setVisibility(View.GONE);
        } else {
            viewHolder.tvActionSheetReportLine.setVisibility(View.VISIBLE);
        }
        return convertView;
    }

    private class ViewHolder {
        TextView tvActionSheetReport;
        View tvActionSheetReportLine;
    }
}
