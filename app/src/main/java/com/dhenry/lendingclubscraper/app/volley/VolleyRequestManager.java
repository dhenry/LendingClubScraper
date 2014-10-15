package com.dhenry.lendingclubscraper.app.volley;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HttpClientStack;

import org.apache.http.impl.client.DefaultHttpClient;

import java.io.File;

public class VolleyRequestManager {

    private static final int SINGLE_THREAD_THREAD_POOL_SIZE = 1;
    private static final String DEFAULT_CACHE_DIR = "volley";
    private static VolleyRequestManager mInstance;
    private RequestQueue mRequestQueue;
    private static Context mCtx;

    private VolleyRequestManager(Context context) {
        mCtx = context;
        mRequestQueue = getRequestQueue();
    }

    public static synchronized VolleyRequestManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new VolleyRequestManager(context);
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {

            String userAgent = "volley/0";
            try {
                String packageName = mCtx.getPackageName();
                PackageInfo info = mCtx.getPackageManager().getPackageInfo(packageName, 0);
                userAgent = packageName + "/" + info.versionCode;
            } catch (PackageManager.NameNotFoundException e) {
            }

            Network network = new BasicNetwork(new HttpClientStack(new DefaultHttpClient()));

            File cacheDir = new File(mCtx.getCacheDir(), DEFAULT_CACHE_DIR);

            mRequestQueue = new RequestQueue(new DiskBasedCache(cacheDir), network, SINGLE_THREAD_THREAD_POOL_SIZE);

            mRequestQueue.start();

        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }
}
