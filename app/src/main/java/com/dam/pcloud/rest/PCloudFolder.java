package com.dam.pcloud.rest;

import java.util.ArrayList;

public class PCloudFolder extends PCloudItem {
    ArrayList<PCloudItem> children;

    public PCloudFolder(){
        children = new ArrayList<PCloudItem>();
    }

    public PCloudFolder(String id, String parent_id, String name, ItemType type, ArrayList<PCloudItem> children){
        this.id = id;
        this.parent_id = parent_id;
        this.name = name;
        this.type = type;
        this.children = children;
    }

    public PCloudFolder(String id, String parent_id, String name, ItemType type){
        this.id = id;
        this.parent_id = parent_id;
        this.name = name;
        this.type = type;
        this.children = new ArrayList<PCloudItem>();
    }

    public ArrayList<PCloudItem> getChildren() {
        return children;
    }

    public void setChildren(ArrayList<PCloudItem> children) {
        this.children = children;
    }
}
