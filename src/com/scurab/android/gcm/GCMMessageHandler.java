package com.scurab.android.gcm;

import android.content.Context;

import com.scurab.android.KillAppException;
import com.scurab.android.KnowsActiveActivity;
import com.scurab.android.rlw.RLWLog;
import com.scurab.android.rlw.RemoteLog;
import com.scurab.gwt.rlw.shared.model.PushMessage;

public class GCMMessageHandler extends GCMBaseReceiver {

    public static final String KEY_ECHO = "Echo";
    public static final String KEY_NOTIFICATION = "Notification";
    public static final String KEY_RESEND_REGISTRATION = "Resend registration";
    public static final String KEY_KILLAPP = "Kill app";
    public static final String KEY_TAKESCREENSHOT = "TakeScreenshot";

    @Override
    public void onMessage(Context context, PushMessage pm) {
	RLWLog.d(this, "PushNotification:" + pm.toString());
	try {
	    String name = pm.getName();
	    if (KEY_ECHO.equalsIgnoreCase(name)) {
		onEcho(context, pm);
	    } else if (KEY_NOTIFICATION.equalsIgnoreCase(name)) {
		onNotification(context, pm);
	    } else if (KEY_RESEND_REGISTRATION.equalsIgnoreCase(name)) {
		onResendRegistration(context, pm);
	    } else if (KEY_KILLAPP.equalsIgnoreCase(name)) {
		onKillApp(context, pm);
	    } else if (KEY_TAKESCREENSHOT.equalsIgnoreCase(name)) {
		onTakeScreenshot(context, pm);
	    } else {
		onCustomMessage(context, pm);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    public void onTakeScreenshot(Context context, PushMessage pm) {	
	Context app = context.getApplicationContext();
	if(app instanceof KnowsActiveActivity){
	    RLWLog.takeScreenshot(this, KEY_TAKESCREENSHOT, (KnowsActiveActivity)app);
	}else{
	    RLWLog.e(this, KEY_TAKESCREENSHOT + " Application object doesn't implement KnowsActiveActivity iface!");
	}
	
    }

    public void onCustomMessage(Context context, PushMessage pm) {

    }

    public void onKillApp(Context context, PushMessage pm) {
	Thread t = new Thread(new Runnable() {
	    @Override
	    public void run() {
		throw new KillAppException();
	    }
	});
	RemoteLog.catchUncaughtErrors(t);
	t.run();
	
    }

    public void onResendRegistration(Context context, PushMessage pm) {

    }

    public void onNotification(Context context, PushMessage pm) {

    }

    public void onEcho(Context context, PushMessage pm) {

    }
}
