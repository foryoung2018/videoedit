package com.wmlive.hhvideo.heihei.discovery;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.format.Time;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.widget.TextView;

import com.wmlive.hhvideo.heihei.beans.discovery.LocalVideoBean;
import com.wmlive.hhvideo.utils.KLog;
import com.wmlive.hhvideo.utils.PermissionUtils;
import com.wmlive.hhvideo.utils.SdkUtils;
import com.wmlive.hhvideo.utils.observer.DcObserver;

import java.io.File;
import java.io.FileFilter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by lsq on 6/6/2017.
 */

public class DiscoveryUtil {


    /**
     * 转换时间
     *
     * @param time
     * @return
     */
    public static String convertTime(int time) {
        KLog.i("========转换时间：" + time);
        if (time < 0) {
            return "00:00";
        }
        int minute = time / 60;
        int second = time % 60;
        return (minute < 10 ? ("0" + minute) : String.valueOf(minute))
                + ":" +
                (second < 10 ? ("0" + second) : String.valueOf(second));
    }

    public static String convertTimeN(int time) {
        KLog.i("========转换时间：" + time);
        if (time < 0) {
            return "0s";
        }
        return time + "s";
    }


    /**
     * 给TextView或者Button设置图片
     *
     * @param view
     * @param drawableId
     * @param direction  0:左 1:上 2:右 3:下
     */
    public static <T extends TextView> void setDrawable(T view, int drawableId, int direction, int width, int height) {
        if (drawableId > 0) {
            Drawable drawable;
            drawable = view.getResources().getDrawable(drawableId);
            drawable.setBounds(0, 0, width, height);
            switch (direction) {
                case 0:
                    view.setCompoundDrawables(drawable, null, null, null);
                    break;
                case 1:
                    view.setCompoundDrawables(null, drawable, null, null);
                    break;
                case 2:
                    view.setCompoundDrawables(null, null, drawable, null);
                    break;
                case 3:
                    view.setCompoundDrawables(null, null, null, drawable);
                    break;
                default:
                    break;
            }
        } else {
            view.setCompoundDrawables(null, null, null, null);
        }

    }

    public static void changeTextColor(TextView view, String string, int start, int end, int colorId) {
        SpannableStringBuilder builder = new SpannableStringBuilder(string);
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(colorId);
        builder.setSpan(colorSpan, start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        view.setText(builder);
    }

    /**
     * 更新媒体文件
     *
     * @param context
     * @param path
     */
    public static void updateMedia(final Context context, final String path) {
        if (!TextUtils.isEmpty(path)) {
            Observable.just(1)
                    .subscribeOn(Schedulers.io())
                    .map(new Function<Integer, Integer>() {
                        @Override
                        public Integer apply(Integer integer) throws Exception {
                            if (SdkUtils.isKITKAT()) {
                                MediaScannerConnection.scanFile(context, new String[]{path}, null,
                                        new MediaScannerConnection.OnScanCompletedListener() {
                                            public void onScanCompleted(String path, Uri uri) {
                                                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                                                mediaScanIntent.setData(uri);
                                                context.sendBroadcast(mediaScanIntent);
                                            }
                                        });
                            } else {
                                context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.fromFile(new File(path))));
                            }
                            return 1;
                        }
                    })
                    .subscribe(new DcObserver<>());
        }
    }


    /**
     * 通知更新本地视频文件
     *
     * @param context
     * @param listener
     */
    public static void updateLocalVideo(final Context context, final OnScanCompleteListener listener) {
        if (SdkUtils.isKITKAT()) {
            if (SdkUtils.isMarshmallow() && !PermissionUtils.hasSDCardRWPermission(context)) {
                return;
            }
            getAllVideos(context)
                    .map(new Function<List<LocalVideoBean>, String[]>() {
                        @Override
                        public String[] apply(List<LocalVideoBean> localVideoBeen) throws Exception {
                            List<String> folderPath = new ArrayList<String>();
                            int i = 0;
                            for (LocalVideoBean video : localVideoBeen) {
                                folderPath.add(i, video.path);
                                i++;
                            }
                            KLog.i("发现本地视频的数量：" + i);
                            return folderPath.toArray(new String[i]);
                        }
                    })
                    .subscribe(new Consumer<String[]>() {
                        @Override
                        public void accept(String[] strings) throws Exception {
                            MediaScannerConnection.scanFile(context.getApplicationContext(), strings, null,
                                    new MediaScannerConnection.OnScanCompletedListener() {
                                        @Override
                                        public void onScanCompleted(String path, Uri uri) {
                                            KLog.i("find video path:" + path + "_uri:" + uri);
                                            if (listener != null) {
                                                if (uri == null) {//扫描结束了
                                                    listener.onScanComplete(path, uri);
                                                }
                                            }
                                        }
                                    });
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            KLog.i("=======updateLocalVideo exception：" + throwable.getMessage());
                        }
                    });

        } else {
            updateLocalMedia4_4(context);
        }
    }

    public interface OnScanCompleteListener {
        void onScanComplete(String path, Uri uri);
    }

    /**
     * 4.4以下通知媒体更新
     *
     * @param context
     */
    private static void updateLocalMedia4_4(Context context) {
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://"
                + Environment.getExternalStorageDirectory())));
    }

    /**
     * 获取本地相册视频
     *
     * @param context
     * @return
     */
    public static Observable<List<LocalVideoBean>> getFirstBatchAblumsVideos(final Context context) {
        Observable<List<LocalVideoBean>> observable = Observable.create(new ObservableOnSubscribe<List<LocalVideoBean>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<List<LocalVideoBean>> e) throws Exception {
                long start = System.currentTimeMillis();
                KLog.i("====开始查询：" + start);
                Cursor cursor = queryVideo(context, null, null, "date_modified DESC");
                KLog.i("====结束查询,耗时：" + (System.currentTimeMillis() - start));
                start = System.currentTimeMillis();
                KLog.i("====开始组装：" + start);
                LocalVideoBean bean;
                String path;
                List<LocalVideoBean> arrayList = new ArrayList<>();
                if ((cursor != null) && (cursor.moveToFirst()))
                    do {
                        path = cursor.getString(8);
                        if (!TextUtils.isEmpty(path) && new File(path).exists()) {
//                            String ringDuring = getRingDuring(path);
                            int duration = cursor.getInt(6);
                            if (duration > 5000) {

                                KLog.i("====开始生成对象：" + start);
                                bean = new LocalVideoBean(
                                        cursor.getLong(0),
                                        cursor.getString(1),
                                        cursor.getString(2),
                                        cursor.getString(3),
                                        cursor.getString(4),
                                        cursor.getString(5),
                                        cursor.getInt(6),
                                        cursor.getInt(7),
                                        path,
                                        cursor.getInt(8));

                                arrayList.add(bean);
                                KLog.i("====结束生成对象：" + start);
                            }
                        }
                    } while (cursor.moveToNext());
                e.onNext(arrayList);
                e.onComplete();
                if (cursor != null) {
                    cursor.close();
                }
                KLog.i("====结束组装：" + start);
            }
        });

        return observable;
    }


    public static Observable<LocalVideoBean> getSecondBatchAblumsVideos(final Context context) {
        Observable<LocalVideoBean> observable = Observable.create(new ObservableOnSubscribe<LocalVideoBean>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<LocalVideoBean> e) throws Exception {
                List<LocalVideoBean> arrayList = new ArrayList<>();
                getVideoFile(arrayList, Environment.getExternalStorageDirectory(), e);
                e.onComplete();
            }
        });
        return observable;
    }

    /**
     * 获取本地相册视频
     *
     * @param context
     * @return
     */
//    public static List<LocalVideoBean> getAllBatchAblumsVideos(final Context context) {
//        return getVideoBeanFromDB(context,limetAll);
//    }
    private static List<LocalVideoBean> getVideoBeanFromDB(Context context, String limit) {
        long start = System.currentTimeMillis();
        KLog.i("====开始查询：" + start);
        Cursor cursor = queryVideo(context, null, null, limit);
        KLog.i("====结束查询,耗时：" + (System.currentTimeMillis() - start));
        start = System.currentTimeMillis();
        KLog.i("====开始组装：" + start);
        LocalVideoBean bean;
        String path;
        List<LocalVideoBean> arrayList = new ArrayList<>();
        if ((cursor != null) && (cursor.moveToFirst()))
            do {
                path = cursor.getString(8);
                if (!TextUtils.isEmpty(path) && new File(path).exists()) {
                    int duration = cursor.getInt(6);
                    if (duration > 5000) {

                        bean = new LocalVideoBean(
                                cursor.getLong(0),
                                cursor.getString(1),
                                cursor.getString(2),
                                cursor.getString(3),
                                cursor.getString(4),
                                cursor.getString(5),
                                cursor.getInt(6),
                                cursor.getInt(7),
                                path,
                                cursor.getInt(8));
                        arrayList.add(bean);
                    }
                }
            } while (cursor.moveToNext());
        if (cursor != null) {
            cursor.close();
        }
        KLog.i("====结束组装：" + start);
        return arrayList;
    }

    /**
     * 获取本地视频
     *
     * @param context
     * @return
     */
    public static Observable<List<LocalVideoBean>> getAllVideos(final Context context) {
        return Observable.just(1)
                .delay(1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .map(new Function<Integer, List<LocalVideoBean>>() {
                    @Override
                    public List<LocalVideoBean> apply(Integer integer) throws Exception {
                        List<LocalVideoBean> arrayList = new ArrayList<>();
                        getVideoFile(arrayList, Environment.getExternalStorageDirectory(), null);
                        return arrayList;
                    }
                });
    }

    public static void getVideoFile(final List<LocalVideoBean> list, File file, ObservableEmitter<LocalVideoBean> e) {// 获得视频文件
        file.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                // sdCard找到视频名称
                String name = file.getName();
                int i = file.getName().lastIndexOf(".");
                if (i != -1) {
                    name = name.substring(i);//获取文件后缀名
                    if (name.equalsIgnoreCase(".mp4")  //忽略大小写
                           /* || name.equalsIgnoreCase(".3gp")
                            || name.equalsIgnoreCase(".wmv")
                            || name.equalsIgnoreCase(".ts")
                            || name.equalsIgnoreCase(".rmvb")
                            || name.equalsIgnoreCase(".mov")
                            || name.equalsIgnoreCase(".m4v")
                            || name.equalsIgnoreCase(".avi")
                            || name.equalsIgnoreCase(".m3u8")
                            || name.equalsIgnoreCase(".3gpp")
                            || name.equalsIgnoreCase(".3gpp2")
                            || name.equalsIgnoreCase(".mkv")
                            || name.equalsIgnoreCase(".flv")
                            || name.equalsIgnoreCase(".divx")
                            || name.equalsIgnoreCase(".f4v")
                            || name.equalsIgnoreCase(".rm")
                            || name.equalsIgnoreCase(".asf")
                            || name.equalsIgnoreCase(".ram")
                            || name.equalsIgnoreCase(".mpg")
                            || name.equalsIgnoreCase(".v8")
                            || name.equalsIgnoreCase(".swf")
                            || name.equalsIgnoreCase(".m2v")
                            || name.equalsIgnoreCase(".asx")
                            || name.equalsIgnoreCase(".ra")
                            || name.equalsIgnoreCase(".ndivx")
                            || name.equalsIgnoreCase(".xvid")*/) {
                        LocalVideoBean vi = new LocalVideoBean();
                        vi.name = file.getName();//文件名
                        vi.path = file.getAbsolutePath();//文件路径
                        String ringDuring = getRingDuring(vi.path)[0];
                        if (ringDuring != null) {
                            vi.duration = Long.parseLong(ringDuring);
                        }

                        if (vi.duration > 5000) {
                            try {
                                vi.date = Long.parseLong(getRingDuring(vi.path)[1]);
                                list.add(vi);
                                if (e != null)
                                    e.onNext(vi);
                            } catch (NumberFormatException e) {
                                vi.date = -1;
                            }
                        }
                        KLog.i("TAG1111111111111111111", "文件名:" + vi.name + ",路径:" + vi.path + "  时长==" + ringDuring);
                        return true;
                    }
                } else if (file.isDirectory()) {
                    getVideoFile(list, file, e);
                }
                return false;
            }
        });
    }


    /**
     * 查询Video
     *
     * @param context
     * @param selection
     * @param paramArrayOfString
     * @param sortOrder
     * @return
     */
    public static Cursor queryVideo(Context context, String selection, String[] paramArrayOfString, String sortOrder) {
        return context.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                new String[]{
                        MediaStore.Video.Media._ID,
                        MediaStore.Video.Media.TITLE,
                        MediaStore.Video.Media.ALBUM,
                        MediaStore.Video.Media.ARTIST,
                        MediaStore.Video.Media.DISPLAY_NAME,
                        MediaStore.Video.Media.MIME_TYPE,
                        MediaStore.Video.Media.DURATION,
                        MediaStore.Video.Media.SIZE,
                        MediaStore.Video.Media.DATA,
                        MediaStore.Video.Media.DATE_MODIFIED
                },
                selection, paramArrayOfString, sortOrder);

    }

    public static Cursor queryThumbnails(Context context) {
        return context.getContentResolver().query(MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI,
                new String[]{
                        MediaStore.Files.FileColumns._ID,
                        MediaStore.MediaColumns.DATA,
                        MediaStore.MediaColumns.WIDTH,
                        MediaStore.MediaColumns.HEIGHT,
                },null,null,null);

    }

    public static String[] thumbColumns = { MediaStore.Video.Thumbnails.DATA };
    public static String[] mediaColumns = { MediaStore.Video.Media._ID };

    public static String getThumbnailPathForLocalFile(Context context,
                                                      long fileId) {

//        long fileId = getFileId(context, fileUri);

        MediaStore.Video.Thumbnails.getThumbnail(context.getContentResolver(),
                fileId, MediaStore.Video.Thumbnails.MICRO_KIND, null);

        Cursor thumbCursor = null;
        try {

            thumbCursor = context.getContentResolver().query(
                    MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI,
                    thumbColumns, MediaStore.Video.Thumbnails.VIDEO_ID + " = "
                            + fileId, null, null);

            if (thumbCursor.moveToFirst()) {
                String thumbPath = thumbCursor.getString(thumbCursor
                        .getColumnIndex(MediaStore.Video.Thumbnails.DATA));

                return thumbPath;
            }

        } finally {
        }

        return null;
    }

    public static long getFileId(Context context, Uri fileUri) {

        Cursor cursor = context.getContentResolver().query(fileUri, mediaColumns, null, null,
                null);

        if (cursor.moveToFirst()) {
            int columnIndex = cursor
                    .getColumnIndexOrThrow(MediaStore.Video.Media._ID);
            int id = cursor.getInt(columnIndex);

            return id;
        }

        return 0;
    }

    public static String[] getRingDuring(String mUri) {
        KLog.i("====getRingDuring：start");
        String duration = null;
        String date = null;
        android.media.MediaMetadataRetriever mmr = new android.media.MediaMetadataRetriever();
        try {
            if (mUri != null) {
                mmr.setDataSource(mUri);
            }
            duration = mmr.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_DURATION);
            date = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DATE);
            date = convertTimeFormat(date);
        } catch (Exception ex) {
        } finally {
            mmr.release();
        }
        KLog.i("====getRingDuring：end");
        String[] re = {duration, date};
        return re;
    }


    /**
     * UTC时间转换  yyyy-MM-dd'T'HH:mm:ss.SSSSSSZ eg 2016-10-26T08:20:53.131252Z:
     */
    public static String convertTimeFormat(String sourceTime) throws ParseException {
        String STANDARD_DATE_FORMAT_UTC = "yyyyMMdd'T'HHmmss.SSS'Z'";
        Time time = new Time();
        DateFormat format = new SimpleDateFormat(STANDARD_DATE_FORMAT_UTC);//注意格式化的表达式
        format.setTimeZone(TimeZone.getDefault());
        Date resDate = format.parse(sourceTime);
        return String.valueOf(resDate.getTime());
    }

}