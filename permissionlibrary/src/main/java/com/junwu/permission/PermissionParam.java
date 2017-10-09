package com.junwu.permission;

import android.Manifest;
import android.content.Context;
import android.os.Build;

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
    boolean isShowDialog = false;//用户勾选了不再提示，导致以后无法申请权限，如果设置为true就可提示用户再次申请权限

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
                .setPermissions(CAMERA);
    }

    public static PermissionParam getParamSDCard() {
        return new PermissionParam()
                .setPermissions(STORAGE);
    }

    public static PermissionParam getParamVideo() {
        return new PermissionParam()
                .setPermissions(VIDEO);
    }

    public static PermissionParam getParamMicrophone() {
        return new PermissionParam()
                .setPermissions(MICROPHONE);
    }

//    public static PermissionParam getParamCalendar() {
//        return new PermissionParam()
//                .setPermissions(Manifest.permission.READ_CALENDAR,
//                        Manifest.permission.WRITE_CALENDAR);
//    }
//
//    public static PermissionParam getParamContacts() {
//        return new PermissionParam()
//                .setPermissions(Manifest.permission.READ_CONTACTS,
//                        Manifest.permission.WRITE_CONTACTS,
//                        Manifest.permission.GET_ACCOUNTS);
//    }
//
//    public static PermissionParam getParamLocation() {
//        return new PermissionParam()
//                .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION,
//                        Manifest.permission.ACCESS_COARSE_LOCATION);
//    }
//
//    public static PermissionParam getParamPhone() {
//        return new PermissionParam()
//                .setPermissions(Manifest.permission.READ_PHONE_STATE,
//                        Manifest.permission.CALL_PHONE,
//                        Manifest.permission.READ_CALL_LOG,
//                        Manifest.permission.WRITE_CALL_LOG,
//                        Manifest.permission.USE_SIP,
//                        Manifest.permission.PROCESS_OUTGOING_CALLS);
//    }
//
//    public static PermissionParam getParamSensors() {
//        return new PermissionParam()
//                .setPermissions(Manifest.permission.BODY_SENSORS);
//    }
//
//    public static PermissionParam getParamSMS() {
//        return new PermissionParam()
//                .setPermissions(Manifest.permission.SEND_SMS,
//                        Manifest.permission.RECEIVE_SMS,
//                        Manifest.permission.READ_SMS,
//                        Manifest.permission.RECEIVE_WAP_PUSH,
//                        Manifest.permission.RECEIVE_MMS);
//    }

    private static PermissionParam getParam(String... permissions) {
        return new PermissionParam().setPermissions(permissions);
    }

    //更多权限组说明：http://blog.csdn.net/koma025/article/details/52913511

    /****************************8.0权限组**************************/

    public static final String[] CALENDAR;   // 读写日历。
    public static final String[] CAMERA;     // 相机。
    public static final String[] CONTACTS;   // 读写联系人。
    public static final String[] LOCATION;   // 读位置信息。
    public static final String[] MICROPHONE; // 使用麦克风。
    public static final String[] PHONE;      // 读电话状态、打电话、读写电话记录。
    public static final String[] SENSORS;    // 传感器。
    public static final String[] SMS;        // 读写短信、收发短信。
    public static final String[] STORAGE;    // 读写存储卡。
    public static final String[] VIDEO;//录像

    static {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            CALENDAR = new String[]{};
            CAMERA = CALENDAR;
            CONTACTS = CALENDAR;
            LOCATION = CALENDAR;
            MICROPHONE = CALENDAR;
            PHONE = CALENDAR;
            SENSORS = CALENDAR;
            SMS = CALENDAR;
            STORAGE = CALENDAR;
            VIDEO = CALENDAR;
        } else {
            CALENDAR = new String[]{
                    Manifest.permission.READ_CALENDAR,
                    Manifest.permission.WRITE_CALENDAR};

            CAMERA = new String[]{
                    Manifest.permission.CAMERA};

            CONTACTS = new String[]{
                    Manifest.permission.READ_CONTACTS,
                    Manifest.permission.WRITE_CONTACTS,
                    Manifest.permission.GET_ACCOUNTS};

            LOCATION = new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION};

            MICROPHONE = new String[]{
                    Manifest.permission.RECORD_AUDIO};

            PHONE = new String[]{
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.CALL_PHONE,
                    Manifest.permission.READ_CALL_LOG,
                    Manifest.permission.WRITE_CALL_LOG,
                    Manifest.permission.USE_SIP,
                    Manifest.permission.PROCESS_OUTGOING_CALLS};

            SENSORS = new String[]{
                    Manifest.permission.BODY_SENSORS};

            SMS = new String[]{
                    Manifest.permission.SEND_SMS,
                    Manifest.permission.RECEIVE_SMS,
                    Manifest.permission.READ_SMS,
                    Manifest.permission.RECEIVE_WAP_PUSH,
                    Manifest.permission.RECEIVE_MMS};

            STORAGE = new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE};

            VIDEO = new String[]{Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE};
        }
    }

}
