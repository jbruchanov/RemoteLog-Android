package com.scurab.android;

import android.util.Log;

import com.scurab.android.rlw.ILog;
import com.scurab.android.rlw.RLog;

/**
 * Basic android LogAdapter for using together with RLog<br/>
 * {@link RLog#setLog(ILog)}
 * @author Jiri Bruchanov
 *
 */
public class AndroidLogAdapter implements ILog {

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
	Log.wtf(category, msg);	
    }

    @Override
    public void w(Object source, String category, String msg) {
	Log.w(category, msg);
    }
}
