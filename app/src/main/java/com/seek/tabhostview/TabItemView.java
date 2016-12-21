package com.seek.tabhostview;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.TextView;

/**
 * Created by ASUS on 2016/4/26.
 */
public class TabItemView extends TextView {

    private static final int[] CHECKED_STATE_SET = {
            android.R.attr.state_checked
    };

    private boolean checked;

    public TabItemView(Context context) {
        this(context, null);
    }

    public TabItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setGravity(Gravity.CENTER_HORIZONTAL);
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

    public void setDrawable(Drawable drawable) {
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        setCompoundDrawables(null, drawable, null, null);
    }
}
