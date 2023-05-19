package com.dam.pcloud;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.dam.pcloud.rest.Error;
import com.dam.pcloud.rest.HandlerCallBack;
import com.dam.pcloud.rest.IPcloudRestHandler;

public class Login extends AppCompatActivity {

    private EditText email;
    private EditText contrasenia;
    private IPcloudRestHandler handler;
    private static final String LOG_TAG = "Inicio_sesion";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.handler = MypCloud.getInstance().getHandler();
        if(handler.alreadyLogged()){
            Intent intent = new Intent(getApplicationContext(), Inicio.class);
            startActivity(intent);
        } else {
            setContentView(R.layout.login);
            Log.d(LOG_TAG, "Pidiendo handler");
        }
    }

    public void clicEnRecuperarContra(View view) {
        Intent intent = new Intent(this, LostPassword.class);
        startActivity(intent);
    }
    public void clicEnIniciarSesion(View view) {
        //Recuperamos los valores introducidos por el usuario
        email= (EditText)findViewById(R.id.email);
        contrasenia= (EditText)findViewById(R.id.contrasenia);
        Log.d(LOG_TAG, "Email: " +email.getText()+ ". Contraseña: " +contrasenia.getText());

        handler.login(email.getText().toString(), contrasenia.getText().toString(), new HandlerCallBack() {
            @Override
            public void onSuccess(Object obj) {
                // Exito
                Log.d(LOG_TAG, "Login exitoso");
                Intent intent = new Intent(getApplicationContext(), Inicio.class);
                startActivity(intent);
            }

            @Override
            public void onError(Error error) {
                // Error
                Log.d(LOG_TAG, "Error "+error.getCode()+" al iniciar sesión: "+error.getDescription());
                Toast.makeText(getApplicationContext(), "Error "+error.getCode()+" al iniciar sesión: "+error.getDescription(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void clicEnRegistrarse(View view) {
        Intent intent = new Intent(this, Register.class);
        startActivity(intent);
    }
}
