package com.RSen.OpenMic.Pheonix.Cards;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.RSen.OpenMic.Pheonix.QustomDialogBuilder;
import com.RSen.OpenMic.Pheonix.R;
import com.RSen.OpenMic.Pheonix.Refreshable;
import com.RSen.OpenMic.Pheonix.RootAccess;

import java.util.ArrayList;
import java.util.List;

import it.gmariotti.cardslib.library.internal.CardHeader;

/**
 * Created by Ryan on 6/11/2014.
 */
public class AdvancedCard extends MyListCard {
    public AdvancedCard(Context context, Refreshable refreshCallback) {
        super(context, refreshCallback);
    }

    @Override
    protected CardHeader initCardHeader() {

        CardHeader header = new CardHeader(getContext());
        header.setTitle(getContext().getString(R.string.advanced));
        return header;
    }


    @Override
    protected List<ListObject> initChildren() {

        List<ListObject> mObjects = new ArrayList<ListObject>();
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        SettingsCardListObject s1 = new SettingsCardListObject(this);
        s1.italicized = getContext().getString(R.string.listen);
        s1.normalText = getContext().getString(R.string.over_bluetooth);
        s1.showButton = false;
        s1.showCheck = true;
        s1.checkEnabled = prefs.getBoolean("bluetooth", false);
        s1.onChecked = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (!showDonateDialogIfNotDonate()) {
                    if (b) {
                        QustomDialogBuilder builder = new QustomDialogBuilder(getContext());
                        builder.setTitle(getContext().getString(R.string.enable_google_bluetooth));
                        builder.setTitleColor("#CC0000");
                        builder.setDividerColor("#CC0000");
                        builder.setCancelable(false);
                        builder.setMessage(getContext().getString(R.string.enable_google_bluetooth_instructions));
                        builder.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(getContext(), getContext().getString(R.string.enable_google_bluetooth_toast), Toast.LENGTH_LONG).show();
                                Intent intent = new Intent("android.search.action.SEARCH_SETTINGS");
                                intent.setClassName("com.google.android.googlequicksearchbox", "com.google.android.velvet.ui.settings.PublicSettingsActivity");
                                getContext().startActivity(intent);

                            }
                        });
                        builder.setNegativeButton(getContext().getString(R.string.done), null);
                        builder.show();
                    }
                    prefs.edit().putBoolean("bluetooth", b).commit();
                } else {
                    compoundButton.setChecked(!b);
                }
            }
        };
        Context context = getContext();
        SettingsCardListObject s2 = new SettingsCardListObject(this);
        s2.italicized = getContext().getString(R.string.read);
        s2.normalText = context.getString(R.string.text_messages);
        s2.showButton = false;
        s2.showCheck = true;
        s2.checkEnabled = prefs.getBoolean("read_messages", false);
        s2.onChecked = getOnCheckedListener("read_messages", prefs);

        SettingsCardListObject s3 = new SettingsCardListObject(this);
        s3.italicized = context.getString(R.string.hide);
        s3.normalText = context.getString(R.string.notification_partially);
        s3.showButton = false;
        s3.showCheck = true;
        s3.checkEnabled = prefs.getBoolean("hide_notification", false);
        s3.onChecked = getOnCheckedListener("hide_notification", prefs);

        SettingsCardListObject s31 = new SettingsCardListObject(this);
        s31.italicized = context.getString(R.string.hide);
        ;
        s31.normalText = context.getString(R.string.notification_completely);
        s31.showButton = false;
        s31.showCheck = true;
        s31.checkEnabled = prefs.getBoolean("su", false);
        s31.onChecked = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    if (RootAccess.requestRoot()) {
                        RootAccess.hideNotification(getContext());
                        prefs.edit().putBoolean("su", true).commit();
                    } else {
                        Toast.makeText(getContext(), R.string.no_su, Toast.LENGTH_SHORT).show();
                        prefs.edit().putBoolean("su", false).commit();
                        compoundButton.setChecked(false);
                    }
                } else {
                    if (RootAccess.requestRoot()) {
                        RootAccess.showNotification(getContext());
                        prefs.edit().putBoolean("su", false).commit();

                    }
                    prefs.edit().putBoolean("su", false).commit();

                }
            }
        };

        SettingsCardListObject s4 = new SettingsCardListObject(this);
        s4.italicized = context.getString(R.string.animate);
        s4.normalText = context.getString(R.string.google_wakeup);
        s4.showButton = false;
        s4.showCheck = true;
        s4.checkEnabled = prefs.getBoolean("overlay", true);
        s4.onChecked = getOnCheckedListener("overlay", prefs);

        SettingsCardListObject s5 = new SettingsCardListObject(this);
        s5.italicized = context.getString(R.string.vibrate);
        s5.normalText = context.getString(R.string.on_activation);
        s5.showButton = false;
        s5.showCheck = true;
        s5.checkEnabled = prefs.getBoolean("vibration_feedback", false);
        s5.onChecked = getOnCheckedListener("vibration_feedback", prefs);
        //Only if set to listen while screen off
        SettingsCardListObject s6 = new SettingsCardListObject(this);
        s6.italicized = context.getString(R.string.reactivate);
        s6.normalText = context.getString(R.string.lockscreen_if_bypassed);
        s6.showButton = false;
        s6.showCheck = true;
        s6.checkEnabled = prefs.getBoolean("relock", true);
        s6.onChecked = getOnCheckedListener("relock", prefs);

        SettingsCardListObject s7 = new SettingsCardListObject(this);
        s7.italicized = context.getString(R.string.turn_off) + " ";
        s7.normalText = context.getString(R.string.screen_after_30);
        s7.showButton = false;
        s7.showCheck = true;
        s7.checkEnabled = prefs.getBoolean("turn_off_screen", false);
        s7.onChecked = getOnCheckedListener("turn_off_screen", prefs);


        SettingsCardListObject s8 = new SettingsCardListObject(this);
        s8.italicized = context.getString(R.string.hide);
        s8.normalText = context.getString(R.string.tasker_executed_message);
        s8.showButton = false;
        s8.showCheck = true;
        s8.checkEnabled = prefs.getBoolean("hide_tasker_messages", false);
        s8.onChecked = getOnCheckedListener("hide_tasker_messages", prefs);
        mObjects.add(s1);
        mObjects.add(s3);
        mObjects.add(s31);
        mObjects.add(s8);
        mObjects.add(s4);
        mObjects.add(s5);
        mObjects.add(s2);
        mObjects.add(s6);
        mObjects.add(s7);

        return mObjects;
    }

    @Override
    public void preferenceSet() {
        refreshCallback.refresh();
    }

}
