package com.ufpi.leevfrequency.View;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.ufpi.leevfrequency.R;

public class MainActivity extends AppCompatActivity {

    private ImageView appIcon;
    private final int TIME_OUT = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        appIcon = findViewById(R.id.appIcon);

        Glide
                .with(getContext())
                .load(getResources().getDrawable(R.drawable.leev_icon))
                .into(appIcon);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent start = new Intent(getContext(), LoginActivity.class);
                startActivity(start);
                finish();
            }
        }, TIME_OUT);
    }

    private Context getContext(){
        return this;
    }
}
