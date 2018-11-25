package com.wmlive.hhvideo.heihei.mainhome.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.wmlive.hhvideo.utils.MyClickListener;
import com.wmlive.hhvideo.widget.BaseCustomView;

import butterknife.BindView;
import cn.wmlive.hhvideo.R;

/**
 * Created by lsq on 2/8/2018.11:07 AM
 *
 * @author lsq
 * @describe 添加描述
 */

public class MessageTabPanel extends BaseCustomView {

    @BindView(R.id.tabChat)
    MessageTabView tabChat;
    @BindView(R.id.tabProduct)
    MessageTabView tabProduct;
    @BindView(R.id.tabFans)
    MessageTabView tabFans;
    @BindView(R.id.tabLike)
    MessageTabView tabLike;
    private int selectIndex = 0;
    private OnMessageTabSelectListener onMessageTabSelectListener;

    private int[] selectIcon = {
            R.drawable.icon_message_chat_sel,
            R.drawable.icon_message_production_sel,
            R.drawable.icon_messahe_fans_sel,
            R.drawable.icon_message_like_sel};
    private int[] unselectIcon = {
            R.drawable.icon_message_chat_nor,
            R.drawable.icon_message_production_nor,
            R.drawable.icon_messahe_fans_nor,
            R.drawable.icon_message_like_nor};

    public MessageTabPanel(Context context) {
        this(context, null);
    }

    public MessageTabPanel(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MessageTabPanel(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void initViews(Context context, AttributeSet attrs, int defStyle) {

    }

    @Override
    public void initData() {
        super.initData();
        tabChat.setData(R.string.attention, unselectIcon[0]);
        tabProduct.setData(R.string.chat, unselectIcon[1]);
        tabFans.setData(R.string.comment, unselectIcon[2]);
        tabLike.setData(R.string.likeandfans, unselectIcon[3]);

        tabChat.setOnClickListener(myClickListener);
        tabProduct.setOnClickListener(myClickListener);
        tabFans.setOnClickListener(myClickListener);
        tabLike.setOnClickListener(myClickListener);
        setSelect(0);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_message_tab_panel;
    }

    public void setSelect(int index) {
        boolean select = index == 0;
        tabChat.setSelected(select, select ? selectIcon[0] : unselectIcon[0], select ? R.color.black : R.color.pop_text_gray);
        select = index == 1;
        tabProduct.setSelected(select, select ? selectIcon[1] : unselectIcon[1], select ? R.color.black : R.color.pop_text_gray);
        select = index == 2;
        tabFans.setSelected(select, select ? selectIcon[2] : unselectIcon[2], select ? R.color.black : R.color.pop_text_gray);
        select = index == 3;
        tabLike.setSelected(select, select ? selectIcon[3] : unselectIcon[3], select ? R.color.black : R.color.pop_text_gray);

    }

    public void setMessageCount(int index, long count) {
        switch (index) {
            case 0:
                tabChat.setMessageCount(count);
                break;
            case 1:
                tabProduct.setMessageCount(count);
                break;
            case 2:
                tabFans.setMessageCount(count);
                break;
            case 3:
                tabLike.setMessageCount(count);
                break;
            default:
                break;
        }

    }

    public void setOnMessageTabSelectListener(OnMessageTabSelectListener onMessageTabSelectListener) {
        this.onMessageTabSelectListener = onMessageTabSelectListener;
    }

    private MyClickListener myClickListener = new MyClickListener() {
        @Override
        protected void onMyClick(View view) {
            if (onMessageTabSelectListener != null) {
                switch (view.getId()) {
                    case R.id.tabChat:
                        onMessageTabSelectListener.onTabSelect1();
                        setSelect(0);
                        break;
                    case R.id.tabProduct:
                        onMessageTabSelectListener.onTabSelect2();
                        setSelect(1);
                        break;
                    case R.id.tabFans:
                        onMessageTabSelectListener.onTabSelect3();
                        setSelect(2);
                        break;
                    case R.id.tabLike:
                        onMessageTabSelectListener.onTabSelect4();
                        setSelect(3);
                        break;
                    default:
                        break;
                }
            }

        }
    };

    public interface OnMessageTabSelectListener {
        void onTabSelect1();

        void onTabSelect2();

        void onTabSelect3();

        void onTabSelect4();
    }
}
