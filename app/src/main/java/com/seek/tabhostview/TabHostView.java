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
            android.R.attr.drawablePadding,
            android.R.attr.itemPadding
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
    private int drawablePadding = 0;
    private LayoutParams defaultTabLayoutParams;
    /**
     * If you wanna the divide for tab, you must set it to true
     */
    private boolean dividerEnabled = false;
    private int itemPadding = 0;
    private int itemPaddingTop = 0;
    private int itemPaddingLeft = 0;
    private int itemPaddingRight = 0;
    private int itemPaddingBottom = 0;

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
        //just support AppCompatActivity, else throw exception
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
        drawablePadding = typedArray.getDimensionPixelSize(3, drawablePadding);
        itemPadding = typedArray.getDimensionPixelSize(4, itemPadding);
        typedArray.recycle();

        // get custom attrs
        typedArray = context.obtainStyledAttributes(attrs, R.styleable.TabHostView);
        dividerWidth = typedArray.getDimensionPixelSize(R.styleable.TabHostView_dividerWidth, dividerWidth);
        dividerEnabled = typedArray.getBoolean(R.styleable.TabHostView_dividerEnabled, dividerEnabled);
        dividerColor = typedArray.getColor(R.styleable.TabHostView_dividerColor, dividerColor);
        topLineColor = typedArray.getColor(R.styleable.TabHostView_topLineColor, topLineColor);
        topLineHeight = typedArray.getDimensionPixelSize(R.styleable.TabHostView_topLineHeight, topLineHeight);
        itemPaddingLeft = typedArray.getDimensionPixelSize(R.styleable.TabHostView_itemPaddingLeft, itemPaddingLeft);
        itemPaddingTop = typedArray.getDimensionPixelSize(R.styleable.TabHostView_itemPaddingTop, itemPaddingTop);
        itemPaddingRight = typedArray.getDimensionPixelSize(R.styleable.TabHostView_itemPaddingRight, itemPaddingRight);
        itemPaddingBottom = typedArray.getDimensionPixelSize(R.styleable.TabHostView_itemPaddingBottom,
                itemPaddingBottom);
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

    /***************************************************
     * (create way 1)
     * ***********************************************************/

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

    /***************************************************
     * (create way 2)
     * ***********************************************************/

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
        drawable.addState(new int[]{android.R.attr.state_checked}, getResources().getDrawable(stated));
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
            if (preFragment != null)
                curTransaction = curTransaction.hide(preFragment);
            if (fragment.isDetached()) {
                curTransaction.attach(fragment).show(fragment);
            } else {
                curTransaction.show(fragment);
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
        tab.setCompoundDrawablePadding(drawablePadding);
        tab.setTextSize(tabTextSize);
        tab.setTextColor(tabTextColor != null ? tabTextColor : ColorStateList.valueOf(0xFF000000));
        Drawable drawable = null;
        if (pageIconDrawable instanceof Integer) {
            int drawableId = (Integer) pageIconDrawable;
            drawable = getResources().getDrawable(drawableId);
        } else if (pageIconDrawable instanceof Drawable) {
            drawable = (Drawable) pageIconDrawable;
        }
        if (drawable != null) {
            tab.setDrawable(drawable);
        }
        tab.setText(TextUtils.isEmpty(pageTitle) ? "" : pageTitle);
        tab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener.onItemClick(tab, i)) {
                    setCurrentPosition(i);
                }
            }
        });
        setTabPadding(tab);
        addView(tab, defaultTabLayoutParams);
    }

    private void setTabPadding(TabItemView tab) {
        tab.setPadding(itemPaddingLeft == 0 ? itemPadding : itemPaddingLeft, itemPaddingTop == 0 ? itemPadding :
                itemPaddingTop, itemPaddingRight == 0 ? itemPadding : itemPaddingRight, itemPaddingBottom == 0 ?
                itemPadding : itemPaddingBottom);
    }

    public int getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(int currentPosition) {
        if (this.currentPosition != currentPosition) {
            this.currentPosition = currentPosition;
            showFragment();
            updateChildState(currentPosition);
        }
    }

    /**
     * show the circle point to the child
     *
     * @param position
     */
    public void showCirclePoint(int position) {
        if (position < 0 || position > tabCount - 1) return;
        TabItemView child = (TabItemView) getChildAt(position);
        child.drawCirclePoint();
    }

    /**
     * clear the circle point to the child
     *
     * @param position
     */
    public void clearCirclePoint(int position) {
        if (position < 0 || position > tabCount - 1) return;
        TabItemView child = (TabItemView) getChildAt(position);
        child.clearCirclePoint();
    }

    /**
     * show unread msg count
     *
     * @param position
     * @param msgCount
     */
    public void showMsgCount(int position, int msgCount) {
        showMsgCount(position, msgCount, 0);
    }

    /**
     * show unread msg count and give a background color
     *
     * @param position
     * @param msgCount
     * @param color
     */
    public void showMsgCount(int position, int msgCount, int color) {
        if (position < 0 || position > tabCount - 1) return;
        TabItemView child = (TabItemView) getChildAt(position);
        if (color != 0) child.setMsgBackgroundColor(color);
        child.drawMsg(msgCount);
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

    /**
     * set tab item text size
     *
     * @param tabTextSize
     */
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

    /**
     * set circle point radius
     *
     * @param circlePointRadius
     */
    public void setCirclePointRadius(float circlePointRadius) {
        setAllTabCirclePointRadius(circlePointRadius);
    }

    private void setAllTabCirclePointRadius(float circlePointRadius) {
        if (tabCount == 0) return;
        for (int i = 0; i < tabCount; i++) {
            TabItemView tab = (TabItemView) getChildAt(i);
            tab.setCirclePointRadius(circlePointRadius);
        }
    }

    /**
     * set circle point color
     *
     * @param circlePointColor
     */
    public void setCirclePointColor(int circlePointColor) {
        setAllTabCirclePointColor(circlePointColor);
    }

    private void setAllTabCirclePointColor(int circlePointColor) {
        if (tabCount == 0) return;
        for (int i = 0; i < tabCount; i++) {
            TabItemView tab = (TabItemView) getChildAt(i);
            tab.setCirclePointColor(circlePointColor);
        }
    }

    /**
     * set unread msg text color
     *
     * @param msgTextColor
     */
    public void setMsgTextColor(int msgTextColor) {
        setAllTabMsgTextColor(msgTextColor);
    }

    private void setAllTabMsgTextColor(int msgTextColor) {
        if (tabCount == 0) return;
        for (int i = 0; i < tabCount; i++) {
            TabItemView tab = (TabItemView) getChildAt(i);
            tab.setMsgTextColor(msgTextColor);
        }
    }

    /**
     * set unread msg text size
     *
     * @param msgTextSize
     */
    public void setMsgTextSize(float msgTextSize) {
        setAllTabMsgTextSize(msgTextSize);
    }

    private void setAllTabMsgTextSize(float msgTextSize) {
        if (tabCount == 0) return;
        for (int i = 0; i < tabCount; i++) {
            TabItemView tab = (TabItemView) getChildAt(i);
            tab.setMsgTextSize(msgTextSize);
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

    public void setTabPadding(int itemPaddingLeft, int itemPaddingTop, int itemPaddingRight, int itemPaddingBottom) {
        this.itemPaddingLeft = itemPaddingLeft;
        this.itemPaddingTop = itemPaddingTop;
        this.itemPaddingRight = itemPaddingRight;
        this.itemPaddingBottom = itemPaddingBottom;
        setTabPadding();
    }

    private void setTabPadding() {
        if (tabCount == 0) return;
        for (int i = 0; i < tabCount; i++) {
            TabItemView tab = (TabItemView) getChildAt(i);
            setTabPadding(tab);
        }
    }

    // you could change the tab icon by this method
    public void refreshTabIcon(int position, Drawable drawable) {
        if (position >= 0 && position < tabCount) {
            TabItemView tab = (TabItemView) getChildAt(position);
            tab.setDrawable(drawable);
        }
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
        setCurrentPosition(savedState.currentPosition);
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
