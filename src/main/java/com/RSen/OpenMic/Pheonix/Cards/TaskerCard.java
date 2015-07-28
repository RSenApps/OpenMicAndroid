package com.RSen.OpenMic.Pheonix.Cards;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.view.View;

import com.RSen.OpenMic.Pheonix.QustomDialogBuilder;
import com.RSen.OpenMic.Pheonix.R;
import com.RSen.OpenMic.Pheonix.Refreshable;
import com.RSen.OpenMic.Pheonix.TaskerActivity;
import com.RSen.OpenMic.Pheonix.TaskerCommand;
import com.RSen.OpenMic.Pheonix.TaskerExecuter;
import com.RSen.OpenMic.Pheonix.TaskerIntent;

import java.util.ArrayList;
import java.util.List;

import it.gmariotti.cardslib.library.internal.CardHeader;

/**
 * Created by Ryan on 6/11/2014.
 */
public class TaskerCard extends MyListCard {
    public TaskerCard(Context context, Refreshable refreshCallback) {
        super(context, refreshCallback);
    }

    @Override
    protected CardHeader initCardHeader() {

        CardHeader header = new CardHeader(getContext());
        if (isEnabled()) {
            header.setTitle(getContext().getString(R.string.tasker_commands));
        } else {
            header.setTitle(getContext().getString(R.string.tasker_commands_disabled));
        }
        return header;
    }

    @Override
    protected List<ListObject> initChildren() {

        List<ListObject> mObjects = new ArrayList<ListObject>();
        if (isEnabled()) {
            SettingsCardListObject s1 = new SettingsCardListObject(this);
            s1.italicized = getContext().getString(R.string.set);
            ArrayList<TaskerCommand> commands = new TaskerExecuter(getContext()).getCommands();

            int numberCommands = 0;
            if (commands != null) {
                numberCommands = commands.size();
            }
            if (numberCommands == 1) {
                s1.normalText = getContext().getString(R.string.one_tasker_command);
            } else {
                s1.normalText = numberCommands + " " + getContext().getString(R.string.tasker_commands);
            }
            s1.buttonDrawableId = R.drawable.ic_action_action_settings;
            s1.onClick = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getContext().startActivity(new Intent(getContext(), TaskerActivity.class));
                }
            };
            mObjects.add(s1);
        } else if (!TaskerIntent.taskerInstalled(getContext())) {
            SettingsCardListObject s1 = new SettingsCardListObject(this);
            s1.italicized = getContext().getString(R.string.tasker);
            s1.normalText = getContext().getString(R.string.not_installed);
            s1.buttonDrawableId = R.drawable.ic_action_action_about;
            s1.onClick = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                     getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://tasker.dinglisch.net/")));

                }
            };
            mObjects.add(s1);
        } else {
            SettingsCardListObject s2 = new SettingsCardListObject(this);
            s2.italicized = getContext().getString(R.string.unavailable);
            s2.normalText = getContext().getString(R.string.with_pocketsphinx_engine);
            s2.buttonDrawableId = R.drawable.ic_action_content_edit;
            s2.onClick = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    QustomDialogBuilder builder = new QustomDialogBuilder(getContext());
                    builder.setTitle(getContext().getString(R.string.choose_speech_engine));
                    builder.setTitleColor("#CC0000");
                    builder.setDividerColor("#CC0000");
                    builder.setMessage(R.string.engine_comparison);
                    builder.setPositiveButton(R.string.google, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putString("speech_engine", "google").commit();
                            preferenceSet();
                        }
                    });
                    builder.setNegativeButton(R.string.pocketsphinx, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putString("speech_engine", "pocketsphinx").commit();
                            preferenceSet();
                        }
                    });
                    builder.show();
                /*
                view.getContext().startActivity(new Intent(view.getContext(),
                        SpeechRecognitionSettingsActivity.class));
                        */
                }
            };
            mObjects.add(s2);
        }

        return mObjects;
    }

    private boolean isEnabled() {
        return TaskerIntent.taskerInstalled(getContext()) && PreferenceManager.getDefaultSharedPreferences(getContext()).getString("speech_engine", "google").equals("google");

    }

    @Override
    public void preferenceSet() {
        refreshCallback.refresh();
    }

}
