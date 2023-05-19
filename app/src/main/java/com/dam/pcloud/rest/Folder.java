package com.dam.pcloud.rest;

import org.json.JSONObject;

import java.sql.Array;
import java.util.ArrayList;

public class Folder extends Item{
    ArrayList<Item> children;

    public Folder(){
        children = new ArrayList<Item>();
    }

    public Folder(String id, String parent_id, String name, ItemType type, ArrayList<Item> children){
        this.id = id;
        this.parent_id = parent_id;
        this.name = name;
        this.type = type;
        this.children = children;
    }

    public Folder(String id, String parent_id, String name, ItemType type){
        this.id = id;
        this.parent_id = parent_id;
        this.name = name;
        this.type = type;
        this.children = new ArrayList<Item>();
    }

    public ArrayList<Item> getChildren() {
        return children;
    }

    public void setChildren(ArrayList<Item> children) {
        this.children = children;
    }
}
