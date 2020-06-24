package com.mnk.env;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.RequiresApi;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class welcomeActivity extends Activity {

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welecome);

        ImageView imageView = findViewById(R.id.splash_ican);
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.blick);
        imageView.startAnimation(animation);

        Thread splash = new Thread() {
            public void run() {
                try {
                    //set sleep time
                    sleep(3 * 1000);
                    Intent intent = new Intent(welcomeActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                    //Intent i = new Intent(welcomeActivity.this, LoginActivity.class);
                    //startActivity(i);
                    //finish();
                } catch (Exception e) { }
            }
        };
        splash.start();
    }
}
