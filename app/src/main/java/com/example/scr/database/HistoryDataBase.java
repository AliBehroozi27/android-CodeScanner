package com.example.scr.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.scr.modules.Record;

import java.util.ArrayList;


public class HistoryDataBase extends SQLiteOpenHelper {
    private static String DATABASE_NAME = "Records",
            TABLE_NAME = "Record",
            ID = "id",
            TITLE = "title",
            DATE = "date",
            COUNTRY = "country",
            COUNTRYCODE = "country_code",
            BARCODETYPE = "barcode_type",
            VALUEFORMAT = "value_format";
    private static Integer DATABASE_VERSION = 1;
    private ArrayList<Record> records;
    private Cursor cursor;
    private Context context;
    private int lastId;
    private static final String FAVORITE = "favorite", EXACT = "EXACT";

    public HistoryDataBase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (" + ID + " INTEGER ,"
                + TITLE + " text ,"
                + VALUEFORMAT + " text ,"
                + BARCODETYPE + " text ,"
                + DATE + " text ,"
                + COUNTRYCODE + " text,"
                + COUNTRY + " text );");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Records");
        onCreate(db);
    }

    public void addRecord(Record record) {
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + ID + "= (SELECT MAX(ID)FROM " + TABLE_NAME + ")", null);
        if (cursor.moveToFirst()) {
            do {
                lastId = cursor.getInt(0);
            } while (cursor.moveToNext());
        }

        ContentValues values = new ContentValues();
        values.put(ID, lastId + 1);
        values.put(TITLE, record.title);
        values.put(DATE, record.date);
        values.put(BARCODETYPE, record.barcodeType);
        values.put(VALUEFORMAT, record.valueFormat);
        values.put(COUNTRYCODE, record.countryCode);
        values.put(COUNTRY, record.country);
        database.insert(TABLE_NAME, null, values);
        cursor.close();
        database.close();
    }

    public void removeRecords(ArrayList<Record> records, boolean removeAllFlag) {
        SQLiteDatabase database = this.getWritableDatabase();
        if (removeAllFlag) {
            for (int i = 0; i < getDatabaseSize(); i++) {
                database.delete(TABLE_NAME, null, null);
            }
        } else {
            for (int i = 0; i < records.size(); i++) {
                int id = records.get(i).id;
                database.delete(TABLE_NAME, ID + "= " + id, null);
            }
        }
        database.close();
    }


    public ArrayList<Record> getRecords() {
        records = new ArrayList<Record>();
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        Record record;
        if (cursor.moveToFirst()) {
            do {
                record = new Record(0, null, null, null, null, null, null);
                record.id = cursor.getInt(0);
                record.title = cursor.getString(1);
                record.valueFormat = cursor.getString(2);
                record.barcodeType = cursor.getString(3);
                record.date = cursor.getString(4);
                record.country = cursor.getString(6);
                record.countryCode = cursor.getString(5);
                records.add(record);
            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
        return records;
    }


    public int getDatabaseSize() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT  * FROM " + TABLE_NAME, null);
        int count = cursor.getCount();
        cursor.close();
        //db.close();
        return count;
    }

}

