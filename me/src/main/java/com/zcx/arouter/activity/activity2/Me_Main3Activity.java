package com.zcx.arouter.activity.activity2;

import android.os.Bundle;

import com.zcx.arouter_annotation.ARouter;
import com.zcx.arouter_annotation.Paramenter;
import com.zcx.me.R;

import androidx.appcompat.app.AppCompatActivity;

@ARouter(Path = "/me/Me_Main3Activity")
public class Me_Main3Activity extends AppCompatActivity {

    @Paramenter
    String age;
    @Paramenter
    String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_me__main2);
    }
}
