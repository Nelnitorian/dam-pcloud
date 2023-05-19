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

    public String getTextItem() {
        return textItem;
    }

    public int getImgPoints() {
        return imgPoints;
    }


}
