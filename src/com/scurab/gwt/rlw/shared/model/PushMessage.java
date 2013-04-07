package com.scurab.gwt.rlw.shared.model;

import java.io.Serializable;

public class PushMessage implements Serializable {


    /**
     *
     */
    private static final long serialVersionUID = -524140627737451205L;

    private String mTimeStamp;
    private String mParams;
    private String mName;
    private String mContext;

    public PushMessage() {
    }

    public String getTimeStamp() {
        return mTimeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        mTimeStamp = timeStamp;
    }

    public String getParams() {
        return mParams;
    }

    public void setParams(String params) {
        mParams = params;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    @Override
    public String toString() {
        if (mContext != null && mContext.trim().length() > 0) {
            return String.format("Name:%s, Context:%s, Params:%s", mName, mContext, mParams);
        } else {
            return String.format("Name:%s, Params:%s", mName, mParams);
        }
    }

    public String getMessageContext() {
        return mContext;
    }

    public void setMessageContext(String context) {
        mContext = context;
    }
}
