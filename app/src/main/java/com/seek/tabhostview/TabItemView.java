package com.seek.tabhostview;

import android.content.Context;
import android.content.res.ColorStateList;
import android.support.annotation.DrawableRes;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by ASUS on 2016/4/26.
 */
public class TabItemView extends LinearLayout implements View.OnClickListener {

    private int[] addAttrs = new int[]{R.attr.state_choose};

    private boolean choose;

    private TextView tabTextView;

    private ImageView tabImageView;

    private OnClickListener delegateClickListener;

    public TabItemView(Context context) {
        this(context, null);
    }

    public TabItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setWillNotDraw(false);
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER);
        setOnClickListener(this);
        initChildView();
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

    public void setTextSize(float textSize) {
        tabTextView.setTextSize(textSize);
    }

    public void setIconResId(@DrawableRes int resId) {
        tabImageView.setImageResource(resId);
    }

    public void setTextColor(ColorStateList colors) {
        tabTextView.setTextColor(colors);
    }

    public void setText(CharSequence text) {
        tabTextView.setText(text);
    }

    public boolean isChoose() {
        return choose;
    }

    public void setChoose(boolean choose) {
        if (this.choose != choose) {
            this.choose = choose;
            refreshDrawableState();
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

    @Override
    public void onClick(View v) {
        setChoose(true);
        if (null != delegateClickListener) delegateClickListener.onClick(v);
    }

    /**
     * use setDelegateClickListener(OnClickListener delegateClickListener)
     *
     * @param l
     */
    @Deprecated
    @Override
    public void setOnClickListener(OnClickListener l) {
        super.setOnClickListener(l);
    }

    public void setDelegateClickListener(OnClickListener delegateClickListener) {
        this.delegateClickListener = delegateClickListener;
    }
}
