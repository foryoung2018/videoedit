package com.wmlive.hhvideo.heihei.record.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import cn.wmlive.hhvideo.R;

public class IncreaseDecreaseView extends View {

    private Context mContext;
    private boolean isMeasured;
    private int width,height;
    private boolean isPlus;

    public IncreaseDecreaseView(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public IncreaseDecreaseView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        TypedArray tArray = context.obtainStyledAttributes(attrs,
                R.styleable.IncreaseDecreaseView);

        isPlus = tArray.getBoolean(R.styleable.IncreaseDecreaseView_isplus,
                false);

        tArray.recycle();
        init();
    }

    public IncreaseDecreaseView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    private void init(){
        mPaint = new Paint();

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (!isMeasured) {
            width = getMeasuredWidth();
            height = getMeasuredHeight();
            isMeasured = true;
        }
    }

    Paint mPaint;
    @Override
    protected void onDraw(Canvas canvas) {
        //画笔
        mPaint.setColor(Color.parseColor("#33ffffff"));

        if(isPlus){
            canvas.drawLine(width/2, 0,width/2,height,mPaint);
            canvas.drawLine(0, height/2,width,height/2,mPaint);
        }else{
            canvas.drawLine(0, height/2,width,height/2,mPaint);
        }
        super.onDraw(canvas);
    }



}
