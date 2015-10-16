package com.RSen.OpenMic.Pheonix;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

public class WaveController implements SensorEventListener {
    private static SensorManager mSensorManager;
    private WaveControlListener listener;
    private Sensor mProximity;

    private double distanceThreshold;
    private int timeToCompleteWave = 2000;
    // false = far, true = near
    private boolean lastMeasurement = false;
    private int numberOfChangesSoFar = 0; // need 4 total to complete wave
    private int numberOfChangesRequired;

    public WaveController(Context context, WaveControlListener listener) {
        this.listener = listener;

        if (mSensorManager == null) {
            mSensorManager = (SensorManager) context
                    .getSystemService(Context.SENSOR_SERVICE);
        }
        mProximity = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        if (mProximity == null) {
            Toast.makeText(context,
                    "Sorry your device does not support Wave control",
                    Toast.LENGTH_LONG).show();
            return;
        }
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        numberOfChangesRequired = prefs.getInt("wavesRequired", 2) * 2;
        timeToCompleteWave = prefs.getInt("wavesTime", 2) * 1000;
        distanceThreshold = mProximity.getMaximumRange() * .8;
        mSensorManager.registerListener(this, mProximity,
                SensorManager.SENSOR_DELAY_UI);
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

        float distance = event.values[0];
        if (distance > distanceThreshold && lastMeasurement) {
            lastMeasurement = false;
            numberOfChangesSoFar++;
        } else if (distance <= distanceThreshold && !lastMeasurement) {
            lastMeasurement = true;
            numberOfChangesSoFar++;
        }
        if (numberOfChangesSoFar >= numberOfChangesRequired) {
            listener.waveControlActivated();
            reset();
        } else if (numberOfChangesSoFar == 1) {
            new Thread(new Runnable() {

                @Override
                public void run() {
                    try {
                        Thread.sleep(timeToCompleteWave);
                    } catch (InterruptedException e) {
                    }
                    Log.d("wave", "resetting");
                    reset();
                }
            }).start();
        }
        Log.d("wave", "number of changes:" + numberOfChangesSoFar);
    }

    private void reset() {
        numberOfChangesSoFar = 0;
        lastMeasurement = false;
    }

}
