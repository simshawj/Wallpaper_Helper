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

package com.jamessimshaw.wallpaperhelper.datasources;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;

import com.jamessimshaw.wallpaperhelper.models.Wallpaper;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class WallpaperFileHelper {
    public static final String LANDSCAPE_FILENAME = "landscape.png";
    public static final String PORTRAIT_FILENAME = "portrait.png";


    public Wallpaper loadWallpaper(Context context, boolean isLandscape) {
        Bitmap bitmap;
        String filename = isLandscape ? LANDSCAPE_FILENAME : PORTRAIT_FILENAME;

        try {
            //TODO: Set this up to be an async task
            FileInputStream fileInputStream = context.openFileInput(filename);
            bitmap = BitmapFactory.decodeStream(fileInputStream);
            fileInputStream.close();
        }
        catch (IOException e) {
            //TODO:Set a default Wallpaper of a black screen and send a notification
            Bitmap.Config bitmapOptions = Bitmap.Config.ARGB_8888;
            bitmap = Bitmap.createBitmap(100, 100, bitmapOptions);
            Canvas canvas = new Canvas(bitmap);
            canvas.drawColor(Color.BLACK);
        }

        return new Wallpaper(bitmap, isLandscape);
    }

    public void saveWallpaper(Context context, Wallpaper wallpaper) throws IOException {
        String filename = wallpaper.isLandscape() ? LANDSCAPE_FILENAME : PORTRAIT_FILENAME;

        //TODO: Set this to be an async task
        FileOutputStream fileOutputStream = context.openFileOutput(filename,
                Context.MODE_PRIVATE);
        wallpaper.getBitmap().compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
        fileOutputStream.close();
    }
}
