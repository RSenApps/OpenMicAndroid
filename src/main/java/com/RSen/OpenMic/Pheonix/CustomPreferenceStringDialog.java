package com.RSen.OpenMic.Pheonix;

import android.content.Context;
import android.content.DialogInterface;
import android.preference.PreferenceManager;
import android.widget.EditText;
import android.widget.LinearLayout;

/**
 * Created by Ryan on 6/11/2014.
 */
public class CustomPreferenceStringDialog {
    public CustomPreferenceStringDialog(final Context context, final String key, Object defaultValue, String title, final CustomPreferenceDialogListener listener) {

        //right now only strings...
        String curValue = PreferenceManager.getDefaultSharedPreferences(context).getString(key, (String) defaultValue);
        QustomDialogBuilder builder = new QustomDialogBuilder(context);

        builder.setTitle(title);
        builder.setTitleColor("#CC0000");
        builder.setDividerColor("#CC0000");
        final EditText input = new EditText(context);
        input.setText(curValue);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        builder.setCustomView(input, context);
        builder.setNegativeButton(R.string.cancel, null);
        builder.setPositiveButton(R.string.set, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                PreferenceManager.getDefaultSharedPreferences(context).edit().putString(key, input.getText().toString()).commit();
                listener.preferenceSet();

            }
        });
        builder.show();
    }

}
