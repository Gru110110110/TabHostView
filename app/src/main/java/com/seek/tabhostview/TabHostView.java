package com.seek.tabhostview;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;

import java.util.List;

/**
 * Created by ASUS on 2016/4/26.
 */
public class TabHostView extends LinearLayout {


    private static final int[] SYS_ATTRS = new int[]{
            android.R.attr.textSize,
            android.R.attr.textColor,
            android.R.attr.dividerPadding,
            android.R.attr.drawablePadding
    };
    private final FragmentManager mFragmentManager;
    private Paint dividerPaint;
    private int tabCount = 0;
    private int dividerWidth = 1;
    private int tabTextSize = 12;
    private ColorStateList tabTextColor;
    private int dividerColor = Color.GRAY;
    private int topLineColor = Color.GRAY;
    private int topLineHeight = 1;
    private int dividerPadding = 5;
    private LayoutParams defaultTabLayoutParams;
    /**
     * If you wanna the divide for tab, you must set it to true
     */
    private boolean dividerEnabled = false;
    private int itemPadding = 5;

    private int currentPosition = 0;
    private List<Fragment> fragments = null;
    private int containerViewId;
    private Fragment preFragment = null;
    private OnItemClickListener onItemClickListener;
    private int[] itemDrawableNormal = null;
    private int[] itemDrawableChoose = null;
    private String[] itemStr = null;

    public TabHostView(Context context) {
        this(context, null);
    }

    @SuppressWarnings("ResourceType")
    public TabHostView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mFragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
        setWillNotDraw(false);
        setOrientation(HORIZONTAL);
        DisplayMetrics dm = getResources().getDisplayMetrics();
        dividerWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dividerWidth, dm);
        dividerPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dividerPadding, dm);
        topLineHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, topLineHeight, dm);
        itemPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, itemPadding, dm);
        // get system attrs (android:textSize and android:textColor and android:dividerPadding)
        TypedArray typedArray = context.obtainStyledAttributes(attrs, SYS_ATTRS);
        tabTextSize = typedArray.getDimensionPixelSize(0, tabTextSize);
        tabTextColor = typedArray.getColorStateList(1);
        dividerPadding = typedArray.getDimensionPixelSize(2, dividerPadding);
        typedArray.recycle();

        // get custom attrs
        typedArray = context.obtainStyledAttributes(attrs, R.styleable.TabHostView);
        dividerWidth = typedArray.getDimensionPixelSize(R.styleable.TabHostView_dividerWidth, dividerWidth);
        dividerEnabled = typedArray.getBoolean(R.styleable.TabHostView_dividerEnabled, dividerEnabled);
        dividerColor = typedArray.getColor(R.styleable.TabHostView_dividerColor, dividerColor);
        topLineColor = typedArray.getColor(R.styleable.TabHostView_topLineColor, topLineColor);
        topLineHeight = typedArray.getDimensionPixelSize(R.styleable.TabHostView_topLineHeigh, topLineHeight);
        typedArray.recycle();

        dividerPaint = new Paint();
        dividerPaint.setAntiAlias(true);

        defaultTabLayoutParams = new LayoutParams(0, LayoutParams.MATCH_PARENT, 1.0f);

        //default
        setOnItemClickListener(new DefaultOnItemClickListener());
    }

    //
    private static String makeFragmentName(int viewId, long id) {
        return "android:switcher:" + viewId + ":" + id;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isInEditMode() || tabCount == 0) {
            return;
        }
        dividerPaint.setColor(topLineColor);
        dividerPaint.setStrokeWidth(topLineHeight);
        canvas.drawRect(0, 0, getWidth(), topLineHeight, dividerPaint);
        if (dividerEnabled) {
            dividerPaint.setColor(dividerColor);
            dividerPaint.setStrokeWidth(dividerWidth);
            for (int i = 0; i < tabCount - 1; i++) {
                View tab = getChildAt(i);
                canvas.drawLine(tab.getRight(), dividerPadding, tab.getRight(), getHeight() - dividerPadding,
                        dividerPaint);
            }
        }
    }

    /**
     * another way to create items
     *
     * @param containerViewId
     * @param fragments
     * @param tabDataProvider
     */
    public void addFragments(@IdRes int containerViewId, List<Fragment> fragments, TabDataProvider tabDataProvider) {
        this.containerViewId = containerViewId;
        this.fragments = fragments;
        if (containerViewId == -1 || fragments == null || tabDataProvider == null) {
            return;
        }
        tabCount = fragments.size();
        showFragment();
        notifyDataChange(tabDataProvider);
    }

    /**
     * step 1
     *
     * @param containerViewId
     * @param fragments
     * @return
     */
    public TabHostView addFragments(@IdRes int containerViewId, List<Fragment> fragments) {
        this.containerViewId = containerViewId;
        this.fragments = fragments;
        return this;
    }

    /**
     * step 2
     *
     * @param itemDrawableNormal
     * @param itemDrawableChoose
     * @param itemStrs
     * @return
     */
    public TabHostView setItemRes(int[] itemDrawableNormal, int[] itemDrawableChoose, String[] itemStrs) {
        this.itemDrawableNormal = itemDrawableNormal;
        this.itemDrawableChoose = itemDrawableChoose;
        this.itemStr = itemStrs;
        return this;
    }

    /**
     * step 3
     */
    public void createItems() {
        if (fragments == null || fragments.size() == 0) return;
        tabCount = fragments.size();
        showFragment();
        notifyDataChange(itemDrawableNormal, itemDrawableChoose, itemStr);
    }

    private StateListDrawable makeDrawable(int normal, int stated) {
        StateListDrawable drawable = new StateListDrawable();
        if (normal == 0 || normal < 0) return drawable;
        if (stated == 0 || stated < 0) return drawable;
        drawable.addState(new int[]{R.attr.state_choose}, getResources().getDrawable(stated));
        drawable.addState(new int[]{}, getResources().getDrawable(normal));
        return drawable;
    }

    private void showFragment() {
        FragmentTransaction curTransaction = mFragmentManager.beginTransaction();
        final int itemId = getCurrentPosition();
        // Do we already have this fragment?
        String name = makeFragmentName(containerViewId, itemId);
        Fragment fragment = mFragmentManager.findFragmentByTag(name);
        if (fragment == null) {
            fragment = fragments.get(itemId);
        }
        if (!fragment.isAdded()) {
            if (preFragment != null)
                curTransaction = curTransaction.hide(preFragment);
            curTransaction.add(containerViewId, fragment, name);
        } else {
            if (fragment.isDetached()) {
                curTransaction.hide(preFragment).attach(fragment).show(fragment);
            } else {
                curTransaction.hide(preFragment).show(fragment);
            }
        }
        curTransaction.commit();
        if (preFragment != fragment) {
            preFragment = fragment;
        }
    }

    private void notifyDataChange(TabDataProvider tabDataProvider) {
        removeAllViews();
        for (int i = 0; i < tabCount; i++) {
            addTab(i, tabDataProvider.getTabIconDrawable(i),
                    tabDataProvider.getTabText(i));
        }
        ((TabItemView) getChildAt(currentPosition)).setChecked(true);
        invalidate();
    }

    private void notifyDataChange(int[] itemDrawableNormal, int[] itemDrawableChoose, String[] itemStr) {
        removeAllViews();
        for (int i = 0; i < tabCount; i++) {
            Drawable drawable = makeDrawable(itemDrawableNormal.length > i ? itemDrawableNormal[i] : 0,
                    itemDrawableChoose.length > i ? itemDrawableChoose[i] : 0);
            String itemText = itemStr.length > i ? itemStr[i] : "";
            addTab(i, drawable,
                    itemText);
        }
        ((TabItemView) getChildAt(currentPosition)).setChecked(true);
        invalidate();
    }

    private void addTab(final int i, Object pageIconDrawable, String pageTitle) {
        final TabItemView tab = new TabItemView(getContext());
        tab.setFocusable(true);
        tab.setTextSize(tabTextSize);
        tab.setTextColor(tabTextColor != null ? tabTextColor : ColorStateList.valueOf(0xFF000000));
        Drawable drawable = null;
        if (pageIconDrawable instanceof Integer) {
            drawable = getResources().getDrawable((Integer) pageIconDrawable);
        } else if (pageIconDrawable instanceof Drawable) {
            drawable = (Drawable) pageIconDrawable;
        }
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        tab.setCompoundDrawables(null, drawable, null, null);
        tab.setText(TextUtils.isEmpty(pageTitle) ? "" : pageTitle);
        tab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener.onItemClick(tab, i))
                    setCurrentPosition(i);
            }
        });
        tab.setPadding(0, itemPadding, 0, itemPadding);
        addView(tab, defaultTabLayoutParams);
    }

    public int getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(int currentPosition) {
        this.currentPosition = currentPosition;
        showFragment();
        updateChildState(currentPosition);
    }

    /**
     * show red point to the child
     *
     * @param position
     */
    public void setPoint(int position) {
        if (position < 0 || position > tabCount - 1) return;
        TabItemView child = (TabItemView) getChildAt(position);
    }

    /**
     * show unread msg count
     *
     * @param position
     * @param msgCount
     */
    public void setMsgCount(int position, int msgCount) {
        if (position < 0 || position > tabCount - 1) return;
        TabItemView child = (TabItemView) getChildAt(position);
    }


    private void updateChildState(int position) {
        int count = fragments.size();
        for (int i = 0; i < count; i++) {
            TabItemView child = (TabItemView) getChildAt(i);
            if (i == position) {
                child.setChecked(true);
                continue;
            }
            child.setChecked(false);
        }
    }

    public void setDividerWidth(int dividerWidth) {
        this.dividerWidth = dividerWidth;
        if (dividerEnabled)
            invalidate();
    }

    public void setTabTextSize(int tabTextSize) {
        this.tabTextSize = tabTextSize;
        setTabTextSize();
    }

    private void setTabTextSize() {
        if (tabCount == 0) return;
        for (int i = 0; i < tabCount; i++) {
            TabItemView tab = (TabItemView) getChildAt(i);
            tab.setTextSize(tabTextSize);
        }
    }

    public void setTabTextColor(int tabTextColorId) {
        this.tabTextColor = getResources().getColorStateList(tabTextColorId);
        setTabTextColor();
    }

    private void setTabTextColor() {
        if (tabCount == 0) return;
        for (int i = 0; i < tabCount; i++) {
            TabItemView tab = (TabItemView) getChildAt(i);
            tab.setTextColor(tabTextColor);
        }
    }

    public void setDividerColor(int dividerColor) {
        this.dividerColor = getResources().getColor(dividerColor);
        invalidate();
    }

    public void setTopLineColor(int topLineColor) {
        this.topLineColor = getResources().getColor(topLineColor);
        invalidate();
    }

    public void setTopLineHeight(int topLineHeight) {
        this.topLineHeight = topLineHeight;
        invalidate();
    }

    @Override
    public void setDividerPadding(int dividerPadding) {
        this.dividerPadding = dividerPadding;
        if (dividerEnabled)
            invalidate();
    }

    public void setDividerEnabled(boolean dividerEnabled) {
        this.dividerEnabled = dividerEnabled;
        invalidate();
    }

    public void setItemPadding(int itemPadding) {
        this.itemPadding = itemPadding;
    }

    public OnItemClickListener getOnItemClickListener() {
        return onItemClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        SavedState savedState = (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());
        currentPosition = savedState.currentPosition;
        requestLayout();
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState savedState = new SavedState(superState);
        savedState.currentPosition = currentPosition;
        return savedState;
    }

    public interface TabDataProvider {
        /**
         * return the drawable resId for tab icon
         *
         * @param position
         * @return
         */
        int getTabIconDrawable(int position);

        /***
         * get text for tabs
         *
         * @param position
         * @return
         */
        String getTabText(int position);
    }

    public interface OnItemClickListener {
        /**
         * return true then tab could choose
         * qq 951882080
         */
        boolean onItemClick(View view, int position);
    }

    static class SavedState extends BaseSavedState {
        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
            @Override
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
        int currentPosition;

        public SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            currentPosition = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(currentPosition);
        }
    }

    private class DefaultOnItemClickListener implements OnItemClickListener {
        @Override
        public boolean onItemClick(View view, int position) {
            return true;
        }
    }
}
