package com.wmlive.hhvideo.fresco;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

import com.facebook.cache.common.CacheKey;
import com.facebook.cache.disk.DiskCacheConfig;
import com.facebook.common.internal.Supplier;
import com.facebook.common.logging.FLog;
import com.facebook.common.util.ByteConstants;
import com.facebook.common.util.SecureHashUtil;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.interfaces.SimpleDraweeControllerBuilder;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.cache.MemoryCacheParams;
import com.facebook.imagepipeline.common.ImageDecodeOptions;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.image.CloseableAnimatedImage;
import com.facebook.imagepipeline.image.CloseableStaticBitmap;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.listener.RequestListener;
import com.facebook.imagepipeline.listener.RequestLoggingListener;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequest.RequestLevel;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.wmlive.hhvideo.utils.AppCacheFileUtils;
import com.wmlive.hhvideo.utils.ImageUtils;
import com.wmlive.hhvideo.utils.KLog;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * Fresco图片库配置
 */
public class FrescoConfigConstants {

    /**
     * 是否使用OKhttp
     */
    private static final boolean USE_OKHTTP = false;
    private static int screen_w, screen_h;

    /**
     * 分配的可用内存
     */
    private static final int MAX_HEAP_SIZE = (int) Runtime.getRuntime().maxMemory();
    /**
     * 使用的缓存数量
     */
    public static final int MAX_MEMORY_CACHE_SIZE = MAX_HEAP_SIZE / 4;
    public static final int MAX_MEMORY_IMAGE_SIZE = 100;

    /**
     * 小图极低磁盘空间缓存的最大值（特性：可将大量的小图放到额外放在另一个磁盘空间防止大图占用磁盘空间而删除了大量的小图）
     */
    public static final int MAX_SMALL_DISK_VERYLOW_CACHE_SIZE = 5 * ByteConstants.MB;
    /**
     * 小图低磁盘空间缓存的最大值（特性：可将大量的小图放到额外放在另一个磁盘空间防止大图占用磁盘空间而删除了大量的小图）
     */
    public static final int MAX_SMALL_DISK_LOW_CACHE_SIZE = 20 * ByteConstants.MB;
    /**
     * 小图磁盘缓存的最大值（特性：可将大量的小图放到额外放在另一个磁盘空间防止大图占用磁盘空间而删除了大量的小图）
     */
    public static final int MAX_SMALL_DISK_CACHE_SIZE = 20 * ByteConstants.MB;

    /**
     * 默认图极低磁盘空间缓存的最大值
     */
    public static final int MAX_DISK_CACHE_VERYLOW_SIZE = 10 * ByteConstants.MB;
    /**
     * 默认图低磁盘空间缓存的最大值
     */
    public static final int MAX_DISK_CACHE_LOW_SIZE = 30 * ByteConstants.MB;
    /**
     * 默认图磁盘缓存的最大值
     */
    public static final int MAX_DISK_CACHE_SIZE = 200 * ByteConstants.MB;

    /**
     * 小图所放路径的文件夹名
     */
    private static final String IMAGE_PIPELINE_SMALL_CACHE_DIR = "imagepipeline_cache";
    /**
     * 默认图所放路径的文件夹名
     */
    private static final String IMAGE_PIPELINE_CACHE_DIR = "imagepipeline_cache";

    /**
     * 应用的磁盘文件夹
     */
//    public static final String APP_DISK_DIR = Environment.getExternalStorageDirectory().getAbsolutePath()
//            + File.separator + com.base.utils.FileUtils.BASE_PATH + File.separator;
    public static final String APP_DISK_DIR = AppCacheFileUtils.getAppImagesPath() + File.separator;

    /**
     * fresco图片缓存的磁盘根目录
     */
    public static final String DISK_CACHE_DIR = APP_DISK_DIR + IMAGE_PIPELINE_CACHE_DIR + File.separator;

    private static ImagePipelineConfig sImagePipelineConfig;

    private FrescoConfigConstants() {
    }

    /**
     * fresco 图片库 初始化 最好在application中进行
     *
     * @param context
     */
    public static void initialize(Context context) {
        Fresco.initialize(context, getImagePipelineConfig(context));// 图片缓存初始化配置
        FLog.setMinimumLoggingLevel(FLog.ERROR);
        screen_w = context.getResources().getDisplayMetrics().widthPixels;
        screen_h = context.getResources().getDisplayMetrics().heightPixels;
    }

    /**
     * 初始化配置，单例
     */
    private static ImagePipelineConfig getImagePipelineConfig(Context context) {
        if (sImagePipelineConfig == null) {
            sImagePipelineConfig = configureCaches(context);
        }

        return sImagePipelineConfig;
    }

    /**
     * 初始化配置
     */
    private static ImagePipelineConfig configureCaches(Context context) {

        File dirFile = new File(APP_DISK_DIR);

        // 内存配置
        final MemoryCacheParams bitmapCacheParams = new MemoryCacheParams(FrescoConfigConstants.MAX_MEMORY_CACHE_SIZE, // 内存缓存中总图片的最大大小,以字节为单位。
                MAX_MEMORY_IMAGE_SIZE, // 内存缓存中图片的最大数量。
                FrescoConfigConstants.MAX_MEMORY_CACHE_SIZE, // 内存缓存中准备清除但尚未被删除的总图片的最大大小,以字节为单位。
                MAX_MEMORY_IMAGE_SIZE, // 内存缓存中准备清除的总图片的最大数量。
                Integer.MAX_VALUE); // 内存缓存中单个图片的最大大小。

        // 修改内存图片缓存数量，空间策略（这个方式有点恶心）
        Supplier<MemoryCacheParams> mSupplierMemoryCacheParams = new Supplier<MemoryCacheParams>() {

            public MemoryCacheParams get() {
                return bitmapCacheParams;
            }
        };

        // 小图片的磁盘配置
        DiskCacheConfig diskSmallCacheConfig = DiskCacheConfig.newBuilder(context).setBaseDirectoryPath(dirFile)// (context.getApplicationContext().getCacheDir())//
                // 缓存图片基路径
                .setBaseDirectoryName(IMAGE_PIPELINE_SMALL_CACHE_DIR)// 文件夹名
                // .setCacheErrorLogger(cacheErrorLogger)//日志记录器用于日志错误的缓存。
                // .setCacheEventListener(cacheEventListener)//缓存事件侦听器。
                // .setDiskTrimmableRegistry(diskTrimmableRegistry)//类将包含一个注册表的缓存减少磁盘空间的环境。
                .setMaxCacheSize(FrescoConfigConstants.MAX_DISK_CACHE_SIZE)// 默认缓存的最大大小。
                .setMaxCacheSizeOnLowDiskSpace(MAX_SMALL_DISK_LOW_CACHE_SIZE)// 缓存的最大大小,使用设备时低磁盘空间。
                .setMaxCacheSizeOnVeryLowDiskSpace(MAX_SMALL_DISK_VERYLOW_CACHE_SIZE)// 缓存的最大大小,当设备极低磁盘空间
                // .setVersion(version)
                .build();

        // 默认图片的磁盘配置
        DiskCacheConfig diskCacheConfig = DiskCacheConfig.newBuilder(context).setBaseDirectoryPath(dirFile)// (Environment.getExternalStorageDirectory().getAbsoluteFile())//
                // 缓存图片基路径
                .setBaseDirectoryName(IMAGE_PIPELINE_CACHE_DIR)// 文件夹名
                // .setCacheErrorLogger(cacheErrorLogger)//日志记录器用于日志错误的缓存。
                // .setCacheEventListener(cacheEventListener)//缓存事件侦听器。
                // .setDiskTrimmableRegistry(diskTrimmableRegistry)//类将包含一个注册表的缓存减少磁盘空间的环境。
                .setMaxCacheSize(FrescoConfigConstants.MAX_DISK_CACHE_SIZE)// 默认缓存的最大大小。
                .setMaxCacheSizeOnLowDiskSpace(MAX_DISK_CACHE_LOW_SIZE)// 缓存的最大大小,使用设备时低磁盘空间。
                .setMaxCacheSizeOnVeryLowDiskSpace(MAX_DISK_CACHE_VERYLOW_SIZE)// 缓存的最大大小,当设备极低磁盘空间
                // .setVersion(version)
                .build();

        // 缓存图片配置
        ImagePipelineConfig.Builder configBuilder = null;
        if (USE_OKHTTP) {
            // 需要imagepipeline-okhttp-v0.9.0.jar
            // configBuilder = OkHttpImagePipelineConfigFactory.newBuilder(context, HttpUtil.getOkHttpClient());
        } else {
            configBuilder = ImagePipelineConfig.newBuilder(context);
        }
        Set<RequestListener> requestListeners = new HashSet<>();
        RequestLoggingListener loggingListener = new RequestLoggingListener();
        requestListeners.add(loggingListener);
        configBuilder
                // .setAnimatedImageFactory(AnimatedImageFactory
                // animatedImageFactory)//图片加载动画
                .setBitmapMemoryCacheParamsSupplier(mSupplierMemoryCacheParams)// 内存缓存配置（一级缓存，已解码的图片）
                // .setCacheKeyFactory(cacheKeyFactory)//缓存Key工厂
                // .setEncodedMemoryCacheParamsSupplier(encodedCacheParamsSupplier)//内存缓存和未解码的内存缓存的配置（二级缓存）
                // .setExecutorSupplier(executorSupplier)//线程池配置
                // .setImageCacheStatsTracker(imageCacheStatsTracker)//统计缓存的命中率
                // .setImageDecoder(ImageDecoder imageDecoder) //图片解码器配置
                // .setIsPrefetchEnabledSupplier(Supplier<Boolean>
                // isPrefetchEnabledSupplier)//图片预览（缩略图，预加载图等）预加载到文件缓存
                .setMainDiskCacheConfig(diskCacheConfig)// 磁盘缓存配置（总，三级缓存）
                // .setMemoryTrimmableRegistry(memoryTrimmableRegistry)
                // //内存用量的缩减,有时我们可能会想缩小内存用量。比如应用中有其他数据需要占用内存，不得不把图片缓存清除或者减小
                // 或者我们想检查看看手机是否已经内存不够了。
                // .setNetworkFetcher(networkFetcher)//自定的网络层配置：如OkHttp，Volley
                // .setPoolFactory(poolFactory)//线程池工厂配置
                // .setProgressiveJpegConfig(progressiveJpegConfig)//渐进式JPEG图
                .setRequestListeners(requestListeners)//图片请求监听
                .setSmallImageDiskCacheConfig(diskSmallCacheConfig)// 磁盘缓存配置（小图片，可选～三级缓存的小图优化缓存）

                .setResizeAndRotateEnabledForNetwork(false)// 调整和旋转是否支持网络图片
                .setDownsampleEnabled(true)
        // mDownsampleEnabled——设置EncodeImage解码时是否解码图片样图，必须和ImageRequest的ResizeOptions一起使用
        // 作用就是在图片解码时根据ResizeOptions所设的宽高的像素进行解码，这样解码出来可以得到一个更小的Bitmap。
        // 通过在Decode图片时，来改变采样率来实现得，使其采样ResizeOptions大小。
        // ResizeOptions和DownsampleEnabled参数都不影响原图片的大小，影响的是EncodeImage的大小，
        // 进而影响Decode出来的Bitmap的大小，ResizeOptions须和此参数结合使用是因为单独使用ResizeOptions的话只支持JPEG图，
        // 所以需支持png、jpg、webp需要先设置此参数。
        ;
        return configBuilder.build();
    }

    // 圆形，圆角切图，对动图无效
    public static RoundingParams getRoundingParams() {
        RoundingParams roundingParams = RoundingParams.fromCornersRadius(0F);
        // roundingParams.asCircle();//圆形
        // roundingParams.setBorder(color,
        // width);//fresco:roundingBorderWidth="2dp"边框
        // fresco:roundingBorderColor="@color/border_color"
        // roundingParams.setCornersRadii(radii);//半径
        // roundingParams.setCornersRadii(topLeft, topRight, bottomRight,
        // bottomLeft)//fresco:roundTopLeft="true" fresco:roundTopRight="false"
        // fresco:roundBottomLeft="false" fresco:roundBottomRight="true"
        // roundingParams.
        // setCornersRadius(radius);//fresco:roundedCornerRadius="1dp"圆角
        // roundingParams.setOverlayColor(overlayColor);//fresco:roundWithOverlayColor="@color/corner_color"
        // roundingParams.setRoundAsCircle(roundAsCircle);//圆
        // roundingParams.setRoundingMethod(roundingMethod);
        // fresco:progressBarAutoRotateInterval="1000"自动旋转间隔
        // 或用 fromCornersRadii 以及 asCircle 方法
        return roundingParams;
    }

    // Drawees DraweeHierarchy 组织
    public static GenericDraweeHierarchy getGenericDraweeHierarchy(Context context) {
        GenericDraweeHierarchy gdh = new GenericDraweeHierarchyBuilder(context.getResources())
                // .reset()//重置
                // .setActualImageColorFilter(colorFilter)//颜色过滤
                // .setActualImageFocusPoint(focusPoint)//focusCrop, 需要指定一个居中点
                // .setActualImageMatrix(actualImageMatrix)
                // .setActualImageScaleType(actualImageScaleType)//fresco:actualImageScaleType="focusCrop"缩放类型
                // .setBackground(background)//fresco:backgroundImage="@color/blue"背景图片
                // .setBackgrounds(backgrounds)
                // fresco:fadeDuration="300"加载图片动画时间
                // .setFailureImage(FrescoConfigConstants.sErrorDrawable)//
                // fresco:failureImage="@drawable/error"失败图
                // .setFailureImage(failureDrawable,
                // failureImageScaleType)//fresco:failureImageScaleType="centerInside"失败图缩放类型
                // .setOverlay(overlay)//fresco:overlayImage="@drawable/watermark"叠加图
                // .setOverlays(overlays)
                // .setPlaceholderImage(FrescoConfigConstants.sPlaceholderDrawable)//
                // fresco:placeholderImage="@color/wait_color"占位图
                // .setPlaceholderImage(placeholderDrawable,
                // placeholderImageScaleType)//fresco:placeholderImageScaleType="fitCenter"占位图缩放类型
                // .setPressedStateOverlay(drawable)//fresco:pressedStateOverlayImage="@color/red"按压状态下的叠加图
                // .setProgressBarImage(new ProgressBarDrawable())//
                // 进度条fresco:progressBarImage="@drawable/progress_bar"进度条
                // .setProgressBarImage(progressBarImage,
                // progressBarImageScaleType)//fresco:progressBarImageScaleType="centerInside"进度条类型
                // .setRetryImage(retryDrawable)//fresco:retryImage="@drawable/retrying"点击重新加载
                // .setRetryImage(retryDrawable,
                // retryImageScaleType)//fresco:retryImageScaleType="centerCrop"点击重新加载缩放类型
                .setRoundingParams(getRoundingParams())// 圆形/圆角fresco:roundAsCircle="true"圆形
                .build();
        return gdh;
    }

    // DraweeView～～～SimpleDraweeView——UI组件
    // public static SimpleDraweeView getSimpleDraweeView(Context context,Uri
    // uri){
    // SimpleDraweeView simpleDraweeView=new SimpleDraweeView(context);
    // simpleDraweeView.setImageURI(uri);
    // simpleDraweeView.setAspectRatio(1.33f);//宽高缩放比
    // return simpleDraweeView;
    // }

    // SimpleDraweeControllerBuilder
    public static SimpleDraweeControllerBuilder getSimpleDraweeControllerBuilder(SimpleDraweeControllerBuilder sdcb,
                                                                                 Uri uri, Object callerContext, DraweeController draweeController) {
        SimpleDraweeControllerBuilder controllerBuilder = sdcb.setUri(uri).setCallerContext(callerContext)
                // .setAspectRatio(1.33f);//宽高缩放比
                .setOldController(draweeController);
        return controllerBuilder;
    }

    // 图片解码
    public static ImageDecodeOptions getImageDecodeOptions() {
        ImageDecodeOptions decodeOptions = ImageDecodeOptions.newBuilder()
                // .setBackgroundColor(Color.TRANSPARENT)//图片的背景颜色
                // .setDecodeAllFrames(decodeAllFrames)//解码所有帧
                // .setDecodePreviewFrame(decodePreviewFrame)//解码预览框
                // .setForceOldAnimationCode(forceOldAnimationCode)//使用以前动画
                // .setFrom(options)//使用已经存在的图像解码
                // .setMinDecodeIntervalMs(intervalMs)//最小解码间隔（分位单位）
                .setUseLastFrameForPreview(true)// 使用最后一帧进行预览
                .build();
        return decodeOptions;
    }

    // 图片显示
    public static ImageRequest getImageRequest(SimpleDraweeView view, String uri) {
        // Log.d("cccmax", "getImageRequest uri=" + uri);
        // uri =
        // "http://g.hiphotos.baidu.com/baike/w%3D268/sign=66d17ed667380cd7e61ea5eb9945ad14/e61190ef76c6a7ef18b42940fffaaf51f2de66c2.jpg";
        ImageRequest imageRequest = null;
        Uri _uri = uri == null ? Uri.parse("") : Uri.parse(uri);
        ImageRequestBuilder builder = ImageRequestBuilder.newBuilderWithSource(_uri);
        // builder.setAutoRotateEnabled(true)//自动旋转图片方向
        // builder.setImageDecodeOptions(getImageDecodeOptions())// 图片解码库
        // builder.setImageType(ImageType.SMALL)//图片类型，设置后可调整图片放入小图磁盘空间还是默认图片磁盘空间
        builder.setLocalThumbnailPreviewsEnabled(true);//缩略图预览，影响图片显示速度（轻微）
        builder.setLowestPermittedRequestLevel(RequestLevel.FULL_FETCH);// 请求经过缓存级别
        // BITMAP_MEMORY_CACHE，ENCODED_MEMORY_CACHE，DISK_CACHE，FULL_FETCH
        // builder.setPostprocessor(postprocessor)//修改图片
        // builder.setProgressiveRenderingEnabled(true)//渐进加载，主要用于渐进式的JPEG图，影响图片显示速度（普通）
        // builder.setSource(Uri uri)//设置图片地址

        if (view != null && view.getLayoutParams().width > 0 && view.getLayoutParams().height > 0) {
            // 调整大小 有固定的大小的时候才会调整
            builder.setResizeOptions(new ResizeOptions(view.getLayoutParams().width, view.getLayoutParams().height));
        } else if (view != null && view.getMeasuredWidth() > 0 && view.getMeasuredHeight() > 0) {
            builder.setResizeOptions(new ResizeOptions(view.getMeasuredWidth(), view.getMeasuredHeight()));
        } else if (screen_w > 0 && screen_h > 0) {
            int size = Math.max(screen_w, screen_h);
            builder.setResizeOptions(new ResizeOptions(size, size));
        }

        imageRequest = builder.build();
        if (imageRequest.getResizeOptions() != null) {
            if (FrescoImageHelper.log)
                KLog.i(
                        "Fresco",
                        "mResizeOptions=" + imageRequest.getResizeOptions().width + ","
                                + imageRequest.getResizeOptions().height + " url=" + uri);
        }
        return imageRequest;
    }

    // DraweeController 控制 DraweeControllerBuilder
    public static DraweeController getDraweeController(ImageRequest imageRequest, boolean retry, SimpleDraweeView view) {
        DraweeController draweeController = null;
        PipelineDraweeControllerBuilder builder = Fresco.newDraweeControllerBuilder();
        // .reset()//重置
        builder.setAutoPlayAnimations(true);// 自动播放图片动画
        // builder.setCallerContext(callerContext);//回调
        // builder.setControllerListener(view.getListener());// 监听图片下载完毕等
        // builder.setDataSourceSupplier(dataSourceSupplier);//数据源
        // builder.setFirstAvailableImageRequests(firstAvailableImageRequests);//本地图片复用，可加入ImageRequest数组
        builder.setImageRequest(imageRequest);// 设置单个图片请求～～～不可与setFirstAvailableImageRequests共用，配合setLowResImageRequest为高分辨率的图
        // builder.setLowResImageRequest(ImageRequest.fromUri(lowResUri));//先下载显示低分辨率的图
        builder.setOldController(view.getController());// DraweeController复用
        builder.setTapToRetryEnabled(retry);// 点击重新加载图
        draweeController = builder.build();
        return draweeController;
    }

    /**
     * DraweeController 控制 DraweeControllerBuilder
     *
     * @param imageRequest
     * @param view
     * @param controllerListener 图片下载监听
     * @return
     */
    public static DraweeController getDraweeController(ImageRequest imageRequest, boolean retry, SimpleDraweeView view,
                                                       BaseControllerListener controllerListener) {
        DraweeController draweeController = null;
        PipelineDraweeControllerBuilder builder = Fresco.newDraweeControllerBuilder();
        // .reset()//重置
        builder.setAutoPlayAnimations(true);// 自动播放图片动画
        // builder.setCallerContext(callerContext);//回调
        builder.setControllerListener(controllerListener);// 监听图片下载完毕等
        // builder.setControllerListener(view.getListener());// 监听图片下载完毕等
        // builder.setDataSourceSupplier(dataSourceSupplier);//数据源
        // builder.setFirstAvailableImageRequests(firstAvailableImageRequests);//本地图片复用，可加入ImageRequest数组
        builder.setImageRequest(imageRequest);// 设置单个图片请求～～～不可与setFirstAvailableImageRequests共用，配合setLowResImageRequest为高分辨率的图
        // builder.setLowResImageRequest(ImageRequest.fromUri(lowResUri));//先下载显示低分辨率的图
        builder.setOldController(view.getController());// DraweeController复用
        builder.setTapToRetryEnabled(false);// 点击重新加载图
        draweeController = builder.build();
        return draweeController;
    }

    // 默认加载图片和失败图片
    // public static Drawable sPlaceholderDrawable;
    // public static Drawable sErrorDrawable;

    @SuppressWarnings("deprecation")
    public static void init(final Resources resources) {
        // if (sPlaceholderDrawable == null)
        // {
        // sPlaceholderDrawable = resources.getDrawable(R.color.placeholder);
        // }
        // if (sErrorDrawable == null)
        // {
        // sErrorDrawable = resources.getDrawable(R.color.error);
        // }
    }

    /**
     * 请求到图片之后 按真实比例设置图片view<br>
     * 在onFinalImageSet方法内会自动重置view的宽高比例<br>
     * width建议设置matchparent或实际尺寸<br>
     * height建议设置warpcontent
     *
     * @author CCCMAX
     */
    public static class ActualRatioControllerListener extends FrescoPreHandleListener {

        public float ratio_max = -1;

        /**
         * @param sdview
         * @see ActualRatioControllerListener
         */
        public ActualRatioControllerListener(SimpleDraweeView sdview) {
            super(sdview);
        }

        public void handle(ImageInfo ii, boolean isgif, int w, int h, float _ratio) {
            try {
                if (ratio_max != -1 && ratio < ratio_max) {
                    ratio = ratio_max;// 最大比例
                }
                if (draweeview != null) {
                    if (draweeview.getLayoutParams().width == -1 && draweeview.getWidth() <= 1) {
                        LayoutParams lp = draweeview.getLayoutParams();
                        View parent = (View) draweeview.getParent();
                        if (parent.getWidth() > 1) {
                            lp.width = parent.getWidth();
                            draweeview.setLayoutParams(lp);
                        }
                    }
                    draweeview.setAspectRatio(ratio);
                }
            } catch (Throwable e) {
            }
        }

    }

    /**
     * fresco图片设置到View之前的预处理 可以再此时修改view的布局宽高等等
     *
     * @author CCCMAX
     */
    public static abstract class FrescoPreHandleListener extends BaseControllerListener<Object> {

        public SimpleDraweeView draweeview;
        /**
         * 图片真实宽度 在onFinalImageSet后赋值
         */
        public int width = 0;
        /**
         * 图片真实高度 在onFinalImageSet后赋值
         */
        public int height = 0;
        /**
         * 图片是否是动态图片 在onFinalImageSet后赋值
         */
        public boolean isGif = false;
        /**
         * 图片宽高比例 在onFinalImageSet后赋值
         */
        public float ratio = 1.0F;

        ImageInfo imageinfo;

        public int resize_w = 0;
        public int resize_h = 0;

        /**
         * @param sdview
         * @see FrescoPreHandleListener
         */
        public FrescoPreHandleListener(SimpleDraweeView sdview) {
            draweeview = sdview;
        }

        /**
         * 特殊情况下 强制修改bitmap尺寸 如果给定的宽高比例与原图不符 依原图比例为准对resize_h进行修改
         *
         * @param sdview
         * @param resize_w 强制修改 宽
         * @param resize_h 强制修改 高
         */
        public FrescoPreHandleListener(SimpleDraweeView sdview, int resize_w, int resize_h) {
            draweeview = sdview;
            this.resize_w = resize_w;
            this.resize_h = resize_h;
        }

        public void onSubmit(String id, Object callerContext) {
            if (FrescoImageHelper.log)
                KLog.i("Fresco", "onSubmit");
        }

        public void onFinalImageSet(String id, Object imageInfo, Animatable animatable) {
            if (FrescoImageHelper.log)
                KLog.i("Fresco", "onFinalImageSet");
            if (imageInfo != null && imageInfo instanceof ImageInfo) {
                try {
                    isGif = CloseableAnimatedImage.class.isInstance(imageInfo);
                    imageinfo = (ImageInfo) imageInfo;

                    if (!isGif) {
                        CloseableStaticBitmap csb = (CloseableStaticBitmap) imageInfo;
                        resizeBitmap(csb.getUnderlyingBitmap());
                    }

                    width = imageinfo.getWidth();
                    height = imageinfo.getHeight();
                    ratio = width * 1.0F / (height == 0 ? width : height);
                    if (FrescoImageHelper.log)
                        KLog.i("Fresco", "FrescoPreHandleListener onFinalImageSet  w=" + imageinfo.getWidth()
                                + " h=" + imageinfo.getHeight() + " isGif=" + isGif + " ratio=" + ratio + "\nview="
                                + draweeview);

                } catch (Exception e) {
                    KLog.e("Fresco", e.getMessage());
                    e.printStackTrace();
                }
            }

            handle(imageinfo, isGif, width, height, ratio);
        }

        /**
         * 可以根据需要重写此方法
         *
         * @param bitmap
         */
        public void resizeBitmap(Bitmap bitmap) {
            autoResizeBitmap(bitmap, new Point(resize_w, resize_h));
        }

        public abstract void handle(ImageInfo ii, boolean isgif, int w, int h, float _ratio);

        public void onFailure(String id, Throwable throwable) {
            if (FrescoImageHelper.log)
                KLog.i("Fresco", "onFailure");
        }

        public void onRelease(String id) {
            if (FrescoImageHelper.log)
                KLog.i("Fresco", "onRelease");
        }

        public void onIntermediateImageSet(String id, Object imageInfo) {
            if (FrescoImageHelper.log)
                KLog.i("Fresco", "onIntermediateImageSet");
        }

        public void onIntermediateImageFailed(String id, Throwable throwable) {
            if (FrescoImageHelper.log)
                KLog.i("Fresco", "onIntermediateImageFailed");
        }

    }

    // -----------------------------------------

    /**
     * 通过URL获取硬盘缓存的key
     *
     * @param url
     * @return
     */
    public static CacheKey getCacheKey_encoded(String url) {
        ImageRequest ir = getImageRequest(null, url);
        return sImagePipelineConfig.getCacheKeyFactory().getEncodedCacheKey(ir, null);
    }

    /**
     * cachekey转fresco磁盘资源id
     *
     * @param key
     * @return
     */
    public static String getResourceId(CacheKey key) {
        try {
            return SecureHashUtil.makeSHA1HashBase64(key.toString().getBytes("UTF-8"));
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * 图片所在的子文件夹
     *
     * @param resourceId
     * @return
     */
    public static String getImageDiskCacheSubDir(String resourceId) {
        // 版本文件夹
        String versiondir = String.format((Locale) null, "%s.ols%d.%d", new Object[]{"v2", Integer.valueOf(100),
                Integer.valueOf(1)});
        // 图片子文件夹
        String subdirectory = String.valueOf(Math.abs(resourceId.hashCode() % 100));
        // 图片所在文件夹的完整路径
        String ret = DISK_CACHE_DIR + versiondir + File.separator + subdirectory + File.separator;
        return ret;
    }

    public static String getImageDiskCachePath(String image_net_url) {
        try {
            CacheKey cachekey = getCacheKey_encoded(image_net_url);
            String frescoDiskResID = getResourceId(cachekey);
            File imageFile = new File(getImageDiskCacheSubDir(frescoDiskResID), frescoDiskResID + ".cnt");
            String path = imageFile.getAbsolutePath();
            return path;
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * 获取图片缓存地址
     *
     * @param image_net_url
     * @return
     */
    public static File getImageDiskCacheFile(String image_net_url) {
        try {
            CacheKey cachekey = getCacheKey_encoded(image_net_url);
            String frescoDiskResID = getResourceId(cachekey);
            File imageFile = new File(getImageDiskCacheSubDir(frescoDiskResID), frescoDiskResID + ".cnt");
            if (imageFile != null && imageFile.exists())
                return imageFile;
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * 控制Bitmap尺寸， 宽高最大边 不超过4096， 等比缩小bitmap, 在原bitmap对象上修改
     *
     * @param srcBitmap
     * @param needSize  预设大小 x是宽 y是高
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static void autoResizeBitmap(Bitmap srcBitmap, Point needSize) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT)
            return;

        if (srcBitmap == null || srcBitmap.isRecycled())
            return;
        int max = 4096;// 最大尺寸
        int src_w = srcBitmap.getWidth();// 原始宽度
        int src_h = srcBitmap.getHeight();// 原始高度
        float raito = 1.0F * src_w / src_h;// raito=宽/高
        int w = 0;
        int h = 0;
        if (needSize != null && needSize.x > 0 && needSize.y > 0) {
            // 如果有预设尺寸
            float tmp_raito = 1.0F * needSize.x / needSize.y;
            if (raito == tmp_raito) {
                w = needSize.x;
                h = needSize.y;
            } else {
                w = needSize.x;
                h = (int) (needSize.x / raito);
            }
        } else {
            if (src_w > src_h && src_w > max) {
                // 宽大于高并且宽大于最大值
                w = max;
                h = (int) (max / raito);
            } else if (src_h > src_w && src_h > max) {
                h = max;
                w = (int) (max * raito);

                int min_w = 240;
                if (w < min_w) {
                    int tmp_w = w;
                    w = src_w;
                    while (true) {
                        w = w / 2;
                        if (w < min_w) {
                            w = w * 2;
                            break;
                        }
                    }
                    if (w < src_w) {
                        h = (int) (w / raito);
                    } else {
                        w = tmp_w;
                    }

                }
            }
        }

        if (w > 0 && h > 0 && src_w > w && src_h > h && !srcBitmap.isRecycled()) {
            try {
                // 在原bitmap对象上进行修改
                Bitmap sampleBitmap = ImageUtils.zoomBitmap(srcBitmap, w, h);
                ByteBuffer bb = ByteBuffer.allocate(sampleBitmap.getByteCount());
                sampleBitmap.copyPixelsToBuffer(bb);
                bb.flip();
                srcBitmap.reconfigure(sampleBitmap.getWidth(), sampleBitmap.getHeight(), srcBitmap.getConfig());
                srcBitmap.copyPixelsFromBuffer(bb);
                sampleBitmap.recycle();
                sampleBitmap = null;
                bb = null;
                if (FrescoImageHelper.log)
                    KLog.i("Fresco", " autoResizeBitmap srcBitmap[" + src_w + "," + src_h + "] change to newBitmap["
                            + w + "," + h + "]");
            } catch (Throwable e) {
                // LogUtil.i("Fresco", " autoResizeBitmap srcBitmap[" + src_w + "," + src_h + "] change to newBitmap["
                // + w + "," + h + "]" + "---Exception:"+e.getMessage());
            }
        }
    }
}