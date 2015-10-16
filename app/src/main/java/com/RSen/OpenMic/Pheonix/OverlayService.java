package com.RSen.OpenMic.Pheonix;

import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;

public class OverlayService extends Service {
    View mView;
    RelativeLayout layout;
    View imageView;
    View textView;
    float alpha = 255;
    CountDownTimer timer = new CountDownTimer(500, 20) {

        @Override
        public void onTick(long arg0) {
            alpha -= 10.2;
            layout.setBackgroundColor(Color.argb((int) alpha, 0, 0, 0));
            imageView.setAlpha(alpha / 255);
            textView.setAlpha(alpha / 255);
        }

        @Override
        public void onFinish() {
            layout.setVisibility(View.GONE);
            stopSelf();
        }
    };
    WindowManager wm;
    WindowManager.LayoutParams params;
    boolean isShown = false;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!isShown) {
            isShown = true;
            LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            mView = inflater.inflate(R.layout.overlay, null);
            alpha = 255;
            layout = (RelativeLayout) mView.findViewById(R.id.layout);
            imageView = mView.findViewById(R.id.imageView1);
            textView = mView.findViewById(R.id.textView1);
            params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY, 0,
                    PixelFormat.TRANSLUCENT);
            wm = (WindowManager) getSystemService(WINDOW_SERVICE);
            wm.addView(mView, params);

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    timer.start();

                }
            }, 1000);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mView != null) {
            ((WindowManager) getSystemService(WINDOW_SERVICE)).removeView(mView);
            mView = null;
        }
        isShown = false;
    }
}

