package com.RSen.OpenMic.Pheonix.Cards;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;

import com.RSen.OpenMic.Pheonix.MainActivity;
import com.RSen.OpenMic.Pheonix.R;
import com.RSen.OpenMic.Pheonix.Refreshable;
import com.apptentive.android.sdk.Apptentive;

import java.util.ArrayList;
import java.util.List;

import it.gmariotti.cardslib.library.internal.CardHeader;

/**
 * Created by Ryan on 6/11/2014.
 */
public class HavingTroubleCard extends MyListCard {
    public HavingTroubleCard(Context context, Refreshable refreshCallback) {
        super(context, refreshCallback);
    }

    @Override
    protected CardHeader initCardHeader() {
        CardHeader header = new CardHeader(getContext());
        //header.setOtherButtonDrawable(android.R.drawable.ic_menu_preferences);
        //header.setOtherButtonVisible(true);
        header.setTitle(getContext().getString(R.string.having_trouble));
        return header;
    }

    @Override
    protected List<ListObject> initChildren() {
        List<ListObject> mObjects = new ArrayList<ListObject>();
        SettingsCardListObject s1 = new SettingsCardListObject(this);
        Context context = getContext();
        s1.italicized = context.getString(R.string.read);
        s1.normalText = context.getString(R.string.knowledgebase);
        s1.buttonDrawableId = R.drawable.ic_action_action_about;
        s1.onClick = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "http://Help.RSenApps.com";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                getContext().startActivity(i);
            }
        };
        SettingsCardListObject s2 = new SettingsCardListObject(this);
        s2.italicized = context.getString(R.string.read);
        s2.normalText = context.getString(R.string.xda_thread);
        s2.buttonDrawableId = R.drawable.ic_action_action_about;
        s2.onClick = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "http://forum.xda-developers.com/showthread.php?t=2450131";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                getContext().startActivity(i);
            }
        };
        SettingsCardListObject s3 = new SettingsCardListObject(this);
        s3.italicized = context.getString(R.string.watch);
        s3.normalText = context.getString(R.string.video);
        s3.buttonDrawableId = R.drawable.ic_action_device_access_video;
        s3.onClick = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://www.youtube.com/watch?v=njDJODbSjhM";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                getContext().startActivity(i);
            }
        };
        SettingsCardListObject s4 = new SettingsCardListObject(this);
        s4.italicized = context.getString(R.string.contact);
        s4.normalText = context.getString(R.string.developer);
        s4.buttonDrawableId = R.drawable.ic_action_social_chat;
        s4.onClick = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Apptentive.showMessageCenter((MainActivity) getContext(), MainActivity.addSharedPreferencesToLog(getContext(), false));
            }
        };

        mObjects.add(s1);
        mObjects.add(s2);
        mObjects.add(s3);
        mObjects.add(s4);
        return mObjects;
    }

    @Override
    public void preferenceSet() {
        refreshCallback.refresh();
    }

}
