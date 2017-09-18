<div class="span6 preview" style="max-height: 1280px; min-height: 1280px;"><h1 class="title mousetrap">无标题文章</h1><div class="content mousetrap"><h1>Permission</h1>
<p>权限申请</p>
<h1>使用方式如下：</h1>
<pre class="hljs undefined"><code>PermissionsApply.getPermissionsApply(
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
</code></pre>
<h1>参数说明</h1>
<pre class="hljs undefined"><code>private Context mContext;
private String[] permissions;//需要申请的权限
private String title = "权限提示";
private String message = "为了应用可以正常使用，请您点击确认申请权限。";
private String negativeButton = "取消";
private String psitiveButton = "确定";
private boolean needGotoSetting = false;// 是否显示跳转到应用权限设置界面</code></pre></div></div>
