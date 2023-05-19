package com.dam.pcloud;

import java.io.Serializable;

public class ListItem implements Serializable {

    private int imgLeft;
    private String textItem;
    private int imgPoints;

    public ListItem(int imgLeft, String textItem, int imgPoints) {
        this.imgLeft = imgLeft;
        this.textItem = textItem;
        this.imgPoints = imgPoints;
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


}
