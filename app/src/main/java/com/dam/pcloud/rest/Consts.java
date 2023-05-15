package com.dam.pcloud.rest;

public class Consts {
    public enum StatusCode{
//        TODO faltan códigos de error. URL: https://docs.pcloud.com/methods/auth/changepassword.html
        SUCCESS(0, "Success"),
        LOGIN_REQUIRED(1000, "Log in required"),
        MISSING_MAIL(1033, "Please provide 'mail'"),
        ;

        private int code;
        private String description;

        private StatusCode (int code, String description){
            this.code = code;
            this.description = description;
        }
    }
}