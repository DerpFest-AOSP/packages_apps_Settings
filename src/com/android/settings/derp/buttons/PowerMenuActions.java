/*
 * SPDX-FileCopyrightText: 2014-2015 The CyanogenMod Project
 * SPDX-FileCopyrightText: 2017-2023 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package com.android.settings.derp.buttons;

import android.Manifest;
import android.content.Context;
import android.content.pm.UserInfo;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.Settings;
import android.service.controls.ControlsProviderService;

import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;

import com.android.internal.util.EmergencyAffordanceManager;
import com.android.settingslib.applications.ServiceListing;

import com.android.internal.util.derp.PowerMenuConstants;
import com.android.internal.util.derp.PowerMenuUtils;
import com.android.internal.logging.nano.MetricsProto.MetricsEvent;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.internal.derp.app.LineageGlobalActions;
import com.android.settings.derp.utils.TelephonyUtils;

import java.util.List;

import static com.android.internal.util.derp.PowerMenuConstants.*;

public class PowerMenuActions extends SettingsPreferenceFragment {
    final static String TAG = "PowerMenuActions";

    private static final String CATEGORY_POWER_MENU_ITEMS = "power_menu_items";

    private PreferenceCategory mPowerMenuItemsCategory;

    private CheckBoxPreference mScreenshotPref;
    private CheckBoxPreference mAirplanePref;
    private CheckBoxPreference mUsersPref;
    private CheckBoxPreference mBugReportPref;
    private CheckBoxPreference mEmergencyPref;
    private CheckBoxPreference mDeviceControlsPref;
    private CheckBoxPreference mPanicPref;

    private LineageGlobalActions mLineageGlobalActions;

    private EmergencyAffordanceManager mEmergencyAffordanceManager;
    private boolean mForceEmergCheck = false;

    Context mContext;
    private UserManager mUserManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.power_menu_actions_settings);
        requireActivity().setTitle(R.string.power_menu_title);
        mContext = requireActivity().getApplicationContext();
        mUserManager = UserManager.get(mContext);
        mLineageGlobalActions = mContext.getSystemService(LineageGlobalActions.class);
        mEmergencyAffordanceManager = new EmergencyAffordanceManager(mContext);

        mPowerMenuItemsCategory = findPreference(CATEGORY_POWER_MENU_ITEMS);

        for (String action : PowerMenuConstants.getAllActions()) {
            if (action.equals(GLOBAL_ACTION_KEY_SCREENSHOT)) {
                mScreenshotPref = findPreference(GLOBAL_ACTION_KEY_SCREENSHOT);
            } else if (action.equals(GLOBAL_ACTION_KEY_AIRPLANE)) {
                mAirplanePref = findPreference(GLOBAL_ACTION_KEY_AIRPLANE);
            } else if (action.equals(GLOBAL_ACTION_KEY_USERS)) {
                mUsersPref = findPreference(GLOBAL_ACTION_KEY_USERS);
            } else if (action.equals(GLOBAL_ACTION_KEY_BUGREPORT)) {
                mBugReportPref = findPreference(GLOBAL_ACTION_KEY_BUGREPORT);
            } else if (action.equals(GLOBAL_ACTION_KEY_EMERGENCY)) {
                mEmergencyPref = findPreference(GLOBAL_ACTION_KEY_EMERGENCY);
            } else if (action.equals(GLOBAL_ACTION_KEY_DEVICECONTROLS)) {
                mDeviceControlsPref = findPreference(GLOBAL_ACTION_KEY_DEVICECONTROLS);
            } else if (action.equals(GLOBAL_ACTION_KEY_PANIC)) {
                mPanicPref = findPreference(GLOBAL_ACTION_KEY_PANIC);
            }
        }

        if (!TelephonyUtils.isVoiceCapable(requireActivity())) {
            mPowerMenuItemsCategory.removePreference(mEmergencyPref);
            mEmergencyPref = null;
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        if (mScreenshotPref != null) {
            mScreenshotPref.setChecked(mLineageGlobalActions.userConfigContains(
                    GLOBAL_ACTION_KEY_SCREENSHOT));
        }

        if (mAirplanePref != null) {
            mAirplanePref.setChecked(mLineageGlobalActions.userConfigContains(
                    GLOBAL_ACTION_KEY_AIRPLANE));
        }

        if (mUsersPref != null) {
            if (!UserHandle.MU_ENABLED || !UserManager.supportsMultipleUsers()) {
                mPowerMenuItemsCategory.removePreference(mUsersPref);
                mUsersPref = null;
            } else {
                List<UserInfo> users = mUserManager.getUsers();
                boolean enabled = (users.size() > 1);
                mUsersPref.setChecked(mLineageGlobalActions.userConfigContains(
                        GLOBAL_ACTION_KEY_USERS) && enabled);
                mUsersPref.setEnabled(enabled);
            }
        }

        if (mBugReportPref != null) {
            mBugReportPref.setChecked(mLineageGlobalActions.userConfigContains(
                    GLOBAL_ACTION_KEY_BUGREPORT));
        }

        if (mEmergencyPref != null) {
            mForceEmergCheck = mEmergencyAffordanceManager.needsEmergencyAffordance();
            mEmergencyPref.setChecked(mLineageGlobalActions.userConfigContains(
                    GLOBAL_ACTION_KEY_EMERGENCY) || mForceEmergCheck);
            mEmergencyPref.setEnabled(!mForceEmergCheck);
        }

        if (mDeviceControlsPref != null) {
            mDeviceControlsPref.setChecked(mLineageGlobalActions.userConfigContains(
                    GLOBAL_ACTION_KEY_DEVICECONTROLS));

            // Enable preference if any device control app is installed
            ServiceListing serviceListing = new ServiceListing.Builder(mContext)
                    .setIntentAction(ControlsProviderService.SERVICE_CONTROLS)
                    .setPermission(Manifest.permission.BIND_CONTROLS)
                    .setNoun("Controls Provider")
                    .setSetting("controls_providers")
                    .setTag("controls_providers")
                    .build();
            serviceListing.addCallback(
                    services -> mDeviceControlsPref.setEnabled(!services.isEmpty()));
            serviceListing.reload();
        }

        if (mPanicPref != null) {
            mPanicPref.setChecked(mLineageGlobalActions.userConfigContains(
                    GLOBAL_ACTION_KEY_PANIC));
            mPanicPref.setEnabled(PowerMenuUtils.isPanicAvailable(mContext));
        }

        updatePreferences();
    }

    @Override
    public void onResume() {
        super.onResume();
        updatePreferences();
    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        boolean value;

        if (preference == mScreenshotPref) {
            value = mScreenshotPref.isChecked();
            mLineageGlobalActions.updateUserConfig(value, GLOBAL_ACTION_KEY_SCREENSHOT);

        } else if (preference == mAirplanePref) {
            value = mAirplanePref.isChecked();
            mLineageGlobalActions.updateUserConfig(value, GLOBAL_ACTION_KEY_AIRPLANE);

        } else if (preference == mUsersPref) {
            value = mUsersPref.isChecked();
            mLineageGlobalActions.updateUserConfig(value, GLOBAL_ACTION_KEY_USERS);

        } else if (preference == mBugReportPref) {
            value = mBugReportPref.isChecked();
            mLineageGlobalActions.updateUserConfig(value, GLOBAL_ACTION_KEY_BUGREPORT);
            Settings.Global.putInt(getContentResolver(),
                    Settings.Global.BUGREPORT_IN_POWER_MENU, value ? 1 : 0);

        } else if (preference == mEmergencyPref) {
            value = mEmergencyPref.isChecked();
            mLineageGlobalActions.updateUserConfig(value, GLOBAL_ACTION_KEY_EMERGENCY);

        } else if (preference == mDeviceControlsPref) {
            value = mDeviceControlsPref.isChecked();
            mLineageGlobalActions.updateUserConfig(value, GLOBAL_ACTION_KEY_DEVICECONTROLS);

        } else if (preference == mPanicPref) {
            value = mPanicPref.isChecked();
            mLineageGlobalActions.updateUserConfig(value, GLOBAL_ACTION_KEY_PANIC);

        } else {
            return super.onPreferenceTreeClick(preference);
        }
        return true;
    }

    private void updatePreferences() {
        UserInfo currentUser = mUserManager.getUserInfo(UserHandle.myUserId());
        boolean developmentSettings = Settings.Global.getInt(
                getContentResolver(), Settings.Global.DEVELOPMENT_SETTINGS_ENABLED, 0) == 1;
        boolean bugReport = Settings.Global.getInt(
                getContentResolver(), Settings.Global.BUGREPORT_IN_POWER_MENU, 0) == 1;
        boolean isPrimaryUser = currentUser == null || currentUser.isPrimary();
        if (mBugReportPref != null) {
            mBugReportPref.setEnabled(developmentSettings && isPrimaryUser);
            if (!developmentSettings) {
                mBugReportPref.setChecked(false);
                mBugReportPref.setSummary(R.string.power_menu_bug_report_devoptions_unavailable);
            } else if (!isPrimaryUser) {
                mBugReportPref.setChecked(false);
                mBugReportPref.setSummary(R.string.power_menu_bug_report_unavailable_for_user);
            } else {
                mBugReportPref.setChecked(bugReport);
                mBugReportPref.setSummary(null);
            }
        }
        if (mEmergencyPref != null) {
            if (mForceEmergCheck) {
                mEmergencyPref.setSummary(R.string.power_menu_emergency_affordance_enabled);
            } else {
                mEmergencyPref.setSummary(null);
            }
        }
    }

    @Override
    public int getMetricsCategory() {
        return MetricsEvent.DERP;
    }
}
