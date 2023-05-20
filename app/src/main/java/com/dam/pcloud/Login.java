package com.dam.pcloud;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.dam.pcloud.rest.Error;
import com.dam.pcloud.rest.HandlerCallBack;
import com.dam.pcloud.rest.IPcloudRestHandler;

public class Login extends AppCompatActivity implements View.OnKeyListener {

    private EditText email;
    private EditText contrasenia;
    private IPcloudRestHandler handler;
    private static final String LOG_TAG = "Inicio_sesion";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.handler = MypCloud.getInstance().getHandler();
        if(handler.alreadyLogged()){
            Intent intent = new Intent(getApplicationContext(), FolderContents.class);
            intent.putExtra("folder_id", "0");
            startActivity(intent);
        } else {
            setContentView(R.layout.login);
            Log.d(LOG_TAG, "Pidiendo handler");

            // Buscar y asignar las referencias de los campos de texto
            email = findViewById(R.id.email);
            contrasenia = findViewById(R.id.contrasenia);

            // Asignar el listener de teclado a los campos de texto
            email.setOnKeyListener((View.OnKeyListener) this);
            contrasenia.setOnKeyListener((View.OnKeyListener) this);
        }
    }

    public void clicEnRecuperarContra(View view) {
        Intent intent = new Intent(this, LostPassword.class);
        startActivity(intent);
    }
    public void clicEnIniciarSesion(View view) {
        //Recuperamos los valores introducidos por el usuario
        Log.d(LOG_TAG, "Email: " +email.getText()+ ". Contraseña: " +contrasenia.getText());

        handler.login(email.getText().toString(), contrasenia.getText().toString(), new HandlerCallBack() {
            @Override
            public void onSuccess(Object obj) {

                // Exito
                Log.d(LOG_TAG, "Login exitoso");
                Intent intent = new Intent(getApplicationContext(), FolderContents.class);
                intent.putExtra("folder_id", "0");
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

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
            // Verificar si se presionó la tecla Intro y si los campos están completos
            if (isFieldsCompleted()) {
                clicEnIniciarSesion(v);

                // Ocultar el teclado
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);

                return true;
            }
        }
        return false;
    }

    private boolean isFieldsCompleted() {
        String emailText = email.getText().toString();
        String contraseniaText = contrasenia.getText().toString();
        return !TextUtils.isEmpty(emailText) && !TextUtils.isEmpty(contraseniaText);
    }

}
