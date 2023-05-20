package com.dam.pcloud;

import android.app.AlertDialog;
import android.content.Context;
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
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.SearchView;


import com.dam.pcloud.rest.Error;
import com.dam.pcloud.rest.HandlerCallBack;
import com.dam.pcloud.rest.IPcloudRestHandler;
import com.dam.pcloud.rest.PCloudFolder;
import com.dam.pcloud.rest.PCloudItem;

import java.util.ArrayList;

public class Inicio extends AppCompatActivity {

    private ListView lista;
    private SearchView buscar;
    private Adaptador adaptador;
    private ArrayList<ListItem> items;
    private Context context;
    private IPcloudRestHandler handler;
    private PCloudFolder currentFolder;
    private static final String LOG_TAG = "Inicio";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inicio);

        this.context = this;

        this.handler = MypCloud.getInstance().getHandler();

        // Se hace la petición al servidor de la carpeta "/"
        handler.folder_list("0", new HandlerCallBack() {
            @Override
            public void onSuccess(Object obj) {
                currentFolder = (PCloudFolder) obj;

                Log.d(LOG_TAG, "Exito en la peticion de la carpeta con id 0");
                Log.d(LOG_TAG, "Se han traído "+currentFolder.getChildren().size()+" hijos del directorio.");

                // Poblamos los ListItem para la vista
                items = new ArrayList<>();
                for (PCloudItem item : currentFolder.getChildren()) {
                    items.add(parsePCloudItemToListItem(item));
                }

                refreshView();
            }

            @Override
            public void onError(Error error) {
                Log.d(LOG_TAG, "Error " + error.getCode() + " al iniciar sesión: " + error.getDescription());
                Toast.makeText(getApplicationContext(), "Error " + error.getCode() + " al solicitar ficheros: " + error.getDescription(), Toast.LENGTH_SHORT).show();
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
                    Log.d(LOG_TAG, "Se ha seleccionado 'Crear carpeta'.");
                    mostrarDialogo();
                } else if (selectedItem.equals("Subir fotos")) {
                    Log.d(LOG_TAG, "Se ha seleccionado 'Subir fotos'.");
                    //LLAMAR AL MÉTODO DE UPLOAD CORRESPONDIENTE
                } else if (selectedItem.equals("Subir archivos")) {
                    Log.d(LOG_TAG, "Se ha seleccionado 'Subir archivos'.");
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
                handler.folder_create(currentFolder.getId(), nuevaCarpeta, new HandlerCallBack() {
                    @Override
                    public void onSuccess(Object obj) {
                        PCloudFolder folder = (PCloudFolder) obj;

                        ArrayList<PCloudItem> children = currentFolder.getChildren();
                        children.add(folder);
                        currentFolder.setChildren(children);

                        items.add(parsePCloudItemToListItem(folder));

                        Log.d(LOG_TAG, "Creada nueva carpeta: "+folder.getName());
                        refreshView();

                    }

                    @Override
                    public void onError(Error error) {
                        Log.d(LOG_TAG, "Error " + error.getCode() + " al crear carpeta: " + error.getDescription());
                        Toast.makeText(getApplicationContext(), "Error " + error.getCode() + " al crear carpeta: " + error.getDescription(), Toast.LENGTH_SHORT).show();
                    }
                });
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

    private ListItem parsePCloudItemToListItem(PCloudItem pcloudItem){
        // TODO poner los iconos bien
        ListItem listItem;
        if (pcloudItem.getType() == PCloudItem.ItemType.FOLDER){
            // Es un directorio
            listItem = new ListItem(R.drawable.file, pcloudItem, R.drawable.points);
        } else if (pcloudItem.getType() == PCloudItem.ItemType.IMAGE){
            // Es una imagen
            listItem = new ListItem(R.drawable.file, pcloudItem, R.drawable.points);
        } else if (pcloudItem.getType() == PCloudItem.ItemType.AUDIO){
            // Es un audio
            listItem = new ListItem(R.drawable.file, pcloudItem, R.drawable.points);
        } else {
            // Es otro
            listItem = new ListItem(R.drawable.file, pcloudItem, R.drawable.points);
        }
        return listItem;
    }

    private void refreshView(){
        lista = (ListView) findViewById(R.id.lista_archivos);
        buscar = (SearchView) findViewById(R.id.buscar);

        //Aplicamos un formato personalizado a cada elemento de la lista
        adaptador = new Adaptador(context, items);
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

    void renameEntryPoint(ListItem listItem, String newName){
        PCloudItem pcloudItem = listItem.getPcloudItem();

        String id = pcloudItem.getId();
        HandlerCallBack callback = new HandlerCallBack() {
            @Override
            public void onSuccess(Object obj) {
                PCloudItem pCloudItem = (PCloudItem) obj;
                Log.d(LOG_TAG, "Exito renombrando de "+listItem.getTextItem() +" a "+newName);

                ArrayList<PCloudItem> children = currentFolder.getChildren();

                int indexChildren = children.indexOf(listItem.getPcloudItem());
                children.set(indexChildren, pCloudItem);

                int indexListItem = items.indexOf(listItem);
                listItem.setPcloudItemAndText(pCloudItem);
                items.set(indexListItem, listItem);

                refreshView();
            }

            @Override
            public void onError(Error error) {
                Log.d(LOG_TAG, "Error " + error.getCode() + " al renombrar: " + error.getDescription());
                Toast.makeText(getApplicationContext(), "Error " + error.getCode() + " al renombrar: " + error.getDescription(), Toast.LENGTH_SHORT).show();
            }
        };

        if (pcloudItem.getType() == PCloudItem.ItemType.FOLDER){
            // Es directorio
            handler.folder_rename(id, newName, callback);
        } else {
            // Es fichero
            handler.file_rename(id, pcloudItem.getParent_id(), newName, callback);
        }
    }
}
