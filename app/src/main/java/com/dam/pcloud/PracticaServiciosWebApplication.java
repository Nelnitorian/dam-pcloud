package com.dam.pcloud;
import android.app.Application;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
public class PracticaServiciosWebApplication extends Application {
    private static PracticaServiciosWebApplication sInstance;
    private RequestQueue mRequestQueue;
    @Override
    public void onCreate() {
        super.onCreate();
        mRequestQueue = Volley.newRequestQueue(this);
        sInstance = this;
    }
    public synchronized static PracticaServiciosWebApplication
    getInstance() {
        return sInstance;
    }
    public RequestQueue getRequestQueue() {
        return mRequestQueue;
    }
}