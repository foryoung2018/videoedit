package com.wmlive.hhvideo.widget;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.Path.FillType;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.FrameLayout;

import cn.wmlive.hhvideo.R;


/**
 * 支持圆角的布局容器 四角半径可单独设置，可以在xml中设置<br>
 * 支持layout描边 需要设置宽度 颜色<br>
 * 理论上可以画各种形状的镂空····<br>
 * 尖角属性 可以想聊天气泡一样 多一个尖角出来<br>
 * 有些设置 在xml预览中 是不被支持的
 * <ul>
 * <br>
 * 圆角属性<br>
 * diy:corner_radius_all="10px"<br>
 * diy:corner_radius_bottomleft="40dp"<br>
 * diy:corner_radius_bottomright="30dp"<br>
 * diy:corner_radius_topleft="10dp"<br>
 * diy:corner_radius_topright="20dp" <br>
 * <br>
 * 描边属性<br>
 * diy:layout_stroke_width="3dp"<br>
 * diy:layout_stroke_color="#992eb089"<br>
 * <br>
 * 尖角属性，horn_direction、horn_width、horn_height同时设置才生效<br>
 * diy:horn_direction="left|top|right|bottom"<br>
 * diy:horn_position_offsize_left="30dp"<br>
 * diy:horn_position_offsize_top="30dp"<br>
 * diy:horn_position_offsize_right="30dp"<br>
 * diy:horn_position_offsize_bottom="30dp"<br>
 * diy:horn_width="10dp"<br>
 * diy:horn_height="15dp"<br>
 * </ul>
 *
 * @author CCCMAX
 */
public class RoundFrameLayout extends FrameLayout {
    private static final int LAYER_FLAGS = Canvas.MATRIX_SAVE_FLAG | Canvas.CLIP_SAVE_FLAG
            | Canvas.HAS_ALPHA_LAYER_SAVE_FLAG | Canvas.FULL_COLOR_LAYER_SAVE_FLAG
            | Canvas.CLIP_TO_LAYER_SAVE_FLAG;
    /**
     * 描边宽
     */
    private float stroke_width = 0;
    /**
     * 描边颜色
     */
    private int stroke_color = 0x0;

    /**
     * 圆角半径
     */
    private float Radius_topLeft = 0;
    private float Radius_topRight = 0;
    private float Radius_bottomRight = 0;
    private float Radius_bottomLeft = 0;

    // 圆角
    private float[] radii = {Radius_topLeft, Radius_topLeft, Radius_topRight, Radius_topRight, Radius_bottomRight,
            Radius_bottomRight, Radius_bottomLeft, Radius_bottomLeft};

    // 尖角 所在矩形的宽高
    private float horn_w = 0;
    private float horn_h = 0;
    /**
     * 尖角方向<br>
     * Gravity.TOP<br>
     * Gravity.LEFT<br>
     * Gravity.BOTTOM<br>
     * Gravity.RIGHT<br>
     */
    private int horn_direction = 0;
    /**
     * 尖角 上下左右偏移量
     */
    private Rect horn_position_offsize = new Rect();

    public RoundFrameLayout(Context context) {
        super(context);
        init(context, null, 0);
    }

    public RoundFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public RoundFrameLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }

    /**
     * 圆角画笔
     */
    Paint pt = new Paint();
    /**
     * 描边画笔
     */
    Paint paint_Stroke = new Paint();

    public void init(Context context, AttributeSet attrs, int defStyle) {
        if (this.getBackground() == null) {
            setBackgroundColor(Color.TRANSPARENT);
        }

        // 创建画笔 颜色 抗锯齿
        pt.setColor(Color.WHITE);
        pt.setDither(true);
        pt.setAntiAlias(true);
        pt.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));

        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RoundedLayout, defStyle, 0);
            if (a != null) {
                int radius = a.getDimensionPixelSize(R.styleable.RoundedLayout_corner_radius_all, -1);
                if (radius >= 0) {
                    setRoundedCornerRadius(radius);
                } else {
                    Radius_topLeft = a.getDimensionPixelSize(R.styleable.RoundedLayout_corner_radius_topleft, 0);
                    Radius_topRight = a.getDimensionPixelSize(R.styleable.RoundedLayout_corner_radius_topright, 0);
                    Radius_bottomRight = a
                            .getDimensionPixelSize(R.styleable.RoundedLayout_corner_radius_bottomright, 0);
                    Radius_bottomLeft = a.getDimensionPixelSize(R.styleable.RoundedLayout_corner_radius_bottomleft, 0);
                    setRoundedCornerRadius(Radius_topLeft, Radius_topRight, Radius_bottomLeft, Radius_bottomRight);
                }

                stroke_width = a.getDimensionPixelSize(R.styleable.RoundedLayout_layout_stroke_width, 0);
                stroke_color = a.getColor(R.styleable.RoundedLayout_layout_stroke_color, 0);

                // 尖角的方向
                String horn_direction_str = a.getString(R.styleable.RoundedLayout_horn_direction);
                if ("left".equals(horn_direction_str)) {
                    horn_direction = Gravity.LEFT;
                } else if ("top".equals(horn_direction_str)) {

                    horn_direction = Gravity.TOP;
                } else if ("right".equals(horn_direction_str)) {

                    horn_direction = Gravity.RIGHT;
                } else if ("bottom".equals(horn_direction_str)) {
                    horn_direction = Gravity.BOTTOM;
                }

                // 尖角位置
                horn_position_offsize.left = a.getDimensionPixelSize(
                        R.styleable.RoundedLayout_horn_position_offsize_left, 0);
                horn_position_offsize.top = a.getDimensionPixelSize(
                        R.styleable.RoundedLayout_horn_position_offsize_top, 0);
                horn_position_offsize.right = a.getDimensionPixelSize(
                        R.styleable.RoundedLayout_horn_position_offsize_right, 0);
                horn_position_offsize.bottom = a.getDimensionPixelSize(
                        R.styleable.RoundedLayout_horn_position_offsize_bottom, 0);
                // 尖角 宽高
                horn_w = a.getDimensionPixelSize(R.styleable.RoundedLayout_horn_width, 0);
                horn_h = a.getDimensionPixelSize(R.styleable.RoundedLayout_horn_height, 0);

            }
        }

        // 描边画笔设置
        paint_Stroke.setStyle(Paint.Style.STROKE);
        paint_Stroke.setStrokeCap(Paint.Cap.ROUND);
        paint_Stroke.setAntiAlias(true);
        paint_Stroke.setColor(stroke_color);
        paint_Stroke.setStrokeWidth(stroke_width);

    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
    }

    int w = 0;
    int h = 0;

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        w = getWidth();
        h = getHeight();
    }

    public void setStrokeWidth(float sw) {
        stroke_width = sw;
    }

    public void setStrokeColor(int color_ARGB) {
        stroke_color = color_ARGB;
        paint_Stroke.setColor(stroke_color);
    }

    /**
     * 圆角半径
     *
     * @param radius
     */
    public void setRoundedCornerRadius(float radius) {
        setRoundedCornerRadius(radius, radius, radius, radius);
    }

    /**
     * 圆角半径
     *
     * @param topLeft     左上
     * @param topRight    右上
     * @param bottomLeft  左下
     * @param bottomRight 右下
     */
    public void setRoundedCornerRadius(float topLeft, float topRight, float bottomLeft, float bottomRight) {
        Radius_topLeft = topLeft;
        Radius_topRight = topRight;
        Radius_bottomRight = bottomRight;
        Radius_bottomLeft = bottomLeft;

        radii = new float[]{Radius_topLeft, Radius_topLeft, Radius_topRight, Radius_topRight, Radius_bottomRight,
                Radius_bottomRight, Radius_bottomLeft, Radius_bottomLeft};
    }

    public float getRadius_TL() {
        return Radius_topLeft;
    }

    public float getRadius_TR() {
        return Radius_topRight;
    }

    public float getRadius_BL() {
        return Radius_bottomLeft;
    }

    public float getRadius_BR() {
        return Radius_bottomRight;
    }

    @SuppressLint("NewApi")
    @Override
    public void draw(Canvas canvas) {

        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.FILTER_BITMAP_FLAG | Paint.ANTI_ALIAS_FLAG));

        // Log.e("cccmax", "w=" + w + " h=" + h + "    canvas" + canvas.getWidth() + "," + canvas.getHeight());

        if (w == 0)
            w = canvas.getWidth();
        if (h == 0)
            h = canvas.getHeight();

        // 画布尺寸矩形
        RectF fullrect = new RectF(0, 0, w, h);
        // 路径
        Path pth = new Path();

        // 必须创建一个新的layer层 否则会把整个页面布局的背景色扣掉。。。
        canvas.saveLayerAlpha(fullrect, 0xFF, LAYER_FLAGS);
        // 路径叠加部分相减 A-B 剩下的是圆角的范围
        pth.setFillType(FillType.EVEN_ODD);
        pth.addRect(fullrect, Direction.CW);// A 矩形

        // B 圆角所在矩形
        RectF roundRect = new RectF(0, 0, w, h);
        // C 尖角所在矩形
        RectF horn_rect = null;
        Path horn_path = null;

        // 尖角位置、形状path
        if (horn_w > 0 && horn_h > 0) {
            switch (horn_direction) {
                case Gravity.LEFT: {
                    // 可视范围 左侧边缘缩进 留出尖角宽度
                    roundRect.left = horn_w;

                    horn_rect = new RectF(0, 0, horn_w, horn_h);
                    if (horn_position_offsize.top > 0) {
                        horn_rect.top = horn_position_offsize.top;
                        horn_rect.bottom = horn_position_offsize.top + horn_h;
                    } else if (horn_position_offsize.bottom > 0) {
                        horn_rect.bottom = h - horn_position_offsize.bottom;
                        horn_rect.top = horn_rect.bottom - horn_h;
                    } else {
                        horn_rect.top = (h - horn_h) / 2;
                        horn_rect.bottom = horn_rect.top + horn_h;
                    }

                    horn_path = new Path();
                    horn_path.moveTo(horn_rect.left, horn_rect.top + (horn_rect.bottom - horn_rect.top) / 2);// c1顶点
                    horn_path.lineTo(horn_rect.right, horn_rect.top);// c2
                    horn_path.lineTo(horn_rect.right, horn_rect.bottom);// c3
                    horn_path.close();
                }
                break;
                case Gravity.TOP: {
                    // 可视范围 上侧边缘缩进 留出尖角宽度
                    roundRect.top = horn_h;

                    horn_rect = new RectF(0, 0, horn_w, horn_h);
                    if (horn_position_offsize.left > 0) {
                        horn_rect.left = horn_position_offsize.left;
                        horn_rect.right = horn_position_offsize.left + horn_w;
                    } else if (horn_position_offsize.right > 0) {
                        horn_rect.right = w - horn_position_offsize.right;
                        horn_rect.left = horn_rect.right - horn_w;
                    } else {
                        horn_rect.left = (w - horn_w) / 2;
                        horn_rect.right = horn_rect.left + horn_w;
                    }

                    horn_path = new Path();
                    horn_path.moveTo(horn_rect.left + (horn_rect.right - horn_rect.left) / 2, horn_rect.top);// c1顶点
                    horn_path.lineTo(horn_rect.right, horn_rect.bottom);// c2
                    horn_path.lineTo(horn_rect.left, horn_rect.bottom);// c3
                    horn_path.close();
                }
                break;
                case Gravity.RIGHT: {
                    // 可视范围 右侧边缘缩进 留出尖角宽度
                    roundRect.right = roundRect.right - horn_w;

                    horn_rect = new RectF(roundRect.right, 0, w, horn_h);
                    if (horn_position_offsize.top > 0) {
                        horn_rect.top = horn_position_offsize.top;
                        horn_rect.bottom = horn_position_offsize.top + horn_h;
                    } else if (horn_position_offsize.bottom > 0) {
                        horn_rect.bottom = h - horn_position_offsize.bottom;
                        horn_rect.top = horn_rect.bottom - horn_h;
                    } else {
                        horn_rect.top = (h - horn_h) / 2;
                        horn_rect.bottom = horn_rect.top + horn_h;
                    }

                    horn_path = new Path();
                    horn_path.moveTo(horn_rect.right, horn_rect.top + (horn_rect.bottom - horn_rect.top) / 2);// c1顶点
                    horn_path.lineTo(horn_rect.left, horn_rect.top);// c2
                    horn_path.lineTo(horn_rect.left, horn_rect.bottom);// c3
                    horn_path.close();
                }
                break;
                case Gravity.BOTTOM: {
                    // 可视范围 下侧边缘缩进 留出尖角宽度
                    roundRect.bottom = roundRect.bottom - horn_h;
                    horn_rect = new RectF(0, roundRect.bottom, horn_w, h);
                    if (horn_position_offsize.left > 0) {
                        horn_rect.left = horn_position_offsize.left;
                        horn_rect.right = horn_position_offsize.left + horn_w;
                    } else if (horn_position_offsize.right > 0) {
                        horn_rect.right = w - horn_position_offsize.right;
                        horn_rect.left = horn_rect.right - horn_w;
                    } else {
                        horn_rect.left = (w - horn_w) / 2;
                        horn_rect.right = horn_rect.left + horn_w;
                    }

                    horn_path = new Path();
                    horn_path.moveTo(horn_rect.left + (horn_rect.right - horn_rect.left) / 2, horn_rect.bottom);// c1顶点
                    horn_path.lineTo(horn_rect.right, horn_rect.top);// c2
                    horn_path.lineTo(horn_rect.left, horn_rect.top);// c3
                    horn_path.close();
                }
                break;
            }
        }

        pth.addRoundRect(roundRect, radii, Direction.CCW);// B 圆角矩形
        // Log.e("cccmax", "RFL radii=" + radii[0]);

        // C 先判断有没有尖角矩形 有矩形位置 再画尖角
        if (horn_rect != null && horn_path != null) {
            if (isInEditMode()) {
                // pth.addRect(horn_rect, Direction.CCW);// C尖角矩形
            } else {
                pth.op(horn_path, Path.Op.DIFFERENCE);
            }
        }

        super.draw(canvas);

        if (horn_path != null) {
            drawStroke_horn(canvas, pth);
        } else {
            drawStroke(canvas, fullrect);// 绘制描边
        }

        canvas.drawPath(pth, pt);// pt设置排除模式、抗锯齿
        // 恢复状态 之前绘制在layer上的内容全都合并到canvas上
        canvas.restore();

        if (horn_path != null && isInEditMode()) {
            Paint peditmode = new Paint();
            peditmode.setColor(0x990099ff);
            canvas.drawPath(horn_path, peditmode);
        }

    }

    /**
     * 绘制描边
     *
     * @param canvas
     * @param fullrect
     */
    private void drawStroke(Canvas canvas, RectF fullrect) {
        if (stroke_width == 0 || stroke_color == 0x0)
            return;

        paint_Stroke.setStrokeWidth(stroke_width * 2);
        RectF stroke_rect = new RectF(fullrect);
        if (isInEditMode()) {
            stroke_rect.right -= 1;
            stroke_rect.bottom -= 1;
        }
        Path path_stroke = new Path();
        path_stroke.addRoundRect(stroke_rect, radii, Direction.CCW);
        canvas.drawPath(path_stroke, paint_Stroke);

        // 打印 路径点
        // int total = 20;
        // ArrayList<Object> pointList = new ArrayList<Object>();
        // PathMeasure pm = new PathMeasure(path_stroke, false);
        // float length = pm.getLength();
        // float distance = 0f;
        // float speed = length / total;
        // int counter = 0;
        // float[] aCoordinates = new float[2];
        // while ((distance < length) && (counter < total)) {
        // pm.getPosTan(distance, aCoordinates, null);
        // pointList.add(aCoordinates);
        // counter++;
        // distance = distance + speed;
        // aCoordinates = new float[2];
        // }
        // String sb = "";
        // for (Object o : pointList) {
        // float[] aaa = (float[]) o;
        // sb += "" + aaa[0] + "," + aaa[1] + "|";
        // }
        // Log.e("cccmax", "RFL path = " + sb);
    }

    /**
     * 绘制描边 xml预览模式会有差异
     *
     * @param canvas
     * @param fullrect
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void drawStroke_horn(Canvas canvas, Path pth) {
        if (stroke_width == 0 || stroke_color == 0x0)
            return;
        paint_Stroke.setStrokeWidth(stroke_width * 2);

        Path path_stroke = new Path();
        path_stroke.setFillType(FillType.EVEN_ODD);
        path_stroke.addRect(new RectF(0, 0, w, h), Direction.CW);
        path_stroke.op(pth, Path.Op.DIFFERENCE);
        canvas.drawPath(path_stroke, paint_Stroke);
    }
}
