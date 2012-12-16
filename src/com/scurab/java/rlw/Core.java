package com.scurab.java.rlw;

import java.text.SimpleDateFormat;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Core {
    public static final SimpleDateFormat DATEFORMAT = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss.SSS");
    public static final Gson GSON = new GsonBuilder().setDateFormat("yyyy-MM-dd kk:mm:ss.SSS").create();
}
