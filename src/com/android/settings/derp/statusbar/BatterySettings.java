/*
 * Copyright (C) 2024 Yet Another AOSP Project
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

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.UserHandle;
import android.provider.DeviceConfig;
import android.provider.Settings;
import android.text.format.DateFormat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.preference.Preference;
import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceScreen;
import androidx.preference.PreferenceCategory;
import androidx.preference.Preference.OnPreferenceChangeListener;
import androidx.preference.PreferenceFragment;
import androidx.preference.SwitchPreferenceCompat;

import com.android.internal.logging.nano.MetricsProto;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.R;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.search.SearchIndexable;

import org.derpfest.support.preferences.SystemSettingListPreference;
import org.derpfest.support.preferences.SystemSettingSwitchPreference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SearchIndexable
public class BatterySettings extends DashboardFragment implements
        OnPreferenceChangeListener {

    private static final String TAG = "BatterySettings";

    private static final String BATTERY_STYLE = "status_bar_battery_style";
    private static final String SHOW_BATTERY_PERCENT = "status_bar_show_battery_percent";
    private static final String SHOW_BATTERY_PERCENT_CHARGING = "status_bar_show_battery_percent_charging";
    private static final String SHOW_BATTERY_PERCENT_INSIDE = "status_bar_show_battery_percent_inside";

    private SystemSettingListPreference mBatteryStyle;
    private SystemSettingSwitchPreference mBatteryPercent;
    private SystemSettingSwitchPreference mBatteryPercentCharging;
    private SystemSettingSwitchPreference mBatteryPercentInside;

    @Override
    protected int getPreferenceScreenResId() {
        return R.xml.battery_settings;
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        PreferenceScreen prefSet = getPreferenceScreen();
        final ContentResolver resolver = getActivity().getContentResolver();

        mBatteryPercent = findPreference(SHOW_BATTERY_PERCENT);
        final boolean percentEnabled = Settings.System.getIntForUser(resolver,
                SHOW_BATTERY_PERCENT, 0, UserHandle.USER_CURRENT) == 1;
        mBatteryPercent.setChecked(percentEnabled);
        mBatteryPercent.setOnPreferenceChangeListener(this);

        mBatteryPercentInside = findPreference(SHOW_BATTERY_PERCENT_INSIDE);
        mBatteryPercentInside.setEnabled(percentEnabled);
        final boolean percentInside = Settings.System.getIntForUser(resolver,
                SHOW_BATTERY_PERCENT_INSIDE, 0, UserHandle.USER_CURRENT) == 1;
        mBatteryPercentInside.setChecked(percentInside);
        mBatteryPercentInside.setOnPreferenceChangeListener(this);

        mBatteryStyle = findPreference(BATTERY_STYLE);
        final int value = Settings.System.getIntForUser(resolver,
                BATTERY_STYLE, 0, UserHandle.USER_CURRENT);
        mBatteryStyle.setValue(Integer.toString(value));
        mBatteryStyle.setSummary(mBatteryStyle.getEntry());
        mBatteryStyle.setOnPreferenceChangeListener(this);
        updatePercentEnablement(value != 2);

        mBatteryPercentCharging = findPreference(SHOW_BATTERY_PERCENT_CHARGING);
        updatePercentChargingEnablement(value, percentEnabled, percentInside);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object objValue) {
        final ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mBatteryStyle) {
            int value = Integer.valueOf((String) objValue);
            int index = mBatteryStyle.findIndexOfValue((String) objValue);
            mBatteryStyle.setSummary(mBatteryStyle.getEntries()[index]);
            Settings.System.putIntForUser(resolver,
                    BATTERY_STYLE, value, UserHandle.USER_CURRENT);
            updatePercentEnablement(value != 2);
            updatePercentChargingEnablement(value, null, null);
            return true;
        } else if (preference == mBatteryPercent) {
            boolean enabled = (boolean) objValue;
            Settings.System.putInt(resolver,
                    SHOW_BATTERY_PERCENT, enabled ? 1 : 0);
            mBatteryPercentInside.setEnabled(enabled);
            updatePercentChargingEnablement(null, enabled, null);
            return true;
        } else if (preference == mBatteryPercentInside) {
            boolean enabled = (boolean) objValue;
            Settings.System.putInt(resolver,
                    SHOW_BATTERY_PERCENT_INSIDE, enabled ? 1 : 0);
            // we already know style isn't text and percent is enabled
            mBatteryPercentCharging.setEnabled(enabled);
            return true;
        }
        return false;
    }

    private void updatePercentEnablement(boolean enabled) {
        mBatteryPercent.setEnabled(enabled);
        mBatteryPercentInside.setEnabled(enabled && mBatteryPercent.isChecked());
    }

    private void updatePercentChargingEnablement(Integer style, Boolean percent, Boolean inside) {
        if (style == null) style = Integer.valueOf(mBatteryStyle.getValue());
        if (percent == null) percent = mBatteryPercent.isChecked();
        if (inside == null) inside = mBatteryPercentInside.isChecked();
        mBatteryPercentCharging.setEnabled(style != 2 && (!percent || inside));
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.DERP;
    }

    @Override
    protected String getLogTag() {
        return TAG;
    }

    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
            new BaseSearchIndexProvider(R.xml.battery_settings);
}
