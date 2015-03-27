
/*
 * Copyright (C) 2014 James Simshaw
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License.  This
 * is available in the License.txt file included with the source code.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package com.jamessimshaw.wallpaperhelper.Services;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Handler;
import android.service.wallpaper.WallpaperService;
import android.view.SurfaceHolder;

import com.jamessimshaw.wallpaperhelper.DataSources.WallpaperFileHelper;
import com.jamessimshaw.wallpaperhelper.Models.Wallpaper;

public class MainService extends WallpaperService {
    @Override
    public Engine onCreateEngine() {
        return new WallpaperHelperEngine();
    }

    private class WallpaperHelperEngine extends Engine {
        private int mWidth;
        private int mHeight;
        private Wallpaper mLandscape;
        private Wallpaper mPortrait;

        private final Handler mHandler = new Handler();
        private final Runnable mRunner = new Runnable() {
            @Override
            public void run() {
                setWallpaper();
            }
        };

        public WallpaperHelperEngine() {
            setOffsetNotificationsEnabled(false);
            WallpaperFileHelper fileHelper = new WallpaperFileHelper();
            mLandscape = fileHelper.loadWallpaper(MainService.this, true);
            mPortrait = fileHelper.loadWallpaper(MainService.this, false);
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);
            if (isVisible()) {
                mHandler.post(mRunner);
            } else {
                mHandler.removeCallbacks(mRunner);
            }
        }

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            mHeight = height;
            mWidth = width;
            super.onSurfaceChanged(holder, format, width, height);
            mHandler.post(mRunner);
        }

        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            super.onSurfaceDestroyed(holder);
            mHandler.removeCallbacks(mRunner);
        }

        private void setWallpaper() {
            if(isVisible()) {
                SurfaceHolder holder = getSurfaceHolder();
                Canvas drawArea = holder.lockCanvas();

                if (drawArea != null) {
                    Rect screenRect = new Rect(0, 0, mWidth, mHeight);
                    if (mWidth > mHeight) {
                        drawArea.drawBitmap(mLandscape.getBitmap(), null, screenRect, null);
                    } else {
                        drawArea.drawBitmap(mPortrait.getBitmap(), null, screenRect, null);
                    }
                    holder.unlockCanvasAndPost(drawArea);
                }
            }
        }
    }
}