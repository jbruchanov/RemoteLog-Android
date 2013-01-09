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
        return String.format("Name:%s, TS:%s, Context:%s, Params:%s", mName, mTimeStamp, mContext, mParams);
    }

    public String getContext() {
	return mContext;
    }

    public void setContext(String context) {
	mContext = context;
    }
}
