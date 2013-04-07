package com.scurab.gwt.rlw.shared.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Settings implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1680757323178371221L;

    @SerializedName("SettingsID")
    private int mSettingsID;

    @SerializedName("AppName")
    private String mAppName;

    @SerializedName("DeviceID")
    private Integer mDeviceID;

    @SerializedName("JsonValue")
    private String mJsonValue;

    public int getSettingsID() {
        return mSettingsID;
    }

    public void setSettingsID(int settingsID) {
        mSettingsID = settingsID;
    }

    public String getAppName() {
        return mAppName;
    }

    public void setAppName(String appName) {
        mAppName = appName;
    }

    public Integer getDeviceID() {
        return mDeviceID;
    }

    public void setDeviceID(Integer deviceID) {
        mDeviceID = deviceID;
    }

    public String getJsonValue() {
        return mJsonValue;
    }

    public void setJsonValue(String jsonValue) {
        mJsonValue = jsonValue;
    }
}
