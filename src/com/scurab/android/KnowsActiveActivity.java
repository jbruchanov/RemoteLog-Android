package com.scurab.android;

import android.app.Activity;

/**
 * Implement this interface for your Application object to enable
 * "TakeScreenshot" push notification feature.
 * <p/>
 * Basically the best approach is to override {@link Activity#onStart()} and {@link Activity#onStop()}
 * methods and set current activity to Application object which implements this interface.
 *
 * @author Jiri Bruchanov
 */
public interface KnowsActiveActivity {
    public Activity getCurrentActivity();
}