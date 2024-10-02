/*
 * SPDX-FileCopyrightText: 2017-2023 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package com.android.settings.derp.statusbar;

import android.content.ContentResolver;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import androidx.preference.DropDownPreference;
import androidx.preference.Preference;

import com.android.internal.logging.nano.MetricsProto.MetricsEvent;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

import org.derpfest.support.preferences.SecureSettingMainSwitchPreference;
import org.derpfest.support.preferences.SecureSettingSwitchPreference;

public class NetworkTrafficSettings extends SettingsPreferenceFragment
        implements Preference.OnPreferenceChangeListener  {

    private static final String TAG = "NetworkTrafficSettings";
    private static final String STATUS_BAR_CLOCK_STYLE = "status_bar_clock";

    private SecureSettingMainSwitchPreference mNetTraffic;
    private SecureSettingSwitchPreference mNetTrafficAutohide;
    private DropDownPreference mNetTrafficUnits;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.network_traffic_settings);
        getActivity().setTitle(R.string.network_traffic_settings_title);

        final ContentResolver resolver = getActivity().getContentResolver();

        mNetTraffic = findPreference(Settings.Secure.NETWORK_TRAFFIC_MODE);

        mNetTrafficAutohide = findPreference(Settings.Secure.NETWORK_TRAFFIC_AUTOHIDE);

        mNetTrafficUnits = findPreference(Settings.Secure.NETWORK_TRAFFIC_UNITS);
        mNetTrafficUnits.setOnPreferenceChangeListener(this);
        int units = Settings.Secure.getInt(resolver,
                Settings.Secure.NETWORK_TRAFFIC_UNITS, /* Mbps */ 1);
        mNetTrafficUnits.setValue(String.valueOf(units));

        updateForClockConflicts();
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        int value = Integer.parseInt((String) newValue);
        String key = preference.getKey();
        switch (key) {
            case Settings.Secure.NETWORK_TRAFFIC_UNITS:
                Settings.Secure.putInt(getActivity().getContentResolver(),
                        Settings.Secure.NETWORK_TRAFFIC_UNITS, value);
                break;
        }
        return true;
    }

    private void updateEnabledStates(boolean enabled) {
        mNetTrafficAutohide.setEnabled(enabled);
        mNetTrafficUnits.setEnabled(enabled);
    }

    private void updateForClockConflicts() {
        int clockPosition = Settings.System.getInt(getActivity().getContentResolver(),
                STATUS_BAR_CLOCK_STYLE, 2);

        if (clockPosition != 1) {
            return;
        }

        mNetTraffic.setEnabled(false);
        Toast.makeText(getActivity(),
                R.string.network_traffic_disabled_clock,
                Toast.LENGTH_LONG).show();
        updateEnabledStates(false);
    }

    @Override
    public int getMetricsCategory() {
        return MetricsEvent.DERP;
    }
}
