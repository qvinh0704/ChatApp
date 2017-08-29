package com.example.quangvinh.chatapprx.Helper;

import android.util.Log;

/**
 * Created by Binh.Nguyen on 12/28/2016.
 */

public class LogUtil {

    public static final String TAG = "LLL";


    private static boolean LOGGING_ENABLED = true;

    private static final int STACK_TRACE_LEVELS_UP = 5;

    public static void verbose(String message)
    {
        if (LOGGING_ENABLED)
        {
            Log.v(TAG, getClassNameMethodNameAndLineNumber() + message);
        }
    }

    public static void debug(String message)
    {
        if (LOGGING_ENABLED)
        {
            Log.d(TAG, getClassNameMethodNameAndLineNumber() + message);
        }
    }

    public static void info(String message)
    {
        if (LOGGING_ENABLED)
        {
            Log.i(TAG, getClassNameMethodNameAndLineNumber() + message);
        }
    }

    public static void error(String message)
    {
        if (LOGGING_ENABLED)
        {
            Log.e(TAG, getClassNameMethodNameAndLineNumber() + message);
        }
    }

    private static int getLineNumber()
    {
        return Thread.currentThread().getStackTrace()[STACK_TRACE_LEVELS_UP].getLineNumber();
    }

    private static String getClassName()
    {
        String fileName = Thread.currentThread().getStackTrace()[STACK_TRACE_LEVELS_UP].getFileName();
        return fileName.substring(0, fileName.length() - 5);
    }

    private static String getMethodName()
    {
        return Thread.currentThread().getStackTrace()[STACK_TRACE_LEVELS_UP].getMethodName();
    }

    private static String getClassNameMethodNameAndLineNumber()
    {
        return "[" + getClassName() + "." + getMethodName() + "()-" + getLineNumber() + "]: ";
    }
}
