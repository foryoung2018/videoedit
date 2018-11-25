package com.wmlive.hhvideo.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.wmlive.hhvideo.utils.KLog;

import cn.wmlive.hhvideo.R;

/**
 * Created by Administrator on 2018/6/14.
 */

@SuppressLint("AppCompatCustomView")
public class RatioFrameLayout extends ImageView {

    private boolean isMeasured;
    private int swidth,sheight;
    private float mRatio;
    private int width,height;

    public RatioFrameLayout(@NonNull Context context) {
        super(context);
    }

    public RatioFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
        TypedArray tArray = context.obtainStyledAttributes(attrs,
                R.styleable.RatioFrameLayout);

        mRatio = tArray.getFloat(R.styleable.RatioFrameLayout_frameLayoutratio,
                1);

        tArray.recycle();
        swidth = getScreenWidth(context);
    }

    public RatioFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
    }


    @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mRatio > 0) { // 高度已知，根据比例，设置宽度
            int height = MeasureSpec.getSize(heightMeasureSpec);
            int width =(int) (height * mRatio);

            if(width>swidth){
                width=swidth;
                height=(int)((width+0.f)/mRatio);
            }


            this.width = width;
            this.height = height;
            super.onMeasure(MeasureSpec.makeMeasureSpec(
                    (int) (width), MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(height+200, MeasureSpec.EXACTLY));
        }
    }


    public void setRatio(float ratio) {
        this.mRatio = ratio;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Drawable drawable = getDrawable();
        Bitmap b = ((BitmapDrawable) drawable).getBitmap();
        Bitmap bitmap = b.copy(Bitmap.Config.ARGB_8888, true);
//        canvas.drawBitmap(bitmap,null,new Rect(0,0,width,height),null);
    }

    /**
     * 获取屏幕高度
     *
     * @param context
     */
    private int getScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        return wm.getDefaultDisplay().getWidth();
    }

}
