package com.dam.pcloud.rest;

import com.android.volley.RequestQueue;

import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PcloudRestHandler {
    private static final String API_BASE = "https://api.pcloud.com/";
    private static final String API_PERSONAL = "https://my.pcloud.com/";
    private static String API_ENDPOINT = null;

    private static final String METHOD_OAUTH2_TOKEN = "oauth2_token";
    private static final String METHOD_OAUTH2_CODE = "oauth2/authorize";
    private static final String METHOD_REGISTER = "register";

    private static final String PARAM_CLIENT_ID = "clientid";
    private static final String PARAM_RESPONSE_TYPE = "response_type";
    private static final String PARAM_CLIENT_SECRET = "client_secret";
    private static final String PARAM_CODE = "code";
    private static final String PARAM_MAIL = "mail";
    private static final String PARAM_PASSWORD = "password";
    private static final String PARAM_TERMSACCEPTED = "termsaccepted";




    private String client_id;
    private String client_secret;
    private String oauth_token;

    private final HttpHandler http_handler;


    public PcloudRestHandler(RequestQueue queue){
        http_handler = new HttpHandler(queue);
    }

    public void configure(String client_id, String client_secret){
        this.client_id = client_id;
        this.client_secret = client_secret;
    }

    private String obtainOauthCode() throws InterruptedException {
        /* Obtiene el código oauth que luego se usa para generar el token

        @throws InterruptedException si se ha producido alguna excepción mientras se produce la petición
        @return     codigo oauth
         */
        String oauth_code = "";
        SyncObject<String> sync_code = new SyncObject<String>(oauth_code);
        String parameters = ParameterHandler.parseRequest(new String[][]{
                {PARAM_CLIENT_ID, client_id},
                {PARAM_RESPONSE_TYPE, "code"}
        });
        String final_uri = API_PERSONAL + METHOD_OAUTH2_CODE + parameters;

        http_handler.getRequest(final_uri, new HttpCallBack() {

            @Override
            public void onSuccess(JSONObject json) {
//                TODO completar. se necesita hacer scrapping
                String new_oauth_code = "";
                API_ENDPOINT = "";
                sync_code.set(new_oauth_code);
                sync_code.notify();
            }
        });
        sync_code.wait();
        return sync_code.get();
    }

    public String obtainOauthToken() throws InterruptedException {
        /* Obtiene el token que se usará luego en la autenticación

        @throws InterruptedException si se ha producido alguna excepción mientras se produce la petición
        @return     codigo oauth
         */
        String code = obtainOauthCode();
        String oauth_token = "";
        SyncObject<String> sync_code = new SyncObject<String>(oauth_token);
        String parameters = ParameterHandler.parseRequest(new String[][]{
                {PARAM_CLIENT_ID, client_id},
                {PARAM_CLIENT_SECRET, client_secret},
                {PARAM_CODE, code}
        });
        String final_uri = API_ENDPOINT + METHOD_OAUTH2_TOKEN + parameters;

        http_handler.getRequest(final_uri, new HttpCallBack() {

            @Override
            public void onSuccess(JSONObject json) {
//                TODO completar
                String new_oauth_token = "";
                sync_code.set(new_oauth_token);
                sync_code.notify();
            }
        });
        sync_code.wait();
        return sync_code.get();
    }

    public int register(String mail, String password) throws InterruptedException {
        /* Registra un usuario

        @param mail correo electronico del usuario
        @param password contraseña del usuario
        @return codigo de error
        @see Consts#StatusCode
        */

        int code = 0;
        SyncObject<Integer> sync_code = new SyncObject<Integer>(code);

        String parameters = ParameterHandler.parseRequest(new String[][]{
                {PARAM_MAIL, mail},
                {PARAM_PASSWORD, password},
                {PARAM_TERMSACCEPTED, "true"}
        });

        String final_uri = API_ENDPOINT + METHOD_REGISTER + parameters;

        http_handler.getRequest(final_uri, new HttpCallBack() {

            @Override
            public void onSuccess(JSONObject json) {
//                TODO completar
                int new_code = 0;
                sync_code.set(new_code);
                sync_code.notify();
            }
        });
        sync_code.wait();
        return sync_code.get();
    }

}



/*

NO ECHAR CUENTA

 */




//    public void login(){
//        // Cargar
//        Properties properties = loadLoginData();
//
//        String clientId = properties.getProperty("clientid");
//        String clientSecret = properties.getProperty("clientsecret");
//
////        ApiClient apiClient = PCloudSdk.newClientBuilder()
////                .authenticator(Authenticators.newOAuthAuthenticator("<your OAuth access token here>"))
////                .apiHost()
////                .create();
//
////        ApiClient apiClient = PCloudSdk.newClientBuilder()
////                .authenticator(Authenticators.newOAuthAuthenticator(""))
////                .apiHost()
////                .create();
//
//
//
//    }
//
//    private Properties loadLoginData(){
//        Properties properties = new Properties();
//        try {
//            InputStream inputStream = new FileInputStream("local.properties");
//            properties.load(inputStream);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return properties;
//    }