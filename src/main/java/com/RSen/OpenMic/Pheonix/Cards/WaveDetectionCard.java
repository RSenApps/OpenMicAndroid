package com.RSen.OpenMic.Pheonix.Cards;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.preference.PreferenceManager;
import android.view.View;

import com.RSen.OpenMic.Pheonix.CustomPreferenceIntDialog;
import com.RSen.OpenMic.Pheonix.R;
import com.RSen.OpenMic.Pheonix.Refreshable;

import java.util.ArrayList;
import java.util.List;

import it.gmariotti.cardslib.library.internal.CardHeader;

/**
 * Created by Ryan on 6/11/2014.
 */
public class WaveDetectionCard extends MyListCard {
    public WaveDetectionCard(Context context, Refreshable refreshCallback) {
        super(context, refreshCallback);
    }

    @Override
    protected CardHeader initCardHeader() {

        //Add Header
        SensorManager mSensorManager = (SensorManager) getContext()
                .getSystemService(Context.SENSOR_SERVICE);
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY) != null) {
            return initCardHeader(getContext().getString(R.string.wave_detection), "wave", false);
        } else {
            CardHeader header = new CardHeader(getContext());
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
            prefs.edit().putBoolean("wave", false).commit();
            header.setTitle(getContext().getString(R.string.wave_detection_disabled));
            return header;
        }
    }

    @Override
    protected List<ListObject> initChildren() {

        List<ListObject> mObjects = new ArrayList<ListObject>();
        SensorManager mSensorManager = (SensorManager) getContext()
                .getSystemService(Context.SENSOR_SERVICE);
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY) != null) {
            SettingsCardListObject s1 = new SettingsCardListObject(this);
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
            s1.italicized = getContext().getString(R.string.wave);
            s1.normalText = prefs.getInt("wavesRequired", 2) + " " + getContext().getString(R.string.times_over_front_camera);
            s1.buttonDrawableId = R.drawable.ic_action_content_edit;
            s1.onClick = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new CustomPreferenceIntDialog(view.getContext(), "wavesRequired", 2, getContext().getString(R.string.number_waves_required), WaveDetectionCard.this);
                }
            };
            SettingsCardListObject s2 = new SettingsCardListObject(this);
            s2.italicized = getContext().getString(R.string.in);
            s2.normalText = (prefs.getInt("wavesTime", 4)) + " " + getContext().getString(R.string.seconds);
            s2.buttonDrawableId = R.drawable.ic_action_content_edit;
            s2.onClick = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!showDonateDialogIfNotDonate()) {
                        new CustomPreferenceIntDialog(view.getContext(), "wavesTime", 4, getContext().getString(R.string.time_in_seconds_to_complete), WaveDetectionCard.this);
                    }
                }
            };
            mObjects.add(s1);
            mObjects.add(s2);
        } else {
            SettingsCardListObject s1 = new SettingsCardListObject(this);
            s1.italicized = getContext().getString(R.string.unavailable);
            s1.normalText = getContext().getString(R.string.no_proximity_sensor);
            s1.showButton = false;
            mObjects.add(s1);
        }
        return mObjects;
    }

    @Override
    public void preferenceSet() {
        refreshCallback.refresh();
    }

}
