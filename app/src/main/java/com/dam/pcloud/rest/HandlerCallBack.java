package com.dam.pcloud.rest;

public interface HandlerCallBack {
    void onSuccess(Object obj);
    void onError(ErrorCode error);
}
