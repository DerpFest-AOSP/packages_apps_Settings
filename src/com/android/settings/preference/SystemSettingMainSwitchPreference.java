package com.android.settings.preference;

import android.content.Context;
import android.util.AttributeSet;

import org.derpfest.support.preferences.SystemSettingsStore;

import com.android.settingslib.widget.MainSwitchPreference;

public class SystemSettingMainSwitchPreference extends MainSwitchPreference {

    public SystemSettingMainSwitchPreference(Context context, AttributeSet attrs,
            int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setPreferenceDataStore(new SystemSettingsStore(context.getContentResolver()));
    }

    public SystemSettingMainSwitchPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setPreferenceDataStore(new SystemSettingsStore(context.getContentResolver()));
    }

    public SystemSettingMainSwitchPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setPreferenceDataStore(new SystemSettingsStore(context.getContentResolver()));
    }

    public SystemSettingMainSwitchPreference(Context context) {
        super(context);
        setPreferenceDataStore(new SystemSettingsStore(context.getContentResolver()));
    }
}
