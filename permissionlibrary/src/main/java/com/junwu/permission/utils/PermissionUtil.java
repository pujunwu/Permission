package com.junwu.permission.utils;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.content.PermissionChecker;

import java.util.ArrayList;

/**
 * Created by adminstrators on 2017/4/11.
 * ,
 */
public class PermissionUtil {

    /**
     * 是否需要运行时权限
     *
     * @return true需要，false不需要
     */
    public static boolean isNeedPermission() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    /**
     * 判断有那些权限需要申请
     *
     * @param activity   上下文
     * @param permission 权限列表
     * @return 需要申请权限的列表
     */
    @TargetApi(value = Build.VERSION_CODES.M)
    public static ArrayList<String> getDeniedPermissions(Activity activity, String... permission) {
        ArrayList<String> denyPermissions = new ArrayList<>();
        if (!isNeedPermission()) {
            return denyPermissions;
        }
        for (String value : permission) {
            //support SYSTEM_ALERT_WINDOW,WRITE_SETTINGS
            if (value.equals(Manifest.permission.SYSTEM_ALERT_WINDOW)) {
                if (!Settings.canDrawOverlays(activity)) {
                    denyPermissions.add(value);
                }
            } else if (value.equals(Manifest.permission.WRITE_SETTINGS)) {
                if (!Settings.System.canWrite(activity)) {
                    denyPermissions.add(value);
                }
            } else if (PermissionChecker.checkSelfPermission(activity, value) != PackageManager.PERMISSION_GRANTED) {
                denyPermissions.add(value);
            }
        }
        return denyPermissions;
    }

}
