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

package org.derpfest.settings.util;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.UserHandle;

public class PackageManagerUtils {

    public static String ACTION_DOZE = "org.lineageos.settings.device.DOZE_SETTINGS";

    public static boolean isIntentPresent(PackageManager pm, String name) {
        Intent intent = new Intent(name);
        for (ResolveInfo info : pm.queryIntentActivitiesAsUser(intent,
                PackageManager.MATCH_SYSTEM_ONLY, UserHandle.myUserId())) {
            if (info.activityInfo != null) {
                return true;
            }
        }

        return false;
    }

    public static boolean isCustomDozePresent(PackageManager pm) {
        return isIntentPresent(pm, ACTION_DOZE);
    }

}
