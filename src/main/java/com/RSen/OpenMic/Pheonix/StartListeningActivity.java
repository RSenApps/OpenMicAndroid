package com.RSen.OpenMic.Pheonix;

/*
 * Copyright 2013 two forty four a.m. LLC <http://www.twofortyfouram.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * <http://www.apache.org/licenses/LICENSE-2.0>
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;

/**
 * This is the "fire" BroadcastReceiver for a Locale Plug-in setting.
 *
 * @see com.twofortyfouram.locale.Intent#ACTION_FIRE_SETTING
 * @see com.twofortyfouram.locale.Intent#EXTRA_BUNDLE
 */
public final class StartListeningActivity extends Activity {

    /**
     * @param context {@inheritDoc}.
     * @param intent  the incoming
     *                {@link com.twofortyfouram.locale.Intent#ACTION_FIRE_SETTING}
     *                Intent. This should contain the
     *                {@link com.twofortyfouram.locale.Intent#EXTRA_BUNDLE} that was
     *                saved by {@link EditActivity} and later broadcast by Locale.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Intent myServiceIntent = new Intent(
                getApplicationContext(), MyService.class);
        startService(new Intent(this, ScreenReceiversService.class));

        if (PreferenceManager.getDefaultSharedPreferences(
                this).getBoolean(
                "listen_only_screen_off", false)) {
            MyService.isRunning = true;

            MainActivity.listenScreenOffActivated = true;
        } else {
            startService(myServiceIntent);

        }
        finish();
    }

}