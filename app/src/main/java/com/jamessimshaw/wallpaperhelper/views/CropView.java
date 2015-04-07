/*
 * Copyright (C) 2014-2015 James Simshaw
 *
 *   This program is free software; you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation; either version 2 of the License.  This
 *   is available in the License.txt file included with the source code.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 */

package com.jamessimshaw.wallpaperhelper.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

public class CropView extends View {
    Bitmap mImage;
    boolean mCropLandscape;
    Paint mCropRectanglePaint;
    Rect mCropRectangle;
    int mHeight;
    int mWidth;
    int mScreenWidth;
    int mScreenHeight;
    float mScreenAspectRatio;


    public CropView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        mCropLandscape = true;
        mImage = null;  //TODO: Create a better default
    }

    private void init() {
        setScreenDimensions();

        mCropRectanglePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCropRectanglePaint.setStyle(Paint.Style.STROKE);
        mCropRectanglePaint.setColor(Color.parseColor("#FF33B5E5")); //TODO: Choose a different color
        mCropRectanglePaint.setStrokeWidth(5);
    }

    private void setScreenDimensions() {
        DisplayMetrics mDisplayMetrics = getResources().getDisplayMetrics();
        mScreenWidth = mDisplayMetrics.widthPixels;  //TODO: Handle both orientations and changing orientations
        mScreenHeight = mDisplayMetrics.heightPixels;

        mScreenAspectRatio = ((float)mScreenWidth) / mScreenHeight;
    }

    public void setImage(Bitmap image) {
        mImage = image;
        invalidate();
    }

    public void setCropLandscape(boolean cropLandscape) {
        mCropLandscape = cropLandscape;
        invalidate();
    }

    public boolean isCropLandscape() {
        return mCropLandscape;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setScreenDimensions();
        mHeight = determineDimension(mScreenHeight,
                MeasureSpec.getSize(heightMeasureSpec),
                MeasureSpec.getMode(heightMeasureSpec));
        mWidth = determineDimension(mScreenWidth,
                MeasureSpec.getSize(widthMeasureSpec),
                MeasureSpec.getMode(widthMeasureSpec));

        mCropRectangle = createCropRectangle(mWidth, mHeight);

        setMeasuredDimension(mWidth, mHeight);
    }

    private Rect createCropRectangle(int width, int height) {
        int rectangleWidth;
        int rectangleHeight;
        int rectangleStartX;
        int rectangleStartY;

        //Calculate long side first, then short side TODO: Double check this
        if(isCropLandscape()) {
            rectangleWidth = (int)Math.round(width * 0.65);
            rectangleHeight = determineCropRectangleDimension(rectangleWidth, width, height);
        }
        else {
            rectangleHeight = (int)Math.round(height * 0.65);
            rectangleWidth = determineCropRectangleDimension(rectangleHeight, width, height);
        }
        rectangleStartX = (width - rectangleWidth) / 2;
        rectangleStartY = (height - rectangleHeight) / 2;
        return new Rect(rectangleStartX,
                rectangleStartY,
                rectangleStartX + rectangleWidth,
                rectangleStartY + rectangleHeight);
    }

    private int determineCropRectangleDimension(int sideOne, int width, int height) {
        if (width > height) {
            return Math.round(sideOne / mScreenAspectRatio);
        }
        else {
            return Math.round(sideOne * mScreenAspectRatio);
        }
    }

    private int determineDimension(int desired, int given, int mode) {
        switch (mode) {
            case MeasureSpec.AT_MOST: return Math.min(desired, given);
            case MeasureSpec.EXACTLY: return given;
            case MeasureSpec.UNSPECIFIED: return desired;
            default: return desired;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawRect(mCropRectangle, mCropRectanglePaint);
    }
}
