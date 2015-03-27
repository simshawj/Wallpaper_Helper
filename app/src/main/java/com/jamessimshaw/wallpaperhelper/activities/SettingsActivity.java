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

package com.jamessimshaw.wallpaperhelper.activities;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;

import com.jamessimshaw.wallpaperhelper.R;


public class SettingsActivity extends PreferenceActivity {
    private static final int LANDSCAPE = 1;
    private static final int PORTRAIT = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingFrag())
                .commit();
    }

    public static class SettingFrag extends PreferenceFragment implements Preference.OnPreferenceClickListener {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings);

            findPreference("landscape").setOnPreferenceClickListener(this);
            findPreference("portrait").setOnPreferenceClickListener(this);
        }

        @Override
        public boolean onPreferenceClick(Preference preference) {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            if (preference.getKey().equals("landscape")) {
                startActivityForResult(intent, LANDSCAPE);
                return true;
            } else if (preference.getKey().equals("portrait")) {
                startActivityForResult(intent, PORTRAIT);
                return true;
            } else {
                return false;
            }
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if (resultCode == RESULT_OK) {
                String imageUri = data.getData().toString();
                Intent intent = new Intent(getActivity(), ImageZoneSelectionActivity.class);
                intent.putExtra("landscape", requestCode == LANDSCAPE);
                intent.putExtra("imageUri", imageUri);
                startActivity(intent);
            }
        }
    }
}
