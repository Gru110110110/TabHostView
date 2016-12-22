package com.seek.tabhostview;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

/**
 * Created by ASUS on 2016/4/25.
 */
public class CircleDrawable extends Drawable {

    private Paint mPaint;
    private int backgroundColor;
    private float radius;
    private RectF rf;
    private boolean show;

    public CircleDrawable(int backgroundColor) {
        super();
        this.backgroundColor = backgroundColor;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(backgroundColor);
        rf = new RectF();
    }

    public CircleDrawable(int backgroundColor, float radius) {
        this(backgroundColor);
        this.radius = radius;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
        mPaint.setColor(backgroundColor);
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    @Override
    public void draw(Canvas canvas) {
        if ((backgroundColor >>> 24) != 0 && radius > 0 && show) {
            if (rf.isEmpty()) {
                rf.set(getBounds());
            }
            canvas.drawCircle(rf.left + radius, rf.top + radius, radius,
                    mPaint);
        }
    }

    @Override
    public void setAlpha(int alpha) {
        mPaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter filter) {
        mPaint.setColorFilter(filter);
    }

    @Override
    public int getOpacity() {
        switch (backgroundColor >>> 24) {
            case 255:
                return PixelFormat.OPAQUE;
            case 0:
                return PixelFormat.TRANSPARENT;
        }
        return PixelFormat.TRANSLUCENT;
    }

    public void setShow(boolean show) {
        this.show = show;
    }
}