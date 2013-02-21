package com.scurab.android;

import android.util.Log;

import com.scurab.android.rlw.ILog;
import com.scurab.android.rlw.RLog;

/**
 * Basic android LogAdapter for using together with RLog<br/>
 * this class is for API >= 8
 * {@link RLog#setLog(ILog)}
 * @author Jiri Bruchanov
 *
 */
public class AndroidLogAdapter2 extends AndroidLogAdapter1 {

    @Override
    public void wtf(Object source, String category, String msg) {
        Log.wtf(category, msg);
    }
}
