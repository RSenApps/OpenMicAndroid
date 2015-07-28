package com.RSen.OpenMic.Pheonix;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.WindowManager;
import android.widget.Toast;

public class WakeupActivity extends Activity {
    public static boolean useNewTask = false;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN
                        | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                        | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
                        | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                        | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
        );

        // Your code here - Or if it doesn't trigger, see below
    }

    @Override
    public void onAttachedToWindow() {

        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {

            @Override
            public void run() {
                Intent i = new Intent(WakeupActivity.this, OverlayService.class);


                if (PreferenceManager.getDefaultSharedPreferences(WakeupActivity.this).getBoolean("overlay", true))

                {
                    startService(i);
                }

                final Intent intent = new Intent("android.intent.action.MAIN");
                intent.setComponent(new ComponentName(
                        "com.google.android.googlequicksearchbox",
                        "com.google.android.googlequicksearchbox.VoiceSearchActivity"));
                intent.setFlags(Intent.FLAG_FROM_BACKGROUND | Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);

                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                try {
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(
                            WakeupActivity.this,
                            getString(R.string.substandard_google_now),
                            Toast.LENGTH_LONG).show();
                }


            }
        };
        handler.postDelayed(runnable, 100);

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {

        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {

        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Intent i = new Intent(WakeupActivity.this,
                CheckIfAppBlackListedService.class);
        startService(i);

        ScreenReceiver.isActivating = false;

        CheckIfAppBlackListedService.blacklisteddetected = true;
        if (!PreferenceManager.getDefaultSharedPreferences(this).getBoolean("use_gettasks", true)) {
            CheckIfAppBlackListedService.checkingForRelockOnly = true;
        }

    }
}