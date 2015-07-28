package com.RSen.OpenMic.Pheonix;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.preference.PreferenceManager;
import android.text.format.DateFormat;

import com.crashlytics.android.Crashlytics;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class MyLog {
    public static void l(String s, Context c) {
        Crashlytics.log(s);
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(c);
        int substringStart = 0;
        String previousLog = prefs.getString("log", "Installed");
        if (previousLog.length() > 10000) {
            substringStart = previousLog.length() - 10000;
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date();
        prefs.edit()
                .putString(
                        "log",
                        previousLog.substring(substringStart) + "\n"
                                + dateFormat.format(date) + ": " + s
                )
                .commit();
    }

    public static String getLog(Context c) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(c);
        String log = "";
        String[] allPrefs = prefs.getAll().keySet().toArray(new String[prefs.getAll().keySet().size()]);
        Arrays.sort(allPrefs);
        for (String key : allPrefs) {
            log += "\n" + key + " = " + String.valueOf(prefs.getAll().get(key));
        }

        PackageInfo pInfo;
        try {
            pInfo = c.getPackageManager().getPackageInfo(c.getPackageName(), 0);
            log += "\nApp Version: " + pInfo.versionName;
            log += "\nPackage: " + pInfo.packageName;
        } catch (NameNotFoundException e) {
        }
        log += "\nManufacturer: " + android.os.Build.MANUFACTURER;
        log += "\nDevice Name: " + android.os.Build.MODEL;
        log += "\nAndroid version: " + android.os.Build.VERSION.SDK_INT;
        log += "\nCurrent Time: "
                + (DateFormat.format("dd-MM-yyyy hh:mm:ss",
                new java.util.Date()).toString());

        return log;
    }
}
