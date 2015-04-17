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
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import com.jamessimshaw.wallpaperhelper.R;

public class CropView extends View {
    private Bitmap mImage;
    private boolean mCropLandscape;
    private Paint mCropRectanglePaint;
    private RectF mCropRectangle;
    private RectF mBaseBitmapArea;
    private double mScaleFactor;
    private GestureDetector mGestureDetector;
    private ScaleGestureDetector mScaleGestureDetector;


    public CropView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mCropRectanglePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCropRectanglePaint.setStyle(Paint.Style.STROKE);
        mCropRectanglePaint.setColor(getResources().getColor(R.color.CropRectColor));
        mCropRectanglePaint.setStrokeWidth(5);

        mScaleFactor = 0.65;
        mCropLandscape = true;
        mImage = null;
        mGestureDetector = new GestureDetector(this.getContext(), mGestureListener);
        mScaleGestureDetector = new ScaleGestureDetector(this.getContext(), mScaleGestureListener);
    }

    public void setImage(Bitmap image) {
        mImage = image;
        requestLayout();
        invalidate();
    }

    public void setCropLandscape(boolean cropLandscape) {
        mCropLandscape = cropLandscape;
        requestLayout();
        invalidate();
    }

    public boolean isCropLandscape() {
        return mCropLandscape;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int screenWidth = displayMetrics.widthPixels;
        int screenHeight = displayMetrics.heightPixels;
        float aspectRatio = ((float)screenWidth) / screenHeight;

        //Determines the size of the view
        int height = determineDimension(screenHeight,
                MeasureSpec.getSize(heightMeasureSpec),
                MeasureSpec.getMode(heightMeasureSpec));
        int width = determineDimension(screenWidth,
                MeasureSpec.getSize(widthMeasureSpec),
                MeasureSpec.getMode(widthMeasureSpec));

        mCropRectangle = createCropRectangle(width, height, aspectRatio);
        //Image could be null if it hasn't been set yet
        if (mImage != null) {
            mBaseBitmapArea = createBaseBitmapRectangle(mImage, mCropRectangle);
        }


        setMeasuredDimension(width, height);
    }

    private RectF createBaseBitmapRectangle(Bitmap image, RectF containingRect) {
        int imageWidth = image.getWidth();
        int imageHeight = image.getHeight();
        float containingWidth = containingRect.width();
        float containingHeight = containingRect.height();
        float multFactor = Math.min(imageHeight / containingHeight,
                                    imageWidth / containingWidth);

        //sets the default height and width so that the image is at
        //least as large as the containing rectangle
        float newImageWidth = imageWidth / multFactor;
        float newImageHeight = imageHeight / multFactor;

        //calculates the difference in x and y coordinates relative to the internal rectangle
        float xOffset = (newImageWidth - containingWidth) / 2;
        float yOffset = (newImageHeight - containingHeight) / 2;

        return new RectF(containingRect.left - xOffset,
                        containingRect.top - yOffset,
                        containingRect.right + xOffset,
                        containingRect.bottom + yOffset);
    }

    private RectF createCropRectangle(int width, int height, float ratio) {
        float rectangleWidth;
        float rectangleHeight;
        float rectangleStartX;
        float rectangleStartY;

        //Calculate long side first, then short side
        if(isCropLandscape()) {
            rectangleWidth = determineLongSide(width);
            rectangleHeight = determineShortSide(rectangleWidth, width, height, ratio);
        }
        else {
            rectangleHeight = determineLongSide(height);
            rectangleWidth = determineShortSide(rectangleHeight, width, height, ratio);
        }
        rectangleStartX = (width - rectangleWidth) / 2;
        rectangleStartY = (height - rectangleHeight) / 2;
        return new RectF(rectangleStartX,
                        rectangleStartY,
                        rectangleStartX + rectangleWidth,
                        rectangleStartY + rectangleHeight);
    }

    private float determineLongSide(int maxSize) {
        return (float)(maxSize * mScaleFactor);
    }

    private float determineShortSide(float longSide, int width, int height, float ratio) {
        if (width > height) {
            return longSide / ratio;
        }
        else {
            return longSide * ratio;
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
        //Image could be null if it hasn't been set yet
        if(mImage != null) {
            canvas.drawBitmap(mImage, null, mBaseBitmapArea, null);
        }
        canvas.drawRect(mCropRectangle, mCropRectanglePaint);
    }

    public boolean isCropValid() {
        return mBaseBitmapArea.contains(mCropRectangle);
    }

    public Bitmap getCroppedImage() {
        float imageHeightScaleFactor = mImage.getHeight() / mBaseBitmapArea.height();
        int xImageStart = Math.round((mCropRectangle.left - mBaseBitmapArea.left) *
                imageHeightScaleFactor);
        int yImageStart = Math.round((mCropRectangle.top - mBaseBitmapArea.top) *
                imageHeightScaleFactor);
        int width = Math.round(mCropRectangle.width() * imageHeightScaleFactor);
        int height = Math.round(mCropRectangle.height() * imageHeightScaleFactor);
        return Bitmap.createBitmap(mImage, xImageStart, yImageStart, width, height);
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        float xStart = event.getX();
        float yStart = event.getY();
        if(mBaseBitmapArea.contains(xStart, yStart)) {
            boolean retVal = mScaleGestureDetector.onTouchEvent(event);
            retVal = mGestureDetector.onTouchEvent(event) || retVal;
            return retVal || super.onTouchEvent(event);
        }
        return super.onTouchEvent(event);
    }

    private GestureDetector.SimpleOnGestureListener mGestureListener = new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            mBaseBitmapArea.set(mBaseBitmapArea.left - distanceX,
                                mBaseBitmapArea.top - distanceY,
                                mBaseBitmapArea.right - distanceX,
                                mBaseBitmapArea.bottom - distanceY);
            invalidate();
            return true;
        }
    };

    private ScaleGestureDetector.SimpleOnScaleGestureListener mScaleGestureListener = new ScaleGestureDetector.SimpleOnScaleGestureListener() {
        float startDistance;

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            startDistance = detector.getCurrentSpan();
            return true;
        }

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float currentDistance = detector.getCurrentSpan();
            float scalingFactor = currentDistance / startDistance;
            float newHeight = mBaseBitmapArea.height() * scalingFactor;
            float newWidth = mBaseBitmapArea.width() * scalingFactor;
            float xOffset = (newWidth - mBaseBitmapArea.height()) / 2;
            float yOffset = (newHeight - mBaseBitmapArea.width()) / 2;
            mBaseBitmapArea.set(mBaseBitmapArea.left - xOffset,
                                mBaseBitmapArea.top - yOffset,
                                mBaseBitmapArea.right + xOffset,
                                mBaseBitmapArea.bottom + yOffset);
            return true;
        }
    };
}
