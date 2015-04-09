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

// TODO: Check every Math.round call for off by 1 errors
public class CropView extends View {
    private Bitmap mImage;
    private boolean mCropLandscape;
    private Paint mCropRectanglePaint;
    private Rect mCropRectangle;
    private Rect mBaseBitmapArea;
    private double mScaleFactor;


    public CropView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mCropRectanglePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCropRectanglePaint.setStyle(Paint.Style.STROKE);
        mCropRectanglePaint.setColor(Color.parseColor("#FF33B5E5")); //TODO: Fine tune color
        mCropRectanglePaint.setStrokeWidth(5);

        mScaleFactor = 0.65;
        mCropLandscape = true;
        mImage = null;
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

    private Rect createBaseBitmapRectangle(Bitmap image, Rect containingRect) {
        int imageWidth = image.getWidth();
        int imageHeight = image.getHeight();
        int containingWidth = containingRect.width();
        int containingHeight = containingRect.height();
        float multFactor = Math.min(((float)(imageHeight))/containingHeight,
                                    ((float)imageWidth)/containingWidth);

        //sets the default height and width so that the image is at
        //least as large as the containing rectangle
        int newImageWidth = Math.round(imageWidth / multFactor);
        int newImageHeight = Math.round(imageHeight / multFactor);

        //calculates the difference in x and y coordinates relative to the internal rectangle
        int xOffset = (newImageWidth - containingWidth) / 2;
        int yOffset = (newImageHeight - containingHeight) / 2;

        return new Rect(containingRect.left - xOffset,
                        containingRect.top - yOffset,
                        containingRect.right + xOffset,
                        containingRect.bottom + yOffset);
    }

    private Rect createCropRectangle(int width, int height, float ratio) {
        int rectangleWidth;
        int rectangleHeight;
        int rectangleStartX;
        int rectangleStartY;

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
        return new Rect(rectangleStartX,
                        rectangleStartY,
                        rectangleStartX + rectangleWidth,
                        rectangleStartY + rectangleHeight);
    }

    private int determineLongSide(int maxSize) {
        return (int)Math.round(maxSize * mScaleFactor);
    }

    private int determineShortSide(int longSide, int width, int height, float ratio) {
        if (width > height) {
            return Math.round(longSide / ratio);
        }
        else {
            return Math.round(longSide * ratio);
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
        return mImage;  //TODO: Finish this
    }
}
