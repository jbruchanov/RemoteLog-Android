package com.scurab.android.rlw;

import com.scurab.gwt.rlw.shared.model.LogItem;
import com.scurab.gwt.rlw.shared.model.LogItemBlobRequest;

public class RLWLog{    
    
    public static void v(Object source, String msg) {	
	send(source, "Verbose", msg);
    }
    
    public static void d(Object source, String msg) {	
	send(source, "Debug", msg);
    }
    
    public static void e(Object source, String msg) {	
	send(source, "Error", msg);
    }
    
    public static void wtf(Object source, String msg) {
	send(source, "Wtf", msg);
    }
    
    public static void send(Object source, String category, String msg){
	send(source, category, msg, null);
    }
    
    public static void send(Object source, String category, String msg, LogItemBlobRequest libr){
	LogItem li = RemoteLog.createLogItem();
	li.setCategory(category);
	li.setMessage(msg);
	try {
	    RemoteLog.getLogSender().addLogItem(li, libr);
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    
}
