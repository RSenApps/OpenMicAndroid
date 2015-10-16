package com.RSen.OpenMic.Pheonix;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.preference.PreferenceManager;
import android.widget.Toast;

public class ShakeController implements SensorEventListener {
    private static SensorManager mSensorManager;
    private ShakeControlListener listener;
    private Sensor mAccSensor;
    private long lastUpdate = 0;
    private int SHAKE_THRESHOLD;
    private float last_x = 0;
    private float last_y = 0;
    private float last_z = 0;

    public ShakeController(Context context, ShakeControlListener listener) {
        this.listener = listener;
        int sensitivity = PreferenceManager.getDefaultSharedPreferences(context).getInt("shakeSensitivity", 5);
        SHAKE_THRESHOLD = 500 + 2000 * ((10 - sensitivity) / 10); //5 is 1500 and there is a range of 700 to 2500
        if (mSensorManager == null) {
            mSensorManager = (SensorManager) context
                    .getSystemService(Context.SENSOR_SERVICE);
        }
        mAccSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (mAccSensor == null) {
            Toast.makeText(context,
                    "Sorry your device does not support Shake control",
                    Toast.LENGTH_LONG).show();
            return;
        }

        mSensorManager.registerListener(this, mAccSensor,
                SensorManager.SENSOR_DELAY_GAME);
    }

    public void stop() {
        try {
            mSensorManager.unregisterListener(this);
        } catch (Exception e) {
        }
    }

    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public final void onSensorChanged(SensorEvent event) {

        long curTime = System.currentTimeMillis();
        // only allow one update every 100ms.
        if ((curTime - lastUpdate) > 100) {
            long diffTime = (curTime - lastUpdate);
            lastUpdate = curTime;

            float x = event.values[SensorManager.DATA_X];
            float y = event.values[SensorManager.DATA_Y];
            float z = event.values[SensorManager.DATA_Z];

            float speed = Math.abs(x + y + z - last_x - last_y - last_z) / diffTime * 10000;

            if (speed > SHAKE_THRESHOLD) {
                listener.shakeControlActivated();

            }
            last_x = x;
            last_y = y;
            last_z = z;
        }

    }

}
