package com.dam.pcloud;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.dam.pcloud.rest.ErrorCode;
import com.dam.pcloud.rest.Folder;
import com.dam.pcloud.rest.HandlerCallBack;
import com.dam.pcloud.rest.PcloudRestHandler;

import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;


import java.io.IOException;
import java.util.Date;

public class MainActivity extends Activity {
    private static final String APPID = "eca867ad831d8b0fe1a41ab7f1efe917";
    //Método que queremos ejecutar en el servicio web
    private static String METHOD_NAME = "";
    //Namespace definido en el servicio web
    private static final String NAMESPACE = "http://tempuri.org/";
    //namespace + método
    private static String SOAP_ACTION = "";
    //Fichero de definición del servicio web
    private static final String URL = "http://www.dneonline.com/calculator.asmx";
    private TextView am; //Amanecer
    private TextView pr; //Presión
    private TextView te; //Temperatura
    private TextView vt; //Viento
    private TextView oc; //Ocaso
    private TextView ho; //Hora
    private TextView vi; //Visibilidad
    private TextView hu; //Humedad
    private TextView res;
    private EditText pa;
    private EditText ci;
    private EditText va1;
    private EditText va2;


    private Resultado response;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Hacer_peticion_REST();

    }
    public void Hacer_peticion_REST () {
        PcloudRestHandler handler = new PcloudRestHandler(Volley.newRequestQueue(this));

        // Primera petición: login
        handler.login("nelnitoreldestruior@gmail.com", "contrasenia1234_2", new HandlerCallBack(){
            @Override
            public void onSuccess(Object obj) {
                Log.d("LOGIN", "OK: "+(Integer)obj);

                // Segunda petición: listar ficheros
                handler.listfolder("0", new HandlerCallBack(){
                    @Override
                    public void onSuccess(Object obj) {
                        Folder folder = (Folder) obj;
                        Log.d("LISTFOLDER", "OK");
                        Log.d("LISTFOLDER", "Folder name: "+folder.getName());
                    }
                    @Override
                    public void onError(ErrorCode error){
                        Log.d("LISTFOLDER", "ERROR. Code: "+error.getCode()+" Description: "+error.getDescription());
                    }
                });
            }
            @Override
            public void onError(ErrorCode error){
                Log.d("LISTFOLDER", "ERROR. Code: "+error.getCode()+" Description: "+error.getDescription());
            }
        });


    }
}
