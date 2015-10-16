package com.RSen.OpenMic.Pheonix;

import android.content.Context;
import android.content.DialogInterface;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

/**
 * Created by Ryan on 6/11/2014.
 */
public class CustomPreferenceIntDialog {
    public CustomPreferenceIntDialog(final Context context, final String key, int defaultValue, String title, final CustomPreferenceDialogListener listener) {

        //right now only strings...
        int curValue = PreferenceManager.getDefaultSharedPreferences(context).getInt(key, defaultValue);
        QustomDialogBuilder builder = new QustomDialogBuilder(context);

        builder.setTitle(title);
        builder.setTitleColor("#CC0000");
        builder.setDividerColor("#CC0000");

        final EditText input = new EditText(context);
        input.setText(curValue + "");
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        builder.setCustomView(input, context);
        builder.setNegativeButton(R.string.cancel, null);
        builder.setPositiveButton(context.getString(R.string.set), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                try {
                    PreferenceManager.getDefaultSharedPreferences(context).edit().putInt(key, Integer.parseInt(input.getText().toString())).commit();
                    listener.preferenceSet();
                } catch (Exception e) {
                    Toast.makeText(context, R.string.invalid_int, Toast.LENGTH_LONG).show();
                }
            }
        });
        builder.show();
    }

}
