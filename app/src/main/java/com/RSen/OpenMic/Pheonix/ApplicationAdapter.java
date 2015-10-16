package com.RSen.OpenMic.Pheonix;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashSet;
import java.util.List;

public class ApplicationAdapter extends ArrayAdapter<ApplicationInfo> {
    private List<ApplicationInfo> appsList = null;
    private Context context;
    private PackageManager packageManager;
    private HashSet<String> blackListedApps;
    private SharedPreferences prefs;

    public ApplicationAdapter(Context context, int textViewResourceId,
                              List<ApplicationInfo> appsList) {
        super(context, textViewResourceId, appsList);
        this.context = context;
        this.appsList = appsList;
        packageManager = context.getPackageManager();
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        blackListedApps = (HashSet<String>) prefs.getStringSet("black_listed_apps", new HashSet<String>());
    }

    @Override
    public int getCount() {
        return ((null != appsList) ? appsList.size() : 0);
    }

    @Override
    public ApplicationInfo getItem(int position) {
        return ((null != appsList) ? appsList.get(position) : null);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (null == view) {
            LayoutInflater layoutInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.snippet_list_row, null);
        }

        final ApplicationInfo data = appsList.get(position);
        if (null != data) {
            TextView appName = (TextView) view.findViewById(R.id.app_name);
            TextView packageName = (TextView) view.findViewById(R.id.app_paackage);
            ImageView iconview = (ImageView) view.findViewById(R.id.app_icon);
            CheckBox checkbox = (CheckBox) view.findViewById(R.id.check);
            checkbox.setOnCheckedChangeListener(null);
            if (blackListedApps.contains(data.packageName)) {
                checkbox.setChecked(true);
            } else {
                checkbox.setChecked(false);
            }
            if (data.packageName.equals("com.google.android.googlequicksearchbox") && prefs.getBoolean("use_gettasks", true)) {
                checkbox.setEnabled(false);
                checkbox.setChecked(true);
            } else {
                checkbox.setEnabled(true);
            }
            checkbox
                    .setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                        @Override
                        public void onCheckedChanged(CompoundButton buttonView,
                                                     boolean isChecked) {
                            if (isChecked) {
                                blackListedApps.add(data.packageName);
                            } else {
                                blackListedApps.remove(data.packageName);
                            }
                            prefs.edit().putStringSet("black_listed_apps", blackListedApps).commit();
                        }
                    });
            appName.setText(data.loadLabel(packageManager));
            packageName.setText(data.packageName);
            iconview.setImageDrawable(data.loadIcon(packageManager));
        }
        return view;
    }
};