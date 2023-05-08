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

    }
    public void Hacer_peticion_suma_SOAP (View view) {
        METHOD_NAME="Add";
        SOAP_ACTION = "http://tempuri.org/Add";
        va1= (EditText)findViewById(R.id.valor1);
        va2= (EditText)findViewById(R.id.valor2);

        response=null;
        myAsyncTask myRequest = new myAsyncTask();
        myRequest.execute(va1.getText().toString(),va2.getText().toString());
    }
    public void Hacer_peticion_resta_SOAP (View view) {
        METHOD_NAME="Subtract";
        SOAP_ACTION = "http://tempuri.org/Subtract";
        va1= (EditText)findViewById(R.id.valor1);
        va2= (EditText)findViewById(R.id.valor2);

        response=null;
        myAsyncTask myRequest = new myAsyncTask();
        myRequest.execute(va1.getText().toString(),va2.getText().toString());
    }
    public void Hacer_peticion_multiplica_SOAP (View view) {
        METHOD_NAME="Multiply";
        SOAP_ACTION = "http://tempuri.org/Multiply";
        va1= (EditText)findViewById(R.id.valor1);
        va2= (EditText)findViewById(R.id.valor2);

        response=null;
        myAsyncTask myRequest = new myAsyncTask();
        myRequest.execute(va1.getText().toString(),va2.getText().toString());
    }
    public void Hacer_peticion_divide_SOAP (View view) {
        METHOD_NAME="Divide";
        SOAP_ACTION = "http://tempuri.org/Divide";
        va1= (EditText)findViewById(R.id.valor1);
        va2= (EditText)findViewById(R.id.valor2);

        response=null;
        myAsyncTask myRequest = new myAsyncTask();
        myRequest.execute(va1.getText().toString(),va2.getText().toString());
    }
    public void Hacer_peticion_REST (View view) {
        ci= (EditText)findViewById(R.id.ciudad);
        pa= (EditText)findViewById(R.id.pais);
        final String URL="http://api.openweathermap.org/data/2.5/weather?q="+ci.getText().toString()+
                ","+pa.getText().toString()+"&lang=es&units=metric&appid="+APPID;
        Log.d("REST", URL.toString());
        final ProgressDialog dlg = ProgressDialog.show(
                this,
                "Obteniendo los datos REST",
                "Por favor, espere...", true);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        dlg.dismiss();
                        try{
                            int codigo=response.getInt("cod");
                            if(codigo==200){
                                //Se ha recibido una previsión
                                Toast.makeText(getApplicationContext(),
                                        "Previsión meteorológica recibida",
                                        Toast.LENGTH_SHORT).show();
                                am = (TextView) findViewById(R.id.amanecer);
                                pr = (TextView) findViewById(R.id.presion);
                                te = (TextView) findViewById(R.id.temperatura);
                                vt = (TextView) findViewById(R.id.viento);
                                oc = (TextView) findViewById(R.id.ocaso);
                                ho = (TextView) findViewById(R.id.hora);
                                vi = (TextView) findViewById(R.id.visibilidad);
                                hu = (TextView) findViewById(R.id.humedad);
                                JSONObject main=response.getJSONObject("main");
                                String temp=main.getString("temp");
                                te.setText(temp + " ºC");
                                pr.setText(response.getJSONObject("main").getString("pressure")
                                        +" bar");
                                Date date = new Date();
                                date.setTime((long)response.getJSONObject("sys").getInt("sunrise")*1000);
                                am.setText(date.toString());
                                vt.setText(response.getJSONObject("wind").getString("speed")+
                                        " km/h, orientación: "+
                                        response.getJSONObject("wind").getString("deg")+"º");
                                oc.setText(response.getJSONObject("sys").getString("sunset"));
                                ho.setText(response.getString("timezone"));
                                vi.setText(response.getString("visibility"));
                                hu.setText(response.getJSONObject("main").getString("humidity"));
                            } else {
                                //Problema en la previsión
                                Toast.makeText(getApplicationContext(),
                                        "No se ha recibido la previsión", Toast.LENGTH_SHORT).show();
                            } } catch (JSONException e){
                            e.printStackTrace();
                        }
                        VolleyLog.v("Response:%n %s", response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dlg.dismiss();
                VolleyLog.e("Error: ", error.getMessage());
                Toast.makeText(getApplicationContext(),
                        "No se ha recibido la previsión", Toast.LENGTH_SHORT).show();
            }
        });
        // add the request object to the queue to be executed
        PracticaServiciosWebApplication.getInstance().getRequestQueue().add(request);
    }


    private class myAsyncTask extends AsyncTask<String, String, Void> {
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if(response!=null) {
                res=(TextView) findViewById(R.id.resultado);
                res.setText(String.valueOf(response.getResultado()));
            }
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected Void doInBackground(String... arg0) {
            Resultado resultado;
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
            PropertyInfo Valor1 = new PropertyInfo();
            Valor1.name = "intA";
            Valor1.setNamespace(NAMESPACE); // to ensure that the element-name is prefixed with the namespace
            //CityName.type = PropertyInfo.STRING_CLASS;
            Valor1.setValue(arg0[0]);

            PropertyInfo Valor2 = new PropertyInfo();
            Valor2.name = "intB";
            //CountryName.type = PropertyInfo.STRING_CLASS;
            Valor2.setNamespace(NAMESPACE); // to ensure that the element-name is prefixed with the namespace
            Valor2.setValue(arg0[1]);

            request.addProperty(Valor1);
            request.addProperty(Valor2);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.implicitTypes=true;
            envelope.setOutputSoapObject(request);
            HttpTransportSE httpTransport = new HttpTransportSE(URL);
            httpTransport.debug = true;
            try {
                httpTransport.call(SOAP_ACTION, envelope);
                Log.d("SOAP", "HTTP REQUEST:\n" + httpTransport.requestDump);
                Log.d("SOAP", "HTTP RESPONSE:\n" + httpTransport.responseDump);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } //send request
            SoapObject result =null;
            SoapFault soapfault =null;
            if (envelope.bodyIn instanceof SoapObject) { // SoapObject = SUCCESS
                result = (SoapObject) envelope.bodyIn;
            } else if (envelope.bodyIn instanceof SoapFault) { // SoapFault = FAILURE
                soapfault = (SoapFault) envelope.bodyIn;
            }

            if(soapfault==null && result!=null){
                Log.d("SOAP",result.getProperty(0).toString());
                resultado = new Resultado();
                resultado.setResultado(Integer.parseInt(result.getProperty(0).toString()));
                response = resultado;
            }

            return null;
        }
    }
}
