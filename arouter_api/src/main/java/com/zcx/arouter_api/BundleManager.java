package com.zcx.arouter_api;

import android.content.Context;
import android.os.Bundle;

import com.zcx.arouter_annotation.ARouter;

import androidx.annotation.Nullable;

public class BundleManager {

    private Bundle bundle= new Bundle();

    public Bundle getBundle() {
        return bundle;
    }

    public  BundleManager withString (@Nullable  String key , @Nullable String value){
        bundle.putString(key,value);

        return this;
    }
    public  BundleManager withInt (@Nullable  String key , @Nullable int value){
        bundle.putInt(key,value);

        return this;
    }

     public  void navigation(Context context){

         ARouterManager.getInstance().arouterNavication(context,this);

    }



}
