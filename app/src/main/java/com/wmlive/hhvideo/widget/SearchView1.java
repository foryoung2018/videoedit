package com.wmlive.hhvideo.widget;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wmlive.hhvideo.utils.DeviceUtils;
import com.wmlive.hhvideo.utils.KLog;

import butterknife.BindView;
import cn.wmlive.hhvideo.R;

/**
 * Created by lsq on 5/31/2017.
 * 发现模块的搜索框
 */

public class SearchView1 extends BaseCustomView {
    @BindView(R.id.rlRoot)
    RelativeLayout rlRoot;
    @BindView(R.id.tvHint)
    TextView tvHint;
    @BindView(R.id.etInput)
    public EditText etInput;
    @BindView(R.id.ivTextIcon)
    ImageView ivTextIcon;
    @BindView(R.id.ivEditIcon)
    ImageView ivEditIcon;
    @BindView(R.id.ivDelete)
    ImageView ivDelete;
    @BindView(R.id.viewBottomLine)
    View viewBottomLine;
    private OnSearchClickListener searchClickListener;

    public SearchView1(Context context) {
        super(context);
    }

    public SearchView1(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void initViews(Context context, AttributeSet attrs, int defStyle) {
//        ivDelete.setOnClickListener(deleteListener);
        ivDelete.setOnClickListener(this);


        etInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(final CharSequence s, final int start, final int before, int count) {
                if (searchClickListener != null) {
                    searchClickListener.onTextChanged(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                ivDelete.setVisibility(!TextUtils.isEmpty(s.toString()) ? VISIBLE : INVISIBLE);
            }
        });
        etInput.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    DeviceUtils.showKeyBoard(etInput);
                } else {
                    DeviceUtils.hiddenKeyBoard(etInput);
                }
                if (searchClickListener != null) {
                    searchClickListener.onEditTextFocusChange(hasFocus);
                }
            }
        });
        etInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_SEARCH) {
                    DeviceUtils.hiddenKeyBoard(etInput);
                    etInput.clearFocus();
                    if (searchClickListener != null) {
                        searchClickListener.onKeyDoneClick(etInput.getText().toString().trim());
                    }
                }
                return false;
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_discovery_search_banner1;
    }


    public void showDiscoverySearch() {
        rlRoot.setBackground(null);
        viewBottomLine.setVisibility(VISIBLE);
    }

//    private MyClickListener deleteListener = new MyClickListener() {
//        @Override
//        protected void onMyClick(View v) {
//            String str = etInput.getText().toString();
//            if (str.length() > 0) {
//                str = str.substring(0, str.length() - 1);
//                etInput.setText(str);
//                etInput.setSelection(str.length());
//                if (null != searchClickListener) {
//                    searchClickListener.onDeleteClick(str.trim());
//                }
//            }
//        }
//    };

    @Override
    protected void onSingleClick(View v) {
        super.onSingleClick(v);
        switch (v.getId()) {
            case R.id.etInput:
                etInput.requestFocus();
                if (null != searchClickListener) {
                    searchClickListener.onEditViewClick(etInput.getEditableText().toString());
                }
                break;
            case R.id.ivDelete:
                etInput.setText("");
                etInput.requestFocus();
                break;
            default:
                break;
        }
    }

    /**
     * SearchView的各种事件监听
     *
     * @param searchClickListener
     */
    public void setSearchClickListener(OnSearchClickListener searchClickListener) {
        this.searchClickListener = searchClickListener;
    }

    /**
     * @param isSearch true表示可以输入进行搜索
     */
    public void setSearchMode(boolean isSearch) {
        ivTextIcon.setVisibility(isSearch ? GONE : VISIBLE);
        tvHint.setVisibility(isSearch ? GONE : VISIBLE);
        ivEditIcon.setVisibility(isSearch ? VISIBLE : GONE);
        etInput.setVisibility(isSearch ? VISIBLE : GONE);
        ivDelete.setVisibility(isSearch ? VISIBLE : GONE);
    }

    /**
     * 显示中间的提示
     *
     * @param isShow
     */
    public void showCenterHint(boolean isShow) {
        ivTextIcon.setVisibility(isShow ? VISIBLE : GONE);
        tvHint.setVisibility(isShow ? VISIBLE : GONE);

        ivEditIcon.setVisibility(isShow ? GONE : VISIBLE);
        etInput.setVisibility(isShow ? GONE : VISIBLE);
        if (!isShow) {
            etInput.requestFocus();
        } else {
            etInput.clearFocus();
        }
    }

    public EditText getEditText() {
        return etInput;
    }

    /**
     * 设置EditText的Hint
     *
     * @param hint
     * @return
     */
    public SearchView1 setEditHint(String hint) {
        etInput.setHint(hint);
        tvHint.setText(hint);
        return this;
    }

    /**
     * 设置TextView的Hint
     *
     * @param hint
     * @return
     */
    public SearchView1 setTextViewHint(String hint) {
        etInput.setHint(hint);
        return this;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        KLog.i("========SearchView onDetachedFromWindow");
//        deleteListener = null;
    }

    public interface OnSearchClickListener {
        void onEditViewClick(String text);

        void onDeleteClick(String text);

        void onTextChanged(String text);

        void onKeyDoneClick(String text);

        void onEditTextFocusChange(boolean hasFocus);

    }
}
