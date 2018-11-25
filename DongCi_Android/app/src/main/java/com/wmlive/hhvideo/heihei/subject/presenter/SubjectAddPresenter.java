package com.wmlive.hhvideo.heihei.subject.presenter;

import android.text.TextUtils;

import com.wmlive.hhvideo.common.base.BasePresenter;
import com.wmlive.hhvideo.common.network.HttpConstant;
import com.wmlive.hhvideo.heihei.beans.splash.InitCatchData;
import com.wmlive.hhvideo.heihei.beans.subject.TopicCreateResponse;
import com.wmlive.hhvideo.heihei.subject.View.SubjectAddView;
import com.wmlive.networklib.observer.DCNetObserver;

/**
 * 话题增加
 * Created by admin on 2017/5/31.
 */

public class SubjectAddPresenter extends BasePresenter<SubjectAddView> {

    public SubjectAddPresenter(SubjectAddView view) {
        super(view);
    }

    /**
     * 添加话题
     *
     * @param subjectName
     * @param subjectDes
     */
    public void submitSubjectInfo(String subjectName, String subjectDes) {
        if (!TextUtils.isEmpty(subjectName) && !TextUtils.isEmpty(subjectDes)) {
            executeRequest(HttpConstant.TYPE_TOPIC_ADD_CODE, getHttpApi().createTopic(InitCatchData.topicCreate(), subjectName, subjectDes))
                    .subscribe(new DCNetObserver<TopicCreateResponse>() {
                        @Override
                        public void onRequestDataReady(int requestCode, String message, TopicCreateResponse response) {
                            if (viewCallback != null) {
                                viewCallback.topicCreateSuccess(response);
                            }
                        }

                        @Override
                        public void onRequestDataError(int requestCode, int serverCode, String message) {
                            if (viewCallback != null) {
                                viewCallback.topicCreateFail(message);
                            }
                        }
                    });
        }

    }

}
