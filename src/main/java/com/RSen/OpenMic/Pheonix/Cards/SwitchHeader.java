package com.RSen.OpenMic.Pheonix.Cards;

import android.content.Context;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.RSen.OpenMic.Pheonix.R;

import it.gmariotti.cardslib.library.internal.CardHeader;

/**
 * Created by Ryan on 6/11/2014.
 */
public class SwitchHeader extends CardHeader {
    String title;
    String key;
    boolean defaultValue;

    public SwitchHeader(Context context, String title, String key, boolean defaultValue) {

        super(context, R.layout.switch_header);
        this.title = title;
        this.key = key;
        this.defaultValue = defaultValue;
    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {
        if (view != null) {
            ((TextView) view.findViewById(R.id.title)).setText(title);
            ((Switch) view.findViewById(R.id.toggle)).setOnCheckedChangeListener(null);
            ((Switch) view.findViewById(R.id.toggle)).setChecked(PreferenceManager.getDefaultSharedPreferences(view.getContext()).getBoolean(key, defaultValue));
            ((Switch) view.findViewById(R.id.toggle)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    PreferenceManager.getDefaultSharedPreferences(compoundButton.getContext()).edit().putBoolean(key, b).commit();
                }
            });
        }
    }


}
