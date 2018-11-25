package com.wmlive.hhvideo.heihei.personal.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.alibaba.sdk.android.oss.model.OSSRequest;
import com.wmlive.hhvideo.common.base.DcBaseActivity;
import com.wmlive.hhvideo.common.ossutils.UpLoadInterface;
import com.wmlive.hhvideo.common.ossutils.UploadALiResultBean;
import com.wmlive.hhvideo.heihei.beans.oss.OSSTokenResponse;
import com.wmlive.hhvideo.heihei.personal.util.OssTokenAndUploadUtils;
import com.wmlive.hhvideo.heihei.personal.widget.ClipImageLayout;
import com.wmlive.hhvideo.utils.AppCacheFileUtils;
import com.wmlive.hhvideo.utils.DeviceUtils;
import com.wmlive.hhvideo.utils.ImageUtils;
import com.wmlive.hhvideo.utils.MyClickListener;
import com.wmlive.hhvideo.utils.SdkUtils;

import java.io.File;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import butterknife.BindView;
import cn.wmlive.hhvideo.R;

/**
 * Created by XueFei on 2017/6/6.
 * <p>
 * 图片裁剪
 */

public class PhotoCutActivity extends DcBaseActivity implements UpLoadInterface {
    private String strSourcesPhonePath = "";//源图片地址

    @BindView(R.id.clipImageLayout)
    ClipImageLayout mClipImageLayout;

    private OssTokenAndUploadUtils mOssTokenAndUploadUtils;

    private String strSaveImgPath;//保存图片地址
    private TextView tvNext;

    @Override
    protected int getLayoutResId() {
        return R.layout.phone_cut_image_layot;
    }

    @Override
    protected void initData() {
        super.initData();
        setTitle(R.string.cut_title, true);
        tvNext = new TextView(this);
        tvNext.setText(getString(R.string.user_edit_save));
        tvNext.setTextSize(16);
        tvNext.setTextColor(getResources().getColor(R.color.hh_color_g));
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
                loading();
                ImageUtils.setBitmapToFile(strSaveImgPath, mClipImageLayout.clip());
                //阿里巴巴上传
                mOssTokenAndUploadUtils.setStrUploadPath(strSaveImgPath);
                mOssTokenAndUploadUtils.getOssTokenUploadByNetwork("jpg", "avatar");
            }
        });

        strSaveImgPath = AppCacheFileUtils.getAppTempPath() + File.separator + getRandomFileName() + ".jpg";
        mOssTokenAndUploadUtils = new OssTokenAndUploadUtils(this, this, strSaveImgPath);

        strSourcesPhonePath = getIntent().getStringExtra(KEY_PARAM);
        mClipImageLayout.setImageBitmap(ImageUtils.getBitmapFromFile(strSourcesPhonePath));

//        ivBack.setOnClickListener(this);
//        tvSave.setOnClickListener(this);
    }

    @Override
    protected void onSingleClick(View v) {
//        if (v.getId() == R.id.iv_back) {
//            finish();
//        } else if (v.getId() == R.id.tv_save) {
//            loading();
//            ImageUtils.setBitmapToFile(strSaveImgPath, mClipImageLayout.clip());
//            //阿里巴巴上传
//            mOssTokenAndUploadUtils.setStrUploadPath(strSaveImgPath);
//            mOssTokenAndUploadUtils.getOssTokenUploadByNetwork("jpg", "avatar");
//        }
    }

    /**
     * 随机生成文件名称
     *
     * @return
     */
    public String getRandomFileName() {
        String rel = "";
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        Date curDate = new Date(System.currentTimeMillis());
        rel = formatter.format(curDate);
        rel = rel + new Random().nextInt(1000);
        return rel;
    }

    private PhotoCutActivity.FinishiHandler finishiHandler = new PhotoCutActivity.FinishiHandler(this);

    static class FinishiHandler extends Handler {
        WeakReference<PhotoCutActivity> photoCutNewFragmentWeakReference;

        public FinishiHandler(PhotoCutActivity photoCutNewFragment) {
            photoCutNewFragmentWeakReference = new WeakReference<>(photoCutNewFragment);
        }

        @Override
        public void handleMessage(Message msg) {
            final PhotoCutActivity photoCutNewFragment = photoCutNewFragmentWeakReference.get();
            if (null != photoCutNewFragment) {
                photoCutNewFragment.finish();
            }
        }
    }

    /**
     * 上传用户的形象照
     */
    @Override
    public void onSuccessUpload(UploadALiResultBean obj) {
        dismissLoad();
        OSSTokenResponse ossTokenResult = obj.getmOssTokenResult();
        if (null != ossTokenResult) {
            try {
                sendBroadcast(new Intent(PhotoPicketActivity.FINISH_ACTION));
                Intent intent = new Intent(PersonalInfoActivity.UPDATE_HEAD_ACTION);
                intent.putExtra(PersonalInfoActivity.UPDATE_HEAD_URL_KEY, strSaveImgPath);
                intent.putExtra(PersonalInfoActivity.UPDATE_HEAD_ORI_KEY, ossTokenResult.getFileInfo().getPath());
                intent.putExtra(PersonalInfoActivity.UPDATE_HEAD_SIGN_KEY, ossTokenResult.getFileInfo().getSign());
                sendBroadcast(intent);

                finishiHandler.sendEmptyMessageDelayed(0, 500);
            } catch (Exception e) {
                showToast("上传失败");
            }
        }
    }

    @Override
    public void onFailsUpload(UploadALiResultBean obj) {
        dismissLoad();
        if (obj.getUpload_type() == UploadALiResultBean.TYPE_UPLOAD_OSSTOKEN) {
            showToast("保存失败");
        } else {
            showToast("保存失败");
        }
    }

    @Override
    public void onExceptionUpload(UploadALiResultBean e) {
        dismissLoad();
    }

    @Override
    public void onProgress(OSSRequest request, long currentSize, long totalSize) {

    }

    public static final String KEY_PARAM = "path";

    public static void startPhotoCutActivity(Context context, String path) {
        Intent intent = new Intent(context, PhotoCutActivity.class);
        intent.putExtra(KEY_PARAM, path);
        context.startActivity(intent);
    }
}
