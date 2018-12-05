package com.khong.qrwarrentyreminder;

public class RegisterItem {
    String uid,itemName,serialNumber,itemColour,itemWarrentyPeriod,itemPurchaseDate;

    public RegisterItem(){

    }

    public RegisterItem(String uid,String itemName,String serialNumber,String itemColour,String itemWarrentyPeriod,String itemPurchaseDate){
        this.uid = uid;
        this.itemName = itemName;
        this.serialNumber = serialNumber;
        this.itemColour = itemColour;
        this.itemWarrentyPeriod = itemWarrentyPeriod;
        this.itemPurchaseDate = itemPurchaseDate;
    }

    public String getUid(){return uid;}
    public String getItemName(){return itemName;}
    public String getSerialNumber(){return serialNumber;}
    public String getItemColour(){return itemColour;}
    public String getItemWarrentyPeriod(){return itemWarrentyPeriod;}
    public String getItemPurchaseDate(){return itemPurchaseDate;}

}
