package com.scurab.gwt.rlw.shared.model;

public class LogItemBlobResponse extends Response<LogItemBlobRequest> {

    public LogItemBlobResponse() {
        super();
    }

    public LogItemBlobResponse(String msg, LogItemBlobRequest context) {
        super(msg, context);
    }

    public LogItemBlobResponse(Throwable t) {
        super(t);
    }

    public LogItemBlobResponse(LogItemBlobRequest context, int written) {
        super(context);
        setCount(written);
    }
}
