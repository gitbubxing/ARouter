package com.zcx.arouter.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.zcx.arouter_annotation.ARouter;
import com.zcx.arouter_annotation.Paramenter;
import com.zcx.me.R;

@ARouter(Path = "/me/Me_Main2Activity")
public class Me_Main2Activity extends AppCompatActivity {

    @Paramenter
    String age;
    @Paramenter
    String sex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_me__main2);
    }
}
