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
public class CircleBg extends Drawable {

    private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private int bgcolor;
    private boolean isRectangle = false;
    private float radius;

    public CircleBg(int bgcolor) {
        super();
        this.bgcolor = bgcolor;
        mPaint.setColor(bgcolor);
    }

    public CircleBg(int bgcolor, boolean rectangle) {
        this(bgcolor);
        isRectangle = rectangle;
    }

    public CircleBg(int bgcolor, float radius) {
        this(bgcolor);
        this.radius = radius;
    }

    @Override
    public void draw(Canvas canvas) {
        if ((bgcolor >>> 24) != 0) {
            mPaint.setColor(bgcolor);
            RectF rf = new RectF(getBounds());
            if (isRectangle) {
                canvas.drawColor(bgcolor);
            } else {
                canvas.drawCircle(rf.centerX(), rf.centerY(), rf.centerX(),
                        mPaint);
            }
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
        switch (bgcolor >>> 24) {
            case 255:
                return PixelFormat.OPAQUE;
            case 0:
                return PixelFormat.TRANSPARENT;
        }
        return PixelFormat.TRANSLUCENT;
    }
}