package com.example.scr.adapters;

import android.app.Activity;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.scr.R;
import com.example.scr.modules.Record;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ListViewAdapter extends ArrayAdapter<Record> {

    private final AssetManager assets;
    Activity context;
    ArrayList<Record> records;
    private SparseBooleanArray mSelectedItemsIds;
    private ArrayList<Record> selectedItems = new ArrayList<Record>();
    private ArrayList<Integer> selectedItemIdsInLV = new ArrayList<Integer>();
    private Record record;
    private boolean iconFlag;


    public ListViewAdapter(Activity context, int resId, ArrayList<Record> records, AssetManager assetManager) {
        super(context, resId, records);
        mSelectedItemsIds = new SparseBooleanArray();
        this.context = context;
        this.records = records;
        this.assets = assetManager;
    }

    private class ViewHolder {
        TextView title, date;
        ImageView icon;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_view, null);
            holder = new ViewHolder();
            holder.title = (TextView) convertView.findViewById(R.id.tv_title);
            holder.date = (TextView) convertView.findViewById(R.id.tv_date);
            holder.icon = (ImageView) convertView.findViewById(R.id.iv_icon);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        record = getItem(position);
        holder.title.setText(record.title.trim());
        holder.date.setText(record.date);
        try {
            InputStream ims = assets.open("barcode_type_icons/barcode.png");
            Drawable d = Drawable.createFromStream(ims, null);
            holder.icon.setImageDrawable(d);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (record.barcodeType.equals("DATA_MATRIX") || record.barcodeType.equals("QR_CODE")) {
            try {
                InputStream ims = assets.open("barcode_type_icons/qrcode.png");
                Drawable d = Drawable.createFromStream(ims, null);
                holder.icon.setImageDrawable(d);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        switch (record.valueFormat) {
            case "GEO":
                try {
                    InputStream ims = assets.open("barcode_type_icons/geo.png");
                    Drawable d = Drawable.createFromStream(ims, null);
                    holder.icon.setImageDrawable(d);
                    iconFlag = true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case "wifi":
                try {
                    InputStream ims = assets.open("barcode_type_icons/wifi.png");
                    Drawable d = Drawable.createFromStream(ims, null);
                    holder.icon.setImageDrawable(d);
                    iconFlag = true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case "url":
                try {
                    InputStream ims = assets.open("barcode_type_icons/url.png");
                    Drawable d = Drawable.createFromStream(ims, null);
                    holder.icon.setImageDrawable(d);
                    iconFlag = true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }

        convertView.setBackgroundColor(mSelectedItemsIds.get(position) ? 0x9934B5E4 : Color.TRANSPARENT);
        return convertView;
    }

    @Override
    public void add(Record record) {
        records.add(record);
        notifyDataSetChanged();
        Toast.makeText(context, records.toString(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void remove(Record object) {
        records.remove(object);
        notifyDataSetChanged();
    }

    public List<Record> getRecords() {
        return records;
    }

    public void toggleSelection(int position) {
        selectView(position, !mSelectedItemsIds.get(position));
    }

    public void removeSelection() {
        mSelectedItemsIds = new SparseBooleanArray();
        notifyDataSetChanged();
    }

    public void selectView(int position, boolean value) {
        if (value) {
            selectedItems.add(records.get(position));
            selectedItemIdsInLV.add(position);
            mSelectedItemsIds.put(position, value);
        } else {
            selectedItems.remove(records.get(position));
            selectedItemIdsInLV.remove((Object) position);
            mSelectedItemsIds.delete(position);
        }

        notifyDataSetChanged();
    }

    public int getSelectedCount() {
        return mSelectedItemsIds.size();
    }

    public SparseBooleanArray getSelectedIds() {
        return mSelectedItemsIds;
    }

    public void emptyselected(){
        selectedItems = new ArrayList<Record>();
        selectedItemIdsInLV = new ArrayList<Integer>();
    }

    public ArrayList<Record> getSelectedItems() {
        return selectedItems;
    }

    public ArrayList<Integer> getSelectedItemIds() {
        return selectedItemIdsInLV;
    }

}
