/*
 * Copyright (C) 2010 The Android Open Source Project
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

package com.android.settings;

import android.app.AlertDialog;
import android.app.settings.SettingsEnums;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.UserHandle;
import android.provider.Settings;
import android.text.InputFilter;
import android.text.Spannable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.preference.Preference;

import com.android.settings.R;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.display.BrightnessLevelPreferenceController;
import com.android.settings.display.CameraGesturePreferenceController;
import com.android.settings.display.EnableBlursPreferenceController;
import com.android.settings.display.LiftToWakePreferenceController;
import com.android.settings.display.PocketJudgePreferenceController;
import com.android.settings.display.ShowOperatorNamePreferenceController;
import com.android.settings.display.TapToWakePreferenceController;
import com.android.settings.display.ThemePreferenceController;
import com.android.settings.display.VrDisplayPreferenceController;
import com.android.settings.preference.SystemSettingPrimarySwitchPreference;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.search.SearchIndexable;
import com.android.systemui.shared.system.BlurUtils;

import com.android.internal.derp.hardware.LineageHardwareManager;

import java.util.ArrayList;
import java.util.List;

@SearchIndexable(forTarget = SearchIndexable.ALL & ~SearchIndexable.ARC)
public class DisplaySettings extends DashboardFragment
        implements Preference.OnPreferenceClickListener {
    private static final String TAG = "DisplaySettings";

    private static final String KEY_HIGH_TOUCH_POLLING_RATE = "high_touch_polling_rate_enable";
    private static final String KEY_HIGH_TOUCH_SENSITIVITY = "high_touch_sensitivity_enable";
    private static final String KEY_PROXIMITY_ON_WAKE = "proximity_on_wake";
    private SystemSettingPrimarySwitchPreference mCarrierLabel;

    @Override
    public int getMetricsCategory() {
        return SettingsEnums.DISPLAY;
    }

    @Override
    protected String getLogTag() {
        return TAG;
    }

    @Override
    protected int getPreferenceScreenResId() {
        return R.xml.display_settings;
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        mCarrierLabel = (SystemSettingPrimarySwitchPreference) findPreference("enable_custom_carrier_label");
        mCarrierLabel.setOnPreferenceClickListener(this);
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        if (preference == mCarrierLabel) {
            ContentResolver resolver = getActivity().getContentResolver();
            AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
            alert.setTitle(R.string.carrier_label_custom_title);
            alert.setMessage(R.string.carrier_label_custom_explain);
            LinearLayout container = new LinearLayout(getActivity());
            container.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(55, 20, 55, 20);
            final EditText input = new EditText(getActivity());
            int maxLength = 25;
            input.setLayoutParams(lp);
            input.setGravity(android.view.Gravity.TOP | Gravity.START);
            String carrierLabel = Settings.System.getStringForUser(getContentResolver(),
                    Settings.System.CUSTOM_CARRIER_LABEL, UserHandle.USER_CURRENT);
            input.setText(TextUtils.isEmpty(carrierLabel) ? "" : carrierLabel);
            input.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLength)});
            container.addView(input);
            alert.setView(container);
            alert.setPositiveButton(getString(android.R.string.ok),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String value = ((Spannable) input.getText()).toString().trim();
                        Settings.System.putStringForUser(resolver,
                                Settings.System.CUSTOM_CARRIER_LABEL,
                                value, UserHandle.USER_CURRENT);
                    }
            });
            alert.setNegativeButton(getString(android.R.string.cancel), null);
            alert.show();
        }
        return true;
    }

    @Override
    protected List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        return buildPreferenceControllers(context, getSettingsLifecycle());
    }

    @Override
    public int getHelpResource() {
        return R.string.help_uri_display;
    }

    private static List<AbstractPreferenceController> buildPreferenceControllers(
            Context context, Lifecycle lifecycle) {
        final List<AbstractPreferenceController> controllers = new ArrayList<>();
        controllers.add(new CameraGesturePreferenceController(context));
        controllers.add(new LiftToWakePreferenceController(context));
        controllers.add(new PocketJudgePreferenceController(context));
        controllers.add(new TapToWakePreferenceController(context));
        controllers.add(new VrDisplayPreferenceController(context));
        controllers.add(new ShowOperatorNamePreferenceController(context));
        controllers.add(new ThemePreferenceController(context));
        controllers.add(new BrightnessLevelPreferenceController(context, lifecycle));
        controllers.add(new EnableBlursPreferenceController(context,
                BlurUtils.supportsBlursOnWindows()));
        return controllers;
    }

    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
            new BaseSearchIndexProvider(R.xml.display_settings) {

                @Override
                public List<String> getNonIndexableKeys(Context context) {
                    List<String> keys = super.getNonIndexableKeys(context);
                    LineageHardwareManager hardware = LineageHardwareManager.getInstance(context);
                    if (!hardware.isSupported(
                            LineageHardwareManager.FEATURE_HIGH_TOUCH_POLLING_RATE)) {
                        keys.add(KEY_HIGH_TOUCH_POLLING_RATE);
                    }
                    if (!hardware.isSupported(
                            LineageHardwareManager.FEATURE_HIGH_TOUCH_SENSITIVITY)) {
                        keys.add(KEY_HIGH_TOUCH_SENSITIVITY);
                    }
                    if (!context.getResources().getBoolean(
                            com.android.internal.R.bool.config_proximityCheckOnWake)) {
                        keys.add(KEY_PROXIMITY_ON_WAKE);
                    }
                    return keys;
                }

                @Override
                public List<AbstractPreferenceController> createPreferenceControllers(
                        Context context) {
                    return buildPreferenceControllers(context, null);
                }
            };
}
