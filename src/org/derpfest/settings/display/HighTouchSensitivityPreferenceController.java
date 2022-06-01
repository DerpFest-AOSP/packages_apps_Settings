/*
 * Copyright (C) 2022 The LeafOS Project
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
package org.derpfest.settings.display;

import android.content.Context;
import android.os.RemoteException;

import java.util.NoSuchElementException;

import org.derpfest.settings.controller.BaseSystemSwitchPreferenceController;

import vendor.lineage.touch.V1_0.IGloveMode;

public class HighTouchSensitivityPreferenceController extends BaseSystemSwitchPreferenceController  {

    private IGloveMode mGloveMode;

    public HighTouchSensitivityPreferenceController(Context context, String key) {
        super(context, key);

        try {
            mGloveMode = IGloveMode.getService();
        } catch (RemoteException ex) {
            ex.printStackTrace();
        } catch (NoSuchElementException ex) {
            // service not available
        }
    }

    @Override
    public int getAvailabilityStatus() {
        return mGloveMode != null ? AVAILABLE : UNSUPPORTED_ON_DEVICE;
    }

    @Override
    public boolean setChecked(boolean isChecked) {
        try {
            mGloveMode.setEnabled(isChecked);
            return super.setChecked(isChecked);
        } catch (RemoteException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public static void enableStatic(boolean enable) {
        try {
            IGloveMode gloveMode = IGloveMode.getService();
            gloveMode.setEnabled(enable);
        } catch (RemoteException ex) {
            ex.printStackTrace();
        } catch (NoSuchElementException ex) {
            // service not available
        }
    }
}
