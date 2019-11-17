/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.settings.system;

import android.content.ContentResolver;
import android.content.Context;
import android.hardware.display.AmbientDisplayConfiguration;
import android.provider.Settings;

import androidx.annotation.NonNull;

import com.android.settings.R;
import com.android.settings.core.BasePreferenceController;
import com.android.settingslib.core.AbstractPreferenceController;

import java.util.ArrayList;
import java.util.List;

public class BatteryLightSettingsPreferenceController extends BasePreferenceController {
    private List<AbstractPreferenceController> mGestureControllers;

    private static final String KEY_SETTINGS = "battery_light_settings";
    private Context mContext;

    public BatteryLightSettingsPreferenceController(Context context) {
        super(context, KEY_SETTINGS);
        mContext = context;
    }

    @Override
    public int getAvailabilityStatus() {
        return mContext.getResources()
                .getBoolean(com.android.internal.R.bool.config_multiColorBatteryLed) ? AVAILABLE : UNSUPPORTED_ON_DEVICE;
    }
}
