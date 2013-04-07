package com.scurab.android;

import android.util.Log;
import com.scurab.android.rlw.ILog;

/**
 * Basic android LogAdapter for using together with RLog<br/>
 * {@link com.scurab.android.rlw.RLog#setLog(com.scurab.android.rlw.ILog)}
 * This class is for Android 1.x
 *
 * @author Jiri Bruchanov
 */
public class AndroidLogAdapter1 implements ILog {

    @Override
    public void i(Object source, String category, String msg) {
        Log.i(category, msg);
    }

    @Override
    public void v(Object source, String category, String msg) {
        Log.v(category, msg);
    }

    @Override
    public void d(Object source, String category, String msg) {
        Log.d(category, msg);
    }

    @Override
    public void e(Object source, String category, String msg) {
        Log.e(category, msg);
    }

    @Override
    public void e(Object source, String category, Throwable t) {
        Log.e(category, t.getMessage(), t);
    }

    @Override
    public void n(Object source, String category, String msg) {
        Log.v(category, msg);
    }

    @Override
    public void wtf(Object source, String category, String msg) {
        //not implemented in SDK < 8
    }

    @Override
    public void w(Object source, String category, String msg) {
        Log.w(category, msg);
    }
}
