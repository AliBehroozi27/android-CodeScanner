package com.example.scr.activities;

import android.content.ClipboardManager;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.scr.R;
import com.example.scr.adapters.ListViewAdapter;
import com.example.scr.database.HistoryDataBase;
import com.example.scr.modules.Record;

import java.util.ArrayList;
import java.util.Collections;

public class HistoryActivity extends AppCompatActivity implements ActionMode.Callback {

    private static final String RESULT = "result";
    private static final String TITLE = "title";
    private HistoryDataBase dataBase;
    private ArrayList<Record> records;
    private ActionMode mActionMode;
    private ClipboardManager clipboard;
    private TextView tvNoRecord;
    private LinearLayoutManager layoutManager;
    private ListView lv;
    private ListViewAdapter lvAdapter;
    private Toolbar toolbar;
    private Intent intent;
    private String data;
    private String recordsContent;
    private Record record;
    private String bigData;
    private AssetManager assetManager;
    private ActionMode actionMode;
    private ArrayList<Record> tmpRecords;
    private ArrayList<Integer> selectedIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        //initial views
        tvNoRecord = (TextView) findViewById(R.id.tv_no_record);

        //database
        dataBase = new HistoryDataBase(this);

        //handle recycler_view
        records = dataBase.getRecords();

        assetManager = getAssets();

        lv = (ListView) findViewById(R.id.lv);
        lvAdapter = new ListViewAdapter(this, R.layout.item_view, records, assetManager);
        lv.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
        lv.setAdapter(lvAdapter);

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                actionMode = startActionMode(HistoryActivity.this);
                view.setSelected(true);
                lvAdapter.toggleSelection(i);
                actionMode.setTitle(lvAdapter.getSelectedCount() + " items");
                return true;
            }
        });

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if (actionMode != null) {
                    view.setSelected(true);
                    lvAdapter.toggleSelection(position);
                    if (lvAdapter.getSelectedCount() == 0) {
                        actionMode.finish();
                    } else
                        actionMode.setTitle(lvAdapter.getSelectedCount() + " items");

                } else {
                    intent = new Intent(getApplicationContext(), ShowResultActivity.class);
                    intent.putExtra(RESULT, records.get(position));
                    startActivity(intent);
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.history_menu, menu);
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.delete:
                dataBase.removeRecords(null, true);
                records.clear();
                lvAdapter.notifyDataSetChanged();
                return true;
            case R.id.share:
                recordsContent = getRecordContent(dataBase.getRecords(), 0, dataBase.getDatabaseSize(), getIdRange(0, dataBase.getDatabaseSize()));
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, recordsContent);
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private ArrayList<Integer> getIdRange(int s, int e) {
        ArrayList<Integer> ids = new ArrayList<Integer>();
        for (int i = s; i < e; i++) {
            ids.add(i);
        }
        return ids;
    }

    private String getRecordContent(ArrayList<Record> records, int s, int e, ArrayList<Integer> ids) {
        bigData = "";
        for (int i = s; i < e; i++) {
            if (ids.contains(i)) {
                record = records.get(i);
                bigData += "record " + i + " :";
                bigData += "\n";
                data = "";
                data += TITLE + ":   " + record.title + "\n";
                if (record.valueFormat != null && !record.valueFormat.equals("")) {
                    data += "Type :      " + record.valueFormat + "\n";
                }
                if (record.barcodeType != null && !record.barcodeType.equals("")) {
                    data += "format :     " + record.barcodeType + "\n";
                }
                if (record.country != null && !record.country.equals("")) {
                    data += "country :     " + record.country + "\n";
                }
                if (record.date != null && !record.date.equals("")) {
                    data += "date :       " + record.date + "\n";
                }
                bigData += data;
                bigData += "------";
                bigData += "\n";
            }
        }
        return bigData;
    }

    @Override
    public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
        MenuInflater inflater = actionMode.getMenuInflater();
        inflater.inflate(R.menu.history_context_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.share:
                selectedIds = lvAdapter.getSelectedItemIds();
                Collections.sort(selectedIds);
                recordsContent = getRecordContent(dataBase.getRecords(),
                        selectedIds.get(0),
                        selectedIds.get(selectedIds.size() - 1) + 1,
                        selectedIds);
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, recordsContent);
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
                actionMode.finish(); // Action picked, so close the CAB
                return true;
            case R.id.delete:
                dataBase.removeRecords(lvAdapter.getSelectedItems(), false);
                records.clear();
                tmpRecords = dataBase.getRecords();
                records.addAll(tmpRecords);
                lvAdapter.notifyDataSetChanged();
                actionMode.finish(); // Action picked, so close the CAB

                return true;
            default:
                return false;
        }
    }


    @Override
    public void onDestroyActionMode(ActionMode actionMode) {
        lvAdapter.notifyDataSetChanged();
        this.actionMode = null;
        lvAdapter.removeSelection();
        lvAdapter.emptyselected();
    }

}

