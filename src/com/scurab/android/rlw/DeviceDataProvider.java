package com.scurab.android.rlw;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.regex.Pattern;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.DisplayMetrics;

import com.google.android.gcm.GCMRegistrar;
import com.scurab.gwt.rlw.shared.model.Device;
import com.scurab.java.rlw.Core;

/**
 * Help class for getting info about device
 * 
 * @author Joe Scurab
 * 
 */
public class DeviceDataProvider {

    private static final String PLATFORM = "Android";
    public static final Pattern EMAIL_ADDRESS = Pattern
	    .compile("[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" + "\\@"
		    + "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" + "(" + "\\."
		    + "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" + ")+");

    /**
     * Get base Device info object for registration
     * 
     * @param c
     * @return
     */
    public static Device getDevice(Context c) {
	Device d = new Device();
	d.setBrand(Build.MANUFACTURER);
	// d.setDescription();
	d.setDetail(getDetails(c));
	d.setDevUUID(Build.SERIAL);
	d.setModel(Build.MODEL);
	d.setOwner(getOwner(c));
	d.setPlatform(PLATFORM);
	d.setResolution(getResolution(c));
	d.setVersion(String.valueOf(Build.VERSION.SDK_INT));
	d.setPushID(getPushId(c));
	return d;
    }

    /**
     * Return push token if device is sucessfuly registered, otherwise null
     * 
     * @param c
     * @return
     */
    public static String getPushId(Context c) {
	if (GCMRegistrar.isRegisteredOnServer(c)) {
	    return GCMRegistrar.getRegistrationId(c);
	} else {
	    return null;
	}
    }

    /**
     * Returns resolution of display width x height
     * 
     * @param c
     * @return
     */
    public static String getResolution(Context c) {
	DisplayMetrics dm = c.getResources().getDisplayMetrics();
	return String.format("%sx%s", dm.widthPixels, dm.heightPixels);
    }

    /**
     * JSON serialized {@link Build}
     * 
     * @return
     */
    public static String getDetails(Context c) {
	HashMap<String, Object> result = new HashMap<String, Object>();
	Field[] fields = Build.class.getDeclaredFields();
	for (Field f : fields) {
	    try {
		result.put(f.getName(), String.valueOf(f.get(null)));
	    } catch (Exception e) {
		result.put(f.getName(), e.getMessage());
	    }
	}
	result.putAll(getHardwareFeatures(c));
	result.putAll(getRuntimeInfo(c));
	String s = Core.GSON.toJson(result);
	return s;
    }

    /**
     * Returns Hardware features
     * 
     * @param c
     * @return
     */
    public static HashMap<String, Object> getHardwareFeatures(Context c) {
	HashMap<String, Object> result = new HashMap<String, Object>();
	PackageManager pm = c.getPackageManager();
	Field[] fields = PackageManager.class.getDeclaredFields();
	for (Field f : fields) {
	    String name = f.getName();

	    if (name.startsWith("FEATURE")) {
		Object o = null;
		try {
		    String value = String.valueOf(f.get(null));
		    o = pm.hasSystemFeature(value);
		} catch (Exception e) {
		    o = e.getMessage();
		}
		result.put(name, String.valueOf(o));
	    }
	}
	return result;
    }

    /**
     * Returns runtime info
     * 
     * @param c
     * @return
     */
    public static HashMap<String, Object> getRuntimeInfo(Context c) {
	HashMap<String, Object> result = new HashMap<String, Object>();
	Runtime r = Runtime.getRuntime();
	result.put("MEMORY_MAX", r.maxMemory());
	result.put("MEMORY_CLASS", getShouldMemory(c));
	result.put("CPU_AVAILABLE", r.availableProcessors());
	result.put("SUPERUSER_STATUS", RootCheck.getDeviceRoot());
	return result;
    }

    private static int getShouldMemory(Context c) {
	ActivityManager am = (ActivityManager) c
		.getSystemService(Context.ACTIVITY_SERVICE);
	int memoryClass = am.getMemoryClass();
	return memoryClass;
    }

    /**
     * Needs
     * <code><uses-permission android:name="android.permission.GET_ACCOUNTS" /></code>
     * in manifest!
     * 
     * @param c
     * @return primary mail if possible, otherwise null
     */
    public static String getOwner(Context c) {
	String result = null;
	try {
	    Pattern emailPattern = EMAIL_ADDRESS; // API level 8+
	    Account[] accounts = AccountManager.get(c).getAccounts();
	    for (Account account : accounts) {
		if (emailPattern.matcher(account.name).matches()) {
		    String possibleEmail = account.name;
		    result = possibleEmail;
		    break;
		}
	    }
	} catch (Throwable e) {
	    e.printStackTrace();
	}
	return result;
    }
}
