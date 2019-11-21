package com.example.scr.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.example.scr.R;

public class SplashActivity extends AppCompatActivity {

    private long DURATION = 2500;
    private View line;
    private Handler handler;
    private View ivIcon;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //animation
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.line);
        Animation iconAnimation = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.fade);
        animation.setRepeatCount(Animation.INFINITE);
        animation.setRepeatMode(Animation.RESTART);

        //line = (View) findViewById(R.id.viewLine);
        ivIcon = (View) findViewById(R.id.ivIcon);

        //line.startAnimation(animation);
        ivIcon.startAnimation(iconAnimation);


        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this,
                        MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, DURATION);
    }
}
