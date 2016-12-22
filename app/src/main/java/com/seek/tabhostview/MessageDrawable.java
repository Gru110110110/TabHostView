package com.seek.tabhostview;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;

/**
 * Created by seek on 2016/12/21.
 */

public class MessageDrawable extends Drawable {

    private Paint mPaint;
    private int backgroundColor;
    private float padding;
    private TextPaint mTextPaint;
    private int msgCount;
    private float radius;
    private float fontHeight;
    private float fontWidth;

    public MessageDrawable(int backgroundColor, float padding, int msgCount, float msgTextSize) {
        this.backgroundColor = backgroundColor;
        this.padding = padding;
        this.msgCount = msgCount;
        initPaint(backgroundColor, msgTextSize);
        calculateMsgValue(padding);
    }

    private void initPaint(int backgroundColor, float msgTextSize) {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(backgroundColor);
        mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextSize(msgTextSize);
        mTextPaint.setColor(Color.WHITE);
    }

    private void calculateMsgValue(float padding) {
        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        fontHeight = (fontMetrics.leading + fontMetrics.ascent + fontMetrics.descent);
        fontWidth = mTextPaint.measureText("99");
        float maxValue = Math.max(fontHeight, fontWidth);
        radius = maxValue / 2 + padding;
    }

    @Override
    public void draw(Canvas canvas) {
        drawMsgCount(canvas, msgCount);
    }

    public float getRadius() {
        return radius;
    }

    private void drawMsgCount(Canvas canvas, int msgCount) {
        String msgText = String.valueOf(msgCount);
        float msgWidth = mTextPaint.measureText(msgText);

        Rect rect = getBounds();
        if (msgCount <= 99) {
            float cx = rect.left + radius;
            float cy = rect.top + radius;
            canvas.drawCircle(cx, cy, radius, mPaint);
            canvas.drawText(msgText, cx - msgWidth / 2, cy - fontHeight / 2, mTextPaint);
        } else {
            msgText = "99+";
            float rw = rect.left + msgWidth + padding * 2;
            float rh = rect.top + radius * 2;
            RectF rectF = new RectF(rect.left, rect.top, rw, rh);
            canvas.drawRoundRect(rectF, radius, radius, mPaint);
            canvas.drawText(msgText, (rw - msgWidth) / 2, rect.top + radius -
                    fontHeight / 2, mTextPaint);
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

    public void setBackgroundColor(int backgroundColor) {
        mPaint.setColor(backgroundColor);
    }

    public void setPadding(float padding) {
        this.padding = padding;
        calculateMsgValue(padding);
    }

    public void setMsgTextSize(float msgTextSize) {
        mTextPaint.setTextSize(msgTextSize);
        calculateMsgValue(padding);
    }

    public void setMsgTextColor(int msgTextColor) {
        mTextPaint.setColor(msgTextColor);
    }

    public int getMsgCount() {
        return msgCount;
    }

    public void setMsgCount(int msgCount) {
        this.msgCount = msgCount;
    }
}
