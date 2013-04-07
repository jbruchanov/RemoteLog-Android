package com.scurab.android.rlw;


public interface ILog {

    public void i(Object source, String category, String msg);

    public void v(Object source, String category, String msg);

    public void d(Object source, String category, String msg);

    public void e(Object source, String category, String msg);

    public void e(Object source, String category, Throwable t);

    public void n(Object source, String category, String msg);

    public void w(Object source, String category, String msg);

    public void wtf(Object source, String category, String msg);

}
