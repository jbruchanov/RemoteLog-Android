package com.scurab.gwt.rlw.shared.model;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public class Device implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -5526599861310748107L;

    @SerializedName("DeviceID")
    private int mDeviceID;

    @SerializedName("DevUUID")
    private String mDevUUID;

    @SerializedName("Brand")
    private String mBrand;

    @SerializedName("Platform")
    private String mPlatform;

    @SerializedName("Version")
    private String mVersion;

    @SerializedName("Detail")
    private String mDetail;

    @SerializedName("Resolution")
    private String mResolution;

    @SerializedName("Owner")
    private String mOwner;

    @SerializedName("OSDescription")
    private String mOSDescription;

    @SerializedName("Description")
    private String mDescription;

    @SerializedName("PushID")
    private String mPushID;

    @SerializedName("Model")
    private String mModel;
    
    @SerializedName("App")
    private String mApp;

    @SerializedName("AppVersion")
    private String mAppVersion;

    public int getDeviceID() {
        return mDeviceID;
    }

    public void setDeviceID(int deviceID) {
        mDeviceID = deviceID;
    }

    public String getDevUUID() {
        return mDevUUID;
    }

    public void setDevUUID(String devUUID) {
        mDevUUID = devUUID;
    }

    public String getBrand() {
        return mBrand;
    }

    public void setBrand(String brand) {
        mBrand = brand;
    }

    public String getPlatform() {
        return mPlatform;
    }

    public void setPlatform(String platform) {
        mPlatform = platform;
    }

    public String getVersion() {
        return mVersion;
    }

    public void setVersion(String version) {
        mVersion = version;
    }

    public String getDetail() {
        return mDetail;
    }

    public void setDetail(String detail) {
        mDetail = detail;
    }

    public String getResolution() {
        return mResolution;
    }

    public void setResolution(String resolution) {
        mResolution = resolution;
    }

    public String getOwner() {
        return mOwner;
    }

    public void setOwner(String owner) {
        mOwner = owner;
    }

    public String getOSDescription() {
        return mOSDescription;
    }

    public void setOSDescription(String oSDescription) {
        mOSDescription = oSDescription;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public String getPushID() {
        return mPushID;
    }

    public void setPushID(String pushID) {
        mPushID = pushID;
    }

    public String getModel() {
        return mModel;
    }

    public void setModel(String model) {
        mModel = model;
    }

    public String getApp() {
	return mApp;
    }

    public void setApp(String app) {
	mApp = app;
    }

    public String getAppVersion() {
        return mAppVersion;
    }

    public void setAppVersion(String appVersion) {
        mAppVersion = appVersion;
    }

}
