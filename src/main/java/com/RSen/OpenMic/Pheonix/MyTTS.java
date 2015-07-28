package com.RSen.OpenMic.Pheonix;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Locale;

/**
 * Should handle only speaking of text
 *
 * @author Ryan
 */
public class MyTTS implements OnInitListener {
    Context context;
    private TextToSpeech tts;
    private boolean useTTS = false;
    private HashMap<String, String> myHashRender = new HashMap<String, String>(); // used
    // to
    // change
    // what
    // stream
    // tts
    // is
    // spoken
    // on

    public MyTTS(Context context) {
        this.context = context;
        tts = new TextToSpeech(context, MyTTS.this);

    }

    public void stopSpeaking() {
        tts.stop();
    }

    public void stop() {
        tts.shutdown();
    }

    @Override
    public void onInit(int status) {
        // TTS is successfully initialized
        if (status == TextToSpeech.SUCCESS) {
            // Setting speech language
            int result = tts.setLanguage(Locale.getDefault());
            // If your device doesn't support language you set above
            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                // Cook simple toast message with message
                Toast.makeText(context, context.getString(R.string.language_not_supported),
                        Toast.LENGTH_LONG).show();
                useTTS = false;
            }
            // Enable the button - It was disabled in main.xml (Go back and
            // Check it)
            else {
                useTTS = true;
            }
            // TTS is not initialized properly
        } else {
            useTTS = false;
        }
    }

    public void speak(String whatToSay) {
        if (useTTS) {
            tts.speak(whatToSay, TextToSpeech.QUEUE_FLUSH, myHashRender);
        } else {
            Toast.makeText(context, whatToSay, Toast.LENGTH_SHORT).show();
        }
        while (tts.isSpeaking()) {
            try {
                Thread.sleep(100); // wait for tts to finish
            } catch (InterruptedException e) {
            }
        }
    }

    public void addToQueue(String whatToSay) {
        if (useTTS) {
            tts.speak(whatToSay, TextToSpeech.QUEUE_ADD, myHashRender);
        } else {
            Toast.makeText(context, whatToSay, Toast.LENGTH_SHORT).show();
        }
        while (tts.isSpeaking()) {
            try {
                Thread.sleep(100); // wait for tts to finish
            } catch (InterruptedException e) {
            }
        }
    }

}
