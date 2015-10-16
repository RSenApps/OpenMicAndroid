package com.RSen.OpenMic.Pheonix;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.BatteryManager;
import android.preference.PreferenceManager;

public class PowerConnectionReceiver extends BroadcastReceiver {
    public PowerConnectionReceiver() {

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        boolean listenCharging = prefs.getBoolean("listen_charging", false);
        boolean listenScreenOffCharging = prefs.getBoolean(
                "listen_screen_off_charging", false);
        if (listenCharging || listenScreenOffCharging) {
            if (intent.getAction() != Intent.ACTION_BATTERY_CHANGED) {
                context.getApplicationContext().registerReceiver(this,
                        new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
            } else {
                context.unregisterReceiver(this);
                registerReceiver(context);
                int status = intent
                        .getIntExtra(BatteryManager.EXTRA_STATUS, -1);

                int chargePlug = intent.getIntExtra(
                        BatteryManager.EXTRA_PLUGGED, -1);
                boolean usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
                boolean acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;
                final Intent myServiceIntent = new Intent(context,
                        MyService.class);
                boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING
                        || status == BatteryManager.BATTERY_STATUS_FULL
                        || usbCharge || acCharge;
                MyLog.l("IsCharging = " + isCharging
                        + ", MyService.isRunning = " + MyService.isRunning
                        + " Battery status = " + status + " Charge Status = "
                        + chargePlug, context);

                if (listenScreenOffCharging) {
                    if (isCharging) {
                        prefs.edit().putBoolean("listen_screen_off", true)
                                .commit();
                        context.startService(myServiceIntent);
                    } else {
                        prefs.edit().putBoolean("listen_screen_off", false)
                                .commit();
                        context.stopService(myServiceIntent);
                    }
                } else {
                    if (isCharging && !MyService.isRunning) {
                        context.startService(new Intent(context, ScreenReceiversService.class));

                        if (PreferenceManager.getDefaultSharedPreferences(
                                context).getBoolean(
                                "listen_only_screen_off", false)) {
                            MyService.isRunning = true;

                            MainActivity.listenScreenOffActivated = true;
                        } else {
                            context.startService(myServiceIntent);

                        }
                    } else if (!isCharging && MyService.isRunning) {
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
                    }
                }
            }
        }
    }

    private void registerReceiver(Context context) {
        IntentFilter filter = new IntentFilter(Intent.ACTION_POWER_CONNECTED);
        filter.addAction(Intent.ACTION_POWER_DISCONNECTED);
        context.registerReceiver(this, filter);
    }
}
