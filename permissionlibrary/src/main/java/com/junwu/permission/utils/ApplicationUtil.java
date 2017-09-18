package com.junwu.permission.utils;


import android.app.Application;

public class ApplicationUtil {

    private ApplicationUtil() {
    }

    private Application application;

    private static class AppInstance {
        private static final ApplicationUtil UTIL = new ApplicationUtil();
    }

    public static ApplicationUtil instance() {
        return AppInstance.UTIL;
    }

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }
}
