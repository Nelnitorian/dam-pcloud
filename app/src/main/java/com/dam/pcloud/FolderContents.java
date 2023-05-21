package com.dam.pcloud;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;


import com.dam.pcloud.rest.Error;
import com.dam.pcloud.rest.HandlerCallBack;
import com.dam.pcloud.rest.IPcloudRestHandler;
import com.dam.pcloud.rest.PCloudFolder;
import com.dam.pcloud.rest.PCloudItem;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

public class FolderContents extends AppCompatActivity {

    private ListView lista;
    private SearchView buscar;
    private Adaptador adaptador;
    private ArrayList<ListItem> items;
    private Context context;
    private IPcloudRestHandler handler;
    private PCloudFolder currentFolder;
    private static final String LOG_TAG = "FolderContents";
    private static final int PICKFILE_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inicio);

        this.context = this;

        // Recupera el handler
        this.handler = MypCloud.getInstance().getHandler();

        Intent intent = getIntent();
        String folder_id = intent.getStringExtra("folder_id");

        handler.folder_list(folder_id, new HandlerCallBack() {
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

    @Override
    protected void onResume() {
        super.onResume();
        if (MypCloud.getInstance().isLogout())
            finish();
    }

    private void changeTitle(){
        TextView textView = findViewById(R.id.inicio);
        if (currentFolder.getId().equals("0")){
            textView.setText(getResources().getString(R.string.inicio));
        } else {
            textView.setText(currentFolder.getName());
        }

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
        if (MypCloud.getInstance().getClipboard() != null)
            popupMenu.getMenu().add(Menu.NONE, plusArray.length, plusArray.length, "Pegar");

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                // Obtener el elemento seleccionado del array
                String selectedItem = (String) item.getTitle();

                // Realizar la acción correspondiente según el elemento seleccionado
                if (selectedItem.equals("Crear carpeta")) {
                    Log.d(LOG_TAG, "Se ha seleccionado 'Crear carpeta'.");
                    mostrarDialogo();
                } else if (selectedItem.equals("Subir archivos")) {
                    Log.d(LOG_TAG, "Se ha seleccionado 'Subir archivos'.");
                    openFileExplorer();
                }else if (selectedItem.equals("Pegar")) {
                    Log.d(LOG_TAG, "Se ha seleccionado 'Pegar'.");
                    pasteItem();
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
        ListItem listItem;
        Log.d(LOG_TAG, "pcloudItem. name: "+pcloudItem.getName()+" type = "+pcloudItem.getType());
        if (pcloudItem.getType() == PCloudItem.ItemType.FOLDER){
            // Es un directorio
            listItem = new ListItem(R.drawable.folder, pcloudItem, R.drawable.points);
        } else if (pcloudItem.getType() == PCloudItem.ItemType.IMAGE){
            // Es una imagen
            listItem = new ListItem(R.drawable.photo, pcloudItem, R.drawable.points);
        } else if (pcloudItem.getType() == PCloudItem.ItemType.AUDIO){
            // Es un audio
            listItem = new ListItem(R.drawable.music, pcloudItem, R.drawable.points);
        } else if (pcloudItem.getType() == PCloudItem.ItemType.VIDEO){
            // Es un audio
            listItem = new ListItem(R.drawable.video, pcloudItem, R.drawable.points);
        } else {
            // Es otro
            listItem = new ListItem(R.drawable.file, pcloudItem, R.drawable.points);
        }
        return listItem;
    }

    private void refreshView(){
        lista = (ListView) findViewById(R.id.lista_archivos);
        buscar = (SearchView) findViewById(R.id.buscar);

        changeTitle();

        Log.d(LOG_TAG, "Se mostaran "+items.size()+" elementos");
        //Aplicamos un formato personalizado a cada elemento de la lista
        adaptador = new Adaptador(context, items);
        lista.setAdapter(adaptador);

        //Filtro del buscador
        buscar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                FolderContents.this.adaptador.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                FolderContents.this.adaptador.getFilter().filter(newText);
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
                ListItem newListitem = parsePCloudItemToListItem(pCloudItem);
                items.set(indexListItem, newListitem);

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

    void deleteEntryPoint(ListItem listItem){
        PCloudItem pcloudItem = listItem.getPcloudItem();

        String id = pcloudItem.getId();
        HandlerCallBack callback = new HandlerCallBack() {
            @Override
            public void onSuccess(Object obj) {
                PCloudItem pCloudItem = (PCloudItem) obj;
                Log.d(LOG_TAG, "Exito borrando "+listItem.getTextItem());

                ArrayList<PCloudItem> children = currentFolder.getChildren();

                children.remove(pCloudItem);
                items.remove(listItem);

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
            handler.folder_delete(id, callback);
        } else {
            // Es fichero
            handler.file_delete(id, callback);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICKFILE_REQUEST_CODE && resultCode == RESULT_OK){
            if (ContextCompat.checkSelfPermission(this , Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                // Tenemos permiso
                Log.d(LOG_TAG, "Ya se tenía permiso");
                uploadFile(data);
            } else {
                // Pedimos permiso
                Log.d(LOG_TAG, "Se va a solicitar permiso");
                ActivityResultLauncher<String> requestPermissionLauncher =
                        registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                            if (isGranted) {
                                // Hay permiso
                                Log.d(LOG_TAG, "Se ha concedido permiso");
                                uploadFile(data);
                            } else {
                                // No hay permiso
                                Log.d(LOG_TAG, "No hay permiso");
                                Toast.makeText(getApplicationContext(), "Error: no se ha concedido permisos", Toast.LENGTH_SHORT).show();
                            }
                        });

                requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        }
    }

    private void openFileExplorer() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        startActivityForResult(intent, PICKFILE_REQUEST_CODE);
    }

    private String getFilePathFromUri(Uri uri) {
        String path = null;
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {MediaStore.Images.Media.DATA};
            Cursor cursor = null;
            try {
                cursor = getContentResolver().query(uri, projection, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    path = cursor.getString(columnIndex);
                }
            } catch (Exception e) {
                // Handle the exception as needed
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            path = uri.getPath();
        }
        return path;
    }

    private InputStream getInputStreamFromUri(Uri uri){
        String path = getFilePathFromUri(uri);
        InputStream is;
        try {
            if (path == null){
                is = getContentResolver().openInputStream(uri);
            } else {
                is = new FileInputStream(path);
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        return is;
    }

    private String getFilenameFromUri(Uri uri) {
        String filename = null;
        if (uri != null) {
            String path = uri.getPath();
            int index = path.lastIndexOf("/");
            if (index >= 0 && index < path.length() - 1) {
                filename = path.substring(index + 1);
            }
        }
        return filename;
    }

    private void uploadFile(Intent data) {
        Uri uri = data.getData();
        InputStream is = getInputStreamFromUri(uri);
        String fileName = getFilenameFromUri(uri);

        Log.d(LOG_TAG, "Subir archivo. Uri: "+uri.toString());
        Log.d(LOG_TAG, "Subir archivo. Nombre del fichero: "+fileName);

        handler.file_upload(currentFolder.getId(), fileName, is, new HandlerCallBack() {
            @Override
            public void onSuccess(Object obj) {
                PCloudItem item = (PCloudItem) obj;

                ArrayList<PCloudItem> children = currentFolder.getChildren();
                children.add(item);
                currentFolder.setChildren(children);

                items.add(parsePCloudItemToListItem(item));

                Log.d(LOG_TAG, "Subido nuevo fichero: "+item.getName());
                refreshView();
            }

            @Override
            public void onError(Error error) {
                Log.d(LOG_TAG, "Error " + error.getCode() + " al subir fichero: " + error.getDescription());
                Toast.makeText(getApplicationContext(), "Error " + error.getCode() + " al subir fichero: " + error.getDescription(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void copyItemEntryPoint(ListItem listItem){
        MypCloud.getInstance().setClipboard(listItem.getPcloudItem());
        Log.d(LOG_TAG, "Se ha copiado el item: "+listItem.getTextItem());
        Toast.makeText(getApplicationContext(), "Se ha copiado correctamente", Toast.LENGTH_SHORT).show();
    }

    public void pasteItem(){
        PCloudItem clipboardItem = MypCloud.getInstance().getClipboard();
        Log.d(LOG_TAG, "Pegando item "+clipboardItem.getName());

        // Copiar un archivo

        String to_name = getNonTakenName(clipboardItem.getName());

        handler.file_copy(clipboardItem.getId(), currentFolder.getId(), to_name, new HandlerCallBack() {
            @Override
            public void onSuccess(Object obj) {
                PCloudItem item = (PCloudItem) obj;

                ArrayList<PCloudItem> children = currentFolder.getChildren();
                children.add(item);

                ListItem listItem = parsePCloudItemToListItem(item);
                items.add(listItem);

                refreshView();
                Log.d(LOG_TAG, "Exito pegando");
                Toast.makeText(getApplicationContext(), "Exito pegando", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onError(Error error) {
                Log.d(LOG_TAG, "Error " + error.getCode() + " al pegar: " + error.getDescription());
                Toast.makeText(getApplicationContext(), "Error " + error.getCode() + " al pegar: " + error.getDescription(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getNonTakenName(String base_name){
        Log.d(LOG_TAG, "base_name="+base_name);
        String non_taken = base_name;
        for (PCloudItem item : currentFolder.getChildren()){
            if (item.getName().equals(base_name)) {
                String [] split_name = non_taken.split("\\.");
                Log.d(LOG_TAG, "split.length="+split_name.length);
                split_name[0] = split_name[0] + " (Copy)";
                String new_name = String.join(".", split_name);
                non_taken = getNonTakenName(new_name);
                break;
            }
        }
        return non_taken;
    }

    public void logout(View v){
        handler.logout(new HandlerCallBack() {
            @Override
            public void onSuccess(Object obj) {
                Log.d(LOG_TAG, "Exito al cerrar sesión");
                Toast.makeText(getApplicationContext(), "Exito al cerrar sesión", Toast.LENGTH_SHORT).show();
                MypCloud.getInstance().setIsLogout(true);
                finish();
            }

            @Override
            public void onError(Error error) {
                Log.d(LOG_TAG, "Error " + error.getCode() + " al cerrar sesión: " + error.getDescription());
                Toast.makeText(getApplicationContext(), "Error " + error.getCode() + " al cerrar sesión: " + error.getDescription(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
