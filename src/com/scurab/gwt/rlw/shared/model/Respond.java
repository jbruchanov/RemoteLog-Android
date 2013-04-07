package com.scurab.gwt.rlw.shared.model;

import com.google.gson.annotations.SerializedName;

public abstract class Respond<T> {

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

    public Respond() {
        mMessage = "OK";
    }

    public Respond(T context) {
        mMessage = "OK";
        mContext = context;
    }

    public Respond(String msg) {
        mMessage = msg;
    }

    public Respond(String msg, T context) {
        mMessage = msg;
        mContext = context;
    }

    public Respond(Throwable t) {
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
