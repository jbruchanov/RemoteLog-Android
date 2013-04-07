package com.scurab.android;

/**
 * Exception used only in case if KillApp message is received
 *
 * @author Jiri Bruchanov
 */
public class KillAppException extends Error {
    /**
     *
     */
    private static final long serialVersionUID = 483697561537019374L;

    public KillAppException() {
        super("RemoteLog KillApp");
    }
}
