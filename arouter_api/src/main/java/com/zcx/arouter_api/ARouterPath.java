package com.zcx.arouter_api;

import com.zcx.arouter_annotation.ARouterBean;

import java.util.Map;

public interface ARouterPath {

    Map<String , ARouterBean> getPathMap();
}
