package com.example.scr.modules;

import java.io.Serializable;

public class Record implements Serializable {
    public int id;
    public String title, date, valueFormat, barcodeType, country, countryCode;

    public Record(int id, String title, String valueFormat, String barcodeType, String date, String country, String countryCode) {
        this.id = id;
        this.title = title;
        this.date = date;
        this.barcodeType = barcodeType;
        this.valueFormat = valueFormat;
        this.country = country;
        this.countryCode = countryCode;
    }
}
