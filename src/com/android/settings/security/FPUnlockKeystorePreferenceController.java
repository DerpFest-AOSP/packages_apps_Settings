/*
 * Copyright (C) 2018 ArrowOS
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

package com.android.settings.security;

import android.content.Context;
import android.hardware.fingerprint.FingerprintManager;
import android.provider.Settings;
import androidx.preference.Preference;
import androidx.preference.SwitchPreference;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.core.AbstractPreferenceController;

import static android.provider.Settings.System.FP_UNLOCK_KEYSTORE;

public class FPUnlockKeystorePreferenceController extends AbstractPreferenceController implements
        PreferenceControllerMixin, Preference.OnPreferenceChangeListener {

    private static final String PREF_FP_UNLOCK_KEYSTORE = "fp_unlock_keystore";

    private FingerprintManager mFingerprintManager;

    public FPUnlockKeystorePreferenceController(Context context) {
	super(context);
    }

    @Override
    public String getPreferenceKey() {
	return PREF_FP_UNLOCK_KEYSTORE;
    }

    @Override
    public void updateState(Preference preference) {
	int FPKeystoreValue = Settings.System.getInt(mContext.getContentResolver(),
                FP_UNLOCK_KEYSTORE, 0);
	((SwitchPreference) preference).setChecked(FPKeystoreValue != 0);
    }

   @Override
    public boolean isAvailable() {
        mFingerprintManager = (FingerprintManager) mContext.getSystemService(Context.FINGERPRINT_SERVICE);
        return (mFingerprintManager.isHardwareDetected());
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        boolean FPKeystoreValue = (Boolean) newValue;
        Settings.System.putInt(mContext.getContentResolver(), FP_UNLOCK_KEYSTORE, FPKeystoreValue ? 1 : 0);
        return true;
    }
}
