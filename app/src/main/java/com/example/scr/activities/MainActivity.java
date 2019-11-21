package com.example.scr.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.widget.SearchView;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.example.scr.R;
import com.example.scr.database.HistoryDataBase;
import com.example.scr.modules.Record;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String SEARCH = "search";
    SurfaceView surfaceView;
    TextView txtBarcodeValue;
    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    private static final int REQUEST_CAMERA_PERMISSION = 201;
    String intentData = "";
    private ImageView more;
    private MenuBuilder menu;
    private View line;
    private Animation anim;
    private Intent intent;
    private boolean flag = false;
    private static final int REQUEST_CODE = 1;
    private static final int RESULT_LOAD_IMAGE = 2;
    private InputStream imageStream;
    Map<String, String> dictionary = new HashMap<String, String>();
    private SharedPreferences preferences;
    private Camera camera;
    private String COUNTRIES = "countries";
    private SparseArray<Barcode> barcodes;
    private String country;
    private String RESULT = "result";
    private Frame frame;
    private int BARCODE_TYPE_ONE = 32;
    private int BARCODE_TYPE_TWO = 512;
    private Bitmap selectedIm;
    private BarcodeDetector detector;
    private MediaPlayer beep;
    private HistoryDataBase dataBase;
    private InputStream is;
    private String content;
    private Record record = new Record(-1 , null ,null ,null , null , null , null);
    private String countryCode;
    private Barcode barcode;
    private View alertDialogView;
    private AlertDialog alertDialog;
    private SearchView sv;
    private String txt;
    private Toolbar toolbar;


    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Animation
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.move);
        animation.setRepeatCount(Animation.INFINITE);
        animation.setRepeatMode(Animation.RESTART);




        //database
        dataBase = new HistoryDataBase(this);


        alertDialogView= getLayoutInflater().inflate(R.layout.alert_dialog_search, null);




//        //alert dialog
//        alertDialog = new AlertDialog.Builder(this)
//                .setView(alertDialogView)
//                .setPositiveButton(R.string.ad_button, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        sv = (SearchView) alertDialog.findViewById(R.id.sv_search);
//                        txt = sv.getQuery().toString();
//                        if (txt.length() > 2) {
//                            country = getCountry(barcodes, txt, true);
//                            if (country.length() > 0) {
//
//                                //record attr
//                                record.title = txt;
//                                record.valueFormat = "";
//                                record.barcodeType = "";
//                                record.date = DateFormat.getDateTimeInstance().format(new Date());
//                                record.country = country;
//                                record.countryCode = getCountryCode(txt);
//
//                                //detect sound
//                                beep.start();
//
//
//                                //send data
//                                intent = new Intent(MainActivity.this, ShowResultActivity.class);
//                                intent.putExtra(SEARCH, record);
//
//                                //add record to db
//                                dataBase.addRecord(record);
//
//                                startActivity(intent);
//                            } else {
//                                Toast.makeText(MainActivity.this, R.string.no_country_found, Toast.LENGTH_SHORT).show();
//                            }
//                        } else {
//                            Toast.makeText(MainActivity.this, R.string.least_number, Toast.LENGTH_SHORT).show();
//                        }
//
//                    }
//                }).create();
//        sv = (SearchView) alertDialogView.findViewById(R.id.sv_search);
//        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String s) {
//                if (s.length() > 2) {
//                    country = getCountry(barcodes, s, true);
//                    if (country.length() > 0) {
//                        //record attr
//                        record.title = s;
//                        record.valueFormat = "";
//                        record.barcodeType = "";
//                        record.date = DateFormat.getDateTimeInstance().format(new Date());
//                        record.country = country;
//                        record.countryCode = getCountryCode(s);
//
//                        //detect sound
//                        beep.start();
//
//
//                        //send data
//                        intent = new Intent(MainActivity.this, ShowResultActivity.class);
//                        intent.putExtra(SEARCH, record);
//
//                        //add record to db
//                        dataBase.addRecord(record);
//
//                        sv.setQuery("" , false);
//                        alertDialog.dismiss();
//                        startActivity(intent);
//                    } else {
//                        Toast.makeText(MainActivity.this, R.string.no_country_found, Toast.LENGTH_SHORT).show();
//                    }
//                    return true;
//                } else {
//                    Toast.makeText(MainActivity.this, R.string.least_number, Toast.LENGTH_SHORT).show();
//                }
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String s) {
//                return false;
//            }
//        });
//
//
//        //fab handling
//        FloatingActionButton fab = findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                sv.setQuery("" , false);
//                alertDialog.show();
//            }
//        });

        //initial views
        surfaceView = findViewById(R.id.surfaceView);
        line = (View) findViewById(R.id.viewLine);
        line.startAnimation(animation);

        //media
        beep = MediaPlayer.create(this, R.raw.beep);

        //construct country database
        preferences = getSharedPreferences(COUNTRIES, MODE_PRIVATE);
        constructDB(preferences);
        //openOptionsMenu();
    }


    private void addCountry(SharedPreferences preferences, String country, int s, int e) {
        @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor = preferences.edit();
        for (int i = s; i < e; i++) {
            if (i < 10) {
                editor.putString("00" + i, country);
            } else if (i < 100) {
                editor.putString("0" + i, country);
            } else {
                editor.putString("" + i, country);
            }
        }
        editor.apply();
    }



    private void constructDB(SharedPreferences preferences) {
        addCountry(preferences, "USA", 0, 140);
        addCountry(preferences, "France", 300, 380);
        addCountry(preferences, "Germany", 400, 440);
        addCountry(preferences, "Japan", 450, 460);
        addCountry(preferences, "Japan", 490, 500);
        addCountry(preferences, "UK", 500, 510);
        addCountry(preferences, "China", 690, 700);
        addCountry(preferences, "Belgium", 540, 549);
        addCountry(preferences, "Iran", 626, 627);
        addCountry(preferences, "Canada", 754, 756);
        addCountry(preferences, "Italy", 800, 840);
        addCountry(preferences, "Turkey", 868, 870);
        addCountry(preferences, "South-Korea", 880, 881);
        addCountry(preferences, "India", 890, 891);
        addCountry(preferences, "Taiwan", 471, 472);
        addCountry(preferences, "Russia", 460, 470);
        addCountry(preferences, "Sweden", 730, 740);
        addCountry(preferences, "Brazil", 789, 791);
        addCountry(preferences, "Thailand", 885, 886);
        addCountry(preferences, "Vietnam", 893, 894);
        addCountry(preferences, "Indonesia", 899, 900);
        addCountry(preferences, "Malaysia", 955, 956);
        addCountry(preferences, "Croatia", 385, 386);
        addCountry(preferences, "Philippines", 480, 481);
        addCountry(preferences, "Hong-Kong", 489, 490);
        addCountry(preferences, "Denmark", 570, 580);
        addCountry(preferences, "Poland", 590, 591);
        addCountry(preferences, "Israel", 729, 730);
        addCountry(preferences, "Mexico", 750, 751);
        addCountry(preferences, "Netherlands", 870, 880);
        addCountry(preferences, "Singapore", 888, 889);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.more, menu);
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.gallery:
                if (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE);
                } else {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), RESULT_LOAD_IMAGE);
                }
                return true;
            case R.id.history:
                Intent i = new Intent(this, HistoryActivity.class);
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private String getCountryCode(String txt) {
        char[] count = new char[3];
        countryCode = "";
        txt.getChars(0, 3, count, 0);
        for (int i = 0; i < 3; i++) {
            countryCode += count[i];
        }
        return countryCode;
    }

    private void initialiseDetectorsAndSources() {
        record = new Record(-1, null, null, null, null, null , null);

        barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.ALL_FORMATS)
                .build();

        cameraSource = new CameraSource.Builder(this, barcodeDetector)
                .setRequestedPreviewSize(1920, 1080)
                .setAutoFocusEnabled(true) //you should add this feature
                .build();

        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        cameraSource.start(surfaceView.getHolder());
                    } else {
                        ActivityCompat.requestPermissions(MainActivity.this, new
                                String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });


        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                barcodes = detections.getDetectedItems();
                if (barcodes.size() != 0) {

                    //detection sound plays
                    beep.start();

                    //get barcode
                    barcode = barcodes.valueAt(0);

                    //set attrs
                    record.title = barcode.rawValue;
                    record.country = getCountry(barcodes, barcode.rawValue , false);
                    record.date = DateFormat.getDateTimeInstance().format(new Date()) + "";
                    record.barcodeType = decodeBarcodeType(barcode.format) + "";
                    record.valueFormat = decodeBarcodeFormat(barcode.valueFormat) + "";
                    record.countryCode = getCountryCode(barcode.rawValue);

                    intent = new Intent(MainActivity.this, ShowResultActivity.class);
                    intent.putExtra(RESULT, record);
                    if (!flag) {
                        dataBase.addRecord(record);
                        startActivity(intent);
                    }
                    flag = true;
                }
            }
        });
    }

    private String decodeBarcodeType(int format) {
        switch (format) {
            case Barcode.CODE_128:
                return "CODE_128";
            case Barcode.CODE_39:
                return "CODE_39";
            case Barcode.CODE_93:
                return "CODE_93";
            case Barcode.CODABAR:
                return "CODABAR";
            case Barcode.DATA_MATRIX:
                return "DATA_MATRIX";
            case Barcode.EAN_13:
                return "EAN_13";
            case Barcode.EAN_8:
                return "EAN_8";
            case Barcode.ITF:
                return "ITF";
            case Barcode.QR_CODE:
                return "QR_CODE";
            case Barcode.UPC_A:
                return "UPC_A";
            case Barcode.UPC_E:
                return "UPC_E";
            case Barcode.PDF417:
                return "PDF417";
            case Barcode.AZTEC:
                return "AZTEC";
            default:
                return "";
        }
    }

    private String decodeBarcodeFormat(int format) {
        switch (format) {
            case Barcode.CONTACT_INFO:
                return "contact information";
            case Barcode.EMAIL:
                return "email";
            case Barcode.ISBN:
                return "ISBN";
            case Barcode.PHONE:
                return "phone number";
            case Barcode.PRODUCT:
                return "product";
            case Barcode.SMS:
                return "sms";
            case Barcode.TEXT:
                return "text";
            case Barcode.URL:
                return "url";
            case Barcode.WIFI:
                return "wifi";
            case Barcode.GEO:
                return "GEO";
            default:
                return "";
        }
    }

    private String getCountry(SparseArray<Barcode> barcodes, String content, boolean flag) {
        country = "";
        if (flag) {
            char[] count = new char[3];
            countryCode = "";
            content.getChars(0, 3, count, 0);
            for (int i = 0; i < 3; i++) {
                countryCode += count[i];
            }
            preferences = getSharedPreferences(COUNTRIES, MODE_PRIVATE);
            country = preferences.getString(countryCode, "");
        } else {
            if (barcodes.valueAt(0).format == Barcode.UPC_A || barcodes.valueAt(0).format == Barcode.EAN_13) {
                char[] count = new char[3];
                countryCode = "";
                content.getChars(0, 3, count, 0);
                for (int i = 0; i < 3; i++) {
                    countryCode += count[i];
                }
                preferences = getSharedPreferences(COUNTRIES, MODE_PRIVATE);
                country = preferences.getString(countryCode, "");

            }
        }
        return country;
    }

    @Override
    protected void onResume() {
        super.onResume();
        flag = false;
        initialiseDetectorsAndSources();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(i, RESULT_LOAD_IMAGE);
                }
            }
            case REQUEST_CAMERA_PERMISSION:
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                try {
                    cameraSource.start(surfaceView.getHolder());
                } catch (IOException e) {
                    e.printStackTrace();
                }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            intent = new Intent(MainActivity.this , ShowResultActivity.class);

            //get image from gallery
            Uri selectedImage = data.getData();
            imageStream = null;
            try {
                imageStream = getContentResolver().openInputStream(selectedImage);
            } catch (FileNotFoundException e) {
            }
            selectedIm = BitmapFactory.decodeStream(imageStream);
            detector = new BarcodeDetector.Builder(getApplicationContext()).build();

            //handle detector crush
            if (!detector.isOperational()) {
                return;
            }
            frame = new Frame.Builder().setBitmap(selectedIm).build();
            barcodes = detector.detect(frame);

            if (barcodes.size() > 0) {
                //detection sound plays
                beep.start();

                //get barcode
                barcode = barcodes.valueAt(0);

                //set attrs
                record.title = barcode.rawValue;
                record.country = getCountry(barcodes  ,barcode.rawValue, false);
                record.date = DateFormat.getDateTimeInstance().format(new Date());
                record.barcodeType = decodeBarcodeType(barcode.format);
                record.valueFormat = decodeBarcodeFormat(barcode.valueFormat);
                record.countryCode = getCountryCode(barcode.rawValue);

                intent.putExtra(RESULT, record);
                dataBase.addRecord(record);
                startActivity(intent);
            } else {
                Toast.makeText(this, getResources().getString(R.string.picture_unreadable), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
