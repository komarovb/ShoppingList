package com.example.borys.shoppinglist.data;

/**
 * Created by Borys on 11/6/16.
 */

public class ShoppingListItem {
    public String imageName;
    public String title;
    public String text;
    public Boolean checked;

    public static ShoppingListItem createNewInstance(String title, String text, String imageName, Boolean checked){
        return new ShoppingListItem(title,text,imageName, checked);
    }
    public static ShoppingListItem createNewInstance(String title, String text){
        return new ShoppingListItem(title,text, "", false);
    }



    public ShoppingListItem(String title, String text, String imageName,Boolean checked){
        this.title=title;
        this.text=text;
        this.imageName=imageName;
        this.checked=checked;
    }
    public void setImage(String imagePath){
        this.imageName = imagePath;
    }

}
