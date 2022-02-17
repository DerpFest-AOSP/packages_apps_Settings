/*
 * Copyright (C) 2020 The Android Open Source Project
 * Copyright (C) 2021 The LineageOS Project
 * Copyright (C) 2022 AOSP-Krypton Project
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

package com.android.settings.display

import android.content.Context
import android.database.ContentObserver
import android.hardware.display.DisplayManager
import android.os.Handler
import android.provider.DeviceConfig
import android.provider.Settings
import android.util.Log
import android.view.Display

import androidx.lifecycle.Lifecycle.Event
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceScreen

import com.android.settings.core.BasePreferenceController
import com.android.settings.R
import com.android.settingslib.core.lifecycle.Lifecycle

class MaxRefreshRatePreferenceController(
    context: Context,
    lifecycle: Lifecycle?,
) : BasePreferenceController(context, KEY),
    LifecycleEventObserver,
    Preference.OnPreferenceChangeListener {

    private val handler = Handler(context.mainLooper)
    private val deviceConfigObserver = DeviceConfigObserver()
    private val settingsObserver = object : ContentObserver(handler) {
        override fun onChange(selfChange: Boolean) {
            listPreference?.let { updateState(it) }
        }
    }

    private var listPreference: ListPreference? = null

    private var defaultPeakRefreshRate = getDefaultPeakRefreshRate()
    private val values = mutableListOf<Float>()

    init {
        val display: Display? = context.getSystemService(
            DisplayManager::class.java).getDisplay(Display.DEFAULT_DISPLAY)
        if (display == null) {
            Log.e(TAG, "No valid default display device")
        } else {
            val mode = display.mode
            display.supportedModes.forEach {
                if (it.physicalWidth == mode.physicalWidth &&
                        it.physicalHeight == mode.physicalHeight) {
                    val refreshRate = refreshRateRegex.find(
                        it.refreshRate.toString())?.value ?: return@forEach
                    values.add(refreshRate.toFloat())
                }
            }
        }
        lifecycle?.addObserver(this)
    }

    private fun getDefaultPeakRefreshRate(): Float {
        val peakRefreshRate = DeviceConfig.getFloat(
            DeviceConfig.NAMESPACE_DISPLAY_MANAGER,
            DisplayManager.DeviceConfig.KEY_PEAK_REFRESH_RATE_DEFAULT,
            INVALID_REFRESH_RATE
        )
        return if (peakRefreshRate == INVALID_REFRESH_RATE) {
            mContext.resources.getInteger(
                com.android.internal.R.integer.config_defaultPeakRefreshRate).toFloat()
        } else {
            peakRefreshRate
        }
    }

    override fun displayPreference(screen: PreferenceScreen) {
        super.displayPreference(screen)
        listPreference = screen.findPreference<ListPreference>(KEY)
    }

    override fun getAvailabilityStatus(): Int =
        if (mContext.resources.getBoolean(R.bool.config_show_refresh_rate_switch)
                && values.size > 1) {
            AVAILABLE
        } else {
            UNSUPPORTED_ON_DEVICE
        }

    override fun updateState(preference: Preference) {
        val currentValue = Settings.System.getFloat(
            mContext.contentResolver,
            Settings.System.PEAK_REFRESH_RATE,
            defaultPeakRefreshRate,
        )
        updateStateInternal(currentValue)
    }

    private fun updateStateInternal(currentValue: Float) {
        val minRate = Settings.System.getFloat(
            mContext.contentResolver,
            Settings.System.MIN_REFRESH_RATE,
            DEFAULT_REFRESH_RATE,
        )
        listPreference?.let { preference ->
            preference.setEnabled(minRate <= currentValue)

            // There is no point in setting max refresh rate
            // below minimum refresh rate. So show only rates
            // greater than minimum rate.
            val restrictedValues = values.filter {
                it >= minRate
            }.map {
                it.toInt().toString()
            }
            preference.entryValues = restrictedValues.toTypedArray()
            preference.entries = restrictedValues.map {
                mContext.getString(R.string.refresh_rate_placeholder, it)
            }.toTypedArray()

            val refreshRate = currentValue.toInt().toString()
            val index = preference.findIndexOfValue(refreshRate)
            if (index != -1) {
                preference.setValueIndex(index)
            }
            // Show actual value as summary irrespective of whether it is
            // present in entries or not.
            preference.summary = mContext.getString(
                R.string.refresh_rate_placeholder, refreshRate)
        }
    }

    override fun onStateChanged(owner: LifecycleOwner, event: Event) {
        if (event == Event.ON_START) {
            deviceConfigObserver.startListening()
            with (mContext.contentResolver) {
                registerContentObserver(
                    Settings.System.getUriFor(Settings.System.MIN_REFRESH_RATE),
                    false,
                    settingsObserver,
                )
                registerContentObserver(
                    Settings.System.getUriFor(Settings.System.PEAK_REFRESH_RATE),
                    false,
                    settingsObserver,
                )
            }
        } else if (event == Event.ON_STOP) {
            mContext.contentResolver.unregisterContentObserver(settingsObserver)
            deviceConfigObserver.stopListening()
        }
    }

    override fun onPreferenceChange(preference: Preference, newValue: Any): Boolean {
        // ContentObserver will update the preference
        return Settings.System.putFloat(
            mContext.contentResolver,
            Settings.System.PEAK_REFRESH_RATE,
            (newValue as String).toFloat(),
        )
    }

    private inner class DeviceConfigObserver :
        DeviceConfig.OnPropertiesChangedListener {

        fun startListening() {
            DeviceConfig.addOnPropertiesChangedListener(
                DeviceConfig.NAMESPACE_DISPLAY_MANAGER,
                {
                    handler.post(it)
                } /* Executor */,
                this /* Listener */)
        }

        fun stopListening() {
            DeviceConfig.removeOnPropertiesChangedListener(this)
        }

        override fun onPropertiesChanged(properties: DeviceConfig.Properties) {
            // Got notified if any property has been changed in NAMESPACE_DISPLAY_MANAGER. The
            // KEY_PEAK_REFRESH_RATE_DEFAULT value could be added, changed, removed or unchanged.
            // Just force a UI update for any case.
            defaultPeakRefreshRate = getDefaultPeakRefreshRate()
            listPreference?.let { updateState(it) }
        }
    }

    companion object {
        private const val KEY = "max_refresh_rate"
        private const val TAG = "MaxRefreshRatePC"

        private val refreshRateRegex = Regex("[0-9]+")

        private const val INVALID_REFRESH_RATE = -1f
        private const val DEFAULT_REFRESH_RATE = 60f
    }
}
