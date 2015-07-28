package com.RSen.OpenMic.Pheonix.Cards;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Toast;

import com.RSen.OpenMic.Pheonix.BuildConfig;
import com.RSen.OpenMic.Pheonix.CustomPreferenceRangeDialog;
import com.RSen.OpenMic.Pheonix.CustomPreferenceStringDialog;
import com.RSen.OpenMic.Pheonix.PocketSphinxSpeechRecognizer;
import com.RSen.OpenMic.Pheonix.QustomDialogBuilder;
import com.RSen.OpenMic.Pheonix.R;
import com.RSen.OpenMic.Pheonix.Refreshable;

import java.util.ArrayList;
import java.util.List;

import it.gmariotti.cardslib.library.internal.CardHeader;

/**
 * Created by Ryan on 6/11/2014.
 */
public class HotwordRecognitionCard extends MyListCard {
    ProgressDialog loadingdialog;
    private boolean checkIfValidHotphrase = false; //in preferenceset

    public HotwordRecognitionCard(Context context, Refreshable refreshCallback) {
        super(context, refreshCallback);
    }

    @Override
    protected CardHeader initCardHeader() {
        return initCardHeader(getContext().getString(R.string.hot_phrase_detection), "listenHotword", true);
    }

    @Override
    protected List<ListObject> initChildren() {

        List<ListObject> mObjects = new ArrayList<ListObject>();
        SettingsCardListObject s1 = new SettingsCardListObject(this);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        s1.italicized = getContext().getString(R.string.say);
        s1.normalText = "\"" + prefs.getString("hot_phrase", "Okay Google") + "\"";
        s1.buttonDrawableId = R.drawable.ic_action_content_edit;
        s1.onClick = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkIfValidHotphrase = true;
                new CustomPreferenceStringDialog(view.getContext(), "hot_phrase", "Okay Google", "Hotphrase", HotwordRecognitionCard.this);
            }
        };
        SettingsCardListObject s2 = new SettingsCardListObject(this);
        s2.italicized = getContext().getString(R.string.using);
        if (prefs.getString("speech_engine", "google").equals("pocketsphinx")) {
            s2.normalText = getContext().getString(R.string.pocketsphinx_engine);
        } else {
            s2.normalText = getContext().getString(R.string.google_engine);
        }
        s2.buttonDrawableId = R.drawable.ic_action_content_edit;
        s2.onClick = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (BuildConfig.googlespeech) {
                    QustomDialogBuilder builder = new QustomDialogBuilder(getContext());
                    builder.setTitle(getContext().getString(R.string.choose_speech_engine));
                    builder.setTitleColor("#CC0000");
                    builder.setDividerColor("#CC0000");
                    builder.setMessage(R.string.engine_comparison);
                    builder.setPositiveButton(getContext().getString(R.string.google), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putString("speech_engine", "google").commit();
                            preferenceSet();
                        }
                    });
                    builder.setNegativeButton(getContext().getString(R.string.pocketsphinx), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            checkIfValidHotphrase = true;
                            PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putString("speech_engine", "pocketsphinx").commit();
                            preferenceSet();
                        }
                    });
                    builder.show();
                }
                else {
                    Toast.makeText(getContext(), "Unfortunately, due to a request from Google, the Google speech engine is no longer available.", Toast.LENGTH_LONG).show();
                }
                /*
                view.getContext().startActivity(new Intent(view.getContext(),
                        SpeechRecognitionSettingsActivity.class));
                        */
            }
        };
        mObjects.add(s1);
        mObjects.add(s2);
        if (prefs.getString("speech_engine", "google").equals("google")) {
            SettingsCardListObject s3 = new SettingsCardListObject(this);
            s3.italicized = getContext().getString(R.string.optimizing);
            s3.normalText = getContext().getString(R.string.for_english);
            s3.showButton = false;
            s3.showCheck = true;
            s3.checkEnabled = prefs.getBoolean("optimizeEnglish", true);
            s3.onChecked = getOnCheckedListener("optimizeEnglish", prefs, false);
            mObjects.add(s3);

        } else {
            SettingsCardListObject s3 = new SettingsCardListObject(this);
            s3.italicized = getContext().getString(R.string.sensitivity);
            s3.normalText = prefs.getInt("sensitivity_int", 3) + " " + getContext().getString(R.string.out_of_5);

            s3.buttonDrawableId = R.drawable.ic_action_content_edit;
            s3.onClick = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new CustomPreferenceRangeDialog(getContext(), "sensitivity_int", 3, 5, getContext().getString(R.string.sensitivity), HotwordRecognitionCard.this);
                }
            };
            mObjects.add(s3);
            SettingsCardListObject s4 = new SettingsCardListObject(this);
            s4.italicized = getContext().getString(R.string.stop);
            s4.normalText = getContext().getString(R.string.listening_during_music);
            s4.showButton = false;
            s4.showCheck = true;
            s4.checkEnabled = prefs.getBoolean("stopMusic", false);
            s4.onChecked = getOnCheckedListener("stopMusic", prefs);
            mObjects.add(s4);

        }
        return mObjects;
    }

    private void updateUseGetTasks() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        boolean wasGetTasks = prefs.getBoolean("use_gettasks", true);

        boolean available = prefs.getBoolean("google_hotphrase_available", false);
        boolean notCustomHotphrase = prefs.getString("hot_phrase", "Okay Google").toLowerCase().equals("okay google");
        boolean useGetTasks = available && notCustomHotphrase;

        if (useGetTasks && !wasGetTasks) {
            //enable hotword recognition in Google Now
            QustomDialogBuilder builder = new QustomDialogBuilder(getContext());
            builder.setTitle(getContext().getString(R.string.enable_google_detection));
            builder.setTitleColor("#CC0000");
            builder.setDividerColor("#CC0000");
            builder.setCancelable(false);
            builder.setMessage(getContext().getString(R.string.on_next_screen) + getContext().getString(R.string.enable_google_detection_instructions));
            builder.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Toast.makeText(getContext(), getContext().getString(R.string.enable_google_detection_instructions), Toast.LENGTH_LONG).show();
                    Intent intent = new Intent("android.search.action.SEARCH_SETTINGS");
                    intent.setClassName("com.google.android.googlequicksearchbox", "com.google.android.velvet.ui.settings.PublicSettingsActivity");
                    getContext().startActivity(intent);

                }
            });
            builder.setNegativeButton(getContext().getString(R.string.done), null);
            builder.show();
        } else if (!useGetTasks && wasGetTasks) {
            //disable hotword recognition in Google Now
            QustomDialogBuilder builder = new QustomDialogBuilder(getContext());
            builder.setTitle(getContext().getString(R.string.disable_google_detection));
            builder.setTitleColor("#CC0000");
            builder.setDividerColor("#CC0000");
            builder.setCancelable(false);
            builder.setMessage(getContext().getString(R.string.on_next_screen) + getContext().getString(R.string.disable_google_detection_instructions));
            builder.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Toast.makeText(getContext(), getContext().getString(R.string.disable_google_detection_instructions), Toast.LENGTH_LONG).show();
                    Intent intent = new Intent("android.search.action.SEARCH_SETTINGS");
                    intent.setClassName("com.google.android.googlequicksearchbox", "com.google.android.velvet.ui.settings.PublicSettingsActivity");
                    getContext().startActivity(intent);

                }
            });
            builder.setNegativeButton(R.string.done, null);
            builder.show();
        }

        prefs.edit().putBoolean("use_gettasks", useGetTasks).commit();
    }

    @Override
    public void preferenceSet() {
        if (checkIfValidHotphrase && PreferenceManager.getDefaultSharedPreferences(getContext()).getString("speech_engine", "google").equals("pocketsphinx") && !PreferenceManager.getDefaultSharedPreferences(getContext()).getString("hot_phrase", "Okay Google").equals("Okay Google")) {

            final Handler handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    loadingdialog.dismiss();
                    if (msg.what == 1) {
                        Toast.makeText(getContext(), R.string.hotphrase_validated, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), R.string.hotphrase_invalidated, Toast.LENGTH_LONG).show();
                        PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putString("hot_phrase", "Okay Google").commit();
                    }
                }
            };
            final Thread thread = new Thread() {
                public void run() {

                    int success = (PocketSphinxSpeechRecognizer.checkIfValidHotphrase(PreferenceManager.getDefaultSharedPreferences(getContext()).getString("hot_phrase", "Okay Google"), getContext())) ? 1 : 0;
                    ;
                    handler.sendEmptyMessage(success);

                }
            };
            loadingdialog = new ProgressDialog(getContext());
            loadingdialog.setMessage(getContext().getString(R.string.hotphrase_validating));
            loadingdialog.setCancelable(false);
            loadingdialog.setButton(DialogInterface.BUTTON_NEGATIVE, getContext().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    Toast.makeText(getContext(), R.string.hotphrase_invalidated, Toast.LENGTH_LONG).show();
                    PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putString("hot_phrase", "Okay Google").commit();
                    thread.interrupt();
                }
            });
            loadingdialog.show();
            thread.start();

        }
        updateUseGetTasks();
        checkIfValidHotphrase = false;
        refreshCallback.refresh();

    }

}
