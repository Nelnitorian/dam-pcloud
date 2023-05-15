package com.dam.pcloud.rest;

public class SyncObject<T> {
    private T data;
    public SyncObject(T obj){
        this.data = obj;
    }

    public void set(T new_data){
        this.data = new_data;
    }

    public T get(){
        return this.data;
    }
}
