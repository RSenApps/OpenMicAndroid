package com.RSen.OpenMic.Pheonix;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;

public class CheckIfMusicPlayingService extends Service {
    final Handler checkIfMusicPlayingDelayed = new Handler();
    boolean isMusicPlaying = false;
    Runnable runnable;

    public CheckIfMusicPlayingService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        final AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        final boolean pocketsphinx = PreferenceManager.getDefaultSharedPreferences(this).getString("speech_engine", "google").equals("pocketsphinx");
        isMusicPlaying = !pocketsphinx; //google engine will only start this if music is already playing, ps will start at the beginning
        runnable = new Runnable() {

            @Override
            public void run() {
                if (!am.isMusicActive()) {
                    if (!pocketsphinx) {
                        MyLog.l("Music has stopped playing",
                                getApplicationContext());
                        Intent i = new Intent(getApplicationContext(),
                                MyService.class);
                        startService(i);
                        stopSelf();
                    } else if (isMusicPlaying) {
                        isMusicPlaying = false;
                        MyLog.l("Music has stopped playing",
                                getApplicationContext());
                        Intent i = new Intent(getApplicationContext(),
                                MyService.class);
                        startService(i);
                    }
                    if (pocketsphinx) {
                        checkIfMusicPlayingDelayed.postDelayed(this, 2000);
                    }
                } else {
                    if (pocketsphinx && !isMusicPlaying) {
                        MyLog.l("Music has started playing",
                                getApplicationContext());
                        Intent i = new Intent(getApplicationContext(), MyService.class);
                        i.setAction("GNACTIVATED");
                        startService(i);
                    }
                    isMusicPlaying = true;
                    checkIfMusicPlayingDelayed.postDelayed(this, 2000);
                }
            }
        };
        checkIfMusicPlayingDelayed.postDelayed(runnable, 2000);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        checkIfMusicPlayingDelayed.removeCallbacks(runnable);
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
