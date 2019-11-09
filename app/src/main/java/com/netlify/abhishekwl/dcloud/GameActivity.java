package com.netlify.abhishekwl.dcloud;

import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class GameActivity extends AppCompatActivity {

    FloatingActionButton rightButton, leftButton, upButton, downButton;
    ImageView gameImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_game);

        initializeViews();
        initializeComponents();
    }

    private void initializeComponents() {
        String tempHash = "QmU3dAhRHHtzfEw3YmEtVthjc6KoGBbUK74uh2v5yHmife";
        Glide.with(getApplicationContext()).load("http://192.168.43.12:8080/ipfs/"+tempHash).into(gameImageView);
    }

    private void initializeViews() {
        rightButton = findViewById(R.id.rightButton);
        leftButton = findViewById(R.id.leftButton);
        upButton = findViewById(R.id.upButton);
        downButton = findViewById(R.id.downButton);
        gameImageView = findViewById(R.id.gameImageView);

        upButton.setOnClickListener(v -> {

        });

        downButton.setOnClickListener(v -> {

        });

        leftButton.setOnClickListener(v -> {

        });

        rightButton.setOnClickListener(v -> {

        });
    }
}
