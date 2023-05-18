package com.dam.pcloud.rest;

import android.util.Log;

import com.android.volley.RequestQueue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class PcloudRestHandler {
    private static final String API_ENDPOINT = "https://eapi.pcloud.com/";

    private static final String METHOD_REGISTER = "register";
    private static final String METHOD_DIGEST = "getdigest";
    private static final String METHOD_LOGIN_DIGEST = "userinfo";
    private static final String METHOD_USER_INFO = "userinfo";
    private static final String METHOD_CREATE_FOLDER = "createfolder";
    private static final String METHOD_LOST_PASSOWRD = "lostpassword";
    private static final String METHOD_RENAME_FOLDER = "renamefolder";
    private static final String METHOD_COPY_FOLDER = "copyfolder";
    private static final String METHOD_DELETE_FOLDER = "deletefolder";
    private static final String METHOD_LIST_FOLDER = "listfolder";
    private static final String METHOD_UPLOAD_FILE = "uploadfile";
    private static final String METHOD_DOWNLOAD_FILE = "downloadfile";
    private static final String METHOD_RENAME_FILE = "renamefile";
    private static final String METHOD_STAT_FILE = "stat";
    private static final String METHOD_DELETE_FILE = "deletefile";
    private static final String METHOD_COPY_FILE = "copyfile";

    private static final String PARAM_MAIL = "mail";
    private static final String PARAM_PASSWORD = "password";
    private static final String PARAM_TERMSACCEPTED = "termsaccepted";
    private static final String PARAM_USERNAME = "username";
    private static final String PARAM_DIGEST = "digest";
    private static final String PARAM_PASSWORD_DIGEST = "passworddigest";
    private static final String PARAM_AUTH = "auth";
    private static final String PARAM_FOLDER_ID = "folderid";
    private static final String PARAM_NAME = "name";
    private static final String PARAM_TO_NAME ="toname";
    private static final String PARAM_TO_FOLDER_ID ="tofolderid";
    private static final String PARAM_FILE_NAME ="filename";
    private static final String PARAM_GET_AUTH ="getauth";
    private static final String RESPONSE_ERROR ="error";

    private static final String RESPONSE_RESULT = "result";
    private static final String RESPONSE_DATA = "metadata";
    private static final String RESPONSE_FOLDER_ID = "folderid";
    private static final String RESPONSE_FILE_ID = "fileid";
    private static final String RESPONSE_PARENT_FOLDER_ID = "parentfolderid";
    private static final String RESPONSE_NAME = "name";
    private static final String RESPONSE_CONTENTS = "contents";
    private static final String RESPONSE_IS_FOLDER = "isfolder";
    private static final String RESPONSE_CONTENT_TYPE = "contenttype";
    private static final String RESPONSE_SIZE = "size";
    private static final String RESPONSE_DIGEST = "digest";
    private static final String RESPONSE_AUTH = "auth";






//    private static final String PARAM_





    private String client_id;
    private String client_secret;
    private String auth_token;

    private final HttpHandler http_handler;


    public PcloudRestHandler(RequestQueue queue){
        http_handler = new HttpHandler(queue);
    }

    private Folder createFolderFromJson(JSONObject json) throws JSONException {

        try {
            Log.d("LISTFOLDER", "Folder RESPONSE_FOLDER_ID: " + json.getString(RESPONSE_FOLDER_ID));

        } catch (JSONException e) {
            Log.d("LISTFOLDER", "ERROR RESPONSE_FOLDER_ID");
        }

        try {
            Log.d("LISTFOLDER", "Folder RESPONSE_PARENT_FOLDER_ID: " + json.getString(RESPONSE_PARENT_FOLDER_ID));

        } catch (JSONException e) {
            Log.d("LISTFOLDER", "ERROR RESPONSE_PARENT_FOLDER_ID");
        }

        try {
            Log.d("LISTFOLDER", "Folder RESPONSE_NAME: " + json.getString(RESPONSE_NAME));

        } catch (JSONException e) {
            Log.d("LISTFOLDER", "ERROR RESPONSE_NAME");
        }

        String parent_folder_id;
        try {
            parent_folder_id = json.getString(RESPONSE_PARENT_FOLDER_ID);
        } catch (JSONException e){
            parent_folder_id = "0";
        }

        return new Folder(
                json.getString(RESPONSE_FOLDER_ID),
                parent_folder_id,
                json.getString(RESPONSE_NAME),
                Item.ItemType.FOLDER
        );
    }

    private File createFileFromJson(JSONObject json) throws JSONException {
        String contenttype = json.getString(RESPONSE_CONTENT_TYPE);

        Item.ItemType type;
        if(contenttype.contains("audio")){
            type = Item.ItemType.AUDIO;
        } else if(contenttype.contains("image")){
            type = Item.ItemType.IMAGE;
        } else { //File
            type = Item.ItemType.FILE;
        }

        try{
            Log.d("LISTFOLDER", "Folder RESPONSE_FILE_ID: "+json.getString(RESPONSE_FILE_ID));

        } catch (JSONException e){
            Log.d("LISTFOLDER", "ERROR RESPONSE_FILE_ID");
        }

        try{
            Log.d("LISTFOLDER", "Folder RESPONSE_PARENT_FOLDER_ID: "+json.getString(RESPONSE_PARENT_FOLDER_ID));

        } catch (JSONException e){
            Log.d("LISTFOLDER", "ERROR RESPONSE_PARENT_FOLDER_ID");
        }

        try{
            Log.d("LISTFOLDER", "Folder RESPONSE_NAME: "+json.getString(RESPONSE_NAME));

        } catch (JSONException e){
            Log.d("LISTFOLDER", "ERROR RESPONSE_NAME");
        }

        try{
            Log.d("LISTFOLDER", "Folder RESPONSE_SIZE: "+json.getInt(RESPONSE_SIZE));

        } catch (JSONException e){
            Log.d("LISTFOLDER", "ERROR RESPONSE_SIZE");
        }

        return new File(
                json.getString(RESPONSE_FILE_ID),
                json.getString(RESPONSE_PARENT_FOLDER_ID),
                json.getString(RESPONSE_NAME),
                json.getString(RESPONSE_SIZE),
                type
        );
    }

    private ArrayList<Item> createChildrenFromJson(JSONArray json_array) throws JSONException {
        Log.d("LISTFOLDER", "entrando en createChildrenFromJson");
        Log.d("LISTFOLDER", "info recibida: "+json_array.toString());
        ArrayList<Item> lst = new ArrayList<Item>();
        for (int i = 0; i<json_array.length(); i++){
            JSONObject json = (JSONObject)json_array.get(i);
            Log.d("LISTFOLDER", "trabajando con: "+json.toString());
            Item item;
            if(json.getBoolean(RESPONSE_IS_FOLDER)){
                Log.d("LISTFOLDER", "se ha considerado folder");
                item = createFolderFromJson(json);
            } else{
                Log.d("LISTFOLDER", "se ha considerado file");
                item = createFileFromJson(json);
            }
            lst.add(item);
        }
        return lst;
    }

    private ErrorCode extractError(JSONObject json){
        ErrorCode error;
        int result = -1;
        try{
            result = json.getInt(RESPONSE_RESULT);
        } catch (JSONException e){
            e.printStackTrace();
        }
        try{
            error = new ErrorCode(result, json.getString(RESPONSE_ERROR));
        } catch (JSONException e){
            error = new ErrorCode(result, "Unknown");
        }
        return error;
    }



    public void register(String mail, String password, HandlerCallBack callback) {
        /* Registra un usuario

        @param callback objeto de callback que gestionará el resultado
        @param mail correo electronico del usuario
        @param password contraseña del usuario
        */
        String parameters = ParameterHandler.parseRequest(new String[][]{
                {PARAM_MAIL, mail},
                {PARAM_PASSWORD, password},
                {PARAM_TERMSACCEPTED, "true"}
        });

        String final_uri = API_ENDPOINT + METHOD_REGISTER + parameters;

        http_handler.getRequest(final_uri, new HttpCallBack() {

            @Override
            public void onSuccess(JSONObject json) {
                try {
                    Integer status_code = json.getInt(RESPONSE_RESULT);
                    callback.onSuccess(status_code);
                } catch (JSONException e) {
                    callback.onError(extractError(json));
                }
            }
        });
    }

    public void obtainDigest(HandlerCallBack callback) {

        /* Obtiene el código temporal de digest

        @throws InterruptedException si se ha producido alguna excepción mientras se produce la petición
        @return código digest
        */

        String parameters = ParameterHandler.parseRequest(new String[][]{});

        String final_uri = API_ENDPOINT + METHOD_DIGEST + parameters;

        http_handler.getRequest(final_uri, new HttpCallBack() {
            public void onSuccess(JSONObject json) {
                try {
                    String new_digest = json.getString(RESPONSE_DIGEST);
                    callback.onSuccess(new_digest);
                } catch (JSONException e) {
                    callback.onError(extractError(json));;
                }
            }
        });
    }

    public void login(String username, String password, HandlerCallBack callback) {
        /* Inicia sesión usando el flujo de password digest

        @param callback objeto de callback que gestionará el resultado
        @param username nombre de usuario
        @param password contraseña del usuario
        */
        Log.d("LOGIN", "Entrando en login");
        obtainDigest(new HandlerCallBack(){
            @Override
            public void onSuccess(Object obj) {
                String digest = (String)obj;
                Log.d("LOGIN", "Digest obtenido: "+digest);

                String passworddigest = "";
                try {

                    MessageDigest md1 = MessageDigest.getInstance("SHA1");
                    md1.update(username.toLowerCase().getBytes(), 0 , username.length());
                    String sha1_username = new BigInteger(1, md1.digest()).toString(16);

                    String word = password+sha1_username+digest;
                    MessageDigest md2 = MessageDigest.getInstance("SHA1");
                    md2.update(word.getBytes(), 0 , word.length());
                    passworddigest = new BigInteger(1, md2.digest()).toString(16);

                } catch (NoSuchAlgorithmException e) {
//            Ignore: code unreachable
                }
                Log.d("LOGIN", "Password calculado: "+passworddigest);

                String parameters = ParameterHandler.parseRequest(new String[][]{
                        {PARAM_USERNAME, username},
                        {PARAM_DIGEST, digest},
                        {PARAM_GET_AUTH, Integer.toString(1)},
                        {PARAM_PASSWORD_DIGEST, passworddigest}
                });

                Log.d("LOGIN", "Parametros: "+parameters);

                String final_uri = API_ENDPOINT + METHOD_LOGIN_DIGEST + parameters;

                Log.d("LOGIN", "URL final: "+final_uri);

                http_handler.getRequest(final_uri, new HttpCallBack() {

                    @Override
                    public void onSuccess(JSONObject json) {
                        try {
                            Log.d("LOGIN", "Respuesta: "+json.toString());
                            auth_token = json.getString(RESPONSE_AUTH);
                            Log.d("LOGIN", "Token auth: "+json.getString(RESPONSE_AUTH));
                            Integer status_code = json.getInt(RESPONSE_RESULT);
                            callback.onSuccess(status_code);
                        } catch (JSONException e) {
                            callback.onError(extractError(json));;
                        }
                    }
                });
            }
            @Override
            public void onError(ErrorCode error){
                callback.onError(error);;
            }

        });
    }

    public void recover_password(String mail, HandlerCallBack callback) {
        /* Manda las instrucciones para restablecer la contraseña al correo.

        @param callback objeto de callback que gestionará el resultado
        @param mail correo al que se mandará la información
        */

        String parameters = ParameterHandler.parseRequest(new String[][]{
                {PARAM_MAIL, mail}
        });

        String final_uri = API_ENDPOINT + METHOD_LOST_PASSOWRD + parameters;

        http_handler.getRequest(final_uri, new HttpCallBack() {

            @Override
            public void onSuccess(JSONObject json) {
                try {
                    Integer status_code = json.getInt(RESPONSE_RESULT);
                    callback.onSuccess(status_code);
                } catch (JSONException e) {
                    callback.onError(extractError(json));;
                }
            }
        });
    }

    public void create_folder(String folder_id, String name, HandlerCallBack callback) {
        /* Manda las instrucciones para restablecer la contraseña al correo.

        Requiere auth.

        @param callback objeto de callback que gestionará el resultado
        @param folder_id id del directorio padre
        @param name nombre del directorio a crear
        */

        String parameters = ParameterHandler.parseRequest(new String[][]{
                {PARAM_FOLDER_ID, folder_id},
                {PARAM_NAME, name},
                {PARAM_AUTH, this.auth_token}
        });

        String final_uri = API_ENDPOINT + METHOD_CREATE_FOLDER + parameters;

        http_handler.getRequest(final_uri, new HttpCallBack() {

            @Override
            public void onSuccess(JSONObject json) {
                try {
                    Folder new_folder = createFolderFromJson(json.getJSONObject(RESPONSE_DATA));
                    callback.onSuccess(new_folder);
                } catch (JSONException e) {
                    callback.onError(extractError(json));;
                }
            }
        });
    }

    public void rename_folder(String folder_id, String new_name, HandlerCallBack callback) {
        /* Manda las instrucciones para restablecer la contraseña al correo.

        Requiere auth.

        @param callback objeto de callback que gestionará el resultado
        @param folder_id id del directorio padre
        @param name nombre del directorio a crear
        */

        String parameters = ParameterHandler.parseRequest(new String[][]{
                {PARAM_FOLDER_ID, folder_id},
                {PARAM_TO_NAME, new_name},
                {PARAM_AUTH, this.auth_token}
        });

        String final_uri = API_ENDPOINT + METHOD_RENAME_FOLDER + parameters;

        http_handler.getRequest(final_uri, new HttpCallBack() {

            @Override
            public void onSuccess(JSONObject json) {
                try {
                    Folder new_folder = createFolderFromJson(json.getJSONObject(RESPONSE_DATA));
                    callback.onSuccess(new_folder);
                } catch (JSONException e) {
                    callback.onError(extractError(json));;
                }
            }
        });
    }

    public void move_folder(String folder_id, String new_parent_id, HandlerCallBack callback) {
        /* Manda las instrucciones para restablecer la contraseña al correo.

        Requiere auth.

        @param callback objeto de callback que gestionará el resultado
        @param folder_id id del directorio padre
        @param name nombre del directorio a crear
        */

        String parameters = ParameterHandler.parseRequest(new String[][]{
                {PARAM_FOLDER_ID, folder_id},
                {PARAM_TO_FOLDER_ID, new_parent_id},
                {PARAM_AUTH, this.auth_token}
        });

        String final_uri = API_ENDPOINT + METHOD_RENAME_FOLDER + parameters;

        http_handler.getRequest(final_uri, new HttpCallBack() {

            @Override
            public void onSuccess(JSONObject json) {
                try {
                    Folder new_folder = createFolderFromJson(json.getJSONObject(RESPONSE_DATA));
                    callback.onSuccess(new_folder);
                } catch (JSONException e) {
                    callback.onError(extractError(json));;
                }
            }
        });
    }

    public void copy_folder(String folder_id, String new_parent_id, HandlerCallBack callback) {
        /* Manda las instrucciones para restablecer la contraseña al correo.

        Requiere auth.

        @param callback objeto de callback que gestionará el resultado
        @param folder_id id del directorio padre
        @param name nombre del directorio a crear
        */

        String parameters = ParameterHandler.parseRequest(new String[][]{
                {PARAM_FOLDER_ID, folder_id},
                {PARAM_TO_FOLDER_ID, new_parent_id},
                {PARAM_AUTH, this.auth_token}
        });

        String final_uri = API_ENDPOINT + METHOD_COPY_FOLDER + parameters;

        http_handler.getRequest(final_uri, new HttpCallBack() {

            @Override
            public void onSuccess(JSONObject json) {
                try {
                    Folder new_folder = createFolderFromJson(json.getJSONObject(RESPONSE_DATA));
                    callback.onSuccess(new_folder);
                } catch (JSONException e) {
                    callback.onError(extractError(json));;
                }
            }
        });
    }


    public void delete_folder(String folder_id, HandlerCallBack callback) {
        /* Manda las instrucciones para restablecer la contraseña al correo.

        Requiere auth.

        @param callback objeto de callback que gestionará el resultado
        @param folder_id id del directorio padre
        @param name nombre del directorio a crear
        */

        String parameters = ParameterHandler.parseRequest(new String[][]{
                {PARAM_FOLDER_ID, folder_id},
                {PARAM_AUTH, this.auth_token}
        });

        String final_uri = API_ENDPOINT + METHOD_DELETE_FOLDER + parameters;

        http_handler.getRequest(final_uri, new HttpCallBack() {

            @Override
            public void onSuccess(JSONObject json) {
                try {
                    Folder new_folder = createFolderFromJson(json.getJSONObject(RESPONSE_DATA));
                    callback.onSuccess(new_folder);
                } catch (JSONException e) {
                    callback.onError(extractError(json));;
                }
            }
        });
    }

    public void listfolder(String folder_id, HandlerCallBack callback) {
        /* Manda las instrucciones para restablecer la contraseña al correo.

        Requiere auth.

        @param callback objeto de callback que gestionará el resultado
        @param folder_id id del directorio padre
        @param name nombre del directorio a crear
        */

        String parameters = ParameterHandler.parseRequest(new String[][]{
                {PARAM_FOLDER_ID, folder_id},
                {PARAM_AUTH, this.auth_token}
        });

        String final_uri = API_ENDPOINT + METHOD_LIST_FOLDER + parameters;


        Log.d("LISTFOLDER", "URL final: "+final_uri);
        http_handler.getRequest(final_uri, new HttpCallBack() {

            @Override
            public void onSuccess(JSONObject json) {
                try {
                    Log.d("LISTFOLDER", "Respuesta: "+json);
                    Folder folder = createFolderFromJson(json.getJSONObject(RESPONSE_DATA));
                    Log.d("LISTFOLDER", "Folder rescatado: "+folder.getName());
                    ArrayList<Item> children = createChildrenFromJson(json.getJSONObject(RESPONSE_DATA).getJSONArray(RESPONSE_CONTENTS));
                    folder.setChildren(children);
                    callback.onSuccess(folder);
                } catch (JSONException e) {
                    callback.onError(extractError(json));;
                }
            }
        });
    }

    public void downloadfile(String folder_id, String file_name, String local_file_path, HandlerCallBack callback) {
        /* Manda las instrucciones para restablecer la contraseña al correo.

        Requiere auth.

        @param callback objeto de callback que gestionará el resultado
        @param folder_id id del directorio padre
        @param name nombre del directorio a crear
        */

//        TODO terminar. No sé descargar ficheros en http. Mirar: https://stackoverflow.com/questions/73759126/download-files-with-the-pcloud-api

        String parameters = ParameterHandler.parseRequest(new String[][]{
                {PARAM_FOLDER_ID, folder_id},
                {PARAM_FILE_NAME, file_name},
                {PARAM_AUTH, this.auth_token}
        });

        String final_uri = API_ENDPOINT + METHOD_DOWNLOAD_FILE + parameters;

        http_handler.getRequest(final_uri, new HttpCallBack() {

            @Override
            public void onSuccess(JSONObject json) {
                try {
                    Folder new_folder = createFolderFromJson(json.getJSONObject(RESPONSE_DATA));
                    callback.onSuccess(new_folder);
                } catch (JSONException e) {
                    callback.onError(extractError(json));;
                }
            }
        });
    }

    public void uploadfile(String folder_id, String file_name, String local_file_path, HandlerCallBack callback) {
        /* Manda las instrucciones para restablecer la contraseña al correo.

        Requiere auth.

        @param callback objeto de callback que gestionará el resultado
        @param folder_id id del directorio padre
        @param name nombre del directorio a crear
        */

//        TODO terminar. No sé subir ficheros en http

        String parameters = ParameterHandler.parseRequest(new String[][]{
                {PARAM_FOLDER_ID, folder_id},
                {PARAM_FILE_NAME, file_name},
                {PARAM_AUTH, this.auth_token}
        });

        String final_uri = API_ENDPOINT + METHOD_UPLOAD_FILE + parameters;

        http_handler.postRequest(final_uri, local_file_path, new HttpCallBack() {

            @Override
            public void onSuccess(JSONObject json) {
                try {
                    ArrayList<Item> children = createChildrenFromJson(json.getJSONArray(RESPONSE_DATA));
                    callback.onSuccess(children);
                } catch (JSONException e) {
                    callback.onError(extractError(json));;
                }
            }
        });
    }


    public void copyfile(String folder_id, String to_folder_id, String name, HandlerCallBack callback) {
        /* Manda las instrucciones para restablecer la contraseña al correo.

        Requiere auth.

        @param callback objeto de callback que gestionará el resultado
        @param folder_id id del directorio padre
        @param name nombre del directorio a crear
        */

        String parameters = ParameterHandler.parseRequest(new String[][]{
                {PARAM_FOLDER_ID, folder_id},
                {PARAM_TO_FOLDER_ID, to_folder_id},
                {PARAM_TO_NAME, name},
                {PARAM_AUTH, this.auth_token}
        });

        String final_uri = API_ENDPOINT + METHOD_COPY_FILE + parameters;

        http_handler.getRequest(final_uri, new HttpCallBack() {

            @Override
            public void onSuccess(JSONObject json) {
                try {
                    File new_file = createFileFromJson(json.getJSONObject(RESPONSE_DATA));
                    callback.onSuccess(new_file);
                } catch (JSONException e) {
                    callback.onError(extractError(json));;
                }
            }
        });
    }


    public void deletefile(String folder_id, HandlerCallBack callback) {
        /* Manda las instrucciones para restablecer la contraseña al correo.

        Requiere auth.

        @param callback objeto de callback que gestionará el resultado
        @param folder_id id del directorio padre
        @param name nombre del directorio a crear
        */

        String parameters = ParameterHandler.parseRequest(new String[][]{
                {PARAM_FOLDER_ID, folder_id},
                {PARAM_AUTH, this.auth_token}
        });

        String final_uri = API_ENDPOINT + METHOD_DELETE_FILE + parameters;

        http_handler.getRequest(final_uri, new HttpCallBack() {

            @Override
            public void onSuccess(JSONObject json) {
                try {
                    File new_file = createFileFromJson(json.getJSONObject(RESPONSE_DATA));
                    callback.onSuccess(new_file);
                } catch (JSONException e) {
                    callback.onError(extractError(json));;
                }
            }
        });
    }

    public void renamefile(String folder_id, String to_folder_id, String name, HandlerCallBack callback) {
        /* Manda las instrucciones para restablecer la contraseña al correo.

        Requiere auth.

        @param callback objeto de callback que gestionará el resultado
        @param folder_id id del directorio padre
        @param name nombre del directorio a crear
        */

        String parameters = ParameterHandler.parseRequest(new String[][]{
                {PARAM_FOLDER_ID, folder_id},
                {PARAM_TO_FOLDER_ID, to_folder_id},
                {PARAM_TO_NAME, name},
                {PARAM_AUTH, this.auth_token}
        });

        String final_uri = API_ENDPOINT + METHOD_RENAME_FILE + parameters;

        http_handler.getRequest(final_uri, new HttpCallBack() {

            @Override
            public void onSuccess(JSONObject json) {
                try {
                    File new_file = createFileFromJson(json.getJSONObject(RESPONSE_DATA));
                    callback.onSuccess(new_file);
                } catch (JSONException e) {
                    callback.onError(extractError(json));;
                }
            }
        });
    }

    public void statfile(String folder_id, HandlerCallBack callback) {
        /* Manda las instrucciones para restablecer la contraseña al correo.

        Requiere auth.

        @param callback objeto de callback que gestionará el resultado
        @param folder_id id del directorio padre
        @param name nombre del directorio a crear
        */

        String parameters = ParameterHandler.parseRequest(new String[][]{
                {PARAM_FOLDER_ID, folder_id},
                {PARAM_AUTH, this.auth_token}
        });

        String final_uri = API_ENDPOINT + METHOD_STAT_FILE + parameters;

        http_handler.getRequest(final_uri, new HttpCallBack() {

            @Override
            public void onSuccess(JSONObject json) {
                try {
                    File new_file = createFileFromJson(json.getJSONObject(RESPONSE_DATA));
                    callback.onSuccess(new_file);
                } catch (JSONException e) {
                    callback.onError(extractError(json));;
                }
            }
        });
    }
}

