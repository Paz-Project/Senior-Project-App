package org.classapp.signlanguage;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button next = (Button)findViewById(R.id.guideline);
        final LoadingDialog loadingDialog = new LoadingDialog(MenuActivity.this);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MenuActivity.this ,GuideActivity.class);
                startActivity(i);
            }
        });

        ImageButton cam = (ImageButton) findViewById(R.id.cam);
        cam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadingDialog.startLoadingDialog();
                Intent i = new Intent(MenuActivity.this, PredictionActivity.class);
                startActivity(i);
            }
        });
    }
}