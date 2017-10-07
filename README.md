<p>权限申请</p>
<h1>使用方式如下：</h1>
<pre class="hljs undefined"><code>PermissionParam.getParam()
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
                }).apply();
</code></pre>
更多调用请看:https://github.com/pujunwu/Permission/blob/master/app/src/main/java/com/junwu/example/MainActivity.java
<h1>参数说明</h1>
<pre class="hljs undefined"><code>Context mContext;//上下文，可选
    String[] permissions;//需要申请的权限
    String title = "权限提示";
    String message = "为了应用可以正常使用，请您点击确认申请权限。";
    String negativeButton = "取消";
    String psitiveButton = "确定";
    boolean isShowDialog = false;//如果进入系统权限管理界面后，权限还未获取成功，就提示是否提示重新获取</code></pre>
    提示用户需要获取权限的对话框可以完全自定义，回调事件：
    <pre class="hljs undefined"><code>PermissionParam.getParamWifi()
                .setShowDialog(true)
                .getPermissionsApply()
                .setOnShowRationaleListener</pre></code>
