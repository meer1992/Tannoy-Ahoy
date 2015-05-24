package com.uow.tannoyahoy;

import android.content.Context;
import android.util.Log;
import android.util.LruCache;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.Volley;


import java.io.InputStream;
import java.security.KeyStore;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

/**
 * Created by Hat on 20/05/2015.
 *
 * Singleton which handles HTTP SSL requests for the lifetime of the application
 */
public class TannoyRequestQueueSingleton {
    private static TannoyRequestQueueSingleton mInstance;
    private RequestQueue theRequestQueue;
    private static Context thisContext;

    private static char[] KEYSTORE_PASSWORD = "1234".toCharArray();

    private TannoyRequestQueueSingleton(Context theContext) {
        thisContext = theContext;
        theRequestQueue = getRequestQueue();
    }

    public static synchronized TannoyRequestQueueSingleton getInstance(Context theContext) {
        if (mInstance == null) {
            mInstance = new TannoyRequestQueueSingleton(theContext);
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (theRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            theRequestQueue = Volley.newRequestQueue(thisContext.getApplicationContext(), new HurlStack(null, newSslSocketFactory()));
        }
        return theRequestQueue;
    }

    /**
     * Adds the request onto the queue
     * @param req the request to add
     */
    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

    /**Tries to handle the SSL key
     * @return an SSLSocketFactory to be used in a request queue
     */
    private SSLSocketFactory newSslSocketFactory() {
        try {
            // Get an instance of the Bouncy Castle KeyStore format
            KeyStore trustedKey = KeyStore.getInstance("BKS");
            // Get the raw resource, which contains the keystore with
            // your trusted certificates (root and any intermediate certs)
            InputStream in = thisContext.getApplicationContext().getResources().openRawResource(R.raw.tannoyserver);
            try {
                // Initialize the keystore with the provided trusted certificates
                // Provide the password of the keystore
                trustedKey.load(in, KEYSTORE_PASSWORD);
            } finally {
                in.close();
            }

            //handle the trustmanager with the trustedKey
            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(trustedKey);

            SSLContext theSSLContext = SSLContext.getInstance("TLS");
            theSSLContext.init(null, tmf.getTrustManagers(), null);

            SSLSocketFactory theSSLSocketFactory = theSSLContext.getSocketFactory();
            return theSSLSocketFactory;
        } catch (Exception e) {
            throw new AssertionError(e);
        }
    }
}

