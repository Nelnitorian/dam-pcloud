package com.dam.pcloud.rest;

import com.android.volley.RequestQueue;
import com.dam.pcloud.rest.Error;
import com.dam.pcloud.rest.ErrorCode;
import com.dam.pcloud.rest.File;
import com.dam.pcloud.rest.Folder;
import com.dam.pcloud.rest.HandlerCallBack;
import com.dam.pcloud.rest.HttpCallBack;
import com.dam.pcloud.rest.HttpHandler;
import com.dam.pcloud.rest.Item;
import com.dam.pcloud.rest.ParameterHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class PcloudRestHandler implements IPcloudRestHandler {
    private static final String API_ENDPOINT = "https://eapi.pcloud.com/";

    private static final String METHOD_REGISTER = "register";
    private static final String METHOD_DIGEST = "getdigest";
    private static final String METHOD_LOGIN_DIGEST = "userinfo";
//    private static final String METHOD_USER_INFO = "userinfo";
    private static final String METHOD_CREATE_FOLDER = "createfolder";
    private static final String METHOD_LOST_PASSWORD = "lostpassword";
    private static final String METHOD_FOLDER_RENAME = "renamefolder";
    private static final String METHOD_FOLDER_COPY = "copyfolder";
    private static final String METHOD_FOLDER_DELETE = "deletefolder";
    private static final String METHOD_FOLDER_LIST = "listfolder";
    private static final String METHOD_FILE_UPLOAD = "uploadfile";
    private static final String METHOD_FILE_DOWNLOAD = "downloadfile";
    private static final String METHOD_FILE_RENAME = "renamefile";
    private static final String METHOD_FILE_STAT = "stat";
    private static final String METHOD_FILE_DELETE = "deletefile";
    private static final String METHOD_FILE_COPY = "copyfile";

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
    private static final String PARAM_FILE_ID ="fileid";


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


    private String auth_token;

    private final HttpHandler http_handler;


    public PcloudRestHandler(RequestQueue queue){
        http_handler = new HttpHandler(queue);
    }

    private Folder createFolderFromJson(JSONObject json) throws JSONException {

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

        return new File(
                json.getString(RESPONSE_FILE_ID),
                json.getString(RESPONSE_PARENT_FOLDER_ID),
                json.getString(RESPONSE_NAME),
                json.getString(RESPONSE_SIZE),
                type
        );
    }

    private ArrayList<Item> createChildrenFromJson(JSONArray json_array) throws JSONException {
        ArrayList<Item> lst = new ArrayList<Item>();
        for (int i = 0; i<json_array.length(); i++){
            JSONObject json = (JSONObject)json_array.get(i);
            Item item;
            if(json.getBoolean(RESPONSE_IS_FOLDER)){
                item = createFolderFromJson(json);
            } else{
                item = createFileFromJson(json);
            }
            lst.add(item);
        }
        return lst;
    }

    private Error extractError(JSONObject json){
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

    private boolean checkForError(JSONObject json){
        int code = -1;
        try {
            code = json.getInt(RESPONSE_RESULT);
        } catch (JSONException e) {
            return true;
        }
        return code != 0;
    }


    public void register(String mail, String password, HandlerCallBack callback) {
        String parameters = ParameterHandler.parseRequest(new String[][]{
                {PARAM_MAIL, mail},
                {PARAM_PASSWORD, password},
                {PARAM_TERMSACCEPTED, "true"}
        });

        String final_uri = API_ENDPOINT + METHOD_REGISTER + parameters;

        http_handler.getRequest(final_uri, new HttpCallBack() {

            @Override
            public void onSuccess(JSONObject json) {
                if (checkForError(json)){
                    callback.onError(extractError(json));
                    return;
                }
                try {
                    Integer status_code = json.getInt(RESPONSE_RESULT);
                    callback.onSuccess(status_code);
                } catch (JSONException e) {
                    callback.onError(extractError(json));
                }
            }
        });
    }

    private void obtainDigest(HandlerCallBack callback) {
        String parameters = ParameterHandler.parseRequest(new String[][]{});

        String final_uri = API_ENDPOINT + METHOD_DIGEST + parameters;

        http_handler.getRequest(final_uri, new HttpCallBack() {
            public void onSuccess(JSONObject json) {
                if (checkForError(json)){
                    callback.onError(extractError(json));
                    return;
                }
                try {
                    String new_digest = json.getString(RESPONSE_DIGEST);
                    callback.onSuccess(new_digest);
                } catch (JSONException e) {
                    callback.onError(extractError(json));
                }
            }
        });
    }

    public void login(String username, String password, HandlerCallBack callback) {
        obtainDigest(new HandlerCallBack(){
            @Override
            public void onSuccess(Object obj) {
                String digest = (String)obj;

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

                String parameters = ParameterHandler.parseRequest(new String[][]{
                        {PARAM_USERNAME, username},
                        {PARAM_DIGEST, digest},
                        {PARAM_GET_AUTH, Integer.toString(1)},
                        {PARAM_PASSWORD_DIGEST, passworddigest}
                });

                String final_uri = API_ENDPOINT + METHOD_LOGIN_DIGEST + parameters;

                http_handler.getRequest(final_uri, new HttpCallBack() {

                    @Override
                    public void onSuccess(JSONObject json) {
                        if (checkForError(json)){
                            callback.onError(extractError(json));
                            return;
                        }
                        try {
                            auth_token = json.getString(RESPONSE_AUTH);
                            Integer status_code = json.getInt(RESPONSE_RESULT);
                            callback.onSuccess(status_code);
                        } catch (JSONException e) {
                            callback.onError(extractError(json));
                        }
                    }
                });
            }
            @Override
            public void onError(Error error){
                callback.onError(error);
            }

        });
    }

    public void recover_password(String mail, HandlerCallBack callback) {

        String parameters = ParameterHandler.parseRequest(new String[][]{
                {PARAM_MAIL, mail}
        });

        String final_uri = API_ENDPOINT + METHOD_LOST_PASSWORD + parameters;

        http_handler.getRequest(final_uri, new HttpCallBack() {

            @Override
            public void onSuccess(JSONObject json) {
                if (checkForError(json)){
                    callback.onError(extractError(json));
                    return;
                }
                try {
                    Integer status_code = json.getInt(RESPONSE_RESULT);
                    callback.onSuccess(status_code);
                } catch (JSONException e) {
                    callback.onError(extractError(json));
                }
            }
        });
    }

    public void folder_create(String folder_id, String name, HandlerCallBack callback) {
        String parameters = ParameterHandler.parseRequest(new String[][]{
                {PARAM_FOLDER_ID, folder_id},
                {PARAM_NAME, name},
                {PARAM_AUTH, this.auth_token}
        });

        String final_uri = API_ENDPOINT + METHOD_CREATE_FOLDER + parameters;

        http_handler.getRequest(final_uri, new HttpCallBack() {

            @Override
            public void onSuccess(JSONObject json) {
                if (checkForError(json)){
                    callback.onError(extractError(json));
                    return;
                }
                try {
                    Folder new_folder = createFolderFromJson(json.getJSONObject(RESPONSE_DATA));
                    callback.onSuccess(new_folder);
                } catch (JSONException e) {
                    callback.onError(extractError(json));
                }
            }
        });
    }

    public void folder_rename(String folder_id, String new_name, HandlerCallBack callback) {
        String parameters = ParameterHandler.parseRequest(new String[][]{
                {PARAM_FOLDER_ID, folder_id},
                {PARAM_TO_NAME, new_name},
                {PARAM_AUTH, this.auth_token}
        });

        String final_uri = API_ENDPOINT + METHOD_FOLDER_RENAME + parameters;

        http_handler.getRequest(final_uri, new HttpCallBack() {

            @Override
            public void onSuccess(JSONObject json) {
                if (checkForError(json)){
                    callback.onError(extractError(json));
                    return;
                }
                try {
                    Folder new_folder = createFolderFromJson(json.getJSONObject(RESPONSE_DATA));
                    callback.onSuccess(new_folder);
                } catch (JSONException e) {
                    callback.onError(extractError(json));
                }
            }
        });
    }

    public void folder_move(String folder_id, String new_parent_id, HandlerCallBack callback) {
        String parameters = ParameterHandler.parseRequest(new String[][]{
                {PARAM_FOLDER_ID, folder_id},
                {PARAM_TO_FOLDER_ID, new_parent_id},
                {PARAM_AUTH, this.auth_token}
        });

        String final_uri = API_ENDPOINT + METHOD_FOLDER_RENAME + parameters;

        http_handler.getRequest(final_uri, new HttpCallBack() {

            @Override
            public void onSuccess(JSONObject json) {
                if (checkForError(json)){
                    callback.onError(extractError(json));
                    return;
                }
                try {
                    Folder new_folder = createFolderFromJson(json.getJSONObject(RESPONSE_DATA));
                    callback.onSuccess(new_folder);
                } catch (JSONException e) {
                    callback.onError(extractError(json));
                }
            }
        });
    }

    public void folder_copy(String folder_id, String new_parent_id, HandlerCallBack callback) {
        String parameters = ParameterHandler.parseRequest(new String[][]{
                {PARAM_FOLDER_ID, folder_id},
                {PARAM_TO_FOLDER_ID, new_parent_id},
                {PARAM_AUTH, this.auth_token}
        });

        String final_uri = API_ENDPOINT + METHOD_FOLDER_COPY + parameters;

        http_handler.getRequest(final_uri, new HttpCallBack() {

            @Override
            public void onSuccess(JSONObject json) {
                if (checkForError(json)){
                    callback.onError(extractError(json));
                    return;
                }
                try {
                    Folder new_folder = createFolderFromJson(json.getJSONObject(RESPONSE_DATA));
                    callback.onSuccess(new_folder);
                } catch (JSONException e) {
                    callback.onError(extractError(json));
                }
            }
        });
    }


    public void folder_delete(String folder_id, HandlerCallBack callback) {
        String parameters = ParameterHandler.parseRequest(new String[][]{
                {PARAM_FOLDER_ID, folder_id},
                {PARAM_AUTH, this.auth_token}
        });

        String final_uri = API_ENDPOINT + METHOD_FOLDER_DELETE + parameters;

        http_handler.getRequest(final_uri, new HttpCallBack() {

            @Override
            public void onSuccess(JSONObject json) {
                if (checkForError(json)){
                    callback.onError(extractError(json));
                    return;
                }
                try {
                    Folder new_folder = createFolderFromJson(json.getJSONObject(RESPONSE_DATA));
                    callback.onSuccess(new_folder);
                } catch (JSONException e) {
                    callback.onError(extractError(json));
                }
            }
        });
    }

    public void folder_list(String folder_id, HandlerCallBack callback) {
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

        String final_uri = API_ENDPOINT + METHOD_FOLDER_LIST + parameters;

        http_handler.getRequest(final_uri, new HttpCallBack() {

            @Override
            public void onSuccess(JSONObject json) {
                if (checkForError(json)){
                    callback.onError(extractError(json));
                    return;
                }
                try {
                    Folder folder = createFolderFromJson(json.getJSONObject(RESPONSE_DATA));
                    ArrayList<Item> children = createChildrenFromJson(json.getJSONObject(RESPONSE_DATA).getJSONArray(RESPONSE_CONTENTS));
                    folder.setChildren(children);
                    callback.onSuccess(folder);
                } catch (JSONException e) {
                    callback.onError(extractError(json));
                }
            }
        });
    }

//    public void file_download(String folder_id, String file_name, String local_file_path, HandlerCallBack callback) {
////        TODO terminar. No sé descargar ficheros en http. Mirar: https://stackoverflow.com/questions/73759126/download-files-with-the-pcloud-api
//
//        String parameters = ParameterHandler.parseRequest(new String[][]{
//                {PARAM_FOLDER_ID, folder_id},
//                {PARAM_FILE_NAME, file_name},
//                {PARAM_AUTH, this.auth_token}
//        });
//
//        String final_uri = API_ENDPOINT + METHOD_FILE_DOWNLOAD + parameters;
//
//        http_handler.getRequest(final_uri, new HttpCallBack() {
//
//            @Override
//            public void onSuccess(JSONObject json) {
//                try {
//                    Folder new_folder = createFolderFromJson(json.getJSONObject(RESPONSE_DATA));
//                    callback.onSuccess(new_folder);
//                } catch (JSONException e) {
//                    callback.onError(extractError(json));
//                }
//            }
//        });
//    }

    public void file_upload(String folder_id, String file_name, String local_file_path, HandlerCallBack callback) {

//        TODO terminar. No sé subir ficheros en http. Mirar: https://docs.pcloud.com/structures/file_descriptors.html

        String parameters = ParameterHandler.parseRequest(new String[][]{
                {PARAM_FOLDER_ID, folder_id},
                {PARAM_FILE_NAME, file_name},
                {PARAM_AUTH, this.auth_token}
        });

        String final_uri = API_ENDPOINT + METHOD_FILE_UPLOAD + parameters;

        http_handler.postRequest(final_uri, local_file_path, new HttpCallBack() {

            @Override
            public void onSuccess(JSONObject json) {
                if (checkForError(json)){
                    callback.onError(extractError(json));
                    return;
                }
                try {
                    ArrayList<Item> children = createChildrenFromJson(json.getJSONArray(RESPONSE_DATA));
                    callback.onSuccess(children);
                } catch (JSONException e) {
                    callback.onError(extractError(json));
                }
            }
        });
    }


    public void file_copy(String file_id, String to_folder_id, String name, HandlerCallBack callback) {
        String parameters = ParameterHandler.parseRequest(new String[][]{
                {PARAM_FILE_ID, file_id},
                {PARAM_TO_FOLDER_ID, to_folder_id},
                {PARAM_TO_NAME, name},
                {PARAM_AUTH, this.auth_token}
        });

        String final_uri = API_ENDPOINT + METHOD_FILE_COPY + parameters;

        http_handler.getRequest(final_uri, new HttpCallBack() {

            @Override
            public void onSuccess(JSONObject json) {
                if (checkForError(json)){
                    callback.onError(extractError(json));
                    return;
                }
                try {
                    File new_file = createFileFromJson(json.getJSONObject(RESPONSE_DATA));
                    callback.onSuccess(new_file);
                } catch (JSONException e) {
                    callback.onError(extractError(json));
                }
            }
        });
    }


    public void file_delete(String file_id, HandlerCallBack callback) {
        String parameters = ParameterHandler.parseRequest(new String[][]{
                {PARAM_FILE_ID, file_id},
                {PARAM_AUTH, this.auth_token}
        });

        String final_uri = API_ENDPOINT + METHOD_FILE_DELETE + parameters;

        http_handler.getRequest(final_uri, new HttpCallBack() {

            @Override
            public void onSuccess(JSONObject json) {
                if (checkForError(json)){
                    callback.onError(extractError(json));
                    return;
                }
                try {
                    File new_file = createFileFromJson(json.getJSONObject(RESPONSE_DATA));
                    callback.onSuccess(new_file);
                } catch (JSONException e) {
                    callback.onError(extractError(json));
                }
            }
        });
    }

    public void file_rename(String file_id, String new_name, HandlerCallBack callback) {
        this.file_move(file_id, file_id, new_name, callback);
    }

    public void file_move(String file_id, String to_folder_id, String new_name, HandlerCallBack callback) {
        String parameters = ParameterHandler.parseRequest(new String[][]{
                {PARAM_FILE_ID, file_id},
                {PARAM_TO_FOLDER_ID, to_folder_id},
                {PARAM_TO_NAME, new_name},
                {PARAM_AUTH, this.auth_token}
        });

        String final_uri = API_ENDPOINT + METHOD_FILE_RENAME + parameters;

        http_handler.getRequest(final_uri, new HttpCallBack() {

            @Override
            public void onSuccess(JSONObject json) {
                if (checkForError(json)){
                    callback.onError(extractError(json));
                    return;
                }
                try {
                    File new_file = createFileFromJson(json.getJSONObject(RESPONSE_DATA));
                    callback.onSuccess(new_file);
                } catch (JSONException e) {
                    callback.onError(extractError(json));
                }
            }
        });
    }

    public void file_stat(String file_id, HandlerCallBack callback) {

        String parameters = ParameterHandler.parseRequest(new String[][]{
                {PARAM_FILE_ID, file_id},
                {PARAM_AUTH, this.auth_token}
        });

        String final_uri = API_ENDPOINT + METHOD_FILE_STAT + parameters;

        http_handler.getRequest(final_uri, new HttpCallBack() {

            @Override
            public void onSuccess(JSONObject json) {
                if (checkForError(json)){
                    callback.onError(extractError(json));
                    return;
                }
                try {
                    File new_file = createFileFromJson(json.getJSONObject(RESPONSE_DATA));
                    callback.onSuccess(new_file);
                } catch (JSONException e) {
                    callback.onError(extractError(json));
                }
            }
        });
    }
}

