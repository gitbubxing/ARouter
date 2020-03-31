package com.zcx.arouter;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.zcx.arouter_annotation.ARouter;

@ARouter(Path = "/app/MainActivity")
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
