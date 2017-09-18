package com.junwu.example;

import android.Manifest;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.junwu.permission.PermissionsApply;
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
        PermissionsApply.getPermissionsApply(
                Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .setNeedGotoSetting(false)
                .setOnListener(new PermissionsApply.OnListener() {
                    @Override
                    public void callback(String[] permissions) {
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


}
