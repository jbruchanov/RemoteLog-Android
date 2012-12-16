package com.scurab.android.rlw;


public interface ILog {

    public void v(String tag, String msg);
    public void v(String tag, String msg, Throwable tr);
    
    public void d(String tag, String msg);
    public void d(String tag, String msg, Throwable tr);
    
    public void i(String tag, String msg);
    public void i(String tag, String msg, Throwable tr);
    
    public void w(String tag, String msg);
    public void w(String tag, String msg, Throwable tr);
    public void w(String tag, Throwable tr);
    
    public void e(String tag, String msg);
    public void e(String tag, String msg, Throwable tr);
    
    public void wtf(String tag, String msg);
    public void wtf(String tag, Throwable tr);
    public void wtf(String tag, String msg, Throwable tr);

//    public String getStackTraceString(Throwable tr) {
//        if (tr == null) {
//            return "";
//        }
//
//        // This is to reduce the amount of log spew that apps do in the non-error
//        // condition of the network being unavailable.
//        Throwable t = tr;
//        while (t != null) {
//            if (t instanceof UnknownHostException) {
//                return "";
//            }
//            t = t.getCause();
//        }
//
//        StringWriter sw = new StringWriter();
//        PrintWriter pw = new PrintWriter(sw);
//        tr.printStackTrace(pw);
//        return sw.toString();
//    }
}
