package com.scurab.gwt.rlw.server.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import com.google.gson.Gson;
import com.scurab.gwt.rlw.shared.model.Device;
import com.scurab.gwt.rlw.shared.model.LogItem;

public class DataGenerator {

    private static final Random sRandom = new Random();
    private static final String[] sBrands = new String[] { "Acer", "Benq", "HTC", "Huawei", "LG", "Microsoft", "Nokia",
            "Samsung", "ZTE" };
    private static final String[] sPlatforms = new String[] { "Android", "Windows Phone", "iOS", "BlackBerry" };
    private static final String[] sResolutions = new String[] { "480x800", "320x480", "240x480", "800x1280",
            "1080x1920" };

    private static final String[] sCategoryLog = new String[] { "Info", "Warning", "Error", "Debug" };

    private static final String[][] sRandomWords = new String[][] { sBrands, sPlatforms, sResolutions };
    private static final Gson sGson = new Gson();
    private static int sId;

    public static List<Device> genDevices(int howMany) {
        ArrayList<Device> result = new ArrayList<Device>();
        for (int i = 0; i < howMany; i++) {
            result.add(genRandomDevice());
        }
        return result;
    }

    public static Device genRandomDevice() {
        Device d = new Device();
        d.setBrand(getRandomBrand());
        d.setDescription("Random desc:" + genRandomString());
        d.setDetail(genRandomJSON(5));
        d.setPlatform(getRandomPlatform());
        d.setResolution(getRandomResolution());
        d.setVersion(String.valueOf(1 + sRandom.nextInt(6)));
        d.setDevUUID("" + sRandom.nextLong());
        d.setModel(genRandomString(3, 5).toUpperCase() + " " + (100 + sRandom.nextInt(899)));
        return d;
    }

    public static String getRandomBrand() {
        return sBrands[sRandom.nextInt(sBrands.length)];
    }

    public static String getRandomPlatform() {
        return sPlatforms[sRandom.nextInt(sPlatforms.length)];
    }

    public static String getRandomResolution() {
        return sResolutions[sRandom.nextInt(sResolutions.length)];
    }

    public static String genRandomString() {
        return genRandomString(5, 15);
    }

    public static String genRandomJSON(int fields) {
        HashMap<String, Object> v = new HashMap<String, Object>();
        for (int i = 0; i < fields; i++) {
            if (i > 0 && i % 3 == 0) {
                v.put("Field" + i, sRandom.nextInt(65536));
            } else {
                if (sRandom.nextBoolean()) {
                    String[] words = sRandomWords[sRandom.nextInt(sRandomWords.length)];
                    v.put("Field" + i, words[sRandom.nextInt(words.length)]);
                } else {
                    v.put("Field" + i, genRandomString());
                }
            }
        }
        return sGson.toJson(v);
    }

    public static String genRandomString(int minlen, int maxlen) {
        int diff = sRandom.nextInt(maxlen - minlen - 1);
        StringBuilder sb = new StringBuilder(maxlen);
        for (int i = 0, n = minlen + diff; i < n; i++) {
            sb.append((char) ('a' + sRandom.nextInt('z' - 'a') + 1));
        }
        return sb.toString();
    }

    public static LogItem genRandomLogItem() {
        return genRandomLogItem(sRandom.nextInt(), genRandomString());
    }

    public static List<LogItem> genRandomLogItems(int howMany) {
        List<LogItem> items = new ArrayList<LogItem>();
        for (int i = 0; i < howMany; i++) {
            items.add(genRandomLogItem());
        }
        return items;
    }

    public static List<LogItem> genRandomLogItems(int devId, String app, int howMany) {
        List<LogItem> items = new ArrayList<LogItem>();
        for (int i = 0; i < howMany; i++) {
            items.add(genRandomLogItem(devId, app));
        }
        return items;
    }

    public static LogItem genRandomLogItem(int devId, String app) {
        LogItem li = new LogItem();
        li.setAppBuild("" + sRandom.nextInt(256));
        li.setApplication(app);
        li.setAppVersion("1." + sRandom.nextInt());
        li.setCategory(sCategoryLog[sRandom.nextInt(sCategoryLog.length)]);
        li.setDate(new Date());
        li.setMessage(getRandomBrand() + " " + genRandomString());
        li.setDeviceID(devId);
        return li;
    }
}
