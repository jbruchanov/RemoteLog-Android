package com.scurab.android.rlw;

import javax.net.ssl.X509TrustManager;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * Simple Trust manager which accepts all SSL certs<br/>
 * For few another and <b>better</b> solutions check {@link http://code.google.com/p/misc-utils/wiki/JavaHttpsUrl }
 *
 * @author Joe Scurab
 */
public class FakeTrustManager implements X509TrustManager {

    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType)
            throws CertificateException {

    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType)
            throws CertificateException {

    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return null;
    }

}