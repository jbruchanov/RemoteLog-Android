package com.scurab.gwt.rlw.shared.model;

public class LogItemRespond extends Respond<LogItem> {

    public LogItemRespond() {
        super();
    }

    public LogItemRespond(LogItem context) {
        super(context);
    }

    public LogItemRespond(String msg, LogItem context) {
        super(msg, context);
    }

    public LogItemRespond(String msg) {
        super(msg);
    }

    public LogItemRespond(Throwable t) {
        super(t);
    }

}
