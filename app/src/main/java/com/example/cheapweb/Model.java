package com.example.cheapweb;

public class Model {

    String idItem, imagePath, itemName, itemPrice, itemInfo, link1, link2 ,link3 ,category;
    int priceInLink1, priceInLink2, priceInLink3;

    public Model(){

    }

    public Model(String idItem, String imagePath, String itemName, String category, String itemPrice, String itemInfo,
                 String link1, String link2, String link3, int priceInLink1, int priceInLink2, int priceInLink3) {
        this.idItem = idItem;
        this.imagePath = imagePath;
        this.itemName = itemName;
        this.itemPrice = itemPrice;
        this.itemInfo = itemInfo;
        this.link1 = link1;
        this.link2 = link2;
        this.link3 = link3;
        this.priceInLink1 = priceInLink1;
        this.priceInLink2 = priceInLink2;
        this.priceInLink3 = priceInLink3;
        this.category=category;
    }

    public String getIdItem() {
        return idItem;
    }

    public void setIdItem(String idItem) {
        this.idItem = idItem;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(String itemPrice) {
        this.itemPrice = itemPrice;
    }

    public String getItemInfo() {
        return itemInfo;
    }

    public void setItemInfo(String itemInfo) {
        this.itemInfo = itemInfo;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getLink1() {
        return link1;
    }

    public void setLink1(String link1) {
        this.link1 = link1;
    }

    public String getLink2() {
        return link2;
    }

    public void setLink2(String link2) {
        this.link2 = link2;
    }

    public String getLink3() {
        return link3;
    }

    public void setLink3(String link3) {
        this.link3 = link3;
    }

    public int getPriceInLink1() {
        return priceInLink1;
    }

    public void setPriceInLink1(int priceInLink1) {
        this.priceInLink1 = priceInLink1;
    }

    public int getPriceInLink2() {
        return priceInLink2;
    }

    public void setPriceInLink2(int priceInLink2) {
        this.priceInLink2 = priceInLink2;
    }

    public int getPriceInLink3() {
        return priceInLink3;
    }

    public void setPriceInLink3(int priceInLink3) {
        this.priceInLink3 = priceInLink3;
    }
}
