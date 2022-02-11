/*
 * Copyright 2022 Syberia Project
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

package com.android.settings.fuelgauge;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.SystemProperties;

//import androidx.fragment.app.Fragment;
import androidx.preference.Preference;
import androidx.preference.SwitchPreference;
import androidx.preference.PreferenceScreen;

//import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.core.PreferenceControllerMixin;

import com.android.settings.overlay.FeatureFactory;
import com.android.settings.R;

import com.android.internal.util.derp.derpUtils;

public class GooglePowerUsageProviderPreferenceController extends BasePreferenceController
        implements Preference.OnPreferenceChangeListener, PreferenceControllerMixin {

    private SwitchPreference mPreference;
    private PowerUsageFeatureProvider mPowerUsageFeatureProvider;
    private Context mContext;

    static final String POWERUSAGE_GOOGLE_PROVIDER_PROPERTY = "persist.powerusage_provider_google";

    public GooglePowerUsageProviderPreferenceController(Context context, String preferenceKey) {
        super(context, preferenceKey);
        mContext = context;
    }

    @Override
    public void displayPreference(PreferenceScreen screen) {
        super.displayPreference(screen);
        mPreference = screen.findPreference(getPreferenceKey());
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        final boolean powerUsageProviderGoogle = (Boolean) newValue;

        AlertDialog dialog = new AlertDialog.Builder(mContext)
            .setTitle(R.string.powerusage_provider_google_dialog_title)
            .setMessage(R.string.powerusage_provider_google_dialog_message)
            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                   SystemProperties.set(POWERUSAGE_GOOGLE_PROVIDER_PROPERTY, Boolean.toString(powerUsageProviderGoogle));
                   derpUtils.killForegroundApp();
                }
            })
            .setNegativeButton(R.string.cancel, null)
            .create();
        dialog.show();
        return false;
    }

    @Override
    public void updateState(Preference preference) {
        super.updateState(preference);
        final boolean powerUsageProviderGoogle =
                SystemProperties.getBoolean(POWERUSAGE_GOOGLE_PROVIDER_PROPERTY, true);
        ((SwitchPreference) preference).setChecked(powerUsageProviderGoogle);
    }

    @Override
    public int getAvailabilityStatus() {
        return AVAILABLE;
    }
}
