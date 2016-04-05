package com.example.ketansharma.shopping_basket;

/**
 * Created by ketan.sharma.
 * Class to create, insert and update the SQLite database.
 * All database functions are executed here.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class item_database extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "items.mka";
    public static final String DATABASE_TABLE = "items";
    public static final int DATABASE_VERSION = 1;

    public static final String KEY_UID = "_id";
    public static final String KEY_NAME = "name";
    public static final String KEY_UNIT_PRICE = "unit_price";
    public static final String KEY_UNIT = "unit";
    public static final String KEY_BASKET_QTY = "basket_qty";

    String GET_DATABASE_VALUES = "select " + KEY_UID + ", "
            + KEY_NAME + ", " + KEY_UNIT_PRICE + ", " + KEY_UNIT + ", " + KEY_BASKET_QTY
            + " From " + DATABASE_TABLE;

    public item_database(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL("CREATE TABLE IF NOT EXISTS " + DATABASE_TABLE + " (" + KEY_UID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + KEY_NAME + ", " + KEY_UNIT_PRICE + ", " + KEY_UNIT + ", " + KEY_BASKET_QTY + ");");
        } catch (Exception e) {

        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
        onCreate(db);
    }

    public Boolean insert(String name, double unit_price, String unit,
                          int basket_qty) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(KEY_NAME, name);
            contentValues.put(KEY_UNIT_PRICE, unit_price);
            contentValues.put(KEY_UNIT, unit);
            contentValues.put(KEY_BASKET_QTY, basket_qty);

            db.insert(DATABASE_TABLE, null, contentValues);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Cursor getData(){
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            String GET_DATABASE_VALUES = "select " + KEY_UID + ", "
                    + KEY_NAME + ", " + KEY_UNIT_PRICE + ", " + KEY_UNIT + ", " + KEY_BASKET_QTY
                    + " From " + DATABASE_TABLE;

            Cursor res = db.rawQuery(GET_DATABASE_VALUES, null);
            return res;
        } catch (Exception e) {
            return null;
        }
    }

    public Cursor getData(String key, String value){
        try {
            SQLiteDatabase db = this.getReadableDatabase();

            Cursor res = db.rawQuery(GET_DATABASE_VALUES + " where " + key + "=" + value, null);
            return res;
        } catch (Exception e) {
            return null;
        }
    }

    public int getInteger(int id, String value){
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            int result = 0;
            Cursor res = db.rawQuery(GET_DATABASE_VALUES + " where " + KEY_UID + " = " + id, null);

            res.moveToFirst();

            while (res.isAfterLast() == false) {
                result = Integer.valueOf(res.getString(Integer.valueOf(res.getColumnIndex(value))));

                res.moveToNext();
            }
            return result;
        } catch (Exception e) {
            return 0;
        }
    }

    public String getString(int id, String value){
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            String result = "";
            Cursor res = db.rawQuery(GET_DATABASE_VALUES + " where " + KEY_UID + " = " + id, null);

            res.moveToFirst();

            while (res.isAfterLast() == false) {
                result = res.getString(res.getColumnIndex(value));

                res.moveToNext();
            }
            return result;
        } catch (Exception e) {
            return null;
        }
    }

    public int getCount(){
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            int numRows = (int) DatabaseUtils.queryNumEntries(db, DATABASE_TABLE);
            return numRows;
        } catch (Exception e) {
            return 0;
        }
    }

    public boolean update (int UID, int basket_qty) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(KEY_BASKET_QTY, basket_qty);
            db.update(DATABASE_TABLE, contentValues, KEY_UID + "=" + UID, null);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Integer delete (int id)
    {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            return db.delete(DATABASE_TABLE,
                    "id = ? ",
                    new String[]{Integer.toString(id)});
        } catch (Exception e) {
            return 0;
        }
    }

    //Gets all rows from SQLite and returns them in an Array
    public ArrayList<item> getArray(String sWhereClause) {
        ArrayList<item> items = new ArrayList<item>();

        try {
            SQLiteDatabase db = this.getReadableDatabase();

            String SQL = GET_DATABASE_VALUES;
            if (!sWhereClause.equals("")) {
                SQL += " WHERE " + sWhereClause;
            }

            Cursor res = db.rawQuery(SQL, null);
            res.moveToFirst();

            while (res.isAfterLast() == false) {
                item Item = new item();
                Item.setID(Integer.valueOf(res.getString(res.getColumnIndex(KEY_UID))));
                Item.setName(res.getString(res.getColumnIndex(KEY_NAME)));
                Item.setUnitPrice(Double.valueOf(res.getString(res.getColumnIndex(KEY_UNIT_PRICE))));
                Item.setUnit(res.getString(res.getColumnIndex(KEY_UNIT)));
                Item.setBasketQty(Integer.valueOf(res.getString(res.getColumnIndex(KEY_BASKET_QTY))));
                items.add(Item);

                res.moveToNext();
            }
        } catch (Exception e) {

        }

        return items;
    }
}
