package com.RSen.OpenMic.Pheonix;

import android.content.Context;
import android.content.DialogInterface;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * Created by Ryan on 6/11/2014.
 */
public class CustomPreferenceRangeDialog {
    public CustomPreferenceRangeDialog(final Context context, final String key, int defaultValue, final int max, String title, final CustomPreferenceDialogListener listener) {

        //right now only strings...
        int curValue = PreferenceManager.getDefaultSharedPreferences(context).getInt(key, defaultValue);
        QustomDialogBuilder builder = new QustomDialogBuilder(context);

        builder.setTitle(title);
        builder.setTitleColor("#CC0000");
        builder.setDividerColor("#CC0000");
        View v = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.range_dialog, null);
        final SeekBar input = (SeekBar) v.findViewById(R.id.seekBar);
        input.setMax(max);
        input.setProgress(curValue);

        final TextView tv = (TextView) v.findViewById(R.id.textView);
        tv.setText(curValue + " " + context.getString(R.string.out_of) + " " + max);

        input.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int value, boolean fromUser) {
                tv.setText(value + " " + context.getString(R.string.out_of) + " " + max);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        builder.setCustomView(v, context);
        builder.setNegativeButton(R.string.cancel, null);
        builder.setPositiveButton(R.string.set, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                PreferenceManager.getDefaultSharedPreferences(context).edit().putInt(key, input.getProgress()).commit();
                listener.preferenceSet();
            }
        });
        builder.show();
    }

}
