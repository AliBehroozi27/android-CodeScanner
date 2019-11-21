package com.example.scr.activities;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.ClipboardManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.scr.R;
import com.example.scr.database.HistoryDataBase;
import com.example.scr.modules.Record;

import java.io.IOException;
import java.io.InputStream;

public class ShowResultActivity extends AppCompatActivity {

    private static final String RESULT = "result";
    private static final String SEARCH = "search";
    private Record result;
    private TextView tvResult;
    private ClipboardManager clipboard;
    private String data;
    private HistoryDataBase dataBase;
    private static final String TITLE = "title";
    private Object recordContent;
    private Intent sendIntent;
    private ImageView ivFlag;
    private ConstraintLayout constraintLayout;
    private TextView tvTitle, tvType, tvTypeResult;
    private TextView tvFormat, tvFormatResult;
    private TextView tvDate, tvDateResult;
    private Record scanResult;
    private Record searchResult;
    private TextView tvCountryCode;
    private TextView tvCountry;
    private boolean scanFlag;
    private boolean shareFlag;
    private boolean flagFlag = true;
    private TextView tvCountryCodeT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_result);
        //handle option menu
        openOptionsMenu();


        //toolbar title
        getSupportActionBar().setTitle(getResources().getString(R.string.result_toolbar_title));

        //database
        dataBase = new HistoryDataBase(this);


        //get decoded data
        Bundle bundle = getIntent().getExtras();
        assert bundle != null;
        scanResult = (Record) bundle.getSerializable(RESULT);
        searchResult = (Record) bundle.getSerializable(SEARCH);
        if (scanResult == null) {
            if (searchResult.country.equals("")) {
                flagFlag = false;
            }
            setLayout(false);
            initialViews(false);
            setViews(searchResult, false);
            shareFlag = false;

        } else if (scanResult.valueFormat.equals("")) {
            if (scanResult.country.equals("")) {
                flagFlag = false;
            }
            setLayout(false);
            initialViews(false);
            setViews(scanResult, false);
            shareFlag = true;

        } else {
            if (scanResult.country.equals("")) {
                flagFlag = false;
            }
            setLayout(true);
            initialViews(true);
            setViews(scanResult, true);
            shareFlag = true;

        }
    }

    private void setLayout(boolean scanFlag) {
        if (scanFlag) {
            setContentView(R.layout.result_scan_view);
        } else {
            setContentView(R.layout.result_search_view);
        }
    }

    private void initialViews(boolean scanFlag) {
        if (scanFlag) {
            tvTitle = (TextView) findViewById(R.id.tv_title);
            tvType = (TextView) findViewById(R.id.tv_type_result);
            tvFormat = (TextView) findViewById(R.id.tv_format_result);
            tvDate = (TextView) findViewById(R.id.tv_date_result);
            tvCountryCode = (TextView) findViewById(R.id.tv_country_code_result);
            tvCountryCodeT = (TextView) findViewById(R.id.tv_country_code);
            tvCountry = (TextView) findViewById(R.id.tv_country);
            ivFlag = (ImageView) findViewById(R.id.ivflag);
        } else {
            tvTitle = (TextView) findViewById(R.id.tv_title);
            tvCountryCode = (TextView) findViewById(R.id.tv_country_code_result);
            tvDate = (TextView) findViewById(R.id.tv_date_result);
            tvCountry = (TextView) findViewById(R.id.tv_country);
            ivFlag = (ImageView) findViewById(R.id.ivflag);
        }
    }

    private void setViews(Record record, boolean scanFlag) {
        if (scanFlag) {
            tvTitle.setText(record.title);
            tvFormat.setText(record.valueFormat);
            tvType.setText(record.barcodeType);
            tvDate.setText(record.date);
            tvCountry.setText(record.country);
            tvCountryCode.setText(record.countryCode);
            if (flagFlag)
                setIcon(record, ivFlag);
            else {
                tvCountry.setVisibility(View.INVISIBLE);
                tvCountryCode.setVisibility(View.INVISIBLE);
                ivFlag.setVisibility(View.INVISIBLE);
                tvCountryCodeT.setVisibility(View.INVISIBLE);
            }

        } else {
            tvTitle.setText(record.title);
            tvCountryCode.setText(record.countryCode);
            tvDate.setText(record.date);
            tvCountry.setText(record.country);
            setIcon(record, ivFlag);
        }

    }

    private void setIcon(Record record, ImageView ivFlag) {
        if (record.country != null && !record.country.equals("")) {
            ivFlag.setVisibility(View.VISIBLE);
            InputStream ims = null;
            try {
                ims = getAssets().open("flags/" + record.country + ".png");
            } catch (IOException e) {
                e.printStackTrace();
            }
            Drawable d = Drawable.createFromStream(ims, null);
            ivFlag.setImageDrawable(d);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.result_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.share:
                if (shareFlag) {
                    recordContent = getRecordContent(scanResult, flagFlag);
                } else {
                    recordContent = getRecordContent(searchResult, flagFlag);
                }
                sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, data);
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private String getRecordContent(Record record, boolean flagCheck) {
        data = "";
        data += TITLE + ":   " + record.title + "\n";
        if (record.valueFormat != null && !record.valueFormat.equals("")) {
            data += "Type :      " + record.valueFormat + "\n";
        }
        if (record.barcodeType != null && !record.barcodeType.equals("")) {
            data += "format :     " + record.barcodeType + "\n";
        }
        if (record.country != null && !record.country.equals("") && flagCheck) {
            data += "country :     " + record.country + "\n";
        }
        if (record.date != null && !record.date.equals("")) {
            data += "date :       " + record.date + "\n";
        }
        return data;
    }
}
