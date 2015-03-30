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

package com.jamessimshaw.wallpaperhelper.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.jamessimshaw.wallpaperhelper.datasources.WallpaperFileHelper;
import com.jamessimshaw.wallpaperhelper.models.Wallpaper;
import com.jamessimshaw.wallpaperhelper.R;

import java.io.FileNotFoundException;


public class ImageZoneSelectionActivity extends Activity {
    private static final String TAG = ImageZoneSelectionActivity.class.getSimpleName();
    Bitmap mImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_zone_selection);

        Intent intent = getIntent();
        setImageFromFilename(intent.getStringExtra("imageUri"));


        
        WallpaperFileHelper wallpaperFileHelper = new WallpaperFileHelper();
        wallpaperFileHelper.saveWallpaper(this, new Wallpaper(mImage,
                intent.getBooleanExtra("landscape", true)));
        //finish();
    }

    private void setImageFromFilename(String file) {
        try {
            mImage = BitmapFactory.decodeStream(this.getContentResolver().openInputStream(
                    Uri.parse(file)));
        }
        catch (FileNotFoundException e) {
            Log.e(TAG, getString(R.string.exceptionCaughtMessage), e);
            Toast.makeText(this, getString(R.string.fileNotFoundToast), Toast.LENGTH_LONG).show();
            finish();
        }
    }
}
