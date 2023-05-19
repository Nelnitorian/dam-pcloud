package com.dam.pcloud;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class LostPassword extends AppCompatActivity {

    private EditText email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lostpass);
    }

    public void clicEnRecuperarContra(View view) {
        //Recuperamos los valores introducidos por el usuario
        email= (EditText)findViewById(R.id.email);
        Log.d("Recuperar contraseña", "Email: " +email.getText());

        //LLAMAR AL MÉTODO DE RECUPERAR CONTRASEÑA

        //Si todo ha ido bien mostrarlo en un toast ("Email de recuperación de contraseña enviado a (email)")
        //if ....

        //Si ha ido algo mal mostarlo en un toast ("El email indicado no está registrado en pcloud")
        //else ...

    }
    public void clicEnIniciarSesion(View view) {
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
    }
}