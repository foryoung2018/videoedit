package com.wmlive.hhvideo.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.constraint.ConstraintLayout;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wmlive.hhvideo.common.DcRouter;
import com.wmlive.hhvideo.heihei.beans.immessage.MessageButton;
import com.wmlive.hhvideo.heihei.db.MessageDetail;
import com.wmlive.hhvideo.utils.imageloader.GlideLoader;
import com.wmlive.hhvideo.utils.imageloader.LoadCallback;

import java.util.List;

import butterknife.BindView;
import cn.wmlive.hhvideo.R;

/**
 * Created by Administrator on 10/27/2017.
 */

public class MessageView extends BaseCustomView {
    private static final int MSG_DISMISS = 100;

    @BindView(R.id.ivImage)
    ImageView ivImage;
    @BindView(R.id.tvToastTitle)
    TextView tvToastTitle;
    @BindView(R.id.tvToastDesc)
    TextView tvToastDesc;
    @BindView(R.id.llToastView)
    LinearLayout llToastView;
    @BindView(R.id.tvDialogTitle)
    TextView tvDialogTitle;
    @BindView(R.id.tvDialogDesc)
    TextView tvDialogDesc;
    @BindView(R.id.tvLeft)
    TextView tvLeft;
    @BindView(R.id.rightLine)
    View rightLine;
    @BindView(R.id.topLine)
    View topLine;
    @BindView(R.id.tvCenter)
    TextView tvCenter;
    @BindView(R.id.leftLine)
    View leftLine;
    @BindView(R.id.tvRight)
    TextView tvRight;
    @BindView(R.id.llDialogView)
    LinearLayout llDialogView;
    @BindView(R.id.ivClose)
    ImageView ivClose;
    @BindView(R.id.clImage)
    ConstraintLayout clImage;
    @BindView(R.id.rlRoot)
    RelativeLayout rlRoot;

    private OnNextMessageListener messageListener;

    public MessageView(Context context) {
        super(context);
    }

    public MessageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MessageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_DISMISS:
                    MessageView.this.setVisibility(GONE);
                    if (messageListener != null) {
                        messageListener.onNextMessage();
                    }
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void initViews(Context context, AttributeSet attrs, int defStyle) {

    }

    @Override
    public void initData() {
        super.initData();
        ivClose.setOnClickListener(this);
        ivImage.setOnClickListener(this);
        tvLeft.setOnClickListener(this);
        tvRight.setOnClickListener(this);
        tvCenter.setOnClickListener(this);
        rlRoot.setOnClickListener(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_system_message;
    }

    public void showToastMessage(MessageDetail message) {
        if (message != null) {
            llDialogView.setVisibility(GONE);
            if (!TextUtils.isEmpty(message.image)) {

                GlideLoader.loadImage(message.image, ivImage, new LoadCallback() {
                    @Override
                    public void onDrawableLoaded(Drawable drawable) {
                        clImage.setVisibility(VISIBLE);
                        llToastView.setVisibility(VISIBLE);
                        rlRoot.setVisibility(VISIBLE);
                    }
                });
                ivImage.setTag(message.jump);
            } else {
                clImage.setVisibility(GONE);
            }
            tvToastTitle.setText(message.title);
            tvToastDesc.setText(message.desc);
            tvToastTitle.setVisibility(!TextUtils.isEmpty(message.title) ? VISIBLE : GONE);
            tvToastDesc.setVisibility(!TextUtils.isEmpty(message.desc) ? VISIBLE : GONE);
            this.setVisibility(VISIBLE);
            if (message.timeout > 0) {
                if (handler != null) {
                    handler.removeMessages(MSG_DISMISS);
                    handler.sendEmptyMessageDelayed(MSG_DISMISS, message.timeout * 1000);
                }
            }
        } else {
            this.setVisibility(GONE);
        }
    }

    public void showAlertMessage(MessageDetail message) {
        if (message != null) {
            llDialogView.setVisibility(VISIBLE);
            llToastView.setVisibility(GONE);
            tvDialogTitle.setText(message.title);
            tvDialogDesc.setText(message.desc);
            tvDialogTitle.setVisibility(!TextUtils.isEmpty(message.title) ? VISIBLE : GONE);
            tvDialogDesc.setVisibility(!TextUtils.isEmpty(message.desc) ? VISIBLE : GONE);
            List<MessageButton> buttons = message.buttons;
            if (buttons != null) {
                int size = buttons.size();
                switch (size) {
                    case 1:
                        tvLeft.setText(buttons.get(0).title);
                        tvLeft.setTag(buttons.get(0).jump);
                        break;
                    case 2:
                        tvRight.setText(buttons.get(1).title);
                        tvLeft.setTag(buttons.get(1).jump);
                        break;
                    case 3:
                        tvCenter.setText(buttons.get(2).title);
                        tvLeft.setTag(buttons.get(2).jump);
                        break;
                    default:
                        break;
                }
                topLine.setVisibility(size > 0 ? VISIBLE : GONE);
                tvLeft.setVisibility(size > 0 ? VISIBLE : GONE);
                tvRight.setVisibility(size > 1 ? VISIBLE : GONE);
                tvCenter.setVisibility(size > 2 ? VISIBLE : GONE);
                leftLine.setVisibility(size > 1 ? VISIBLE : GONE);
                rightLine.setVisibility(size > 2 ? VISIBLE : GONE);
            } else {
                topLine.setVisibility(GONE);
                leftLine.setVisibility(GONE);
                rightLine.setVisibility(GONE);
            }
            if (message.timeout > 0) {
                if (handler != null) {
                    handler.removeMessages(MSG_DISMISS);
                    handler.sendEmptyMessageDelayed(MSG_DISMISS, message.timeout * 1000);
                }
            }
            this.setVisibility(VISIBLE);
        } else {
            this.setVisibility(GONE);
        }
    }

    @Override
    protected void onSingleClick(View v) {
        super.onSingleClick(v);
        String jump = null;
        switch (v.getId()) {
            case R.id.rlRoot:
                return;
            case R.id.ivClose:
                this.setVisibility(GONE);
                break;
            case R.id.tvLeft:
                jump = (String) tvLeft.getTag();
                break;
            case R.id.tvRight:
                jump = (String) tvRight.getTag();
                break;
            case R.id.tvCenter:
                jump = (String) tvCenter.getTag();
                break;
            case R.id.ivImage:
                jump = (String) ivImage.getTag();
                this.setVisibility(GONE);
                break;
            default:
                break;
        }
        if (!TextUtils.isEmpty(jump)) {
            DcRouter.linkTo(getContext(), jump);
        } else {
            if (v.getId() != R.id.ivClose
                    && v.getId() != R.id.ivImage
                    && messageListener != null) {
                messageListener.onNextMessage();
            }
        }
    }

    public void setNextMessageListener(OnNextMessageListener messageListener) {
        this.messageListener = messageListener;
    }

    public interface OnNextMessageListener {
        void onNextMessage();
    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
            handler = null;
        }
    }
}
