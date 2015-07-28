package com.RSen.OpenMic.Pheonix;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

public class ScreenReceiversService extends Service {
    private static ScreenReceiver mReceiver = new ScreenReceiver();
    private static KeyguardReceiver keyguardReceiver = new KeyguardReceiver();

    public ScreenReceiversService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        IntentFilter filter = new IntentFilter(
                Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        getApplicationContext().registerReceiver(mReceiver,
                filter);
        filter = new IntentFilter(Intent.ACTION_USER_PRESENT);
        getApplicationContext().registerReceiver(
                keyguardReceiver, filter);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {

        try {
            getApplicationContext().unregisterReceiver(
                    mReceiver);
            getApplicationContext().unregisterReceiver(
                    keyguardReceiver);
        } catch (Exception e) {
        }
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }
}
