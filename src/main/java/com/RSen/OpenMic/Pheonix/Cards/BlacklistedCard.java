package com.RSen.OpenMic.Pheonix.Cards;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;

import com.RSen.OpenMic.Pheonix.BlacklistActivity;
import com.RSen.OpenMic.Pheonix.R;
import com.RSen.OpenMic.Pheonix.Refreshable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import it.gmariotti.cardslib.library.internal.CardHeader;

/**
 * Created by Ryan on 6/11/2014.
 */
public class BlacklistedCard extends MyListCard {
    public BlacklistedCard(Context context, Refreshable refreshCallback) {
        super(context, refreshCallback);
    }

    @Override
    protected CardHeader initCardHeader() {

        CardHeader header = new CardHeader(getContext());
        header.setTitle(getContext().getString(R.string.blacklisted_apps));
        return header;
    }

    @Override
    protected List<ListObject> initChildren() {
        Context context = getContext();
        List<ListObject> mObjects = new ArrayList<ListObject>();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        SettingsCardListObject s1 = new SettingsCardListObject(this);
        if (prefs.getBoolean("blacklist_mic", true)) {
            s1.italicized = context.getString(R.string.blacklisted);
        } else {
            s1.italicized = context.getString(R.string.not_blacklisted);
        }
        s1.normalText = context.getString(R.string.apps_using_mic);
        s1.buttonDrawableId = R.drawable.ic_action_content_edit;
        s1.onClick = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(view.getContext())
                        .setTitle(view.getContext().getString(R.string.apps_using_mic))
                        .setMessage(view.getContext().getString(R.string.apps_mic_explanation))
                        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
                                prefs.edit().putBoolean("blacklist_mic", false).commit();
                                preferenceSet();

                            }
                        })
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
                                prefs.edit().putBoolean("blacklist_mic", true).commit();
                                preferenceSet();

                            }
                        })
                        .show();
            }
        };
        SettingsCardListObject s2 = new SettingsCardListObject(this);
        HashSet<String> blackListedApps = (HashSet<String>) prefs.getStringSet("black_listed_apps", new HashSet<String>());
        if (blackListedApps.size() < 1) {
            s2.italicized = context.getString(R.string.no_blacklisted);
            s2.normalText = context.getString(R.string.custom_apps);
        } else if (blackListedApps.size() == 1) {
            s2.italicized = context.getString(R.string.blacklisted);
            s2.normalText = context.getString(R.string.one_custom_app);
        } else {
            s2.italicized = context.getString(R.string.blacklisted);
            s2.normalText = blackListedApps.size() + " " + context.getString(R.string.custom_apps);
        }
        s2.buttonDrawableId = R.drawable.ic_action_action_settings;
        s2.onClick = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!showDonateDialogIfNotDonate()) {
                    getContext().startActivity(new Intent(getContext(),
                            BlacklistActivity.class));
                }
            }
        };
        mObjects.add(s1);
        mObjects.add(s2);

        return mObjects;
    }

    @Override
    public void preferenceSet() {
        refreshCallback.refresh();
    }

}
