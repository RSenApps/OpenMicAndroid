package com.RSen.OpenMic.Pheonix;

import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

/**
 * all ui speaking/listening/waving...etc. handled
 *
 * @author Ryan
 */
public class AudioUI implements WaveControlListener, ShakeControlListener {
    public static KeyguardLock lock;
    public static int activationCount = 0;
    private SpeechRecognizer speechRecognizer;
    private WaveController waveController;
    private ShakeController shakeController;
    private MyAudioManager myAudioManager;
    private MyTTS myTTS;
    private IncomingTextMessages incomingTextMessages;
    // activation methods
    private boolean listenHotword = false;
    private boolean waveHand = false;
    private boolean shake = false;
    private boolean readMessages = false;
    private Context context;
    private boolean onPhoneCall = false;
    private PhoneStateListener phoneStateListener = new PhoneStateListener() {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            if (state == TelephonyManager.CALL_STATE_RINGING) {
                if (waveHand) {
                    waveController.stop();
                }
                if (shake) {
                    shakeController.stop();
                }
                stopListening();
                onPhoneCall = true;
            } else if (state == TelephonyManager.CALL_STATE_IDLE && onPhoneCall) { // called
                // when
                // listening
                // started
                if (waveHand) {
                    waveController.stop();
                    waveController = new WaveController(context, AudioUI.this);
                }
                if (shake) {
                    shakeController.stop();
                    shakeController = new ShakeController(context, AudioUI.this);
                }
                startListening();
            }
            super.onCallStateChanged(state, incomingNumber);
        }
    };

    public AudioUI(Context context) {
        this.context = context;

        setActivationMethods();
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        KeyguardManager myKeyGuard = (KeyguardManager) context
                .getSystemService(Context.KEYGUARD_SERVICE);
        if (lock == null) {
            lock = myKeyGuard.newKeyguardLock("openmic");
        }
        if ((listenHotword || waveHand || shake || readMessages)
                && prefs.getBoolean("listen_screen_off", false)) {
            WakelockManager.acquireWakelock(context);
        }

        if (listenHotword) {
            if (prefs.getString("speech_engine", "google").equals("pocketsphinx")) {
                speechRecognizer = new PocketSphinxSpeechRecognizer(context, this);
            } else {
                speechRecognizer = new GoogleSpeechRecognizer(context, this);
            }
            myAudioManager = new MyAudioManager(context, this);
        }
        if (waveHand) {
            waveController = new WaveController(context, this);
        }
        if (shake) {
            shakeController = new ShakeController(context, this);
        }
        if (readMessages) {
            myTTS = new MyTTS(context);
            incomingTextMessages = new IncomingTextMessages(context, this);
        }
        TelephonyManager mgr = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        if (mgr != null) {
            mgr.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        }
    }

    public void stopListening() {
        if (listenHotword) {
            speechRecognizer.stopListening();
        }
    }

    public void startListening() {
        if (listenHotword) {
            speechRecognizer.startListening();
        }
    }

    private void setActivationMethods() {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        listenHotword = prefs.getBoolean("listenHotword", true);
        waveHand = prefs.getBoolean("wave", false);
        readMessages = prefs.getBoolean("read_messages", false);
        shake = prefs.getBoolean("shake", false);
    }

    public void activateGoogleNow() {
        activationCount++;
        ScreenReceiver.isActivating = true;
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);

        Intent i = new Intent(context,
                CheckIfAppBlackListedService.class);
        context.stopService(i);

        if (prefs.getBoolean("vibration_feedback", false)) {
            Vibrator v = (Vibrator) context
                    .getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(200);
        }
        stopListening();

        if (myAudioManager != null && !prefs.getBoolean("use_gettasks", true)) {
            myAudioManager.startListening();
        }
        WakeupActivity.useNewTask = false;
        if (!ScreenReceiver.isScreenOn
                || KeyguardReceiver.keyguardEnabled) {
            WakeupActivity.useNewTask = true;
            lock.disableKeyguard();
        }
        if (!ScreenReceiver.isScreenOn && prefs.getBoolean("turn_off_screen", false)) {
            WakelockManager.changeScreenTimeout(context, 30000);
        }
        /*
         * if(PreferenceManager.getDefaultSharedPreferences(context).getBoolean(
		 * "listen_screen_off", false)) {
		 * MyLog.l("Google Now activated when screen off", context); final
		 * KeyguardManager keyguardManager = (KeyguardManager) context
		 * .getSystemService(Context.KEYGUARD_SERVICE); final PowerManager
		 * powerManager = (PowerManager)
		 * context.getSystemService(Context.POWER_SERVICE);
		 * 
		 * if(!powerManager.isScreenOn()) { MyLog.l("screen is off", context);
		 * WakelockManager.turnOnScreen(context); final Handler waitForUnlock =
		 * new Handler(new Handler.Callback() {
		 * 
		 * @Override public boolean handleMessage(Message msg) {
		 * 
		 * startGoogleNow(); return true; } }); new Thread(new Runnable() {
		 * 
		 * @Override public void run() { while(!powerManager.isScreenOn()) { try
		 * { Thread.sleep(500); } catch (InterruptedException e) { } } try {
		 * Thread.sleep(500); } catch (InterruptedException e) { }
		 * MyLog.l("Screen on", context); //if(
		 * keyguardManager.inKeyguardRestrictedInputMode()) { //it is locked
		 * //myAudioManager.lockscreenDeactivated = true; KeyguardLock mLock =
		 * keyguardManager.newKeyguardLock("OpenMic"); mLock.disableKeyguard();
		 * MyLog.l("Unlocking Screen", context); try { Thread.sleep(1000); }
		 * catch (InterruptedException e) { } waitForUnlock.sendEmptyMessage(0);
		 * //} else { //it is not locked // startGoogleNow(); //}
		 * 
		 * } }).start();
		 * 
		 * } else { startGoogleNow(); }
		 * 
		 * 
		 * 
		 * } else { startGoogleNow(); }
		 */

        WakelockManager.turnOnScreen(context);
        if (prefs.getBoolean("use_gettasks", true)) {
            i = new Intent(context, MyService.class);
            i.setAction("GNACTIVATED");
            context.startService(i);
        }
        context.startActivity(new Intent(context, WakeupActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK

                        | Intent.FLAG_FROM_BACKGROUND));

    }

    /*
     * private void startGoogleNow() { final Intent intent = new
     * Intent("android.intent.action.MAIN"); intent.setComponent(new
     * ComponentName("com.google.android.googlequicksearchbox",
     * "com.google.android.googlequicksearchbox.VoiceSearchActivity"));
     * intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
     * Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED | Intent.FLAG_FROM_BACKGROUND);
     *
     * context.startActivity(intent); }
     */
    public void stop() {
        MyLog.l("STOP", context);

        // if(PreferenceManager.getDefaultSharedPreferences(context).getBoolean("listen_screen_off",
        // false))
        // {
        // KeyguardManager keyguardManager = (KeyguardManager) context
        // .getSystemService(Context.KEYGUARD_SERVICE);
        // KeyguardLock keyguardLock =
        // keyguardManager.newKeyguardLock("Open Mic");
        // keyguardLock.reenableKeyguard();
        // }
        // lock.reenableKeyguard();
        WakelockManager.releaseWakelock();
        if (listenHotword) {
            speechRecognizer.stop();
            myAudioManager.stop();
        }
        if (waveHand) {
            waveController.stop();
        }
        if (shake) {
            shakeController.stop();
        }
        TelephonyManager mgr = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        if (mgr != null) {
            mgr.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
        }
    }

    public void HotwordHeard() {
        activateGoogleNow();
    }

    @Override
    public void waveControlActivated() {
        MyLog.l("Wave Activated", context);

        activateGoogleNow();
    }

    public void speak(String phrase) {
        stopListening();
        myTTS.addToQueue(phrase);
        startListening();
    }

    @Override
    public void shakeControlActivated() {
        MyLog.l("Shake Activated", context);

        activateGoogleNow();
    }

}
