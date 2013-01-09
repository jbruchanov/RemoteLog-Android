package com.scurab.android;

import android.Manifest;
import android.R;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;

public class NotificationHelper {

    public static Notification createSimpleNotification(Context context,
	    String subj, String msg) {
	return createSimpleNotification(context, R.drawable.stat_sys_warning,
		subj, msg);
    }

    /**
     * For vibration is {@link Manifest.permission#VIBRATE} necessary in manifest!
     * @param context
     * @param iconResId
     * @param subj
     * @param msg
     * @return
     */
    public static Notification createSimpleNotification(Context context,
	    int iconResId, String subj, String msg) {
	NotificationCompat.Builder b = new NotificationCompat.Builder(context);	
	
	int defaults = Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND;
	if(context.checkCallingOrSelfPermission(Manifest.permission.VIBRATE) == PackageManager.PERMISSION_GRANTED) {
	    defaults |= Notification.DEFAULT_VIBRATE;
	}
	
	b.setContentTitle(subj)
	.setContentText(msg)
	.setSmallIcon(iconResId)
	.setLargeIcon(BitmapFactory.decodeResource(context.getResources(),iconResId))
	.setDefaults(defaults)
	.setContentIntent(PendingIntent.getBroadcast(context, 0, new Intent(), 0));
	
	return b.build();
    }
    
    public static Notification createQuestionNotification(Context context,
	    String subj, String msg, String... actions) {
	return createQuestionNotification(context, R.drawable.stat_sys_warning, subj, msg, actions);
    }
    
    public static Notification createQuestionNotification(Context context,
	    int iconResId, String subj, String msg, String... actions) {
	NotificationCompat.Builder b = new NotificationCompat.Builder(context);	
	
	int defaults = Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND;
	if(context.checkCallingOrSelfPermission(Manifest.permission.VIBRATE) == PackageManager.PERMISSION_GRANTED) {
	    defaults |= Notification.DEFAULT_VIBRATE;
	}
	
	b.setContentTitle(subj)
	.setContentText(msg)
	.setSmallIcon(iconResId)
	.setLargeIcon(BitmapFactory.decodeResource(context.getResources(),iconResId))
	.setDefaults(defaults)
	.setContentIntent(PendingIntent.getBroadcast(context, 0, new Intent(), 0));
	for(String action : actions){
	    b.addAction(0, action, PendingIntent.getBroadcast(context, 0, new Intent(), 0));
	}
	return b.build();
	
    }
}
