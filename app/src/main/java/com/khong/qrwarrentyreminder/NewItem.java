package com.khong.qrwarrentyreminder;

public class NewItem {
    String registerItemDescription;//Name of Item
    String uid;//User unique id

    public NewItem(){

    }

    public NewItem(String uid, String registerItemDescription) {
        this.uid = uid;
        this.registerItemDescription = registerItemDescription;
    }

    public String getItemDescription(){
        return registerItemDescription;
    }
}
