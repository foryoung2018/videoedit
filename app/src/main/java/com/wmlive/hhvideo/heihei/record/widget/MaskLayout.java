package com.wmlive.hhvideo.heihei.record.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import cn.wmlive.hhvideo.R;


/**
 * Created by Administrator on 2018/6/14.
 */

@SuppressLint("AppCompatCustomView")
public class MaskLayout extends View {

    private int swidth,sheight;
    private float mRatio;
    private Context context;
    private int width,height;
    private int dpi;
    private int vectPadding;
    private int maskColor = 0x33000000;

    public MaskLayout(@NonNull Context context) {
        super(context);
    }

    public MaskLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
        TypedArray tArray = context.obtainStyledAttributes(attrs,
                R.styleable.MaskLayout);

        mRatio = tArray.getFloat(R.styleable.MaskLayout_maskLayoutratio,
                1);
        vectPadding = tArray.getInteger(R.styleable.MaskLayout_maskLayoutvertPadding,
                0);
        maskColor = tArray.getColor(R.styleable.MaskLayout_maskLayoutColor,maskColor);
        tArray.recycle();
        swidth = getScreenWidth(context);
    }

    public MaskLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    private void init(){
        dpi = (int) context.getResources().getDimension(R.dimen.t1dp);
        paint = new Paint();
        paint.setColor(Color.BLUE);
        paint.setAntiAlias(true);
    }

    private  int left ,rigth ,top,bottom;
    private int rectWidth, rectHeight;
    private int totalWidth,totalHeight;
    Paint paint;


    @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        totalHeight =MeasureSpec.getSize(heightMeasureSpec);
        totalWidth =MeasureSpec.getSize(widthMeasureSpec);
        if (mRatio > 0) { // 高度已知，根据比例，设置宽度

            int height = MeasureSpec.getSize(heightMeasureSpec)- dpi*vectPadding*2;
            int width =(int) (height * mRatio);

            if(width>swidth){
                width=swidth;
                height=(int)((width+0.f)/mRatio);
            }
            rectWidth = width;
            rectHeight = height;

            Log.d("rec", "onMeasure() called with: rectWidth = [" + rectWidth + "], rectHeight = [" + rectHeight + "]");
        }
        super.onMeasure(widthMeasureSpec,heightMeasureSpec);

//        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        RectF srcRect = new RectF(0,0,totalWidth, totalHeight);
        left = totalWidth/2-rectWidth/2;
        rigth =  totalWidth/2+rectWidth/2;
        top = totalHeight/2-rectHeight/2;
        bottom = totalHeight/2 + rectHeight/2;
        RectF dstRect = new RectF(left,top,rigth, bottom);
        RectF mapRect = new RectF(left-dpi*3,top-dpi*3,rigth+dpi*4, bottom+dpi*4);
        Drawable drawable = getResources().getDrawable(R.drawable.icon_video_upload_tailor_border);
        Bitmap bitmap = drawable2Bitmap(drawable);
        canvas.drawBitmap(bitmap,null,mapRect,null);
        Log.d("rect", "onDraw() called with: srcRect = [" + srcRect + "]");
        Log.d("rect", "onDraw() called with: dstRect = [" + dstRect + "]");
        canvas.clipRect(srcRect);
        canvas.clipRect(dstRect, Region.Op.XOR);
        canvas.clipRect(srcRect);
        canvas.drawColor(maskColor);
        super.onDraw(canvas);

    }

    public void setRatio(float ratio) {
        this.mRatio = ratio;
    }

    /**
     * 获取屏幕高度
     *
     * @param context
     */
    private int getScreenWidth(Context context) {
        int widthPixels = context.getResources().getDisplayMetrics().widthPixels;
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        return widthPixels;
    }


    public  Drawable bitmap2Drawable(int resId){
        Drawable d = context.getResources().getDrawable(resId);
        return d;
    }

    /**
     * bitmap to drawable
     * @param bitmap
     * @return
     */
    public static Drawable bitmap2Drawable(Bitmap bitmap){
        return new BitmapDrawable(bitmap);
    }


    /**
     * drawable to bitmap
     * @param drawable
     * @return
     */
    public  Bitmap drawable2Bitmap(Drawable drawable){
        if(drawable instanceof BitmapDrawable){//转换成Bitmap
            return ((BitmapDrawable)drawable).getBitmap() ;
        }else if(drawable instanceof NinePatchDrawable){//.9图片转换成Bitmap
            Bitmap bitmap = Bitmap.createBitmap(
                    drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight(),
                    drawable.getOpacity() != PixelFormat.OPAQUE ?
                            Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight());
            drawable.draw(canvas);
            return bitmap;
        }else{
            return null ;
        }
    }


    public int[] getRectSize() {
        int [] size = new int[]{rectWidth,rectHeight};
        return size;
    }
}
