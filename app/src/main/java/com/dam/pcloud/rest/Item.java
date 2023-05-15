package com.dam.pcloud.rest;

public class Item {
    public enum ItemType{
        FOLDER(1),
        FILE(2),
        ;

        private int type;

        private ItemType (int type){
            this.type = type;
        }
    }
    private String id;
    private ItemType type;


}
