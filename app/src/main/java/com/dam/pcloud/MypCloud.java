package com.dam.pcloud;
import android.app.Application;

import com.android.volley.toolbox.Volley;
import com.dam.pcloud.rest.IPcloudRestHandler;
import com.dam.pcloud.rest.PcloudRestHandler;

import java.io.File;

public class MypCloud extends Application {
    private static MypCloud sInstance;
    private IPcloudRestHandler mPcloudRestHandler = null;
    private File mFile;
    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;

    }
    public synchronized static MypCloud getInstance() {
        return sInstance;
    }
    public IPcloudRestHandler getHandler() {
        if (mPcloudRestHandler == null)
            mPcloudRestHandler = new PcloudRestHandler(Volley.newRequestQueue(this));
        return mPcloudRestHandler;
    }
    public File getFilesDir() {
        return mFile;
    }
    public void setFilesDir(File file) {
        mFile = file;
    }
}