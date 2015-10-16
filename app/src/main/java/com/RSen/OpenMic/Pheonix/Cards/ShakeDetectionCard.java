package com.RSen.OpenMic.Pheonix.Cards;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.preference.PreferenceManager;
import android.view.View;

import com.RSen.OpenMic.Pheonix.CustomPreferenceRangeDialog;
import com.RSen.OpenMic.Pheonix.R;
import com.RSen.OpenMic.Pheonix.Refreshable;

import java.util.ArrayList;
import java.util.List;

import it.gmariotti.cardslib.library.internal.CardHeader;

/**
 * Created by Ryan on 6/11/2014.
 */
public class ShakeDetectionCard extends MyListCard {
    public ShakeDetectionCard(Context context, Refreshable refreshCallback) {
        super(context, refreshCallback);
    }

    @Override
    protected CardHeader initCardHeader() {
        SensorManager mSensorManager = (SensorManager) getContext()
                .getSystemService(Context.SENSOR_SERVICE);
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            return initCardHeader(getContext().getString(R.string.shake_detection), "shake", false);
        } else {
            CardHeader header = new CardHeader(getContext());
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
            prefs.edit().putBoolean("shake", false).commit();
            header.setTitle(getContext().getString(R.string.shake_detection_disabled));
            return header;
        }
    }

    @Override
    protected List<ListObject> initChildren() {
        SensorManager mSensorManager = (SensorManager) getContext()
                .getSystemService(Context.SENSOR_SERVICE);
        List<ListObject> mObjects = new ArrayList<ListObject>();
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {

            SettingsCardListObject s1 = new SettingsCardListObject(this);
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
            s1.italicized = getContext().getString(R.string.sensitivity);
            s1.normalText = prefs.getInt("shakeSensitivity", 5) + " " + getContext().getString(R.string.out_of_10);
            s1.buttonDrawableId = R.drawable.ic_action_content_edit;
            s1.onClick = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new CustomPreferenceRangeDialog(view.getContext(), "shakeSensitivity", 5, 10, getContext().getString(R.string.shake_sensitivity), ShakeDetectionCard.this);
                }
            };

            mObjects.add(s1);
        } else {
            SettingsCardListObject s1 = new SettingsCardListObject(this);
            s1.italicized = getContext().getString(R.string.unavailable);
            s1.normalText = getContext().getString(R.string.no_accelerometer);
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
