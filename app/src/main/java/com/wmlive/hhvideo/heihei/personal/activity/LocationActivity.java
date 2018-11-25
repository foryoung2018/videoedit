package com.wmlive.hhvideo.heihei.personal.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tbruyelle.rxpermissions2.RxPermissions;
import com.wmlive.hhvideo.common.base.DcBaseActivity;
import com.wmlive.hhvideo.utils.ToastUtil;
import com.wmlive.hhvideo.utils.location.AMapLocateCallback;
import com.wmlive.hhvideo.utils.location.AMapLocator;
import com.wmlive.hhvideo.utils.location.LocationEntity;

import butterknife.BindView;
import cn.wmlive.hhvideo.R;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

/**
 * Created by XueFei on 2017/6/3.
 * <p>
 * 城市定位
 * modify by lsq
 */

public class LocationActivity extends DcBaseActivity implements AMapLocateCallback {

    @BindView(R.id.btn_back)
    RelativeLayout btnBlack;
    @BindView(R.id.img_location)
    ImageView imgLocation;
    @BindView(R.id.text_city)
    TextView textCity;
    @BindView(R.id.tv_location_permission)
    TextView tvLocationPermission;
    @BindView(R.id.tv_location_permission_set)
    TextView tvLocationPermissionSet;

    private String cityName = "";
    private AMapLocator aMapLocator;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_location;
    }

    @Override
    protected void initData() {
        super.initData();
        setTitle(R.string.user_location_ttitle, true);
        btnBlack.setOnClickListener(this);
        aMapLocator = new AMapLocator(this).setOnceLocation(true).setLocateCallback(this);
        new RxPermissions(this)
                .request(Manifest.permission.ACCESS_FINE_LOCATION)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(@NonNull Boolean aBoolean) throws Exception {
                        if (aBoolean) {
                            if (tvLocationPermission != null) {
                                tvLocationPermission.setText(getString(R.string.user_location_permission_suc));
                            }
                            aMapLocator.startLocate();
                        } else {
                            if (tvLocationPermission != null) {
                                tvLocationPermission.setText(getString(R.string.user_location_permission_faid));
                            }
                            ToastUtil.showToast("定位权限禁止后该功能不能正常使用，请在应用程序管理中开启权限");
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                        if (tvLocationPermission != null) {
                            tvLocationPermission.setText(getString(R.string.user_location_permission_faid));
                        }
                    }
                });
    }

    @Override
    public void onLocateOk(final LocationEntity entity) {
        if (entity != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    imgLocation.setImageResource(R.drawable.icon_profile_place_nor);
                    cityName = entity.city;
                    textCity.setText(cityName);
                    textCity.setTextColor(getResources().getColor(R.color.hh_color_c));
//                latlng = location.latitude + "," + location.longitude;
                }
            });
        } else {
            textCity.setText(getText(R.string.user_location_fail));
            showToast("定位失败");
        }
    }

    @Override
    public void onLocateFailed() {
        showToast("定位失败");
        textCity.setText(getText(R.string.user_location_fail));
    }

    @Override
    protected void onSingleClick(View v) {
        if (v.getId() == R.id.btn_back) {
            if (!TextUtils.isEmpty(cityName)) {
                Intent intent = new Intent();
                intent.putExtra("city", cityName);
                setResult(Activity.RESULT_OK, intent);
                finish();
            } else {
                finish();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (aMapLocator != null) {
            aMapLocator.stopLocate();
            aMapLocator = null;
        }
    }


}
