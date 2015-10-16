package com.RSen.OpenMic.Pheonix;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.usage.UsageStatsManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.apptentive.android.sdk.Apptentive;
import com.apptentive.android.sdk.ApptentiveActivity;
import com.crashlytics.android.Crashlytics;
import com.github.johnpersano.supertoasts.SuperCardToast;
import com.github.johnpersano.supertoasts.SuperToast;
import com.github.johnpersano.supertoasts.util.OnClickWrapper;
import com.github.johnpersano.supertoasts.util.OnDismissWrapper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.mobiroo.host.drm.MobirooDrm;

import io.fabric.sdk.android.Fabric;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class MainActivity extends ApptentiveActivity {

    public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    public static boolean listenScreenOffActivated = false;
    ImageView toggleButton;
    String SENDER_ID = "666953364898";
    GoogleCloudMessaging gcm;
    AtomicInteger msgId = new AtomicInteger();
    SharedPreferences prefs;
    String regid;
    private BroadcastReceiver manualActivationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                serviceStartedLayout();
                ((SettingsCardsFragment) getFragmentManager().findFragmentById(R.id.settingsFragment)).switchToStartedLayout();
                Apptentive.engage(MainActivity.this, "service_started");
            } catch (Exception e) {
            }
        }
    };
    private BroadcastReceiver serviceStoppedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                serviceStoppedLayout();
                ((SettingsCardsFragment) getFragmentManager().findFragmentById(R.id.settingsFragment)).switchToStoppedLayout();
                Apptentive.engage(MainActivity.this, "service_stopped");
            } catch (Exception e) {
            }
        }
    };

    public static Map<String, String> addSharedPreferencesToLog(Context context, boolean firstTime) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        Map<String, String> map = new HashMap<String, String>();
        for (String key : prefs.getAll().keySet()) {
            Object value = prefs.getAll().get(key);
            if (value instanceof Set) {
                map.put(key, Arrays.toString(((Set) value).toArray()));
            } else {
                map.put(key, value.toString());
                if (value instanceof Boolean) {
                    Crashlytics.setBool(key, (Boolean) value);
                } else if (value instanceof String) {
                    Crashlytics.setString(key, (String) value);
                } else if (value instanceof Integer) {
                    Crashlytics.setInt(key, (Integer) value);
                }
            }
        }
        if (firstTime) {
            PackageInfo pInfo;
            try {
                pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
                map.put("Package", pInfo.packageName);
            } catch (NameNotFoundException e) {
            }
            map.put("Language", Locale.getDefault().toString());
            Apptentive.setCustomDeviceData(context, map);
            Apptentive.setCustomPersonData(context, map);
        }
        return map;
    }

    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            boolean ranApptentive = Apptentive.handleOpenedPushNotification(this);
            if (ranApptentive) {
                // Don't try to take any action here. Wait until the customer closes our UI.
                return;
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Fabric.with(this, new Crashlytics());


        String id = PreferenceManager.getDefaultSharedPreferences(this).getString("user_id", "");
        if (id.equals("")) {
            id = new RandomString(12).nextString();
            PreferenceManager.getDefaultSharedPreferences(this).edit().putString("user_id", id).commit();
        }
        if (Build.VERSION.SDK_INT >= 21)
        {
            @SuppressWarnings("ResourceType") UsageStatsManager usageStatsManager = (UsageStatsManager) getSystemService("usagestats");
            if(!usageStatsManager.queryEvents(System.currentTimeMillis() - 86400000, System.currentTimeMillis()).hasNextEvent())
            {
                AlertDialog.Builder alertadd = new AlertDialog.Builder(
                        this);
                alertadd.setTitle("Lollipop Recent Apps");
                alertadd.setMessage("So as to not conflict with other apps, Open Mic+ needs to know what app is currently running on your phone. In Lollipop, you must give explicit permission. On the following page, please allow Open Mic+ this information").show();
                alertadd.setPositiveButton("Launch Settings", new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        try {
                            Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                            startActivity(intent);
                        }
                       catch(Exception e)
                       {
                           Toast.makeText(MainActivity.this, "Sorry your phone does not support this feature. Feel free to manually start/stop Open Mic+ to minimize conflicts.", Toast.LENGTH_LONG).show();
                       }
                    }
                });
               alertadd.setCancelable(false);
                alertadd.show();
            }
        }
        if (!PreferenceManager.getDefaultSharedPreferences(this).getBoolean("no_google_notified", false))
        {
            AlertDialog.Builder alertadd = new AlertDialog.Builder(
                    this);
            alertadd.setTitle("Google Engine Discontinued");
            alertadd.setMessage("Google has asked me to stop using the Google Speech Recognition Engine due to high server usage. PocketSphinx will continue to be supported and in the future, I will be pushing more updates to try to make it easier to use... This also means that Tasker commands will no longer work through Open Mic+... I know a way around this, but it will take some significant time to implement which I don't have right now because of school/college apps. In the mean time please use my other app Commandr which allows you to add commands to Google Now. Also please ensure you take the time to configure the sensitivity value for the PocketSphinx engine.");
            alertadd.setPositiveButton("Ok", null);
            alertadd.setNegativeButton("Download Commandr", new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    final String appName = "com.RSen.Commandr";
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW,
                                Uri.parse("market://details?id=" + appName)));
                    } catch (android.content.ActivityNotFoundException anfe) {
                        startActivity(new Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("http://play.google.com/store/apps/details?id="
                                        + appName)
                        ));
                    }
                }
            });
            alertadd.show();
            PreferenceManager.getDefaultSharedPreferences(this).edit().putString("speech_engine", "pocketsphinx").putString("hot_phrase", "Okay Google").putBoolean("no_google_notified", true).commit();
        }
        if (!PreferenceManager.getDefaultSharedPreferences(this).getBoolean("google_now_setup", false)) {
            String[] localesWithHotwordRecognition = new String[]{"en_US", "en_UK", "en_AU", "en_CA"};
            String[] languagesWithHotwordRecognition = new String[]{"de", "fr"};
            PreferenceManager.getDefaultSharedPreferences(this).edit().putString("langprefs", Locale.getDefault().getLanguage()).commit();
            if (Arrays.asList(languagesWithHotwordRecognition).contains(Locale.getDefault().getLanguage()) || Arrays.asList(localesWithHotwordRecognition).contains(Locale.getDefault().toString())) {
                PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean("google_now_setup", true).putBoolean("google_hotphrase_available", true).commit();
                QustomDialogBuilder builder = new QustomDialogBuilder(this);
                builder.setTitle(getString(R.string.welcome));
                builder.setTitleColor("#CC0000");
                builder.setDividerColor("#CC0000");
                builder.setIcon(R.drawable.ic_launcher);
                builder.setCancelable(false);
                builder.setMessage(getString(R.string.setup_message) + getString(R.string.setup_instructions));
                builder.setPositiveButton(getString(R.string.settings), new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(MainActivity.this, getString(R.string.setup_instructions), Toast.LENGTH_LONG).show();
                        Intent intent = new Intent("android.search.action.SEARCH_SETTINGS");
                        intent.setClassName("com.google.android.googlequicksearchbox", "com.google.android.velvet.ui.settings.PublicSettingsActivity");
                        try {
                            startActivity(intent);
                        } catch (Exception e) {
                            intent = new Intent("android.intent.action.MAIN");
                            intent.setPackage("com.google.android.googlequicksearchbox");
                            try{
                                startActivity(intent);
                            }
                            catch (Exception e2)
                            {
                                Toast.makeText(MainActivity.this, "Sorry, your phone does not support opening up settings for you. Please open up Google Search and configure thsese settings yourself.", Toast.LENGTH_LONG).show();
                            }
                        }

                    }
                });
                builder.setNegativeButton(getString(R.string.done), null);
                builder.show();
            } else {
                PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean("google_now_setup", true).putBoolean("use_gettasks", false).commit();
            }
        }

        setContentView(R.layout.activity_main);
        if (BuildConfig.mobiroo) {
            try {
                MobirooDrm.validateActivity(this);
            } catch (MobirooDrm.MobirooStoreNotFoundException e) {
                e.printStackTrace();
            } catch (MobirooDrm.MobirooException e) {
                e.printStackTrace();
            }
        }

        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo("com.google.android.googlequicksearchbox", 0);
            if (pInfo.versionCode < 300302160) {
                AlertDialog.Builder alertadd = new AlertDialog.Builder(
                        this);
                LayoutInflater factory = LayoutInflater.from(this);
                final View view = factory.inflate(R.layout.update_dialog, null);
                alertadd.setView(view);
                alertadd.setTitle(R.string.google_update_title);
                alertadd.setPositiveButton(getString(R.string.play_store), new OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final String appName = "com.google.android.googlequicksearchbox";
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW,
                                    Uri.parse("market://details?id=" + appName)));
                        } catch (android.content.ActivityNotFoundException anfe) {
                            startActivity(new Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("http://play.google.com/store/apps/details?id="
                                            + appName)
                            ));
                        }
                    }
                });

                alertadd.show();
            }
        } catch (NameNotFoundException e) {
        }

        Apptentive.engage(this, "init");

        if (checkPlayServices()) {
            gcm = GoogleCloudMessaging.getInstance(this);
            regid = getRegistrationId(this);

            if (regid.isEmpty()) {
                registerInBackground();
            }
        } else {

        }
        addSharedPreferencesToLog(this, true);

    }

    private void checkIfOtherVersionInstalled() {
        android.content.pm.PackageManager mPm = getPackageManager();
        boolean selfIsDonate = BuildConfig.DONATE;
        boolean needsToUninstall = false;
        try {
            if (selfIsDonate) {
                //check if should also uninstall free version
                PackageInfo info = mPm.getPackageInfo("com.RSen.OpenMic.Pheonix", 0);
                needsToUninstall = info != null;

            } else {
                //check if should uninstall self
                PackageInfo info = mPm.getPackageInfo(getPackageName(), 0);
                needsToUninstall = info != null;
            }
        } catch (Exception e) {
        }
        if (needsToUninstall && !PreferenceManager.getDefaultSharedPreferences(this).getBoolean("two_versions_asked", false)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.two_versions);
            builder.setMessage(R.string.two_versions_installed);
            builder.setPositiveButton(R.string.yes, new OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Intent.ACTION_DELETE, Uri.fromParts("package",
                            "com.RSen.OpenMic.Pheonix", null));
                    startActivity(intent);
                }
            });
            builder.setNegativeButton(R.string.no, new OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    PreferenceManager.getDefaultSharedPreferences(MainActivity.this).edit().putBoolean("two_versions_asked", true).commit();
                }
            });
            builder.show();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (BuildConfig.mobiroo) {
            try {
                MobirooDrm.validateActivity(this);
            } catch (MobirooDrm.MobirooStoreNotFoundException e) {
                e.printStackTrace();
            } catch (MobirooDrm.MobirooException e) {
                e.printStackTrace();
            }
        }
        setupUI();
        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean(
                "toggle_launch", false)) {
            toggleService();
        }

        LocalBroadcastManager.getInstance(this).registerReceiver(
                serviceStoppedReceiver, new IntentFilter("service-stopped"));
        LocalBroadcastManager.getInstance(this).registerReceiver(
                manualActivationReceiver, new IntentFilter("service-started"));


        checkIfOtherVersionInstalled();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.feedback) {
            //Apptentive.engage(MainActivity.this, "message_center");
            Apptentive.showMessageCenter(this, addSharedPreferencesToLog(this, false));
            // Apptentive.showMessageCenter(MainActivity.this);
            /*
            ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
			clipboard.setText(MyLog.getLog(this));
			String id = PreferenceManager.getDefaultSharedPreferences(this).getString("user_id", "");
			if (id.equals(""))
			{
				id = new RandomString(12).nextString();
				PreferenceManager.getDefaultSharedPreferences(this).edit().putString("user_id", id).commit();
			}
			Config config = new Config("rsenapps.uservoice.com");
			HashMap<String, String> customFields = new HashMap<String, String>();
			customFields.put("Log", MyLog.getLog(this));
			customFields.put("crashID", id);
			config.setCustomFields(customFields);

			UserVoice.init(config, this);
			Toast.makeText(this, R.string.log_copied, Toast.LENGTH_LONG).show();
			UserVoice.launchUserVoice(this);
			*/
        } else if (item.getItemId() == R.id.share) {
            Apptentive.engage(MainActivity.this, "share");
            List<Intent> targetedShareIntents = new ArrayList<Intent>();
            Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            List<ResolveInfo> resInfo = getPackageManager().queryIntentActivities(shareIntent, 0);
            if (!resInfo.isEmpty()) {
                for (ResolveInfo resolveInfo : resInfo) {
                    String packageName = resolveInfo.activityInfo.packageName;
                    Intent targetedShareIntent = new Intent(android.content.Intent.ACTION_SEND);
                    targetedShareIntent.setType("text/plain");
                    targetedShareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.app_name));
                    if (StringUtils.equals(packageName, "com.facebook.katana")) {
                        targetedShareIntent.putExtra(android.content.Intent.EXTRA_TEXT, "http://OpenMic.RSenApps.com");
                    } else {
                        targetedShareIntent.putExtra(android.content.Intent.EXTRA_TEXT, getString(R.string.twitter_share_msg));
                    }

                    targetedShareIntent.setPackage(packageName);
                    targetedShareIntents.add(targetedShareIntent);


                }
                Intent chooserIntent = Intent.createChooser(targetedShareIntents.remove(0), "Select app to share");

                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, targetedShareIntents.toArray(new Parcelable[]{}));

                startActivity(chooserIntent);
            }
        } else if (item.getItemId() == R.id.about) {
            Apptentive.engage(MainActivity.this, "about");
            QustomDialogBuilder builder = new QustomDialogBuilder(this);
            builder.setTitle(getString(R.string.about));
            builder.setTitleColor("#CC0000");
            builder.setDividerColor("#CC0000");
            builder.setMessage(R.string.about_message);
            builder.setPositiveButton(R.string.done, null);
            builder.show();
        } else {  //donate
            Apptentive.engage(MainActivity.this, "donate_button");
            if (getPackageName().matches("com.RSen.OpenMic.Pheonix.Donate")) {
                Intent i = new Intent(this, DonateActivity.class);
                startActivity(i);
            } else {
                final String appName = "com.RSen.OpenMic.Pheonix.Donate";
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("market://details?id=" + appName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("http://play.google.com/store/apps/details?id="
                                    + appName)
                    ));
                }
            }

        }
        return super.onOptionsItemSelected(item);
    }

    private void setupUI() {
        toggleButton = (ImageView) findViewById(R.id.toggleButton);

        if (MyService.isRunning || listenScreenOffActivated) {
            serviceStartedLayout();
        } else {
            serviceStoppedLayout();
        }
        setupOnClickListeners();

    }

    private void serviceStartedLayout() {

        toggleButton.setImageResource(R.drawable.ic_stop_service);
        /*
        ((SettingsFragment) getFragmentManager()
		.findFragmentById(R.id.settingsFragment)).disableAll();
		*/
    }

    private void serviceStoppedLayout() {

        toggleButton.setImageResource(R.drawable.ic_start_service);
        /*
        ((SettingsFragment) getFragmentManager()
				.findFragmentById(R.id.settingsFragment)).enableAll();
				*/
    }

    private void setupOnClickListeners() {
        toggleButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                toggleService();
            }
        });

    }

    protected void toggleService() {

        final Intent myServiceIntent = new Intent(
                getApplicationContext(), MyService.class);
        if (MyService.isRunning) {
            stopService(new Intent(this, ScreenReceiversService.class));
            stopService(new Intent(getApplicationContext(),
                    CheckIfMusicPlayingService.class));
            if (PreferenceManager.getDefaultSharedPreferences(MainActivity.this).getString("speech_engine", "google").equals("pocketsphinx")) {
                stopService(new Intent(this, CheckIfAppBlackListedService.class));
            }
            if (WakelockManager.timeoutAltered) {
                WakelockManager.restoreScreenTimeout(this);
            }
            try {
                AudioUI.lock.reenableKeyguard();
            } catch (Exception e) {
            }
            if (PreferenceManager.getDefaultSharedPreferences(
                    MainActivity.this).getBoolean(
                    "listen_only_screen_off", false)) {
                MyService.isRunning = false;

                listenScreenOffActivated = false;
            } else {
                stopService(myServiceIntent);
            }
            serviceStoppedLayout();
            ((SettingsCardsFragment) getFragmentManager().findFragmentById(R.id.settingsFragment)).switchToStoppedLayout();

        } else {
            AudioUI.activationCount = 0;
            prefs = PreferenceManager.getDefaultSharedPreferences(this);
            if (!prefs.getBoolean("listenHotword", true) && !prefs.getBoolean("wave", false) && !prefs.getBoolean("shake", false)) {
                Toast.makeText(this, getString(R.string.enable_at_least_one), Toast.LENGTH_SHORT).show();
                return;
            }
            addSharedPreferencesToLog(this, false);
            startService(new Intent(this, ScreenReceiversService.class));
            if (PreferenceManager.getDefaultSharedPreferences(MainActivity.this).getString("speech_engine", "google").equals("pocketsphinx")) {
                CheckIfAppBlackListedService.blacklisteddetected = false;
                startService(new Intent(this, CheckIfAppBlackListedService.class));
            }
            if (PreferenceManager.getDefaultSharedPreferences(
                    MainActivity.this).getBoolean(
                    "listen_only_screen_off", false)) {
                MyService.isRunning = true;

                listenScreenOffActivated = true;
            } else {
                startService(myServiceIntent);

            }
            PreferenceManager.getDefaultSharedPreferences(MainActivity.this).edit().putLong("lastStartedTime", System.currentTimeMillis()).commit();
            serviceStartedLayout();
            ((SettingsCardsFragment) getFragmentManager().findFragmentById(R.id.settingsFragment)).switchToStartedLayout();

        }
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(
                manualActivationReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(
                serviceStoppedReceiver);
        super.onPause();
    }

    /**
     * Needed for Google Play In-app Billing. It uses
     * startIntentSenderForResult(). The result is not propagated to the
     * Fragment like in startActivityForResult(). Thus we need to propagate
     * manually to our Fragment.
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        FragmentManager fragmentManager = getFragmentManager();
        Fragment fragment = fragmentManager
                .findFragmentByTag("donationsFragment");
        if (fragment != null) {
            fragment.onActivityResult(requestCode,
                    resultCode, data);
        }
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {

            return false;
        }
        return true;
    }

    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            return "";
        }
        return registrationId;
    }

    /**
     * @return Application's {@code SharedPreferences}.
     */
    private SharedPreferences getGCMPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the regID in your app is up to you.
        return getSharedPreferences(MainActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }

    private void registerInBackground() {
        new AsyncTask() {

            @Override
            protected String doInBackground(Object[] objects) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(MainActivity.this);
                    }
                    regid = gcm.register(SENDER_ID);
                    msg = "Device registered, registration ID=" + regid;

                    // You should send the registration ID to your server over HTTP,
                    // so it can use GCM/HTTP or CCS to send messages to your app.
                    // The request to your server should be authenticated if your app
                    // is using accounts.
                    Apptentive.setPushNotificationIntegration(MainActivity.this, Apptentive.PUSH_PROVIDER_AMAZON_AWS_SNS, regid);

                    // For this demo: we don't need to send it because the device
                    // will send upstream messages to a server that echo back the
                    // message using the 'from' address in the message.

                    // Persist the regID - no need to register again.
                    storeRegistrationId(MainActivity.this, regid);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }
                return msg;
            }

        }.execute(null, null, null);
    }

    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGCMPreferences(context);
        int appVersion = getAppVersion(context);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }

}
