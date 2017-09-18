# Permission
权限申请
#使用方式如下：
PermissionsApply.getPermissionsApply(<br>
                >Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS,<br>
                >Manifest.permission.WRITE_EXTERNAL_STORAGE)<br>
                >.setNeedGotoSetting(false)<br>
                >.setOnListener(new PermissionsApply.OnListener() {<br>
                    >>@Override<br>
                    >>public void callback(String[] permissions) {<br>
                        >>>if (permissions == null) {<br>
                            >>>>Toast.makeText(MainActivity.this, "所有权限已经申请成功", Toast.LENGTH_SHORT).show();<br>
                            >>>>return;<br>
                        >>>}<br>
                        >>>Toast.makeText(MainActivity.this, "未申请成功的权限如下：", Toast.LENGTH_SHORT).show();<br>
                        >>>//申请权限后操作<br>
                        >>>for (String per : permissions) {<br>
                            >>>>Toast.makeText(MainActivity.this, per, Toast.LENGTH_SHORT).show();<br>
                        >>>}<br>
                    >>>}<br>
                >>}).apply();<br>
返回permissions为空即所有权限申请成功，反之则有权限为获取成功<br>
#参数信息如下：<br>
    >private Context mContext;<br>
    >private String[] permissions;//需要申请的权限<br>
    >private String title = "权限提示";<br>
    >private String message = "为了应用可以正常使用，请您点击确认申请权限。";<br>
    >private String negativeButton = "取消";<br>
    >private String psitiveButton = "确定";<br>
    >private boolean needGotoSetting = false;// 是否显示跳转到应用权限设置界面<br>
