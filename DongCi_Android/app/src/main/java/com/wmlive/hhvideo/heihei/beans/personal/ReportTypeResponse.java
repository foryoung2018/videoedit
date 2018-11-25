package com.wmlive.hhvideo.heihei.beans.personal;

import com.wmlive.networklib.entity.BaseResponse;

import java.util.List;

/**
 * Created by XueFei on 2017/6/5.
 * <p>
 * 举报列表
 */

public class ReportTypeResponse extends BaseResponse {
    private List<ReportType> report_type_list;

    public ReportTypeResponse() {
    }

    public List<ReportType> getReport_type_list() {
        return report_type_list;
    }

    public void setReport_type_list(List<ReportType> report_type_list) {
        this.report_type_list = report_type_list;
    }

    @Override
    public String toString() {
        return "ReportTypeResponse{" +
                "report_type_list=" + report_type_list +
                '}';
    }
}
