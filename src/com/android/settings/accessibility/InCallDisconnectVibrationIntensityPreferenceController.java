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
import android.provider.Settings;

/** Preference controller for incall vibration intensity */
public class InCallDisconnectVibrationIntensityPreferenceController
        extends VibrationIntensityPreferenceController {

    /** General configuration for incall vibration intensity settings. */
    public static final class InCallVibrationPreferenceConfig extends VibrationPreferenceConfig {

        public InCallVibrationPreferenceConfig(Context context) {
            super(context, Settings.System.VIBRATE_ON_DISCONNECT,
                    VibrationAttributes.USAGE_UNKNOWN);
        }
    }

    public InCallDisconnectVibrationIntensityPreferenceController(Context context, String preferenceKey) {
        super(context, preferenceKey, new InCallVibrationPreferenceConfig(context));
    }

    protected InCallDisconnectVibrationIntensityPreferenceController(Context context, String preferenceKey,
            int supportedIntensityLevels) {
        super(context, preferenceKey, new InCallVibrationPreferenceConfig(context),
                supportedIntensityLevels);
    }

    @Override
    public int getAvailabilityStatus() {
        return AVAILABLE;
    }
}
