package com.wmlive.hhvideo.fresco;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.BaseDataSubscriber;
import com.facebook.datasource.DataSource;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.animated.base.AnimatedImage;
import com.facebook.imagepipeline.animated.base.AnimatedImageFrame;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.image.CloseableAnimatedImage;
import com.facebook.imagepipeline.image.CloseableBitmap;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.wmlive.hhvideo.utils.FileUtil;
import com.wmlive.hhvideo.utils.KLog;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cn.wmlive.hhvideo.R;

public class FrescoImageHelper {

    public static boolean log = false;

    /**
     * 通过图片URL获取本地缓存文件
     *
     * @param image_net_url
     * @return
     */
    public static File getImageDiskCacheFile(String image_net_url) {
        return FrescoConfigConstants.getImageDiskCacheFile(image_net_url);
    }

    /**
     * 图片缓存的磁盘根目录
     *
     * @return
     */
    public static File getAllImageDiskCacheFile() {
        return new File(FrescoConfigConstants.DISK_CACHE_DIR);
    }

    /**
     * 把一个本地图片添加成指定网络URL的图片缓存
     *
     * @param localImgPath 本地图片路径
     * @param netImgUrl    本地图片对应的网络地址
     * @return 是否成功
     */
    public static boolean addDiskCacheFromLocalImg(String localImgPath, String netImgUrl) {
        boolean ret = false;
        String cachepath = null;
        try {
            if (!TextUtils.isEmpty(localImgPath) && !TextUtils.isEmpty(netImgUrl)) {
                File localFile = new File(localImgPath);
                if (localFile != null && localFile.exists()) {
                    cachepath = FrescoConfigConstants.getImageDiskCachePath(netImgUrl);
                    File cacheFile = new File(cachepath);
                    if (cacheFile != null) {
                        if (cacheFile.exists()) {
                            ret = true; // netImgUrl对应的本地缓存文件已经存在
                        } else {
                            FileUtil.fileChannelCopy(localFile, cacheFile);// 文件通道对拷
                            ret = cacheFile.exists();// copy是否成功
                        }
                    }
                }
            }
        } catch (Exception e) {
        }
        // Log.e("cccmax", "addDiskCacheFromLocalImg ret=" + ret + "\n localImgPath=" + localImgPath + "\n netImgUrl="
        // + netImgUrl + "\ncachepath=" + cachepath);
        return ret;
    }

    /**
     * 创建SimpleDraweeView
     *
     * @param context
     * @return
     */
    public static SimpleDraweeView createView(Context context) {
        SimpleDraweeView view = new SimpleDraweeView(context, FrescoConfigConstants.getGenericDraweeHierarchy(context));
        return view;
    }

    /**
     * 创建SimpleDraweeView
     *
     * @param context
     * @param hierarchy
     * @return
     */
    public static SimpleDraweeView createView(Context context, GenericDraweeHierarchy hierarchy) {
        SimpleDraweeView view = new SimpleDraweeView(context, hierarchy);
        return view;
    }

    // /**
    // * 简单的获取图片
    // *
    // * @param uri
    // * 图片地址
    // * @param view
    // */
    // public static void getImage(String uri, SimpleDraweeView view)
    // {
    // ImageRequest imageRequest = FrescoConfigConstants.getImageRequest(view,
    // uri);
    // DraweeController draweeController =
    // FrescoConfigConstants.getDraweeController(imageRequest, view);
    // view.setController(draweeController);
    // }
    //
    // /**
    // * 简单的获取图片 自定义控制器
    // *
    // * @param uri
    // * 图片地址
    // * @param view
    // * @param controllerlistener
    // * 控制器回调
    // */
    // public static void getImage(String uri, SimpleDraweeView view,
    // BaseControllerListener controllerlistener)
    // {
    // ImageRequest imageRequest = FrescoConfigConstants.getImageRequest(view,
    // uri);
    // DraweeController draweeController =
    // FrescoConfigConstants.getDraweeController(imageRequest, view,
    // controllerlistener);
    // view.setController(draweeController);
    // }

    /**
     * 获取图片 图片高度按真实比例设置(获取到图片后自动计算的)
     *
     * @param param
     * @param view
     * @param ratio_max
     */
    public static void getImage_ChangeRatio(FrescoParam param, SimpleDraweeView view, float... ratio_max) {
        try {
            FrescoConfigConstants.ActualRatioControllerListener l = new FrescoConfigConstants.ActualRatioControllerListener(view);
            if (ratio_max != null && ratio_max.length > 0)
                l.ratio_max = ratio_max[0];
            getImage(param, view, l);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取图片 加载到SimpleDraweeView中，支持Gif图片，自动释放内存
     *
     * @param param
     * @param view               需要用SimpleDraweeView来替代ImageView
     * @param controllerlistener 加载图片的特殊操作可以在这里加，例如ActualRatioControllerListener
     */
    public static void getImage(FrescoParam param, SimpleDraweeView view, FrescoConfigConstants.FrescoPreHandleListener controllerlistener) {

        if (param == null)
            return;

        // 服务器假数据图片地址不对
        // param.setURI(param.getURI().replace("http://10.9.57.202:9000/", ""));

        try {
            // 设置圆角、圆形 ，gif无效
            RoundingParams rp = view.getHierarchy().getRoundingParams();
            if (!param.isNoRoundingParams()) {
                if (rp == null)
                    rp = new RoundingParams();
                rp.setRoundAsCircle(param.getRoundAsCircle());
                if (!param.getRoundAsCircle()) {
                    rp.setCornersRadii(param.getRadius_TL(), param.getRadius_TR(), param.getRadius_BR(), param.getRadius_BL());
                }
                view.getHierarchy().setRoundingParams(rp);
            }
            // 设置描边颜色、宽度
            if (param.getBordeWidth() >= 0 && rp != null) {
                rp.setBorder(param.getBordeColor(), param.getBordeWidth());
                view.getHierarchy().setRoundingParams(rp);
            }

            // 默认图片（占位图）
            if (param.DefaultImageID > 0) {
                view.getHierarchy().setPlaceholderImage(param.DefaultImageID);
                // view.getHierarchy().setPlaceholderImage(view.getContext().getResources().getDrawable(param.DefaultImageID),
                // param.scaletype);
            }
            // 失败（占位图）
            if (param.FailsImageID > 0) {
                view.getHierarchy().setFailureImage(param.FailsImageID);
                view.getHierarchy().setFailureImage(view.getContext().getResources().getDrawable(param.FailsImageID),
                        param.scaletype);
            }
            // view缩放模式
            view.getHierarchy().setActualImageScaleType(param.scaletype);

            // scaletype模式的焦点
            if (param.scaleFocusPoint != null)
                view.getHierarchy().setActualImageFocusPoint(param.scaleFocusPoint);
            // 请求
            ImageRequest imageRequest = FrescoConfigConstants.getImageRequest(view, param.getURI());

            // 图片请求log 包括 分辨率、比例、uri
            // if (controllerlistener == null) {
            // final String uri = param.getURI();
            // controllerlistener = new BaseControllerListener<Object>() {
            //
            // public void onFinalImageSet(String id, Object imageInfo, Animatable animatable) {
            // if (imageInfo != null && imageInfo instanceof ImageInfo) {
            // boolean isGif = CloseableAnimatedImage.class.isInstance(imageInfo);
            // ImageInfo ii = (ImageInfo) imageInfo;
            // int width = ii.getWidth();
            // int height = ii.getHeight();
            // float ratio = width * 1.0F / (height == 0 ? width : height);
            // LogUtil.i("fresco", "BaseControllerListener onFinalImageSet  w=" + width + " h=" + height
            // + " isGif=" + isGif + " ratio=" + ratio + " -----URI=" + uri);
            // }
            // };
            // };
            // }

            DraweeController draweeController = null;
            if (controllerlistener == null) {
                // draweeController = FrescoConfigConstants.getDraweeController(imageRequest,
                // param.getClickToRetryEnabled(), view);

                // 加一个默认处理 其中有必要的Resize处理
                controllerlistener = new FrescoConfigConstants.FrescoPreHandleListener(view) {

                    public void handle(ImageInfo ii, boolean isgif, int w, int h, float _ratio) {
                    }
                };
                draweeController = FrescoConfigConstants.getDraweeController(imageRequest,
                        param.getClickToRetryEnabled(), view, controllerlistener);
            } else {
                draweeController = FrescoConfigConstants.getDraweeController(imageRequest,
                        param.getClickToRetryEnabled(), view, controllerlistener);
            }
            view.setController(draweeController);

        } catch (Exception e) {
            KLog.e("fresco", "getImage exception");
        }
    }

    /**
     * 单纯的请求图片和view无关，加回调后可以做自定义操作
     *
     * @param param
     * @param callback     请求图片回调
     * @param onUIcallback 是否在UI线程执行callback
     */
    public static void getImage(FrescoParam param, CloseableImageCallback callback, boolean onUIcallback) {

        try {
            ImageRequest imageRequest = FrescoConfigConstants.getImageRequest(null, param.getURI());
            ImagePipeline imagePipeline = Fresco.getImagePipeline();
            DataSource<CloseableReference<CloseableImage>> dataSource = imagePipeline.fetchDecodedImage(imageRequest, null);
            FrescoBaseDataSubscriber dataSubscriber = new FrescoBaseDataSubscriber(callback, onUIcallback);
            dataSource.subscribe(dataSubscriber, getExecutor());
        } catch (Exception e) {
            KLog.e("fresco", "getImage CloseableImageCallback exception");
        }
    }

    /**
     * 从bitmap内存缓存中获取
     *
     * @param param
     * @return
     */
    public static Bitmap getMemoryCachedImage(FrescoParam param) {
        ImageRequest imageRequest = FrescoConfigConstants.getImageRequest(null, param.getURI());
        DataSource<CloseableReference<CloseableImage>> dataSource = Fresco.getImagePipeline()
                .fetchImageFromBitmapCache(imageRequest, null);
        CloseableReference<CloseableImage> imageReference = null;
        try {
            imageReference = dataSource.getResult();
            if (imageReference != null) {
                CloseableImage image = imageReference.get();
                // do something with the image
                if (image instanceof CloseableBitmap) {
                    return ((CloseableBitmap) image).getUnderlyingBitmap();
                }
            }
        } finally {
            dataSource.close();
            CloseableReference.closeSafely(imageReference);
        }
        return null;
    }

    /**
     * 从disk缓存中获取图片
     *
     * @param param
     * @return
     */
    public static Bitmap getDiskCachedImage(FrescoParam param) {
        try {
            File file = getImageDiskCacheFile(param.getURI());
            if (file != null) {

                BitmapFactory.Options newOpts = new BitmapFactory.Options();
                newOpts.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(file.getAbsolutePath(), newOpts);
                int w = newOpts.outWidth;
                int h = newOpts.outHeight;
                float hh = 1024;
                float ww = 1024;
                int SCALE = 1;
                if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
                    SCALE = (int) (newOpts.outWidth / ww);
                } else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
                    SCALE = (int) (newOpts.outHeight / hh);
                }
                if (SCALE <= 1)
                    SCALE = 1;
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = false;
                options.inSampleSize = SCALE;
                Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
                return bitmap;
            }
        } catch (Exception e) {
        }
        return null;
    }

    private static ExecutorService executor = null;

    private static ExecutorService getExecutor() {
        if (executor == null)
            executor = Executors.newFixedThreadPool(3);
        return executor;
    }

    public static interface CloseableImageCallback {

        public void callback(CloseableImage image, Bitmap bitmap);
    }

    /**
     * fresco数据源请求监听
     */
    private static class FrescoBaseDataSubscriber extends BaseDataSubscriber<CloseableReference<CloseableImage>> {

        boolean callbackOnUI = true;
        CloseableImageCallback callback;

        public FrescoBaseDataSubscriber(CloseableImageCallback cb, boolean callbackOnUI) {
            callback = cb;
            this.callbackOnUI = callbackOnUI;
        }

        protected void onNewResultImpl(DataSource<CloseableReference<CloseableImage>> dataSource) {

            // if (!dataSource.isFinished()) {
            // Log.e("fresco",
            // "Not yet finished - this is just another progressive scan.");
            // }

            CloseableReference<CloseableImage> imageReference = dataSource.getResult();
            if (imageReference != null) {
                try {
                    final CloseableImage image = imageReference.get();
                    if (image != null) {

                        if (log)
                            KLog.i("fresco",
                                    "onNewResultImpl " + " w=" + image.getWidth() + " h=" + image.getHeight()
                                            + " image=" + image);

                        Bitmap bitmap = null;

                        if (image instanceof CloseableBitmap) {
                            // jpg png
                            bitmap = ((CloseableBitmap) image).getUnderlyingBitmap();
                        } else if (image instanceof CloseableAnimatedImage) {
                            // GIF WEBP
                            try {
                                CloseableAnimatedImage cai = (CloseableAnimatedImage) image;
                                if (cai.getImageResult().getPreviewBitmap() != null)
                                    bitmap = cai.getImageResult().getPreviewBitmap().get();
                                if (bitmap == null) {
                                    AnimatedImage ai = cai.getImage();
                                    if (ai != null && ai.getFrameCount() > 0) {
                                        AnimatedImageFrame aif = ai.getFrame(0);
                                        if (aif != null) {
                                            bitmap = Bitmap.createBitmap(aif.getWidth(), aif.getHeight(),
                                                    Config.ARGB_8888);
                                            aif.renderFrame(aif.getWidth(), aif.getHeight(), bitmap);
                                        }
                                    }
                                }
                                if (bitmap != null) {
                                    Bitmap tmp = bitmap.copy(Config.RGB_565, true);
                                    bitmap.recycle();
                                    bitmap = null;
                                    bitmap = tmp;
                                }
                            } catch (Exception e) {
                            }
                        }

                        if (callbackOnUI) {
                            final Bitmap tmpbitmap = bitmap;
                            Runnable runnable = new Runnable() {

                                public void run() {
                                    callback.callback(image, tmpbitmap);
                                }
                            };
                            Handler handler = new Handler(Looper.getMainLooper());
                            handler.post(runnable);
                        } else {
                            callback.callback(image, bitmap);
                        }
                    }

                } finally {
                    imageReference.close();
                }
            }
        }

        protected void onFailureImpl(DataSource dataSource) {
            try {
                Throwable throwable = dataSource.getFailureCause();
                Log.e("fresco", "onFailureImpl Throwable＝" + throwable.getMessage());

                // handle failure
            } catch (Exception e) {
            }
        }
    }

    /**
     * 清除某一个图片的缓存
     *
     * @param param
     */
    public static void evictFromCache(FrescoParam param) {
        try {
            Fresco.getImagePipeline().evictFromCache(Uri.parse(param.getURI()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // --------------------------fresco框架加载图片设置给ImageView

    private static HashMap<CloseableImageCallback, WeakReference<View>> mViews = new HashMap<CloseableImageCallback, WeakReference<View>>();

    public static void loadImage(String url, SimpleDraweeView simpleDraweeView) {
        FrescoParam param = new FrescoParam(url);
        FrescoImageHelper.getImage(param, simpleDraweeView);
    }

    public static void showThumb(SimpleDraweeView draweeView, String url, int width, int height) {
        if (url == null || "".equals(url))
            return;
        if (draweeView == null)
            return;
        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(Uri.parse(url))
                .setResizeOptions(new ResizeOptions(width, height))
                .build();
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setImageRequest(request)
                .setOldController(draweeView.getController())
                .setControllerListener(new BaseControllerListener<ImageInfo>())
                .build();
        draweeView.setController(controller);
    }


    public static void loadImage(String url, SimpleDraweeView simpleDraweeView, int defaultImg) {
        FrescoParam param = new FrescoParam(url);
        param.setDefaultImage(defaultImg);
        param.setFailsImage(defaultImg);
        FrescoImageHelper.getImage(param, simpleDraweeView);
    }

    /**
     * 获取图片 加载到ImageView中，不支持Gif图片
     *
     * @param param
     * @param view  ImageView
     */
    public static void getImage(FrescoParam param, ImageView view) {
        if (param == null || view == null)
            return;

        if (view instanceof SimpleDraweeView) {
            getImage(param, (SimpleDraweeView) view, null);
            return;
        }

        // final int key = param.getURI().hashCode();
//        if (param.DefaultImageID != 0 && view.getDrawable() == null) {
//            // 如果有Imageview已经设置了图片 drawable不是null，就不替换默认图了
//            view.setImageResource(param.DefaultImageID);
//        }
        CloseableImageCallback cicb = new CloseableImageCallback() {

            public void callback(CloseableImage image, Bitmap bitmap) {
                if (log)
                    KLog.d("fresco", "getImageToImageView bitmap=" + bitmap);
                try {
                    if (bitmap != null) {
                        FrescoConfigConstants.autoResizeBitmap(bitmap, null);
                        WeakReference<View> br = mViews.get(this);
                        View v = null;
                        if (br != null) {
                            v = br.get();
                        }
                        if (v != null && (v instanceof ImageView)) {
                            ((ImageView) v).setImageBitmap(bitmap);
                        }
                    }
                } catch (Exception e) {
                    KLog.e("frescoToImageview", "CloseableImageCallback exception");
                }
                mViews.remove(this);
            }
        };
        mViews.put(cicb, new WeakReference<View>(view));
        getImage(param, cicb, true);
    }

    /**
     * 给Fresco图片控件设置图片
     *
     * @param uri  网络地址、磁盘文件、asset、res
     * @param view SimpleDraweeView、FrescoImageView
     */
    public static void getImage(String uri, SimpleDraweeView view) {
        FrescoParam fp = new FrescoParam(uri);
        // fp.setDefaultImage(R.drawable.def_image_bg); // 默认图
        getImage(fp, view, null);
    }

    /**
     * 给普通的ImageView设置图片
     *
     * @param uri  网络地址、磁盘文件、asset、res
     * @param view ImageView
     */
    public static void getImage(String uri, ImageView view) {
        if (view instanceof SimpleDraweeView) {
            getImage(uri, (SimpleDraweeView) view);
            return;
        }
        FrescoParam fp = new FrescoParam(uri);
        // fp.setDefaultImage(R.drawable.def_image_bg);// 默认图
        getImage(fp, view);
    }

    //=============================加载礼物部分===============================

    /**
     * 加载网络礼物资源时
     *
     * @param uri
     * @param view
     */
    public static void loadGiftResByNetwork(String uri, SimpleDraweeView view) {
        FrescoParam fp = new FrescoParam(uri);
        fp.setDefaultImage(R.drawable.home_gift_default);// 默认透明占位
        fp.setFailsImage(R.drawable.home_gift_default);
        getImage(fp, view, null);
    }

    /**
     * 加载本地礼物资源时
     *
     * @param uri
     * @param view
     */
    public static void loadGiftResByLocal(String uri, SimpleDraweeView view) {
        FrescoParam fp = new FrescoParam(uri);
        fp.setDefaultImage(R.drawable.home_gift_default);// 默认透明占位
        fp.setFailsImage(R.drawable.home_gift_default);
        getImage(fp, view, null);
    }


    /**
     * 加载drawable 资源
     *
     * @param drawableId
     * @param context
     * @param view
     */
    public static void loadGiftResByRes(int drawableId, Context context, SimpleDraweeView view) {
        String uri = "res://" + context.getPackageName() + "/" + drawableId;
        FrescoParam fp = new FrescoParam(uri);
        fp.setDefaultImage(R.drawable.home_gift_default);// 默认透明占位
        fp.setFailsImage(R.drawable.home_gift_default);
        getImage(fp, view, null);
    }

    //=================================加载其他资源============================

    public static void loadImagesResByNetwork(String url, int defaultId, int failsId, Context context, SimpleDraweeView view) {
        FrescoParam fp = new FrescoParam(url);
        fp.setDefaultImage(defaultId);// 默认透明占位
        fp.setFailsImage(failsId);
        getImage(fp, view, null);
    }


    public static void loadImagesRes(int drawableId, Context context, SimpleDraweeView view) {
        String uri = "res://" + context.getPackageName() + "/" + drawableId;
        FrescoParam fp = new FrescoParam(uri);
        fp.setDefaultImage(R.drawable.home_gift_default);// 默认透明占位
        fp.setFailsImage(R.drawable.home_gift_default);
        getImage(fp, view, null);
    }


//    public static void getFileEmptyDefault(,Context context, SimpleDraweeView view) {
//        String uri = "res://"+context.getPackageName()+"/"+drawableId;
//        FrescoParam fp = new FrescoParam(uri);
//        fp.setDefaultImage(R.drawable.full_transparent);// 默认透明占位
//        fp.setFailsImage(R.drawable.hh_live_gift_default);
//        getImage(fp, view, null);
//    }

}
