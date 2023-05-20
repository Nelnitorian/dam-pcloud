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
                        Log.d("HTTPHANDLER", response.toString());
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

    public void putRequest (String uri, byte[] body, HttpCallBack callback) {
        Log.d("HTTPHANDLER", "Uri: "+uri);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, uri, null,
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
        }){
            @Override
            public String getBodyContentType() {
                return "application/octet-stream";
            }

            @Override
            public byte[] getBody() {
                return body;
            }
        };
        // add the request object to the queue to be executed
        queue.add(request);
    }
}
