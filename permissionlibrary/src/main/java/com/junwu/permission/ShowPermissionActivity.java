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
    //    //是否跳转到获取修改系统设置权限界面，这个权限需要单独处理Manifest.permission.WRITE_SETTINGS
//    private boolean hasRequestedWriteSettings = false;
//    //是否跳转到获取显示在其他应用上面的权限界面，Manifest.permission.SYSTEM_ALERT_WINDOW
//    private boolean hasRequestedSystemAlertWindow = false;
//    private String permissionSystemAlertWindow;
//    private String permissionWriteSettings;
//    private String permissionStr;

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
    private Callback.OnShowRationaleListene mOnShowRationaleListene;
    //需要获取的权限类型
    private String[] permissionTypes = null;
    //true跳转到权限设置界面，false跳转到特殊权限申请界面
    private boolean isGoToSetting = false;

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
        mOnShowRationaleListene = Callback.getOnShowRationaleListene(bundle.getString("callbackKey"));
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
                systemSpecialPermission();
                break;
            case REQ_CODE_REQUEST_WRITE_SETTING: //系统设置权限回调
                systemSpecialPermission();
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
            permissionGranted();//所有权限获取成功
        } else {
            //第一次申请权限还未完成
            if (requestPermissions(deniedPermissions, permissionTypes)) {
                return;
            }
            if (!isShowDialog) {//提示用户需要如下权限
                permissionDenied(deniedPermissions);
                return;
            }
            permissionTypes = getShowRequestPermission(deniedPermissions);
            if (!getPermissionType(permissionTypes, "true")) {
                permissionDenied(deniedPermissions);
                return;
            }
            isGoToSetting = true;
            showRationaleMessage(deniedPermissions);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onNewIntent(getIntent());
    }

    /**
     * 判断特殊权限
     * SYSTEM_ALERT_WINDOW权限是否申请成功
     */
    private void systemSpecialPermission() {
        ArrayList<String> deniedPermissions = PermissionUtil.getDeniedPermissions(this, this.permissions);
        if (deniedPermissions == null || deniedPermissions.isEmpty()) {
            permissionGranted();//所有权限申请成功
        } else if (requestPermissions(deniedPermissions, permissionTypes)) {
            //第一次申请权限还未完成
            return;
        }
        if (!isShowDialog) {
            permissionDenied(deniedPermissions);
            return;
        }
        isGoToSetting = false;
        showRationaleMessage(deniedPermissions);
    }

    /**
     * 判断是否需要申请权限
     */
    private void checkPermissions() {
        ArrayList<String> deniedPermissions = getDeniedPermissions(this, this.permissions);
        if (deniedPermissions == null || deniedPermissions.isEmpty()) {
            permissionGranted();
            return;
        }
        if (requestPermissions(deniedPermissions, permissionTypes)) {
            //第一次申请权限还未完成
            return;
        }
        permissionDenied(deniedPermissions);
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
     * 显示申请权限说明
     */
    private void showRationaleMessage(final ArrayList<String> deniedPermissions) {
        if (!isShowDialog) {//如果设置为不提示
            permissionDenied(deniedPermissions);
            return;
        }
        if (mOnShowRationaleListene != null) {
            mOnShowRationaleListene.onShowRationale(new Callback.OnCallbackListener() {
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
                }).create()
                .show();
    }

    /**
     * 权限申请判断
     */
    private void isRequestPermissions(ArrayList<String> deniedPermissions) {
        if (isGoToSetting) {
            gotoSetting(permissionTypes);
        } else {
            permissionTypes = getShowRequestPermission(deniedPermissions);
            requestPermissions(deniedPermissions, permissionTypes);
        }
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

    private void permissionDenied(String[] deniedpermissions) {
        if (mPermissionListener != null) {
            mPermissionListener.onCallback(deniedpermissions);
        }
        finish();
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onDestroy() {
        mPermissionListener = null;
        super.onDestroy();
    }
}
