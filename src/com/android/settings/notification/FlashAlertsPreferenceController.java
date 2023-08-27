/*
 * Copyright (C) 2023 Havoc-OS
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

package com.android.settings.notification;

import android.content.Context;

import com.android.internal.util.derp.derpUtils;
import com.android.settings.core.BasePreferenceController;

/**
 * Feature level screen for bubbles, available through notification menu.
 * Allows user to turn bubbles on or off for the device.
 */
public class FlashAlertsPreferenceController extends BasePreferenceController {

    public FlashAlertsPreferenceController(Context context, String key) {
        super(context, key);
    }

    @Override
    public int getAvailabilityStatus() {
        return derpUtils.deviceHasFlashlight(mContext) ? AVAILABLE : UNSUPPORTED_ON_DEVICE;
    }
}
