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
import android.util.AttributeSet;
import android.view.View;

import com.jamessimshaw.wallpaperhelper.R;

public class CropView extends View {
    Bitmap mImage;
    boolean mCropLandscape;
    Paint mCropRectanglePaint;


    public CropView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mCropRectanglePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCropRectanglePaint.setStyle(Paint.Style.STROKE);
        mCropRectanglePaint.setColor(Color.WHITE); //TODO: Choose a different color
    }

    public void setImage(Bitmap image) {
        mImage = image;
        invalidate();
    }

    public void setCropLandscape(boolean cropLandscape) {
        mCropLandscape = cropLandscape;
    }

    public boolean isCropLandscape() {
        return mCropLandscape;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
}
