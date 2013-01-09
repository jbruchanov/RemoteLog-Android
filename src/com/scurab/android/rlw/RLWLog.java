package com.scurab.android.rlw;

import java.io.ByteArrayOutputStream;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.View;

import com.scurab.android.KnowsActiveActivity;
import com.scurab.gwt.rlw.shared.model.LogItem;
import com.scurab.gwt.rlw.shared.model.LogItemBlobRequest;

public class RLWLog{

    public static void n(Object source, String category, String msg) {
	send(source, category, msg);
    }
    
    public static void i(Object source, String msg) {
	send(source, "Info", msg);
    }
    
    public static void i(Object source, String category, String msg) {
	send(source, category, msg);
    }
    
    public static void v(Object source, String msg) {
	send(source, "Verbose", msg);
    }
    
    public static void v(Object source, String category, String msg) {
	send(source, category, msg);
    }

    public static void d(Object source, String msg) {
	send(source, "Debug", msg);
    }
    
    public static void d(Object source, String category, String msg) {
	send(source, category, msg);
    }

    public static void e(Object source, String msg) {
	send(source, "Error", msg);
    }
    
    public static void e(Object source, String category, String msg) {
	send(source, category, msg);
    }
    
    public static void e(Object source, Throwable t) {
	send(source, "Error", getMessageOrClassName(t), new LogItemBlobRequest(
		"text/plain", "error.txt", RemoteLog.getStackTrace(t)
			.getBytes()));
    }
    
    public static void e(Object source, String category, Throwable t) {
	send(source, category, getMessageOrClassName(t), new LogItemBlobRequest(
		"text/plain", "error.txt", RemoteLog.getStackTrace(t)
			.getBytes()));
    }
    
    private static String getMessageOrClassName(Throwable t){
	String s = t.getMessage();
	if(!(s != null && s.length() > 0)){
	    s = t.getClass().getSimpleName();
	}
	return s;
    }

    public static void wtf(Object source, String msg) {
	send(source, "WTF", msg);
    }
    
    public static void wtf(Object source, String category, String msg) {
	send(source, category, msg);
    }

    /**
     * Save screenshot of app<br/>
     * Application must implement {@link KnowsActiveActivity}
     * 
     * @param source
     * @param msg
     * @parem c
     */
    public static void takeScreenshot(Object source, String msg,
	    KnowsActiveActivity c) {
	try {
	    Activity a = c.getCurrentActivity();
	    if (a != null) {
		View v = a.getWindow().getDecorView();
		takeScreenshot(source, msg, v);
	    } else {
		send(source, "Screenshot", "Current Activity is null\n" + msg);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    e(RLWLog.class, String.format("%s\n%s",
		    e.getMessage(), RemoteLog.getStackTrace(e, null)));
	}
    }

    /**
     * Save screenshot of Activity
     * 
     * @param source
     * @param msg
     * @param act
     */
    public static void takeScreenshot(Object source, String msg, Activity act) {
	takeScreenshot(source, msg, act.getWindow().getDecorView());
    }

    /**
     * Save screenshot of view
     * 
     * @param source
     * @param msg
     * @param view
     */
    public static void takeScreenshot(Object source, String msg, View view) {
	try {
	    // prepare view
	    view.destroyDrawingCache();
	    view.buildDrawingCache(false);
	    // get bitmap
	    Bitmap b = view.getDrawingCache();
	    final ByteArrayOutputStream baos = new ByteArrayOutputStream();

	    Exception e = null;
	    boolean saved = false;
	    // save it
	    try {
		saved = b.compress(Bitmap.CompressFormat.JPEG, 85, baos);
	    } catch (Exception ex) {
		e = ex;
	    }
	    // create blob item
	    LogItemBlobRequest libr = null;
	    if (saved) {
		libr = new LogItemBlobRequest("image/jpeg", String.format(
			"%s.jpg", System.currentTimeMillis()),
			baos.toByteArray());

	    } else {
		libr = new LogItemBlobRequest("text/plain", String.format(
			"%s.txt", System.currentTimeMillis()), e.getMessage()
			.getBytes());
	    }
	    view.destroyDrawingCache();
	    // send it
	    send(source, "Screenshot", msg, libr);
	} catch (Exception e) {
	    e.printStackTrace();
	    e(RLWLog.class, String.format("takeScreenshot\n%s\n%s",
		    e.getMessage(), RemoteLog.getStackTrace(e, null)));
	}
    }

    /**
     * Send custom log item
     * @param source
     * @param category
     * @param msg
     */
    public static void send(Object source, String category, String msg) {
	send(source, category, msg, null);
    }

    /**
     * Send custim log item with blob
     * @param source
     * @param category
     * @param msg
     * @param libr
     */
    public static void send(Object source, String category, String msg,
	    LogItemBlobRequest libr) {
	LogItem li = RemoteLog.createLogItem();
	li.setCategory(category);
	li.setMessage(msg);
	RemoteLog.getLogSender().addLogItem(li, libr);
    }
}
