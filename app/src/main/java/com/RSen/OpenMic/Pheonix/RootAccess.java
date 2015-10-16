package com.RSen.OpenMic.Pheonix;

import android.content.Context;

import java.io.DataOutputStream;
import java.io.IOException;

public class RootAccess {
    public static boolean requestRoot() {
        Process p;
        try {
            // Preform su to get root privledges
            p = Runtime.getRuntime().exec("su");

            // Attempt to write a file to a root-only
            DataOutputStream os = new DataOutputStream(p.getOutputStream());
            os.writeBytes("echo \"Do I have root?\" >/system/sd/temporary.txt\n");

            // Close the terminal
            os.writeBytes("exit\n");
            os.flush();
            try {
                p.waitFor();
                if (p.exitValue() != 255) {
                    return true;
                } else {
                    return false;
                }
            } catch (InterruptedException e) {
                return false;
            }
        } catch (IOException e) {
            return false;
        }

    }

    public static void hideNotification(Context context) {
        Process p;
        try {
            // Preform su to get root privledges
            p = Runtime.getRuntime().exec("su");
            DataOutputStream os = new DataOutputStream(p.getOutputStream());
            os.writeBytes("service call notification 6 s16 " + context.getPackageName() + " i32 " + context.getApplicationInfo().uid + " i32 0");

            // Close the terminal
            os.writeBytes("exit\n");
            os.flush();
        } catch (Exception e) {
        }
    }

    public static void showNotification(Context context) {
        Process p;
        try {
            // Preform su to get root privledges
            p = Runtime.getRuntime().exec("su");
            DataOutputStream os = new DataOutputStream(p.getOutputStream());
            os.writeBytes("service call notification 6 s16 " + context.getPackageName() + " i32 " + context.getApplicationInfo().uid + " i32 1");

            // Close the terminal
            os.writeBytes("exit\n");
            os.flush();
        } catch (Exception e) {
        }
    }
}
