package com.RSen.OpenMic.Pheonix;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class KeyguardReceiver extends BroadcastReceiver {
    public static boolean keyguardEnabled = false;

    public KeyguardReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        keyguardEnabled = false;

    }
}
