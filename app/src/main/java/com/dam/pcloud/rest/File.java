package com.dam.pcloud.rest;

public class File extends Item{
    private String size;

    public File(){

    }

    public File(String id, String parent_id, String name, String size, ItemType type){
        this.id = id;
        this.parent_id = parent_id;
        this.name = name;
        this.size = size;
        this.type = type;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }
}
