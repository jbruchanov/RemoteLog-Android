package com.scurab.android.gcm;

import android.content.Context;

import com.scurab.android.rlw.RLog;
import com.scurab.gwt.rlw.shared.model.PushMessage;

abstract class GCMAbstractMessageHandler extends GCMBaseReceiver {

    public static final String KEY_ECHO = "Echo";
    public static final String KEY_NOTIFICATION = "Notification";
    public static final String KEY_QUESTION = "Question";
    public static final String KEY_KILLAPP = "KillApp";
    public static final String KEY_TAKESCREENSHOT = "TakeScreenshot";
    public static final String KEY_LASTKNOWNLOCATION = "LastKnownLocation";
    public static final String KEY_RELOADSETTINGS = "ReloadSettings";
    
    public static final String CATEGORY = "Push";

    @Override
    public void onMessage(Context context, PushMessage pm) {
	RLog.v(this, CATEGORY, pm.toString());
	try {
	    String name = pm.getName();
	    if (KEY_ECHO.equalsIgnoreCase(name)) {
		onEcho(context, pm);
	    } else if (KEY_NOTIFICATION.equalsIgnoreCase(name)) {
		onNotification(context, pm);
	    } else if (KEY_QUESTION.equalsIgnoreCase(name)) {
		onQuestion(context, pm);
	    } else if (KEY_KILLAPP.equalsIgnoreCase(name)) {
		onKillApp(context, pm);
	    } else if (KEY_TAKESCREENSHOT.equalsIgnoreCase(name)) {
		onTakeScreenshot(context, pm);
	    } else if (KEY_LASTKNOWNLOCATION.equalsIgnoreCase(name)){
		onLastKnonwLocation(context, pm);
	    }else if(KEY_RELOADSETTINGS.equalsIgnoreCase(name)) {
		onReloadSettings(context, pm);
	    } else {
		onCustomMessage(context, pm);
	    }
	} catch (Exception e) {
	    RLog.e(this, CATEGORY, e);
	    e.printStackTrace();
	}
    }
    
    public abstract void onEcho(Context context, PushMessage pm);
    public abstract void onNotification(Context context, PushMessage pm);
    public abstract void onQuestion(Context context, PushMessage pm);
    public abstract void onKillApp(Context context, PushMessage pm);
    public abstract void onTakeScreenshot(Context context, PushMessage pm);
    public abstract void onCustomMessage(Context context, PushMessage pm);
    public abstract void onLastKnonwLocation(Context context, PushMessage pm);
    public abstract void onReloadSettings(Context context, PushMessage pm);
}
