package com.RSen.OpenMic.Pheonix;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

/**
 * @author Ryan Senanayake
 *         Commandr for Google Now
 *         TaskerActivity.java
 * @version 1.0
 *          5/28/14
 */
public class TaskerActivity extends Activity {
    ArrayList<TaskerCommand> savedList;

    /**
     * Called when the activity is created. Setup the data
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_correction_phrases);
        // Show the Up button in the action bar.
        setupActionBar();

        final ListView listview = (ListView) findViewById(R.id.listview);
        //load data from file
        File file = new File(getDir("data", MODE_PRIVATE), "tasker");

        try {
            ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(file));
            savedList = (ArrayList<TaskerCommand>) inputStream.readObject();
            inputStream.close();

        } catch (Exception e) {
        }
        if (savedList == null) {
            savedList = new ArrayList<TaskerCommand>();
        }

        ArrayList<String> listDisplay = new ArrayList<String>();
        for (TaskerCommand cmd : savedList) {
            String item = cmd.activationName + " -> " + cmd.taskerCommandName;
            listDisplay.add(item);
        }
        if (savedList.size() <= 0) {
            listDisplay.add(getString(R.string.no_tasker));
        }
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, listDisplay);
        adapter.setNotifyOnChange(true);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                if (savedList.size() > 0) {
                    editDialog(adapter, savedList.get(position));

                } else {
                    if (savedList.size() <= 0) {
                        adapter.remove(getString(R.string.no_tasker));
                        ;
                    }
                    addDialog(adapter);
                }
            }

        });
        findViewById(R.id.add).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (savedList.size() <= 0) {
                    adapter.remove(getString(R.string.no_tasker));
                }
                addDialog(adapter);
            }

        });

    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setupActionBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * Display the add command dialog
     *
     * @param adapter The adapter that holds the data
     */
    private void addDialog(final ArrayAdapter<String> adapter) {
        QustomDialogBuilder builder = new QustomDialogBuilder(this);
        builder.setTitle(getString(R.string.add_tasker));
        builder.setTitleColor("#CC0000");
        builder.setDividerColor("#CC0000");
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view = inflater.inflate(R.layout.tasker_dialog, null);
        final Spinner commandList = (Spinner) view
                .findViewById(R.id.commandListButton);
        SpinnerAdapter spinnerAdapter = getTaskerCommandsAdapter();
        if (spinnerAdapter == null) {
            Toast.makeText(
                    this,
                    R.string.you_have_no_tasker_tasks_please_ensure_that_tasker_misc_allow_external,
                    Toast.LENGTH_SHORT).show();
            return;
        }
        commandList.setAdapter(spinnerAdapter);
        builder.setView(view);
        builder.setPositiveButton(R.string.ok, null); // handled in
        // onShow so
        // that i
        // can control dismiss
        builder.setNegativeButton(R.string.cancel,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }
        );

        final AlertDialog alertDialog = builder.create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialog) {

                Button b = alertDialog
                        .getButton(DialogInterface.BUTTON_POSITIVE);
                b.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        String commandPhrase = ((EditText) view
                                .findViewById(R.id.commandPhrase)).getText()
                                .toString();
                        String taskerCommand = (String) ((Spinner) view
                                .findViewById(R.id.commandListButton))
                                .getSelectedItem();
                        if (commandPhrase.trim().length() == 0) {
                            Toast.makeText(TaskerActivity.this,
                                    getString(R.string.enter_command_phrase),
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }
                        TaskerCommand command = new TaskerCommand(commandPhrase, taskerCommand);
                        SharedPreferences prefs = PreferenceManager
                                .getDefaultSharedPreferences(getApplicationContext());
                        savedList.add(command);

                        String item = commandPhrase + " -> " + taskerCommand;
                        adapter.remove(item); // incase attempting to make
                        // duplicate
                        adapter.add(item);
                        alertDialog.dismiss();
                    }
                });
            }
        });
        alertDialog.show();
    }

    /**
     * Creates a spinneradapter of Tasker Commands
     *
     * @return
     */
    private SpinnerAdapter getTaskerCommandsAdapter() {
        Cursor c = getContentResolver().query(
                Uri.parse("content://net.dinglisch.android.tasker/tasks"),
                null, null, null, null);
        if (c != null) {
            ArrayList<String> list = new ArrayList<String>();
            while (c.moveToNext()) {
                list.add(c.getString(c.getColumnIndex("name")));
            }
            return new ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_dropdown_item, list);
        }
        return null;
    }

    /**
     * Shows a dialog to edit a command.
     *
     * @param adapter          The adapter that holds the data
     * @param oldTaskerCommand The tasker command to edit
     */
    private void editDialog(final ArrayAdapter<String> adapter,
                            final TaskerCommand oldTaskerCommand) {
        QustomDialogBuilder builder = new QustomDialogBuilder(this);
        builder.setTitle(getString(R.string.edit_tasker));
        builder.setTitleColor("#CC0000");
        builder.setDividerColor("#CC0000");
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view = inflater.inflate(R.layout.tasker_dialog, null);
        final EditText commandPhraseEdit = (EditText) view
                .findViewById(R.id.commandPhrase);
        final Spinner commandList = (Spinner) view
                .findViewById(R.id.commandListButton);
        commandPhraseEdit.setText(oldTaskerCommand.activationName);
        SpinnerAdapter spinnerAdapter = getTaskerCommandsAdapter();
        if (spinnerAdapter == null) {
            Toast.makeText(this, R.string.you_have_no_tasker_tasks_please_ensure_that_tasker_misc_allow_external,
                    Toast.LENGTH_SHORT).show();
            return;
        }
        commandList.setAdapter(spinnerAdapter);
        for (int i = 0; i < spinnerAdapter.getCount(); i++) {
            if (spinnerAdapter.getItem(i).equals(oldTaskerCommand.taskerCommandName)) {
                commandList.setSelection(i);
                break;
            }
        }
        builder.setView(view);
        builder.setPositiveButton("Ok", null); // in onShow so i have
        // control
        // of dismiss
        builder.setNeutralButton("Cancel",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }
        );
        builder.setNegativeButton("Delete",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        savedList.remove(oldTaskerCommand);

                        String item = oldTaskerCommand.activationName + " -> "
                                + oldTaskerCommand.taskerCommandName;
                        adapter.remove(item);
                    }
                }
        );
        final AlertDialog alertDialog = builder.create();
        alertDialog.setOnShowListener(new OnShowListener() {

            @Override
            public void onShow(DialogInterface dialog) {
                Button b = alertDialog
                        .getButton(DialogInterface.BUTTON_POSITIVE);
                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String item = oldTaskerCommand.activationName + " -> "
                                + oldTaskerCommand.taskerCommandName;
                        adapter.remove(item);
                        String commandPhrase = commandPhraseEdit.getText()
                                .toString();
                        String taskerCommand = (String) ((Spinner) view
                                .findViewById(R.id.commandListButton))
                                .getSelectedItem();
                        if (commandPhrase.trim().length() == 0) {
                            Toast.makeText(TaskerActivity.this,
                                    R.string.enter_command_phrase,
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }

                        savedList.remove(oldTaskerCommand);
                        savedList.add(new TaskerCommand(commandPhrase, taskerCommand));

                        item = commandPhrase + " -> " + taskerCommand;
                        adapter.add(item);
                        alertDialog.dismiss();
                    }

                });
            }
        });

        alertDialog.show();

    }

    /**
     * Called when the activity is paused. Save the data
     */
    @Override
    protected void onPause() {
        File file = new File(getDir("data", MODE_PRIVATE), "tasker");
        try {
            ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(file));

            outputStream.writeObject(savedList);


            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onPause();
    }

    /**
     * Called when an options item is selected. Return to previous activity
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // This ID represents the Home or Up button. In the case of this
                // activity, the Up button is shown. Use NavUtils to allow users
                // to navigate up one level in the application structure. For
                // more details, see the Navigation pattern on Android Design:
                //
                // http://developer.android.com/design/patterns/navigation.html#up-vs-back
                //
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
