package com.junwu.permission;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import java.util.ArrayList;

import static com.junwu.permission.utils.PermissionUtil.getDeniedPermissions;

/**
 * ===============================
 * 描    述：实际处理权限申请的Activity
 * 作    者：pjw
 * 创建日期：2018/2/5 11:05
 * ===============================
 */
public class PermissionApplicationActivity extends Activity {

    //回调接口
    private Callback.OnPermissionListener mPermissionListener;
    //提示框显示回调接口
    private Callback.OnShowRationaleListener mOnShowRationaleListener;
    //参数
    private PermissionParam mParam;

    //需要获取的权限类型
    private String[] permissionTypes = null;
    //提示调用次数
    private int tipCount = 0;
    //工具类
    private PermissionActivityUtil mActivityUtil;
    //是否监听焦点改变
    private int isMonitoringFocus = 0;
    //焦点是否改变
    private boolean isFocusChange = false;

    static void startToActivity(Context context, PermissionParam mParam) {
        Intent intent = new Intent(context, PermissionApplicationActivity.class);
//        intent.putExtra("Param", mParam);
        intent.putExtra("permissions", mParam.permissions);
        intent.putExtra("title", mParam.title);
        intent.putExtra("message", mParam.message);
        intent.putExtra("negativeButton", mParam.negativeButton);
        intent.putExtra("positiveButton", mParam.positiveButton);
        intent.putExtra("isPermissionsPrompt", mParam.isPermissionsPrompt);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
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
        if (intent == null) {
            permissionDenied(new String[]{});
            return;
        }
        setIntent(intent);
        //参数
        mParam = PermissionParam.getParam();
        mParam.permissions = intent.getStringArrayExtra("permissions");
        mParam.title = intent.getStringExtra("title");
        mParam.message = intent.getStringExtra("message");
        mParam.negativeButton = intent.getStringExtra("negativeButton");
        mParam.positiveButton = intent.getStringExtra("positiveButton");
        mParam.isPermissionsPrompt = intent.getBooleanExtra("isPermissionsPrompt", mParam.isPermissionsPrompt);
        //回调接口
        mPermissionListener = Callback.getOnPermissionListener();
        mOnShowRationaleListener = Callback.getOnShowRationaleListener();
        //重置提示次数
        tipCount = 0;
        //权限申请
        ArrayList<String> deniedPermissions = getDeniedPermissions(this, mParam.permissions);
        if (deniedPermissions == null || deniedPermissions.isEmpty()) {
            permissionGranted();
            return;
        }
        mActivityUtil = new PermissionActivityUtil(this);
        //判断权限类型
        permissionTypes = mActivityUtil.getShowRequestPermission(deniedPermissions);
        //发起第一次申请完成，要permissionTypes里面的类型都申请一遍才算完成
        mActivityUtil.requestPermissions(deniedPermissions, permissionTypes);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PermissionActivityUtil.REQ_CODE_REQUEST_SETTING://进入权限管理界面回调
                checkPermissions();
                break;
            case PermissionActivityUtil.REQ_CODE_REQUEST_SYSTEM_ALERT_WINDOW: //显示在其他应用窗口回调
                checkPermissions();
                break;
            case PermissionActivityUtil.REQ_CODE_REQUEST_WRITE_SETTING: //系统设置权限回调
                checkPermissions();
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != PermissionActivityUtil.REQ_CODE_PERMISSION_REQUEST) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }
        checkPermissions();
    }

    /**
     * 判断是否需要申请权限
     */
    private void checkPermissions() {
        ArrayList<String> deniedPermissions = getDeniedPermissions(this, mParam.permissions);
        if (deniedPermissions == null || deniedPermissions.isEmpty()) {
            //所有权限请求成功
            permissionGranted();
            return;
        }
        if (mActivityUtil.requestPermissions(deniedPermissions, permissionTypes)) {
            //第一次申请权限还未完成
            return;
        }
        if (!mParam.isPermissionsPrompt) {//如果设置为不提示
            permissionDenied(deniedPermissions);
            return;
        }
        if (isMonitoringFocus == 1 && !isFocusChange) {//&& mActivityUtil.getPermissionType(permissionTypes, "true")
            //当用户点击不在提示权限申请，并且拒绝权限申请时
            //这时调用ActivityCompat.requestPermissions();是不会有权限申请弹框
            if (!mActivityUtil.gotoSetting(permissionTypes, "true")) {
                //调用申请权限失败
                permissionDenied(getDeniedPermissions(this, mParam.permissions));
            }
            return;
        }
        permissionTypes = mActivityUtil.getShowRequestPermission(deniedPermissions);
        showRationaleMessage(deniedPermissions, permissionTypes);
    }

    /**
     * 显示申请权限说明
     */
    private void showRationaleMessage(final ArrayList<String> deniedPermissions, final String[] permissionTypes) {
        tipCount++;
        if (tipCount > 1) {
            permissionDenied(deniedPermissions);
            return;
        }
        if (mOnShowRationaleListener != null) {
            String[] strings = new String[deniedPermissions.size()];
            mOnShowRationaleListener.onShowRationale(this, deniedPermissions.toArray(strings), new Callback.OnCallbackListener() {
                @Override
                public void onNegative() {
                    permissionDenied(deniedPermissions);
                }

                @Override
                public void onPsitive() {
                    isRequestPermissions(deniedPermissions, permissionTypes);
                }
            });
            return;
        }
        new AlertDialog.Builder(this)
                .setTitle(mParam.title)
                .setMessage(mParam.message)
                .setNegativeButton(mParam.negativeButton, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        permissionDenied(deniedPermissions);
                    }
                })
                .setPositiveButton(mParam.positiveButton, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        isRequestPermissions(deniedPermissions, permissionTypes);
                    }
                }).create().show();
    }

    /**
     * 权限申请判断
     */
    private void isRequestPermissions(ArrayList<String> deniedPermissions, String[] permissionTypes) {
        isMonitoringFocus = 1;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d("123", "设置焦点是否改变:");
                isMonitoringFocus = 2;
            }
        }, 100);
        mActivityUtil.requestPermissions(deniedPermissions, permissionTypes);
//        if (mActivityUtil.getPermissionType(permissionTypes, "true")) {
//            if (!mActivityUtil.gotoSetting(permissionTypes, "true")) {
//                //调用申请权限失败
//                permissionDenied(getDeniedPermissions(this, mParam.permissions));
//            }
//            return;
//        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (isMonitoringFocus == 2) {
            Log.d("123", "onWindowFocusChanged: 焦点变了");
            isFocusChange = true;
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
