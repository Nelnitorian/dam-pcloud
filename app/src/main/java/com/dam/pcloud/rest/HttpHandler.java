package com.dam.pcloud.rest;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class HttpHandler {

    private RequestQueue queue;

    public HttpHandler(RequestQueue queue){
        this.queue = queue;
    }

    public void getRequest (String uri, HttpCallBack callback) {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, uri, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        callback.onSuccess(response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//                        callback.onError();
                    }
                });
        // add the request object to the queue to be executed
        queue.add(request);
    }

    public void postRequest (String uri, String body, HttpCallBack callback) {
//        TODO esta copiado de get. No funciona.
        Log.d("HTTPHANDLER", "Uri: "+uri);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, uri, new JSONObject(){
            @Override
            public String toString(){
                return body;
            }
        },
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        callback.onSuccess(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                        callback.onError();
            }
        });
        // add the request object to the queue to be executed
        queue.add(request);
    }
}
