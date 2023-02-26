package org.classapp.signlanguage;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

public class Splash extends AppCompatActivity {

    ImageView sp_bg,sp_image;
    TextView appname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        appname = findViewById(R.id.appname);
        sp_image = findViewById(R.id.sp_image);
        sp_bg = findViewById(R.id.sp_bg);

        sp_bg.animate().translationY(-2500).setDuration(1000).setStartDelay(3000);
        appname.animate().translationY(2000).setDuration(1000).setStartDelay(3000);
        sp_image.animate().translationY(1500).setDuration(1000).setStartDelay(3000);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                startActivity(new Intent(Splash.this,MenuActivity.class));

            }
        },4000);
    }
}