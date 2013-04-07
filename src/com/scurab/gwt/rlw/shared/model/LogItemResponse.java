package com.scurab.gwt.rlw.shared.model;

public class LogItemResponse extends Response<LogItem> {

    public LogItemResponse() {
        super();
    }

    public LogItemResponse(LogItem context) {
        super(context);
    }

    public LogItemResponse(String msg, LogItem context) {
        super(msg, context);
    }

    public LogItemResponse(String msg) {
        super(msg);
    }

    public LogItemResponse(Throwable t) {
        super(t);
    }

}
