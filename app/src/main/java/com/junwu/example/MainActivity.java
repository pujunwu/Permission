package com.junwu.example;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.junwu.permission.Callback;
import com.junwu.permission.PermissionParam;
import com.junwu.permission.utils.ApplicationUtil;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //建议在Application中设置
        ApplicationUtil.instance().setApplication(getApplication());
    }

    public void onClickListener(View view) {
//        Toast.makeText(this, ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA) + "", Toast.LENGTH_SHORT).show();
        PermissionParam.getParam().setPermissions(Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.READ_PHONE_STATE)//申请sdCard操作权限
                .setShowDialog(true)//用户勾选了不再提示，导致以后无法申请权限，如果设置为true就可提示用户再次申请权限
                .getPermissionsApply()
                .setOnPermissionCallbackListener(new Callback.OnPermissionCallbackListener() {
                    @Override
                    public void onCallback(String[] permissions) {
                        permissions(permissions);
                    }
                }).request();
    }

    public void onClickListener2(View view) {
        PermissionParam.getParamCamera()
                .setShowDialog(true)
                .getPermissionsApply()
                .setOnSuccessErrorListener(new Callback.OnSuccessErrorListener() {
                    @Override
                    public void onSuccess() {
                        permissions(null);
                    }

                    @Override
                    public void onError(String[] permissions) {
                        permissions(permissions);
                    }
                }).request();
    }

    /**
     * 如果申请权限失败，将提示用户是否进入系统权限管理页面
     * setShowDialog(true)才会有提示
     * setShowDialog(true)但是没有设置setOnShowRationaleListene()会弹出默认提示框
     */
    public void onClickListener3(View view) {
        PermissionParam.getParamVideo()
                .setShowDialog(true)
                .getPermissionsApply()
                .setOnShowRationaleListener(new Callback.OnShowRationaleListener() {
                    @Override
                    public void onShowRationale(Context context, String[] deniedPermissions, final Callback.OnCallbackListener listener) {
                        //注意context,是使用传入的context
                        new AlertDialog.Builder(context)
                                .setTitle("权限申请")
                                .setMessage("为了应用可以正常使用，请您点击确认申请权限。")
                                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        listener.onNegative();
                                    }
                                })
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        listener.onPsitive();
                                    }
                                }).create().show();
                    }
                })
                .setOnSuccessErrorListener(new Callback.OnSuccessErrorListener() {
                    @Override
                    public void onSuccess() {
                        permissions(null);
                    }

                    @Override
                    public void onError(String[] permissions) {
                        permissions(permissions);
                    }
                }).request();
    }

    public void onClickListener4(View view) {
        PermissionParam.getParam()
                .setPermissions(
                        Manifest.permission.CAMERA,//相机
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,//sdCard权限
                        Manifest.permission.SYSTEM_ALERT_WINDOW,//允许在其他应用上层显示权限
                        Manifest.permission.WRITE_SETTINGS)//修改系统设置权限
                .setShowDialog(true)
                .getPermissionsApply()
                .setOnSuccessErrorListener(new Callback.OnSuccessErrorListener() {
                    @Override
                    public void onSuccess() {
                        permissions(null);
                    }

                    @Override
                    public void onError(String[] permissions) {
                        permissions(permissions);
                    }
                }).request();
    }

    private void permissions(String[] permissions) {
        if (permissions == null || permissions.length == 0) {
            Toast.makeText(MainActivity.this, "所有权限已经申请成功", Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(MainActivity.this, "未申请成功的权限如下：", Toast.LENGTH_SHORT).show();
        //申请权限后操作
        for (String per : permissions) {
            Toast.makeText(MainActivity.this, per, Toast.LENGTH_SHORT).show();
        }
    }

}
