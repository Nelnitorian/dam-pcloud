package com.dam.pcloud;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.toolbox.Volley;
import com.dam.pcloud.rest.Error;
import com.dam.pcloud.rest.HandlerCallBack;
import com.dam.pcloud.rest.IPcloudRestHandler;
import com.dam.pcloud.rest.PcloudRestHandler;

public class Login extends AppCompatActivity {

    private EditText email;
    private EditText contrasenia;

    private IPcloudRestHandler handler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        this.handler = new PcloudRestHandler(Volley.newRequestQueue(this));
    }

    public void clicEnRecuperarContra(View view) {
        Intent intent = new Intent(this, LostPassword.class);
        startActivity(intent);
    }
    public void clicEnIniciarSesion(View view) {
        //Recuperamos los valores introducidos por el usuario
        email= (EditText)findViewById(R.id.email);
        contrasenia= (EditText)findViewById(R.id.contrasenia);
        Log.d("Inicio_sesion", "Email: " +email.getText()+ ". Contraseña: " +contrasenia.getText());

        //LLAMAR AL MÉTODO DE LOGIN
        handler.login(email.getText().toString(), contrasenia.getText().toString(), new HandlerCallBack() {
            @Override
            public void onSuccess(Object obj) {
                // Exito
                Intent intent = new Intent(getApplicationContext(), Inicio.class);
                startActivity(intent);
            }

            @Override
            public void onError(Error error) {
                // Error
                Toast.makeText(getApplicationContext(), "Error "+error.getCode()+" al iniciar sesión: "+error.getDescription(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void clicEnRegistrarse(View view) {
        Intent intent = new Intent(this, Register.class);
        startActivity(intent);
    }
}
