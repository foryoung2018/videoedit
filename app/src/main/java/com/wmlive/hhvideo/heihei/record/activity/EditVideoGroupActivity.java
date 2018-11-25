package com.wmlive.hhvideo.heihei.record.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wmlive.hhvideo.DCApplication;
import com.wmlive.hhvideo.common.GlobalParams;
import com.wmlive.hhvideo.common.base.DcBaseActivity;
import com.wmlive.hhvideo.heihei.beans.frame.FrameInfo;
import com.wmlive.hhvideo.heihei.beans.frame.FrameSortBean;
import com.wmlive.hhvideo.heihei.beans.frame.Frames;
import com.wmlive.hhvideo.heihei.beans.record.ShortVideoEntity;
import com.wmlive.hhvideo.heihei.db.ProductEntity;
import com.wmlive.hhvideo.heihei.record.adapter.FrameAdapter;
import com.wmlive.hhvideo.heihei.record.manager.RecordManager;
import com.wmlive.hhvideo.heihei.record.manager.RecordSetting;
import com.wmlive.hhvideo.heihei.record.utils.RecordFileUtil;
import com.wmlive.hhvideo.heihei.record.widget.CustomFrameView;
import com.wmlive.hhvideo.heihei.record.widget.SmallFrameView;
import com.wmlive.hhvideo.utils.JsonUtils;
import com.wmlive.hhvideo.utils.KLog;
import com.wmlive.hhvideo.utils.ParamUtis;
import com.wmlive.hhvideo.utils.ScreenUtil;
import com.wmlive.hhvideo.utils.ToastUtil;
import com.wmlive.hhvideo.utils.preferences.SPUtils;
import com.wmlive.hhvideo.widget.ZTablayout.ZTabLayout;
import com.wmlive.hhvideo.widget.dialog.MyDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.BindView;
import cn.wmlive.hhvideo.R;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import static com.wmlive.hhvideo.heihei.record.activity.EditProductionActivity.CURRENT_INDEX;
import static com.wmlive.hhvideo.heihei.record.activity.EditProductionActivity.CURRENT_TYPE;

public class EditVideoGroupActivity extends DcBaseActivity implements FrameAdapter.OnFrameItemClickListener {
    private CustomFrameView customFrameView;
    private LinearLayout llSortBottomMenu;
    private RecyclerView rvframes;

    private Context mContext = EditVideoGroupActivity.this;

    private FrameInfo mFrameInfo;
    private int currentIndex;//录制页面 传过来的当前预览的坐标
    private FrameAdapter frameAdapter;
    private ImageView ivBarLeft;
    private TextView tvBarCenter;
    private TextView tvBarRight;
    private String currentGroup = "推荐";//当前分辨率tab


    //记录当前大画框所在的组 和坐标
    private String currentFrameGroup;
    private int currentFramIndex = 0;

    private Map<String, FrameSortBean> framGroups = new HashMap<>();
    private Map<String, ShortVideoEntity> pathMapVideo = new HashMap<>();//截图与视频的对应集合
    private List<FrameSortBean> layoutList; // 所有比例画框的集合的集合
    private List<FrameInfo> frameList;
    private List<SmallFrameView> itemViewList;//每个画框view集合
    private List<Integer> itemIndexList;//记录下标的集合   画框的操作只记录下标操作
    private List<String> coverList;
    private String[] coverPaths;

    @BindView(R.id.ztab)
    ZTabLayout ztabs;
    @BindView(R.id.fr_delete)
    RelativeLayout fr_delete;
    @BindView(R.id.rl_coutainer)
    FrameLayout rl_coutainer;
    private List<ShortVideoEntity> shortVideoList;
    private ArrayList<String> tabNames = new ArrayList<>();//存放所有分比率分组的集合
    private ShortVideoEntity lastVideoEntity;
    private boolean isExigt = false;//当前所预览视频是否存在；
    private String name;
    private int selectFrameIndex;
    private static final int VIEWHEIGHT = 280;
    private int count;
    private MyDialog noticeDialog;

    //    private boolean isteamwork;//共同创作或者 替换进来
    private int type;
    private boolean allImport = true;
//    TYPE_TOGETHER

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    private void fbi() {
        customFrameView = (CustomFrameView) findViewById(R.id.customFrameView);
        llSortBottomMenu = (LinearLayout) findViewById(R.id.llSortBottomMenu);
        rvframes = (RecyclerView) findViewById(R.id.rvframes);

        ivBarLeft = (ImageView) findViewById(R.id.iv_bar_left);
        tvBarCenter = (TextView) findViewById(R.id.tv_bar_center);
        tvBarRight = (TextView) findViewById(R.id.tv_bar_right);

    }

    public static void startEditVideoGroupActivity(Activity context, int requestPageType, int currentIndex) {
        Intent intent = new Intent(context, EditVideoGroupActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt(CURRENT_INDEX, currentIndex);
        intent.putExtras(bundle);
        context.startActivityForResult(intent, PublishActivity.REQUEST_NEED_RELOAD);
    }

    public static void startEditVideoGroupActivity(Activity context, int requestPageType, int currentIndex, int type) {
        Intent intent = new Intent(context, EditVideoGroupActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt(CURRENT_INDEX, currentIndex);
        bundle.putInt(CURRENT_TYPE, type);
        intent.putExtras(bundle);
        context.startActivityForResult(intent, PublishActivity.REQUEST_NEED_RELOAD);
    }

    @Override
    protected void onSingleClick(View v) {
        if (v == ivBarLeft) {
            finish();
        } else if (v == tvBarRight) {
            List<ShortVideoEntity> newVideoList = new ArrayList<>();
            for (int i = 0; i < itemIndexList.size(); i++) {
                String coverPath = coverPaths[i];
                if (!TextUtils.isEmpty(coverPath)) {
                    ShortVideoEntity shortVideoEntity = pathMapVideo.get(coverPath);
                    newVideoList.add(shortVideoEntity);
                    if (shortVideoEntity == lastVideoEntity) {
                        currentIndex = i;
                        isExigt = true;
                    }
                } else {
                    newVideoList.add(new ShortVideoEntity());
                }
            }
            ProductEntity productEntity = RecordManager.get().getProductEntity();
            productEntity.frameInfo = mFrameInfo;
            productEntity.setShortVideos(newVideoList);
            RecordManager.get().updateProduct();

            if (!isExigt) {
                for (int i = 0; i < coverPaths.length; i++) {
                    String coverPath = coverPaths[i];
                    if (TextUtils.isEmpty(coverPath)) {
                        currentIndex = Math.min(i, itemIndexList.size() - 1);
                        break;
                    }
                }
            }
            KLog.d("index==" + currentIndex);
            Intent intent = new Intent();
            intent.putExtra(CURRENT_INDEX, currentIndex);
            setResult(RESULT_OK, intent);
            finish();

        }
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_edit_video_group;
    }

    @Override
    protected void initData() {
        super.initData();
        fbi();
        Intent intent = getIntent();
        mFrameInfo = RecordManager.get().getFrameInfo();
        if (mFrameInfo != null)
            name = mFrameInfo.name;
        currentIndex = intent.getIntExtra(CURRENT_INDEX, -1);
        type = intent.getIntExtra(CURRENT_TYPE, -1);
        initFrameItemView();
        if(mFrameInfo==null){
            ToastUtil.showToast("数据错误，请重试.");
            finish();
            return;
        }

        setBgm();
        frameAdapter = new FrameAdapter(new ArrayList<FrameInfo>());
        frameAdapter.setItemClickListener(this);
        rvframes.setLayoutManager(new GridLayoutManager(EditVideoGroupActivity.this, 4, GridLayoutManager.VERTICAL, false));
        rvframes.setAdapter(frameAdapter);
        getFrames();
        setListener();
        ViewGroup.LayoutParams layoutParams = rl_coutainer.getLayoutParams();
        layoutParams.height = ScreenUtil.getHeight(this) - ScreenUtil.dip2px(this, VIEWHEIGHT);
        layoutParams.width = -1;
        rl_coutainer.setLayoutParams(layoutParams);
    }

    private void setBgm() {
        String frameImagePath = RecordFileUtil.getFrameImagePath(mFrameInfo.sep_image);
        Bitmap bitmap = BitmapFactory.decodeFile(frameImagePath);
        KLog.d("ggqBACK", "frameImagePath==" + frameImagePath);
        customFrameView.setBackground(new BitmapDrawable(bitmap));
    }

    private void setListener() {
        ivBarLeft.setOnClickListener(this);
        tvBarRight.setOnClickListener(this);
        customFrameView.setEventListener(new CustomFrameView.EventListener() {
            @Override
            public void onChildClick(int index, int x, int y, int width, int height) {

            }

            @Override
            public void onChangePosition(int selectIndex, int targetIndex) {
                KLog.d("coverPaths===" + Arrays.toString(coverPaths));
                if (itemIndexList != null) {
                    // 存储临时交换位置
                    Collections.swap(itemIndexList, targetIndex, selectIndex);
                }

                SmallFrameView targetView = itemViewList.get(targetIndex);
                SmallFrameView selectView = itemViewList.get(selectIndex);
                if (coverPaths != null) {
                    // 交换封面图
                    swapCoverPathArray(targetIndex, selectIndex);
                    targetView.setCoverImage(coverPaths[targetIndex]);
                    selectView.setCoverImage(coverPaths[selectIndex]);
                }
            }

            @Override
            public void showDeletField(boolean show, int index) {
                //提示删除
                String coverPath = itemViewList.get(index).getCoverPath();
                if (show && !TextUtils.isEmpty(coverPath)) {//没有封面的item不显示删除提示
                    fr_delete.setVisibility(View.VISIBLE);
                } else {
                    fr_delete.setVisibility(View.GONE);
                }
            }

            @Override
            public void deleteFramItem(int i) {
                String coverPath = itemViewList.get(i).getCoverPath();//没有封面的item不删除
                if (!TextUtils.isEmpty(coverPath))
                    deleteFrame(i, coverPath);

            }


        });
        ztabs.setOnTabSelectedListener(new ZTabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int position) {
                String s = tabNames.get(position);
                if (currentGroup.equals(s)) {
                    return;
                }
                currentGroup = s;
                frameList = framGroups.get(s).layout;
                frameAdapter.addData(frameList);
                if (currentGroup.equals(currentFrameGroup)) {
                    frameAdapter.setInitPosition(currentFramIndex, type == 30 && allImport ? coverList.size() + 1 : coverList.size());
                } else {
                    frameAdapter.setInitPosition(-1, type == 30 && allImport ? coverList.size() + 1 : coverList.size());
                }


            }

            @Override
            public void onTabUnselected(int position) {

            }

            @Override
            public void onTabReselected(int position) {

            }
        });
    }

    /**
     * 初始化当前作品画框信息
     */
    private void initFrameItemView() {
        itemViewList = new ArrayList<>();
        itemIndexList = new ArrayList();
        if (mFrameInfo != null && mFrameInfo.getLayout() != null) {
            int size = mFrameInfo.getLayout().size();
            ParamUtis.setLayoutParam(this, customFrameView, mFrameInfo.canvas_height, VIEWHEIGHT);
            SmallFrameView itemView;
            for (int i = 0; i < size; i++) {
                itemView = new SmallFrameView(this);
                itemView.setViewType(SmallFrameView.VIEW_TYPE_SWAP_POSITION);
                itemView.setLayoutInfo(mFrameInfo.getLayout().get(i), true);
                itemView.setTag(i);
                itemViewList.add(itemView);
                itemIndexList.add(i);
            }
            customFrameView.setFrameView(mFrameInfo, itemViewList, true);
            coverPaths = new String[size];
            coverList = new ArrayList<>();

            //获取当前作品信息
            final ProductEntity productEntity = RecordManager.get().getProductEntity();
            shortVideoList = productEntity.shortVideoList;
            Log.d("dddddddd", "initFrameItemView: shortVideoList==" + shortVideoList);
            for (int i = 0; i < shortVideoList.size(); i++) {
                if (!TextUtils.isEmpty(shortVideoList.get(i).importVideoPath) || !TextUtils.isEmpty(shortVideoList.get(i).editingVideoPath)) {
                    count++;
                    if (!shortVideoList.get(i).isImport()) {//判断是否全部是导入视频
                        allImport = false;
                    }
                    Log.d("dddddddd", "initFrameItemView: isImport" + shortVideoList.get(i).isImport());
                }
            }
            if (count == 0) {
                allImport = false;
            }
            //生成当前作品信息的封面集合
            if (productEntity != null && productEntity.hasVideo()) {
                Observable.range(0, size)
                        .subscribeOn(Schedulers.io())
                        .map(new Function<Integer, Integer>() {
                            @Override
                            public Integer apply(@NonNull Integer index) throws Exception {
                                ShortVideoEntity shortVideoEntity = productEntity.shortVideoList.get(index);
                                KLog.i("coverPaths[result]  index===" + index + "  size==" + size);
                                if (index == size) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            frameAdapter.setInitPosition(selectFrameIndex, type == 30 && allImport ? coverList.size() + 1 : coverList.size());
                                            rvframes.smoothScrollToPosition(selectFrameIndex);
                                            currentFramIndex = selectFrameIndex;
                                        }
                                    });
                                }
                                KLog.d("shortVideoEntity.coverUrl===" + shortVideoEntity.coverUrl);
                                KLog.d("shortVideoEntity.editingVideoPath===" + shortVideoEntity.editingVideoPath);
                                if (shortVideoEntity == null || TextUtils.isEmpty(shortVideoEntity.editingVideoPath)) {
                                    coverPaths[index] = "";
                                    return index;
                                }
                                if (!TextUtils.isEmpty(shortVideoEntity.coverUrl)) {
                                    coverPaths[index] = shortVideoEntity.coverUrl;
                                    pathMapVideo.put(coverPaths[index], shortVideoEntity);
                                    coverList.add(coverPaths[index]);
                                    if (index == currentIndex) {
                                        lastVideoEntity = shortVideoEntity;
                                    }
//                                    itemViewList.get(index).setCoverImage(coverPaths[index]);
                                    return index;
                                }
                                coverPaths[index] = shortVideoEntity.baseDir + File.separator + "video_cover" + index + "_" + System.currentTimeMillis() + ".jpg";
                                pathMapVideo.put(coverPaths[index], shortVideoEntity);

                                KLog.i("====开始生成视频封面:" + coverPaths[index]);
                                boolean result = RecordFileUtil.getVideoCover(shortVideoEntity.editingVideoPath,
                                        coverPaths[index],
                                        GlobalParams.Config.VIDEO_COVER_CLIP_SECOND,
                                        RecordSetting.VIDEO_WIDTH,
                                        RecordSetting.VIDEO_HEIGHT
                                );
                                if (index == currentIndex) {
                                    lastVideoEntity = shortVideoEntity;
                                }
                                coverList.add(coverPaths[index]);
                                return index;
                            }
                        })
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<Integer>() {
                            @Override
                            public void accept(@NonNull Integer result) throws Exception {
                                KLog.i("====生成视频封面" + (result >= 0 ? "成功" : "失败") + "result==" + result);
                                itemViewList.get(result).setCoverImage(coverPaths[result]);
                                frameAdapter.setInitPosition(selectFrameIndex, type == 30 && allImport ? coverList.size() + 1 : coverList.size());

                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(@NonNull Throwable throwable) throws Exception {
                                KLog.i("====生成视频封面出错");
                                throwable.printStackTrace();
                            }
                        });
            } else {
                KLog.i("====作品不存在");
            }

        }
    }

    private void deleteFrame(int index, String coverPath) {
//        itemIndexList.set(index, -1);//标记要移除的素材的下标

        //移除存储集合中的封面图
        if (coverList.size() > 0) {
            coverList.remove(coverPath);
        }
        frameAdapter.setInitCount(type == 30 && allImport ? coverList.size() + 1 : coverList.size());
        itemViewList.get(index).setCoverImage("");
        //置空用于交换集合的封面
        for (int i = 0; i < coverPaths.length; i++) {
            if (coverPath.equalsIgnoreCase(coverPaths[i])) {
                coverPaths[i] = "";
                break;
            }
        }

    }

    public FrameInfo getFrameInfo(String frameLayout) {
        for (FrameSortBean bean : layoutList) {
            List<FrameInfo> layout = bean.layout;
            for (int i = 0; i < layout.size(); i++) {
                FrameInfo frame = layout.get(i);
                if (frame.name.equalsIgnoreCase(frameLayout)) {
                    if (frame.video_count == 1) {
                        currentGroup = tabNames.get(0);
                        selectFrameIndex = 0;
                        return layoutList.get(0).layout.get(0);
                    } else {
                        currentGroup = bean.name;
                        selectFrameIndex = i;
                        return frame;
                    }
                }
            }
        }
        return layoutList.get(0).layout.get(0);
    }

    private void swapCoverPathArray(int targetIndex, int selectIndex) {
        if (coverPaths != null) {
            String temp = coverPaths[targetIndex];
            coverPaths[targetIndex] = coverPaths[selectIndex];
            coverPaths[selectIndex] = temp;
        }
    }

    /**
     * 获取所有画框数据
     */
    private void getFrames() {
        Observable.just(1)
                .subscribeOn(Schedulers.io())
                .map(new Function<Integer, String>() {
                    @Override
                    public String apply(Integer integer) throws Exception {
                        return SPUtils.getString(DCApplication.getDCApp(), SPUtils.FRAME_LAYOUT_DATA, "");
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String frames) throws Exception {
                        Frames f = JsonUtils.parseObject(frames, Frames.class);
                        if (f != null) {
                            layoutList = f.layouts;
                            for (FrameSortBean sortBean : layoutList) {
                                framGroups.put(sortBean.name, sortBean);
                                tabNames.add(sortBean.name);
                                KLog.d("画框--->" + tabNames);
                            }
                            KLog.d("画框类别集合名称" + tabNames);
                            ztabs.setupWithoutViewPager(tabNames.toArray(new String[tabNames.size()]), 0);
                            getFrameInfo(name);//找到当前画框的位置
                            ztabs.selectTab(currentGroup);
                            frameList = framGroups.get(currentGroup).layout;
                            frameAdapter.addData(frameList);
                            rvframes.smoothScrollToPosition(selectFrameIndex);
                            frameAdapter.setInitPosition(selectFrameIndex, type == 30 && allImport ? count + 1 : count);
                            currentFrameGroup = currentGroup;
                            currentFramIndex = selectFrameIndex;
                            KLog.i("使用网络画框数据");
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        throwable.printStackTrace();
//                        getLocalFrameLayouts();
                    }
                });
    }

    /**
     * 只更改画框结构，不对视频做操作
     *
     * @param frameInfo
     * @param enable
     * @param position
     */
    @Override
    public void onFrameItemClick(FrameInfo frameInfo, boolean enable, int position) {
        if (enable) {
            mFrameInfo = frameInfo;
            currentFramIndex = position;
            currentFrameGroup = currentGroup;
            setBgm();
            reDrawFrame();
        }
    }


    /**
     * 重新设置画框
     */
    private void reDrawFrame() {
        itemViewList.clear();
        itemIndexList.clear();
        coverPaths = new String[20];
        if (mFrameInfo != null && mFrameInfo.getLayout() != null) {
            ParamUtis.setLayoutParam(this, customFrameView, mFrameInfo.canvas_height, VIEWHEIGHT);
            int size = mFrameInfo.getLayout().size();
            SmallFrameView itemView;
            for (int i = 0; i < size; i++) {
                itemView = new SmallFrameView(this);
                itemView.setViewType(SmallFrameView.VIEW_TYPE_SWAP_POSITION);
                itemView.setLayoutInfo(mFrameInfo.getLayout().get(i), true);
                itemView.setTag(i);
                itemViewList.add(itemView);
                itemIndexList.add(i);
                if (coverList.size() != 0 && i < coverList.size()) {
                    itemView.setCoverImage(coverList.get(i));
                    coverPaths[i] = coverList.get(i);
                }
            }
            customFrameView.setFrameView(mFrameInfo, itemViewList, true);
        }
    }


}
