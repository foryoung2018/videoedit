package com.wmlive.hhvideo.widget;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wmlive.hhvideo.common.GlobalParams;
import com.wmlive.hhvideo.common.manager.LogFileManager;
import com.wmlive.hhvideo.heihei.beans.opus.PublishResponseEntity;
import com.wmlive.hhvideo.heihei.db.ProductEntity;
import com.wmlive.hhvideo.heihei.record.activity.PublishActivity;
import com.wmlive.hhvideo.heihei.record.manager.RecordManager;
import com.wmlive.hhvideo.heihei.record.presenter.AbsPublishView;
import com.wmlive.hhvideo.heihei.record.presenter.PublishPresenter;
import com.wmlive.hhvideo.heihei.record.utils.RecordUtil;
import com.wmlive.hhvideo.utils.KLog;
import com.wmlive.hhvideo.utils.ToastUtil;
import com.wmlive.hhvideo.utils.WeakHandler;
import com.wmlive.networklib.util.EventHelper;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import cn.wmlive.hhvideo.R;
import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import static com.wmlive.hhvideo.utils.ToastUtil.showToast;

/**
 * 上传作品进度页面
 * 发布作品成功后的分享调用PopupWindowUtils.showUploadResultPanel
 */
public class UploadMaskView extends BaseCustomView implements Handler.Callback {
    @BindView(R.id.tvProgress)
    CustomFontTextView tvProgress;
    @BindView(R.id.ivResult)
    ImageView ivResult;
    @BindView(R.id.tvErrorHint)
    TextView tvErrorHint;
    @BindView(R.id.tvUploadHint)
    TextView tvUploadHint;
    @BindView(R.id.tvRetry)
    TextView tvRetry;
    @BindView(R.id.tvExit)
    TextView tvExit;
    @BindView(R.id.rlMaskRoot)
    RelativeLayout rlMaskRoot;
    @BindView(R.id.pbProgress)
    ProgressBar pbProgress;

    public static final byte STATUS_NORMAL = 0;
    public static final byte STATUS_PUBLISHING = 1;
    public static final byte STATUS_PUBLISH_OK = 2;
    public static final byte STATUS_PUBLISH_FAIL = 3;
    public static final byte STATUS_PUBLISH_START = 4;

    private byte publishStatus = STATUS_NORMAL;//0还未开始，1正在发布，2发布成功，3发布失败

    private static final int MSG_PUBLISH_ABNORMALITY_ERROR = 30;
    private static final int MSG_REFRESH_PROGRESS = 40;
    private static final int MSG_PUBLISH_OK = 50;
    private static final int MSG_PUBLISH_FAIL = 60;
    private static final int MSG_PUBLISH_START = 70;

    private ProductEntity productEntity;
    private PublishPresenter publishPresenter;
    private volatile int successCount = 2; // 成功的个数 (发布成功+1与导出水印作品+1)
    private UploadMaskListener uploadMaskListener;
    private WeakHandler weakHandler;
    private boolean insertGallery;

    public UploadMaskView(Context context) {
        super(context);
    }

    public UploadMaskView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public UploadMaskView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void initViews(Context context, AttributeSet attrs, int defStyle) {
        weakHandler = new WeakHandler(Looper.getMainLooper(), this);
        tvRetry.setOnClickListener(this);
        tvExit.setOnClickListener(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_upload_mask;
    }

    @Override
    protected void onSingleClick(View v) {
        super.onSingleClick(v);
        switch (v.getId()) {
            case R.id.tvRetry:
                if (publishPresenter != null) {
                    publishPresenter.retryPublish();
                }
                break;
            case R.id.tvExit:
                if (uploadMaskListener != null) {
                    uploadMaskListener.onPublishExit(publishStatus != STATUS_PUBLISH_FAIL);
                }
                break;
            default:
                break;
        }
    }

    private int currentProgress = 0;

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_PUBLISH_ABNORMALITY_ERROR:
            case MSG_PUBLISH_FAIL:
                onPublishError(STATUS_PUBLISH_FAIL, "publish abnormality error");
                break;
            case MSG_PUBLISH_START:
                tvProgress.setText("0%");
                break;
            case MSG_REFRESH_PROGRESS:
                publishStatus = STATUS_PUBLISHING;
                if (currentProgress < msg.arg1) {
                    currentProgress = msg.arg1;
                    tvProgress.setText(msg.arg1 + "%");
                }
                tvUploadHint.setText(getResources().getString(R.string.stringUploadingDoNotExit));
                showStatus(STATUS_PUBLISHING);
                break;
            case MSG_PUBLISH_OK:
                tvProgress.setText("100%");
                tvUploadHint.setText("上传成功");
//                ToastUtil.showToast("发布成功");
                if (uploadMaskListener != null) {
                    uploadMaskListener.onUploadOk((PublishResponseEntity) msg.obj);
                }
                publishStatus = STATUS_PUBLISH_OK;
                showStatus(STATUS_PUBLISH_OK);
                if (publishPresenter != null) {
                    publishPresenter.destroy();
                }
                break;
            default:
                break;
        }
        return false;
    }

    private void showStatus(int status) {
        pbProgress.setVisibility(status == STATUS_PUBLISHING ? VISIBLE : GONE);
        ivResult.setVisibility(status == STATUS_PUBLISH_OK || status == STATUS_PUBLISH_FAIL ? VISIBLE : GONE);
        ivResult.setImageResource(status == STATUS_PUBLISH_OK ? R.drawable.icon_upload_success : R.drawable.icon_upload_defeat);
        tvUploadHint.setVisibility(status == STATUS_PUBLISHING ? VISIBLE : GONE);
        tvErrorHint.setVisibility(status == STATUS_PUBLISH_FAIL ? VISIBLE : GONE);
        tvErrorHint.setTextColor(getResources().getColor(status == STATUS_PUBLISH_FAIL ? R.color.hh_color_ff : R.color.hh_color_b));
        tvRetry.setVisibility(status == STATUS_PUBLISH_FAIL ? VISIBLE : GONE);
        tvExit.setVisibility(status == STATUS_PUBLISH_FAIL || status == STATUS_PUBLISH_OK ? VISIBLE : GONE);
    }

    public void doPublish(boolean insertGallery) {
        this.insertGallery = insertGallery;
        if (RecordManager.get().getProductEntity() != null) {
            try {
                productEntity = (ProductEntity) RecordManager.get().getProductEntity().clone();
                if (productEntity != null) {
                    publishStatus = STATUS_PUBLISHING;
                    if (publishPresenter == null) {
                        publishPresenter = new PublishPresenter(publishView);
                    }
                    RecordManager.get().setPublishingProductId(productEntity.getId());
                    successCount = insertGallery ? 2 : 1;
//                RecordManager.get().clearAll();
//                    publishPresenter.setAct((PublishActivity) getContext());
                    publishPresenter.preparePublish(productEntity, insertGallery);
                } else {
                    publishStatus = STATUS_PUBLISH_FAIL;
                    onPublishError(1, "clone productEntity is null");
                    KLog.i("=====克隆productEntity为空");
                    showToast(getResources().getString(R.string.hintErrorDataDelayTry));
                }
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
                publishStatus = STATUS_PUBLISH_FAIL;
                onPublishError(2, "clone fail");
                KLog.i("=====克隆productEntity失败：" + e.getMessage());
                showToast(getResources().getString(R.string.hintErrorDataDelayTry));
            }
        } else {
            publishStatus = STATUS_PUBLISH_FAIL;
            onPublishError(2, "productEntity is null");
            KLog.i("=====productEntity为空");
            showToast(getResources().getString(R.string.hintErrorDataDelayTry));
        }
    }

    private AbsPublishView publishView = new AbsPublishView() {
        @Override
        public void onPublishStart(int index) {
            currentProgress = 0;
            publishStatus = STATUS_PUBLISH_START;
            RecordUtil.moveToPublishing(productEntity);
            weakHandler.sendEmptyMessage(MSG_PUBLISH_START);
        }

        @Override
        public void onPublishing(final int index, final int progress) {
            weakHandler.removeMessages(MSG_PUBLISH_ABNORMALITY_ERROR);
            weakHandler.sendEmptyMessageDelayed(MSG_PUBLISH_ABNORMALITY_ERROR, 90000);
            Message message = Message.obtain();
            message.what = MSG_REFRESH_PROGRESS;
            message.arg1 = progress;
            weakHandler.sendMessage(message);
            KLog.i("======当前全部进度index：" + index + " ,progress:" + progress);
        }

        @Override
        public void onPublishOk(PublishResponseEntity entity) {
            successCount--;
            if (successCount <= 0) {
                weakHandler.removeMessages(MSG_PUBLISH_ABNORMALITY_ERROR);
                weakHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        RecordManager.get().setPublishingProductId(0);
                        if(GlobalParams.StaticVariable.sReleaseEnvironment==true){
                            RecordManager.get().clearAll();
                        }

                        KLog.i("=====从本地删除作品成功");
                        deleteProduct();
                        if (publishPresenter != null) {
                            publishPresenter.destroy();
                        }
                        publishPresenter = null;
                        Message message = Message.obtain();
                        message.what = MSG_PUBLISH_OK;
                        message.obj = entity;
                        weakHandler.sendMessage(message);
                    }
                }, 200);
            }
        }

        @Override
        public void onExportLocal(int code, PublishResponseEntity entity) {

        }

        @Override
        public void onPublishFail(final int type, final String message) {
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    onPublishError(type, message);
                }
            }, 500);
        }
    };

    private void onPublishError(int type, String message) {
        publishStatus = STATUS_PUBLISH_FAIL;
        RecordUtil.moveToDraft(productEntity);
        ToastUtil.showToast(getContext().getString(R.string.publish_falied));
        showStatus(STATUS_PUBLISH_FAIL);
        tvErrorHint.setText("上传失败,已存至草稿箱");
        weakHandler.removeMessages(MSG_PUBLISH_ABNORMALITY_ERROR);
        currentProgress = 0;
        RecordManager.get().setPublishingProductId(0);
        LogFileManager.getInstance().saveLogInfo("publish product", "type:" + type + "message" + message);
        KLog.i("====失败type：" + type + ",message:" + message);
    }

    private void deleteProduct() {
        Observable.just(1)
                .subscribeOn(Schedulers.computation())
                .map(new Function<Integer, Boolean>() {
                    @Override
                    public Boolean apply(@NonNull Integer integer) throws Exception {
                        RecordManager.get().setPublishingProductId(0);
                        RecordUtil.deleteProduct(productEntity, true);
                        return true;
                    }
                })
                .observeOn(Schedulers.io())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(@NonNull Boolean aBoolean) throws Exception {
                        EventHelper.post(GlobalParams.EventType.TYPE_PUBLISH_PRODUCT_OK);
                        productEntity = null;
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        KLog.i("=====从本地删除作品失败");
                        RecordUtil.deleteProduct(productEntity, true);
                        RecordManager.get().setPublishingProductId(0);
                        productEntity = null;
                    }
                });
    }

    public byte getPublishStatus() {
        return publishStatus;
    }

    public void show(boolean insertGallery) {
        startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.anim_bottom_pop_in));
        setVisibility(VISIBLE);
        postDelayed(new Runnable() {
            @Override
            public void run() {
                doPublish(insertGallery);
            }
        }, 300);
    }

    public void dismiss() {
        startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.anim_bottom_pop_out));
        if (publishPresenter != null) {
            publishPresenter.destroy();
        }
        postDelayed(new Runnable() {
            @Override
            public void run() {
                setVisibility(GONE);
            }
        }, 300);
    }


    public void setUploadMaskListener(UploadMaskListener uploadMaskListener) {
        this.uploadMaskListener = uploadMaskListener;
    }

    public interface UploadMaskListener {
        void onUploadOk(PublishResponseEntity entity);

        void onPublishExit(boolean isSuccess);

    }

}
