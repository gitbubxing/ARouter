package com.zcx.arouter;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.zcx.arouter_annotation.ARouter;
import com.zcx.arouter_annotation.Paramenter;
import com.zcx.arouter_api.ARouterManager;
import com.zcx.me.R;

@ARouter(Path = "/me/Me_MainActivity")
public class Me_MainActivity extends AppCompatActivity {

    @Paramenter
    String name;

    @Paramenter
    String age;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_me__main);
        ARouterManager.getInstance().build("/app/MainActivity").navigation(this);
    }
}
