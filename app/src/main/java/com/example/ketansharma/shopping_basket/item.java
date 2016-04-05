package com.example.ketansharma.shopping_basket;

import android.content.Context;

import java.util.ArrayList;

/**
 * Created by ketan.sharma.
 * Class to get and set shopping items when
 * retrieving rows from SQLite
 */
public class item {
    int iID = 0;
    String name = "";
    double unit_price = 0;
    String unit = "";
    int basket_qty = 0;

    int getID() {
        return	this.iID;
    }

    void setID(int value) {
        this.iID = value;
    }

    String getName() {
        return	this.name;
    }

    void setName(String value) {
        this.name = value;
    }

    double getUnitPrice() {
        return	this.unit_price;
    }

    void setUnitPrice(double value) {
        this.unit_price = value;
    }

    String getUnit() {
        return	this.unit;
    }

    void setUnit(String value) {
        this.unit = value;
    }

    int getBasketQty() {
        return	this.basket_qty;
    }

    void setBasketQty(int value) {
        this.basket_qty = value;
    }

    /**Retrieves all products (items) from the SQLite
     * database and adds them to an array.
     * If this is the first time the app is being run,
     * populate the database with 4 products with their
     * GBP values.
     * This array is called on the front-end to populate the listviews.
     */
    ArrayList<item> getItems(Context c, String sWhereClause) {
        item_database item_db = new item_database(c);
        if (item_db.getCount() == 0) {
            item_db.insert("Peas", 0.95, "per bag", 0);
            item_db.insert("Eggs", 2.10, "per dozen", 0);
            item_db.insert("Milk", 1.30, "per bottle", 0);
            item_db.insert("Beans", 0.73, "per can", 0);
        }

        return item_db.getArray(sWhereClause);
    }
}
