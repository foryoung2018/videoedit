package com.dongci.sun.gpuimglibrary.activity;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.GridView;

import com.dongci.sun.gpuimglibrary.camera.VideoInfo;

import java.util.ArrayList;
import java.util.List;


/**
 * create by ggq at 2018/5/31
 * 视频拼接
 */
public class VideoMergeActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private GridView grVideolist;
    private List<VideoInfo> videoInfos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_video_merge);
//        grVideolist = (GridView) findViewById(R.id.gr_videolist);

        Cursor cursor = getContentResolver()
                .query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                        null, null, null,
                        MediaStore.Video.Media.DEFAULT_SORT_ORDER);
        try {
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)); // id
                String displayName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE));
                String album = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.ALBUM)); // 专辑
                String artist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.ARTIST)); // 艺术家
                String title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)); // 显示名称
                String mimeType = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.MIME_TYPE));
                String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)); // 路径
                int duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)); // 时长
                long size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)); // 大小
                String resolution = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.RESOLUTION));

                VideoInfo info = new VideoInfo();
                info.title = title;
                info.path = path;
                info.duration = duration;
                info.size = size;
                info.resolution = resolution;
                videoInfos.add(info);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
        }
    }

    /**
     * 选择视频
     *
     * @param view
     */
    public void merge(View view) {


    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this,
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Video.Media.DATA, MediaStore.Video.Media.DEFAULT_SORT_ORDER},
                null,
                null,
                MediaStore.Video.Media.DEFAULT_SORT_ORDER);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(Loader loader) {

    }
}
