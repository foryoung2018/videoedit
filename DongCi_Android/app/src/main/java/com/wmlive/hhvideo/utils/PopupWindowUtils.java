package com.wmlive.hhvideo.utils;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationUtils;
import android.widget.PopupWindow;

import cn.wmlive.hhvideo.R;

import static cn.wmlive.hhvideo.R.id.llDelete;

/**
 * Created by lsq on 6/1/2017.
 * Popup的工具类
 */

public class PopupWindowUtils {


    public static PopupWindow showSelfShare(Context context, View anchor, MyClickListener listener, boolean showManager, boolean isTeamwork) {
        View shareView = LayoutInflater.from(context).inflate(R.layout.view_share_panel, null);
        showNormal(listener, shareView);
        View llCopy = shareView.findViewById(R.id.llCopy);
        llCopy.setVisibility(View.VISIBLE);
        llCopy.setOnClickListener(listener);
        View llSave = shareView.findViewById(R.id.llSave);
        llSave.setOnClickListener(listener);
        llSave.setVisibility(View.VISIBLE);
        View llDelete = shareView.findViewById(R.id.llDelete);
        llDelete.setOnClickListener(listener);
        llDelete.setVisibility(View.VISIBLE);
        View llManager = shareView.findViewById(R.id.llManager);
        if (showManager) {
            llManager.setOnClickListener(listener);
            llManager.setVisibility(View.VISIBLE);
        } else {
            llManager.setVisibility(View.GONE);
        }
        View llEdit = shareView.findViewById(R.id.llEdit);
        llEdit.setVisibility(View.VISIBLE);
        llEdit.setOnClickListener(listener);

        shareView.findViewById(R.id.llReport).setVisibility(View.GONE);
        return PopupWindowUtils.createPopWindowFromBottom(anchor, shareView, 1f);
    }

    public static PopupWindow showOtherShare(Context context, View anchor, MyClickListener listener, boolean showManager) {
        View shareView = LayoutInflater.from(context).inflate(R.layout.view_share_panel, null);
        showNormal(listener, shareView);
        View llCopy = shareView.findViewById(R.id.llCopy);
        llCopy.setVisibility(View.VISIBLE);
        llCopy.setOnClickListener(listener);
        View llSave = shareView.findViewById(R.id.llSave);
        llSave.setOnClickListener(listener);
        llSave.setVisibility(View.VISIBLE);
        View llReport = shareView.findViewById(R.id.llReport);
        llReport.setOnClickListener(listener);
        llReport.setVisibility(View.VISIBLE);

        View llManager = shareView.findViewById(R.id.llManager);
        if (showManager) {
            llManager.setOnClickListener(listener);
            llManager.setVisibility(View.VISIBLE);
        } else {
            llManager.setVisibility(View.INVISIBLE);
        }

        shareView.findViewById(R.id.llDelete).setVisibility(View.GONE);
        return PopupWindowUtils.createPopWindowFromBottom(anchor, shareView, 1f);
    }

    /**
     * 分享音乐的弹窗
     * 底部：复制链接
     *
     * @param context
     * @param anchor
     * @param listener
     * @return
     */
    public static PopupWindow showMusicShare(Context context, View anchor, MyClickListener listener) {
        View shareView = LayoutInflater.from(context).inflate(R.layout.view_share_panel, null);
        showNormal(listener, shareView);
        View llCopy = shareView.findViewById(R.id.llCopy);
        llCopy.setOnClickListener(listener);
        llCopy.setVisibility(View.VISIBLE);
        shareView.findViewById(R.id.llSave).setVisibility(View.INVISIBLE);
        shareView.findViewById(R.id.llReport).setVisibility(View.INVISIBLE);
        shareView.findViewById(llDelete).setVisibility(View.INVISIBLE);
        shareView.findViewById(R.id.llManager).setVisibility(View.GONE);
        return PopupWindowUtils.createPopWindowFromBottom(anchor, shareView);
    }

    /**
     * 普通分享  只有微信 朋友圈 qq 新浪 复制链接
     *
     * @param context
     * @param anchor
     * @param listener
     * @return
     */
    public static PopupWindow showNormal(Context context, View anchor, MyClickListener listener) {
        View shareView = LayoutInflater.from(context).inflate(R.layout.view_share_panel, null);
        showNormal(listener, shareView);
        View llCopy = shareView.findViewById(R.id.llCopy);
        llCopy.setVisibility(View.VISIBLE);
        llCopy.setOnClickListener(listener);
        shareView.findViewById(R.id.llSave).setVisibility(View.INVISIBLE);
        shareView.findViewById(R.id.llReport).setVisibility(View.INVISIBLE);
        shareView.findViewById(R.id.llDelete).setVisibility(View.INVISIBLE);
        shareView.findViewById(R.id.llManager).setVisibility(View.GONE);
        return PopupWindowUtils.createPopWindowFromBottom(anchor, shareView);
    }

    /**
     * 作品发布成功后的分享
     *
     * @param context
     * @param anchor
     * @param listener
     * @return
     */
    public static PopupWindow showUploadResultPanel(Context context, View anchor, MyClickListener listener) {
        View shareView = LayoutInflater.from(context).inflate(R.layout.view_share_panel, null);
        showNormal(listener, shareView);
        shareView.findViewById(R.id.llRow2).setVisibility(View.GONE);
        shareView.findViewById(R.id.tvUploadLabel).setVisibility(View.VISIBLE);
        shareView.findViewById(R.id.tvOk).setVisibility(View.VISIBLE);
        shareView.findViewById(R.id.tvOk).setOnClickListener(listener);
        return new PopupWindowUtils.Builder(anchor, shareView)
//                .setAnimationStyle(R.style.AnimationFromBottom)
                .setOutsideTouchable(false)
                .setWidth(-1)
                .setGravity(Gravity.BOTTOM)
                .setBgAlpha(1)
                .create();
    }

    private static void showNormal(MyClickListener listener, View shareView) {
        shareView.findViewById(R.id.llWeChat).setOnClickListener(listener);
        shareView.findViewById(R.id.llCircle).setOnClickListener(listener);
        shareView.findViewById(R.id.llWeibo).setOnClickListener(listener);
        shareView.findViewById(R.id.llQQ).setOnClickListener(listener);
    }

    public static PopupWindow createPopWindowFromBottom(View anchor, Object view) {
        return createPopWindow(anchor, view);
    }

    public static PopupWindow createPopWindow(View anchor, Object view) {
        return createPopWindowFromBottom(anchor, view, 1f);
    }


    /**
     * 从anchor的底部弹出popwindow
     *
     * @param anchor
     * @param view   popwindow中的内容
     * @return
     */
    public static PopupWindow createPopWindowFromBottom(View anchor, Object view, float alpha) {
        return new PopupWindowUtils.Builder(anchor, view)
//                .setAnimationStyle(R.style.AnimationFromBottom)
                .setWidth(-1)
                .setGravity(Gravity.BOTTOM)
                .setBgAlpha(alpha)
                .create();
    }

    public static class Builder {
        private View anchor;
        private Object view;
        private int gravity = Gravity.CENTER;
        private boolean outside_touchable = true;
        private boolean focusable = true;
        private boolean showAtCurrentPosition = false;
        private int width = -2;    //宽度默认包裹内容
        private int height = -2;   //高度默认包裹内容
        private int offset_x = 0;
        private int offset_y = 0;
        private float bgAlpha = 0.0f;  //背景透明度
        private int anim_id = -1;


        public Builder(View anchor, Object view) {
            this.anchor = anchor;
            this.view = view;
        }


        /**
         * 设置弹出位置
         *
         * @param gravity
         * @return
         */
        public Builder setGravity(int gravity) {
            this.gravity = gravity;
            return this;
        }

        /**
         * 是否在当前位置显示
         *
         * @param showAtCurrentPosition
         * @return
         */
        public Builder setShowAtCurrentPosition(boolean showAtCurrentPosition) {
            this.showAtCurrentPosition = showAtCurrentPosition;
            return this;
        }

        /**
         * 设置宽度，默认是包裹内容
         *
         * @param width
         * @return
         */
        public Builder setWidth(int width) {
            this.width = width;
            return this;
        }


        /**
         * 设置高度，默认是包裹内容
         *
         * @param height
         * @return
         */
        public Builder setHeight(int height) {
            this.height = height;
            return this;
        }

        /**
         * 水平偏移量
         *
         * @param offset_x
         * @return
         */
        public Builder setOffsetX(int offset_x) {
            this.offset_x = offset_x;
            return this;
        }

        /**
         * 垂直偏移量
         *
         * @param offset_y
         * @return
         */
        public Builder setOffsetY(int offset_y) {
            this.offset_y = offset_y;
            return this;
        }

        /**
         * 外部是否可点击
         *
         * @param outside_touchable
         * @return
         */
        public Builder setOutsideTouchable(boolean outside_touchable) {
            this.outside_touchable = outside_touchable;
            return this;
        }

        public Builder setFocusable(boolean focusable) {
            this.focusable = focusable;
            return this;
        }

        /**
         * 设置动画
         *
         * @param anim_id
         * @return
         */
        public Builder setAnimationStyle(int anim_id) {
            this.anim_id = anim_id;
            return this;
        }

        /**
         * 设置弹出背景透明度，0.0f完全透明，1.0f完全不透明
         *
         * @param bgAlpha
         * @return
         */
        public Builder setBgAlpha(float bgAlpha) {
            this.bgAlpha = bgAlpha;
            return this;
        }


        public PopupWindow createDefault(final View anchor,
                                         Object view,
                                         int gravity,
                                         boolean outside_touchable,
                                         boolean focusable,
                                         boolean showAtCurrentPosition,
                                         int width, int height,
                                         int offset_x,
                                         int offset_y,
                                         int anim_id) {
            View pop_view = null;
            if (view instanceof Integer) {
                pop_view = LayoutInflater.from(anchor.getContext()).inflate((Integer) view, null, false);
            }
            if (view instanceof View) {
                pop_view = (View) view;

            }
            final PopupWindow popWindow = new PopupWindow(pop_view, width, height, focusable);
            popWindow.update();
            popWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
//            popWindow.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
            KLog.i("=======popWindow");
            popWindow.update();
//            if (outside_touchable) {
//                ColorDrawable dw = new ColorDrawable(0x00000000);
//                popWindow.setBackgroundDrawable(dw);
//            }
//            popWindow.setOutsideTouchable(outside_touchable);
            if (anim_id != -1) {
                popWindow.setAnimationStyle(anim_id);
            }
            if (bgAlpha >= 0.0f && bgAlpha <= 1.0f) {
                WindowManager.LayoutParams params = ((Activity) popWindow.getContentView().getContext()).getWindow().getAttributes();
                params.alpha = bgAlpha;
                ((Activity) popWindow.getContentView().getContext()).getWindow().setAttributes(params);
                ((Activity) popWindow.getContentView().getContext()).getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            }
            popWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    if (bgAlpha > 0.0f && bgAlpha <= 1.0f) {
                        closePopupWindow(popWindow);
                    }
                }
            });

            if (showAtCurrentPosition) {
                popWindow.showAsDropDown(anchor, offset_x, offset_y);
            } else {
                popWindow.showAtLocation(anchor, gravity, offset_x, offset_y);
            }

            if (pop_view != null) {
                View holder = pop_view.findViewById(R.id.viewBlankHolder);
                View llPanel = pop_view.findViewById(R.id.llPanel);
                View llRoot = pop_view.findViewById(R.id.llRoot);
                if (holder != null) {
                    AlphaAnimation aa = new AlphaAnimation(0.0f, 0.3f);
                    aa.setDuration(300);
                    holder.startAnimation(aa);
                    llPanel.startAnimation(AnimationUtils.loadAnimation(anchor.getContext(), R.anim.anim_bottom_pop_in));
                    holder.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (outside_touchable) {
                                AlphaAnimation aa = new AlphaAnimation(0.3f, 0.0f);
                                aa.setDuration(300);
                                holder.startAnimation(aa);
                                llPanel.startAnimation(AnimationUtils.loadAnimation(anchor.getContext(), R.anim.anim_bottom_pop_out));
                                llPanel.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        popWindow.dismiss();
                                    }
                                }, 300);
                            }
                        }
                    });
                }
            }
            return popWindow;
        }


        public PopupWindow create() {
            return createDefault(anchor, view, gravity, outside_touchable, focusable, showAtCurrentPosition, width, height, offset_x, offset_y, anim_id);
        }

        private void closePopupWindow(PopupWindow popWindow) {
            if (popWindow != null) {
                WindowManager.LayoutParams params = ((Activity) popWindow.getContentView().getContext()).getWindow().getAttributes();
                params.alpha = 1f;
                ((Activity) popWindow.getContentView().getContext()).getWindow().setAttributes(params);
                ((Activity) popWindow.getContentView().getContext()).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            }
        }

    }

}
