package com.dam.pcloud;

import com.dam.pcloud.rest.PCloudItem;

import java.io.Serializable;

public class ListItem implements Serializable {

    private int imgLeft;
    private String textItem;
    private int imgPoints;

    private PCloudItem pcloudItem;

    public  ListItem(int imgLeft, PCloudItem pcloudItem, int imgPoints) {
        this.imgLeft = imgLeft;
        this.textItem = pcloudItem.getName();
        this.imgPoints = imgPoints;
        this.pcloudItem = pcloudItem;
    }

    public int getImgLeft() {
        return imgLeft;
    }
    public void setImgLeft(int imgLeft) {
        this.imgLeft = imgLeft;
    }

    public String getTextItem() {
        return textItem;
    }
    public void setTextItem(String textItem) {
        this.textItem = textItem;
    }

    public int getImgPoints() {
        return imgPoints;
    }
    public void setImgPoints(int imgPoints) {
        this.imgPoints = imgPoints;
    }

    public PCloudItem getPcloudItem() {
        return pcloudItem;
    }

    public void setPcloudItem(PCloudItem pcloudItem) {
        this.pcloudItem = pcloudItem;
    }

    public void setPcloudItemAndText(PCloudItem pcloudItem) {
        this.pcloudItem = pcloudItem;
        this.textItem = pcloudItem.getName();
    }
}
