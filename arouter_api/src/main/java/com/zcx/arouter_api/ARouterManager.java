package com.zcx.arouter_api;

import android.content.Context;
import android.content.Intent;
import android.util.LruCache;

import com.zcx.arouter_annotation.ARouterBean;

import java.util.IllegalFormatCodePointException;
import java.util.List;
import java.util.Map;

public class ARouterManager {

    private String path;
    private String group;
    public static ARouterManager instance;
    private LruCache<String, ARouterGroup> aRouterGroupLruCache;
    private LruCache<String, ARouterBean> aRouterPathLruCache;

    private ARouterManager() {
        aRouterGroupLruCache = new LruCache<>(100);
        aRouterPathLruCache = new LruCache<>(100);
    }

    public static ARouterManager getInstance() {
        if (instance == null) {
            synchronized (ARouterManager.class) {
                if (instance == null) {
                    instance = new ARouterManager();
                }
            }
        }

        return instance;
    }

    public  BundleManager build(String path){

        if (path == null || path.length()<=0){
           throw  new IllegalArgumentException();
        }
        this.path = path;
        this.group = path.split("/")[1];

        return  new BundleManager();
    }

    public void arouterNavication(Context context,BundleManager bundleManager) {

        String packageName = context.getPackageName()+".ARouter$$Group$$"+this.group;

        ARouterGroup aRouterGroup = aRouterGroupLruCache.get(group);

        if (aRouterGroup == null){
            try {
                Class<?> aClass = Class.forName(packageName);
                aRouterGroup =(ARouterGroup) aClass.newInstance();

                aRouterGroupLruCache.put(group,aRouterGroup);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }

        }

        ARouterBean bean = aRouterPathLruCache.get(path);
        if (bean == null){
            Map<String, Class<? extends ARouterPath>> groupClass = aRouterGroup.getGroup();
            Class<? extends ARouterPath> aClass = groupClass.get(group);
            try {
                bean= (ARouterBean) aClass.newInstance();
                aRouterPathLruCache.put(path,bean);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }

        }

       if (bean.getBeanType() == ARouterBean.BeanType.ACTIVITY){
           Intent intent = new Intent(context,bean.getaClass());
         intent.putExtras(bundleManager.getBundle());

       }



    }
}
