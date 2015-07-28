package com.RSen.OpenMic.Pheonix;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public abstract class SpeechRecognizer {
    protected boolean useBluetooth;
    protected BluetoothHelper myBluetoothHelper;

    public SpeechRecognizer(Context context, AudioUI uiReference) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        useBluetooth = prefs.getBoolean("bluetooth", false);
        if (useBluetooth) {
            myBluetoothHelper = new BluetoothHelper(context);
        }
    }

    public abstract void startListening();

    public abstract void stopListening();

    public abstract void stop();
}
