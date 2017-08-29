package com.example.quangvinh.chatapprx.Helper;

import android.app.Activity;
import android.content.Context;
import android.os.Vibrator;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by QuangVinh on 3/7/2017.
 */

public class HelperDevice {
    public static Vibrator v;

    public static void HideKeyBoard(Activity mAcitivy) {
        View view = mAcitivy.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) mAcitivy.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static void initialVibrate(Context context) {
        v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
    }

    public static void vibrateDevice() {
        if (v != null)
            v.vibrate(500);
    }

    public static void stopVibrate() {
        if (v != null) {
            v = null;
        }
    }
}
