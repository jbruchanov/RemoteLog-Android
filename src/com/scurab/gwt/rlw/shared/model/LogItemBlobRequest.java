package com.scurab.gwt.rlw.shared.model;

import com.google.gson.annotations.SerializedName;

public class LogItemBlobRequest {

    public static final String MIME_TEXT_PLAIN = "text/plain";
    public static final String MIME_TEXT_JSON = "text/json";
    public static final String MIME_IMAGE_JPEG = "image/jpeg";
    public static final String MIME_IMAGE_PNG = "image/png";

    @SerializedName("LogItemID")
    private int mLogItemID;

    @SerializedName("MimeType")
    private String mMimeType;

    @SerializedName("DataLength")
    private int mDataLength;

    @SerializedName("FileName")
    private String mFileName;

    //just container, dont serialize it by gson
    private final transient byte[] mData;

    public LogItemBlobRequest() {
        mData = null;
    }

    private transient boolean mIsUncaughtError = false;

    public LogItemBlobRequest(String mime, String fileName, byte[] data) {
        mMimeType = mime;
        mDataLength = data.length;
        mFileName = fileName;
        mData = data;
    }

    public int getLogItemID() {
        return mLogItemID;
    }

    public void setLogItemID(int logItemID) {
        mLogItemID = logItemID;
    }

    public String getMimeType() {
        return mMimeType;
    }

    public void setMimeType(String mimeType) {
        mMimeType = mimeType;
    }

    public int getDataLength() {
        return mDataLength;
    }

    public void setDataLength(int dataLength) {
        mDataLength = dataLength;
    }

    public String getFileName() {
        return mFileName;
    }

    public void setFileName(String fileName) {
        mFileName = fileName;
    }

    public byte[] getData() {
        return mData;
    }

    public boolean isUncaughtError() {
        return mIsUncaughtError;
    }

    public void setIsUncaughtError(boolean isUncaughtError) {
        mIsUncaughtError = isUncaughtError;
    }
}