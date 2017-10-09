package com.junwu.permission;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;

import java.util.ArrayList;

import static com.junwu.permission.utils.PermissionUtil.getDeniedPermissions;

/**
 * 实际申请权限的activity
 */
public class ShowPermissionActivity extends Activity {

    //申请权限回调标识
    public static final int REQ_CODE_PERMISSION_REQUEST = 110;
    //进入权限管理界面管理权限回调标识
    public static final int REQ_CODE_REQUEST_SETTING = 119;
    //跳转到获取显示在其他应用窗口上的权限的回调标识
    public static final int REQ_CODE_REQUEST_SYSTEM_ALERT_WINDOW = 120;
    //跳转到获取系统设置权限的回调标识
    public static final int REQ_CODE_REQUEST_WRITE_SETTING = 121;
    //需要申请的权限
    private String[] permissions;
    //提示框标题
    private String title;
    //提示内容
    private String message;
    //取消按钮
    private String negativeButton;
    //确定按钮
    private String psitiveButton;
    //如果权限申请失败是否跳转到权限设置界面
    private boolean isShowDialog;
    //回调接口
    private Callback.OnPermissionListener mPermissionListener;
    //提示框显示回调接口
    private Callback.OnShowRationaleListener mOnShowRationaleListener;
    //需要获取的权限类型
    private String[] permissionTypes = null;
    //提示调用次数
    private int tipCount = 0;

    public static void start(Context context, String[] permissions, String title, String message, String negativeButton, String psitiveButton, /*boolean needGotoSetting,*/ boolean isShowDialog, String callbackKey) {
        Intent intent = new Intent(context, ShowPermissionActivity.class);
        intent.putExtra("permissions", permissions);
//        intent.putExtra("needGotoSetting", needGotoSetting);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("title", title);
        intent.putExtra("message", message);
        intent.putExtra("negativeButton", negativeButton);
        intent.putExtra("isShowDialog", isShowDialog);
        intent.putExtra("psitiveButton", psitiveButton);
        intent.putExtra("callbackKey", callbackKey);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onNewIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null) {
            setIntent(intent);
        }
        Bundle bundle = getIntent().getExtras();
        permissions = bundle.getStringArray("permissions");
//        needGotoSetting = bundle.getBoolean("needGotoSetting", false);
        isShowDialog = bundle.getBoolean("isShowDialog", false);
        title = bundle.getString("title", "权限提示");
        message = bundle.getString("message", "为了应用可以正常使用，请您点击确认申请权限。");
        negativeButton = bundle.getString("negativeButton", "取消");
        psitiveButton = bundle.getString("psitiveButton", "确定");
        mPermissionListener = Callback.getOnPermissionListener(bundle.getString("callbackKey"));
        mOnShowRationaleListener = Callback.getOnShowRationaleListener(bundle.getString("callbackKey"));
        //重置提示次数
        tipCount = 0;
        //权限申请
        ArrayList<String> deniedPermissions = getDeniedPermissions(this, this.permissions);
        if (deniedPermissions != null && !deniedPermissions.isEmpty()) {
            //判断权限类型
            permissionTypes = getShowRequestPermission(deniedPermissions);
            //发起第一次申请完成，要permissionTypes里面的类型都申请一遍才算完成
            requestPermissions(deniedPermissions, permissionTypes);
        } else {
            permissionGranted();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQ_CODE_REQUEST_SETTING://进入权限管理界面回调
                checkPermissions();
                break;
            case REQ_CODE_REQUEST_SYSTEM_ALERT_WINDOW: //显示在其他应用窗口回调
                checkPermissions();
                break;
            case REQ_CODE_REQUEST_WRITE_SETTING: //系统设置权限回调
                checkPermissions();
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != REQ_CODE_PERMISSION_REQUEST) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }
        if (isShowDialog) {
            ArrayList<String> deniedPermissions = getDeniedPermissions(this, this.permissions);
            if (deniedPermissions == null || deniedPermissions.isEmpty()) {
                //所有权限请求成功
                permissionGranted();
                return;
            }
            for (String permission : deniedPermissions) {
                boolean rationale = this.shouldShowRequestPermissionRationale(permission);
                if (!rationale) {
                    permissionTypes = getShowRequestPermission(deniedPermissions);
                    permissionTypes[3] = "";//取消申请权限
                    permissionTypes[2] = "true";//跳转到权限设置界面
                    showRationaleMessage(deniedPermissions);
                    return;
                }
            }
        }
        checkPermissions();
    }

    /**
     * 判断是否需要申请权限
     */
    private void checkPermissions() {
        ArrayList<String> deniedPermissions = getDeniedPermissions(this, this.permissions);
        if (deniedPermissions == null || deniedPermissions.isEmpty()) {
            //所有权限请求成功
            permissionGranted();
            return;
        }
        if (requestPermissions(deniedPermissions, permissionTypes)) {
            //第一次申请权限还未完成
            return;
        }
        if (!isShowDialog) {//如果设置为不提示
            permissionDenied(deniedPermissions);
            return;
        }

        permissionTypes = getShowRequestPermission(deniedPermissions);
        showRationaleMessage(deniedPermissions);
    }

    /**
     * 显示申请权限说明
     */
    private void showRationaleMessage(final ArrayList<String> deniedPermissions) {
        tipCount++;
        if (tipCount > 1) {
            permissionDenied(deniedPermissions);
            return;
        }
        if (mOnShowRationaleListener != null) {
            String[] strings = new String[deniedPermissions.size()];
            mOnShowRationaleListener.onShowRationale(deniedPermissions.toArray(strings), new Callback.OnCallbackListener() {
                @Override
                public void onNegative() {
                    permissionDenied(deniedPermissions);
                }

                @Override
                public void onPsitive() {
                    isRequestPermissions(deniedPermissions);
                }
            });
            return;
        }
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton(negativeButton, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        permissionDenied(deniedPermissions);
                    }
                })
                .setPositiveButton(psitiveButton, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        isRequestPermissions(deniedPermissions);
                    }
                }).create().show();
    }

    /**
     * 权限申请判断
     */
    private void isRequestPermissions(ArrayList<String> deniedPermissions) {
        if (getPermissionType(permissionTypes, "true")) {
            gotoSetting(permissionTypes);
            return;
        }
        requestPermissions(deniedPermissions, permissionTypes);
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
     * 跳转到权限管理界面
     *
     * @param needPermissions 需要申请的权限
     * @param permissionTypes 权限类型
     * @return 是否跳转到对应页面
     */
    @TargetApi(value = Build.VERSION_CODES.M)
    private boolean requestPermissions(ArrayList<String> needPermissions, String[] permissionTypes) {
        if (needPermissions == null || needPermissions.isEmpty() || arrayIsEmpty(permissionTypes)) {
            return false;
        }
        if (getPermissionType(permissionTypes, "false")) {
            permissionTypes[3] = "";
            //其他权限申请
            ActivityCompat.requestPermissions(this, needPermissions.toArray(new String[needPermissions.size()]), REQ_CODE_PERMISSION_REQUEST);
            return true;
        } else if (getPermissionType(permissionTypes, Manifest.permission.WRITE_SETTINGS)) {
            //修改系统设置权限
            permissionTypes[1] = "";
            Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS, Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, REQ_CODE_REQUEST_WRITE_SETTING);
            return true;
        } else if (getPermissionType(permissionTypes, Manifest.permission.SYSTEM_ALERT_WINDOW)) {
            permissionTypes[0] = "";
            //允许在其他应用上层显示权限
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, REQ_CODE_REQUEST_SYSTEM_ALERT_WINDOW);
            return true;
        }
        return false;
    }

    /**
     * 申请权限失败，提示用户进入权限管理界面设置权限
     */
    private void gotoSetting(String[] permissionTypes) {
        if (permissionTypes == null || arrayIsEmpty(permissionTypes)) {
            return;
        }
        if (!getPermissionType(permissionTypes, "true")) {
            return;
        }
        permissionTypes[2] = "";
//        Intent localIntent = new Intent();
//        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        if (Build.VERSION.SDK_INT >= 9) {
//            localIntent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//            localIntent.setData(Uri.fromParts("package", getPackageName(), null));
//        } else if (Build.VERSION.SDK_INT <= 8) {
//            localIntent.setAction(Intent.ACTION_VIEW);
//            localIntent.setClassName("com.android.settings",Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
//            localIntent.putExtra("com.android.settings.ApplicationPkgName", getPackageName());
//        }
//        try {
//            startActivityForResult(localIntent, REQ_CODE_REQUEST_SETTING);
//        } catch (ActivityNotFoundException e) {
//            e.printStackTrace();
//            //调用申请权限失败
//            permissionDenied(getDeniedPermissions(this, this.permissions));
//        }
        try {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, REQ_CODE_REQUEST_SETTING);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            try {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
                startActivityForResult(intent, REQ_CODE_REQUEST_SETTING);
            } catch (ActivityNotFoundException e1) {
                e1.printStackTrace();
                //调用申请权限失败
                permissionDenied(getDeniedPermissions(this, this.permissions));
            }
        }
    }

    /**
     * 获取需要申请权限的类型
     *
     * @param deniedPermissions 需要申请的权限
     * @return 权限的类型
     */
    private String[] getShowRequestPermission(ArrayList<String> deniedPermissions) {
        String[] permissionTypes = new String[4];
        for (String permission : deniedPermissions) {
            if (permission.equals(Manifest.permission.SYSTEM_ALERT_WINDOW)) {
                permissionTypes[0] = Manifest.permission.SYSTEM_ALERT_WINDOW;//判断是否需要申请，显示在其他应用上层权限
            } else if (permission.equals(Manifest.permission.WRITE_SETTINGS)) {
                permissionTypes[1] = Manifest.permission.WRITE_SETTINGS;//判断是否需要申请，修改系统参数权限
            } else if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                //判断当前权限是否可以在权限设置里面显示
                permissionTypes[2] = "true";
            } else {
                permissionTypes[3] = "false";
            }
        }
        return permissionTypes;
    }

    /**
     * 判断是否存在权限类型
     *
     * @param permissionTypes 权限类型
     * @param type            是否存在的类型
     * @return Boolean
     */
    private boolean getPermissionType(String[] permissionTypes, String type) {
        for (String t : permissionTypes) {
            if (TextUtils.equals(t, type)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 权限申请成功
     */
    private void permissionGranted() {
        if (mPermissionListener != null) {
            mPermissionListener.onCallback(null);
        }
        finish();
        overridePendingTransition(0, 0);
    }

    /**
     * 权限申请失败
     *
     * @param deniedPermissions 没有申请到的权限
     */
    private void permissionDenied(ArrayList<String> deniedPermissions) {
        String[] strings = new String[deniedPermissions.size()];
        permissionDenied(deniedPermissions.toArray(strings));
    }

    /**
     * 权限申请失败
     *
     * @param deniedPermissions 没有申请到的权限
     */
    private void permissionDenied(String[] deniedPermissions) {
        if (mPermissionListener != null) {
            mPermissionListener.onCallback(deniedPermissions);
        }
        finish();
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onDestroy() {
        mPermissionListener = null;
        mOnShowRationaleListener = null;
        super.onDestroy();
    }
}
