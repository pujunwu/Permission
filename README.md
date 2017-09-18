Permission

权限申请

使用方式如下：

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
参数说明

private Context mContext;
private String[] permissions;//需要申请的权限
private String title = "权限提示";
private String message = "为了应用可以正常使用，请您点击确认申请权限。";
private String negativeButton = "取消";
private String psitiveButton = "确定";
private boolean needGotoSetting = false;// 是否显示跳转到应用权限设置界面
