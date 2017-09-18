package com.junwu.permission.utils;

import android.app.Activity;
import android.content.Context;

/**
 * Created by adminstrators on 2017/4/12.
 * activity工具类
 */
public class ContextUtil {

    public static Context getContext(Object object) {
        if (object == null) {
            return null;
        }
        if (object instanceof Activity) {
            return (Activity) object;
        } else if (object instanceof android.app.Fragment) {
            android.app.Fragment fragment = (android.app.Fragment) object;
            return fragment.getActivity();
        } else if (object instanceof android.support.v4.app.Fragment) {
            android.support.v4.app.Fragment fragment = (android.support.v4.app.Fragment) object;
            return fragment.getActivity();
        } else if (object instanceof Context) {
            return (Context) object;
        } else if (ApplicationUtil.instance().getApplication() != null) {
            return ApplicationUtil.instance().getApplication();
        }
        return null;
    }

    /**
     * 获取上下文
     *
     * @return 上下文
     */
    public static Context getContext() {
        return ApplicationUtil.instance().getApplication();
    }

}
