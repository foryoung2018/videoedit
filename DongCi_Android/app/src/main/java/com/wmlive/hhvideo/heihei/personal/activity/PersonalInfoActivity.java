package com.wmlive.hhvideo.heihei.personal.activity;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.wmlive.hhvideo.common.GlobalParams;
import com.wmlive.hhvideo.common.base.DcBaseActivity;
import com.wmlive.hhvideo.heihei.beans.login.UserInfo;
import com.wmlive.hhvideo.heihei.beans.login.VerifyEntity;
import com.wmlive.hhvideo.heihei.beans.personal.WeiboBindEntity;
import com.wmlive.hhvideo.heihei.beans.splash.InitCatchData;
import com.wmlive.hhvideo.heihei.login.AccountUtil;
import com.wmlive.hhvideo.heihei.personal.presenter.BaseUserInfoPresenter;
import com.wmlive.hhvideo.heihei.personal.presenter.PersonalInfoPresenter;
import com.wmlive.hhvideo.heihei.personal.view.IPersonalInfoView;
import com.wmlive.hhvideo.heihei.personal.widget.BirthdayDialog;
import com.wmlive.hhvideo.utils.DeviceUtils;
import com.wmlive.hhvideo.utils.HeaderUtils;
import com.wmlive.hhvideo.utils.KLog;
import com.wmlive.hhvideo.utils.MyClickListener;
import com.wmlive.hhvideo.utils.RegexUtil;
import com.wmlive.hhvideo.utils.SdkUtils;
import com.wmlive.hhvideo.utils.StringUtils;
import com.wmlive.hhvideo.utils.ToastUtil;
import com.wmlive.hhvideo.utils.imageloader.GlideLoader;
import com.wmlive.hhvideo.utils.location.AMapLocateCallback;
import com.wmlive.hhvideo.utils.location.AMapLocator;
import com.wmlive.hhvideo.utils.location.LocationEntity;
import com.wmlive.hhvideo.widget.dialog.CustomDialog;
import com.wmlive.networklib.entity.EventEntity;
import com.wmlive.networklib.util.EventHelper;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import cn.wmlive.hhvideo.R;
import cn.wmlive.hhvideo.wxapi.WbBindPresenter;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

/**
 * Created by XueFei on 2017/5/27.
 * <p>
 * <p>
 * 个人信息页
 */

public class PersonalInfoActivity extends DcBaseActivity<PersonalInfoPresenter> implements
        IPersonalInfoView,
        AMapLocateCallback,
        WbBindPresenter.IWbBindView, BaseUserInfoPresenter.IBaseUserInfoView {

    public static final int LOCATION_CODE = 1001;

    @BindView(R.id.iv_head)
    ImageView ivHead;
    @BindView(R.id.et_nickname)
    EditText etNickname;
    @BindView(R.id.btn_male)
    Button btnMale;
    @BindView(R.id.btn_female)
    Button btnFemale;
    @BindView(R.id.btn_birthday)
    RelativeLayout btnBirthday;
    @BindView(R.id.llAuth)
    LinearLayout llAuth;
    @BindView(R.id.rlWeiboHome)
    RelativeLayout rlWeiboHome;
    @BindView(R.id.tv_birthday_tip)
    TextView tvBirthdayTip;
    @BindView(R.id.tv_collensation)
    TextView tvCollensation;
    @BindView(R.id.btn_city)
    RelativeLayout btnCity;
    @BindView(R.id.iv_city)
    ImageView ivCity;
    @BindView(R.id.tv_city)
    TextView tvCity;
    @BindView(R.id.tvAuth)
    TextView tvAuth;
    @BindView(R.id.tvWeiboHome)
    TextView tvWeiboHome;
    @BindView(R.id.ivVerifyIcon)
    ImageView ivVerifyIcon;
    @BindView(R.id.et_sign)
    EditText etSign;

    public static final String KEY_PARAM = "user";
    public static final String UPDATE_HEAD_ACTION = "update_head";
    public static final String UPDATE_HEAD_URL_KEY = "update_head_url_key";
    public static final String UPDATE_HEAD_ORI_KEY = "update_head_ori_key";
    public static final String UPDATE_HEAD_SIGN_KEY = "update_head_sign_key";

    private String cityName = "";
    private ManageActivityBroadCast manageActivityBroadCast;
    private String name;
    private String gender;
    private String birth_day;
    private String region;
    private String description;
    private String cover_url;
    private String cover_ori;
    private String cover_ori_file_sign;
    private UserInfo userInfo;
    private WbBindPresenter wbBindPresenter;
    private BaseUserInfoPresenter userInfoPresenter;

    private boolean isEdit = false;

    private AMapLocator aMapLocator;

    public static void startPersonalInfoActivity(Context context, UserInfo userInfo) {
        Intent intent = new Intent(context, PersonalInfoActivity.class);
        intent.putExtra(KEY_PARAM, userInfo);
        context.startActivity(intent);
    }

    @Override
    protected PersonalInfoPresenter getPresenter() {
        return new PersonalInfoPresenter(this);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_personal_edit_info;
    }

    @Override
    protected void initData() {
        super.initData();
        userInfo = (UserInfo) getIntent().getSerializableExtra(KEY_PARAM);
        if (userInfo != null && userInfo.getId() > 0) {
            EventHelper.register(this);
            setTitle(R.string.user_edit_tip, true);
            TextView tvNext = new TextView(this);
            tvNext.setText(getString(R.string.user_edit_save));
            tvNext.setTextSize(16);
            tvNext.setTextColor(getResources().getColor(R.color.hh_color_e));
            TypedValue tv = new TypedValue();
            if (SdkUtils.isLollipop()) {
//            getTheme().resolveAttribute(android.R.attr.selectableItemBackgroundBorderless, tv, true);
            }
            tvNext.setBackgroundResource(tv.resourceId);
            tvNext.setGravity(Gravity.CENTER);
            tvNext.setPadding(10, 6, DeviceUtils.dip2px(this, 15), 6);
            setToolbarRightView(tvNext, new MyClickListener() {
                @Override
                protected void onMyClick(View v) {
                    saveUser();
                }
            });
            userInfoPresenter = new BaseUserInfoPresenter(this);
            wbBindPresenter = new WbBindPresenter(this);
            addPresenter(wbBindPresenter, userInfoPresenter);
            refreshInfo();
            bindListener();
            aMapLocator = new AMapLocator(this).setOnceLocation(true).setLocateCallback(this);
            new RxPermissions(this)
                    .request(Manifest.permission.ACCESS_FINE_LOCATION)
                    .subscribe(new Consumer<Boolean>() {
                        @Override
                        public void accept(@NonNull Boolean aBoolean) throws Exception {
                            if (aBoolean) {
                                aMapLocator.startLocate();
                            } else {
                                ToastUtil.showToast("定位权限禁止后该功能不能正常使用，请在应用程序管理中开启权限");
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(@NonNull Throwable throwable) throws Exception {
                            throwable.printStackTrace();
                        }
                    });
            if (null == manageActivityBroadCast) {
                manageActivityBroadCast = new ManageActivityBroadCast();
            }
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(UPDATE_HEAD_ACTION);
            registerReceiver(manageActivityBroadCast, intentFilter);
        } else {
            toastFinish();
        }
    }

    private void refreshInfo() {
        if (null != userInfo) {
            cover_url = userInfo.getCover_url();
            cover_ori = userInfo.getCover_ori();
            GlideLoader.loadCircleImage(userInfo.getCover_url(), ivHead, userInfo.isFemale() ? R.drawable.ic_default_female : R.drawable.ic_default_male);
            name = userInfo.getName();
            if (!TextUtils.isEmpty(name)) {
                etNickname.setText(name);
            }
            gender = userInfo.getGender();
            if (UserInfo.MALE.equals(gender)) {
                btnMale.setSelected(true);
                btnMale.setTextColor(getResources().getColor(R.color.hh_color_a));
                btnFemale.setTextColor(getResources().getColor(R.color.hh_color_b));
            } else if (UserInfo.FEMALE.equals(gender)) {
                btnFemale.setSelected(true);
                btnMale.setTextColor(getResources().getColor(R.color.hh_color_b));
                btnFemale.setTextColor(getResources().getColor(R.color.hh_color_a));
            } else {
                btnMale.setSelected(true);
                btnMale.setTextColor(getResources().getColor(R.color.hh_color_a));
                btnFemale.setTextColor(getResources().getColor(R.color.hh_color_b));
            }

            birth_day = userInfo.getBirth_day();
            if (!TextUtils.isEmpty(birth_day)) {
                tvBirthdayTip.setText(birth_day);
                tvBirthdayTip.setTextColor(getResources().getColor(R.color.hh_color_c));

                tvCollensation.setText(StringUtils.getConstellation(StringUtils.parseYMD(birth_day)));
                tvCollensation.setTextColor(getResources().getColor(R.color.hh_color_c));
            }

            region = userInfo.getRegion();
            if (!TextUtils.isEmpty(region)) {
                tvCity.setText(region);
                tvCity.setTextColor(getResources().getColor(R.color.hh_color_c));
                ivCity.setVisibility(View.VISIBLE);
            }

            description = userInfo.getDescription();
            if (!TextUtils.isEmpty(description)) {
                etSign.setText(description);
            }
            setWeiboAuthView(userInfo);
            setBindWeiboView();
        }
    }

    private void setBindWeiboView() {
        if (userInfo != null && userInfo.getBind_weibo() != null && !TextUtils.isEmpty(userInfo.getBind_weibo().weibo_id)) {
            tvWeiboHome.setText(userInfo.getBind_weibo().weibo_name);
            tvWeiboHome.setTextColor(getResources().getColor(R.color.hh_color_c));
            rlWeiboHome.setTag(true);
        } else {
            rlWeiboHome.setTag(false);
            tvWeiboHome.setText(getString(R.string.hintBindWeibo));
            tvWeiboHome.setTextColor(getResources().getColor(R.color.hh_color_b));
        }
    }

    private void setWeiboAuthView(UserInfo userInfo) {
        if (userInfo != null && userInfo.getVerify() != null) {
            if (TextUtils.isEmpty(userInfo.getVerify().type) || "normal".equalsIgnoreCase(userInfo.getVerify().type)) {
                tvAuth.setText(R.string.hintAuth);
                tvAuth.setTextColor(getResources().getColor(R.color.hh_color_b));
                ivVerifyIcon.setVisibility(View.GONE);
            } else {
                ivVerifyIcon.setVisibility(View.VISIBLE);
                tvAuth.setText(userInfo.getVerify().verify_reason);
                tvAuth.setTextColor(getResources().getColor(R.color.hh_color_c));
                GlideLoader.loadImage(userInfo.getVerify().icon, ivVerifyIcon);
            }
        } else {
            ivVerifyIcon.setVisibility(View.GONE);
            tvAuth.setText(R.string.hintAuth);
            tvAuth.setTextColor(getResources().getColor(R.color.hh_color_b));
        }
    }

    private void bindListener() {
        InputFilter[] filters = new InputFilter[1];
        filters[0] = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                isEdit = true;
                int sourceLen = StringUtils.getLengthOfByteCode(source.toString());
                int destLen = StringUtils.getLengthOfByteCode(dest.toString());

                if (dstart == 0 && StringUtils.isBlackChar(source.toString())) {
                    return "";
                }

                if (dest.toString().endsWith(" ") && StringUtils.isBlackChar(source.toString())) {
                    return "";
                }

                if (dstart >= 1 && dstart <= dest.length()) {
                    String sectionStr = dest.subSequence(dstart - 1, dstart).toString();
                    if (StringUtils.isBlackChar(sectionStr) && StringUtils.isBlackChar(source.toString())) {
                        return "";
                    }
                }

                if (sourceLen + destLen > 16) {
                    showToast(R.string.account_nickname_too_long);
                    try {
                        if (source.length() >= 16) {
                            return source.subSequence(0, 16);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    return "";
                }
                return source;
            }
        };
        etNickname.setFilters(filters);

        etNickname.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                return (event.getKeyCode() == KeyEvent.KEYCODE_ENTER);
            }
        });

        InputFilter filter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                isEdit = true;
                int sourceLen = StringUtils.getLengthOfByteCode(source.toString());
                int destLen = StringUtils.getLengthOfByteCode(dest.toString());

                if (dstart == 0 && StringUtils.isBlackChar(source.toString()))// 第一个不能为空格
                {
                    return "";
                }

                if (dest.toString().endsWith(" ") && StringUtils.isBlackChar(source.toString()))// 不能连续两个空格
                {
                    return "";
                }

                if (dstart >= 1 && dstart <= dest.length()) {
                    String sectionStr = dest.subSequence(dstart - 1, dstart).toString();
                    if (StringUtils.isBlackChar(sectionStr) && StringUtils.isBlackChar(source.toString())) {
                        return "";
                    }
                }

                if (sourceLen + destLen > 48) {
                    showToast(R.string.account_sign_too_long);
                    return "";
                }
                return source;
            }
        };
        etSign.setFilters(new InputFilter[]{filter});
        ivHead.setOnClickListener(this);
        btnMale.setOnClickListener(this);
        btnFemale.setOnClickListener(this);
        btnBirthday.setOnClickListener(this);
        btnCity.setOnClickListener(this);
        llAuth.setOnClickListener(this);
        rlWeiboHome.setOnClickListener(this);
    }

    @Override
    public void handleInfoSucceed() {
        showToast(R.string.save_suc);
        dismissLoad();
        finish();
    }

    @Override
    public void handleInfoFailure(String error_msg) {
        showToast(error_msg);
        dismissLoad();
    }

    @Override
    protected void onSingleClick(View v) {
        switch (v.getId()) {
            case R.id.iv_head:
                new RxPermissions(PersonalInfoActivity.this)
                        .request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .subscribe(new Consumer<Boolean>() {
                            @Override
                            public void accept(@NonNull Boolean aBoolean) throws Exception {
                                if (aBoolean) {
                                    PhotoPicketActivity.startPhotoPicketActivity(PersonalInfoActivity.this);
                                } else {
                                    showToast("存储权限禁止后该功能不能正常使用，请在应用程序管理中开启权限");
                                }
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(@NonNull Throwable throwable) throws Exception {
                                throwable.printStackTrace();
                            }
                        });

                break;
            case R.id.btn_male:
                if (!btnMale.isSelected()) {
                    isEdit = true;
                    btnMale.setSelected(true);
                    btnFemale.setSelected(false);
                    gender = UserInfo.MALE;
                    btnMale.setTextColor(getResources().getColor(R.color.hh_color_a));
                    btnFemale.setTextColor(getResources().getColor(R.color.hh_color_b));
                }
                break;
            case R.id.btn_female:
                if (!btnFemale.isSelected()) {
                    isEdit = true;
                    btnMale.setSelected(false);
                    btnFemale.setSelected(true);
                    btnFemale.setTextColor(getResources().getColor(R.color.hh_color_a));
                    btnMale.setTextColor(getResources().getColor(R.color.hh_color_b));
                    gender = UserInfo.FEMALE;
                }
                break;
            case R.id.btn_birthday:
                BirthdayDialog bd = new BirthdayDialog(this, StringUtils.parseYMD(birth_day));
                bd.setOnDatePickListener(new BirthdayDialog.OnDatePickListener() {

                    @Override
                    public void onDatePick(int year, int month, int day) {
                        isEdit = true;
                        String monthStr = "";
                        String dayStr = "";
                        if (month < 10) {
                            monthStr = "0" + String.valueOf(month);
                        } else {
                            monthStr = String.valueOf(month);
                        }
                        if (day < 10) {
                            dayStr = "0" + String.valueOf(day);
                        } else {
                            dayStr = String.valueOf(day);
                        }
                        birth_day = year + "-" + monthStr + "-" + dayStr;
                        tvBirthdayTip.setText(birth_day);
                        tvCollensation.setText(StringUtils.getConstellation(StringUtils.parseYMD(birth_day)));
                    }
                });
                bd.show();
                break;
            case R.id.btn_city:
                startActivityForResult(new Intent(this, LocationActivity.class), LOCATION_CODE);
                break;
            case R.id.llAuth:
                String url = InitCatchData.getWeiboVerified();
                if (!TextUtils.isEmpty(url) && RegexUtil.isUrl(url)) {
                    WebViewActivity.startWeiboAuth(this, url + "?token=" + AccountUtil.getToken(), "认证", true);
                } else {
                    showToast(R.string.hintErrorDataDelayTry);
                }
                break;
            case R.id.rlWeiboHome:
                if (rlWeiboHome.getTag() != null) {
                    boolean isBind = (boolean) rlWeiboHome.getTag();
                    if (isBind) {
                        CustomDialog customDialog = new CustomDialog(this);
                        customDialog.setContent("是否解除绑定该微博");
                        customDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                customDialog.dismiss();
                            }
                        });

                        customDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                wbBindPresenter.unBindWeibo();
                                customDialog.dismiss();
                            }
                        });
                        customDialog.show();
                    } else {
                        weiboLogin(true);
                    }
                } else {
                    weiboLogin(true);
                }
                break;
            default:
                break;
        }
    }


    @Override
    protected void onWeiboAuthOk(Oauth2AccessToken accessToken) {
        super.onWeiboAuthOk(accessToken);
        if (accessToken != null && !TextUtils.isEmpty(accessToken.getToken())) {
            wbBindPresenter.bindWeibo(accessToken.getToken(), accessToken.getUid());
        } else {
            showToast("绑定微博失败，请稍后再试");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && null != data) {
            if (requestCode == LOCATION_CODE) {
                String cityName = data.getStringExtra("city");
                region = cityName;
//                String latlng = data.getStringExtra("latlng");
                tvCity.setText(cityName);
                tvCity.setTextColor(getResources().getColor(R.color.hh_color_c));
                ivCity.setVisibility(View.VISIBLE);
            }
        }
    }

    private void saveUser() {
        if (null != userInfo) {
            name = etNickname.getText().toString();
            description = etSign.getText().toString();
            if (TextUtils.isEmpty(name)) {
                showToast(R.string.user_input_nick_name_null);
                return;
            }

            loading();
            presenter.updateUser(name, gender, birth_day, region, description, cover_ori, cover_ori_file_sign);
        }
    }

    @Override
    public void onLocateOk(final LocationEntity entity) {
        if (entity != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    cityName = entity.city;
                    if (!region.equalsIgnoreCase(cityName)) {
                        isEdit = true;
                    }
                    region = cityName;
                    tvCity.setText(region);
                    tvCity.setTextColor(getResources().getColor(R.color.hh_color_c));
                    ivCity.setVisibility(View.VISIBLE);
                    //更新位置信息
                    HeaderUtils.updateLatlon(entity.latitude, entity.longitude, entity.city);
                }
            });

        }
    }

    @Override
    public void onLocateFailed() {
        showToast("定位失败");
    }

    @Override
    public void onBindOk(WeiboBindEntity response) {
        showToast("绑定微博成功");
        if (userInfo != null) {
            if (response != null) {
                userInfo.setBind_weibo(response.bind_weibo);
            } else {
                userInfo.setBind_weibo(null);
            }
        }
        dismissLoad();
        setBindWeiboView();
    }

    @Override
    public void onUnBindOk() {
        showToast("解除绑定微博成功");
        if (userInfo != null) {
            userInfo.setBind_weibo(null);
        }
        setBindWeiboView();
        dismissLoad();
    }

    @Override
    public void onGetUserInfoOk(UserInfo userInfo) {
        setWeiboAuthView(userInfo);
    }

    @Override
    public void onGetUserInfoFail(String message) {

    }

    private class ManageActivityBroadCast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (null != intent) {
                String action = intent.getAction();
                if (UPDATE_HEAD_ACTION.equals(action)) {
                    isEdit = true;
                    String url = intent.getStringExtra(UPDATE_HEAD_URL_KEY);
                    cover_ori = intent.getStringExtra(UPDATE_HEAD_ORI_KEY);
                    cover_ori_file_sign = intent.getStringExtra(UPDATE_HEAD_SIGN_KEY);
                    GlideLoader.loadCircleImage(url, ivHead, R.drawable.ic_default_male);
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        EventHelper.unregister(this);
        if (aMapLocator != null) {
            aMapLocator.stopLocate();
            aMapLocator = null;
        }
        if (null != manageActivityBroadCast) {
            unregisterReceiver(manageActivityBroadCast);
        }
        super.onDestroy();
    }

    private void back() {
        if (isEdit) {
            CustomDialog customDialog = new CustomDialog(this, R.style.BaseDialogTheme);
            customDialog.setContent(getString(R.string.dialog_personal_save_title));
            customDialog.setPositiveButton(R.string.dialog_personal_save_positive, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    customDialog.dismiss();
                    saveUser();
                }
            });
            customDialog.setNegativeButton(R.string.dialog_personal_save_negative, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    customDialog.dismiss();
                    finish();
                }
            });
            customDialog.show();
        } else {
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        back();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onWeiAuthEvent(EventEntity eventEntity) {
        if (eventEntity != null && eventEntity.code == GlobalParams.EventType.TYPE_WEIBO_AUTH_OK) {
            KLog.i("====收到微博认证成功的消息");
            if (userInfo != null) {
                if (eventEntity.data != null) {
                    if (eventEntity.data instanceof VerifyEntity) {
                        userInfo.setVerify((VerifyEntity) eventEntity.data);
                    } else if (eventEntity.data instanceof Boolean) {
                        if (userInfoPresenter != null) {
                            userInfoPresenter.getPersonalInfo(userInfo.getId());
                        }
                    } else {
                        userInfo.setVerify(null);
                    }
                }
            }
            setWeiboAuthView(userInfo);
        }
    }
}
