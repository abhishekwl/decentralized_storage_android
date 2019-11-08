package com.netlify.abhishekwl.dcloud;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Objects;

import okhttp3.OkHttpClient;

public class MainActivity extends AppCompatActivity {

    private BottomAppBar mainBottomAppBar;
    private FragmentManager fragmentManager;
    private FloatingActionButton addFab;

    public OkHttpClient okHttpClient;
    public static String userId, userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeComponents();
        initializeViews();
    }

    private void initializeViews() {
        mainBottomAppBar = findViewById(R.id.mainBottomAppBar);
        setSupportActionBar(mainBottomAppBar);
        Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(R.drawable.ic_home_black_24dp);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        mainBottomAppBar.replaceMenu(R.menu.main_menu);
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.mainFrameLayout, new PostsFragment()).commit();
        addFab = findViewById(R.id.mainAddFab);
        addFab.setColorFilter(Color.WHITE);
        addFab.setOnClickListener(v -> {
            Intent uploadFileIntent = new Intent(MainActivity.this, UploadActivity.class);
            uploadFileIntent.putExtra("user_id", userId);
            uploadFileIntent.putExtra("user_name", userName);
            startActivity(uploadFileIntent);
        });
    }

    private void initializeComponents() {
        okHttpClient = new OkHttpClient();
        userId = getIntent().getStringExtra("user_id");
        userName = getIntent().getStringExtra("user_name");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.videosMenuItem:
                fragmentManager.beginTransaction().replace(R.id.mainFrameLayout, new VideosFragment()).commit();
                break;
            case R.id.photosMenuItem:
                fragmentManager.beginTransaction().replace(R.id.mainFrameLayout, new PhotosFragment()).commit();
                break;
            case R.id.privateMenuItem:
                fragmentManager.beginTransaction().replace(R.id.mainFrameLayout, new PrivateFragment()).commit();
                break;
            default:
                fragmentManager.beginTransaction().replace(R.id.mainFrameLayout, new PostsFragment()).commit();
                break;
        }
        return true;
    }
}
