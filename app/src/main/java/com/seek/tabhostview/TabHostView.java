package com.seek.tabhostview;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.IdRes;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;

import java.util.List;

/**
 * Created by ASUS on 2016/4/26.
 */
public class TabHostView extends LinearLayout{


    private Paint dividerPaint;

    private int tabCount = 0;

    private int dividerWidth = 1;
    private int tabTextSize = 12;
    private ColorStateList tabTextColor;
    private int dividerColor = Color.GRAY;

    private int topLineColor = Color.GRAY;
    private int topLineHeigh = 1;

    private int dividerPadding = 5;

    private LinearLayout.LayoutParams defaultTabLayoutParams;

    /**
     * If you wanna the divide for tab, you must set it to true
     */
    private boolean dividerEnabled = false;

    private int itemPadding = 5;

    private static final int[] SYS_ATTRS = new int[] {
            android.R.attr.textSize,
            android.R.attr.textColor,
            android.R.attr.dividerPadding
    };

    private int currentPosition = 0;

    private List<Fragment> fragments = null;

    private int containerViewId;

    private Context context;

    private Fragment preFragment = null;


    public TabHostView(Context context) {
        this(context,null);
    }

    public TabHostView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context =context;
        setWillNotDraw(false);
        setOrientation(HORIZONTAL);
        DisplayMetrics dm = getResources().getDisplayMetrics();
        dividerWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dividerWidth, dm);
        dividerPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dividerPadding, dm);
        topLineHeigh = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, topLineHeigh, dm);
        itemPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, itemPadding, dm);
        // get system attrs (android:textSize and android:textColor and android:dividerPadding)
        TypedArray typedArray = context.obtainStyledAttributes(attrs, SYS_ATTRS);
        tabTextSize = typedArray.getDimensionPixelSize(0, tabTextSize);
        tabTextColor = typedArray.getColorStateList(1);
        dividerPadding = typedArray.getDimensionPixelSize(2,dividerPadding);
        typedArray.recycle();

        // get custom attrs
        typedArray = context.obtainStyledAttributes(attrs,R.styleable.TabHostView);
        dividerWidth = typedArray.getDimensionPixelSize(R.styleable.TabHostView_dividerWidth,dividerWidth);
        dividerEnabled = typedArray.getBoolean(R.styleable.TabHostView_dividerEnabled,dividerEnabled);
        dividerColor = typedArray.getColor(R.styleable.TabHostView_dividerColor,dividerColor);
        topLineColor = typedArray.getColor(R.styleable.TabHostView_topLineColor,topLineColor);
        topLineHeigh = typedArray.getDimensionPixelSize(R.styleable.TabHostView_topLineHeigh,topLineHeigh);
        typedArray.recycle();

        dividerPaint = new Paint();
        dividerPaint.setAntiAlias(true);

        defaultTabLayoutParams = new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 1.0f);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isInEditMode() || tabCount == 0) {
            return;
        }
        dividerPaint.setColor(topLineColor);
        dividerPaint.setStrokeWidth(topLineHeigh);
        canvas.drawRect(0, 0, getWidth(), topLineHeigh, dividerPaint);
        if (dividerEnabled){
            dividerPaint.setColor(dividerColor);
            dividerPaint.setStrokeWidth(dividerWidth);
            for (int i =0;i<tabCount-1;i++){
                View tab = getChildAt(i);
                canvas.drawLine(tab.getRight(), dividerPadding, tab.getRight(), getHeight() - dividerPadding, dividerPaint);
            }
        }
    }


    public void addFragments(@IdRes int containerViewId,List<Fragment> fragments,TabDataProvider tabDataProvider){
        this.containerViewId = containerViewId;
        this.fragments = fragments;
        if (containerViewId == -1 || fragments == null||tabDataProvider==null) {
            return;
        }
        tabCount = fragments.size();
        showFragment();
        notifyDataChange(tabDataProvider);
    }

    private void showFragment(){
        showFragment(containerViewId,fragments.get(currentPosition));
    }

    private void showFragment(int containerViewId, Fragment fragment) {
        if (fragment==null) return;
        FragmentManager fm = ((Activity) context).getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        if (!fragment.isAdded()) {
            if (preFragment!=null)
            ft = ft.hide(preFragment);
            ft.add(containerViewId, fragment, "fragment")
                    .commit();
        } else {
            if (fragment.isDetached()) {
                ft.hide(preFragment).attach(fragment).show(fragment).commit();
            }else {
                ft.hide(preFragment).show(fragment).commit();
            }
        }
        if (preFragment!=fragment)
        preFragment = fragment;
    }

    private void notifyDataChange(TabDataProvider tabDataProvider) {
        removeAllViews();
        for (int i = 0; i < tabCount; i++) {
            addTab(i, tabDataProvider.getTabIconDrawable(i),
                    tabDataProvider.getTabText(i));
        }
        ((TabItemView) getChildAt(currentPosition)).setChoose(true);
        invalidate();
    }

    private void addTab(final int i, int pageIconDrawable, String pageTitle) {
        TabItemView tab = new TabItemView(getContext());
        tab.setFocusable(true);
        tab.setTextSize(tabTextSize);
        tab.setTextColor(tabTextColor != null ? tabTextColor : ColorStateList.valueOf(0xFF000000));
        tab.setIconResId(pageIconDrawable);
        tab.setText(pageTitle);
        tab.setDelegateClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setCurrentPosition(i);
            }
        });
        tab.setPadding(0, itemPadding,0,itemPadding);
        addView(tab,defaultTabLayoutParams);
    }

    public void setCurrentPosition(int currentPosition) {
        this.currentPosition = currentPosition;
        showFragment();
        updateChildState(currentPosition);
    }

    private void updateChildState(int position) {
        int count = fragments.size();
        for (int i = 0; i < count; i++) {
            TabItemView child = (TabItemView) getChildAt(i);
            if (i == position) {
                child.setChoose(true);
                continue;
            }
            child.setChoose(false);
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

    private void setTabTextSize(){
        if (tabCount==0) return;
        for (int i=0;i<tabCount;i++){
            TabItemView tab = (TabItemView) getChildAt(i);
            tab.setTextSize(tabTextSize);
        }
    }

    public void setTabTextColor(int tabTextColorId) {
        this.tabTextColor = getResources().getColorStateList(tabTextColorId);
        setTabTextColor();
    }

    private void setTabTextColor(){
        if (tabCount==0) return;
        for (int i=0;i<tabCount;i++){
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

    public void setTopLineHeigh(int topLineHeigh) {
        this.topLineHeigh = topLineHeigh;
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

    public interface TabDataProvider{
        /**
         * return the drawable resId for tab icon
         * @param position
         * @return
         */
        int getTabIconDrawable(int position);

        /***
         * get text for tabs
         * @param position
         * @return
         */
        String getTabText(int position);
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

    static class SavedState extends BaseSavedState {
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
    }
}
