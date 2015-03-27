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

package com.jamessimshaw.wallpaperhelper.models;

import android.graphics.Bitmap;

public class Wallpaper {
    private Bitmap mBitmap;
    private boolean mIsLandscape;

    public Wallpaper(Bitmap bitmap, boolean isLandscape) {
        mBitmap = bitmap;
        mIsLandscape = isLandscape;
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        mBitmap = bitmap;
    }

    public boolean isLandscape() {
        return mIsLandscape;
    }

    public void setLandscape(boolean isLandscape) {
        mIsLandscape = isLandscape;
    }
}
