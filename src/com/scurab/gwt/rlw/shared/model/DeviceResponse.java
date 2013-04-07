package com.scurab.gwt.rlw.shared.model;

public class DeviceResponse extends Response<Device> {

    public DeviceResponse() {
        super();
    }

    public DeviceResponse(Device context) {
        super(context);
    }

    public DeviceResponse(String msg, Device context) {
        super(msg, context);
    }

    public DeviceResponse(String msg) {
        super(msg);
    }

    public DeviceResponse(Throwable t) {
        super(t);
    }

}
