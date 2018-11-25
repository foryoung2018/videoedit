package com.wmlive.hhvideo.heihei.subject.View;

import com.wmlive.hhvideo.common.base.BaseView;
import com.wmlive.hhvideo.heihei.beans.subject.TopicCreateResponse;


/**
 * 添加话题信息
 */
public interface SubjectAddView extends BaseView {

    /**
     * 话题添加成功
     */
    void topicCreateSuccess(TopicCreateResponse response);

    void topicCreateFail(String message);

}
