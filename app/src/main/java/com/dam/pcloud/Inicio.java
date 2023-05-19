package com.dam.pcloud;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.SearchView;


import java.util.ArrayList;

public class Inicio extends AppCompatActivity {

    private ListView lista;
    private SearchView buscar;
    private int fotoItem;
    private Adaptador adaptador;
    private ArrayList<ListItem> items;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inicio);
        lista = (ListView) findViewById(R.id.lista_archivos);
        buscar = (SearchView) findViewById(R.id.buscar);

        //LLAMAR AL MÉTODO QUE LISTE TODOS LOS ARCHIVOS Y CARPETAS

        // ArrayList para almacenar los elementos de la lista
        items = new ArrayList<>();
        //Dependiendo del formato del archivo ponemos una imagen personalizada
        fotoItem = R.drawable.file; //He puesto esta por defecto para probar
        //lista incluye unos archivos de ejemplo que he creado para probar el código
        for (int i = 0; i < lista.getCount(); i++) {
            items.add(new ListItem(fotoItem, lista.getItemAtPosition(i).toString(), R.drawable.points));
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
    }


    //Al hacer click en el botón + se llama al método que despliega el menú de opciones
    public void clicEnPlusCircle(View view) {
        showDesplegable(view);
    }

    //Método que muestra un menú desplegable al pulsar el botón +
    private void showDesplegable(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);

        // Obtener los elementos del array
        String[] plusArray = this.getResources().getStringArray(R.array.plus);

        // Agregar los elementos al desplegable
        for (int i = 0; i < plusArray.length; i++) {
            popupMenu.getMenu().add(Menu.NONE, i, i, plusArray[i]);
        }

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int menuItemId = item.getItemId();

                // Obtener el elemento seleccionado del array
                String selectedItem = plusArray[menuItemId];

                // Realizar la acción correspondiente según el elemento seleccionado
                if (selectedItem.equals("Crear carpeta")) {
                    Log.d("Inicio", "Se ha seleccionado 'Crear carpeta'.");
                    mostrarDialogo();
                } else if (selectedItem.equals("Subir fotos")) {
                    Log.d("Inicio", "Se ha seleccionado 'Subir fotos'.");
                    //LLAMAR AL MÉTODO DE UPLOAD CORRESPONDIENTE
                } else if (selectedItem.equals("Subir archivos")) {
                    Log.d("Inicio", "Se ha seleccionado 'Subir archivos'.");
                    //LLAMAR AL MÉTODO DE UPLOAD CORRESPONDIENTE
                }

                return true;
            }
        });

        popupMenu.show();
    }

    //Método para mostrar un cuadro de diálogo al Crear Carpeta
    private void mostrarDialogo() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Crear carpeta");

        // Inflar el diseño personalizado para el contenido del diálogo
        View dialogView = LayoutInflater.from(this).inflate(R.layout.alert_dialog, null);
        builder.setView(dialogView);

        // Obtener la referencia a la caja de texto
        EditText editText = dialogView.findViewById(R.id.editText);
        editText.setHint("Nombre de Carpeta");
        builder.setPositiveButton("Crear", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String nuevaCarpeta = editText.getText().toString();
                //LLAMAR AL MÉTODO CREAR CARPETA Y POSTERIORMENTE ACTUALIZAR ARCHIVOS LOCALES
                Log.d("Inicio", "Nueva carpeta: "+nuevaCarpeta);
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //nada
            }
        });
        builder.show();
    }


}
