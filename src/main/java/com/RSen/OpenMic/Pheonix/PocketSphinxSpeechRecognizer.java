package com.RSen.OpenMic.Pheonix;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import edu.cmu.pocketsphinx.Config;
import edu.cmu.pocketsphinx.Decoder;
import edu.cmu.pocketsphinx.Hypothesis;
import edu.cmu.pocketsphinx.RecognitionListener;
import edu.cmu.pocketsphinx.SpeechRecognizer;
import edu.cmu.pocketsphinx.SphinxUtil;

public class PocketSphinxSpeechRecognizer extends com.RSen.OpenMic.Pheonix.SpeechRecognizer implements
        RecognitionListener {

    protected static String KWS_SRCH_NAME = "wakeup_search";
    protected static String KEYPHRASE = "okay google";
    protected Context context;
    protected SpeechRecognizer recognizer;
    private boolean listening = false;
    private AudioUI uiReference;
    private boolean useBluetooth;
    private BluetoothHelper myBluetoothHelper;

    private boolean stopped = false;

    static {
        System.loadLibrary("pocketsphinx_jni");
    }

    public PocketSphinxSpeechRecognizer(Context context, AudioUI uiReference) {
        super(context, uiReference);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        //ensure other model is deleted...

        KEYPHRASE = prefs.getString("hot_phrase", "okay google").toLowerCase();
        this.context = context;
        this.uiReference = uiReference;

        startListening();
    }

    private static String joinPath(File dir, String path) {
        return new File(dir, path).getPath();
    }

    public static boolean checkIfValidHotphrase(String hotphrase, Context context) {
        if (hotphrase.trim().length() < 1) {
            return false;
        }
        char[] chars = hotphrase.toCharArray();

        for (char c : chars) {
            if (!(Character.isLetter(c) || c == ' ')) {
                return false;
            }
        }
        String[] words = hotphrase.trim().split(" ");
        try {

            DataInputStream dataIO;
            for (String word : words) {
                if (word.toLowerCase().matches("ok|okay|google|hey|computer|jarvis")) {
                    continue;
                }
                dataIO = new DataInputStream(new FileInputStream(new File(joinPath(SphinxUtil.syncAssets(context), "models/lm/hub4.5000.dic"))));
                boolean wordFound = false;
                String strLine = null;

                while ((strLine = dataIO.readLine()) != null) {
                    if (strLine.startsWith(word.toLowerCase())) {
                        wordFound = true;
                        break;
                    }
                    if (Thread.interrupted()) {
                        dataIO.close();
                        return false;
                    }
                }
                dataIO.close();
                if (!wordFound) {
                    return false;
                }

            }

        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static boolean deleteDirectory(File path) {
        if (path.exists()) {
            File[] files = path.listFiles();
            if (files == null) {
                return true;
            }
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    deleteDirectory(files[i]);
                } else {
                    files[i].delete();
                }
            }
        }
        return (path.delete());
    }

    public void startListening() {
        if (useBluetooth && !myBluetoothHelper.isStarted) {
            myBluetoothHelper.start();
        }
        listening = true;
        if (recognizer == null) {
            try {
                recognizer = createRecognizer();
            } catch (RuntimeException e) {
                context.stopService(new Intent(context, MyService.class));
                return;
            }
            recognizer.addListener(this);
            recognizer.setSearch(KWS_SRCH_NAME);

        }
        stopped = false;
        recognizer.startListening();
    }

    public void stopListening() {
        if (useBluetooth && myBluetoothHelper.isStarted) {
            myBluetoothHelper.stop();
        }
        listening = false;
        stop();
    }

    private SpeechRecognizer createRecognizer() throws RuntimeException {
        File appDir;
        try {
            appDir = SphinxUtil.syncAssets(context);
        } catch (IOException e) {
            Toast.makeText(context, context.getString(R.string.storage_not_available), Toast.LENGTH_LONG).show();
            throw new RuntimeException(e);
        }
        if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean("hasOldModel", true)) {
            File file = new File(joinPath(appDir, "models/lm/cmu07a.dic"));
            file.delete();
            file = new File(joinPath(appDir, "models/hmm/en-us-semi"));
            deleteDirectory(file);
            PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean("hasOldModel", false);
        }
        Config config = Decoder.defaultConfig();

        config.setString("-dict", joinPath(appDir, "models/lm/hub4.5000.dic"));

        config.setString("-hmm", joinPath(appDir, "models/hmm/hub4wsj_sc_8k"));

        // config.setString("-rawlogdir", appDir.getPath());
        // config.setBoolean("-fwdflat", false);
        // config.setBoolean("-bestpath", false);
        config.setFloat("-samprate", 8000.0);
        int sensitivity = PreferenceManager.getDefaultSharedPreferences(context).getInt("sensitivity_int", 3);
        double threshold = 1e-16;
        switch (sensitivity) {
            case 0:
                threshold = 1e-2;
                break;
            case 1:
                threshold = 1e-6;
                break;
            case 2:
                threshold = 1e-10;
                break;
            case 4:
                threshold = 1e-30;
                break;
            case 5:
                threshold = 1e-50;
                break;
        }
        config.setFloat("-kws_threshold", threshold);
        // config.setInt("-ds", 2);
        //  config.setInt("-maxwpf", 5);
        // config.setInt("-maxhmmpf", 10000);
        config.setBoolean("-remove_noise", true);

        recognizer = new SpeechRecognizer(config);

        recognizer.setKws(KWS_SRCH_NAME, KEYPHRASE);
        // Toast.makeText(context, "Open Mic+ ready", Toast.LENGTH_SHORT).show();
        return recognizer;
    }

    public void stop() {
        if (!stopped) {
            stopped = true;
            listening = false;
            //recognizer.stopListening();
            recognizer.removeListener(this);
            recognizer.stop();
            recognizer = null;
        }
    }

    @Override
    public void onPartialResult(Hypothesis hypothesis) {
        if (listening && hypothesis.getHypstr().equals(KEYPHRASE)) {
            MyLog.l("Heard: " + hypothesis.getHypstr(), context);

            stopListening();
            uiReference.HotwordHeard();

        }
    }

    @Override
    public void onResult(Hypothesis hypothesis) {

    }

    @Override
    public void log(String s) {
        MyLog.l(s, context);
    }

}