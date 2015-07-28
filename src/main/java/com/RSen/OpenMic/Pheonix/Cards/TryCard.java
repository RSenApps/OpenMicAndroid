package com.RSen.OpenMic.Pheonix.Cards;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.RSen.OpenMic.Pheonix.R;
import com.RSen.OpenMic.Pheonix.Refreshable;

import java.util.ArrayList;
import java.util.List;

import it.gmariotti.cardslib.library.internal.CardHeader;

/**
 * Created by Ryan on 6/11/2014.
 */
public class TryCard extends MyListCard {
    public TryCard(Context context, Refreshable refreshCallback) {
        super(context, refreshCallback);
    }

    @Override
    protected CardHeader initCardHeader() {
        CardHeader header = new CardHeader(getContext());
        //header.setOtherButtonDrawable(android.R.drawable.ic_menu_preferences);
        //header.setOtherButtonVisible(true);
        header.setTitle(getContext().getString(R.string.try_word));
        return header;
    }

    @Override
    protected List<ListObject> initChildren() {
        List<ListObject> mObjects = new ArrayList<ListObject>();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        if (prefs.getBoolean("listenHotword", true)) {
            SettingsCardListObject s1 = new SettingsCardListObject(this);
            s1.italicized = getContext().getString(R.string.saying);
            s1.normalText = prefs.getString("hot_phrase", "Okay Google");
            s1.showButton = false;
            mObjects.add(s1);
        }
        /*TODO
        SettingsCardListObject s2 = new SettingsCardListObject(this);
        s2.italicized = "Saying";
        s2.normalText = "Turn on the Lights, Open Switchr";
        s2.showButton = false;
        */
        if (prefs.getBoolean("wave", false)) {
            SettingsCardListObject s3 = new SettingsCardListObject(this);
            s3.italicized = getContext().getString(R.string.waving);
            s3.normalText = getContext().getString(R.string.hand) + prefs.getInt("wavesRequired", 2) + getContext().getString(R.string.times_over_front_camera);
            s3.showButton = false;
            mObjects.add(s3);
        }
        if (prefs.getBoolean("shake", false)) {
            SettingsCardListObject s4 = new SettingsCardListObject(this);
            s4.italicized = getContext().getString(R.string.shaking);
            s4.normalText = getContext().getString(R.string.phone);
            s4.showButton = false;
            mObjects.add(s4);
        }


        return mObjects;
    }

    @Override
    public void preferenceSet() {
        refreshCallback.refresh();
    }

}
