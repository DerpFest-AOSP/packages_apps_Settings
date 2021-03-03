/*
 * Copyright (C) 2023 The LeafOS Project
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

package com.android.settings.derp.statusbar;

import android.os.Bundle;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;

import com.android.settings.R;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.search.SearchIndexable;

@SearchIndexable
public class QSPanelSettings extends DashboardFragment {

    private static final String KEY_QS_SHOW_AUTO_BRIGHTNESS = "qs_show_auto_brightness";
    private static final String TAG = "QSPanelSettings";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PreferenceScreen preferenceScreen = getPreferenceScreen();
        Preference qsShowAutoBrightnessPreference = preferenceScreen.findPreference(KEY_QS_SHOW_AUTO_BRIGHTNESS);

        if (qsShowAutoBrightnessPreference != null) {
            boolean automaticBrightnessAvailable = getContext().getResources().getBoolean(
                    com.android.internal.R.bool.config_automatic_brightness_available);
            if (!automaticBrightnessAvailable) {
                qsShowAutoBrightnessPreference.setVisible(false);
            }
        }
    }

    @Override
    public int getMetricsCategory() {
        return METRICS_CATEGORY_UNKNOWN;
    }

    @Override
    protected String getLogTag() {
        return TAG;
    }

    @Override
    protected int getPreferenceScreenResId() {
        return R.xml.qs_panel_settings;
    }

    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
            new BaseSearchIndexProvider(R.xml.qs_panel_settings);
}
