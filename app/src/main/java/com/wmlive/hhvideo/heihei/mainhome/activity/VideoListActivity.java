package com.wmlive.hhvideo.heihei.mainhome.activity;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.wmlive.hhvideo.common.DcRouter;
import com.wmlive.hhvideo.common.GlobalParams;
import com.wmlive.hhvideo.common.base.DcBaseActivity;
import com.wmlive.hhvideo.common.manager.DcIjkPlayerManager;
import com.wmlive.hhvideo.heihei.beans.discovery.TopicInfoBean;
import com.wmlive.hhvideo.heihei.beans.main.MultiTypeVideoBean;
import com.wmlive.hhvideo.heihei.beans.main.ShareInfo;
import com.wmlive.hhvideo.heihei.beans.opus.ShareEventEntity;
import com.wmlive.hhvideo.heihei.mainhome.fragment.RecommendFragment;
import com.wmlive.hhvideo.utils.CommonUtils;
import com.wmlive.hhvideo.utils.DeviceUtils;
import com.wmlive.hhvideo.utils.KLog;
import com.wmlive.hhvideo.utils.MyClickListener;
import com.wmlive.hhvideo.utils.PopupWindowUtils;

import butterknife.BindView;
import cn.magicwindow.mlink.annotation.MLinkRouter;
import cn.wmlive.hhvideo.R;

/**
 * 视频播放列表页面
 * 进入话题、音乐、最新视频列表
 */
@MLinkRouter(keys = {GlobalParams.MWConfig.LINK_KEY_TOPIC})
public class VideoListActivity extends DcBaseActivity {

    @BindView(R.id.flContentContainer)
    public FrameLayout flContainer;

    private RecommendFragment recommendFragmentNew;
    private TextView tvName;
    private ShareInfo shareInfoBean;
    private ImageView ivShare;
    private PopupWindow shareWindow;
    private View ivAvatar;
    private MultiTypeVideoBean multiTypeVideoBean;
    private boolean isMwJump;
    private int type;
    private long topicId;

    public static void startVideoListActivity(Context context, int videoType, MultiTypeVideoBean userShortVideoParam) {
        startVideoListActivity(context, videoType, userShortVideoParam, null);
    }

    /**
     * @param context
     * @param videoType           视频的类型，参考{@link RecommendFragment }中{@link RecommendFragment#videoType}的Type参数
     * @param userShortVideoParam 根据{@link RecommendFragment#videoType}所传的参数{@link MultiTypeVideoBean}
     */
    public static void startVideoListActivity(Context context, int videoType, MultiTypeVideoBean userShortVideoParam, String routeKey) {
        Intent intent = new Intent(context, VideoListActivity.class);
        intent.putExtra(RecommendFragment.KEY_VIDEO_TYPE, videoType);
        intent.putExtra(RecommendFragment.KEY_VIDEO_LIST, userShortVideoParam);
        intent.putExtra(DcRouter.KEY_ROUTE_KEY, routeKey);
        context.startActivity(intent);
    }

    @Override
    protected void onSingleClick(View v) {

    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_video_list;
    }

    @Override
    protected void initData() {
        super.initData();
        Intent intent = getIntent();
        if (intent != null) {
            setTitle(" ", true);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            type = intent.getIntExtra(RecommendFragment.KEY_VIDEO_TYPE, RecommendFragment.TYPE_TOPIC);
            multiTypeVideoBean = (MultiTypeVideoBean) intent.getSerializableExtra(RecommendFragment.KEY_VIDEO_LIST);
            String mwTopicId = getIntent().getStringExtra("id");//从魔窗跳转过来的topicId
            KLog.i("======魔窗传过来的mwTopicId：" + mwTopicId);
            topicId = CommonUtils.stringParseLong(mwTopicId);
            if (multiTypeVideoBean == null) {
                multiTypeVideoBean = MultiTypeVideoBean.createTopicParma(topicId, 0, null);
            }
            isMwJump = topicId != -1L;
            if (type == RecommendFragment.TYPE_TOPIC) {
                View view = LayoutInflater.from(this).inflate(R.layout.view_topic_video_title, toolbar, false);
                tvName = view.findViewById(R.id.tvName);
                ivAvatar = view.findViewById(R.id.ivAvatar);
                toolbar.addCenterView(view, null);
                ivShare = new ImageView(this);
                ivShare.setImageResource(R.drawable.icon_tab_forwarding);
                ivShare.setPadding(10, 6, DeviceUtils.dip2px(this, 15), 6);
                ivShare.setVisibility(View.GONE);
                setToolbarRightView(ivShare, new MyClickListener() {
                    @Override
                    protected void onMyClick(View v) {
                        //话题列表顶部的分享按钮
                        shareWindow = PopupWindowUtils.showMusicShare(VideoListActivity.this, toolbar, shareClick);
                    }
                });
                if (multiTypeVideoBean != null) {
                    refreshTitle(multiTypeVideoBean.getTopicListBean());
                }
            }
            recommendFragmentNew = RecommendFragment.newInstance(type, multiTypeVideoBean);
            FragmentManager fragmentManager = getSupportFragmentManager();
            for (int i = 0; i < fragmentManager.getBackStackEntryCount(); i++) {
                fragmentManager.popBackStack();
            }
            fragmentManager
                    .beginTransaction()
                    .add(R.id.flContentContainer, recommendFragmentNew, "video_list_fragment")
                    .commit();

        } else {
            toastFinish();
        }
    }

    private MyClickListener shareClick = new MyClickListener() {
        @Override
        protected void onMyClick(View v) {
            shareInfoBean.objId = multiTypeVideoBean.getTopicId();
            shareInfoBean.shareType = ShareEventEntity.TYPE_TOPIC;
            switch (v.getId()) {
                case R.id.llWeChat:
                    shareInfoBean.shareTarget = ShareEventEntity.TARGET_WECHAT;
                    if (shareInfoBean.wxprogram_share_info != null) {
                        shareInfoBean.wxprogram_share_info.thumb_data = recommendFragmentNew.getFirstThumbUrl();
                        wxMinAppShare(0, shareInfoBean, null);
                    } else {
                        wechatShare(0, shareInfoBean);
                    }
                    break;
                case R.id.llCircle:
                    shareInfoBean.shareTarget = ShareEventEntity.TARGET_FRIEND;
                    wechatShare(1, shareInfoBean);
                    break;
                case R.id.llWeibo:
                    shareInfoBean.shareTarget = ShareEventEntity.TARGET_WEIBO;
                    weiboShare(shareInfoBean);
                    break;
                case R.id.llQQ:
                    shareInfoBean.shareTarget = ShareEventEntity.TARGET_QQ;
                    qqShare(shareInfoBean);
                    break;
                case R.id.llCopy:
                    if (shareInfoBean != null && !TextUtils.isEmpty(shareInfoBean.share_url)) {
                        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                        cm.setText(shareInfoBean.web_link);
                        showToast(R.string.copy_succeed);
                        shareInfoBean.shareTarget = ShareEventEntity.TARGET_WEB;
                        ShareEventEntity.share(shareInfoBean);
                    } else {
                        showToast("数据错误，复制链接失败");
                    }
                    break;
                default:
                    break;
            }
            if (null != shareWindow) {
                shareWindow.dismiss();
            }
        }
    };


    public void refreshTitle(TopicInfoBean bean) {
        if (bean != null && type == RecommendFragment.TYPE_TOPIC) {
            tvName.setText(bean.getName());
            ivAvatar.setVisibility(!TextUtils.isEmpty(bean.getName()) ? View.VISIBLE : View.GONE);
            shareInfoBean = bean.getShare_info();
            ivShare.setVisibility(shareInfoBean != null ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void onBack() {
        onKeyDown(KeyEvent.KEYCODE_BACK, new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK));
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (recommendFragmentNew != null && recommendFragmentNew.onBackPressed()) {
                return true;
            }

            DcIjkPlayerManager.get().resetUrl();
            DcIjkPlayerManager.get().removeListener(recommendFragmentNew.getPageId());
            finish();
            if (isMwJump) {
                MainActivity.startMainActivity(this);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    //恢复播放
    public void resumePlay() {
        if (null != recommendFragmentNew) {
            recommendFragmentNew.resumePlay();
        }
    }

}
