package com.wmlive.hhvideo.heihei.personal.activity;

import android.content.DialogInterface;
import android.support.v7.widget.GridLayoutManager;
import android.view.View;

import com.wmlive.hhvideo.DCApplication;
import com.wmlive.hhvideo.common.GlobalParams;
import com.wmlive.hhvideo.common.base.BaseCompatActivity;
import com.wmlive.hhvideo.common.base.DcBaseActivity;
import com.wmlive.hhvideo.heihei.beans.frame.FrameInfo;
import com.wmlive.hhvideo.heihei.beans.frame.FrameSortBean;
import com.wmlive.hhvideo.heihei.beans.frame.Frames;
import com.wmlive.hhvideo.heihei.db.ProductEntity;
import com.wmlive.hhvideo.heihei.personal.adapter.DraftBoxAdapter;
import com.wmlive.hhvideo.heihei.personal.fragment.UserHomeFragment;
import com.wmlive.hhvideo.heihei.personal.util.SpaceItemDecoration;
import com.wmlive.hhvideo.heihei.record.activity.LocalPublishActivity;
import com.wmlive.hhvideo.heihei.record.activity.PublishActivity;
import com.wmlive.hhvideo.heihei.record.activity.RecordActivitySdk;
import com.wmlive.hhvideo.heihei.record.activity.RecordMvActivity;
import com.wmlive.hhvideo.heihei.record.activitypresenter.RecordMvActivityHelper;
import com.wmlive.hhvideo.heihei.record.manager.RecordManager;
import com.wmlive.hhvideo.heihei.record.utils.RecordUtil;
import com.wmlive.hhvideo.utils.CommonUtils;
import com.wmlive.hhvideo.utils.JsonUtils;
import com.wmlive.hhvideo.utils.KLog;
import com.wmlive.hhvideo.utils.ToastUtil;
import com.wmlive.hhvideo.utils.preferences.SPUtils;
import com.wmlive.hhvideo.widget.dialog.CustomDialog;
import com.wmlive.hhvideo.widget.refreshrecycler.OnRecyclerItemClickListener;
import com.wmlive.hhvideo.widget.refreshrecycler.RefreshRecyclerView;
import com.wmlive.networklib.entity.EventEntity;
import com.wmlive.networklib.util.EventHelper;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.wmlive.hhvideo.R;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by XueFei on 2017/6/3.
 * <p>
 * 草稿箱
 * modify by lsq
 */

public class DraftBoxActivity extends DcBaseActivity implements DraftBoxAdapter.OnClickFansCustom, OnRecyclerItemClickListener {
    @BindView(R.id.rv_list)
    RefreshRecyclerView rvList;
    private DraftBoxAdapter draftBoxAdapter;
    private Disposable disposable;
    private CustomDialog customDialog;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_draftbox;
    }

    @Override
    protected void initData() {
        super.initData();
        EventHelper.register(this);
        setTitle(R.string.user_draftbox_title, true);
        rvList.setLayoutManager(new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false));
        draftBoxAdapter = new DraftBoxAdapter(new ArrayList<ProductEntity>(), rvList);
        rvList.addItemDecoration(new SpaceItemDecoration(this, UserHomeFragment.SPACE_ITEM_DECRRATION, false));
        rvList.setAdapter(draftBoxAdapter);
        rvList.setRefreshing(false);
        rvList.setLoadMoreEnable(false);
        draftBoxAdapter.setOnRecyclerItemClickListener(this);
        draftBoxAdapter.setOnClickCustom(this);
    }

    @Override
    protected void onSingleClick(View v) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        queryProduct();
    }

    @Override
    public void onClick(View view, final int position, final ProductEntity productEntity) {
        if (productEntity.getId() != RecordManager.get().getPublishingProductId()) {
            customDialog = new CustomDialog(this, R.style.BaseDialogTheme);
            customDialog.setContent(R.string.dialog_draftbox_tip);
//            customDialog.setCancelable(false);
            customDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    RecordUtil.deleteProduct(productEntity, true);
                    KLog.i("position-->"+position+"size--->"+draftBoxAdapter.getDataContainer().size());
                    if (position < draftBoxAdapter.getDataContainer().size()) {
                        draftBoxAdapter.getDataContainer().remove(position);
                        draftBoxAdapter.notifyItemRemoved(position);
                        draftBoxAdapter.notifyDataSetChanged();
                        rvList.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (draftBoxAdapter.getDataContainer().size() == 0) {
                                    DraftBoxActivity.this.finish();
                                }
                            }
                        }, 200);
                    } else {
                        DraftBoxActivity.this.showToast(R.string.hintErrorDataDelayTry);
                    }
                    customDialog.dismiss();
                }
            });
            customDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    customDialog.dismiss();
                }
            });
            customDialog.show();
        } else {
            showToast("此作品正在发布中，不能删除");
        }
    }

    @Override
    public void onRecyclerItemClick(int dataPosition, View view, Object data) {
        KLog.i("click-->" + data + "data");
        if (data != null && data instanceof ProductEntity) {
            ProductEntity productEntity = (ProductEntity) data;
            if (RecordMvActivityHelper.TYPE_RECORD_MV == productEntity.getExtendInfo().productCreateType) {
                if (RecordManager.get().getPublishingProductId() != productEntity.getId()) {
                    String template = productEntity.extendInfo.template_name;
                    if (RecordMvActivityHelper.isNoInvalidTemplate(this, template)) {
                        ToastUtil.showToast(getResources().getString(R.string.temp_no_can_use));
                    } else {
                        RecordManager.get().setProductEntity(productEntity);
                        RecordMvActivity.startRecordMv(this, RecordMvActivityHelper.EXTRA_RECORD_TYPE_DRAFT, 0);
                    }
                } else {
                    showToast("此作品正在发布中，不能进行编辑");
                }
            } else {
                KLog.i("query---click>" + productEntity.getId());
                FrameInfo frameInfo = productEntity.frameInfo;
                if (frameInfo != null) {
                    int id = frameInfo.id;
                    String frames = SPUtils.getString(DCApplication.getDCApp(), SPUtils.FRAME_LAYOUT_DATA, "");
                    Frames f = JsonUtils.parseObject(frames, Frames.class);
                    List<FrameSortBean> layouts = f.layouts;
                    for (FrameSortBean bean : layouts) {
                        List<FrameInfo> layout = bean.layout;
                        for (FrameInfo info : layout) {
                            if (info.id == id) {
                                productEntity.frameInfo = info;
                            }
                        }
                    }
                }
                KLog.i("=======需要编辑的作品：" + productEntity.toString());
                if (RecordManager.get().getPublishingProductId() != productEntity.getId()) {
                    RecordManager.get().setProductEntity(productEntity);
                    if (productEntity.isLocalUploadVideo()) {
                        LocalPublishActivity.startLocalPublishActivity(this, LocalPublishActivity.FORM_DRAFT);
                    } else {
//                    PublishActivity.startPublishActivity(this, false);
                        RecordActivitySdk.startRecordActivity(this, RecordActivitySdk.TYPE_DRAFT);
                    }
                } else {
                    showToast("此作品正在发布中，不能进行编辑");
                }
            }
        } else {
            showToast(R.string.hintErrorDataDelayTry);
        }
    }

    @Override
    protected void onDestroy() {
        EventHelper.unregister(this);
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
        disposable = null;
        super.onDestroy();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPublishOkEvent(EventEntity eventEntity) {
        if (eventEntity != null && eventEntity.code == GlobalParams.EventType.TYPE_PUBLISH_PRODUCT_OK) {
            queryProduct();
            KLog.i("=====作品发布成功，更新数据");
        }
    }

    private void queryProduct() {
        disposable = Observable.just(1)
                .subscribeOn(Schedulers.computation())
                .map(new Function<Integer, List<ProductEntity>>() {
                    @Override
                    public List<ProductEntity> apply(@NonNull Integer integer) throws Exception {
                        KLog.i("====开始查询数据");
                        return RecordUtil.queryAllDraft();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<ProductEntity>>() {
                    @Override
                    public void accept(@NonNull List<ProductEntity> productEntities) throws Exception {
                        KLog.i("====结束草稿查询:" + CommonUtils.printList(productEntities));
                        if (productEntities.size() > 0) {
                            if (draftBoxAdapter != null) {
                                draftBoxAdapter.addData(true, productEntities);
                            }
                            for (ProductEntity p : productEntities) {
                                KLog.i("query--->" + p.getId() + p.frameInfo);
                            }
                        } else {
                            finish();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                        KLog.i("====查询数据出错:" + throwable.getMessage());
                    }
                });
    }
}
