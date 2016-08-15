package com.seek.tabhostview;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.telecom.PhoneAccount;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by ASUS on 2016/4/26.
 */
public class TabItemView extends LinearLayout{

    private int[] addAttrs = new int[]{R.attr.state_choose};

    private boolean choose;

    private TextView tabTextView;

    private ImageView tabImageView;

    private boolean point = false;

    private int pointRadius = 8;

    private Paint mTextPaint;

    private Paint mPaint;

    private int msgCount;

    private RectF ovalText;


    public void setShowMode(ShowMode showMode) {
        this.showMode = showMode;
        showByMode(showMode);
    }

    private ShowMode showMode = ShowMode.NORMAL;

    public TabItemView(Context context) {
        this(context, null);
    }

    public TabItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setWillNotDraw(false);
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER);
        initChildView();
        initPaint();
    }

    private void initPaint() {
        mPaint  = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.RED);
        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(Color.WHITE);
        mTextPaint.setTextSize(18);
        ovalText = new RectF();
    }

    private void initChildView() {
        // add icon
        tabImageView = new ImageView(getContext());
        tabImageView.setDuplicateParentStateEnabled(true);
        addView(tabImageView,new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
        // add text
        tabTextView = new TextView(getContext());
        tabTextView.setGravity(Gravity.CENTER);
        tabTextView.setSingleLine();
        tabTextView.setDuplicateParentStateEnabled(true);
        addView(tabTextView);
    }

    /**
     * if true ,show the red point
     * @param point
     */
    public void setPoint(boolean point) {
        this.point = point;
        invalidate();
    }

    public void setPoint() {
        msgCount = 0;
        setPoint(true);
    }

    public void setMsgCount(int msgCount){
        this.msgCount = msgCount;
        setPoint(false);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        drawPoint(canvas);
        drawMsgCount(canvas,msgCount);
    }

    private void drawPoint(Canvas canvas) {
        if (point) {
            canvas.save();
            canvas.drawCircle(getWidth() / 2 + tabImageView.getWidth() / 2, getHeight() / 2 - tabImageView.getHeight() / 2, pointRadius, mPaint);
            canvas.restore();
        }
    }

    private void drawMsgCount(Canvas canvas,int msgCount){
        if (msgCount<0) throw new IllegalArgumentException("message count can't be a negative number");
        if (msgCount==0) return;
        String msgText = String.valueOf(msgCount);
        if (msgCount>99) msgText = "99+";
        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        float fontHeight = (fontMetrics.leading+fontMetrics.ascent+fontMetrics.descent);
        float fontWidth = mTextPaint.measureText("9");
        fontWidth = fontWidth>fontHeight?fontWidth:fontWidth;
        canvas.drawCircle(getWidth() / 2 + tabImageView.getWidth() / 2, getHeight() / 2 - tabImageView.getHeight() / 2, fontWidth*1.4f, mPaint);
        fontWidth = mTextPaint.measureText(msgText);
        canvas.drawText(msgText,(getWidth() / 2 + tabImageView.getWidth() / 2-fontWidth/2),(getHeight() / 2 - tabImageView.getHeight() / 2-fontHeight/2),mTextPaint);
    }

    public void setTextSize(float textSize) {
        tabTextView.setTextSize(textSize);
    }

    public void setIconResId(@DrawableRes int resId) {
        tabImageView.setImageResource(resId);
    }

    public void setIconDrawable(Drawable drawable) {
        tabImageView.setImageDrawable(drawable);
    }

    public void setTextColor(ColorStateList colors) {
        tabTextView.setTextColor(colors);
    }

    public void setText(CharSequence text) {
        if (TextUtils.isEmpty(text)) {
            tabTextView.setVisibility(GONE);
            return;
        }
        tabTextView.setText(text);
    }
    public void setText(int text) {
        tabTextView.setText(text);
    }

    public boolean isChoose() {
        return choose;
    }

    public void setChoose(boolean choose) {
        if (this.choose != choose) {
            this.choose = choose;
            showByMode(showMode);
            refreshDrawableState();
        }
    }

    private void showByMode(ShowMode showMode) {
        if (showMode==ShowMode.NORMAL){
            tabTextView.setVisibility(VISIBLE);
        }else if (showMode==ShowMode.RAISE){
            if (choose){
                tabTextView.setVisibility(VISIBLE);
            }else{
                tabTextView.setVisibility(GONE);
            }
        }
    }

    @Override
    protected int[] onCreateDrawableState(int extraSpace) {
        if (choose) {
            int[] drawables = super.onCreateDrawableState(extraSpace + 1);
            return mergeDrawableStates(drawables, addAttrs);
        } else {
            return super.onCreateDrawableState(extraSpace);
        }
    }

    public enum ShowMode{
        NORMAL,RAISE;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction()==MotionEvent.ACTION_DOWN){

        }
        return super.onTouchEvent(event);
    }
}
