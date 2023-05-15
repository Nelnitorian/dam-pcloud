package com.dam.pcloud.rest;

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
        JSONObject response = null;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, uri, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                            int codigo=response.getInt("cod");
                            if(codigo==200){
                                JSONObject main = response.getJSONObject("main");
//                                String temp=main.getString("temp");
                                callback.onSuccess(main);
                            } else {
                                //Problema
//                                callback.onError(main);
                            } } catch (JSONException e){
                            e.printStackTrace();
                        }
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
