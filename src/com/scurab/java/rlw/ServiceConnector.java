package com.scurab.java.rlw;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import com.scurab.gwt.rlw.shared.model.Device;
import com.scurab.gwt.rlw.shared.model.DeviceRespond;
import com.scurab.gwt.rlw.shared.model.LogItem;
import com.scurab.gwt.rlw.shared.model.LogItemBlobRequest;
import com.scurab.gwt.rlw.shared.model.LogItemBlobRespond;
import com.scurab.gwt.rlw.shared.model.LogItemRespond;

public class ServiceConnector {
    private String mUrl;
    private static final String REGS_URL = "/regs";
    private static final String LOGS_URL = "/logs";

    private static final String HTTP_GET = "GET";
    private static final String HTTP_POST = "POST";
    private static final String HTTP_PUT = "PUT";

    public ServiceConnector(String url) throws MalformedURLException {
        if (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }
        mUrl = url;
        //just check if url is correct
        new URL(url);
    }

    public DeviceRespond saveDevice(Device... d) throws IOException {
        String url = mUrl + REGS_URL;
        String json = Core.GSON.toJson(d);
        // write request
        String respond = sendRequest(json, url, HTTP_POST);
        // parse response
        DeviceRespond dr = Core.GSON.fromJson(respond, DeviceRespond.class);
        return dr;
    }
    
    public LogItemRespond saveLogItem(LogItem... d) throws IOException {
        String url = mUrl + LOGS_URL;
        String json = Core.GSON.toJson(d);
        // write request
        String respond = sendRequest(json, url, HTTP_POST);
        // parse response
        LogItemRespond dr = Core.GSON.fromJson(respond, LogItemRespond.class);
        return dr;
    }
    
    public LogItemBlobRespond saveLogItemBlob(LogItemBlobRequest req, byte[] data) throws IOException {
        String json = Core.GSON.toJson(req);
        String url = String.format("%s%s?%s",mUrl,LOGS_URL, URLEncoder.encode(json, "UTF-8"));
        // write request
        String respond = sendRequest(data, url, HTTP_PUT);
        // parse response
        LogItemBlobRespond dr = Core.GSON.fromJson(respond, LogItemBlobRespond.class);
        return dr;
    }

    /**
     * Send JSON data
     * @param data
     * @param url
     * @param method
     * @return
     * @throws IOException
     */
    protected String sendRequest(String data, String url, String method) throws IOException {
        HttpURLConnection hc = openConnection(url, method);
        // write request
        hc.getOutputStream().write(data.getBytes());
        hc.getOutputStream().flush();
        // read response
        String respond = read(hc.getInputStream());
        return respond;
    }
    
    /**
     * Send binary data
     * @param data
     * @param url
     * @param method
     * @return
     * @throws IOException
     */
    protected String sendRequest(byte[] data, String url, String method) throws IOException {
        HttpURLConnection hc = openConnection(url, method);
        // write request
        hc.getOutputStream().write(data);
        // read response
        String respond = read(hc.getInputStream());
        return respond;
    }

    protected HttpURLConnection openConnection(String url, String httpMethod) throws IOException {
        URL u = new URL(url);
        URLConnection connection = u.openConnection();
        HttpURLConnection hc = (HttpURLConnection) connection;
        hc.setDoInput(true);
        hc.setDoOutput(true);
        hc.setDefaultUseCaches(false);
        hc.setRequestMethod(httpMethod);
        return hc;
    }

    protected String read(InputStream is) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(is));

        StringBuilder sb = new StringBuilder();

        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }

        return sb.toString();
    }
}
