package com.wmlive.hhvideo.heihei.record.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import com.facebook.drawee.view.SimpleDraweeView;

import cn.wmlive.hhvideo.R;

public class CircleAnimationView extends SimpleDraweeView {

    private static final ScaleType SCALE_TYPE = ScaleType.CENTER_CROP;

    private static final Bitmap.Config BITMAP_CONFIG = Bitmap.Config.ARGB_8888;
    private static final int COLORDRAWABLE_DIMENSION = 1;

    private static final int DEFAULT_BORDER_WIDTH = 0;
    private static final int DEFAULT_BORDER_COLOR = Color.BLACK;

    private final RectF mDrawableRect = new RectF();
    private final RectF mBorderRect = new RectF();

    private final Matrix mShaderMatrix = new Matrix();
    private final Paint mBitmapPaint = new Paint();
    private final Paint mBorderPaint = new Paint();
    private Paint mShadowPaint;

    private int mBorderColor = DEFAULT_BORDER_COLOR;
    private int mBorderWidth = DEFAULT_BORDER_WIDTH;

    private Bitmap mBitmap;
    private BitmapShader mBitmapShader;
    private int mBitmapWidth;
    private int mBitmapHeight;

    private float mDrawableRadius;
    private float mBorderRadius;
    private Drawable mDrawableSelected;
    private Drawable mDrawableLoading;

    private boolean mReady;
    private boolean mSetupPending;

    private boolean isChecked;
    private boolean isLoading = false;
    private boolean showShadow = false;

    private int mIconWidth, mIconHeight;

    public CircleAnimationView(Context context) {
        super(context);
    }

    public CircleAnimationView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleAnimationView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        super.setScaleType(SCALE_TYPE);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CircleAnimationView, defStyle, 0);

        mBorderWidth = DEFAULT_BORDER_WIDTH;
        mBorderColor = DEFAULT_BORDER_COLOR;
        mDrawableLoading = a.getDrawable(R.styleable.CircleAnimationView_loadingSrc);
        a.recycle();

        mIconWidth = (int) getResources().getDimension(R.dimen.dimen_40);
        mIconHeight = (int) getResources().getDimension(R.dimen.dimen_40);

        mReady = true;
        mDrawableSelected = getResources().getDrawable(R.drawable.effect_time_selected);
        if (mSetupPending) {
            setup();
            mSetupPending = false;
        }
    }

    @Override
    public ScaleType getScaleType() {
        return SCALE_TYPE;
    }

    @Override
    public void setScaleType(ScaleType scaleType) {
        if (scaleType != SCALE_TYPE) {
            throw new IllegalArgumentException(String.format("ScaleType %s not supported.", scaleType));
        }
    }

    public void setLoading() {
        isLoading = true;
    }

    public void cancelLoading() {
        isLoading = false;
        invalidate();
    }

    public boolean isLoading() {
        return isLoading;
    }

    public void showShadow(int color) {
        showShadow = true;
        if (mShadowPaint == null) {
            mShadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        }
        mShadowPaint.setColor(color);
        mShadowPaint.setStyle(Paint.Style.FILL);
    }

    private int mLoadingLevel = 0;

    public void cancelShadow() {
        showShadow = false;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (getTopLevelDrawable() == null) {
            return;
        }

        int selectLeft = mIconWidth - mDrawableSelected.getIntrinsicWidth();
        int selectTop = mIconHeight - mDrawableSelected.getIntrinsicHeight();
        mBitmap = getBitmapFromDrawable(getTopLevelDrawable());
        if (mBitmap != null) {
            setup();
        }

        canvas.drawCircle(getWidth() / 2, getHeight() / 2, mDrawableRadius, mBitmapPaint);
        if (showShadow && mShadowPaint != null) {
            canvas.drawCircle(getWidth() / 2, getHeight() / 2, mDrawableRadius, mShadowPaint);
        }

        if (isLoading && mDrawableLoading != null) {
            int loadLeft = (mIconWidth - mDrawableLoading.getIntrinsicWidth()) / 2;
            int loadTop = (mIconHeight - mDrawableLoading.getIntrinsicHeight()) / 2;
            mDrawableLoading.setBounds(loadLeft, loadTop, loadLeft + mDrawableLoading.getIntrinsicWidth(),
                    loadTop + mDrawableLoading.getIntrinsicHeight());
            mDrawableLoading.draw(canvas);
            mDrawableLoading.setLevel(mLoadingLevel += 100);
        } else {
            if (isChecked()) {
                mDrawableSelected.setBounds(selectLeft, selectTop, selectLeft + mDrawableSelected.getIntrinsicWidth(),
                        selectTop + mDrawableSelected.getIntrinsicHeight());
                mDrawableSelected.draw(canvas);
            }
        }
    }

    public void setChecked(boolean check) {
        isChecked = check;
    }

    public boolean isChecked() {
        return isChecked;
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        setup();
    }

    public int getBorderColor() {
        return mBorderColor;
    }

    public void setBorderColor(int borderColor) {
        if (borderColor == mBorderColor) {
            return;
        }

        mBorderColor = borderColor;
        mBorderPaint.setColor(mBorderColor);
        invalidate();
    }

    public int getBorderWidth() {
        return mBorderWidth;
    }

    public void setBorderWidth(int borderWidth) {
        if (borderWidth == mBorderWidth) {
            return;
        }

        mBorderWidth = borderWidth;
        setup();
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        mBitmap = bm;
        setup();
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        mBitmap = getBitmapFromDrawable(drawable);
        setup();
    }


    @Override
    public void setImageResource(int resId) {
        super.setImageResource(resId);
        mBitmap = getBitmapFromDrawable(getDrawable());
        setup();
    }

    private Bitmap getBitmapFromDrawable(Drawable drawable) {
        if (drawable == null) {
            return null;
        }

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        try {
            Bitmap bitmap;

            if (drawable instanceof ColorDrawable) {
                bitmap = Bitmap.createBitmap(COLORDRAWABLE_DIMENSION, COLORDRAWABLE_DIMENSION, BITMAP_CONFIG);
            } else {
                bitmap = Bitmap.createBitmap(mIconWidth, mIconHeight,
                        drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
            }

            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, mIconWidth, mIconHeight);
            drawable.draw(canvas);
            return bitmap;
        } catch (OutOfMemoryError e) {
            return null;
        }
    }

    private void setup() {
        if (!mReady) {
            mSetupPending = true;
            return;
        }

        if (mBitmap == null) {
            return;
        }

        mBitmapShader = new BitmapShader(mBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);

        mBitmapPaint.setAntiAlias(true);
        mBitmapPaint.setShader(mBitmapShader);

        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setAntiAlias(true);
        mBorderPaint.setColor(mBorderColor);
        mBorderPaint.setStrokeWidth(mBorderWidth);

        mBitmapHeight = mBitmap.getHeight();
        mBitmapWidth = mBitmap.getWidth();

        mBorderRect.set(0, 0, getWidth(), getHeight());
        mBorderRadius = Math.min((mBorderRect.height() - mBorderWidth) / 2, (mBorderRect.width() - mBorderWidth) / 2);

        mDrawableRect.set(mBorderWidth, mBorderWidth, mBorderRect.width() - mBorderWidth, mBorderRect.height() - mBorderWidth);
        mDrawableRadius = Math.min(mDrawableRect.height() / 2, mDrawableRect.width() / 2);

        updateShaderMatrix();
        invalidate();
    }

    private void updateShaderMatrix() {
        float scale;
        float dx = 0;
        float dy = 0;

        mShaderMatrix.set(null);

        if (mBitmapWidth * mDrawableRect.height() > mDrawableRect.width() * mBitmapHeight) {
            scale = mDrawableRect.height() / (float) mBitmapHeight;
            dx = (mDrawableRect.width() - mBitmapWidth * scale) * 0.5f;
        } else {
            scale = mDrawableRect.width() / (float) mBitmapWidth;
            dy = (mDrawableRect.height() - mBitmapHeight * scale) * 0.5f;
        }

        mShaderMatrix.setScale(scale, scale);
        mShaderMatrix.postTranslate((int) (dx + 0.5f) + mBorderWidth, (int) (dy + 0.5f) + mBorderWidth);

        mBitmapShader.setLocalMatrix(mShaderMatrix);
    }

}