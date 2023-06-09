package com.dam.pcloud;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
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
            contrasenia.setOnEditorActionListener((v, actionId, event) -> {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    hideKeyboard(v.getWindowToken());
                    clicEnIniciarSesion();
                    return true;
                }
                return false;
            });
        }
    }

    public void clicEnRecuperarContra(View view) {
        Intent intent = new Intent(this, LostPassword.class);
        startActivity(intent);
    }
    private void clicEnIniciarSesion(){
        //Recuperamos los valores introducidos por el usuario
        Log.d(LOG_TAG, "Email: " +email.getText()+ ". Contraseña: " +contrasenia.getText());

        handler.login(email.getText().toString(), contrasenia.getText().toString(), new HandlerCallBack() {
            @Override
            public void onSuccess(Object obj) {

                // Exito
                Log.d(LOG_TAG, "Login exitoso");
                MypCloud.getInstance().setIsLogout(false);
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
    public void clicEnIniciarSesion(View view) {
        clicEnIniciarSesion();
    }
    public void clicEnRegistrarse(View view) {
        Intent intent = new Intent(this, Register.class);
        startActivity(intent);
    }

    private void hideKeyboard(IBinder windowToken) {
        if (windowToken != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(windowToken, 0);
        }
    }

}
