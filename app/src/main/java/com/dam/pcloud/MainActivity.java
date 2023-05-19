package com.dam.pcloud;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import com.android.volley.toolbox.Volley;
import com.dam.pcloud.rest.Error;
import com.dam.pcloud.rest.PCloudFile;
import com.dam.pcloud.rest.PCloudFolder;
import com.dam.pcloud.rest.HandlerCallBack;
import com.dam.pcloud.rest.IPcloudRestHandler;
import com.dam.pcloud.rest.PCloudItem;
import com.dam.pcloud.rest.PcloudRestHandler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class MainActivity extends Activity {
    private String path;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setup();
        pruebaPeticiones();

    }
    private void setup(){
        String filename = "example.txt";
        String fileContents = "Hello world!";
        try (FileOutputStream fos = openFileOutput(filename, Context.MODE_PRIVATE)) {
            fos.write(fileContents.getBytes());
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String readFile(){
        String filename = "example.txt";
        FileInputStream fis = null;
        try {
            fis = openFileInput(filename
            );
            InputStreamReader inputStreamReader =
                    new InputStreamReader(fis, StandardCharsets.UTF_8);
            StringBuilder stringBuilder = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(inputStreamReader)) {
                String line = reader.readLine();
                while (line != null) {
                    stringBuilder.append(line).append('\n');
                    line = reader.readLine();
                }
            } catch (IOException e) {
                // Error occurred when opening raw file for reading.
            } finally {
                String contents = stringBuilder.toString();
                return contents;
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    public void pruebaPeticiones () {
        IPcloudRestHandler handler = new PcloudRestHandler(Volley.newRequestQueue(this));

        // Primera petición: login
        handler.login("nelnitoreldestruior@gmail.com", "contrasenia1234_2", new HandlerCallBack(){
            @Override
            public void onSuccess(Object obj) {
                Log.d("LOGIN", "OK: "+(Integer)obj);

                // Segunda petición: listar ficheros
//                handler.folder_list("0", new HandlerCallBack(){
//                    @Override
//                    public void onSuccess(Object obj) {
//                        PCloudFolder folder = (PCloudFolder) obj;
//                        Log.d("LISTFOLDER", "OK");
//                        Log.d("LISTFOLDER", "Folder name: "+folder.getName());
//                    }
//                    @Override
//                    public void onError(Error error){
//                        Log.d("LISTFOLDER", "ERROR. Code: "+error.getCode()+" Description: "+error.getDescription());
//                    }
//                });

                File file = new File(getFilesDir(), "example.txt");
                String data = readFile();

                Log.i("SETUP", "Path: "+file.getAbsolutePath());
                // Tercera petición: subir fichero
                handler.file_upload("0","prueba_remota", file.getAbsolutePath(), new HandlerCallBack(){
                    @Override
                    public void onSuccess(Object obj) {
                        PCloudFile file = (PCloudFile) obj;
                        Log.d("FILE_UPLOAD", "OK");
                        Log.d("FILE_UPLOAD", "NOMBRE DE FICHERO: "+file.getName());
                    }
                    @Override
                    public void onError(Error error){
                        Log.d("FILE_UPLOAD", "ERROR. Code: "+error.getCode()+" Description: "+error.getDescription());
                    }
                });

                // Cuarta petición: descargar fichero
//                handler.file_upload("0","prueba_remota", file.getAbsolutePath(), new HandlerCallBack(){
//                    @Override
//                    public void onSuccess(Object obj) {
//                        ArrayList<PCloudItem> children = (ArrayList<PCloudItem>) obj;
//                        PCloudFile file = (PCloudFile) children.get(0);
//                        Log.d("FILE_UPLOAD", "OK");
//                        Log.d("FILE_UPLOAD", "NOMBRE DE FICHERO: "+file.getName());
//                    }
//                    @Override
//                    public void onError(Error error){
//                        Log.d("FILE_UPLOAD", "ERROR. Code: "+error.getCode()+" Description: "+error.getDescription());
//                    }
//                });
            }
            @Override
            public void onError(Error error){
                Log.d("FILE_UPLOAD", "ERROR. Code: "+error.getCode()+" Description: "+error.getDescription());
            }
        });


    }

//    private void setup(){
//        String filename = "example.txt";
//        String fileContents = "Hello world!";
//        try (FileOutputStream fos = openFileOutput(filename, Context.MODE_PRIVATE)) {
//            fos.write(fileContents.getBytes());
//        } catch (FileNotFoundException e) {
//            throw new RuntimeException(e);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }
//private String readFile(){
//    String filename = "example.txt";
//    FileInputStream fis = null;
//    try {
//        fis = openFileInput(filename
//        );
//        InputStreamReader inputStreamReader =
//                new InputStreamReader(fis, StandardCharsets.UTF_8);
//        StringBuilder stringBuilder = new StringBuilder();
//        try (BufferedReader reader = new BufferedReader(inputStreamReader)) {
//            String line = reader.readLine();
//            while (line != null) {
//                stringBuilder.append(line).append('\n');
//                line = reader.readLine();
//            }
//        } catch (IOException e) {
//            // Error occurred when opening raw file for reading.
//        } finally {
//            String contents = stringBuilder.toString();
//            return contents;
//        }
//    } catch (FileNotFoundException e) {
//        throw new RuntimeException(e);
//    }
}
