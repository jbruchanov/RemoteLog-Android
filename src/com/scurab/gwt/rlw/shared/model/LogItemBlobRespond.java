package com.scurab.gwt.rlw.shared.model;

public class LogItemBlobRespond extends Respond<LogItemBlobRequest> {

    public LogItemBlobRespond() {
        super();
    }

    public LogItemBlobRespond(String msg, LogItemBlobRequest context) {
        super(msg, context);
    }

    public LogItemBlobRespond(Throwable t) {
        super(t);
    }

    public LogItemBlobRespond(LogItemBlobRequest context, int written) {
        super(context);
        setCount(written);
    }
}
