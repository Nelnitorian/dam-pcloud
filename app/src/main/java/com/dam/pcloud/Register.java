package com.dam.pcloud;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.toolbox.Volley;
import com.dam.pcloud.rest.Error;
import com.dam.pcloud.rest.HandlerCallBack;
import com.dam.pcloud.rest.IPcloudRestHandler;
import com.dam.pcloud.rest.PcloudRestHandler;

public class Register extends AppCompatActivity {
    private EditText email;
    private EditText contrasenia;
    private IPcloudRestHandler handler;
    private static final String LOG_TAG = "Registro";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        Log.d(LOG_TAG, "Pidiendo handler");
        this.handler = MypCloud.getInstance().getHandler();
    }


    public void clicEnIniciarSesion(View view) {
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
    }
    public void clicEnRegistrarse(View view) {
        //Recuperamos los valores introducidos por el usuario
        email= (EditText)findViewById(R.id.email);
        contrasenia= (EditText)findViewById(R.id.contrasenia);
        Log.d(LOG_TAG, "Email: " +email.getText()+ ". Contraseña: " +contrasenia.getText());
        handler.register(email.getText().toString(), contrasenia.getText().toString(), new HandlerCallBack() {
            @Override
            public void onSuccess(Object obj) {
                // Exito
                Log.d(LOG_TAG, "Exito al registrarse");
                Toast.makeText(getApplicationContext(), "Exito", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
            }

            @Override
            public void onError(Error error) {
                // Error
                Log.d(LOG_TAG, "Error "+error.getCode()+" al registrarse: "+error.getDescription());
                Toast.makeText(getApplicationContext(), "Error "+error.getCode()+" al registrarse: "+error.getDescription(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}