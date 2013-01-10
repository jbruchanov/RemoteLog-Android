package com.scurab.android.gcm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.location.Location;

import com.scurab.android.KillAppException;
import com.scurab.android.KnowsActiveActivity;
import com.scurab.android.rlw.Locator;
import com.scurab.android.rlw.Locator.OnLocationListener;
import com.scurab.android.rlw.RLog;
import com.scurab.android.rlw.RemoteLog;
import com.scurab.gwt.rlw.shared.model.PushMessage;

/**
 * Push message handler
 * 
 * @author Jiri Bruchanov
 * 
 */
public class GCMMessageHandler extends GCMAbstractMessageHandler {

    @Override
    public void onCustomMessage(Context context, PushMessage pm) {
	RLog.n(this, CATEGORY, pm.toString());
    }

    @Override
    public void onTakeScreenshot(Context context, PushMessage pm) {
	Context app = context.getApplicationContext();
	if (app instanceof KnowsActiveActivity) {
	    RLog.takeScreenshot(this, KEY_TAKESCREENSHOT,
		    (KnowsActiveActivity) app);
	} else {
	    RLog.e(
		    this,
		    KEY_TAKESCREENSHOT
			    + " Application object doesn't implement KnowsActiveActivity iface!");
	}
    }

    @Override
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

    @Override
    public void onNotification(Context context, PushMessage pm) {
	HashMap<String, Object> params = parseSimple(pm);
	// parse
	String subj = (String) params.get("Title");
	String message = (String) params.get("Message");
	// show
	Notification n = NotificationHelper.createSimpleNotification(context, pm ,
		subj, message);
	NotificationManager manager = (NotificationManager) context
		.getSystemService(Context.NOTIFICATION_SERVICE);
	manager.notify((int) System.currentTimeMillis(), n);
    }

    @Override
    public void onEcho(Context context, PushMessage pm) {
	// not necessary, every push is logged on income
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public void onQuestion(Context context, PushMessage pm) {
	HashMap<String, Object> params = parseSimple(pm);
	// parse
	String subj = (String) params.get("Title");
	String message = (String) params.get("Message");
	List list = ((ArrayList) params.get("Actions"));
	String[] actions = (String[]) list.toArray(new String[list.size()]);

	// show
	Notification n = NotificationHelper.createQuestionNotification(context,  pm ,
		subj, message, actions);
	NotificationManager manager = (NotificationManager) context
		.getSystemService(Context.NOTIFICATION_SERVICE);
	manager.notify((int) System.currentTimeMillis(), n);
    }

    @SuppressWarnings("unchecked")
    private HashMap<String, Object> parseSimple(PushMessage pm) {
	return RemoteLog.getGson().fromJson(pm.getParams(), HashMap.class);
    }

    @Override
    public void onLastKnonwLocation(Context context, PushMessage pm) {
	Locator l = new Locator(context);
	if (!l.isGeolocationEnabled()) {
	    RLog.n(this, "Location", "Geolocation is disabled!");
	} else {
	    l.getMyLocation(new OnLocationListener() {
		@Override
		public void onLocationFound(String provider, Location l) {
		    RLog.n(GCMMessageHandler.this, "Location", String.format("Provider:%s, Location:%s", provider, getLocationSring(l)));
		}
	    });
	}
    }
    
    private static String getLocationSring(Location l){
	return String.format("lat:%s, lng:%s, alt:%s, accuracy:%s", l.getLatitude(), l.getLongitude(), l.getAltitude(), l.getAccuracy());
    }

    @Override
    public void onReloadSettings(Context context, PushMessage pm) {
	RemoteLog rl = RemoteLog.getInstance();
	if(rl != null){
	    rl.loadSettings(null);
	}
    }
}
