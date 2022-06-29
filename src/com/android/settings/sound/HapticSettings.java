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

import android.os.Bundle;

import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;

import com.android.internal.logging.nano.MetricsProto;
import com.android.internal.util.derp.derpUtils;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

import org.derpfest.support.preferences.SystemSettingSwitchPreference;

public class HapticSettings extends SettingsPreferenceFragment {

    private static final String KEY_HAPTIC_ON_SLIDER = "haptic_on_slider";
    private static final String KEY_HAPTIC_ON_VOLUME_SLIDER = "haptic_on_volume_slider";
    private static final String KEY_HAPTIC_ON_VOLUME_KEYS = "haptic_on_volume_keys";
    private static final String KEY_HAPTIC_ON_POWER_MENU = "haptic_on_power_menu";

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        addPreferencesFromResource(R.xml.haptic_settings);

        SystemSettingSwitchPreference slider = (SystemSettingSwitchPreference) findPreference(KEY_HAPTIC_ON_SLIDER);
        SystemSettingSwitchPreference volume_slider = (SystemSettingSwitchPreference) findPreference(KEY_HAPTIC_ON_VOLUME_SLIDER);
        SystemSettingSwitchPreference volume_keys = (SystemSettingSwitchPreference) findPreference(KEY_HAPTIC_ON_VOLUME_KEYS);
        SystemSettingSwitchPreference power_menu = (SystemSettingSwitchPreference) findPreference(KEY_HAPTIC_ON_POWER_MENU);

        if (!derpUtils.hasLinearMotorVibrator(getContext())) {
            final PreferenceScreen prefSet = getPreferenceScreen();
            prefSet.removePreference(slider);
            prefSet.removePreference(volume_slider);
            prefSet.removePreference(volume_keys);
            prefSet.removePreference(power_menu);
        }
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.DERP;
    }
}
