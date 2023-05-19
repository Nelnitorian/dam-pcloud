package com.dam.pcloud;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.dam.pcloud.rest.Error;
import com.dam.pcloud.rest.HandlerCallBack;
import com.dam.pcloud.rest.IPcloudRestHandler;

public class LostPassword extends AppCompatActivity {

    private EditText email;
    private IPcloudRestHandler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lostpass);
        this.handler = MypCloud.getInstance().getHandler();
    }

    public void clicEnRecuperarContra(View view) {
        //Recuperamos los valores introducidos por el usuario
        email= (EditText)findViewById(R.id.email);
        Log.d("Recuperar contraseña", "Email: " +email.getText());

        handler.recover_password(email.getText().toString(), new HandlerCallBack() {
            @Override
            public void onSuccess(Object obj) {
                // Exito
                Log.d("Recuperar contraseña", "Correo enviado!");
                Intent intent = new Intent(getApplicationContext(), Inicio.class);
                startActivity(intent);
            }

            @Override
            public void onError(Error error) {
                // Error
                Toast.makeText(getApplicationContext(), "Error "+error.getCode()+" al intentar rercuperar la contraseña: "+error.getDescription(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void clicEnIniciarSesion(View view) {
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
    }
}