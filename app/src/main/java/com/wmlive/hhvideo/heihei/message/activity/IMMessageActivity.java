package com.wmlive.hhvideo.heihei.message.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.lamemp3.MP3Recorder;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.wmlive.hhvideo.DCApplication;
import com.wmlive.hhvideo.common.DcRouter;
import com.wmlive.hhvideo.common.GlobalParams;
import com.wmlive.hhvideo.common.base.BaseCompatActivity;
import com.wmlive.hhvideo.common.base.DcBaseActivity;
import com.wmlive.hhvideo.common.manager.gift.GiftPresenter;
import com.wmlive.hhvideo.common.manager.greendao.GreenDaoManager;
import com.wmlive.hhvideo.common.manager.message.MessageManager;
import com.wmlive.hhvideo.heihei.beans.gifts.GiftEntity;
import com.wmlive.hhvideo.heihei.beans.gifts.SendGiftResultResponse;
import com.wmlive.hhvideo.heihei.beans.immessage.DcMessage;
import com.wmlive.hhvideo.heihei.beans.immessage.MessageContent;
import com.wmlive.hhvideo.heihei.beans.login.UserInfo;
import com.wmlive.hhvideo.heihei.beans.splash.InitCatchData;
import com.wmlive.hhvideo.heihei.db.MessageDetail;
import com.wmlive.hhvideo.heihei.login.AccountUtil;
import com.wmlive.hhvideo.heihei.mainhome.adapter.EmotionGridViewAdapter;
import com.wmlive.hhvideo.heihei.mainhome.adapter.EmotionPagerAdapter;
import com.wmlive.hhvideo.heihei.mainhome.presenter.SendGiftPresenter;
import com.wmlive.hhvideo.heihei.mainhome.util.EmotionKeyboard;
import com.wmlive.hhvideo.heihei.mainhome.util.GlobalOnItemClickManagerUtils;
import com.wmlive.hhvideo.heihei.mainhome.widget.GiftView;
import com.wmlive.hhvideo.heihei.message.RingtoneController;
import com.wmlive.hhvideo.heihei.message.adapter.IMMessageAdapter;
import com.wmlive.hhvideo.heihei.message.listener.IMMediaPlayOtherSoundListener;
import com.wmlive.hhvideo.heihei.message.listener.MediaPlayerStatusListener;
import com.wmlive.hhvideo.heihei.message.utils.IMMediaPlayUtils;
import com.wmlive.hhvideo.heihei.message.utils.IMUtils;
import com.wmlive.hhvideo.heihei.message.utils.RecorderAndPlayUtil;
import com.wmlive.hhvideo.heihei.message.viewholder.MeSoundViewHolder;
import com.wmlive.hhvideo.heihei.message.viewholder.OtherSoundViewHolder;
import com.wmlive.hhvideo.heihei.personal.activity.UserAccountEarningsActivity;
import com.wmlive.hhvideo.heihei.personal.activity.UserHomeActivity;
import com.wmlive.hhvideo.utils.AppCacheFileUtils;
import com.wmlive.hhvideo.utils.CollectionUtil;
import com.wmlive.hhvideo.utils.DeviceUtils;
import com.wmlive.hhvideo.utils.JsonUtils;
import com.wmlive.hhvideo.utils.KLog;
import com.wmlive.hhvideo.utils.ScreenUtil;
import com.wmlive.hhvideo.utils.ToastUtil;
import com.wmlive.hhvideo.widget.dialog.CustomDialog;
import com.wmlive.hhvideo.widget.dialog.PermissionDialog;
import com.wmlive.hhvideo.widget.emojiview.EmojiIndicatorView;
import com.wmlive.networklib.entity.EventEntity;
import com.wmlive.networklib.util.EventHelper;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import cn.wmlive.hhvideo.R;
import io.reactivex.functions.Consumer;

/**
 * Created by hsing on 2018/2/1.
 */
public class IMMessageActivity extends DcBaseActivity implements
        MediaPlayerStatusListener,
        IMMediaPlayOtherSoundListener,
        MP3Recorder.RecordMessageInterface,
        GiftView.GiftViewListener,
        SendGiftPresenter.ISendGiftView, GiftPresenter.IGiftView {

    public static final int HANDLER_WHAT_RESPONSE_HINT_FLAG = -1; //操作正常，无任何操作
    public static final int HANDLER_WHAT_SHOW_TOAST_FLAG = 1001; //toast 显示
    public static final int HANDLER_WHAT_SHOW_TIP_FLAG = 1002; //展示IM提示消息
    public static final int HANDLER_WHAT_GOTO_OTHER_PAGE = 1002; //跳转到其他页面

    public static final int HANDLER_WHAT_PLAY_RECORD_ME_FALG = 1003; //播放自己的录音
    public static final int HANDLER_WHAT_PLAY_RECORD_OTHER_FALG = 1004; //播放对方的录音
    public static final int HANDLER_WHAT_RECORD_UPDATE_TIME_FLAG = 1005; //更新录音时间
    public static final int HANDLER_WHAT_RECORD_UPDATE_DECIBEL_FLAG = 1006; //更新录音分贝

    private static final String EXTRA_USER_ID = "extra_user_id";
    private static final String EXTRA_USER_INFO = "extra_user_info";
    private static final String TAG = IMMessageActivity.class.getSimpleName();

    private static final int[] sMicrophoneValue = new int[]{
            R.drawable.ic_microphone_value,
            R.drawable.ic_microphone_value0,
            R.drawable.ic_microphone_value1,
            R.drawable.ic_microphone_value2,
            R.drawable.ic_microphone_value3,
            R.drawable.ic_microphone_value4,
            R.drawable.ic_microphone_value5,
            R.drawable.ic_microphone_value6,
            R.drawable.ic_microphone_value7,
            R.drawable.ic_microphone_value8,
            R.drawable.ic_microphone_value9,
    };

    @BindView(R.id.btnBack)
    ImageView btnBack;
    @BindView(R.id.tvNickName)
    TextView tvNickName;
    @BindView(R.id.ivUserInfo)
    ImageView ivUserInfo;

    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.rvIMDetailView)
    RecyclerView rvIMDetailView;
    @BindView(R.id.rl_im_text_terrace)
    LinearLayout mRlIMInputTextTerrace;//文字平台
    @BindView(R.id.iv_im_text_record_flag)
    ImageView mIvIMInputTextRecordSwitch;//录音切换
    @BindView(R.id.et_im_text_input)
    EditText mEtIMInputTextContent;//输入框
    @BindView(R.id.iv_im_text_extend_flag)
    ImageView mIvIMInputTextExtend;//扩展
    @BindView(R.id.btn_im_text_send)
    Button mBtnIMTextSend;//发送按钮
    @BindView(R.id.tv_im_text_warn)
    TextView mTvIMInputTextWarn;//文字警告区

    //录音
    @BindView(R.id.rl_im_record_terrace)
    RelativeLayout mRlIMInputRecordTerrace;//语音平台
    @BindView(R.id.iv_im_record_text_flag)
    ImageView mIvIMInputRecordTextSwitch;//文字切换
    @BindView(R.id.tv_im_record_content)
    TextView mTvIMInputRecordContent;//录音按钮
    @BindView(R.id.iv_im_record_extend_flag)
    ImageView mIvIMInputRecordExtend;//扩展
    @BindView(R.id.view_im_details_record)
    View mViewIMDetailsRecordView;
    @BindView(R.id.tv_im_detail_record_time)
    TextView mTvIMDetailsRecordTime;//录音时间
    @BindView(R.id.rl_im_detail_record_icon)
    RelativeLayout mRlIMDetialsRecordIcon;//录音中
    @BindView(R.id.iv_im_detail_record_icon)
    ImageView mIvIMDetialsRecordIcon;//录音中
    @BindView(R.id.iv_im_detail_record_cancle_icon)
    ImageView mIvIMDetailsRecordCancleIcon;//录音取消
    @BindView(R.id.tv_im_detail_record_text)
    TextView mTvIMDetailsRecordText;//录音内容

    //表情
    @BindView(R.id.iv_emoji)
    ImageView iv_emoji;
    @BindView(R.id.ll_emotion_layout)
    LinearLayout ll_emotion_layout;//表情面板
    @BindView(R.id.vp_emotion)
    ViewPager vp_emotion;
    @BindView(R.id.ll_point_group)
    EmojiIndicatorView ll_point_group;

    private EmotionKeyboard mEmotionKeyboard;
    private EmotionPagerAdapter emotionPagerGvAdapter;


    private long downPullCidFlag = -1;
    public static long lastIMMsgCreateTime = 0;//最后一条数据的创建时间
    public static long lastIMMsgId = -1;//最后一条的数据cid dcImId

    private List<MessageDetail> listCurrentData = new ArrayList<MessageDetail>();//当前展示的数据集合
    private List<MessageDetail> listBeforeData = new ArrayList<MessageDetail>();//历史展示的数据集合

    public int pageSize = 50;//每页数量

    private final MyIMResponsHandler mMyIMResponsHandler = new MyIMResponsHandler(this);

    private LinearLayoutManager mLinearLayoutManager;

    //========================录音====================
    private boolean isCancel;//是否取消
    private boolean isTouchDown = false;//是否按下
    float startX = 0.0f;
    float startY = 0.0f;
    private Timer mTimerRecord = null;
    private RecordTimeTask mRecordTimeTask = null;
    private MyRecordHandeler mMyRecordHandler = new MyRecordHandeler(this);
    private static int iMaxRecordTime = 60;//最长录音时间
    private static int iCurrentRecordTime = 0;//当前录音
    private RecorderAndPlayUtil mRecorderAndPlayUtil = null;
    public int iFinishRecordByFlag = -1;// 0：发布停止   1：取消停止  2：超时停止  3：强制停止   4：错误停止
    private boolean isRecording = false;//是否录音中
    // private RelativeLayout mRL_im_chat_loading;//加载框
    //跳转页面handler
    private final MyIMGotoOtherFragmentHandler mMyIMGotoOtherFragmentHandler = new MyIMGotoOtherFragmentHandler(this);

    private Context mContext;
    private long otherUserId;
    private IMMessageAdapter mMessageAdapter;

    private Hashtable<String, RecyclerView.ViewHolder> mIMPlayerItem = new Hashtable<String, RecyclerView.ViewHolder>();
    private UserInfo otherUserInfo;
    private GiftView giftView;
    private CustomDialog rechargeDialog;
    private GiftPresenter giftPresenter;
    private InputMethodManager imm;


    public static void startIMMessageActivity(final BaseCompatActivity context, long userId, UserInfo userInfo) {
        new RxPermissions(context).request(Manifest.permission.RECORD_AUDIO)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean granted) throws Exception {
                        KLog.i("====请求麦克风权限：" + granted);
                        if (!granted) {
                            new PermissionDialog(context, 10).show();
                        } else {
                            Intent intent = new Intent(context, IMMessageActivity.class);
                            intent.putExtra(EXTRA_USER_ID, userId);
                            intent.putExtra(EXTRA_USER_INFO, userInfo);
                            context.startActivity(intent);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.getMessage();
                    }
                });
    }

    @Override
    protected void onSingleClick(View v) {
        switch (v.getId()) {
            case R.id.btnBack:
                onBack();
                break;
            case R.id.ivUserInfo:
                UserHomeActivity.startUserHomeActivity(this, otherUserId, null, true);
                break;
            case R.id.iv_emoji:

                break;
        }
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_immessage;
    }

    @Override
    protected void initData() {
        super.initData();
        EventHelper.register(this);
        mContext = IMMessageActivity.this;
        initView();
        Intent intent = getIntent();
        otherUserId = intent.getLongExtra(EXTRA_USER_ID, 0);
        otherUserInfo = (UserInfo) intent.getSerializableExtra(EXTRA_USER_INFO);
        if (otherUserInfo != null) {
            tvNickName.setText(otherUserInfo.getName());
        }
        mMessageAdapter.setOtherUserId(otherUserId);

        // 媒体播放
        IMMediaPlayUtils.getInstance().setMediaPlayerStatusListener(this);
        IMMediaPlayUtils.getInstance().setImMediaPlayOtherSoundListener(this);

        // 更新消息为已读
        setMessageReadStstus();
        //获取历史消息
        getIMDetailsDataByUId();
        initRecordParams();
        autoLoadViewParams();
        autoLoadViewListener();

        giftPresenter = new GiftPresenter(this);
        addPresenter(giftPresenter);

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);


    }

    /**
     * 初始化view
     */
    private void initView() {
        btnBack.setOnClickListener(this);
        ivUserInfo.setOnClickListener(this);
        iv_emoji.setOnClickListener(this);

        swipeRefreshLayout.setProgressViewEndTarget(false, 0);
        mLinearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        mMessageAdapter = new IMMessageAdapter(mContext);
        mMessageAdapter.setIMMsgHandler(mMyIMResponsHandler);
        mMessageAdapter.setIMGotoHanlder(mMyIMGotoOtherFragmentHandler);
        rvIMDetailView.setLayoutManager(mLinearLayoutManager);
        rvIMDetailView.setAdapter(mMessageAdapter);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                getWeakHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        listBeforeData = MessageManager.get().queryImChatMessageBeforeDcImId(AccountUtil.getUserId(), otherUserId, pageSize, downPullCidFlag);
                        if (null != listBeforeData && listBeforeData.size() > 0) {
                            Collections.reverse(listBeforeData);
                            downPullCidFlag = listBeforeData.get(0).getDcImId();
                            IMUtils.addTimeMessageToList(listBeforeData, -1);
                            if (listBeforeData != null && listBeforeData.size() > 0) {
                                int size = listBeforeData.size();
                                for (int i = 0; i < size; i++) {
                                    MessageDetail itemData = listBeforeData.get(i);
                                    if (MessageDetail.TYPE_AUDIO_CONTENT.equals(itemData.getMsg_type()) && otherUserId == itemData.getFromUserId() && (MessageDetail.IM_STATUS_PLAYED != itemData.getStatus())) {
                                        IMMediaPlayUtils.getInstance().addIMOtherSoundToHeadQueue(itemData.getMsg_id());
                                    }
                                }
                            }
                            mMessageAdapter.addHeaderRefreshDatas(listBeforeData);
                        }
                        //刷新完成
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 2000);
            }
        });

        giftView = new GiftView(this);
        getWindow().addContentView(giftView, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        giftView.initData();
        giftView.setVisibility(View.INVISIBLE);
        giftView.setGiftViewListener(this);
        //初始化表情view
        mEmotionKeyboard = EmotionKeyboard.with(this)
                .setEmotionView(ll_emotion_layout)//绑定表情面板
                .bindToContent(swipeRefreshLayout)//绑定内容view
                .bindToEditText(mEtIMInputTextContent)//判断绑定那种EditView
                .bindToEmotionButton(iv_emoji)//绑定表情按钮
                .build();
        List<String> emojiGroup = InitCatchData.getInitCatchData().getTips().emojiGroup.emojiDefault;
        KLog.d("emojiGroup==" + emojiGroup);
        initEmotion(emojiGroup);
        GlobalOnItemClickManagerUtils globalOnItemClickManager = GlobalOnItemClickManagerUtils.getInstance(this);
        globalOnItemClickManager.attachToEditText(mEtIMInputTextContent);

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

    }

    /**
     * 自动加载view 参数
     */
    private void autoLoadViewParams() {
        mRlIMInputTextTerrace.setVisibility(View.VISIBLE);
        mRlIMInputRecordTerrace.setVisibility(View.GONE);

        //默认初始化
        openIMExtendView(mIvIMInputTextExtend);
    }

    /**
     * 禁固所有扩展事件
     */
    private void hideIMExtendViews() {
        mIvIMInputTextExtend.setVisibility(View.GONE);
        mIvIMInputTextExtend.setEnabled(false);
        mIvIMInputTextExtend.setBackgroundResource(R.drawable.icon_chat_gift);
        mBtnIMTextSend.setVisibility(View.GONE);
        mBtnIMTextSend.setEnabled(false);

        mIvIMInputRecordExtend.setVisibility(View.GONE);
        mIvIMInputRecordExtend.setEnabled(false);
        mIvIMInputRecordExtend.setBackgroundResource(R.drawable.icon_chat_gift);
    }

    /**
     * 开启某个view
     *
     * @param mView
     */
    private void openIMExtendView(View mView) {
        mView.setVisibility(View.VISIBLE);
        mView.setEnabled(true);
    }

    private void hideIMExtendView(View mView) {
        mView.setVisibility(View.GONE);
        mView.setEnabled(false);
    }

    private void setMessageReadStstus() {
        MessageManager.get().setImChatMessageRead(AccountUtil.getUserId(), otherUserId);
    }

    /**
     * 获取最新的im详情
     */
    private void getIMDetailsDataByUId() {
        listCurrentData = MessageManager.get().queryImChatMessage(AccountUtil.getUserId(), otherUserId, pageSize);
        if (listCurrentData != null && listCurrentData.size() > 0) {

            lastIMMsgId = listCurrentData.get(0).getDcImId();
            lastIMMsgCreateTime = listCurrentData.get(0).getCreate_time();
            Collections.reverse(listCurrentData);
            downPullCidFlag = listCurrentData.get(0).getDcImId();
//            KLog.e(TAG, "------------time----------- " + lastIMMsgCreateTime + "<>" + downPullTimeFlag);
            IMUtils.addTimeMessageToList(listCurrentData, -1);
            KLog.e(TAG, "------------time----------- " + lastIMMsgId + "<>" + downPullCidFlag);

            //数据增加到播放队列中
            int size = listCurrentData.size();
            for (int i = 0; i < size; i++) {
                MessageDetail itemData = listCurrentData.get(i);
                KLog.e("im_detail_play", "------- ---------:" + itemData.getMsg_id());
                if (MessageDetail.TYPE_AUDIO_CONTENT.equals(itemData.getMsg_type()) && otherUserId == itemData.getFromUserId() && (MessageDetail.IM_STATUS_PLAYED != itemData.getStatus())) {
                    KLog.e("im_detail_play", "-------add queur to footer ---------:" + itemData.getMsg_id());
                    IMMediaPlayUtils.getInstance().addIMOtherSoundToFooterQueue(itemData.getMsg_id());
                }
            }
            mMessageAdapter.setRefreshDatas(listCurrentData);
            rvIMDetailView.scrollToPosition(listCurrentData.size() - 1);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onImChatMessageEvent(EventEntity eventEntity) {
        if (eventEntity != null && eventEntity.code == GlobalParams.EventType.TYPE_IM_CHAT_NOT_SELF_MSG) {
            KLog.i("====收到新的ImChat消息");
            if (eventEntity.data != null && eventEntity.data instanceof List) {
                if (mMessageAdapter != null) {
                    List<Long> idList = (List<Long>) eventEntity.data;
                    if (!CollectionUtil.isEmpty(idList)) {
                        List<MessageDetail> messageDetailList = new ArrayList<>(2);
                        Set<Long> fromUserIdSet = new HashSet<>(2);
                        MessageDetail messageDetail;
                        for (Long id : idList) {
                            if (id != null) {
                                messageDetail = GreenDaoManager.get().getMessageDetailDao().load(id);
                                if (messageDetail != null) {
                                    fromUserIdSet.add(messageDetail.fromUserId);
                                    messageDetailList.add(messageDetail);
                                    if (MessageDetail.TYPE_AUDIO_CONTENT.equals(messageDetail.getMsg_type())) {
                                        IMMediaPlayUtils.getInstance().addIMOtherSoundToFooterQueue(messageDetail.getMsg_id());
                                    }
                                    //播放接收消息音效
//                                    if (AccountUtil.getUserId() != messageDetail.fromUserId) {
//                                        //对方的消息
//                                        RingtoneController.playIMAcceptMusic();
//                                    }
                                }
                            }
                        }
                        //获取去数据库中最新的数据
                        addIMMsgLastDataByCid();
                    }
                }
            }
        }
    }


    /**
     * view 的事件加载
     */
    private void autoLoadViewListener() {

        //切换录音页面
        mIvIMInputTextRecordSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeviceUtils.hiddenKeyBoard(v);
                ll_emotion_layout.setVisibility(View.GONE);
                iv_emoji.setSelected(false);
                hideIMExtendViews();
                openIMExtendView(mIvIMInputRecordExtend);
                mRlIMInputTextTerrace.setVisibility(View.GONE);
                mRlIMInputRecordTerrace.setVisibility(View.VISIBLE);
            }
        });

        //输入的字数限制
        mEtIMInputTextContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (s != null) {
                    String strContent = s.toString();
                    hideIMExtendViews();
                    if (TextUtils.isEmpty(strContent)) {
                        openIMExtendView(mIvIMInputTextExtend);
                    } else {
                        openIMExtendView(mBtnIMTextSend);
                    }
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String strContent = s.toString();
                if (TextUtils.isEmpty(strContent) && mIvIMInputTextExtend.getVisibility() != View.VISIBLE) {
                    hideIMExtendViews();
                    openIMExtendView(mIvIMInputTextExtend);
                } else if (!TextUtils.isEmpty(strContent) && mBtnIMTextSend.getVisibility() != View.VISIBLE) {
                    hideIMExtendViews();
                    openIMExtendView(mBtnIMTextSend);
                }
                if (strContent.length() > 500) {
                    mTvIMInputTextWarn.setVisibility(View.VISIBLE);
                } else {
                    mTvIMInputTextWarn.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //文字平台的扩展
        mIvIMInputTextExtend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showGiftView();
            }
        });
        //文字平台的发送功能
        mBtnIMTextSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strContent = mEtIMInputTextContent.getText().toString().trim();
                if (TextUtils.isEmpty(strContent)) {
                    ToastUtil.showToast("不能发送空内容!");
                } else if (strContent.length() > 500) {
                    ToastUtil.showToast("内容过多");
                } else {
                    //执行发送
                    mEtIMInputTextContent.setText("");
                    onIMTextSend(strContent);
                    hideIMExtendViews();
                    openIMExtendView(mIvIMInputTextExtend);
                    RingtoneController.playMessageRingtone(DCApplication.getDCApp());
                }
            }
        });
        //==============================语音平台==========================
        //切换文字平台
        mIvIMInputRecordTextSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideIMExtendViews();
                openIMExtendView(mIvIMInputTextExtend);
                mRlIMInputRecordTerrace.setVisibility(View.GONE);
                mRlIMInputTextTerrace.setVisibility(View.VISIBLE);
                //自动填出键盘
                mEtIMInputTextContent.setFocusable(true);
                mEtIMInputTextContent.setFocusableInTouchMode(true);
                mEtIMInputTextContent.requestFocus();
                DeviceUtils.showKeyBoard(mEtIMInputTextContent);

            }
        });

        //按住录音
        mTvIMInputRecordContent.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                //停止播放录音
                IMMediaPlayUtils.getInstance().stopAllPlayingMsg();
                KLog.e("im_detail_record", "----" + event.getAction());
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (!checkClickValid()) {
                            return true;
                        }
                        mTvIMInputRecordContent.setBackgroundResource(R.drawable.bg_im_record_btn_bg_pressed);
                        startIMRecord();
                        KLog.e("im_detail_record", "--down -- : ");
                        startX = event.getX();
                        startY = event.getY();
                        //按下
                        isCancel = false;
                        isTouchDown = true;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        //移动
                        if (!isTouchDown) {
                            return true;
                        }
                        if (event.getX() - startX >= 256 || event.getY() - startY >= 256) {
                            //提示取消信息
                            isCancel = true;
                            scrollRecordCancleUI();
                        } else if (event.getX() - startX < -256 || event.getY() - startY < -256) {
                            //提示取消信息
                            isCancel = true;
                            scrollRecordCancleUI();
                        } else {
                            //可发布状态
                            isCancel = false;
                            initRecordSubmitUI();
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        //抬起
                        mTvIMInputRecordContent.setBackgroundResource(R.drawable.bg_im_text_terrace);
                        initIMRecordNormalUI();
                        if (!isTouchDown) {
                            return true;
                        }
                        KLog.e("im_detail_record", "--action_up -- : " + iFinishRecordByFlag);
                        if (iFinishRecordByFlag != 2) {
                            //非超时提交
                            if (isCancel) {
                                //取消发送
                                iFinishRecordByFlag = 1;
                            } else {
                                //正常发送
                                iFinishRecordByFlag = 0;
                            }
                            stopIMRecord();
                        }
                        isTouchDown = false;
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        //取消
                        mTvIMInputRecordContent.setBackgroundResource(R.drawable.bg_im_text_terrace);
                        KLog.e("im_detail_record", "--action_cancle -- : ");
                        ToastUtil.showToast(R.string.im_details_record_cancle_msg);
                        initIMRecordNormalUI();
                        isCancel = false;
                        isTouchDown = false;
                        iFinishRecordByFlag = -1;
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

        //语音平台的扩展
        mIvIMInputRecordExtend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showGiftView();
            }
        });
    }

    private void showGiftView() {
        if (AccountUtil.isLogin()) {
            DeviceUtils.hiddenKeyBoard(mIvIMInputTextExtend);
            giftPresenter.getImGiftList(otherUserId);
//            giftView.setVisibility(View.VISIBLE);
//            giftView.showGiftPanel(otherUserInfo);
        } else {
            showReLogin();
        }
//        DcSqlHelper.get().queryMessageAll();
    }

    long lastClickTime = 0l;

    private boolean checkClickValid() {
        long currentTime = System.currentTimeMillis();
        if (!(currentTime - lastClickTime > GlobalParams.Config.MINIMUM_CLICK_DELAY)) {
            return false;
        }
        lastClickTime = System.currentTimeMillis();
        return true;
    }


    /**
     * 初始化录音参数
     */
    private void initRecordParams() {
        mRecorderAndPlayUtil = new RecorderAndPlayUtil();
        mRecorderAndPlayUtil.setMP3RecorderDirPath(AppCacheFileUtils.getAppIMCachePath() + otherUserId + "/");
        mRecorderAndPlayUtil.getRecorder().setRecordMessageInterface(this);
        mRecorderAndPlayUtil.getRecorder().setHandle(new Handler() {
            @Override
            public void handleMessage(Message msg) {
                KLog.e("im_detail_record", "-----------what---------:" + msg.what);
                switch (msg.what) {
                    case MP3Recorder.MSG_REC_STARTED:
                        // 开始录音
                        if (!isTouchDown) {
                            return;
                        }
                        isRecording = true;
                        initRecordSubmitUI();
                        break;
                    case MP3Recorder.MSG_REC_STOPPED:
                        // 录音结束
                        isRecording = false;
                        initIMRecordNormalUI();
                        if (iFinishRecordByFlag == 0) {
                            //发布停止录音
                            submintIMRecordPath();
                        } else if (iFinishRecordByFlag == 1) {
                            //取消录音停止，删除录音文件
                            delIMRecordPath();
                        } else if (iFinishRecordByFlag == 2) {
                            //超时停止,
                            submintIMRecordPath();
                        } else if (iFinishRecordByFlag == 3) {
                            //强制停止
                            delIMRecordPath();
                        } else if (iFinishRecordByFlag == 4) {
                            //错误停止
                            delIMRecordPath();
                        }
                        break;
                    case MP3Recorder.MSG_REC_RESTORE:
                        //继续录音
                        break;
                    case MP3Recorder.MSG_REC_PAUSE:
                        //暂停录音
                        break;
                    case MP3Recorder.MSG_ERROR_GET_MIN_BUFFERSIZE:
                        //缓冲区错误，录音失败
                        iFinishRecordByFlag = 4;
                        stopIMRecord();
                        ToastUtil.showToast("采样率手机不支持");
                        break;
                    case MP3Recorder.MSG_ERROR_CREATE_FILE:
                        //创建文件夹错误
                        iFinishRecordByFlag = 4;
                        stopIMRecord();
                        ToastUtil.showToast("创建音频文件出错");
                        break;
                    case MP3Recorder.MSG_ERROR_REC_START:
                        iFinishRecordByFlag = 4;
                        stopIMRecord();
                        ToastUtil.showToast("初始化录音器出错");
                        break;
                    case MP3Recorder.MSG_ERROR_AUDIO_RECORD:
                        iFinishRecordByFlag = 4;
                        stopIMRecord();
                        ToastUtil.showToast("录音的时候出错");
                        break;
                    case MP3Recorder.MSG_ERROR_AUDIO_ENCODE:
                        iFinishRecordByFlag = 4;
                        stopIMRecord();
                        ToastUtil.showToast("编码出错");
                        break;
                    case MP3Recorder.MSG_ERROR_WRITE_FILE:
                        iFinishRecordByFlag = 4;
                        stopIMRecord();
                        ToastUtil.showToast("文件写入出错");
                        break;
                    case MP3Recorder.MSG_ERROR_CLOSE_FILE:
                        iFinishRecordByFlag = 4;
                        stopIMRecord();
                        ToastUtil.showToast("文件流关闭出错");
                        break;
                }
            }
        });
    }

    //开始录音
    private void startIMRecord() {
        if (RecorderAndPlayUtil.isRecordeHasPermission()) {
            startIMRecording();
            iFinishRecordByFlag = -1;
        } else {
            ToastUtil.showToast(R.string.record_permission_notices_message_off);
        }
    }

    /**
     * 录音中
     */
    private void startIMRecording() {
        iCurrentRecordTime = 0;
        //开始录音
        mRecorderAndPlayUtil.startRecording();
        isRecording = true;
        try {
            if (null != mRecordTimeTask) {
                mRecordTimeTask.cancel();
            }
        } catch (Exception e) {

        } finally {
            mRecordTimeTask = null;
        }
        mRecordTimeTask = new RecordTimeTask(this);
        try {
            if (mTimerRecord != null) {
                mTimerRecord.cancel();
            }
        } catch (Exception e) {

        } finally {
            mTimerRecord = null;
        }
        mTimerRecord = new Timer(true);
        mTimerRecord.schedule(mRecordTimeTask, 1000, 1000);
        showRecordTimeUI(iCurrentRecordTime);
    }

    //结束录音
    private void stopIMRecord() {
        isRecording = false;
        isTouchDown = false;
        mRecorderAndPlayUtil.stopRecording();
        try {
            if (null != mRecordTimeTask) {
                mRecordTimeTask.cancel();
            }
        } catch (Exception e) {

        } finally {
            mRecordTimeTask = null;
        }
        try {
            if (mTimerRecord != null) {
                mTimerRecord.cancel();
            }
        } catch (Exception e) {

        } finally {
            mTimerRecord = null;
        }
    }

    /**
     * 初始化到录音的初始化状态
     */
    private void initIMRecordNormalUI() {
        mViewIMDetailsRecordView.setVisibility(View.GONE);
        mTvIMInputRecordContent.setText(R.string.im_details_record_down_speek);
        mRlIMDetialsRecordIcon.setVisibility(View.VISIBLE);
        //mIvIMDetialsRecordIcon.setVisibility(View.VISIBLE);
        mIvIMDetailsRecordCancleIcon.setVisibility(View.GONE);
        mIvIMDetialsRecordIcon.setImageResource(R.drawable.ic_microphone_value);
        mTvIMDetailsRecordText.setTextColor(getResources().getColor(R.color.tv_im_recod_notice));
        mTvIMDetailsRecordText.setText(R.string.im_details_record_scroll_up_cancle);
//        stopRecordIcon();
    }

    /**
     * 初始化录音可以提交状态
     */
    private void initRecordSubmitUI() {
        if (mViewIMDetailsRecordView.getVisibility() != View.VISIBLE) {
            mViewIMDetailsRecordView.setVisibility(View.VISIBLE);
        }
        mTvIMInputRecordContent.setText(R.string.im_details_record_up_finish);
        mRlIMDetialsRecordIcon.setVisibility(View.VISIBLE);
        mIvIMDetialsRecordIcon.setImageResource(R.drawable.ic_microphone_value);
        //mIvIMDetialsRecordIcon.setVisibility(View.VISIBLE);
        mIvIMDetailsRecordCancleIcon.setVisibility(View.GONE);
        mTvIMDetailsRecordText.setTextColor(getResources().getColor(R.color.tv_im_recod_notice));
        mTvIMDetailsRecordText.setText(R.string.im_details_record_scroll_up_cancle);
//        startRecordIcon();
    }

    /**
     * 滑动到指定距离可以取消
     */
    private void scrollRecordCancleUI() {
        mTvIMInputRecordContent.setText(R.string.im_details_record_up_finish);
        mRlIMDetialsRecordIcon.setVisibility(View.GONE);
        //  mIvIMDetialsRecordIcon.setVisibility(View.GONE);
        mIvIMDetailsRecordCancleIcon.setVisibility(View.VISIBLE);
        mTvIMDetailsRecordText.setTextColor(getResources().getColor(R.color.tv_im_recod_notice_cancel));
        mTvIMDetailsRecordText.setText(R.string.im_details_record_up_cancle);
    }

    /**
     * 上传IM 录音信息
     */
    private void submintIMRecordPath() {
        if (iCurrentRecordTime < 1) {
            ToastUtil.showToast("录音时间太短");
        } else {
            KLog.e("im_detail_record", "---------submintIMRecordPath-------");
            onIMRecordSend(mRecorderAndPlayUtil.getRecorderPath(), iCurrentRecordTime);
        }
    }

    /**
     * 上传图片信息
     */
    private void submitIMPicturePath() {

    }

    /**
     * 删除IM 录音信息
     */
    private void delIMRecordPath() {
        String mp3Path = mRecorderAndPlayUtil.getRecorderPath();
        if (!TextUtils.isEmpty(mp3Path)) {
            try {
                File mFile = new File(mp3Path);
                if (mFile != null && mFile.exists()) {
                    mFile.delete();
                }
            } catch (Exception e) {

            }
        }
    }

    /**
     * 显示当前录音时间
     *
     * @param time
     */
    private void showRecordTimeUI(int time) {
        StringBuffer sb = new StringBuffer();
        sb.append("00:");
        if (time <= 9) {
            sb.append("0");
        }
        sb.append(time);
        mTvIMDetailsRecordTime.setText(sb.toString());
    }

    /**
     * 更新当期录音的分贝动画
     *
     * @param volumnValue
     */
    private void showRecordDecibleUI(int volumnValue) {
        int value = (int) Math.floor(volumnValue * 1f / 10);
        KLog.e("=====计算音量值：" + value);
        value = value < 0 ? 0 : (value > (sMicrophoneValue.length - 1) ? (sMicrophoneValue.length - 1) : value);
        KLog.e("=====调整音量值：" + value);
        mIvIMDetialsRecordIcon.setImageResource(sMicrophoneValue[value]);
    }

    @Override
    public void onGiftListOk(List<GiftEntity> giftEntities, boolean isInit, long giftId) {
        if (!CollectionUtil.isEmpty(giftEntities)) {
            giftView.setVisibility(View.VISIBLE);
            giftView.showGiftPanel(otherUserInfo, giftEntities);
        } else {
            showToast(getString(R.string.stringGiftDataError));
        }
    }

    @Override
    public void onGiftListFail(String message) {
        showToast(message);
    }

    /**
     * 定时任务
     */
    public static class RecordTimeTask extends TimerTask {
        private final WeakReference<IMMessageActivity> activity;

        public RecordTimeTask(IMMessageActivity act) {
            this.activity = new WeakReference<IMMessageActivity>(act);
        }

        @Override
        public void run() {
            if (activity != null && activity.get() != null) {
                ++iCurrentRecordTime;
                activity.get().mMyRecordHandler.sendEmptyMessage(HANDLER_WHAT_RECORD_UPDATE_TIME_FLAG);
            }
        }
    }

    @Override
    public void onRecordVolumeValue(int volumeValue) {
        //录音时分贝值回调
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showRecordDecibleUI(volumeValue);
            }
        });
    }

    //本地发送文字信息
    public void onIMTextSend(String content) {
        //获取本地新Id
        String strLocalMsgId = IMUtils.getLocalMsgId();
        long localTime = System.currentTimeMillis() / 1000;
        // 文本消息
        MessageDetail messageDetail = new MessageDetail();
        messageDetail.setLocal_msg_id(strLocalMsgId);
        messageDetail.setStatus(MessageDetail.IM_STATUS_SENDING);
        messageDetail.setCreate_time(localTime);
        messageDetail.jump = "";

        MessageContent contenBean = new MessageContent();
        contenBean.text = content;
        contenBean.desc = content;
        messageDetail.setMessageContent(contenBean);
        messageDetail.fromUserId = AccountUtil.getUserId();
        messageDetail.toUserId = otherUserId;
        messageDetail.from_user = AccountUtil.getUserInfo();
        messageDetail.to_user = otherUserInfo;
        messageDetail.setMsg_type(MessageDetail.TYPE_TEXT_CONTENT);
        messageDetail.setTips("");
        KLog.e("im_request", "<> " + messageDetail.toString());

        //添加一条时间记录
        MessageDetail imMsgSystemTime = null;
        //获取最后一条数据的时间
        long lastCreateTime = MessageManager.get().getLastCreateTimeByUserId(AccountUtil.getUserId(), otherUserId);
        if (localTime - lastCreateTime >= IMUtils.TIME_DELAY_BETWEEN_MESSAGE) {
            imMsgSystemTime = new MessageDetail();
            imMsgSystemTime.setLocal_msg_id(strLocalMsgId + "t");
            imMsgSystemTime.setStatus(MessageDetail.IM_STATUS_SENDING);
            imMsgSystemTime.setCreate_time(localTime - 1);

            MessageContent contentTimeBean = new MessageContent();
            contentTimeBean.text = String.valueOf(imMsgSystemTime.create_time);
            imMsgSystemTime.setMessageContent(contentTimeBean);
            imMsgSystemTime.fromUserId = AccountUtil.getUserId();
            imMsgSystemTime.toUserId = otherUserId;
            imMsgSystemTime.from_user = AccountUtil.getUserInfo();
            imMsgSystemTime.to_user = otherUserInfo;
            imMsgSystemTime.belongUserId = AccountUtil.getUserId();
            imMsgSystemTime.setImType(DcMessage.TYPE_IM_CHAT);
            imMsgSystemTime.setMsg_type(MessageDetail.TYPE_SYSTIME_CONTENT);
        }

        //数据加入数据库
//        lastIMMsgId = MessageManager.get().insertOrReplaceMessageInfo(messageDetail);
        lastIMMsgId = MessageManager.get().parseChatMessageList(messageDetail);
        lastIMMsgCreateTime = messageDetail.getCreate_time();
        //数据上屏幕
        if (imMsgSystemTime != null) {
            mMessageAdapter.addFooterItemRefreshDatas(imMsgSystemTime, messageDetail);
        } else {
            mMessageAdapter.addFooterItemRefreshData(messageDetail);
        }
        rvIMDetailView.scrollToPosition(mMessageAdapter.getItemCount() - 1);
    }

    //本地发送语音嘻信息
    public void onIMRecordSend(String path, int time) {
        String strLocalMsgId = IMUtils.getLocalMsgId();
        long localTime = System.currentTimeMillis() / 1000;
        // 语音消息
        MessageDetail messageDetail = new MessageDetail();
        messageDetail.setLocal_msg_id(strLocalMsgId);
        messageDetail.setStatus(MessageDetail.IM_STATUS_SENDING);
        messageDetail.setCreate_time(localTime);
        messageDetail.jump = "";

        MessageContent contenBean = new MessageContent();
        //contenBean.setAudio(path);
        contenBean.local_path = path;
        contenBean.length = time;
        contenBean.desc = "[语音]";
        messageDetail.setMessageContent(contenBean);
        messageDetail.fromUserId = AccountUtil.getUserId();
        messageDetail.toUserId = otherUserId;
        messageDetail.from_user = AccountUtil.getUserInfo();
        messageDetail.to_user = otherUserInfo;
        messageDetail.setMsg_type(MessageDetail.TYPE_AUDIO_CONTENT);
        messageDetail.setTips("");
        KLog.e("im_request", "<audio> " + messageDetail.toString());

        //添加一条时间记录
        MessageDetail imMsgSystemTime = null;
        //获取最后一条数据的时间
        long lastCreateTime = MessageManager.get().getLastCreateTimeByUserId(AccountUtil.getUserId(), otherUserId);
        if (localTime - lastCreateTime >= IMUtils.TIME_DELAY_BETWEEN_MESSAGE) {
            imMsgSystemTime = new MessageDetail();
            imMsgSystemTime.setLocal_msg_id(strLocalMsgId + "t");
            imMsgSystemTime.setStatus(MessageDetail.IM_STATUS_SENDING);
            imMsgSystemTime.setCreate_time(localTime - 1);

            MessageContent contentTimeBean = new MessageContent();
            contentTimeBean.text = String.valueOf(imMsgSystemTime.create_time);
            imMsgSystemTime.setMessageContent(contentTimeBean);
            imMsgSystemTime.fromUserId = AccountUtil.getUserId();
            imMsgSystemTime.toUserId = otherUserId;
            imMsgSystemTime.from_user = AccountUtil.getUserInfo();
            imMsgSystemTime.to_user = otherUserInfo;
            imMsgSystemTime.belongUserId = AccountUtil.getUserId();
            imMsgSystemTime.setImType(DcMessage.TYPE_IM_CHAT);
            imMsgSystemTime.setMsg_type(MessageDetail.TYPE_SYSTIME_CONTENT);
        }

        //数据加入数据库
//        lastIMMsgId = MessageManager.get().insertOrReplaceMessageInfo(messageDetail);
        lastIMMsgId = MessageManager.get().parseChatMessageList(messageDetail);
        lastIMMsgCreateTime = messageDetail.getCreate_time();

        //数据上屏幕
        if (imMsgSystemTime != null) {
            mMessageAdapter.addFooterItemRefreshDatas(imMsgSystemTime, messageDetail);
        } else {
            mMessageAdapter.addFooterItemRefreshData(messageDetail);
        }
        rvIMDetailView.scrollToPosition(mMessageAdapter.getItemCount() - 1);

    }

    /**
     * 加载指定Id之后的最新收的最新消息
     */
    public void addIMMsgLastDataByCid() {
        // 拉黑消息体的fromUser为对方，toUser为己方用户
        List<MessageDetail> listLastData = MessageManager.get().queryImChatMessageByImId(AccountUtil.getUserId(), otherUserId, lastIMMsgId);
        KLog.e(TAG, "---------lastIMMsgId:" + String.valueOf(lastIMMsgId));
        if (listLastData != null && listLastData.size() > 0) {
            IMUtils.addTimeMessageToList(listLastData, lastIMMsgCreateTime);
            int lastSize = listLastData.size();
            lastIMMsgId = listLastData.get(lastSize - 1).getDcImId();
            lastIMMsgCreateTime = listLastData.get(lastSize - 1).getCreate_time();
            KLog.e(TAG, "---------listLastData is size :" + String.valueOf(lastSize));
            mMessageAdapter.addFooterRefreshDatas(listLastData);
            if (!hasMoveHalfScreen()) {
                rvIMDetailView.scrollToPosition(mMessageAdapter.getItemCount() - 1);
            }
        } else {
            KLog.e(TAG, "---------listLastData is size : 0 ");
        }
    }

    public boolean hasMoveHalfScreen() {
        RecyclerView.LayoutManager layoutManager = rvIMDetailView.getLayoutManager();
        if (layoutManager instanceof LinearLayoutManager) {
            LinearLayoutManager linearlayoutManager = (LinearLayoutManager) layoutManager;
            int position = linearlayoutManager.findLastVisibleItemPosition();
            return mMessageAdapter.getItemCount() - position > 6;
        }
        return false;
    }


    //=================================播放语音======================================
    @Override
    public void playOnStartListener(String msgId) {
        KLog.e("im_detail_play", "---------playOnStartListener-------");
        if (TextUtils.isEmpty(msgId)) {
            return;
        }
        try {
            startIMItemPlayInfo(mIMPlayerItem.get(msgId));
        } catch (Exception e) {
            KLog.e("im_detail_play", "---------playOnStartListener------Exception-");
        }
    }

    @Override
    public void playOnCompleteListener(String msgId) {
        KLog.e("im_detail_play", "---------playOnCompleteListener-------");
        if (TextUtils.isEmpty(msgId)) {
            return;
        }
        try {
            stopIMItemPlayInfo(mIMPlayerItem.get(msgId));
            mIMPlayerItem.remove(msgId);
        } catch (Exception e) {
            KLog.e("im_detail_play", "---------playOnCompleteListener------Exception-");
        }
    }

    @Override
    public void playOnErrorListener(String msgId) {
        KLog.e("im_detail_play", "---------playOnErrorListener-------");
        if (TextUtils.isEmpty(msgId)) {
            return;
        }
        try {
            stopIMItemPlayInfo(mIMPlayerItem.get(msgId));
            mIMPlayerItem.remove(msgId);
        } catch (Exception e) {
            KLog.e("im_detail_play", "---------playOnErrorListener------Exception-");
        }
    }

    @Override
    public void autoStartIMMediaPlayOtherSoundItem(String msgId) {
        //自动顺序播放回调函数
        KLog.e("im_detail_play", "---------autoStartIMMediaPlayOtherSoundItem------msgid---:" + msgId);
        int position = mMessageAdapter.getItemPositionByMsgId(msgId);
        KLog.e("im_detail_play", "---------autoStartIMMediaPlayOtherSoundItem------position---:" + position);
        if (position > -1) {
            playOtherRecordMsgMsgByPosition(position);
        } else {
            ToastUtil.showToast("文件不存在");
        }
    }

    /**
     * 开始指定的IM 播放信息
     *
     * @param mViewHolde
     */
    private void startIMItemPlayInfo(RecyclerView.ViewHolder mViewHolde) {
        if (mViewHolde == null) {
            return;
        }
        if (mViewHolde instanceof MeSoundViewHolder) {
            //自己的录音
            MeSoundViewHolder meSoundViewHolder = (MeSoundViewHolder) mViewHolde;
            meSoundViewHolder.startRecordAndPlayerAnimation();
        } else if (mViewHolde instanceof OtherSoundViewHolder) {
            //对方的录音
            OtherSoundViewHolder otherSoundViewHolder = (OtherSoundViewHolder) mViewHolde;
            otherSoundViewHolder.startRecordAndPlayerAnimation();
            otherSoundViewHolder.updateOtherSoundViewStatu();
        }
    }

    /**
     * 停止指定的IM 播放信息
     *
     * @param mViewHolde
     */
    private void stopIMItemPlayInfo(RecyclerView.ViewHolder mViewHolde) {
        if (mViewHolde == null) {
            return;
        }
        if (mViewHolde instanceof MeSoundViewHolder) {
            //自己的录音
            MeSoundViewHolder meSoundViewHolder = (MeSoundViewHolder) mViewHolde;
            meSoundViewHolder.stopRecordAndPlayerAnimation();
        } else if (mViewHolde instanceof OtherSoundViewHolder) {
            //对方的录音
            OtherSoundViewHolder otherSoundViewHolder = (OtherSoundViewHolder) mViewHolde;
            otherSoundViewHolder.stopRecordAndPlayerAnimation();
        }
    }

    /**
     * 播放自己录音
     *
     * @param position
     */
    public void playMeRecordMsgMsgByPosition(int position) {
        KLog.e("im_detail_play", "----me-----position-------:" + position);
        if (IMMediaPlayUtils.getInstance().isPlayingPlayer()) {
            IMMediaPlayUtils.getInstance().stopMediaPlayByPathToMe();
            if (IMMediaPlayUtils.getInstance().getiPlayingPosition() == position) {
                KLog.e("im_detail_play", "--me-------stop-------:" + position);
                return;
            }
        }
        KLog.e("im_detail_play", "----me-----start-------");
        RecyclerView.ViewHolder mViewHolder = getIMItemViewHolderByPosition(position);
        MessageDetail mItemData = null;
        if (mViewHolder != null && mViewHolder instanceof MeSoundViewHolder) {
            mItemData = getIMItemDataByPosition(position);
        }
        if (mItemData != null) {
            String msgId = mItemData.getMsg_id();
            String localPath = "";
            String serverPath = "";
            try {
                MessageContent contentBean = JsonUtils.parseObject(mItemData.getMessageContent(), MessageContent.class);
                if (contentBean != null) {
                    localPath = contentBean.local_path;
                    serverPath = contentBean.audio;
                }
            } catch (Exception e) {

            }
            KLog.e("im_detail_play", "---------------me------------localPath:" + localPath + "<>serverPath:" + serverPath);
            if (TextUtils.isEmpty(localPath) && TextUtils.isEmpty(serverPath)) {
                ToastUtil.showToast("文件不存在");
            } else {
                String path = checkLocalSoundFile(localPath) ? localPath : serverPath;
                KLog.e("im_detail_play", "---------------me------------playpath:" + path);
                mIMPlayerItem.put(msgId, mViewHolder);
                KLog.e("im_detail_play", "---------------me------------path:" + path);
                IMMediaPlayUtils.getInstance().startMediaPlayByPathToMe(position, msgId, path);
            }
        } else {
            ToastUtil.showToast("文件不存在");
        }
    }


    /**
     * 播放对方录音
     *
     * @param position
     */
    public void playOtherRecordMsgMsgByPosition(int position) {
        KLog.e("im_detail_play", "-----other----position-------:" + position);
        if (IMMediaPlayUtils.getInstance().isPlayingPlayer()) {
            IMMediaPlayUtils.getInstance().stopMediaPlayByPathToMe();
            if (IMMediaPlayUtils.getInstance().getiPlayingPosition() == position) {
                KLog.e("im_detail_play", "---other------stop-------:" + position);
                return;
            }
        }
        KLog.e("im_detail_play", "------other---start-------");
        RecyclerView.ViewHolder mViewHolder = getIMItemViewHolderByPosition(position);
        MessageDetail mItemData = null;
        if (mViewHolder != null && mViewHolder instanceof OtherSoundViewHolder) {
            mItemData = getIMItemDataByPosition(position);
        }
        if (mItemData != null) {
            String msgId = mItemData.getMsg_id();
            String serverPath = "";
            try {
                MessageContent contentBean = JsonUtils.parseObject(mItemData.getMessageContent(), MessageContent.class);
                if (contentBean != null) {
                    serverPath = contentBean.audio;
                }
            } catch (Exception e) {

            }
            mIMPlayerItem.put(msgId, mViewHolder);
            IMMediaPlayUtils.getInstance().startMediaPlayByPathToOther(position, msgId, serverPath);
            if (mMessageAdapter.getItemCount() > position + 1) {
                rvIMDetailView.scrollToPosition(position + 1);
            } else {
                rvIMDetailView.scrollToPosition(mMessageAdapter.getItemCount() - 1);
            }

        } else {
            ToastUtil.showToast("文件不存在");
        }

    }

    /**
     * 设置自动顺序播放参数
     *
     * @param msgId
     */
    public void setAutoOrderPlayOtherRecordMsg(String msgId) {
        KLog.e("im_detail_play", "-----setAutoOrderPlayOtherRecordMsg-----msgId--:" + msgId);
        IMMediaPlayUtils.getInstance().setIMOtherSoundPlayingQueue(msgId);
    }

    /**
     * 检测本地音频文件是否存在
     *
     * @return
     */
    private boolean checkLocalSoundFile(String localPath) {
        if (TextUtils.isEmpty(localPath)) {
            ToastUtil.showToast("本地文件丢失");
            return false;
        } else {
            try {
                File mFile = new File(localPath);
                if (mFile != null && !mFile.exists()) {
                    ToastUtil.showToast("本地文件丢失");
                    return false;
                }
            } catch (Exception e) {
                ToastUtil.showToast("本地文件丢失");
                return false;
            }
        }
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        IMMediaPlayUtils.getInstance().stopAllPlayingMsg();
    }

    /**
     * 礼物
     **/

    @Override
    protected SendGiftPresenter getPresenter() {
        return new SendGiftPresenter(this);
    }

    @Override
    public void onGiftDismiss(int position) {

    }

    @Override
    public void doPayGift(int position, long userId, String giftIds, String giftsAmount, int all, int decibelCount) {
        KLog.i("=====doPayGift videoId:" + userId + " ids:" + giftIds + " ,giftsAmount：" + giftsAmount);
        if (userId > 0 && !TextUtils.isEmpty(giftIds) && !TextUtils.isEmpty(giftsAmount)) {
            getPresenter().sendGift(userId, giftIds, giftsAmount);
        }
    }

    @Override
    public void onRechargeClick(int position) {
        if (AccountUtil.isLogin()) {
            UserAccountEarningsActivity.startUserAccountActivity(this, AccountUtil.getUserId());
        } else {
            showReLogin();
        }
    }

    @Override
    public void onGoldNotEnough() {
        if (rechargeDialog == null) {
            rechargeDialog = new CustomDialog(this, R.style.BaseDialogTheme);
            rechargeDialog.setContent(getString(R.string.stringGoldNotEnough));
            rechargeDialog.setCanceledOnTouchOutside(false);
            rechargeDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    rechargeDialog.dismiss();
                    onRechargeClick(1);
                }
            });
            rechargeDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    rechargeDialog.dismiss();
                }
            });
        }
        if (!rechargeDialog.isShowing()) {
            rechargeDialog.show();
        }
    }

    @Override
    public void onSendGiftOk(int position, long userId, String giftId, SendGiftResultResponse sendGiftResultResponse) {
        KLog.d("赠送礼物成功sendGiftResultResponse:" + sendGiftResultResponse);
        if (sendGiftResultResponse != null && sendGiftResultResponse.message != null) {
            onIMGiftSend(sendGiftResultResponse.message);
        }
        getWeakHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (sendGiftResultResponse != null
                        && sendGiftResultResponse.settle_msg != null
                        && !CollectionUtil.isEmpty(sendGiftResultResponse.settle_msg.prize_message)) {
                    giftView.showFreeResultPanel(sendGiftResultResponse.settle_msg);
                    giftView.setVisibility(View.VISIBLE);
                    giftView.removeSendGift(giftId);
                } else {
                    giftView.removeSendGift(giftId);
                    if (giftView.getVisibility() == View.VISIBLE) {
                        giftView.dismiss();
                    }
                }
            }
        }, 400);
    }

    @Override
    public void onSendGiftFail(int position, long userId, String giftId, String message) {
        showToast(message);
        if (giftView != null) {
            giftView.removeSendGift(giftId);
            giftView.dismiss();
        }
    }

    // IM礼物信息上屏
    public void onIMGiftSend(DcMessage dcMessage) {
        if (dcMessage != null && dcMessage.message != null) {
            //获取本地新Id
            String strLocalMsgId = IMUtils.getLocalMsgId();
            long localTime = System.currentTimeMillis() / 1000;
            MessageDetail giftMessage = dcMessage.message;
            giftMessage.belongUserId = AccountUtil.getUserId();
            giftMessage.setImType(DcMessage.TYPE_IM_CHAT);
            giftMessage.setLocal_msg_id(strLocalMsgId);
            giftMessage.setStatus(MessageDetail.IM_STATUS_SENT);
            giftMessage.setCreate_time(localTime);
            KLog.e("im_request", "<gift> " + giftMessage.toString());

            //添加一条时间记录
            MessageDetail imMsgSystemTime = null;
            //获取最后一条数据的时间
            long lastCreateTime = MessageManager.get().getLastCreateTimeByUserId(AccountUtil.getUserId(), otherUserId);
            if (localTime - lastCreateTime >= IMUtils.TIME_DELAY_BETWEEN_MESSAGE) {
                imMsgSystemTime = new MessageDetail();
                imMsgSystemTime.setLocal_msg_id(strLocalMsgId + "t");
                imMsgSystemTime.setStatus(MessageDetail.IM_STATUS_SENDING);
                imMsgSystemTime.setCreate_time(localTime - 1);

                MessageContent contentTimeBean = new MessageContent();
                contentTimeBean.text = String.valueOf(imMsgSystemTime.create_time);
                imMsgSystemTime.setMessageContent(contentTimeBean);
                imMsgSystemTime.fromUserId = AccountUtil.getUserId();
                imMsgSystemTime.toUserId = otherUserId;
                imMsgSystemTime.from_user = AccountUtil.getUserInfo();
                imMsgSystemTime.to_user = otherUserInfo;
                imMsgSystemTime.belongUserId = AccountUtil.getUserId();
                imMsgSystemTime.setImType(DcMessage.TYPE_IM_CHAT);
                imMsgSystemTime.setMsg_type(MessageDetail.TYPE_SYSTIME_CONTENT);
            }

            //数据加入数据库
//            lastIMMsgId = MessageManager.get().insertOrReplaceMessageInfo(giftMessage);
            lastIMMsgId = MessageManager.get().parseChatMessageList(giftMessage);
            lastIMMsgCreateTime = giftMessage.getCreate_time();
            //数据上屏幕
            if (imMsgSystemTime != null) {
                mMessageAdapter.addFooterItemRefreshDatas(imMsgSystemTime, giftMessage);
            } else {
                mMessageAdapter.addFooterItemRefreshData(giftMessage);
            }
            rvIMDetailView.scrollToPosition(mMessageAdapter.getItemCount() - 1);
        }

    }

    /**
     * 根据position 获取个定的ItemViewHolder
     *
     * @param position
     * @return
     */
    private RecyclerView.ViewHolder getIMItemViewHolderByPosition(int position) {
        RecyclerView.ViewHolder mViewHolder = null;
        int firstItemPosition = mLinearLayoutManager.findFirstVisibleItemPosition();
        KLog.e("im_detail_play", "---------firstItemPosition-------:" + firstItemPosition);
        try {
            if (firstItemPosition == 0) {
                //非满屏状态
                KLog.e("im_detail_play", "--------- not man ping -------");
                View mView = rvIMDetailView.getChildAt(position);
                mViewHolder = rvIMDetailView.getChildViewHolder(mView);
            } else {
                //满屏幕状态
                KLog.e("im_detail_play", "---------man ping -------");
                View mView = rvIMDetailView.getChildAt(position - firstItemPosition);
                mViewHolder = rvIMDetailView.getChildViewHolder(mView);
            }
        } catch (Exception e) {
            KLog.e("im_detail_play", "---------man ping ----Exception---");
        }
        return mViewHolder;

    }

    /**
     * @param position
     * @return
     */
    private MessageDetail getIMItemDataByPosition(int position) {
        return mMessageAdapter.getItemDataByPosition(position);
    }


    @Override
    protected void onDestroy() {
        EventHelper.unregister(this);
        super.onDestroy();
    }

    private void initEmotion(List<String> emojiGroup) {
        // 获取屏幕宽度
        int screenWidth = ScreenUtil.getWidth(this);
        // item的间距
        int spacing = ScreenUtil.dip2px(this, 5);
        // 动态计算item的宽度和高度
        int itemWidth = (screenWidth - spacing * 8) / 7;
        //动态计算gridview的总高度
        int gvHeight = itemWidth * 3 + spacing * 6;

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
        GridView gv = new GridView(this);
        //设置点击背景透明
        gv.setSelector(android.R.color.transparent);
        //设置7列
        gv.setNumColumns(7);
        gv.setPadding(padding, padding, padding, padding);
        gv.setHorizontalSpacing(padding);
        gv.setVerticalSpacing(padding * 2);
        //设置GridView的宽高
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(gvWidth, gvHeight);
        gv.setLayoutParams(params);
        // 给GridView设置表情图片
        EmotionGridViewAdapter adapter = new EmotionGridViewAdapter(this, emotionNames, itemWidth, 1);
        gv.setAdapter(adapter);
        //设置全局点击事件
        gv.setOnItemClickListener(GlobalOnItemClickManagerUtils.getInstance(this).getOnItemClickListener(1));
        return gv;
    }

    /**
     * 录音消息处理
     */
    public static class MyRecordHandeler extends Handler {
        private final WeakReference<IMMessageActivity> activity;

        public MyRecordHandeler(IMMessageActivity act) {
            this.activity = new WeakReference<IMMessageActivity>(act);
        }

        @Override
        public void handleMessage(Message msg) {
            if (activity != null && activity.get() != null) {
                switch (msg.what) {
                    case HANDLER_WHAT_RECORD_UPDATE_TIME_FLAG:
                        //更新录音的时间
                        if (iCurrentRecordTime >= iMaxRecordTime) {
                            activity.get().iFinishRecordByFlag = 2;
                            activity.get().stopIMRecord();
                        } else {
                            activity.get().showRecordTimeUI(iCurrentRecordTime);
                        }
                        break;
                    case HANDLER_WHAT_RECORD_UPDATE_DECIBEL_FLAG:
                        //更新分贝数
                        break;
                }
            }
        }
    }


    /**
     * IM 发送信息handler 处理
     */
    public static class MyIMResponsHandler extends Handler {
        private final WeakReference<IMMessageActivity> activity;

        public MyIMResponsHandler(IMMessageActivity act) {
            activity = new WeakReference<IMMessageActivity>(act);
        }

        @Override
        public void handleMessage(Message msg) {
            KLog.e(TAG, "-------------handler --------" + msg.what);
            if (activity != null && activity.get() != null) {
                switch (msg.what) {
                    case HANDLER_WHAT_RESPONSE_HINT_FLAG:
                        //发送成功，不需要任何提示
                        break;
                    case HANDLER_WHAT_SHOW_TIP_FLAG:
                        //显示最新数据
                        if (activity.get() == null) {
                            break;
                        }
                        activity.get().addIMMsgLastDataByCid();
                        break;
                    case HANDLER_WHAT_SHOW_TOAST_FLAG:
                        //返回toast 提示信息
                        ToastUtil.showToast(msg.getData().getString("msg"));
                        break;
                    case HANDLER_WHAT_PLAY_RECORD_ME_FALG:
                        //播放自己的录音
                        if (activity.get() == null) {
                            break;
                        }
                        activity.get().playMeRecordMsgMsgByPosition(msg.getData().getInt("position"));
                        break;
                    case HANDLER_WHAT_PLAY_RECORD_OTHER_FALG:
                        //播放对方的
                        if (activity.get() == null) {
                            break;
                        }
                        activity.get().setAutoOrderPlayOtherRecordMsg(msg.getData().getString("msg_id"));
                        activity.get().playOtherRecordMsgMsgByPosition(msg.getData().getInt("position"));
                        break;
                }
            }
        }

    }


    /**
     * IM 发送信息handler 处理
     */
    public static class MyIMGotoOtherFragmentHandler extends Handler {
        private final WeakReference<IMMessageActivity> activity;

        public MyIMGotoOtherFragmentHandler(IMMessageActivity act) {
            activity = new WeakReference<IMMessageActivity>(act);
        }

        @Override
        public void handleMessage(Message msg) {
            if (activity != null && activity.get() != null) {
                switch (msg.what) {
                    case HANDLER_WHAT_GOTO_OTHER_PAGE:
                        //跳转其他link
                        DcRouter.linkTo(activity.get(), (String) msg.obj);
                        break;
                }
            }
        }
    }
}
