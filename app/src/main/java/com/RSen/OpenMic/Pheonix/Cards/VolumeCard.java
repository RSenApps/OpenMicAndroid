package com.RSen.OpenMic.Pheonix.Cards;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.RSen.OpenMic.Pheonix.GoogleSpeechRecognizer;
import com.RSen.OpenMic.Pheonix.R;
import com.RSen.OpenMic.Pheonix.Refreshable;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardHeader;

/**
 * Created by Ryan on 6/11/2014.
 */
public class VolumeCard extends Card {
    public boolean isDismissing = false;
    float lastVolume = 0;
    Refreshable refreshCallback;

    public VolumeCard(Context context, Refreshable refreshCallback) {
        super(context, R.layout.volume_card);
        isDismissing = false;
        this.refreshCallback = refreshCallback;
        CardHeader header = new CardHeader(context);
        header.setTitle(context.getString(R.string.volume));
        addCardHeader(header);
    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {
        final LinearLayout volumeBar = (LinearLayout) view.findViewById(R.id.volumeBar);

        final ObjectAnimator animBar = ObjectAnimator.ofFloat(volumeBar, "weightSum", 0, 0);
        animBar.setDuration(1000);
        animBar.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                volumeBar.requestLayout();
            }
        });
        final Handler handler = new Handler();
        Runnable updateVolume = new Runnable() {
            @Override
            public void run() {
                float currentVolume = GoogleSpeechRecognizer.lastVolume;
                if (currentVolume < 0) {
                    currentVolume = 0;
                }
                if (currentVolume > 10) {
                    currentVolume = 10;
                }

                if (currentVolume == 0) {
                    currentVolume = .5f; //prevent divide by 0
                }
                if (currentVolume > lastVolume) {
                    animBar.cancel();
                    animBar.setDuration(1000);
                    float start = volumeBar.getWeightSum();

                    animBar.setFloatValues(start, 10 / currentVolume);
                    animBar.start();
                    lastVolume = currentVolume;
                } else if (!animBar.isRunning()) {

                    animBar.setDuration(1000);

                    animBar.setFloatValues(volumeBar.getWeightSum(), 10 / currentVolume);
                    animBar.start();
                    lastVolume = currentVolume;
                }


                handler.postDelayed(this, 200);
            }
        };
        updateVolume.run();
    }


    @Override
    public int getType() {
        return 1;
    }
}
