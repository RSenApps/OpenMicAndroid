package com.RSen.OpenMic.Pheonix;

import android.content.Context;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.os.CountDownTimer;

public class MyAudioManager {
    public boolean lockscreenDeactivated = false; // means ignore one loss
    private AudioUI uiReference;
    private boolean ensureQuietStarted = false;
    private boolean listeningLossTransient = false;
    // transient
    private boolean listeningGain = false;
    private Context context;
    CountDownTimer ensureQuiet = new CountDownTimer(2000, 2000) {

        public void onTick(long millisUntilFinished) {
        }

        public void onFinish() {
            MyLog.l("Quiet Ensured", context);
            listeningLossTransient = false;
            listeningGain = false;
            ensureQuietStarted = false;
            stop();
            // if(PreferenceManager.getDefaultSharedPreferences(context).getBoolean("listen_screen_off",
            // false))
            // {
            // MyLog.l("Reenable keyguard", context);
            // KeyguardManager keyguardManager = (KeyguardManager) context
            // .getSystemService(Context.KEYGUARD_SERVICE);
            // KeyguardLock keyguardLock =
            // keyguardManager.newKeyguardLock("Open Mic");
            // keyguardLock.reenableKeyguard();
            // }

            uiReference.startListening();
        }
    };
    CountDownTimer timeoutCounter = new CountDownTimer(15000, 15000) {

        public void onTick(long millisUntilFinished) {
        }

        public void onFinish() {
            MyLog.l("Timeout Google Now", context);
            listeningLossTransient = false;
            listeningGain = false;
            ensureQuietStarted = false;
            stop();
            // if(PreferenceManager.getDefaultSharedPreferences(context).getBoolean("listen_screen_off",
            // false))
            // {
            // MyLog.l("Reenable keyguard", context);
            // KeyguardManager keyguardManager = (KeyguardManager) context
            // .getSystemService(Context.KEYGUARD_SERVICE);
            // KeyguardLock keyguardLock =
            // keyguardManager.newKeyguardLock("Open Mic");
            // keyguardLock.reenableKeyguard();
            // }

            uiReference.startListening();
        }
    };
    private boolean successful = false; // results were spoken in google now
    OnAudioFocusChangeListener afChangeListener = new OnAudioFocusChangeListener() {
        public void onAudioFocusChange(int focusChange) {
            MyLog.l("Audio Focus: " + focusChange, context);
            if (listeningLossTransient
                    && focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
                if (lockscreenDeactivated) {
                    MyLog.l("lockscreen deactivated noise took into account",
                            context);
                    lockscreenDeactivated = false;
                    return;
                }

                listeningLossTransient = false;
                listeningGain = true;
                if (ensureQuietStarted) {
                    MyLog.l("Not Quiet", context);
                    successful = true;
                    ensureQuietStarted = false;
                    ensureQuiet.cancel();
                } else {
                    timeoutCounter.start();
                }
            } else if (listeningGain
                    && focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                timeoutCounter.cancel();
                if (successful) {
                    MyLog.l("Google Now spoke", context);
                    ensureQuiet.onFinish();
                    return;
                }
                if (!ensureQuietStarted) {
                    MyLog.l("Ensure quiet Started", context);
                    ensureQuietStarted = true;
                    ensureQuiet.start();
                }
                listeningLossTransient = true;
                listeningGain = false;
            }
        }

    };
    private AudioManager am;

    public MyAudioManager(Context c, AudioUI uiReference) {
        context = c;
        this.uiReference = uiReference;
        am = (AudioManager) c.getSystemService(Context.AUDIO_SERVICE);

    }

    public void startListening() {
        lockscreenDeactivated = false;
        listeningLossTransient = true;
        listeningGain = false;
        successful = false;
        ensureQuietStarted = false;
        am.requestAudioFocus(afChangeListener,
                // Use the music stream.
                AudioManager.STREAM_MUSIC,
                // Request permanent focus.
                AudioManager.AUDIOFOCUS_GAIN);
    }

    public void stop() {
        ensureQuiet.cancel();
        listeningLossTransient = false;
        listeningGain = false;
        am.abandonAudioFocus(afChangeListener);
    }
}
