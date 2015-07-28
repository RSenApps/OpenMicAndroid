package com.RSen.OpenMic.Pheonix;

import android.Manifest;
import android.app.ActivityManager;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

public class CheckIfAppBlackListedService extends Service {
    public static boolean checkingForRelockOnly = false;
    public static boolean blacklisteddetected = false;
    final Handler checkIfAppBlacklistedDelayed = new Handler();
    Runnable runnable;

    public CheckIfAppBlackListedService() {
    }

    public static boolean checkIfBlacklistedBecauseOfMic(Context context, String pkg) {
        if (!PreferenceManager.getDefaultSharedPreferences(context).getBoolean("blacklist_mic", true)) {
            return false;
        }
        if (pkg.equals("com.google.android.googlequicksearchbox") || pkg.equals(context.getPackageName())) {
            return false;
        }
        PackageInfo packageInfo = null;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(pkg, PackageManager.GET_PERMISSIONS);
        } catch (PackageManager.NameNotFoundException e) {
        }

        String[] requestedPermissions = packageInfo.requestedPermissions;
        return Arrays.asList(requestedPermissions).contains(Manifest.permission.RECORD_AUDIO);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        final HashSet<String> blackListedApps = (HashSet<String>) PreferenceManager.getDefaultSharedPreferences(this).getStringSet("black_listed_apps", new HashSet<String>());
        final boolean useGetTasks = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("use_gettasks", true);
        final boolean relock = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("relock", true);
        //checkingForRelockOnly = ScreenReceiver.isActivating && !useGetTasks;
        final boolean pocketsphinx = PreferenceManager.getDefaultSharedPreferences(this).getString("speech_engine", "google").equals("pocketsphinx");
        runnable = new Runnable() {

            @Override
            public void run() {

                try {
                    String pkgName = "";
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        @SuppressWarnings("ResourceType") UsageStatsManager mUsageStatsManager = (UsageStatsManager)getSystemService("usagestats");
                        long time = System.currentTimeMillis();
                        // We get usage stats for the last 10 seconds
                        List<UsageStats> stats = mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000*10, time);
                        // Sort the stats by the last time used
                        if(stats != null) {
                            SortedMap<Long,UsageStats> mySortedMap = new TreeMap<Long,UsageStats>();
                            for (UsageStats usageStats : stats) {
                                mySortedMap.put(usageStats.getLastTimeUsed(),usageStats);
                            }
                            if(mySortedMap != null && !mySortedMap.isEmpty()) {
                                pkgName =  mySortedMap.get(mySortedMap.lastKey()).getPackageName();
                            }
                        }
                    }
                    else {
                        List<ActivityManager.RunningTaskInfo> taskInfo = am
                                .getRunningTasks(1);
                        ComponentName componentInfo = taskInfo.get(0).topActivity;
                        pkgName = componentInfo.getPackageName();
                    }

                    if (checkingForRelockOnly) {
                        if (!pkgName.equals(
                                "com.google.android.googlequicksearchbox")) {
                            if (relock) {
                                try {
                                    AudioUI.lock.reenableKeyguard();
                                } catch (Exception e) {
                                }
                            }
                            if (pocketsphinx) {
                                checkingForRelockOnly = false;
                                checkIfAppBlacklistedDelayed.postDelayed(this, 500);
                                return;
                            }
                            stopSelf();
                        } else {
                            checkIfAppBlacklistedDelayed.postDelayed(this, 500);
                            return;
                        }
                    }
                    if (!(useGetTasks && pkgName.equals(
                            "com.google.android.googlequicksearchbox")) && !blackListedApps.contains(pkgName) && !checkIfBlacklistedBecauseOfMic(CheckIfAppBlackListedService.this, pkgName) && !ScreenReceiver.isActivating) {

                        if (relock && useGetTasks && !pocketsphinx) {
                            try {
                                AudioUI.lock.reenableKeyguard();
                            } catch (Exception e) {
                            }
                        }
                        if (!MyService.isRunning && !(pocketsphinx && !blacklisteddetected)) {
                            if (relock && useGetTasks) {
                                try {
                                    AudioUI.lock.reenableKeyguard();
                                } catch (Exception e) {
                                }
                            }
                            MyLog.l("Blacklisted app no longer active",
                                    getApplicationContext());
                            blacklisteddetected = false;
                            Intent i = new Intent(getApplicationContext(),
                                    MyService.class);
                            startService(i);
                        }

                        if (pocketsphinx) {
                            checkIfAppBlacklistedDelayed.postDelayed(this, 500);
                            return;
                        } else {
                            stopSelf();
                        }
                    } else {

                        if (pocketsphinx && MyService.isRunning && !KeyguardReceiver.keyguardEnabled && ScreenReceiver.isScreenOn && !ScreenReceiver.isActivating) {
                            blacklisteddetected = true;
                            Intent i = new Intent(getApplicationContext(),
                                    MyService.class);
                            i.setAction("GNACTIVATED");
                            startService(i);
                        }
                        checkIfAppBlacklistedDelayed.postDelayed(this, 500);
                    }
                } catch (Exception e) {
                    checkIfAppBlacklistedDelayed.postDelayed(this, 500);
                }
            }
        };
        checkIfAppBlacklistedDelayed.postDelayed(runnable, 2000);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        checkIfAppBlacklistedDelayed.removeCallbacks(runnable);
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

}
