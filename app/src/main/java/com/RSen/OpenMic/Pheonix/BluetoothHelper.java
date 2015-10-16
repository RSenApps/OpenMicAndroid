package com.RSen.OpenMic.Pheonix;

import android.content.Context;

public class BluetoothHelper extends BluetoothHeadsetUtils {
    public boolean isStarted = false;

    public BluetoothHelper(Context context) {
        super(context);
    }

    @Override
    public boolean start() {
        isStarted = true;
        return super.start();
    }

    @Override
    public void stop() {
        isStarted = false;
        super.stop();
    }

    @Override
    public void onScoAudioDisconnected() {
        // Cancel speech recognizer if desired
    }

    @Override
    public void onScoAudioConnected() {
        // Should start speech recognition here if not already started
    }

    @Override
    public void onHeadsetDisconnected() {

    }

    @Override
    public void onHeadsetConnected() {

    }
}

