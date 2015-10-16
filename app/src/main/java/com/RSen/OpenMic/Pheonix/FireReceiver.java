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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.widget.Toast;

/**
 * This is the "fire" BroadcastReceiver for a Locale Plug-in setting.
 *
 * @see com.twofortyfouram.locale.Intent#ACTION_FIRE_SETTING
 * @see com.twofortyfouram.locale.Intent#EXTRA_BUNDLE
 */
public final class FireReceiver extends BroadcastReceiver {

    /**
     * @param context {@inheritDoc}.
     * @param intent  the incoming
     *                {@link com.twofortyfouram.locale.Intent#ACTION_FIRE_SETTING}
     *                Intent. This should contain the
     *                {@link com.twofortyfouram.locale.Intent#EXTRA_BUNDLE} that was
     *                saved by {@link EditActivity} and later broadcast by Locale.
     */
    @Override
    public void onReceive(final Context context, final Intent intent) {
        try {
            String action = intent.getStringExtra("Action");
            final Intent myServiceIntent = new Intent(
                    context.getApplicationContext(), MyService.class);
            MyLog.l("TaskerFire: " + action, context);
            if (action.equals(context.getString(R.string.enable_screen_off))) {
                PreferenceManager
                        .getDefaultSharedPreferences(
                                context.getApplicationContext()).edit()
                        .putBoolean("listen_screen_off", true).commit();
                if (MyService.isRunning) {
                    MyService.ui.stop();
                    MyService.ui = new AudioUI(context.getApplicationContext());
                }

            } else if (action.equals(context
                    .getString(R.string.disable_screen_off))) {
                PreferenceManager
                        .getDefaultSharedPreferences(
                                context.getApplicationContext()).edit()
                        .putBoolean("listen_screen_off", false).commit();
                if (MyService.isRunning) {
                    MyService.ui.stop();
                    MyService.ui = new AudioUI(context.getApplicationContext());
                }
            } else if ((MyService.isRunning && action.equals(context
                    .getString(R.string.toggle)))
                    || action.equals(context.getString(R.string.stop))) {
                context.stopService(new Intent(context, ScreenReceiversService.class));
                if (WakelockManager.timeoutAltered) {
                    WakelockManager.restoreScreenTimeout(context);
                }
                try {
                    AudioUI.lock.reenableKeyguard();
                } catch (Exception e) {
                }
                if (PreferenceManager.getDefaultSharedPreferences(
                        context).getBoolean(
                        "listen_only_screen_off", false)) {
                    MyService.isRunning = false;

                    MainActivity.listenScreenOffActivated = false;
                } else {
                    context.stopService(myServiceIntent);
                }
            } else {
                context.startService(new Intent(context, ScreenReceiversService.class));

                if (PreferenceManager.getDefaultSharedPreferences(
                        context).getBoolean(
                        "listen_only_screen_off", false)) {
                    MyService.isRunning = true;

                    MainActivity.listenScreenOffActivated = true;
                } else {
                    context.startService(myServiceIntent);

                }
            }
        } catch (Exception e) {
            MyLog.l("TaskerFire failed" + e.getMessage(), context);
            Toast.makeText(context, "Tasker Fire Failed", Toast.LENGTH_SHORT)
                    .show();
        }
    }
}