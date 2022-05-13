package com.android.settings.sound;

import android.os.Bundle;

import androidx.preference.Preference;

import com.android.internal.logging.nano.MetricsProto;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

public class HapticSettings extends SettingsPreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        addPreferencesFromResource(R.xml.haptic_settings);
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.DERP;
    }
}
