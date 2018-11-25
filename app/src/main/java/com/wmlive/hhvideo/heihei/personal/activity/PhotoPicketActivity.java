package com.wmlive.hhvideo.heihei.personal.activity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Build;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewStub;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import com.wmlive.hhvideo.DCApplication;
import com.wmlive.hhvideo.common.base.DcBaseActivity;
import com.wmlive.hhvideo.heihei.beans.personal.Photo;
import com.wmlive.hhvideo.heihei.beans.personal.PhotoFolder;
import com.wmlive.hhvideo.heihei.personal.adapter.FolderAdapter;
import com.wmlive.hhvideo.heihei.personal.adapter.PhotoAdapter;
import com.wmlive.hhvideo.heihei.personal.util.PhotoUtils;
import com.wmlive.hhvideo.utils.DeviceUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.BindView;
import cn.wmlive.hhvideo.R;

/**
 * Created by XueFei on 2017/6/6.
 * <p>
 * 选择相册照片
 */

public class PhotoPicketActivity extends DcBaseActivity implements PhotoAdapter.PhotoClickCallBack {
    public final static String KEY_RESULT = "picker_result";
    public final static int REQUEST_CAMERA = 1;

    /**
     * 是否显示相机
     */
    public final static String EXTRA_SHOW_CAMERA = "is_show_camera";
    /**
     * 照片选择模式
     */
    public final static String EXTRA_SELECT_MODE = "select_mode";
    /**
     * 最大选择数量
     */
    public final static String EXTRA_MAX_MUN = "max_num";
    /**
     * 单选
     */
    public final static int MODE_SINGLE = 0;
    /**
     * 多选
     */
    public final static int MODE_MULTI = 1;
    /**
     * 默认最大选择数量
     */
    public final static int DEFAULT_NUM = 9;

    private final static String ALL_PHOTO = "所有图片";
    /**
     * 是否显示相机，默认不显示
     */
    private boolean mIsShowCamera = false;
    /**
     * 照片选择模式，默认是单选模式
     */
    private int mSelectMode = MODE_SINGLE;
    /**
     * 最大选择数量，仅多选模式有用
     */
    private int mMaxNum;

    public static void startPhotoPicketActivity(Context context) {
        context.startActivity(new Intent(context, PhotoPicketActivity.class));
    }

    @BindView(R.id.photo_gridview)
    GridView mGridView;
    private Map<String, PhotoFolder> mFolderMap;//图片的全部数据
    private List<Photo> mPhotoLists = new ArrayList<>();
    private List<String> mSelectList = new ArrayList<>();
    private PhotoAdapter mPhotoAdapter;
    private ProgressDialog mProgressDialog;
    private ListView mFolderListView;

    @BindView(R.id.photo_num)
    TextView mPhotoNumTV;
    @BindView(R.id.floder_name)
    TextView mPhotoNameTV;
    private Button mCommitBtn;
    /**
     * 文件夹列表是否处于显示状态
     */
    boolean mIsFolderViewShow = false;
    /**
     * 文件夹列表是否被初始化，确保只被初始化一次
     */
    boolean mIsFolderViewInit = false;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_photo_picker;
    }


    @Override
    protected void initData() {
        super.initData();
        setTitle(R.string.photo_title, true);
        getPhotosTask.execute();

        findViewById(R.id.bottom_tab_bar).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //消费触摸事件，防止触摸底部tab栏也会选中图片
                return true;
            }
        });

        registerReceiver();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != manageActivityBroadCast) {
            unregisterReceiver(manageActivityBroadCast);
        }
    }

    @Override
    public void onPhotoClick() {
        List<String> list = mPhotoAdapter.getmSelectedPhotos();
        if (list != null && list.size() > 0) {
            mCommitBtn.setEnabled(true);
            mCommitBtn.setText(getResources().getString(R.string.commit_num, list.size(), mMaxNum));
        } else {
            mCommitBtn.setEnabled(false);
            mCommitBtn.setText(R.string.commit);
        }
    }

    @Override
    protected void onSingleClick(View v) {

    }

    /**
     * 弹出或者收起文件夹列表
     */
    private void toggle() {
        if (mIsFolderViewShow) {
            outAnimatorSet.start();
            mIsFolderViewShow = false;
        } else {
            inAnimatorSet.start();
            mIsFolderViewShow = true;
        }
    }

    /**
     * 初始化文件夹列表的显示隐藏动画
     */
    AnimatorSet inAnimatorSet = new AnimatorSet();
    AnimatorSet outAnimatorSet = new AnimatorSet();

    private void initAnimation(View dimLayout) {
        ObjectAnimator alphaInAnimator, alphaOutAnimator, transInAnimator, transOutAnimator;
        TypedValue tv = new TypedValue();
        int actionBarHeight = 0;
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
        }

        int height = DeviceUtils.getScreenWH(DCApplication.getDCApp())[1] - 3 * actionBarHeight;
        alphaInAnimator = ObjectAnimator.ofFloat(dimLayout, "alpha", 0f, 0.7f);
        alphaOutAnimator = ObjectAnimator.ofFloat(dimLayout, "alpha", 0.7f, 0f);
        transInAnimator = ObjectAnimator.ofFloat(mFolderListView, "translationY", height, 0);
        transOutAnimator = ObjectAnimator.ofFloat(mFolderListView, "translationY", 0, height);

        LinearInterpolator linearInterpolator = new LinearInterpolator();

        inAnimatorSet.play(transInAnimator).with(alphaInAnimator);
        inAnimatorSet.setDuration(300);
        inAnimatorSet.setInterpolator(linearInterpolator);
        outAnimatorSet.play(transOutAnimator).with(alphaOutAnimator);
        outAnimatorSet.setDuration(300);
        outAnimatorSet.setInterpolator(linearInterpolator);
    }

    /**
     * 获取存储卡中所有的图片
     */
    private void getPhotosSuccess() {
        mProgressDialog.dismiss();
        mPhotoLists.addAll(mFolderMap.get(ALL_PHOTO).getPhotoList());
        mPhotoNumTV.setText(getResources().getString(R.string.photos_num, mPhotoLists.size()));

        mPhotoAdapter = new PhotoAdapter(this, mPhotoLists);
        mPhotoAdapter.setIsShowCamera(mIsShowCamera);
        mPhotoAdapter.setSelectMode(mSelectMode);
        mPhotoAdapter.setMaxNum(mMaxNum);
        mPhotoAdapter.setPhotoClickCallBack(this);
        mGridView.setAdapter(mPhotoAdapter);
        Set<String> keys = mFolderMap.keySet();

        final List<PhotoFolder> folders = new ArrayList<>();
        for (String key : keys) {
            if (ALL_PHOTO.equals(key)) {
                PhotoFolder folder = mFolderMap.get(key);
                folder.setIsSelected(true);
                folders.add(0, folder);
            } else {
                folders.add(mFolderMap.get(key));
            }
        }
        //显示文件夹
        mPhotoNameTV.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                //显示或隐藏文件夹
                toggleFolderList(folders);
            }
        });

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectPhoto(mPhotoAdapter.getItem(position));
            }
        });
    }

    /**
     * 显示或者隐藏文件夹列表
     *
     * @param folders
     */
    private void toggleFolderList(final List<PhotoFolder> folders) {
        //初始化文件夹列表
        if (!mIsFolderViewInit) {
            ViewStub folderStub = (ViewStub) findViewById(R.id.floder_stub);
            folderStub.inflate();
            View dimLayout = findViewById(R.id.dim_layout);
            mFolderListView = (ListView) findViewById(R.id.listview_floder);
            final FolderAdapter adapter = new FolderAdapter(this, folders);
            mFolderListView.setAdapter(adapter);
            mFolderListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    for (PhotoFolder folder : folders) {
                        folder.setIsSelected(false);
                    }
                    PhotoFolder folder = folders.get(position);
                    folder.setIsSelected(true);
                    adapter.notifyDataSetChanged();

                    mPhotoLists.clear();
                    mPhotoLists.addAll(folder.getPhotoList());
                    if (ALL_PHOTO.equals(folder.getName())) {
                        mPhotoAdapter.setIsShowCamera(mIsShowCamera);
                    } else {
                        mPhotoAdapter.setIsShowCamera(false);
                    }
                    //这里重新设置adapter而不是直接notifyDataSetChanged，是让GridView返回顶部
                    mGridView.setAdapter(mPhotoAdapter);
                    mPhotoNumTV.setText(getResources().getString(R.string.photos_num, mPhotoLists.size()));
                    mPhotoNameTV.setText(folder.getName());
                    toggle();
                }
            });

            dimLayout.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (mIsFolderViewShow) {
                        toggle();
                        return true;
                    } else {
                        return false;
                    }
                }
            });
            initAnimation(dimLayout);
            mIsFolderViewInit = true;
        }
        toggle();
    }

    /**
     * 点击选择某张照片
     *
     * @param photo
     */
    private void selectPhoto(Photo photo) {
        if (photo == null) {
            return;
        }
        String path = photo.getPath();
        if (mSelectMode == MODE_SINGLE) {
            mSelectList.add(path);
            PhotoCutActivity.startPhotoCutActivity(this, path);
        }
    }

    /**
     * 获取照片的异步任务
     */
    private AsyncTask getPhotosTask = new AsyncTask() {
        @Override
        protected void onPreExecute() {
            mProgressDialog = ProgressDialog.show(PhotoPicketActivity.this, null, "loading...");
        }

        @Override
        protected Object doInBackground(Object[] params) {
            mFolderMap = PhotoUtils.getPhotos(PhotoPicketActivity.this);
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            getPhotosSuccess();
        }
    };

    public static Intent getCallingIntent(Context context) {
        Intent intent = new Intent(context, PhotoPicketActivity.class);
        return intent;
    }

    private void registerReceiver() {
        if (null == manageActivityBroadCast) {
            manageActivityBroadCast = new ManageActivityBroadCast();
        }
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(FINISH_ACTION);
        registerReceiver(manageActivityBroadCast, intentFilter);
    }

    public static final String FINISH_ACTION = "photo_pick_finish";
    private ManageActivityBroadCast manageActivityBroadCast;

    private class ManageActivityBroadCast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (null != intent) {
                String action = intent.getAction();
                if (action.equals(FINISH_ACTION)) {
                    finish();
                }
            }
        }
    }
}
