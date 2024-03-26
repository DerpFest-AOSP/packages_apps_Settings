/*
 * Copyright (C) 2017 The Android Open Source Project
 * Copyright (C) 2022 The Calyx Institute
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

package com.android.settings.other;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.provider.Settings;
import android.util.Log;

import androidx.preference.Preference;
import androidx.preference.SwitchPreference;

import com.android.internal.statusbar.IStatusBarService;
import com.android.settings.R;
import com.android.settings.core.TogglePreferenceController;
import com.android.settings.development.qstile.DevelopmentTiles.SensorsOff;

public class SensorsOffPreferenceController extends TogglePreferenceController {

    private static final String TAG = "SensorsOffPreferenceController";

    private ComponentName mComponentName;
    private PackageManager mPackageManager;

    public SensorsOffPreferenceController(Context context, String preferenceKey) {
        super(context, preferenceKey);
        mComponentName = new ComponentName(context.getPackageName(), SensorsOff.class.getName());
        mPackageManager = context.getPackageManager();
    }

    @Override
    public int getAvailabilityStatus() {
        return AVAILABLE;
    }

    @Override
    public boolean isChecked() {
        return mPackageManager.getComponentEnabledSetting(mComponentName) == 
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED;
    }

    @Override
    public boolean setChecked(boolean isChecked) {
        IStatusBarService mStatusBarService = IStatusBarService.Stub.asInterface(
                ServiceManager.checkService(Context.STATUS_BAR_SERVICE));

        mPackageManager.setComponentEnabledSetting(mComponentName, isChecked
                        ? PackageManager.COMPONENT_ENABLED_STATE_ENABLED
                        : PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);

        try {
            if (mStatusBarService != null) {
                if (isChecked) {
                    mStatusBarService.addTile(mComponentName);
                } else {
                    mStatusBarService.remTile(mComponentName);
                }
            }
        } catch (RemoteException e) {
            Log.e(TAG, "Failed to modify QS tile for component " + mComponentName.toString(), e);
        }
        return true;
    }

    @Override
    public int getSliceHighlightMenuRes() {
        return R.string.menu_key_system;
    }

}
