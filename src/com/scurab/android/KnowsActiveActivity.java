package com.scurab.android;

import android.app.Activity;

/**
 * Implement this interface for your Application object to enable
 * "TakeScreenshot" push notification feature.
 *
 * @author Joe Scurab
 *
 */
public interface KnowsActiveActivity {
    public Activity getCurrentActivity();
}