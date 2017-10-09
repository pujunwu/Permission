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
                }).request();
</code></pre>
更多调用请看:<br />https://github.com/pujunwu/Permission/blob/master/app/src/main/java/com/junwu/example/MainActivity.java
<h1>参数说明</h1>
<pre class="hljs undefined"><code>Context mContext;//上下文，可选
    String[] permissions;//需要申请的权限
    String title = "权限提示";
    String message = "为了应用可以正常使用，请您点击确认申请权限。";
    String negativeButton = "取消";
    String psitiveButton = "确定";
    boolean isShowDialog = false;//用户勾选了不再提示，导致以后无法申请权限，如果设置为true就可提示用户再次申请权限</code></pre>
    提示用户需要获取权限的对话框可以完全自定义，回调事件：
    <pre class="hljs undefined"><code>PermissionParam.getParamVideo()
                .setShowDialog(true)
                .getPermissionsApply()
                .setOnShowRationaleListener</pre></code>
<p>引用</p>
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
  
  compile 'com.github.pujunwu:Permission:v0.2.3'
  
