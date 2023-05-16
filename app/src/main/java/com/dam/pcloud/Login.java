package com.dam.pcloud;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class Login extends AppCompatActivity {

    private EditText email;
    private EditText contrasenia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
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

        //Aquí comprobar que el login ha ido bien.
            // En caso correcto nos vamos a inicio.
        //if .........
        Intent intent = new Intent(this, Inicio.class);
        startActivity(intent);

        //else ........
            // En caso incorrecto permanecemos aquí y avisamos de error con un Toast

    }
    public void clicEnRegistrarse(View view) {
        Intent intent = new Intent(this, Register.class);
        startActivity(intent);
    }
}
