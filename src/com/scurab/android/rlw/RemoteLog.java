package com.scurab.android.rlw;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.net.MalformedURLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Stack;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;

import com.scurab.android.KillAppException;
import com.scurab.gwt.rlw.shared.model.Device;
import com.scurab.gwt.rlw.shared.model.DeviceRespond;
import com.scurab.gwt.rlw.shared.model.LogItem;
import com.scurab.gwt.rlw.shared.model.LogItemBlobRequest;
import com.scurab.gwt.rlw.shared.model.Settings;
import com.scurab.gwt.rlw.shared.model.SettingsRespond;

public final class RemoteLog {

    private String sAppVersion;
    private String sAppBuild;
    private String sAppName;
    private ServiceConnector sConnector;
    private static LogSender sLogSender;
    private SharedPreferences sPreferences;
    private int sDeviceID;
    private final String DEVICE_ID = "DEVICE_ID";
    private SettingsRespond mSettings;
    private static final RemoteLog sSelf = new RemoteLog();

    private RemoteLog() {

    }
    
    /**
     * 
     * @return reference only if RemoteLog was initialized by init method
     */
    public static RemoteLog getInstance(){
	return sLogSender != null ? sSelf : null;
    }

    /**
     * By default resendRegistration is false
     * 
     * @param c
     * @param appName
     * @param serverLocation
     * @param resendRegistration
     * @param {@see #RLog}
     * @throws NameNotFoundException
     * @throws MalformedURLException
     */
    public static RemoteLog init(Context c, String appName, String serverLocation, int logMode)
	    throws NameNotFoundException, MalformedURLException {
	return init(c, appName, serverLocation, false, logMode);
    }

    /**
     * 
     * @param c
     * @param appName
     *            - global app name for log
     * @param serverLocation
     *            ig http://myserver:8080/RemoteLogWeb/
     * @param resendRegistration
     *            - always send registration
     * @throws NameNotFoundException
     * @throws MalformedURLException
     */
    public static RemoteLog init(Context c, String appName, String serverLocation,
	    boolean resendRegistration, int logMode) throws NameNotFoundException,
	    MalformedURLException {
	if(appName == null){
	    throw new IllegalArgumentException("appName is null");
	}
	if(serverLocation == null){
	    throw new IllegalArgumentException("serverLocation is null");
	}
	RLog.setMode(logMode);
	sSelf.sPreferences = c.getSharedPreferences(
		RemoteLog.class.getSimpleName(), Context.MODE_PRIVATE);

	PackageInfo pInfo = c.getPackageManager().getPackageInfo(
		c.getPackageName(), 0);

	sSelf.sAppVersion = pInfo.versionName;
	sSelf.sAppBuild = String.valueOf(pInfo.versionCode);
	sSelf.sAppName = appName;
	sSelf.sConnector = new ServiceConnector(serverLocation);
	sLogSender = new LogSender(sSelf.sConnector);

	sSelf.registerDevice(c, resendRegistration);
	return sSelf;
    }

    /**
     * Load settings from server<br/>
     * It's blocking => call it from nonMainThread
     * 
     * @param appName
     * @param callback
     * @throws IllegalStateException
     */
    public void loadSettings(
	    AsyncCallback<SettingsRespond> callback)
	    throws IllegalStateException {
	if (sDeviceID == 0) {
	    throw new IllegalStateException(
		    "Device is not registered on server!");
	}
	if (sAppName == null) {
	    throw new IllegalStateException(
		    "Not initialized!");
	}
	try {
	    mSettings = sConnector.loadSettings(sDeviceID, sAppName);
	    onSettings(mSettings);
	    if (callback != null) {
		callback.call(getSettings());
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    protected void onSettings(SettingsRespond resp) {
	try {
	    if (resp.getCount() > 0) {
		Settings[] ss = resp.getContext();
		//going from end, where should be device specific
		for (int i = ss.length - 1; i >= 0; i--) {
		    @SuppressWarnings("unchecked")
		    HashMap<String,Object> vs = Core.GSON.fromJson(ss[i].getJsonValue(), HashMap.class);
		    if(vs != null && vs.containsKey("RLog")){
			String logMode = String.valueOf(vs.get("RLog"));
			int parsed = RLog.getMode(logMode);
			if(parsed != -1){
			    RLog.setMode(parsed);
			}
			break;
		    }
		}
	    }

	} catch (Exception e) {
	    RLog.e(this, e);
	    // ignore any error and let the code continue
	    e.printStackTrace();
	}
    }    

    private static Thread sRegDeviceThread = null;

    public void registerDevice(final Context c, boolean resend) {
	registerDevice(c, resend, true);
    }

    /**
     * register device, must be called in nonMainThread
     * @param c
     * @param resend
     * @param async - to start as new Thread, if false loadSettings is not called
     */
    public void registerDevice(final Context c, final boolean resend,
	    final boolean async) {
	if (sRegDeviceThread != null) {
	    throw new IllegalStateException("Register process is already running!");
	}

	sDeviceID = sPreferences.getInt(DEVICE_ID, 0);	// if devId == 0 not registered yet
	
	sRegDeviceThread = new Thread(new Runnable() {
	    @Override
	    public void run() {
		if (sDeviceID == 0 || resend) {
		    sendDeviceToServer(c);
		}
		
		//run only in nonMainThread and if device is registered on server
		if(async && sDeviceID != 0){
		    loadSettings(null);
		}
		// don't forget to set it null
		sRegDeviceThread = null;
	    }
	}, "RemoteLog-RegisterDevice");
	// start
	if (async) {
	    sRegDeviceThread.start();
	} else {
	    sRegDeviceThread.run();
	}
    }

    /**
     * Must bu runned in nonMainThread
     * @param c
     */
    private void sendDeviceToServer(Context c) {
	// get device
	Device d = DeviceDataProvider.getDevice(c);
	try {
	    // save it
	    DeviceRespond dr = sConnector.saveDevice(d);
	    if (dr == null || dr.hasError()) {
		System.err.print(dr.getMessage());
		sDeviceID = 0;
	    } else {
		sDeviceID = dr.getContext().getDeviceID();
	    }
	    // save id to shared preferences
	    Editor e = sPreferences.edit();
	    e.putInt(DEVICE_ID, sDeviceID).commit();
	} catch (Throwable e) {
	    e.printStackTrace();
	}
    }

    /**
     * Active wait to finish registration process
     */
    public void waitForDeviceRegistration() {
	waitForDeviceRegistration(Integer.MIN_VALUE);
    }

    /**
     * 
     * @param timeOut
     *            in milis
     */
    public void waitForDeviceRegistration(int timeOut) {
	if (sRegDeviceThread != null) {
	    try {
		for (int i = 0, n = timeOut / 50; i < n; i++) {
		    Thread.sleep(50);
		}
	    } catch (InterruptedException e) {
		e.printStackTrace();
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	}
    }

    public boolean isDeviceRegistered() {
	return sPreferences.getInt(DEVICE_ID, 0) > 0;
    }

    public int getDeviceId() {
	return sDeviceID;
    }

    public static LogItem createLogItem() throws IllegalStateException {
	if (sLogSender == null) {
	    throw new IllegalStateException("Not initialized!");
	}
	LogItem li = new LogItem();
	li.setDeviceID(sSelf.sDeviceID);
	li.setAppBuild(sSelf.sAppBuild);
	li.setApplication(sSelf.sAppName);
	li.setAppVersion(sSelf.sAppVersion);
	li.setDate(new Date());
	return li;
    }

    public ServiceConnector getConnector() {
	return sConnector;
    }

    protected static LogSender getLogSender() {
	return sSelf.sLogSender;
    }

    /**
     * Override default UncaughtExceptionHandler
     * 
     * @param t
     */
    public static void catchUncaughtErrors(Thread t) {
	final UncaughtExceptionHandler oldOne = t.getUncaughtExceptionHandler();
	t.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
	    @Override
	    public void uncaughtException(Thread thread, Throwable ex) {
		if(sLogSender == null){
		    //not initialized => unable to send it
		}else if ((RLog.getMode() ^ RLog.ERROR) == RLog.ERROR) {
		    Throwable[] ts = new Throwable[1];
		    String stack = getStackTrace(ex, ts);

		    LogItemBlobRequest libr = new LogItemBlobRequest(
			    LogItemBlobRequest.MIME_TEXT_PLAIN,
			    "fatalerror.txt", stack.getBytes());

		    RLog.send(this,
			    (ex instanceof KillAppException) ? "KillApp"
				    : "UncaughtException", ts[0].getMessage(),
			    libr);

		    sLogSender.waitForEmptyQueue();
		}
		oldOne.uncaughtException(thread, ex);
	    }
	});
    }

    public static String getStackTrace(Throwable ex) {
	return getStackTrace(ex, null);
    }

    /**
     * Get more informative stacktrace
     * 
     * @param ex
     * @param outT
     *            output param for reason
     * @return
     */
    public static String getStackTrace(Throwable ex, Throwable[] outT) {
	StringWriter sw = new StringWriter();
	PrintWriter pw = new PrintWriter(sw);

	Stack<Throwable> subStack = new Stack<Throwable>();
	Throwable t = ex;

	for (int i = 0; i < 5 && t != null; i++) {
	    subStack.push(t);
	    t = t.getCause();
	}
	if (outT != null && outT.length > 0) {
	    outT[0] = subStack.peek();
	}

	for (int i = 0; i < subStack.size(); i++) {
	    t = subStack.pop();
	    t.printStackTrace(pw);
	    pw.println();
	}
	return sw.toString();
    }

    /**
     * Get server settings
     * 
     * @return
     */
    public SettingsRespond getSettings() {
	return mSettings;
    }
    
    public void updatePushToken(String pushToken){
	if (sDeviceID == 0) {
	    throw new IllegalStateException(
		    "Device is not registered on server!");
	}
	try {
	    sConnector.updatePushToken(sDeviceID, pushToken);
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }
}
