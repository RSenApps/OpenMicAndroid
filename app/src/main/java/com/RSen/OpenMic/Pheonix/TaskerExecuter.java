package com.RSen.OpenMic.Pheonix;

import android.content.Context;
import android.preference.PreferenceManager;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;

public class TaskerExecuter {
    public boolean hasCommands = false;
    private ArrayList<TaskerCommand> taskerCommands;

    public TaskerExecuter(Context context) {
        File file = new File(context.getDir("data", Context.MODE_PRIVATE), "tasker");

        try {
            ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(file));
            taskerCommands = (ArrayList<TaskerCommand>) inputStream.readObject();
            if (taskerCommands.size() > 0) {
                hasCommands = true;
            }
            inputStream.close();

        } catch (Exception e) {
        }
    }

    public ArrayList<TaskerCommand> getCommands() {
        return taskerCommands;
    }

    public void executeCommand(String command, Context context) {
        if (TaskerIntent.testStatus(context).equals(TaskerIntent.Status.OK)) {
            if (!PreferenceManager.getDefaultSharedPreferences(context)
                    .getBoolean("hide_tasker_messages", false)) {
                Toast.makeText(context,
                        R.string.tasker_command_executed_ + command,
                        Toast.LENGTH_SHORT).show();
            }
            String taskerCommand = "";
            for (TaskerCommand tC : taskerCommands) {
                if (tC.activationName.equals(command)) {
                    taskerCommand = tC.taskerCommandName;
                    break;
                }
            }
            TaskerIntent i = new TaskerIntent(taskerCommand);
            context.sendBroadcast(i);
        } else {
            Toast.makeText(context, R.string.tasker_not_ready_,
                    Toast.LENGTH_LONG).show();
            MyLog.l("Tasker not ready: " + TaskerIntent.testStatus(context),
                    context);
        }
    }

}
