package com.RSen.OpenMic.Pheonix;

import android.content.Context;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;

public class WakelockManager {
    public static boolean timeoutAltered = false;
    private static WakeLock wakeLock;
    private static WakeLock screenOnWakelock;
    private static int originalTimeout;

    public static void acquireWakelock(Context context) {
        PowerManager pm = (PowerManager) context
                .getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "OpenMic");
        wakeLock.acquire();

    }

    public static void releaseWakelock() {
        if (wakeLock != null) {
            if (wakeLock.isHeld()) {
                wakeLock.release();
            }
        }
    }

    public static void turnOnScreen(Context context) {
        PowerManager pm = (PowerManager) context
                .getSystemService(Context.POWER_SERVICE);
        if (pm.isScreenOn()) {
            return;
        }
        if (screenOnWakelock == null) {
            screenOnWakelock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK
                    | PowerManager.ACQUIRE_CAUSES_WAKEUP, "Open Mic screen");
        }
        if (screenOnWakelock.isHeld()) {
            screenOnWakelock.release();
        } else {
            screenOnWakelock.acquire(15000);
        }
    }

    public static void changeScreenTimeout(Context context, int timeout) {
        timeoutAltered = true;
        try {
            originalTimeout = Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT);
        } catch (SettingNotFoundException e) {
        }
        Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, timeout);
    }

    public static void restoreScreenTimeout(Context context) {
        timeoutAltered = false;
        Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, originalTimeout);
    }
}
