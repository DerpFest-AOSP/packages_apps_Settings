/*
 * Copyright (C) 2023 The Android Open Source Project
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

package com.android.settings.biometrics.fingerprint;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.settings.biometrics.fingerprint.feature.SfpsEnrollmentFeature;
import com.android.settings.biometrics.fingerprint.feature.SfpsEnrollmentFeatureImpl;
import com.android.settings.biometrics.fingerprint.feature.SfpsRestToUnlockFeature;
import com.android.settings.biometrics.fingerprint.feature.SfpsRestToUnlockFeatureImpl;

public class FingerprintFeatureProviderImpl implements FingerprintFeatureProvider {

    @Nullable
    private SfpsEnrollmentFeature mSfpsEnrollmentFeatureImpl = null;

    @Nullable
    private SfpsRestToUnlockFeature mSfpsRestToUnlockFeature = null;

    @Override
    public SfpsEnrollmentFeature getSfpsEnrollmentFeature() {
        if (mSfpsEnrollmentFeatureImpl == null) {
            mSfpsEnrollmentFeatureImpl = new SfpsEnrollmentFeatureImpl();
        }
        return mSfpsEnrollmentFeatureImpl;
    }

    @Override
    public SfpsRestToUnlockFeature getSfpsRestToUnlockFeature(@NonNull Context context) {
        if (mSfpsRestToUnlockFeature == null) {
            mSfpsRestToUnlockFeature = new SfpsRestToUnlockFeatureImpl();
        }
        return mSfpsRestToUnlockFeature;
    }
}
