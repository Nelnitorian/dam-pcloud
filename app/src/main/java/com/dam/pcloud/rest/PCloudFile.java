package com.dam.pcloud.rest;

public class PCloudFile extends PCloudItem {
    private String size;

    public PCloudFile(){

    }

    public PCloudFile(String id, String parent_id, String name, String size, ItemType type){
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
