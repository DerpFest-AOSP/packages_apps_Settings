/*
 * Copyright (C) 2022 The Android Open Source Project
 * Copyright (C) 2022 The LibreMobileOS Foundation
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

package com.android.settings.accessibility;

import android.content.Context;
import android.os.VibrationAttributes;
import android.os.Vibrator;
import android.provider.Settings;

/** Preference controller for incall vibration with only a toggle for on/off states. */
public class InCallDisconnectVibrationTogglePreferenceController extends VibrationTogglePreferenceController {

    /** General configuration for incall vibration intensity settings. */
    public static final class InCallVibrationPreferenceConfig extends VibrationPreferenceConfig {

        public InCallVibrationPreferenceConfig(Context context) {
            super(context, Settings.System.VIBRATE_ON_DISCONNECT,
                    VibrationAttributes.USAGE_UNKNOWN);
        }

        /** Returns the default intensity to be displayed when the setting value is not set. */
        public int getDefaultValue() {
            return Vibrator.VIBRATION_INTENSITY_OFF;
        }

        /** Reads setting value for corresponding {@link VibrationPreferenceConfig} */
        @Override
        public int readIntensity() {
            return Settings.System.getInt(mContentResolver, getSettingKey(), getDefaultValue());
        }
    }

    public InCallDisconnectVibrationTogglePreferenceController(Context context, String preferenceKey) {
        super(context, preferenceKey, new InCallVibrationPreferenceConfig(context));
    }

    @Override
    public int getAvailabilityStatus() {
        return AVAILABLE;
    }
}
