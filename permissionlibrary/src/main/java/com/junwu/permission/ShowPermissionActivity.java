package com.junwu.permission;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;

import com.junwu.permission.utils.PermissionUtil;

import java.util.ArrayList;

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
    //处理结束回调接口
    private static PermissionListener permissionListener;
    //是否跳转到获取修改系统设置权限界面，这个权限需要单独处理Manifest.permission.WRITE_SETTINGS
    private boolean hasRequestedWriteSettings = false;
    //是否跳转到获取显示在其他应用上面的权限界面，Manifest.permission.SYSTEM_ALERT_WINDOW
    private boolean hasRequestedSystemAlertWindow = false;
    private String permissionSystemAlertWindow;
    private String permissionWriteSettings;
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
    //如果权限申请失败是否提示用户到权限设置界面设置权限
    private boolean needGotoSetting;
    //当前包路径
    private String packageName;

    public static void start(Context context, String[] permissions, String title, String message, String negativeButton, String psitiveButton, boolean needGotoSetting, PermissionListener permissionListener) {
        ShowPermissionActivity.permissionListener = permissionListener;
        Intent intent = new Intent(context, ShowPermissionActivity.class);
        intent.putExtra("permissions", permissions);
        intent.putExtra("needGotoSetting", needGotoSetting);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("title", title);
        intent.putExtra("message", message);
        intent.putExtra("negativeButton", negativeButton);
        intent.putExtra("psitiveButton", psitiveButton);
        context.startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQ_CODE_REQUEST_SETTING://进入权限管理界面回调
                checkPermissions(true);
                break;
            case REQ_CODE_REQUEST_SYSTEM_ALERT_WINDOW: //显示在其他应用窗口回调
                hasRequestedSystemAlertWindow = true;
                checkPermissions(false);
                break;
            case REQ_CODE_REQUEST_WRITE_SETTING: //系统设置权限回调
                hasRequestedWriteSettings = true;
                checkPermissions(false);
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
        ArrayList<String> deniedPermissions = new ArrayList<>();
        for (int i = 0, size = permissions.length; i < size; i++) {
            String permission = permissions[i];
            if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                deniedPermissions.add(permission);
            }
        }
        if (deniedPermissions.isEmpty()) {
            permissionGranted();
        } else {
            if (needGotoSetting) {
                gotoSetting();
            } else {
                permissionDenied(deniedPermissions);
            }
        }
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
        needGotoSetting = bundle.getBoolean("needGotoSetting", false);
        title = bundle.getString("title", "权限提示");
        message = bundle.getString("message", "为了应用可以正常使用，请您点击确认申请权限。");
        negativeButton = bundle.getString("negativeButton", "取消");
        psitiveButton = bundle.getString("psitiveButton", "确定");
        packageName = getPackageName();
        checkPermissions(false);
    }

    /**
     * 判断是否需要申请权限
     *
     * @param isAllRequested ,,
     */
    private void checkPermissions(boolean isAllRequested) {
        ArrayList<String> deniedPermissions = PermissionUtil.getDeniedPermissions(this, this.permissions);
        boolean showRationale = false;
        for (String permission : deniedPermissions) {
            if (!hasRequestedSystemAlertWindow && permission.equals(Manifest.permission.SYSTEM_ALERT_WINDOW)) {
                permissionSystemAlertWindow = Manifest.permission.SYSTEM_ALERT_WINDOW;//判断是否需要申请SYSTEM_ALERT_WINDOW权限
            } else if (!hasRequestedWriteSettings && permission.equals(Manifest.permission.WRITE_SETTINGS)) {
                permissionWriteSettings = Manifest.permission.WRITE_SETTINGS;//判断是否需要申请WRITE_SETTINGS权限
            } else if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                showRationale = true;
            }
        }
        if (!PermissionUtil.isNeedPermission() || deniedPermissions.isEmpty()) {
            permissionGranted();
        } else if (showRationale && !TextUtils.isEmpty(message)) {
            showRationaleMessage(deniedPermissions);
        } else if (isAllRequested) {
            permissionDenied(deniedPermissions);
        } else {
            requestPermissions(deniedPermissions);
        }
    }

    /**
     * 跳转到权限管理界面
     */
    @TargetApi(value = Build.VERSION_CODES.M)
    private void requestPermissions(ArrayList<String> needPermissions) {
        if (!hasRequestedSystemAlertWindow && !TextUtils.isEmpty(permissionSystemAlertWindow)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + packageName));
            startActivityForResult(intent, REQ_CODE_REQUEST_SYSTEM_ALERT_WINDOW);
        } else if (!hasRequestedWriteSettings && !TextUtils.isEmpty(permissionWriteSettings)) {
            //second WRITE_SETTINGS
            Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS, Uri.parse("package:" + packageName));
            startActivityForResult(intent, REQ_CODE_REQUEST_WRITE_SETTING);
        } else {
            //other permission
            ActivityCompat.requestPermissions(this, needPermissions.toArray(new String[needPermissions.size()]), REQ_CODE_PERMISSION_REQUEST);
        }
    }

    /**
     * 显示申请权限说明
     */
    private void showRationaleMessage(final ArrayList<String> deniedPermissions) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton(negativeButton, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        permissionDenied(permissions);
                    }
                })
                .setPositiveButton(psitiveButton, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        requestPermissions(deniedPermissions);
                    }
                }).create()
                .show();
    }

    /**
     * 申请权限失败，提示用户进入权限管理界面设置权限
     */
    private void gotoSetting() {
        try {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(Uri.parse("package:" + packageName));
            startActivityForResult(intent, REQ_CODE_REQUEST_SETTING);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            Intent intent = new Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
            startActivityForResult(intent, REQ_CODE_REQUEST_SETTING);
        }
    }

    /**
     * 权限申请成功
     */
    private void permissionGranted() {
        if (permissionListener != null) {
            permissionListener.permissionGranted();
            permissionListener = null;
        }
        finish();
        overridePendingTransition(0, 0);
    }

    private void permissionDenied(ArrayList<String> deniedPermissions) {
        String[] strings = new String[deniedPermissions.size()];
        permissionDenied(deniedPermissions.toArray(strings));
    }

    private void permissionDenied(String[] deniedpermissions) {
        if (permissionListener != null) {
            permissionListener.permissionDenied(deniedpermissions);
            permissionListener = null;
        }
        finish();
        overridePendingTransition(0, 0);
    }

    public interface PermissionListener {
        /**
         * 全部获取成功
         */
        void permissionGranted();

        /**
         * 未获取到的权限
         *
         * @param permissions 未获取到的权限
         */
        void permissionDenied(String[] permissions);
    }


}
