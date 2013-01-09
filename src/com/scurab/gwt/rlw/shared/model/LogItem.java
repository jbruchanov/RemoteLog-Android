package com.scurab.gwt.rlw.shared.model;

import java.io.Serializable;
import java.util.Date;

import com.google.gson.annotations.SerializedName;

public class LogItem implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 7649642015381756913L;

    @SerializedName("ID")
    private int mID;

    @SerializedName("Application")
    private String mApplication;

    @SerializedName("AppVersion")
    private String mAppVersion;

    @SerializedName("AppBuild")
    private String mAppBuild;

    @SerializedName("Date")
    private Date mDate;

    @SerializedName("Category")
    private String mCategory;
    
    @SerializedName("Source")
    private String mSource;

    @SerializedName("Message")
    private String mMessage;

    @SerializedName("BlobMime")
    private String mBlobMime;

    @SerializedName("DeviceID")
    private int mDeviceID;

    public int getID() {
        return mID;
    }

    public void setID(int iD) {
        mID = iD;
    }

    public String getApplication() {
        return mApplication;
    }

    public void setApplication(String application) {
        mApplication = application;
    }

    public String getAppVersion() {
        return mAppVersion;
    }

    public void setAppVersion(String appVersion) {
        mAppVersion = appVersion;
    }

    public String getAppBuild() {
        return mAppBuild;
    }

    public void setAppBuild(String appBuild) {
        mAppBuild = appBuild;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public String getCategory() {
        return mCategory;
    }

    public void setCategory(String category) {
        mCategory = category;
    }

    public String getMessage() {
        return mMessage;
    }

    public void setMessage(String message) {
        mMessage = message;
    }

    public String getBlobMime() {
        return mBlobMime;
    }

    public void setBlobMime(String blobMime) {
        mBlobMime = blobMime;
    }

    public int getDeviceID() {
        return mDeviceID;
    }

    public void setDeviceID(int deviceID) {
        mDeviceID = deviceID;
    }

    public String getSource() {
	return mSource;
    }

    public void setSource(String source) {
	mSource = source;
    }

}