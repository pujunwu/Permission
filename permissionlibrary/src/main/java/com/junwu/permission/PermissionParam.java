package com.junwu.permission;

import android.Manifest;
import android.content.Context;

/**
 * ===============================
 * 描    述：申请权限参数配置
 * 作    者：pjw
 * 创建日期：2017/9/29 10:16
 * ===============================
 */
public class PermissionParam {
    Context mContext;//上下文，可选
    String[] permissions;//需要申请的权限
    String title = "权限提示";
    String message = "为了应用可以正常使用，请您点击确认申请权限。";
    String negativeButton = "取消";
    String psitiveButton = "确定";
    //    boolean needGotoSetting = false;// 如果权限申请有失败的，判断是否跳转到权限设置界面
    boolean isShowDialog = false;//如果进入系统权限管理界面后，权限还未获取成功，就提示是否提示重新获取

    public PermissionParam setContext(Context context) {
        mContext = context;
        return this;
    }

    public PermissionParam setPermissions(String... permissions) {
        this.permissions = permissions;
        return this;
    }

    public PermissionParam setTitle(String title) {
        this.title = title;
        return this;
    }

    public PermissionParam setMessage(String message) {
        this.message = message;
        return this;
    }

    public PermissionParam setNegativeButton(String negativeButton) {
        this.negativeButton = negativeButton;
        return this;
    }

    public PermissionParam setPsitiveButton(String psitiveButton) {
        this.psitiveButton = psitiveButton;
        return this;
    }

//    public PermissionParam setNeedGotoSetting(boolean needGotoSetting) {
//        this.needGotoSetting = needGotoSetting;
//        return this;
//    }

    public PermissionParam setShowDialog(boolean showDialog) {
        isShowDialog = showDialog;
        return this;
    }

    /****************************获取PermissionsApply对象**************************/

    private PermissionParam() {
    }

    public PermissionsApply getPermissionsApply() {
        return new PermissionsApply(this);
    }

    /****************************获取当前对象**************************/

    public static PermissionParam getParam() {
        return new PermissionParam();
    }

    public static PermissionParam getParamCamera() {
        return new PermissionParam()
                .setPermissions(Manifest.permission.CAMERA);
    }

    public static PermissionParam getParamSDCard() {
        return new PermissionParam()
                .setPermissions(Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    public static PermissionParam getParamCameraSDCard() {
        return new PermissionParam()
                .setPermissions(Manifest.permission.CAMERA,
                        Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    public static PermissionParam getParamWifi() {
        return new PermissionParam()
                .setPermissions(Manifest.permission.CHANGE_WIFI_STATE,
                        Manifest.permission.ACCESS_WIFI_STATE,
                        Manifest.permission.ACCESS_NETWORK_STATE);
    }

    public static PermissionParam getParamVideo() {
        return new PermissionParam()
                .setPermissions(Manifest.permission.CAMERA,
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    private static PermissionParam getParam(String... permissions) {
        return new PermissionParam().setPermissions(permissions);
    }

    //更多权限组说明：http://blog.csdn.net/koma025/article/details/52913511

}
