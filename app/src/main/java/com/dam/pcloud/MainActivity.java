package com.dam.pcloud;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import com.android.volley.toolbox.Volley;
import com.dam.pcloud.rest.Error;
import com.dam.pcloud.rest.Folder;
import com.dam.pcloud.rest.HandlerCallBack;
import com.dam.pcloud.rest.IPcloudRestHandler;
import com.dam.pcloud.rest.PcloudRestHandler;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pruebaPeticiones();

    }
    public void pruebaPeticiones () {
        IPcloudRestHandler handler = new PcloudRestHandler(Volley.newRequestQueue(this));

        // Primera petición: login
        handler.login("nelnitoreldestruior@gmail.com", "contrasenia1234_2", new HandlerCallBack(){
            @Override
            public void onSuccess(Object obj) {
                Log.d("LOGIN", "OK: "+(Integer)obj);

                // Segunda petición: listar ficheros
                handler.folder_list("0", new HandlerCallBack(){
                    @Override
                    public void onSuccess(Object obj) {
                        Folder folder = (Folder) obj;
                        Log.d("LISTFOLDER", "OK");
                        Log.d("LISTFOLDER", "Folder name: "+folder.getName());
                    }
                    @Override
                    public void onError(Error error){
                        Log.d("LISTFOLDER", "ERROR. Code: "+error.getCode()+" Description: "+error.getDescription());
                    }
                });
            }
            @Override
            public void onError(Error error){
                Log.d("LISTFOLDER", "ERROR. Code: "+error.getCode()+" Description: "+error.getDescription());
            }
        });


    }
}
