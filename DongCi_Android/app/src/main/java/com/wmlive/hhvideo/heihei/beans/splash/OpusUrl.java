package com.wmlive.hhvideo.heihei.beans.splash;

import com.wmlive.hhvideo.common.base.BaseModel;

/**
 * Created by vhawk on 2017/5/22.
 */

public class OpusUrl extends BaseModel {

    /**
     * listOpusByRecommend : http://api.dongci-test.wmlives.com/api/opus/list-opus-recommend
     * listOpusByFollow : http://api.dongci-test.wmlives.com/api/opus/list-opus-follow
     * listOpusByTime : http://api.dongci-test.wmlives.com/api/opus/list-opus
     * getOpus : http://api.dongci-test.wmlives.com/api/opus/get-opus
     * viewOpus : http://api.dongci-test.wmlives.com/api/opus/view-opus
     * likeOpus : http://api.dongci-test.wmlives.com/api/opus/like-opus
     * deleteOpus : http://api.dongci-test.wmlives.com/api/opus/delete-opus
     * manageOpus : http://api.dongci-test.wmlives.com/api/opus/manage,
     * commendOpus : http://api.dongci-test.wmlives.com/api/opus/commend-opus
     * listOpusComments : http://api.dongci-test.wmlives.com/api/opus/list-opus-comments
     * listOpusByMusic : http://api.dongci-test.wmlives.com/api/opus/list-opus-music
     * buyGiftBatch:/api/opus/buy-gift-batch
     * likeComment:/api/opus/like-comment
     * opusUploadMaterial:/api/opus/upload-material
     * publishProduct:/api/opus/save-opus
     * opusPointList=/api/opus/point-list
     * modifyOpus=http://api.dongci-test.wmlives.com/api/opus/modify-opus
     */

    private String saveOpus;
    private String listOpusByRecommend;
    private String listOpusByRecommendV2;
    private String listOpusByFollow;
    private String listOpusByTime;
    private String getOpus;
    private String viewOpus;
    private String likeOpus;
    private String deleteOpus;
    private String manageOpus;
    private String commendOpus;
    private String listOpusComment;
    private String deleteComment;
    private String listReportType;
    private String report;
    private String OpusLogs;
    private String buyGiftBatch;
    private String likeComment;
    private String opusUploadMaterial;
    private String getOpusMaterial;
    private String opusPointList;
    private String opusFrameLayout;
    private String modifyOpus;
    private String opusTopList;
    private String uploadCreativeOpus;
    private String uploadLocalOpus;

    public String getOpusTopRule() {
        return opusTopRule;
    }

    public void setOpusTopRule(String opusTopRule) {
        this.opusTopRule = opusTopRule;
    }

    private String opusTopRule;


    public String getUploadCreativeOpus() {
        return uploadCreativeOpus;
    }

    public void setUploadCreativeOpus(String uploadCreativeOpus) {
        this.uploadCreativeOpus = uploadCreativeOpus;
    }

    public String getUploadLocalOpus() {
        return uploadLocalOpus;
    }

    public void setUploadLocalOpus(String uploadLocalOpus) {
        this.uploadLocalOpus = uploadLocalOpus;
    }

    public String getBuyGiftBatch() {
        return buyGiftBatch;
    }

    public void setBuyGiftBatch(String buyGiftBatch) {
        this.buyGiftBatch = buyGiftBatch;
    }

    public String getOpusLogs() {
        return OpusLogs;
    }

    public OpusUrl setOpusLogs(String opusLogs) {
        OpusLogs = opusLogs;
        return this;
    }

    public String getListReportType() {
        return listReportType;
    }

    public OpusUrl setListReportType(String listReportType) {
        this.listReportType = listReportType;
        return this;
    }

    public String getReport() {
        return report;
    }

    public OpusUrl setReport(String report) {
        this.report = report;
        return this;
    }

    public String getDeleteComment() {
        return deleteComment;
    }

    public void setDeleteComment(String deleteComment) {
        this.deleteComment = deleteComment;
    }

    public String getSaveOpus() {
        return saveOpus;
    }

    public void setSaveOpus(String saveOpus) {
        this.saveOpus = saveOpus;
    }

    public String getListOpusByRecommend() {
        return listOpusByRecommend;
    }

    public void setListOpusByRecommend(String listOpusByRecommend) {
        this.listOpusByRecommend = listOpusByRecommend;
    }

    public String getListOpusByRecommendV2() {
        return listOpusByRecommendV2;
    }

    public void setListOpusByRecommendV2(String listOpusByRecommend) {
        this.listOpusByRecommendV2 = listOpusByRecommend;
    }

    public String getListOpusByFollow() {
        return listOpusByFollow;
    }

    public void setListOpusByFollow(String listOpusByFollow) {
        this.listOpusByFollow = listOpusByFollow;
    }

    public String getListOpusByTime() {
        return listOpusByTime;
    }

    public void setListOpusByTime(String listOpusByTime) {
        this.listOpusByTime = listOpusByTime;
    }

    public String getGetOpus() {
        return getOpus;
    }

    public void setGetOpus(String getOpus) {
        this.getOpus = getOpus;
    }

    public String getViewOpus() {
        return viewOpus;
    }

    public void setViewOpus(String viewOpus) {
        this.viewOpus = viewOpus;
    }

    public String getLikeOpus() {
        return likeOpus;
    }

    public void setLikeOpus(String likeOpus) {
        this.likeOpus = likeOpus;
    }

    public String getDeleteOpus() {
        return deleteOpus;
    }

    public void setDeleteOpus(String deleteOpus) {
        this.deleteOpus = deleteOpus;
    }

    public String getCommendOpus() {
        return commendOpus;
    }

    public void setCommendOpus(String commendOpus) {
        this.commendOpus = commendOpus;
    }

    public String getListOpusComment() {
        return listOpusComment;
    }

    public void setListOpusComment(String listOpusComment) {
        this.listOpusComment = listOpusComment;
    }

    public String getManageOpus() {
        return manageOpus;
    }

    public void setManageOpus(String manageOpus) {
        this.manageOpus = manageOpus;
    }

    public String getLikeComment() {
        return likeComment;
    }

    public void setLikeComment(String likeComment) {
        this.likeComment = likeComment;
    }

    public String getOpusUploadMaterial() {
        return opusUploadMaterial;
    }

    public void setOpusUploadMaterial(String opusUploadMaterial) {
        this.opusUploadMaterial = opusUploadMaterial;
    }

    public String getGetOpusMaterial() {
        return getOpusMaterial;
    }

    public void setGetOpusMaterial(String getOpusMaterial) {
        this.getOpusMaterial = getOpusMaterial;
    }

    public String getOpusFrameLayout() {
        return opusFrameLayout;
    }

    public void setOpusFrameLayout(String opusFrameLayout) {
        this.opusFrameLayout = opusFrameLayout;
    }

    public String getOpusPointList() {
        return opusPointList;
    }

    public void setOpusPointList(String opusPointList) {
        this.opusPointList = opusPointList;
    }

    public String getModifyOpus() {
        return modifyOpus;
    }

    public void setModifyOpus(String modifyOpus) {
        this.modifyOpus = modifyOpus;
    }

    public String getOpusTopList() {
        return opusTopList;
    }

    public void setOpusTopList(String opusTopList) {
        this.opusTopList = opusTopList;
    }

    @Override
    public String toString() {
        return "OpusUrl{" +
                "saveOpus='" + saveOpus + '\'' +
                ", listOpusByRecommend='" + listOpusByRecommend + '\'' +
                ", listOpusByFollow='" + listOpusByFollow + '\'' +
                ", listOpusByTime='" + listOpusByTime + '\'' +
                ", getOpus='" + getOpus + '\'' +
                ", viewOpus='" + viewOpus + '\'' +
                ", likeOpus='" + likeOpus + '\'' +
                ", deleteOpus='" + deleteOpus + '\'' +
                ", manageOpus='" + manageOpus + '\'' +
                ", commendOpus='" + commendOpus + '\'' +
                ", listOpusComment='" + listOpusComment + '\'' +
                ", deleteComment='" + deleteComment + '\'' +
                ", listReportType='" + listReportType + '\'' +
                ", report='" + report + '\'' +
                ", OpusLogs='" + OpusLogs + '\'' +
                ", opusUploadMaterial='" + opusUploadMaterial + '\'' +
                ", getOpusMaterial='" + getOpusMaterial + '\'' +
                ", opusFrameLayout='" + opusFrameLayout + '\'' +
                ", opusPointList='" + opusPointList + '\'' +
                ", modifyOpus='" + modifyOpus + '\'' +
                ", opusTopList='" + opusTopList + '\'' +
                '}';
    }
}
