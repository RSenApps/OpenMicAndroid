package com.RSen.OpenMic.Pheonix.Cards;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.format.DateUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.RSen.OpenMic.Pheonix.AudioUI;
import com.RSen.OpenMic.Pheonix.GoogleSpeechRecognizer;
import com.RSen.OpenMic.Pheonix.R;
import com.RSen.OpenMic.Pheonix.Refreshable;

import java.util.ArrayList;
import java.util.List;

import it.gmariotti.cardslib.library.internal.CardHeader;

/**
 * Created by Ryan on 6/11/2014.
 */
public class RunningCard extends MyListCard {
    public RunningCard(Context context, Refreshable refreshCallback) {
        super(context, refreshCallback);
    }

    @Override
    protected CardHeader initCardHeader() {
        CardHeader header = new CardHeader(getContext());
        header.setTitle(getContext().getString(R.string.running));
        return header;
    }

    @Override
    protected List<ListObject> initChildren() {
        List<ListObject> mObjects = new ArrayList<ListObject>();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        SettingsCardListObject s1 = new SettingsCardListObject(this);
        s1.italicized = getContext().getString(R.string.started);
        s1.normalText = "";
        s1.showButton = false;
        SettingsCardListObject s2 = new SettingsCardListObject(this);
        s2.italicized = getContext().getString(R.string.activated);
        s2.normalText = getContext().getString(R.string.no_times);
        s2.showButton = false;
        mObjects.add(s1);
        mObjects.add(s2);
        if (prefs.getString("speech_engine", "google").equals("google")) {
            SettingsCardListObject s3 = new SettingsCardListObject(this);
            s3.italicized = getContext().getString(R.string.last_heard);
            s3.normalText = getContext().getString(R.string.nothing_yet);
            s3.showButton = false;
            mObjects.add(s3);
        }


        return mObjects;
    }


    @Override
    public View setupChildView(int childPosition, ListObject object, View convertView, ViewGroup parent) {
        TextView italicized = (TextView) convertView.findViewById(R.id.italicized);
        final TextView normalText = (TextView) convertView.findViewById(R.id.normalText);
        ImageButton button = (ImageButton) convertView.findViewById(R.id.button);


        SettingsCardListObject s1 = (SettingsCardListObject) object;
        if (childPosition == 0) {
            final Handler handler = new Handler();
            final Context context = getContext();
            Runnable updateTime = new Runnable() {
                @Override
                public void run() {
                    normalText.setText(DateUtils.getRelativeTimeSpanString(PreferenceManager.getDefaultSharedPreferences(context).getLong("lastStartedTime", System.currentTimeMillis()), System.currentTimeMillis(), (long) 0).toString());
                    handler.postDelayed(this, 1000);
                }
            };
            updateTime.run();
        } else if (childPosition == 1) {
            final Handler handler = new Handler();
            Runnable updateTime = new Runnable() {
                @Override
                public void run() {
                    normalText.setText(AudioUI.activationCount + " " + getContext().getString(R.string.times));
                    handler.postDelayed(this, 2000);
                }
            };
            updateTime.run();
        } else if (childPosition == 2) {
            final Handler handler = new Handler();
            Runnable updateTime = new Runnable() {
                @Override
                public void run() {
                    normalText.setText(GoogleSpeechRecognizer.lastHeard);
                    handler.postDelayed(this, 500);
                }
            };
            updateTime.run();

        }
        if (s1.showButton) {
            button.setImageResource(s1.buttonDrawableId);

            button.setOnClickListener(s1.onClick);
        } else {
            button.setVisibility(View.GONE);
        }
        italicized.setText(s1.italicized);


        return convertView;
    }

    @Override
    public void preferenceSet() {
        refreshCallback.refresh();
    }
}
