package com.junwu.permission;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;

import java.util.ArrayList;

/**
 * ===============================
 * 描    述：权限申请Activity需要的工具类
 * 作    者：pjw
 * 创建日期：2018/1/5 11:15
 * ===============================
 */
class PermissionActivityUtil {

    //申请权限回调标识
    static final int REQ_CODE_PERMISSION_REQUEST = 110;
    //进入权限管理界面管理权限回调标识
    static final int REQ_CODE_REQUEST_SETTING = 119;
    //跳转到获取显示在其他应用窗口上的权限的回调标识
    static final int REQ_CODE_REQUEST_SYSTEM_ALERT_WINDOW = 120;
    //跳转到获取系统设置权限的回调标识
    static final int REQ_CODE_REQUEST_WRITE_SETTING = 121;

    private Activity mActivity;

    PermissionActivityUtil(Activity activity) {
        mActivity = activity;
    }

    /**
     * 获取需要申请权限的类型
     *
     * @param deniedPermissions 需要申请的权限
     * @return 权限的类型
     */
    String[] getShowRequestPermission(ArrayList<String> deniedPermissions) {
//        String[] permissionTypes = new String[4];
//        for (String permission : deniedPermissions) {
//            if (permission.equals(Manifest.permission.SYSTEM_ALERT_WINDOW)) {
//                permissionTypes[0] = Manifest.permission.SYSTEM_ALERT_WINDOW;//判断是否需要申请，显示在其他应用上层权限
//            } else if (permission.equals(Manifest.permission.WRITE_SETTINGS)) {
//                permissionTypes[1] = Manifest.permission.WRITE_SETTINGS;//判断是否需要申请，修改系统参数权限
//            } else if (ActivityCompat.shouldShowRequestPermissionRationale(mActivity, permission)) {
//                //表明用户没有彻底禁止弹出权限请求
//                permissionTypes[2] = "true";
//            } else {
//                //表明用户已经彻底禁止弹出权限请求
//                permissionTypes[3] = "false";
//            }
//        }
//        return permissionTypes;
        String[] permissionTypes = new String[3];
        for (String permission : deniedPermissions) {
            if (permission.equals(Manifest.permission.SYSTEM_ALERT_WINDOW)) {
                permissionTypes[0] = Manifest.permission.SYSTEM_ALERT_WINDOW;//判断是否需要申请，显示在其他应用上层权限
            } else if (permission.equals(Manifest.permission.WRITE_SETTINGS)) {
                permissionTypes[1] = Manifest.permission.WRITE_SETTINGS;//判断是否需要申请，修改系统参数权限
            } else {
                //可以弹出申请提示框的权限
                permissionTypes[2] = "true";
            }
        }
        return permissionTypes;
    }

    /**
     * 判断数组是否为空
     *
     * @param strs 数组
     * @return 是否为空
     */
    private boolean arrayIsEmpty(String[] strs) {
        if (strs == null || strs.length == 0) {
            return true;
        }
        for (String s : strs) {
            if (!TextUtils.isEmpty(s))
                return false;
        }
        return true;
    }

    /**
     * 判断是否存在权限类型
     *
     * @param permissionTypes 权限类型
     * @param type            是否存在的类型
     * @return Boolean
     */
    boolean getPermissionType(String[] permissionTypes, String type) {
        for (String t : permissionTypes) {
            if (TextUtils.equals(t, type)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 权限类型申请完成置空
     *
     * @param permissionTypes 权限类型数组
     * @param value           置空的类型
     */
    private void setPermissionTypeNull(String[] permissionTypes, String value) {
        for (int i = 0, len = permissionTypes.length; i < len; i++) {
            if (TextUtils.equals(permissionTypes[i], value)) {
                permissionTypes[i] = "";
            }
        }

    }

    /**
     * 跳转到权限管理界面
     *
     * @param needPermissions 需要申请的权限
     * @param permissionTypes 权限类型
     * @return 是否跳转到对应页面
     */
    @TargetApi(value = Build.VERSION_CODES.M)
    boolean requestPermissions(ArrayList<String> needPermissions, String[] permissionTypes) {
        if (needPermissions == null || needPermissions.isEmpty() || arrayIsEmpty(permissionTypes)) {
            return false;
        }
        if (getPermissionType(permissionTypes, "true")) {
            setPermissionTypeNull(permissionTypes, "true");
            //其他权限申请
            ActivityCompat.requestPermissions(mActivity, needPermissions.toArray(new String[needPermissions.size()]), REQ_CODE_PERMISSION_REQUEST);
            return true;
        } else if (getPermissionType(permissionTypes, Manifest.permission.WRITE_SETTINGS)) {
            //修改系统设置权限
            setPermissionTypeNull(permissionTypes, Manifest.permission.WRITE_SETTINGS);
            Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS, Uri.parse("package:" + mActivity.getPackageName()));
            mActivity.startActivityForResult(intent, REQ_CODE_REQUEST_WRITE_SETTING);
            return true;
        } else if (getPermissionType(permissionTypes, Manifest.permission.SYSTEM_ALERT_WINDOW)) {
            setPermissionTypeNull(permissionTypes, Manifest.permission.SYSTEM_ALERT_WINDOW);
            //允许在其他应用上层显示权限
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + mActivity.getPackageName()));
            mActivity.startActivityForResult(intent, REQ_CODE_REQUEST_SYSTEM_ALERT_WINDOW);
            return true;
        }
        return false;
    }

    /**
     * 申请权限失败，提示用户进入权限管理界面设置权限
     */
    boolean gotoSetting(String[] permissionTypes, String value) {
//        if (permissionTypes == null || arrayIsEmpty(permissionTypes)) {
//            return false;
//        }
        setPermissionTypeNull(permissionTypes, value);
        try {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(Uri.parse("package:" + mActivity.getPackageName()));
            mActivity.startActivityForResult(intent, REQ_CODE_REQUEST_SETTING);
            return true;
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            try {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
                mActivity.startActivityForResult(intent, REQ_CODE_REQUEST_SETTING);
                return true;
            } catch (ActivityNotFoundException e1) {
                e1.printStackTrace();
                return false;
            }
        }
    }


}
