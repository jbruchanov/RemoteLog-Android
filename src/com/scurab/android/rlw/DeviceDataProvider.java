package com.scurab.android.rlw;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.hardware.Camera;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import com.google.android.gcm.GCMRegistrar;
import com.scurab.gwt.rlw.shared.model.Device;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Help class for getting info about device
 *
 * @author Jiri Bruchanov
 */
public class DeviceDataProvider {

    private static final String PLATFORM = "Android";

    private static final String SHARED_PREF = "RemoteLog";
    private static final String SHARED_PREF_UUID = "UUID";
    public static final Pattern EMAIL_ADDRESS = Pattern
            .compile("[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" + "\\@"
                    + "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" + "(" + "\\."
                    + "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" + ")+");

    private static int sVersion = 0;

    static {
        initVersion();
    }

    /**
     * Get base Device info object for registration
     *
     * @param c
     * @return
     */
    public Device getDevice(Context c) {
        Device d = new Device();
        d.setVersion(String.valueOf(sVersion));
        try {
            d.setBrand(getManufacturer());
            // d.setDescription();
            d.setDetail(getDetails(c));
            d.setDevUUID(getSerialNumber(c));
            d.setModel(Build.MODEL);
            d.setOwner(getOwner(c));
            d.setPlatform(PLATFORM);
            d.setResolution(getResolution(c));
            d.setPushID(getPushId(c));
        } catch (Exception e) {
            d.setDescription(e.getMessage() + "\n" + RemoteLog.getStackTrace(e));
        }
        return d;
    }

    @SuppressLint("InlinedApi")
    private static void initVersion() {
        try {
            sVersion = Build.VERSION.SDK_INT;
        } catch (Throwable e) {
            try {
                sVersion = Integer.parseInt(Build.VERSION.SDK);
            } finally {
            }
        }
    }

    private String getManufacturer() {
        if (sVersion >= 4) {
            return Build.MANUFACTURER;
        } else {
            return "API_LEVEL_4_MIN";
        }
    }

    public static String getSerialNumber(Context c) {
        SharedPreferences sp = c.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
        String uuid = sp.getString(SHARED_PREF_UUID, null);

        if (TextUtils.isEmpty(uuid)) {
            if (sVersion >= 9) {
                uuid = Build.SERIAL;
            } else if (Build.VERSION.SDK_INT >= 3) {
                uuid = Settings.Secure.getString(c.getContentResolver(), Settings.Secure.ANDROID_ID);
            }
            if (!validateSerialNumber(uuid)) {
                uuid = null;
            }

            if (TextUtils.isEmpty(uuid)) {
                uuid = getMACAddress(c);
            }

            if (TextUtils.isEmpty(uuid)) {
                uuid = UUID.randomUUID().toString();
            }

            sp.edit().putString(SHARED_PREF_UUID, uuid).commit();
        }
        return uuid;
    }

    /**
     * Return MAC address of wifi NIC
     * @param context
     * @return value or null if any problem
     */
    static String getMACAddress(Context context) {
        try {
            WifiManager manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo info = manager.getConnectionInfo();
            return info != null ? info.getMacAddress() : null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Overwrite any uuid with new one<br/>
     * User it only if we are restoring
     * @param c
     * @param uuid
     */
    static void saveSerialNumber(Context c, String uuid){
        SharedPreferences sp = c.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
        sp.edit().putString(SHARED_PREF_UUID, uuid).commit();
    }

    /**
     * Checks if serial number is usual dummy stuff
     * @param serialNumber
     * @return true if it's fine
     */
    private static boolean validateSerialNumber(String serialNumber){
        String[] values = {"UNKNOWN","0123456789ABCDEF", "0123456789"};
        for(String v : values){
            if(v.equalsIgnoreCase(serialNumber)){
                return false;
            }
        }
        return true;
    }

    /**
     * Return push token if device is sucessfuly registered, otherwise null
     *
     * @param c
     * @return
     */
    protected static String getPushId(Context c) {
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
    protected String getResolution(Context c) {
        WindowManager wm = (WindowManager) c.getSystemService(Context.WINDOW_SERVICE);
        Display d = wm.getDefaultDisplay();
        Point p = new Point();
        if (Build.VERSION.SDK_INT >= 17) {
            d.getRealSize(p);
        } else if (Build.VERSION.SDK_INT >= 14) { //it's hidden, maybe it is in lower versions
            try {
                Method m = Display.class.getMethod("getRealSize", Point.class);
                m.invoke(d, p);
            } catch (Throwable t) {
                t.printStackTrace();
                //ignore it
            }
        }

        if (p.x == 0 || p.y == 0) {
            return getVirtualResolution(c);
        }
        return String.format("%sx%s", Math.min(p.x, p.y), Math.max(p.x, p.y));
    }

    /**
     * Returns display resolution excluding button bar, so this value is changing based on rotation
     *
     * @return
     */
    protected String getVirtualResolution(Context c) {
        DisplayMetrics dm = c.getResources().getDisplayMetrics();
        Point p = new Point();
        p.set(dm.widthPixels, dm.heightPixels);
        return String.format("%sx%s", Math.min(p.x, p.y), Math.max(p.x, p.y));
    }

    protected String getWifiMACAddress(Context c) {
        String result = null;
        try {
            if (c.checkCallingOrSelfPermission(Manifest.permission.ACCESS_WIFI_STATE) == PackageManager.PERMISSION_GRANTED) {
                WifiManager wifiMan = (WifiManager) c
                        .getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiMan.getConnectionInfo();
                result = wifiInfo != null ? wifiInfo.getMacAddress() : null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * JSON serialized {@link Build}
     *
     * @return
     */
    protected String getDetails(Context c) {
        HashMap<String, Object> result = new HashMap<String, Object>();
        Field[] fields = Build.class.getDeclaredFields();
        for (Field f : fields) {
            try {
                f.setAccessible(true);
                result.put(f.getName(), String.valueOf(f.get(null)));
            } catch (Exception e) {
                result.put(f.getName(), e.getMessage());
            }
        }
        result.putAll(getHardwareFeatures(c));
        result.putAll(getRuntimeInfo(c));
        result.putAll(getTelephonyInfo(c));
        result.putAll(getCameraInfo(c));
        result.put("WIFI_MAC", getWifiMACAddress(c));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            result.put("PERMANENT_MENU_KEY", ViewConfiguration.get(c).hasPermanentMenuKey());
        }
        result.put("VIRTUAL_RESOLUTION", getVirtualResolution(c));
        String s = RemoteLog.getGson().toJson(result);
        return s;
    }

    private Map<? extends String, ?> getCameraInfo(Context c) {
        HashMap<String, Object> result = new HashMap<String, Object>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            int cameras = Camera.getNumberOfCameras();
            result.put("CAMERA_COUNT", cameras);
            Camera.CameraInfo ci = new Camera.CameraInfo();
            boolean hasCameraPerm = c.checkCallingOrSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
            SharedPreferences sp = hasCameraPerm ? c.getSharedPreferences("RemoteLog_Cameras", Context.MODE_PRIVATE) : null;

            for (int i = 0; i < cameras; i++) {
                Camera.getCameraInfo(i, ci);
                result.put(String.format("CAMERA_%s_FACING", i), ci.facing);
                result.put(String.format("CAMERA_%s_ORIENTATION", i), ci.orientation);
                if (hasCameraPerm) {
                    String key = String.valueOf(i);
                    String value = sp.getString(key, null);
                    try {
                        if (value == null) {
                            Camera cam = Camera.open(i);
                            value = cam.getParameters().flatten();
                            sp.edit().putString(key, value).commit();
                            result.put(String.format("CAMERA_%s_PARAMS", i), value);
                            cam.release();
                        } else {
                            result.put(String.format("CAMERA_%s_PARAMS", i), value);
                        }
                    } catch (Throwable e) {
                        sp.edit().putString(key, e.getMessage()).commit();
                        e.printStackTrace();
                    }
                }
            }
        }
        return result;
    }

    /**
     * Returns Hardware features
     *
     * @param c
     * @return
     */
    protected HashMap<String, Object> getHardwareFeatures(Context c) {
        HashMap<String, Object> result = new HashMap<String, Object>();
        PackageManager pm = c.getPackageManager();
        Field[] fields = PackageManager.class.getDeclaredFields();
        for (Field f : fields) {
            String name = f.getName();

            if (name.startsWith("FEATURE")) {
                Object o = null;
                try {
                    f.setAccessible(true);
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
    protected HashMap<String, Object> getRuntimeInfo(Context c) {
        HashMap<String, Object> result = new HashMap<String, Object>();
        ActivityManager am = (ActivityManager) c.getSystemService(Context.ACTIVITY_SERVICE);
        Runtime r = Runtime.getRuntime();
        result.put("MEMORY_MAX", r.maxMemory());
        result.put("MEMORY_CLASS_NORMAL", am.getMemoryClass());
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            result.put("MEMORY_CLASS_LARGE", am.getLargeMemoryClass());
        }
        result.put("CPU_AVAILABLE", r.availableProcessors());
        result.put("SUPERUSER_STATUS", RootCheck.getDeviceRoot());
        return result;
    }

    /**
     * Needs
     * <code><uses-permission android:name="android.permission.GET_ACCOUNTS" /></code>
     * in manifest!
     *
     * @param c
     * @return primary mail if possible, otherwise null
     */
    protected String getOwner(Context c) {
        String result = null;
        try {
            if (c.checkCallingOrSelfPermission(Manifest.permission.GET_ACCOUNTS) == PackageManager.PERMISSION_GRANTED) {
                Pattern emailPattern = EMAIL_ADDRESS; // API level 8+
                Account[] accounts = AccountManager.get(c).getAccounts();
                for (Account account : accounts) {
                    if (emailPattern.matcher(account.name).matches()) {
                        String possibleEmail = account.name;
                        result = possibleEmail;
                        break;
                    }
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return result;
    }

    protected HashMap<String, Object> getTelephonyInfo(Context c) {

        HashMap<String, Object> result = new HashMap<String, Object>();
        try {
            if (c.checkCallingOrSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                TelephonyManager tm = (TelephonyManager) c
                        .getSystemService(Context.TELEPHONY_SERVICE);
                result.put("TEL_IMEI/MEID", tm.getDeviceId());
                result.put("TEL_IMEI/SV", tm.getDeviceSoftwareVersion());
                result.put("TEL_LINE1NUMBER", tm.getLine1Number());
                result.put("TEL_COUNTRY", tm.getNetworkCountryIso());
                result.put("SIM_OPERATOR", tm.getSimOperator());
                result.put("SIM_OPERATOR_NAME", tm.getSimOperatorName());
                result.put("SIM_SERNUM", tm.getSimSerialNumber());
                result.put("SIM_IMSI", tm.getSubscriberId());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
