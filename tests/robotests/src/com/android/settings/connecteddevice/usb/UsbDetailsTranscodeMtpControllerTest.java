/*
 * Copyright (C) 2021 The Android Open Source Project
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

package com.android.settings.connecteddevice.usb;

import static android.hardware.usb.UsbPortStatus.DATA_ROLE_NONE;
import static android.hardware.usb.UsbPortStatus.POWER_ROLE_NONE;

import static com.google.common.truth.Truth.assertThat;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import android.app.KeyguardManager;
import android.content.Context;
import android.hardware.usb.UsbManager;
import android.os.SystemProperties;
import android.platform.test.annotations.RequiresFlagsEnabled;

import androidx.fragment.app.FragmentActivity;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;

import com.android.settings.flags.Flags;
import com.android.settings.testutils.shadow.ShadowUtils;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(shadows = {
        com.android.settings.testutils.shadow.ShadowFragment.class,
})
public class UsbDetailsTranscodeMtpControllerTest {
    private static final String TRANSCODE_MTP_SYS_PROP_KEY = "sys.fuse.transcode_mtp";

    private Context mContext;
    private PreferenceCategory mPreference;
    private PreferenceManager mPreferenceManager;
    private PreferenceScreen mScreen;
    private UsbDetailsTranscodeMtpController mUnderTest;
    private UsbDetailsFragment mFragment;

    @Mock
    private UsbBackend mUsbBackend;
    @Mock
    private FragmentActivity mActivity;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        mFragment = spy(new UsbDetailsFragment());
        mContext = RuntimeEnvironment.application;
        mPreferenceManager = new PreferenceManager(mContext);
        mScreen = mPreferenceManager.createPreferenceScreen(mContext);

        when(mFragment.getActivity()).thenReturn(mActivity);
        when(mActivity.getApplicationContext()).thenReturn(mContext);
        when(mFragment.getContext()).thenReturn(mContext);
        when(mFragment.getPreferenceManager()).thenReturn(mPreferenceManager);
        when(mFragment.getPreferenceScreen()).thenReturn(mScreen);

        mUnderTest = new UsbDetailsTranscodeMtpController(mContext, mFragment, mUsbBackend);

        mPreference = new PreferenceCategory(mContext);
        mPreference.setKey(mUnderTest.getPreferenceKey());
        mScreen.addPreference(mPreference);

        mUnderTest.displayPreference(mScreen);
    }

    @Test
    public void displayRefresh_noUsbConnection_shouldDisablePrefCategory() {
        when(mUsbBackend.areAllRolesSupported()).thenReturn(true);

        mUnderTest.refresh(false /* connected */, UsbManager.FUNCTION_MTP, POWER_ROLE_NONE,
                DATA_ROLE_NONE);

        assertThat(mPreference.isEnabled()).isFalse();
    }

    @Test
    public void displayRefresh_noDataTransfer_shouldDisablePrefCategory() {
        when(mUsbBackend.areAllRolesSupported()).thenReturn(true);

        mUnderTest.refresh(true /* connected */, UsbManager.FUNCTION_NONE, POWER_ROLE_NONE,
                DATA_ROLE_NONE);

        assertThat(mPreference.isEnabled()).isFalse();
    }

    @Test
    public void displayRefresh_noDataRole_shouldDisablePrefCategory() throws InterruptedException {
        when(mUsbBackend.areAllRolesSupported()).thenReturn(true);

        mUnderTest.refresh(true /* connected */, UsbManager.FUNCTION_MTP, POWER_ROLE_NONE,
                DATA_ROLE_NONE);

        assertThat(mPreference.isEnabled()).isFalse();
    }

    @Ignore("b/313362757")
    @Test
    public void displayRefresh_fileTransfer_withAbsentProp_shouldCheck() {
        when(mUsbBackend.areAllRolesSupported()).thenReturn(true);

        mUnderTest.refresh(true /* connected */, UsbManager.FUNCTION_MTP, POWER_ROLE_NONE,
                DATA_ROLE_NONE);

        assertThat(getSwitchPreference().isChecked()).isFalse();
    }

    @Ignore("b/313362757")
    @Test
    public void displayRefresh_fileTransfer_withUnsetProp_shouldUncheck() {
        SystemProperties.set(TRANSCODE_MTP_SYS_PROP_KEY, Boolean.toString(false));
        when(mUsbBackend.areAllRolesSupported()).thenReturn(true);

        mUnderTest.refresh(true /* connected */, UsbManager.FUNCTION_MTP, POWER_ROLE_NONE,
                DATA_ROLE_NONE);

        assertThat(getSwitchPreference().isChecked()).isFalse();
    }

    @Ignore("b/313362757")
    @Test
    public void displayRefresh_fileTransfer_withSetProp_shouldCheck() {
        SystemProperties.set(TRANSCODE_MTP_SYS_PROP_KEY, Boolean.toString(true));
        when(mUsbBackend.areAllRolesSupported()).thenReturn(true);

        mUnderTest.refresh(true /* connected */, UsbManager.FUNCTION_MTP, POWER_ROLE_NONE,
                DATA_ROLE_NONE);

        assertThat(getSwitchPreference().isChecked()).isTrue();
    }

    @Ignore("b/313362757")
    @Test
    public void click_checked_shouldSetSystemProperty() {
        getSwitchPreference().performClick();
        assertThat(SystemProperties.getBoolean(TRANSCODE_MTP_SYS_PROP_KEY, false)).isTrue();
    }

    @Ignore("b/313362757")
    @Test
    public void click_unChecked_shouldUnsetSystemProperty() {
        getSwitchPreference().performClick();
        getSwitchPreference().performClick();
        assertThat(SystemProperties.getBoolean(TRANSCODE_MTP_SYS_PROP_KEY, true)).isFalse();
    }

    @Test
    @Config(shadows = ShadowUtils.class)
    public void isAvailable_isMonkey_shouldReturnFalse() {
        ShadowUtils.setIsUserAMonkey(true);
        assertThat(mUnderTest.isAvailable()).isFalse();
    }

    @Test
    @RequiresFlagsEnabled(Flags.FLAG_ENABLE_AUTH_CHALLENGE_FOR_USB_PREFERENCES)
    public void onClick_userAuthenticated() {
        setAuthPassesAutomatically();

        mUnderTest.onPreferenceClick(null);

        assertThat(mFragment.isUserAuthenticated()).isTrue();
    }

    private void setAuthPassesAutomatically() {
        Shadows.shadowOf(mContext.getSystemService(KeyguardManager.class))
                .setIsKeyguardSecure(false);
    }

    private SwitchPreference getSwitchPreference() {
        return (SwitchPreference) mPreference.getPreference(0);
    }
}
