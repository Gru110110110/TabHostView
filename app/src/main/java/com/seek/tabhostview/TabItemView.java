package com.seek.tabhostview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.TextView;

/**
 * Created by ASUS on 2016/4/26.
 */
public class TabItemView extends TextView {

    private static final int[] CHECKED_STATE_SET = {
            android.R.attr.state_checked
    };
    private static final float DEFAULT_MSG_TEXT_SIZE = 12;
    private static final float DEFAULT_MSG_PADDING = 3;
    private static final float DEFAULT_POINT_RADIUS = 5;

    private boolean checked;

    private CircleDrawable circleDrawable;

    private MessageDrawable messageDrawable;

    public TabItemView(Context context) {
        this(context, null);
    }

    public TabItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setGravity(Gravity.CENTER_HORIZONTAL);

        int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_MSG_PADDING,
                getResources()
                        .getDisplayMetrics());
        int textSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, DEFAULT_MSG_TEXT_SIZE, getResources()
                .getDisplayMetrics());
        messageDrawable = new MessageDrawable(Color.RED, padding, 0, textSize);
        int radius = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_POINT_RADIUS, getResources()
                .getDisplayMetrics());
        circleDrawable = new CircleDrawable(Color.RED, radius);
    }

    @Override
    protected int[] onCreateDrawableState(int extraSpace) {
        final int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
        if (isChecked()) {
            mergeDrawableStates(drawableState, CHECKED_STATE_SET);
        }
        return drawableState;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        if (this.checked != checked) {
            this.checked = checked;
            refreshDrawableState();
        }
    }

    public void drawCirclePoint() {
        circleDrawable.setShow(true);
        invalidate();
    }

    public void clearCirclePoint() {
        circleDrawable.setShow(false);
        invalidate();
    }

    public void drawMsg(int msgCount) {
        if (msgCount < 0) throw new IllegalArgumentException("message count can't be a negative number");
        messageDrawable.setMsgCount(msgCount);
        invalidate();
    }

    public void setMsgTextSize(float msgTextSize) {
        messageDrawable.setMsgTextSize(msgTextSize);
    }

    public void setMsgBackgroundColor(int backgroundColor) {
        messageDrawable.setBackgroundColor(backgroundColor);
    }

    public void setMsgPadding(float padding) {
        messageDrawable.setPadding(padding);
    }

    public void setMsgTextColor(int msgTextColor) {
        messageDrawable.setMsgTextColor(msgTextColor);
    }

    public void setCirclePointColor(int circleBackgroundColor) {
        circleDrawable.setBackgroundColor(circleBackgroundColor);
    }

    public void setCirclePointRadius(float radius) {
        circleDrawable.setRadius(radius);
    }

    public void setDrawable(Drawable drawable) {
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        setCompoundDrawables(null, drawable, null, null);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawCirclePoint(canvas);
        drawMsgCount(canvas);
    }

    private void drawMsgCount(Canvas canvas) {
        if (messageDrawable.getMsgCount() > 0) {
            canvas.save();
            Drawable top = getCompoundDrawables()[1];
            int dx = getWidth();
            int dy = getPaddingTop();
            if (top != null) {
                dx += top.getIntrinsicWidth();
            }
            dx = (int) ((dx >> 1) - messageDrawable.getRadius());
            canvas.translate(dx, dy);
            messageDrawable.draw(canvas);
            canvas.restore();
        }
    }

    private void drawCirclePoint(Canvas canvas) {
        int circlePointRadius = (int) circleDrawable.getRadius();
        if (circlePointRadius >= 0) {
            canvas.save();
            Drawable top = getCompoundDrawables()[1];
            int dx = getWidth();
            int dy = getPaddingTop();
            if (top != null) {
                dx += top.getIntrinsicWidth();
            }
            dx = (dx >> 1) - circlePointRadius;
            dy = dy + circlePointRadius;
            canvas.translate(dx, dy);
            circleDrawable.draw(canvas);
            canvas.restore();
        }
    }
}
