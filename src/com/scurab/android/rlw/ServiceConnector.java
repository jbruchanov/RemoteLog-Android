package com.scurab.android.rlw;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.util.Base64;

import com.google.gson.Gson;
import com.scurab.gwt.rlw.shared.model.Device;
import com.scurab.gwt.rlw.shared.model.DeviceResponse;
import com.scurab.gwt.rlw.shared.model.LogItem;
import com.scurab.gwt.rlw.shared.model.LogItemBlobRequest;
import com.scurab.gwt.rlw.shared.model.LogItemBlobRespond;
import com.scurab.gwt.rlw.shared.model.LogItemRespond;
import com.scurab.gwt.rlw.shared.model.SettingsRespond;

class ServiceConnector {

    private static final String REGS_URL = "/regs";
    private static final String LOGS_URL = "/logs";
    private static final String SETTINGS_TEMPLATE_URL = "/settings/%s/%s";

    private static final String HTTP_GET = "GET";
    private static final String HTTP_POST = "POST";
    private static final String HTTP_PUT = "PUT";

    private static final int TIMEOUT = 2000;

    private String mUrl;
    private final Gson mGson;
    private final SSLSocketFactory mSSLFactory;
    
    private final String mLoginBase64;    

    private final HostnameVerifier mHostnameVerifier = new HostnameVerifier() {
	@Override
	public boolean verify(String hostname, SSLSession session) {
	    return true;
	}
    };

    public ServiceConnector(String url, TrustManager manager, String username, String password)
	    throws MalformedURLException, KeyManagementException,
	    NoSuchAlgorithmException {
	if (url.endsWith("/")) {
	    url = url.substring(0, url.length() - 1);
	}
	mGson = RemoteLog.getGson();
	if (mGson == null) {
	    throw new IllegalStateException("RemoteLog.getGson() returns null!");
	}
	mUrl = url;

	// just check if url is correct
	new URL(url);

	mSSLFactory = manager != null ? initSSLSocketFactory(manager) : null;

	if(username != null && password != null){
	    mLoginBase64 = Base64.encodeToString(String.format("%s:%s", username, password).getBytes(), Base64.DEFAULT);
	}else{
	    mLoginBase64 = null;
	}
    }

    private SSLSocketFactory initSSLSocketFactory(TrustManager manager)
	    throws KeyManagementException, NoSuchAlgorithmException {
	// Install the all-trusting trust manager
	final SSLContext sslContext = SSLContext.getInstance("SSL");
	sslContext.init(null, new TrustManager[] { manager },
		new java.security.SecureRandom());
	// Create an ssl socket factory with our all-trusting manager
	return sslContext.getSocketFactory();
    }

    /**
     * Save device on server
     * 
     * @param d
     * @return
     * @throws IOException
     */
    public DeviceResponse saveDevice(Device... d) throws IOException {
	String url = mUrl + REGS_URL;
	String json = mGson.toJson(d);
	// write request
	String respond = sendRequest(json, url, HTTP_POST);
	// parse response
	DeviceResponse dr = mGson.fromJson(respond, DeviceResponse.class);
	return dr;
    }

    /**
     * Save LogItem on server
     * 
     * @param d
     * @return
     * @throws IOException
     */
    public LogItemRespond saveLogItem(LogItem... d) throws IOException {
	String url = mUrl + LOGS_URL;
	String json = mGson.toJson(d);
	// write request
	String respond = sendRequest(json, url, HTTP_POST);
	// parse response
	LogItemRespond dr = mGson.fromJson(respond, LogItemRespond.class);
	return dr;
    }

    /**
     * Save LogItemBloblRequest on server
     * 
     * @param req
     * @param data
     * @return
     * @throws IOException
     */
    public LogItemBlobRespond saveLogItemBlob(LogItemBlobRequest req,
	    byte[] data) throws IOException {
	String json = mGson.toJson(req);
	String url = String.format("%s%s?%s", mUrl, LOGS_URL,
		URLEncoder.encode(json, "UTF-8"));
	// write request
	String respond = sendRequest(data, url, HTTP_PUT);
	// parse response
	LogItemBlobRespond dr = mGson.fromJson(respond,
		LogItemBlobRespond.class);
	return dr;
    }

    /**
     * Send JSON data
     * 
     * @param data
     * @param url
     * @param method
     * @return
     * @throws IOException
     */
    protected String sendRequest(String data, String url, String method)
	    throws IOException {
	HttpURLConnection hc = openConnection(url, method);
	// write request
	hc.getOutputStream().write(data.getBytes());
	hc.getOutputStream().flush();
	// read response
	String respond = read(hc.getInputStream());
	hc.disconnect();
	return respond;
    }

    /**
     * Send binary data
     * 
     * @param data
     * @param url
     * @param method
     * @return
     * @throws IOException
     */
    protected String sendRequest(byte[] data, String url, String method)
	    throws IOException {
	HttpURLConnection hc = openConnection(url, method);
	// write request
	hc.getOutputStream().write(data);
	// read response
	String respond = read(hc.getInputStream());
	hc.disconnect();
	return respond;
    }

    protected HttpURLConnection openConnection(String url, String httpMethod)
	    throws IOException {
	URL u = new URL(url);
	URLConnection connection = u.openConnection();
	HttpURLConnection hc = (HttpURLConnection) connection;
	if(mSSLFactory != null && hc instanceof HttpsURLConnection){
	    HttpsURLConnection huc = (HttpsURLConnection)hc;
	    huc.setHostnameVerifier(mHostnameVerifier);
	    huc.setSSLSocketFactory(mSSLFactory);
	}
	
	if(mLoginBase64 != null){
	    hc.setRequestProperty("Authorization", "Basic " + mLoginBase64);
	}

	// set timeout
	HttpParams httpParameters = new BasicHttpParams();
	HttpConnectionParams.setConnectionTimeout(httpParameters, TIMEOUT);
	HttpConnectionParams.setSoTimeout(httpParameters, TIMEOUT);

	hc.setRequestMethod(httpMethod);
	hc.setDoInput(true);
	if(!HTTP_GET.equals(httpMethod)){
	    hc.setDoOutput(true);
	}
	hc.setUseCaches(false);
	hc.setDefaultUseCaches(false);
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

    public SettingsRespond loadSettings(int deviceId, String appName)
	    throws IOException {
	HttpURLConnection hc = openConnection(
		mUrl + String.format(SETTINGS_TEMPLATE_URL, deviceId, appName),
		HTTP_GET);
	// read response
	String respond = read(hc.getInputStream());
	SettingsRespond result = mGson.fromJson(respond, SettingsRespond.class);
	hc.disconnect();
	return result;
    }

    public void updatePushToken(int deviceId, String pushToken)
	    throws IOException {
	HttpURLConnection hc = openConnection(mUrl + REGS_URL + "/" + deviceId,
		HTTP_PUT);
	// write request
	hc.getOutputStream().write(pushToken.getBytes());
	hc.getOutputStream().flush();
	// read response
	@SuppressWarnings("unused")
	final String respond = read(hc.getInputStream());
	hc.disconnect();
    }
}
