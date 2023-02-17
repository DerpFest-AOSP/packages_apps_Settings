/*
 * SPDX-FileCopyrightText: 2023 DerpFest
 * SPDX-License-Identifier: Apache-2.0
 */

package com.android.settings.deviceinfo.firmwareversion;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.os.UserManager;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.VisibleForTesting;
import androidx.preference.Preference;

import com.android.settings.R;
import com.android.settings.Utils;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.Sliceable;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedLockUtilsInternal;

public class DerpFestOfficialBannerController extends BasePreferenceController {

    private static final String TAG = "derpfestOfficialBannerCtrl";

    private static final String KEY_DERP_VERSION_PROP = "ro.derp.buildtype";

    private boolean mIsOfficialBuild = false;

    public DerpFestOfficialBannerController(Context context, String key) {
        super(context, key);
    }

    @Override
    public int getAvailabilityStatus() {
        String mAvailable = SystemProperties.get(KEY_DERP_VERSION_PROP,
                mContext.getString(R.string.unknown));
        if (mAvailable == "Official" || mAvailable == "OFFICIAL") {
            return AVAILABLE;
        }
        else {
            return UNAVAILABLE;
        }
    }

    @Override
    public boolean useDynamicSliceSummary() {
        return true;
    }

    @Override
    public boolean isSliceable() {
        return true;
    }

}
