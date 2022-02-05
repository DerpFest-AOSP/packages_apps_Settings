/*
 * Copyright (C) 2022 Benzo Rom
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.android.settings.display;

import android.content.Context;
import android.hardware.display.DisplayManager;
import android.provider.Settings;
import android.view.Display;

import androidx.fragment.app.Fragment;

import com.android.settings.R;
import com.android.settings.core.TogglePreferenceController;

public class ForcePeakRefreshRatePreferenceController extends TogglePreferenceController {

    private static final String MIN_REFRESH_RATE = "min_refresh_rate";

    private float mPeakRefreshRate;
    private Fragment mParent;

    public ForcePeakRefreshRatePreferenceController(Context context, String key) {
        super(context, key);
        final DisplayManager dm = context.getSystemService(DisplayManager.class);
        final Display display = dm.getDisplay(Display.DEFAULT_DISPLAY);
        if (display == null) {
            mPeakRefreshRate = getDefaultRefreshRate();
        } else {
            mPeakRefreshRate = findPeakRefreshRate(display.getSupportedModes());
        }
    }

    public void init(Fragment fragment) {
        mParent = fragment;
    }

    @Override
    public int getAvailabilityStatus() {
        return mContext.getResources().getBoolean(R.bool.config_show_smooth_display)
                        ? AVAILABLE : UNSUPPORTED_ON_DEVICE;
    }

    @Override
    public boolean isChecked() {
        final float peakRefreshRate =
            Settings.System.getFloat(mContext.getContentResolver(),
                Settings.System.MIN_REFRESH_RATE, 0f);
        return peakRefreshRate >= mPeakRefreshRate;
    }

    @Override
    public boolean setChecked(boolean isChecked) {
        Settings.System.putFloat(mContext.getContentResolver(),
            Settings.System.MIN_REFRESH_RATE,
            isChecked ? mPeakRefreshRate : 0f);
        return true;
    }

    private float getDefaultRefreshRate() {
        return (float) mContext.getResources().getInteger(
            com.android.internal.R.integer.config_defaultRefreshRate);
    }

    private float findPeakRefreshRate(Display.Mode[] modes) {
        float peakRefreshRate = getDefaultRefreshRate();
        float defaultPeakRefreshRate = (float) mContext.getResources().getInteger(
                    com.android.internal.R.integer.config_defaultPeakRefreshRate);
        for (Display.Mode mode : modes) {
            float refreshRate = Math.round(mode.getRefreshRate());
            if (refreshRate > peakRefreshRate
                    && (defaultPeakRefreshRate == 0 || refreshRate <= defaultPeakRefreshRate)) {
                peakRefreshRate = refreshRate;
            }
        }
        return peakRefreshRate;
    }

    @Override
    public int getSliceHighlightMenuRes() {
        return R.string.menu_key_display;
    }
}
