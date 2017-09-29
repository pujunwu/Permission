package com.junwu.example;

import android.Manifest;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
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
//        Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS, Uri.parse("package:" + getPackageName()));
//        startActivityForResult(intent, REQ_CODE_REQUEST_WRITE_SETTING);MOUNT_UNMOUNT_FILESYSTEMS
//        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_SETTINGS}, ShowPermissionActivity.REQ_CODE_PERMISSION_REQUEST);
//        Toast.makeText(this, ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA) + "", Toast.LENGTH_SHORT).show();

        PermissionParam.getParamSDCard()//申请sdCard操作权限
                .setShowDialog(true)//如果进入系统权限管理界面后，权限还未获取成功，就提示是否提示重新获取
                .getPermissionsApply()
                .setOnListener(new Callback.OnPermissionCallbackListener() {
                    @Override
                    public void onCallback(String[] permissions) {
                        if (permissions == null) {
                            Toast.makeText(MainActivity.this, "所有权限已经申请成功", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Toast.makeText(MainActivity.this, "未申请成功的权限如下：", Toast.LENGTH_SHORT).show();
                        //申请权限后操作
                        for (String per : permissions) {
                            Toast.makeText(MainActivity.this, per, Toast.LENGTH_SHORT).show();
                        }
                    }
                }).apply();
    }

    public void onClickListener2(View view) {
        PermissionParam.getParam()
                .setPermissions(Manifest.permission.SYSTEM_ALERT_WINDOW, Manifest.permission.SYSTEM_ALERT_WINDOW, Manifest.permission.WRITE_SETTINGS)
                .setShowDialog(true)
                .getPermissionsApply()
                .setOnSuccessErrorListener(new Callback.OnSuccessErrorListener() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(MainActivity.this, "所有权限已经申请成功", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(String[] permissions) {
                        Toast.makeText(MainActivity.this, "未申请成功的权限如下：", Toast.LENGTH_SHORT).show();
                        //申请权限后操作
                        for (String per : permissions) {
                            Toast.makeText(MainActivity.this, per, Toast.LENGTH_SHORT).show();
                        }
                    }
                }).apply();
    }

    /**
     * 如果申请权限失败，将提示用户是否进入系统权限管理页面
     * setNeedGotoSetting(true)才会有提示
     * 如果setNeedGotoSetting(true)但是没有设置setOnShowRationaleListene()会弹出默认提示框
     */
    public void onClickListener3(View view) {
        PermissionParam.getParamWifi()
                .setShowDialog(true)
                .getPermissionsApply()
                .setOnShowRationaleListene(new Callback.OnShowRationaleListene() {
                    @Override
                    public void onShowRationale(final Callback.OnCallbackListener listener) {
                        new AlertDialog.Builder(MainActivity.this)
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
                                }).create()
                                .show();
                    }
                })
                .setOnSuccessErrorListener(new Callback.OnSuccessErrorListener() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(MainActivity.this, "所有权限已经申请成功", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(String[] permissions) {
                        Toast.makeText(MainActivity.this, "未申请成功的权限如下：", Toast.LENGTH_SHORT).show();
                        //申请权限后操作
                        for (String per : permissions) {
                            Toast.makeText(MainActivity.this, per, Toast.LENGTH_SHORT).show();
                        }
                    }
                }).apply();
    }

}
