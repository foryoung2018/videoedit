package com.wmlive.hhvideo.heihei.beans.personal;

import com.wmlive.hhvideo.common.base.BaseModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by XueFei on 2017/6/5.
 * <p>
 * 举报
 */

public class ReportEntry extends BaseModel {
    public ReportEntry() {
        report_type = new ArrayList<>();
    }

    private List<ReportType> report_type;

    public List<ReportType> getReport_type() {
        return report_type;
    }

    public void setReport_type(List<ReportType> report_type) {
        this.report_type = report_type;
    }

    @Override
    public String toString() {
        return "ReportEntry{" +
                "report_type=" + report_type +
                '}';
    }
}
