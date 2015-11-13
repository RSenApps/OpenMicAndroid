package com.RSen.OpenMic.Pheonix.Cards;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;

import com.RSen.OpenMic.Pheonix.BuildConfig;
import com.RSen.OpenMic.Pheonix.CustomPreferenceDialogListener;
import com.RSen.OpenMic.Pheonix.R;
import com.RSen.OpenMic.Pheonix.Refreshable;

import java.util.List;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.prototypes.CardWithList;

/**
 * Created by Ryan on 6/11/2014.
 */
public abstract class MyListCard extends CardWithList implements CustomPreferenceDialogListener {
    public boolean isDismissing = false;
    Refreshable refreshCallback;

    public MyListCard(Context context, Refreshable refreshCallback) {
        super(context);
        isDismissing = false;
        this.refreshCallback = refreshCallback;
    }

    @Override
    protected abstract CardHeader initCardHeader();

    protected CardHeader initCardHeader(String title, String key, boolean defaultValue) {
        return new SwitchHeader(getContext(), title, key, defaultValue);
    }


    @Override
    protected void initCard() {
        setUseEmptyView(false);
    }

    @Override
    protected abstract List<ListObject> initChildren();

    @Override
    public abstract void preferenceSet();

    @Override
    public int getChildLayoutId() {
        return R.layout.settings_card_row;
    }

    @Override
    public View setupChildView(int childPosition, ListObject object, View convertView, ViewGroup parent) {
        try {
            getCardView().setAlpha(1f);
            getCardView().setTranslationX(0);
        } catch (Exception e) {
        }
        TextView italicized = (TextView) convertView.findViewById(R.id.italicized);
        TextView normalText = (TextView) convertView.findViewById(R.id.normalText);
        ImageButton button = (ImageButton) convertView.findViewById(R.id.button);
        CheckBox check = (CheckBox) convertView.findViewById(R.id.check);

        SettingsCardListObject s1 = (SettingsCardListObject) object;
        if (s1.showButton) {
            button.setImageResource(s1.buttonDrawableId);
            button.setVisibility(View.VISIBLE);
            button.setContentDescription("Toggle " + s1.italicized + " " + s1.normalText);
            button.setOnClickListener(s1.onClick);
        } else {
            button.setVisibility(View.GONE);
        }
        if (s1.showCheck) {
            check.setVisibility(View.VISIBLE);
            check.setChecked(s1.checkEnabled);
            check.setOnCheckedChangeListener(s1.onChecked);
        } else {
            check.setVisibility(View.GONE);
        }
        italicized.setText(s1.italicized);
        normalText.setText(s1.normalText);

        return convertView;
    }


    protected CompoundButton.OnCheckedChangeListener getOnCheckedListener(final String key, final SharedPreferences prefs) {
        return new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (!showDonateDialogIfNotDonate()) {
                    prefs.edit().putBoolean(key, b).commit();
                } else {
                    compoundButton.setChecked(!b);
                }
            }
        };
    }

    protected CompoundButton.OnCheckedChangeListener getOnCheckedListener(final String key, final SharedPreferences prefs, final boolean donateOnly) {
        return new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (donateOnly) {
                    getOnCheckedListener(key, prefs);
                } else {
                    prefs.edit().putBoolean(key, b).commit();
                }
            }
        };
    }


    protected boolean showDonateDialogIfNotDonate() {
        if (!BuildConfig.DONATE && !PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean("freeDonate", false)) {

            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle(R.string.donate);
            builder.setMessage(R.string.donate_only);
            builder.setPositiveButton(R.string.donate, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    final String appName = "com.RSen.OpenMic.Pheonix.Donate";
                    try {
                        getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri
                                .parse("market://details?id=" + appName)));
                    } catch (android.content.ActivityNotFoundException anfe) {
                        getContext().startActivity(new Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("http://play.google.com/store/apps/details?id="
                                        + appName)
                        ));
                    }
                }
            });
            builder.setNegativeButton(R.string.cancel, null);
            builder.show();
            return true;
        }
        return false;
    }

    @Override
    public int getType() {
        return 0;
    }

    public class SettingsCardListObject extends DefaultListObject {
        public String italicized;
        public String normalText;
        public int buttonDrawableId;
        public boolean showButton = true;
        public boolean showCheck = false;
        public boolean checkEnabled = false;
        public CompoundButton.OnCheckedChangeListener onChecked;
        public View.OnClickListener onClick;

        public SettingsCardListObject(Card parentCard) {
            super(parentCard);
        }

        @Override
        public String getObjectId() {
            return normalText;
        }
    }
}
