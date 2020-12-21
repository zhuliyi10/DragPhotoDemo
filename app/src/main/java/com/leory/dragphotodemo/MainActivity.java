package com.leory.dragphotodemo;

import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private ImageView mImage;
    private PhotoViewPage mPage;
    private View mRoot;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mImage = findViewById(R.id.image);
        mPage = findViewById(R.id.view_page);
        mRoot = findViewById(R.id.root);
        mImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBigPhoto();
            }
        });
    }

    private void showBigPhoto() {
        int[] location = new int[2];
        mImage.getLocationOnScreen(location);
        Rect srcRect = new Rect(location[0], location[1], location[0] + mImage.getWidth(), location[1] + mImage.getHeight());
        mPage.showPhotoView(srcRect);

    }

    @Override
    public void onBackPressed() {
        if (mPage.getVisibility() == View.VISIBLE) {
            mPage.closePhotoView();
            return;
        }
        super.onBackPressed();
    }
}