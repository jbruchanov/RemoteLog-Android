package com.scurab.android.settings;

import com.google.gson.annotations.SerializedName;

/**
 * User: Joe Scurab
 * Date: 09/04/13
 * Time: 21:09
 */
public class Update {

    public static final String TYPE_TOAST = "TOAST";
    public static final String TYPE_DIALOG = "DIALOG";

    @SerializedName("Build")
    public String build;

    @SerializedName("Type")
    public String type;

    @SerializedName("Message")
    public String message;
}
