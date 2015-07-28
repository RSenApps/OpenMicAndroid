package com.RSen.OpenMic.Pheonix.Cards;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.RSen.OpenMic.Pheonix.AutoStartSettingsActivity;
import com.RSen.OpenMic.Pheonix.QustomDialogBuilder;
import com.RSen.OpenMic.Pheonix.R;
import com.RSen.OpenMic.Pheonix.Refreshable;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

import it.gmariotti.cardslib.library.internal.CardHeader;

/**
 * Created by Ryan on 6/11/2014.
 */
public class WhenToRunCard extends MyListCard {
    public WhenToRunCard(Context context, Refreshable refreshCallback) {
        super(context, refreshCallback);
    }

    @Override
    protected CardHeader initCardHeader() {

        CardHeader header = new CardHeader(getContext());
        header.setOtherButtonDrawable(android.R.drawable.ic_menu_preferences);
        header.setTitle(getContext().getString(R.string.when_to_run));

        return header;
    }

    @Override
    protected List<ListObject> initChildren() {

        List<ListObject> mObjects = new ArrayList<ListObject>();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        SettingsCardListObject s1 = new SettingsCardListObject(this);
        s1.italicized = getContext().getString(R.string.auto_start);
        ArrayList<String> autoStartTriggers = new ArrayList<String>();
        if (prefs.getBoolean("start_on_boot", false)) {
            autoStartTriggers.add(getContext().getString(R.string.on_boot));
        }
        if (prefs.getBoolean("listen_charging", false)) {
            autoStartTriggers.add(getContext().getString(R.string.on_charger));
        }
        if (prefs.getBoolean("toggle_launch", false)) {
            autoStartTriggers.add(getContext().getString(R.string.on_app_launch));
        }
        if (autoStartTriggers.size() < 1) {
            autoStartTriggers.add(getContext().getString(R.string.never));
        }

        s1.normalText = StringUtils.join(autoStartTriggers.toArray(), ", ");
        s1.buttonDrawableId = R.drawable.ic_action_action_settings;
        s1.onClick = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.getContext().startActivity(new Intent(view.getContext(),
                        AutoStartSettingsActivity.class));
            }
        };
        SettingsCardListObject s2 = new SettingsCardListObject(this);
        final boolean listeningWhileScreenOn = !(prefs.getBoolean("listen_screen_off", false) && prefs.getBoolean("listen_only_screen_off", false));
        if (listeningWhileScreenOn) {
            s2.italicized = getContext().getString(R.string.listening);
        } else {
            s2.italicized = getContext().getString(R.string.not_listening);
        }
        s2.normalText = getContext().getString(R.string.while_screen_on);
        s2.buttonDrawableId = R.drawable.ic_action_content_edit;
        s2.onClick = new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                QustomDialogBuilder builder = new QustomDialogBuilder(getContext());
                builder.setTitle(getContext().getString(R.string.listen_while_screen_on));
                builder.setTitleColor("#CC0000");
                builder.setDividerColor("#CC0000");
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_single_choice, new String[]{getContext().getString(R.string.yes), getContext().getString(R.string.no)});
                final ListView listView = new ListView(getContext());
                listView.setAdapter(adapter);
                listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                listView.setItemChecked((listeningWhileScreenOn) ? 0 : 1, true);
                builder.setCustomView(listView, getContext());
                builder.setNegativeButton(R.string.cancel, null)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
                                int selectedPosition = listView.getCheckedItemPosition();
                                if (selectedPosition == 0) {
                                    prefs.edit().putBoolean("listen_only_screen_off", false).commit();
                                } else {
                                    prefs.edit().putBoolean("listen_screen_off", true).putBoolean("listen_only_screen_off", true).commit();
                                }
                                preferenceSet();

                            }
                        })
                        .show();
            }
        };
        SettingsCardListObject s3 = new SettingsCardListObject(this);
        final boolean listeningWhileScreenOff = prefs.getBoolean("listen_screen_off", false);
        if (listeningWhileScreenOff) {
            s3.italicized = getContext().getString(R.string.listening);
        } else {
            s3.italicized = getContext().getString(R.string.not_listening);
        }
        final boolean listeningWhileScreenOffIfCharging = prefs.getBoolean("listen_screen_off", false) && prefs.getBoolean("listen_screen_off_charging", false);
        if (listeningWhileScreenOffIfCharging) {
            s3.normalText = getContext().getString(R.string.while_screen_off_if_charging);
        } else {
            s3.normalText = getContext().getString(R.string.while_screen_off);
        }
        s3.buttonDrawableId = R.drawable.ic_action_content_edit;
        s3.onClick = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int selection = 2;
                if (!listeningWhileScreenOffIfCharging) {
                    if (listeningWhileScreenOff) {
                        selection = 0;
                    } else {
                        selection = 1;
                    }
                }

                QustomDialogBuilder builder = new QustomDialogBuilder(getContext());
                builder.setTitle(R.string.listen_while_screen_off);
                builder.setTitleColor("#CC0000");
                builder.setDividerColor("#CC0000");
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_single_choice, new String[]{getContext().getString(R.string.yes), getContext().getString(R.string.no), getContext().getString(R.string.only_if_charging)});
                final ListView listView = new ListView(getContext());
                listView.setAdapter(adapter);
                listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                listView.setItemChecked(selection, true);
                builder.setCustomView(listView, getContext());
                builder.setNegativeButton(R.string.cancel, null)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
                                int selectedPosition = listView.getCheckedItemPosition();
                                switch (selectedPosition) {
                                    case 0:
                                        prefs.edit().putBoolean("listen_screen_off_charging", false).putBoolean("listen_screen_off", true).commit();
                                        break;
                                    case 1:
                                        prefs.edit().putBoolean("listen_screen_off_charging", false).putBoolean("listen_screen_off", false).commit();
                                        break;
                                    case 2:
                                        prefs.edit().putBoolean("listen_screen_off_charging", true).putBoolean("listen_screen_off", true).commit();
                                        break;

                                }
                                preferenceSet();
                            }
                        })
                        .show();
            }
        };
        mObjects.add(s1);
        mObjects.add(s2);
        mObjects.add(s3);

        return mObjects;
    }

    @Override
    public void preferenceSet() {
        refreshCallback.refresh();
    }

}
