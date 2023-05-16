package com.dam.pcloud;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

//Welcome
public class MainActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    public void clicEnIniciarSesion(View view) {
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
    }
    public void clicEnRegistrarse(View view) {
        Intent intent = new Intent(this, Register.class);
        startActivity(intent);
    }
}
