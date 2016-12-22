package com.seek.tabhostview;

import android.graphics.Rect;

public class Circle {
    float mCenterX, mCenterY;
    float mRadius;

    Circle() {
    }

    Circle(float centerX, float centerY, float radius) {
        mCenterX = centerX;
        mCenterY = centerY;
        mRadius = radius;
    }

    boolean contains(float x, float y) {
        x = mCenterX - x;
        y = mCenterY - y;
        return x * x + y * y <= mRadius * mRadius;
    }

    public float getCenterX() {
        return mCenterX;
    }

    public float getCenterY() {
        return mCenterY;
    }

    public float getRadius() {
        return mRadius;
    }

    Rect getBoundingRect() {
        return new Rect(Math.round(mCenterX - mRadius), Math.round(mCenterY - mRadius),
                Math.round(mCenterX + mRadius), Math.round(mCenterY + mRadius));
    }

    @Override
    public String toString() {
        return "Radius: " + mRadius + " X: " + mCenterX + " Y: " + mCenterY;
    }
}
