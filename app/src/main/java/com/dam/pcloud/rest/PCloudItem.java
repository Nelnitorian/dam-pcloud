package com.dam.pcloud.rest;

public abstract class PCloudItem {
    public enum ItemType{
        FOLDER(1),
        FILE(2),
        AUDIO(3),
        IMAGE(4),
        ;

        private int type;

        private ItemType (int type){
            this.type = type;
        }
    }

    ItemType type;
    String id;
    String parent_id;
    String name;

    public ItemType getType() {
        return type;
    }

    public void setType(ItemType type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParent_id() {
        return parent_id;
    }

    public void setParent_id(String parent_id) {
        this.parent_id = parent_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
