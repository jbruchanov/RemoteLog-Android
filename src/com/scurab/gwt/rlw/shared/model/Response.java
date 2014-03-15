package com.scurab.gwt.rlw.shared.model;

import com.google.gson.annotations.SerializedName;

public abstract class Response<T> {

    public static final String OK = "OK";
    @SerializedName("Type")
    private String mType;

    @SerializedName("Message")
    private String mMessage;

    @SerializedName("HasError")
    private boolean hasError;

    @SerializedName("Context")
    private T mContext;

    @SerializedName("Count")
    private int mCount;

    public Response() {
        mMessage = OK;
    }

    public Response(T context) {
        mMessage = OK;
        mContext = context;
    }

    public Response(String msg) {
        mMessage = msg;
    }

    public Response(String msg, T context) {
        mMessage = msg;
        mContext = context;
    }

    public Response(Throwable t) {
        mMessage = t.getMessage();
        mType = t.getClass().getName();
        hasError = true;
    }

    public String getType() {
        return mType;
    }

    public void setType(String type) {
        mType = type;
    }

    public String getMessage() {
        return mMessage;
    }

    public void setMessage(String message) {
        mMessage = message;
    }

    public boolean hasError() {
        return hasError;
    }

    public void setHasError(boolean hasError) {
        this.hasError = hasError;
    }

    public T getContext() {
        return mContext;
    }

    public void setContext(T context) {
        mContext = context;
    }

    public int getCount() {
        return mCount;
    }

    public void setCount(int count) {
        mCount = count;
    }
}
