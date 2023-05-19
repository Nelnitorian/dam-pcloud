package com.dam.pcloud.rest;

public class ParameterHandler {
    private static final String TEMPLATE_PARAM = "%s=%s&";
    private static final String TEMPLATE_START = "?";

    public static String parseRequest(String[][] full_params){
        StringBuilder request = new StringBuilder();
        request.append(TEMPLATE_START);

        for (String[] params : full_params){
            request.append(String.format(TEMPLATE_PARAM, params[0], params[1]));
        }

        if (request.length() > 1){
//            Teníamos parámetros
//            Quitamos el último carácter (un "&")
            request.deleteCharAt(request.length()-1);
        } else {
//            No teníamos parámetros
//            Quitamos toda la cadena (solo compuesta por "?")
            request.setLength(0);
        }
        return request.toString();
    }
}
