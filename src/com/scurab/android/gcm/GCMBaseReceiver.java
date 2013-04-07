package com.scurab.android.gcm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.util.Log;
import com.google.android.gcm.GCMConstants;
import com.google.android.gcm.GCMRegistrar;
import com.scurab.android.rlw.RemoteLog;
import com.scurab.gwt.rlw.shared.model.PushMessage;

import java.io.IOException;

/**
 * Base GCM reciver <br/>
 * Handles messages for registration and unregistration.
 *
 * @author jbruchanov
 */

class GCMBaseReceiver extends BroadcastReceiver {
    private static final String TAG = "GCMReceiver";
    private static final String PREFERENCES = "com.google.android.gcm";
    private static final String PROPERTY_REG_ID = "regId";
    private static final String PROPERTY_APP_VERSION = "appVersion";

    public static final String CDM_REGISTRATION = "com.google.android.c2dm.intent.REGISTRATION";


    private static final String COLLAPSE_KEY = "collapse_key";
    private static final String TIMESTAMP = "timestamp";
    private static final String PARAMS = "params";
    private static final String MESSAGECONTEXT = "context";

    @Override
    public void onReceive(final Context context, final Intent intent) {
        RemoteLog.catchUncaughtErrors(Thread.currentThread());

        final String action = intent.getAction();
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // don't use GCMConstants, probably different values!
                    if (intent.hasExtra("error")) {
                        onError(intent.getStringExtra("error"));
                    } else if (CDM_REGISTRATION.equals(action)) {
                        if (intent.hasExtra(GCMConstants.EXTRA_REGISTRATION_ID)) {
                            onRegistered(context, intent);
                        } else {
                            onUnregistered(context, intent);
                        }
                    } else if (GCMConstants.INTENT_TO_GCM_UNREGISTRATION
                            .equals(action)) {
                        onUnregistered(context, intent);
                    } else if (GCMConstants.INTENT_FROM_GCM_MESSAGE
                            .equals(action)) // ok
                    {
                        onMessage(context, parseMessage(intent.getExtras()));
                    }
                } catch (Exception e) {
                    Log.v(TAG + ".onReceive(..)@Thread",
                            String.format("Err:%s", e.getMessage()));
                    e.printStackTrace();
                }
            }
        });
        RemoteLog.catchUncaughtErrors(t);
        t.start();
    }

    /**
     * Called when msg has error
     *
     * @param stringExtra
     */
    protected void onError(String stringExtra) {
        Log.e("GCMReceiver.onError(.)", stringExtra);
    }

    /**
     * Called when registered msg is received
     *
     * @param context
     * @param intent
     * @throws IOException
     */
    protected void onRegistered(Context context, Intent intent)
            throws IOException {
        String id = intent.getExtras().getString(
                GCMConstants.EXTRA_REGISTRATION_ID);
        // mServer.registerDevice(id);
        setRegistrationId(context, id);
        GCMRegistrar.setRegisteredOnServer(context, true);
        RemoteLog rl = RemoteLog.getInstance();
        if (rl != null) {
            rl.updatePushToken(id);
        }
    }

    /**
     * Called when unregistered msg is received
     *
     * @param context
     * @param intent
     * @throws IOException
     */
    protected void onUnregistered(Context context, Intent intent)
            throws IOException {
        @SuppressWarnings("unused")
        String oldid = setRegistrationId(context, null);
        Log.v(TAG + ".onUnregistered(..)",
                "No ID saved, app is probably already unregistered!");
        GCMRegistrar.setRegisteredOnServer(context, false);
        RemoteLog rl = RemoteLog.getInstance();
        if (rl != null) {
            rl.updatePushToken("");
        }
    }

    protected void onMessage(Context context, PushMessage pm) {
    }

    static PushMessage parseMessage(Bundle b) {

        String name = b.getString(COLLAPSE_KEY);
        String timeStamp = b.getString(TIMESTAMP);
        String params = null;
        if (b.containsKey(PARAMS)) {
            params = b.getString(PARAMS);
        }
        String context = null;
        if (b.containsKey(MESSAGECONTEXT)) {
            context = b.getString(MESSAGECONTEXT);
        }

        PushMessage pm = new PushMessage();
        pm.setName(name);
        pm.setTimeStamp(timeStamp);
        pm.setParams(params);
        pm.setMessageContext(context);

        return pm;
    }

    static String setRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGCMPreferences(context);
        String oldRegistrationId = prefs.getString(PROPERTY_REG_ID, "");
        int appVersion = getAppVersion(context);
        Log.v(TAG, "Saving regId on app version " + appVersion);
        Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
        return oldRegistrationId;
    }

    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Coult not get package name: " + e);
        }
    }

    private static SharedPreferences getGCMPreferences(Context context) {
        return context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
    }
}
