package com.dam.pcloud;
import android.app.Application;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.dam.pcloud.rest.IPcloudRestHandler;
import com.dam.pcloud.rest.PcloudRestHandler;

public class MypCloud extends Application {
    private static MypCloud sInstance;
    private IPcloudRestHandler mPcloudRestHandler;
    @Override
    public void onCreate() {
        super.onCreate();
        mPcloudRestHandler = new PcloudRestHandler(Volley.newRequestQueue(this));
        sInstance = this;
    }
    public synchronized static MypCloud getInstance() {
        return sInstance;
    }
    public IPcloudRestHandler getHandler() {
        return mPcloudRestHandler;
    }
}