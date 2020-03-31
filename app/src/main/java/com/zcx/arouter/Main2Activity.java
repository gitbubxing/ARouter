package com.zcx.arouter;

import android.os.Bundle;

import com.zcx.arouter_annotation.ARouter;

import androidx.appcompat.app.AppCompatActivity;

@ARouter(Path = "/app/Main2Activity")
public class Main2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
