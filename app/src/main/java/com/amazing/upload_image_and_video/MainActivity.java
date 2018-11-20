package com.amazing.upload_image_and_video;

import android.Manifest;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

/**
 * get the url of the image or video file
 * url is not enough, because some url is only valid for a period of time
 * need to store the bitmap file into storage (external storage)
 * */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container, PickMediaFragment.newInstance())
                .commit();
    }
}
