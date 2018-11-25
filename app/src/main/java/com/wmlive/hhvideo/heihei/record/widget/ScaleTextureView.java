package com.wmlive.hhvideo.heihei.record.widget;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;


/**
 * Author：create by admin on 2018/11/17 11:43
 * Email：haitian.jiang@welines.cn
 */
public class ScaleTextureView extends TextureView {

    private static final String TAG = ScaleTextureView.class.getSimpleName() ;
    private ScaleGestureDetector mScaleGestureDetector = null;
    private GestureDetector mGestureDetector = null;
    private ScaleGestureListener mScaleGestureListener = null;
    private ScrollGestureListener mScrollGestureListener = null;
    private float rectWidth, rectHeight , maxTranslateWidth,maxTranslateHeight;

    private ViewGroup wrapper;

    private float maxScale = 2.0f;
    private  Context context;
    private boolean isScaleEnd = true;
    private OnTextureViewChangeListener listener;

    public ScaleTextureView(Context context) {
        super(context);
        this.context = context;
        initView();
    }

    public ScaleTextureView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initView();
    }

    public ScaleTextureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initView();
    }

    private void initView() {
        mScaleGestureListener = new ScaleGestureListener();
        mScrollGestureListener = new ScrollGestureListener();

        mScaleGestureDetector = new ScaleGestureDetector(context,mScaleGestureListener);  //缩放监听
        mGestureDetector = new GestureDetector(context,mScrollGestureListener);           //移动监听
    }

    public void setWrapper(ViewGroup v) {
        wrapper = v;
        wrapper.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(listener!=null){
                    listener.OnTextureViewChange();
                }
                Log.d(TAG, "onTouchEvent() called with: event.getPointerCount() = [" + event.getPointerCount() + "]");
                if (event.getPointerCount() == 1&& isScaleEnd ) {
                    return mGestureDetector.onTouchEvent(event);
                }else if(event.getPointerCount() == 2||!isScaleEnd){
                    isScaleEnd = event.getAction() == MotionEvent.ACTION_UP;

                    if (isScaleEnd) {
                        mScaleGestureListener.onActionUp();
                    }
                    return mScaleGestureDetector.onTouchEvent(event);
                }

                return true;
            }
        });
    }

    public void log() {
        Log.d(TAG, "log() called" + " width:"+getWidth() +" height:"+getHeight() +" scale:"+getScaleX()  + " translatex: " + getTranslationX()+ "  translatey: " + getTranslationY());
    }

    public void setTranslateLimit(float rectWidth, float rectHeight) {
        this.rectWidth = rectWidth;
        this.rectHeight = rectHeight;

    }

    public void reset() {
        setTranslationX(0);
        setTranslationY(0);
        setScaleX(1);
        setScaleY(1);
    }

    public class ScrollGestureListener extends GestureDetector.SimpleOnGestureListener{

        private float translateX;
        private float translateY;

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {


            distanceX = -distanceX;
            distanceY = -distanceY;

            translateX += distanceX;
            translateY += distanceY;
            checkTranslate();

            return super.onScroll(e1, e2, distanceX, distanceY);
        }

        private void checkTranslate() {
            float angel = Math.abs(getRotation()/90%4);

            if(angel%2==0){
                maxTranslateWidth = (getWidth()*getScaleX() - rectWidth)/2;
                maxTranslateHeight = (getHeight()*getScaleY() - rectHeight)/2;
            }else {
                maxTranslateHeight = (getWidth()*getScaleX() - rectWidth)/2;
                maxTranslateWidth = (getHeight()*getScaleY() - rectHeight)/2;
            }


            if(Math.abs(translateX)>=maxTranslateWidth){
                translateX = translateX >=0?maxTranslateWidth:-maxTranslateWidth;

            }

            if(Math.abs(translateY)>=maxTranslateHeight){
                translateY = translateY >=0?maxTranslateHeight:-maxTranslateHeight;
            }

            Log.d(TAG, "onScroll() getRotation(): " + getRotation());


            if(angel==1){
                setTranslationX(translateX);
                setTranslationY(translateY);
            }else  if(angel==2){
                setTranslationX(translateX);
                setTranslationY(translateY);
            }else if (angel==3){
                setTranslationX(translateX);
                setTranslationY(translateY);
            }else {
                setTranslationX(translateX);
                setTranslationY(translateY);
            }
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }
    }




    public class ScaleGestureListener implements ScaleGestureDetector.OnScaleGestureListener{

        private float scale = 1;


        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            scale*=detector.getScaleFactor();
            if(scale<=1){
//                scale=1;
            }

            if(scale>=maxScale){
                scale=maxScale;
            }


            setScaleX(scale);
            setScaleY(scale);
            return true;
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
        }

        public void onActionUp() {
            if(scale<1){
                scale = 1;
                setScaleX(scale);
                setScaleY(scale);
            }
            mScrollGestureListener.checkTranslate();
        }
    }

    public void setOnTextureViewChangeListener(OnTextureViewChangeListener listener) {
        this.listener = listener;

    }


    public interface OnTextureViewChangeListener {
        void OnTextureViewChange();
    }

}
