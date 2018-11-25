package com.wmlive.hhvideo.heihei.mainhome.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.wmlive.hhvideo.common.base.BaseCompatActivity;
import com.wmlive.hhvideo.common.base.DcBaseFragment;
import com.wmlive.hhvideo.heihei.beans.main.ShortVideoItem;
import com.wmlive.hhvideo.heihei.mainhome.adapter.CommentPanelViewPagerAdapter;
import com.wmlive.hhvideo.heihei.mainhome.fragment.CommentListFragment;
import com.wmlive.hhvideo.heihei.mainhome.fragment.DecibelListFragment;
import com.wmlive.hhvideo.heihei.mainhome.view.RefreshCommentListener;
import com.wmlive.hhvideo.utils.CommonUtils;
import com.wmlive.hhvideo.widget.BaseCustomView;
import com.wmlive.hhvideo.widget.CustomFontTextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.wmlive.hhvideo.R;

public class CommentPanel extends BaseCustomView implements
        ViewPager.OnPageChangeListener, RefreshCommentListener {

    private static final byte TYPE_COMMENT = 0;
    private static final byte TYPE_DECIBEL = 1;
    @BindView(R.id.rlRoot)
    RelativeLayout rlRoot;
    @BindView(R.id.llRoot)
    LinearLayout llRoot;
    @BindView(R.id.tvComment)
    CustomFontTextView tvComment;
    @BindView(R.id.viewCommentLine)
    View viewCommentLine;
    @BindView(R.id.llComment)
    LinearLayout llComment;
    @BindView(R.id.tvDecibel)
    CustomFontTextView tvDecibel;
    @BindView(R.id.viewDecibelLine)
    View viewDecibelLine;
    @BindView(R.id.llDecibel)
    LinearLayout llDecibel;
    @BindView(R.id.llCountPanel)
    LinearLayout llCountPanel;
    @BindView(R.id.ivClose)
    ImageView ivClose;
    @BindView(R.id.tvDecibelTitle)
    CustomFontTextView tvDecibelTitle;
    @BindView(R.id.tvCommentTitle)
    CustomFontTextView tvCommentTitle;
    @BindView(R.id.viewBlankHolder)
    View viewBlankHolder;
    @BindView(R.id.vpContainer)
    ViewPager vpContainer;

    private long videoId;
    private int pageId;
    private int videoPosition;
    private byte currentType = TYPE_COMMENT;//0评论，1分贝
    private boolean hasLoadDecibel = false;
    private ShortVideoItem shortVideoItem;
    private CommentListFragment commentListFragment;
    private DecibelListFragment decibelListFragment;

    public CommentPanel(Context context) {
        super(context);
    }

    public CommentPanel(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CommentPanel(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.panel_comment;
    }

    @Override
    protected void initViews(Context context, AttributeSet attrs, int defStyle) {
        ivClose.setOnClickListener(this);
        llDecibel.setOnClickListener(this);
        llComment.setOnClickListener(this);
        viewBlankHolder.setOnClickListener(this);
        vpContainer.addOnPageChangeListener(this);
        vpContainer.setOffscreenPageLimit(2);
        commentListFragment = CommentListFragment.newInstance();
        decibelListFragment = DecibelListFragment.newInstance();
        List<DcBaseFragment> fragmentList = new ArrayList<>(2);
        fragmentList.add(commentListFragment);
        fragmentList.add(decibelListFragment);
        vpContainer.setAdapter(new CommentPanelViewPagerAdapter(((BaseCompatActivity) getContext()).getSupportFragmentManager(), fragmentList));
        commentListFragment.setRefreshCommentListener(this);
        decibelListFragment.setRefreshCommentListener(this);
        setSelect(currentType);
    }

    @Override
    protected void onSingleClick(View v) {
        super.onSingleClick(v);
        switch (v.getId()) {
            case R.id.llComment:
                if (currentType != TYPE_COMMENT) {
                    currentType = TYPE_COMMENT;
                    setSelect(currentType);
                    vpContainer.setCurrentItem(0);
                }
                break;
            case R.id.llDecibel:
                if (currentType != TYPE_DECIBEL) {
                    currentType = TYPE_DECIBEL;
                    setSelect(currentType);
                    vpContainer.setCurrentItem(1);
                    if (!hasLoadDecibel) {
                        decibelListFragment.refreshData(videoId, pageId, shortVideoItem.getComment_count(), videoPosition);
                    }
                    hasLoadDecibel = true;
                }
                break;
            case R.id.ivClose:
            case R.id.viewBlankHolder:
                dismiss();
                break;
            default:
                break;
        }
    }

    private void setSelect(byte type) {
        currentType = type;
        viewCommentLine.setVisibility(type == TYPE_COMMENT ? VISIBLE : INVISIBLE);
        viewDecibelLine.setVisibility(type == TYPE_COMMENT ? INVISIBLE : VISIBLE);
        tvComment.setTextColor(getResources().getColor(type == TYPE_COMMENT ? R.color.hh_color_dd : R.color.hh_color_aa));
        tvCommentTitle.setTextColor(getResources().getColor(type == TYPE_COMMENT ? R.color.hh_color_dd : R.color.hh_color_aa));
        tvDecibel.setTextColor(getResources().getColor(type == TYPE_COMMENT ? R.color.hh_color_aa : R.color.hh_color_dd));
        tvDecibelTitle.setTextColor(getResources().getColor(type == TYPE_COMMENT ? R.color.hh_color_aa : R.color.hh_color_dd));
    }

    public void refreshCommentCount() {
        if (shortVideoItem != null) {
            tvComment.setText(CommonUtils.getCountString(shortVideoItem.getComment_count(), false));
        }
    }

    public void refreshDecibelCount(int count) {
        tvDecibel.setText(CommonUtils.getCountString(count, false));
    }


    public void show(int pageId, long videoId, int videoPosition, ShortVideoItem shortVideoItem) {
        this.shortVideoItem = shortVideoItem;
        this.videoPosition = videoPosition;
        this.pageId = pageId;
        this.videoId = videoId;
        hasLoadDecibel = false;
        setVisibility(VISIBLE);
        currentType = TYPE_COMMENT;
        rlRoot.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.alpha_in));
        llRoot.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.anim_bottom_pop_in));
        setSelect(currentType);
        llRoot.postDelayed(new Runnable() {
            @Override
            public void run() {
                commentListFragment.refreshData(videoId, pageId, shortVideoItem.getComment_count(), videoPosition);
            }
        }, 300);
        refreshCommentCount();
        refreshDecibelCount(shortVideoItem.total_point);
    }

    public void dismiss() {
        llRoot.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.anim_bottom_pop_out));
        rlRoot.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.alpha_out));
        postDelayed(new Runnable() {
            @Override
            public void run() {
                if (commentListFragment != null) {
                    commentListFragment.clearData();
                }
                if (decibelListFragment != null) {
                    decibelListFragment.clearData();
                }
                if (currentType != TYPE_COMMENT) {
                    vpContainer.setCurrentItem(0);
                }
                hasLoadDecibel = false;
                setVisibility(GONE);
            }
        }, 300);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        setSelect(position == 0 ? TYPE_COMMENT : TYPE_DECIBEL);
        if (position == 1 && !hasLoadDecibel && shortVideoItem != null) {
            decibelListFragment.refreshData(videoId, pageId, shortVideoItem.getComment_count(), videoPosition);
            hasLoadDecibel = true;
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onRefreshComment(boolean isComment, boolean reset, int count) {
        if (isComment) {
            if (reset) {
                shortVideoItem.setComment_count(count);
            }
            refreshCommentCount();
        } else {
            if (reset) {
                shortVideoItem.total_point = count;
            }
            refreshDecibelCount(count);
        }
    }

    @Override
    public void onDismiss() {
        dismiss();
    }
}
