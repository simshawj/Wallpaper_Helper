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
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.jamessimshaw.wallpaperhelper.datasources.WallpaperFileHelper;
import com.jamessimshaw.wallpaperhelper.models.Wallpaper;
import com.jamessimshaw.wallpaperhelper.R;
import com.jamessimshaw.wallpaperhelper.views.CropView;

import java.io.FileNotFoundException;
import java.io.IOException;


public class ImageZoneSelectionActivity extends Activity {
    private static final String TAG = ImageZoneSelectionActivity.class.getSimpleName();
    private boolean mIsLandscape;
    private CropView mCropView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_zone_selection);

        Button cropButton = (Button) findViewById(R.id.cropButton);

        Intent intent = getIntent();
        Bitmap baseImage = setImageFromFilename(intent.getStringExtra("imageUri"));
        mIsLandscape = intent.getBooleanExtra("landscape", true);

        mCropView = (CropView) findViewById(R.id.cropView);
        mCropView.setCropLandscape(mIsLandscape);
        mCropView.setImage(baseImage);

        cropButton.setOnClickListener(mCropButtonListener);
    }

    private Bitmap setImageFromFilename(String file) {
        try {
            //TODO: Needs to be done async
            return BitmapFactory.decodeStream(this.getContentResolver().openInputStream(
                    Uri.parse(file)));
        }
        catch (NullPointerException | FileNotFoundException e) {
            Log.e(TAG, getString(R.string.exceptionCaughtMessage), e);
            Toast.makeText(this, getString(R.string.fileNotFoundToast), Toast.LENGTH_LONG).show();
            finish();
            return null;
        }
    }

    private View.OnClickListener mCropButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mCropView.isCropValid()) {
                Bitmap image = mCropView.getCroppedImage();
                WallpaperFileHelper wallpaperFileHelper = new WallpaperFileHelper();
                try {
                    wallpaperFileHelper.saveWallpaper(ImageZoneSelectionActivity.this,
                            new Wallpaper(image, mIsLandscape));
                    finish();
                }
                catch (IOException e) {
                    Log.e(TAG, getString(R.string.exceptionCaughtMessage), e);
                    Toast.makeText(ImageZoneSelectionActivity.this,
                            getString(R.string.unableToSaveImageToast), Toast.LENGTH_LONG).show();
                }
            }
            else {
                Toast.makeText(ImageZoneSelectionActivity.this,
                        getString(R.string.invalidCrop),
                        Toast.LENGTH_LONG).show();
            }
        }
    };
}