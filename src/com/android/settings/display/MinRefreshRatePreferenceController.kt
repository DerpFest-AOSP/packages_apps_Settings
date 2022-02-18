/*
 * Copyright (C) 2020 The LineageOS Project
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

class MinRefreshRatePreferenceController(
    context: Context,
    lifecycle: Lifecycle?,
) : BasePreferenceController(context, KEY),
    LifecycleEventObserver,
    Preference.OnPreferenceChangeListener {

    private val handler = Handler(context.mainLooper)
    private val settingsObserver = object : ContentObserver(handler) {
        override fun onChange(selfChange: Boolean) {
            listPreference?.let { updateState(it) }
        }
    }

    private var listPreference: ListPreference? = null

    private val entries = mutableListOf<String>()
    private val values = mutableListOf<String>()

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
                    entries.add(context.getString(R.string.refresh_rate_placeholder, refreshRate))
                    values.add(refreshRate)
                }
            }
        }
        lifecycle?.addObserver(this)
    }

    override fun displayPreference(screen: PreferenceScreen) {
        super.displayPreference(screen)
        listPreference = screen.findPreference<ListPreference>(KEY)?.also {
            it.entries = entries.toTypedArray()
            it.entryValues = values.toTypedArray()
        }
    }

    override fun getAvailabilityStatus(): Int =
        if (mContext.resources.getBoolean(R.bool.config_show_refresh_rate_switch)
                && entries.size > 1) {
            AVAILABLE
        } else {
            UNSUPPORTED_ON_DEVICE
        }

    override fun updateState(preference: Preference) {
        val currentValue = Settings.System.getFloat(
            mContext.contentResolver,
            Settings.System.MIN_REFRESH_RATE,
            DEFAULT_REFRESH_RATE,
        )
        updateStateInternal(
            if (currentValue == NO_CONFIG)
                DEFAULT_REFRESH_RATE
            else
                currentValue
        )
    }

    private fun updateStateInternal(currentValue: Float) {
        listPreference?.let {
            val refreshRate = currentValue.toInt().toString()
            val index = it.findIndexOfValue(refreshRate)
            if (index > -1) {
                it.setValueIndex(index)
            }
            // Show actual value as summary irrespective of whether it is
            // present in entries or not.
            it.summary = mContext.getString(
                R.string.refresh_rate_placeholder, refreshRate)
        }
    }

    override fun onStateChanged(owner: LifecycleOwner, event: Event) {
        if (event == Event.ON_START) {
            mContext.contentResolver.registerContentObserver(
                Settings.System.getUriFor(Settings.System.MIN_REFRESH_RATE),
                false,
                settingsObserver,
            )
        } else if (event == Event.ON_STOP) {
            mContext.contentResolver.unregisterContentObserver(settingsObserver)
        }
    }

    override fun onPreferenceChange(preference: Preference, newValue: Any): Boolean {
        val refreshRate = (newValue as String).toFloat()
        val changed = Settings.System.putFloat(
            mContext.contentResolver,
            Settings.System.MIN_REFRESH_RATE,
            refreshRate,
        )
        if (changed) {
            updateStateInternal(refreshRate)
        }
        return changed
    }

    companion object {
        private const val KEY = "min_refresh_rate"
        private const val TAG = "MinRefreshRatePC"

        private val refreshRateRegex = Regex("[0-9]+")

        private const val INVALID_REFRESH_RATE = -1f
        private const val NO_CONFIG = 0f
        private const val DEFAULT_REFRESH_RATE = 60f
    }
}
