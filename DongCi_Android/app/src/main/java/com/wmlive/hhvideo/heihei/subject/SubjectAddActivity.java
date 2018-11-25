package com.wmlive.hhvideo.heihei.subject;

import android.content.Intent;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.wmlive.hhvideo.DCApplication;
import com.wmlive.hhvideo.common.base.DcBaseActivity;
import com.wmlive.hhvideo.heihei.beans.subject.TopicCreateResponse;
import com.wmlive.hhvideo.heihei.discovery.DiscoveryUtil;
import com.wmlive.hhvideo.heihei.subject.View.SubjectAddView;
import com.wmlive.hhvideo.heihei.subject.presenter.SubjectAddPresenter;
import com.wmlive.hhvideo.utils.KLog;
import com.wmlive.hhvideo.utils.ToastUtil;

import butterknife.BindView;
import cn.wmlive.hhvideo.R;

/**
 * 添加话题部分
 */
public class SubjectAddActivity extends DcBaseActivity<SubjectAddPresenter> implements SubjectAddView {
    public static final String SUBJECT_TITLE_FLAG = "SUBJECT_TITLE_FLAG";
    public static final String KEY_TOPIC = "key_topic";


    private static final int OPUS_TITLE_MAX_LENGTH = 28;
    private static final int OPUS_DESC_MAX_LENGTH = 60;

    @BindView(R.id.et_subject_add_content)
    EditText etTitle;
    @BindView(R.id.et_subject_add_description)
    EditText etDesc;
    @BindView(R.id.tv_subject_limit_count)
    TextView tvDescCount;
    @BindView(R.id.btn_subject_add_submit)
    TextView btn_subject_add_submit;

    private int iMaxSubjectContentLength = 28;
    private int iMaxSubjectDesLength = 60;

    private SubjectAddPresenter subjectAddPresenter;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_subject_add;
    }

    @Override
    protected SubjectAddPresenter getPresenter() {
        if (subjectAddPresenter == null) {
            subjectAddPresenter = new SubjectAddPresenter(this);
        }
        return subjectAddPresenter;
    }

    @Override
    protected void initData() {
        super.initData();
        setTitle("发起话题", true);


        InputFilter titleFilter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                KLog.d("xxxx", "source " + source + " start " + start + " end " + end + " dstart " + dstart + " dend " + dend);
                if (!TextUtils.isEmpty(source)) {
                    int destLength = dest.length();
                    if (destLength + source.length() > OPUS_TITLE_MAX_LENGTH) {
                        // 超出字数限制
                        ToastUtil.showToast(R.string.subject_add_error);
                        int incrementLength = OPUS_TITLE_MAX_LENGTH - destLength;
                        if (incrementLength >= 0 && incrementLength < source.length()) {
                            return source.subSequence(0, incrementLength);
                        } else {
                            return "";
                        }
                    } else {
                        // 字数范围内
                        return source;
                    }
                } else {
                    // 删除操作
                    return source;
                }
            }
        };
        etTitle.setFilters(new InputFilter[]{titleFilter});
        etTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });


        InputFilter descFilter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                KLog.d("xxxx", "source " + source + " start " + start + " end " + end + " dstart " + dstart + " dend " + dend);
                if (!TextUtils.isEmpty(source)) {
                    int destLength = dest.length();
                    if (destLength + source.length() > OPUS_DESC_MAX_LENGTH) {
                        // 超出字数限制
                        ToastUtil.showToast(R.string.subject_add_error);
                        int incrementLength = OPUS_DESC_MAX_LENGTH - destLength;
                        if (incrementLength >= 0 && incrementLength < source.length()) {
                            return source.subSequence(0, incrementLength);
                        } else {
                            return "";
                        }
                    } else {
                        // 字数范围内
                        return source;
                    }
                } else {
                    // 删除操作
                    return source;
                }
            }
        };
        etDesc.setFilters(new InputFilter[]{descFilter});
        etDesc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                setCountText(tvDescCount, charSequence.length());
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        btn_subject_add_submit.setOnClickListener(this);
        setCountText(tvDescCount, etDesc.getText().toString().trim().length());
        String strSubjectTitle = getIntent().getStringExtra(SUBJECT_TITLE_FLAG);
        etTitle.setText(strSubjectTitle);
    }

    private void setCountText(TextView tvDescCount, int length) {
        String textCount = length + "/" + OPUS_DESC_MAX_LENGTH;
        if (length >= OPUS_DESC_MAX_LENGTH) {
            SpannableString spanString = new SpannableString(textCount);
            ForegroundColorSpan span = new ForegroundColorSpan(getResources().getColor(R.color.hh_color_f));
            spanString.setSpan(span, 0, String.valueOf(length).length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            tvDescCount.setText(spanString);
        } else {
            tvDescCount.setText(textCount);
        }
    }

    private void showContentCount() {
        String content = etDesc.getText().toString().trim();
//        btn_subject_add_submit.setEnabled(!TextUtils.isEmpty(content));
        String showContent = (!TextUtils.isEmpty(content) ? content.length() : 0) + "/60";
        DiscoveryUtil.changeTextColor(tvDescCount, showContent, 0, showContent.length() - 3, 0xFFFF2120);
    }

    @Override
    public void topicCreateSuccess(TopicCreateResponse response) {
        //添加成功
        Intent intent = new Intent();
        intent.putExtra(KEY_TOPIC, response.getTopic());
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void topicCreateFail(String message) {
        showToast(message);
    }

    @Override
    protected void onSingleClick(View v) {
        switch (v.getId()) {
            case R.id.btn_subject_add_submit:
                //提交话题
                submitSubject();
                break;
        }

    }

    /**
     * 提交话题
     */
    public void submitSubject() {
        if (checkSubjectContentEmpty()) {
            ToastUtil.showToast(R.string.subject_add_content_empty);
            return;
        }
        if (checkSubjectDesEmpty()) {
            ToastUtil.showToast(R.string.subject_add_des_empty);
            return;
        }
        subjectAddPresenter.submitSubjectInfo(etTitle.getText().toString().trim(), etDesc.getText().toString().trim());
    }

    /**
     * 显示剩余数量
     *
     * @param count
     */
    public void showSubjectLimitCount(int count) {
        if (count < 0) {
            tvDescCount.setText(getText(R.string.subject_limit_count_content));
        } else {
            tvDescCount.setText(DCApplication.getDCApp().getResources().getString(R.string.subject_surplus_count_content, String.valueOf(count)));
        }
    }

    /**
     * 检查话题描述
     *
     * @return
     */
    public boolean checkSubjectDesEmpty() {
        String strDes = etDesc.getText().toString().trim();
        return TextUtils.isEmpty(strDes);
    }

    /**
     * 检查话题内容
     *
     * @return
     */
    public boolean checkSubjectContentEmpty() {
        String strContent = etTitle.getText().toString().trim();
        return TextUtils.isEmpty(strContent);
    }
}
