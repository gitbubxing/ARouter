package com.zcx.arouter_api;

import java.util.List;
import java.util.Map;

public interface ARouterGroup {

    Map<String , Class< ? extends ARouterPath>> getGroup();
}
