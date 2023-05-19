package com.dam.pcloud;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;


import java.util.ArrayList;

public class Inicio extends AppCompatActivity {
    private Spinner sPlus;
    private Spinner sPoints;
    private ListView lista;
    private SearchView buscar;
    private int fotoItem;
    private Adaptador adaptador;
    private ArrayList<ListItem> items;
    private boolean isFirstItemSelection = true; // Variable para controlar la primera selección del spinner
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inicio);
        sPlus = (Spinner) findViewById(R.id.despl_plus);
        lista = (ListView) findViewById(R.id.lista_archivos);
        buscar = (SearchView) findViewById(R.id.buscar);

        //Para poder hacer referencia a un elemento que no está en Inicio.xml
        View convertView = LayoutInflater.from(this).inflate(R.layout.formato_item, null);
        sPoints = (Spinner) convertView.findViewById(R.id.despl_points);

        //Dependiendo del formato del archivo ponemos una imagen personalizada
        fotoItem= R.drawable.file;

        // ArrayList para almacenar los elementos de la lista
        items = new ArrayList<ListItem>();
        for (int i = 0; i < lista.getCount(); i++) {
            items.add(new ListItem(fotoItem,lista.getItemAtPosition(i).toString(),R.drawable.points));
        }

        //Aplicamos un formato personalizado a cada elemento de la lista
        adaptador = new Adaptador(this, items);
        lista.setAdapter(adaptador);

        //Filtro del buscador
        buscar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Inicio.this.adaptador.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Inicio.this.adaptador.getFilter().filter(newText);
                return false;
            }
        });


        //Desplegable botón +
        sPlus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isFirstItemSelection) {
                    isFirstItemSelection = false;
                    return; // Evita que se ejecute automáticamente en la primera selección
                }

                // Obtener el elemento seleccionado en el Spinner
                String selectedItem = (String) parent.getItemAtPosition(position);

                // Realizar la acción correspondiente según el elemento seleccionado
                if (selectedItem.equals("Crear carpeta")) {
                    Log.d("Inicio", "Se ha seleccionado 'Crear carpeta'.");
                    //metodoOpcion1();
                } else if (selectedItem.equals("Subir fotos")) {
                    Log.d("Inicio", "Se ha seleccionado 'Subir fotos'.");
                    //metodoOpcion2();
                } else if (selectedItem.equals("Subir archivos")) {
                    Log.d("Inicio", "Se ha seleccionado 'Subir archivos'.");
                    //metodoOpcion3();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Método vacío
            }
        });
    }

    //Al hacer click en el botón + sale un desplegable con varias opciones
    public void clicEnPlusCircle(View view) {
        sPlus.performClick();
    }

}
