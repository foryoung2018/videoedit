package com.wmlive.hhvideo.widget.dialog;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wmlive.hhvideo.heihei.beans.splash.InitCatchData;
import com.wmlive.hhvideo.heihei.beans.splash.TipsUrl;
import com.wmlive.hhvideo.heihei.mainhome.adapter.EmotionGridViewAdapter;
import com.wmlive.hhvideo.heihei.mainhome.adapter.EmotionPagerAdapter;
import com.wmlive.hhvideo.heihei.mainhome.util.EmotionKeyboard;
import com.wmlive.hhvideo.heihei.mainhome.util.GlobalOnItemClickManagerUtils;
import com.wmlive.hhvideo.utils.KLog;
import com.wmlive.hhvideo.utils.ScreenUtil;
import com.wmlive.hhvideo.widget.CommentEditTextView;
import com.wmlive.hhvideo.widget.emojiview.EmojiIndicatorView;

import java.util.ArrayList;
import java.util.List;

import cn.wmlive.hhvideo.R;

import static com.wmlive.hhvideo.utils.ToastUtil.showToast;

/**
 * Created by lsq on 4/25/2018 - 12:30 PM
 * 类描述：评论框
 */
public class CommentDialog extends DialogFragment {

    private CommentEditTextView etComment;
    private TextView tvSend;
    private CommentListener listener;
    private Context context;
    private String hint;
    private String msg;
    private ImageView iv_emoji;
    private EmojiIndicatorView ll_point_group;
    private ViewPager vp_emotion;
    private EmotionPagerAdapter emotionPagerGvAdapter;
    private EmotionKeyboard mEmotionKeyboard;
    private LinearLayout ll_emotion_layout;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Holo_Light_DialogWhenLarge_NoActionBar);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.activity_comment, null);
        etComment = view.findViewById(R.id.etComment);
        tvSend = view.findViewById(R.id.tvSend);
        iv_emoji = view.findViewById(R.id.iv_emoji);
        etComment.requestFocus();


        ll_emotion_layout = view.findViewById(R.id.ll_emotion_layout);
        //自定义表情页面
        vp_emotion = view.findViewById(R.id.vp_emotion);
        //底部小圆圈
        ll_point_group = view.findViewById(R.id.ll_point_group);
//        vp_emotion.setAdapter();
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        if (TextUtils.isEmpty(msg)) {
            etComment.setHint(hint);
        } else {
            etComment.setText(msg);
        }
        etComment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = etComment.getText().toString().trim();
                if (text.length() > 100) {
                    text = text.substring(0, 100);
                    etComment.setText(text);
                    etComment.setSelection(text.length());
                    showToast(R.string.comment_content_length_limit);
                } else if (text.length() == 0) {
                    etComment.setHint(hint);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString().trim();
                KLog.i("=======text:" + text + "\nlength:" + text.length());
                tvSend.setEnabled(!TextUtils.isEmpty(text));
            }
        });
        tvSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    String comment = etComment.getText().toString().trim();
                    if (!TextUtils.isEmpty(comment)) {
                        listener.onSendComment(comment);
                    }
                }
                dismiss();
            }
        });
        view.findViewById(R.id.viewBlankHolder).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        view.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (getActivity() != null && !getActivity().isDestroyed()) {
                    view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        private boolean isShow = true;

                        @Override
                        public void onGlobalLayout() {
                            int heightDiff = view.getRootView().getHeight() - view.getHeight();
                            if (heightDiff > 200) {
                                isShow = true;
                            } else {
                                if (isShowing() && !ll_emotion_layout.isShown() && isShow) {//消失的条件只能是点击键盘上下想下按钮
                                    dismiss();
                                }
                                isShow = false;
                            }
                        }
                    });
                }
            }
        }, 500);


        mEmotionKeyboard = EmotionKeyboard.with(getActivity())
                .setEmotionView(ll_emotion_layout)//绑定表情面板
                .bindToContent(view.findViewById(R.id.viewBlankHolder))//绑定内容view
                .bindToEditText(etComment)//判断绑定那种EditView
                .bindToEmotionButton(iv_emoji)//绑定表情按钮
                .build();
        List<String> emojiGroup = InitCatchData.getInitCatchData().getTips().emojiGroup.emojiDefault;
        KLog.d("emojiGroup==" + emojiGroup);
        initEmotion(emojiGroup);
        GlobalOnItemClickManagerUtils globalOnItemClickManager = GlobalOnItemClickManagerUtils.getInstance(getActivity());
        globalOnItemClickManager.attachToEditText(etComment);

        vp_emotion.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            int oldPagerPos = 0;

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                ll_point_group.playByStartPointToNext(oldPagerPos, position);
                oldPagerPos = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        return view;
    }

    public void showDialog(Activity context, String msg, String replyName, CommentListener listener) {
        this.msg = msg;
        this.context = context;
        this.hint = replyName;
        this.listener = listener;
    }

    @Override
    public void dismiss() {
        super.dismiss();
        if (listener != null) {
            listener.onDialogDismiss(etComment.getText().toString().trim());
        }
    }

    public boolean isShowing() {
        return !this.isHidden();
    }

    public interface CommentListener {
        void onSendComment(String comment);

        void onDialogDismiss(String lastMsg);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (listener != null) {
            listener.onDialogDismiss(etComment.getText().toString().trim());
        }
    }

    private void initEmotion(List<String> emojiGroup) {
        // 获取屏幕宽度
        int screenWidth = ScreenUtil.getWidth(getActivity());
        // item的间距
        int spacing = ScreenUtil.dip2px(getActivity(), 6);
        // 动态计算item的宽度和高度
        int itemWidth = (screenWidth - spacing * 8) / 7;
        //动态计算gridview的总高度
        int gvHeight = itemWidth * 4 + spacing * 6;

        List<GridView> emotionViews = new ArrayList<>();
        List<String> emotionNames = new ArrayList<>();
        // 遍历所有的表情的key
        for (String emojiName : emojiGroup) {
            emotionNames.add(emojiName);
            // 每20个表情作为一组,同时添加到ViewPager对应的view集合中
            if (emotionNames.size() == 27) {
                GridView gv = createEmotionGridView(emotionNames, screenWidth, spacing, itemWidth, gvHeight);
                emotionViews.add(gv);
                // 添加完一组表情,重新创建一个表情名字集合
                emotionNames = new ArrayList<>();
            }
        }

        // 判断最后是否有不足20个表情的剩余情况
        if (emotionNames.size() > 0) {
            GridView gv = createEmotionGridView(emotionNames, screenWidth, spacing, itemWidth, gvHeight);
            emotionViews.add(gv);
        }

        //初始化指示器
        ll_point_group.initIndicator(emotionViews.size());
        // 将多个GridView添加显示到ViewPager中
        emotionPagerGvAdapter = new EmotionPagerAdapter(emotionViews);
        vp_emotion.setAdapter(emotionPagerGvAdapter);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(screenWidth, gvHeight);
        vp_emotion.setLayoutParams(params);


    }

    private GridView createEmotionGridView(List<String> emotionNames, int gvWidth, int padding, int itemWidth, int gvHeight) {
        // 创建GridView
        GridView gv = new GridView(getActivity());
        //设置点击背景透明
        gv.setSelector(android.R.color.transparent);
        //设置7列
        gv.setNumColumns(7);
        gv.setPadding(padding, padding, padding, padding);
        gv.setHorizontalSpacing(padding);
        gv.setVerticalSpacing(padding * 2);
        //设置GridView的宽高
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        gv.setLayoutParams(params);
        // 给GridView设置表情图片
        EmotionGridViewAdapter adapter = new EmotionGridViewAdapter(getActivity(), emotionNames, itemWidth, 1);
        gv.setAdapter(adapter);
        //设置全局点击事件
        gv.setOnItemClickListener(GlobalOnItemClickManagerUtils.getInstance(getActivity()).getOnItemClickListener(1));
        return gv;
    }

}
