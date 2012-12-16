package com.scurab.gwt.rlw.shared.model;

import java.io.Serializable;

public class LogItemBlob implements Serializable {

    private static final long serialVersionUID = 897695906066127214L;

    private int mId;

    private byte[] mData;

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public byte[] getData() {
        return mData;
    }

    public void setData(byte[] data) {
        mData = data;
    }
}
