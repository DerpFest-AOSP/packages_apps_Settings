/*
 * Copyright (C) 2022 The Nameless-AOSP Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package com.android.settings.sound;

import android.content.Context;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;

import androidx.preference.Preference;

import com.android.internal.logging.nano.MetricsProto;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

public class HapticSettings extends SettingsPreferenceFragment {

    private static final String BRIGHTNESS_SLIDER_HAPTICS_KEY = "brightness_slider_haptics";
    private static final String VOLUME_SLIDER_HAPTICS_KEY = "volume_panel_haptics";

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        addPreferencesFromResource(R.xml.haptic_settings);

        if (!isSliderHapticsSupported()) {
            Preference sliderHaptics = findPreference(BRIGHTNESS_SLIDER_HAPTICS_KEY);
            if (sliderHaptics != null) sliderHaptics.setVisible(false);
        }

        if (!isSliderHapticsSupported()) {
            Preference sliderHaptics = findPreference(VOLUME_SLIDER_HAPTICS_KEY);
            if (sliderHaptics != null) sliderHaptics.setVisible(false);
        }
    }

    private boolean isSliderHapticsSupported() {
        Vibrator vibrator = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator == null || !vibrator.hasVibrator()) {
            return false; // device has no vibrator
        }
        if (vibrator.areAllPrimitivesSupported(
                VibrationEffect.Composition.PRIMITIVE_LOW_TICK,
                VibrationEffect.Composition.PRIMITIVE_CLICK)) {
            return true; // device supports primitives
        }
        int max = getContext().getResources().getInteger(
                com.android.internal.R.integer.config_sliderVibFallbackDuration);
        if (max <= 0) {
            return false; // fallbacks are not set
        }
        return true; // does not support primitives but fallbacks are set
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.DERP;
    }
}
