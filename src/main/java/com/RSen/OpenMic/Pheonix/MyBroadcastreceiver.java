package com.RSen.OpenMic.Pheonix;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;

public class MyBroadcastreceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean(
                "start_on_boot", false)) {
            context.startService(new Intent(context, ScreenReceiversService.class));

            if (PreferenceManager.getDefaultSharedPreferences(
                    context).getBoolean(
                    "listen_only_screen_off", false)) {
                MyService.isRunning = true;

                MainActivity.listenScreenOffActivated = true;
            } else {
                if (PreferenceManager.getDefaultSharedPreferences(context).getString("speech_engine", "google").equals("pocketsphinx")) {
                    CheckIfAppBlackListedService.blacklisteddetected = false;
                    context.startService(new Intent(context, CheckIfAppBlackListedService.class));
                }
                Intent startServiceIntent = new Intent(context, MyService.class);
                context.startService(startServiceIntent);
            }
        }
    }
}