package com.junwu.permission;

import android.content.Context;
import android.util.Log;

import com.junwu.permission.utils.ContextUtil;

/**
 * ===============================
 * 描    述：权限申请
 * 作    者：pjw
 * 创建日期：2017/9/7 17:28
 * ===============================
 */
public class PermissionsApply {
    private Context mContext;
    private String[] permissions;//需要申请的权限
    private String title = "权限提示";
    private String message = "为了应用可以正常使用，请您点击确认申请权限。";
    private String negativeButton = "取消";
    private String psitiveButton = "确定";
    private boolean needGotoSetting = false;// 是否显示跳转到应用权限设置界面
    private boolean isSucceedCallback = false;//是否是所有权限申请成功才回调
    private OnListener mOnListener;//回调接口
	//提示框提示内容，支撑可改变
	//布局可自定义

    private PermissionsApply() {
    }

    public PermissionsApply setContext(Context context) {
        mContext = context;
        return this;
    }

    private PermissionsApply setPermissions(String[] permissions) {
        this.permissions = permissions;
        return this;
    }

    public PermissionsApply setTitle(String title) {
        this.title = title;
        return this;
    }

    public PermissionsApply setMessage(String message) {
        this.message = message;
        return this;
    }

    public PermissionsApply setNegativeButton(String negativeButton) {
        this.negativeButton = negativeButton;
        return this;
    }

    public PermissionsApply setPsitiveButton(String psitiveButton) {
        this.psitiveButton = psitiveButton;
        return this;
    }

    public PermissionsApply setNeedGotoSetting(boolean needGotoSetting) {
        this.needGotoSetting = needGotoSetting;
        return this;
    }

    public PermissionsApply setOnListener(OnListener onListener) {
        mOnListener = onListener;
        return this;
    }

    public PermissionsApply setSucceedCallback(boolean succeedCallback) {
        isSucceedCallback = succeedCallback;
        return this;
    }

    /**
     * 发起申请权限，必须调用当前方法，否则权限申请不会有任何反馈
     */
    public void apply() {
        Context context = mContext;
        if (context == null) {
            context = ContextUtil.getContext();
        }
        if (context == null) {
            Log.d("PermissionsApp", "context不能为空，请调用setContext()或者ApplicationUtil.setApplication()方法设置context");
            listener(permissions);
            return;
        }
        if (permissions == null || permissions.length == 0) {
            Log.d("PermissionsApp", "没有需要申请的权限，请调用setPermissions()方法设置需要申请的权限");
            listener(null);
            return;
        }
        ShowPermissionActivity.start(context, permissions, title, message, negativeButton, psitiveButton, needGotoSetting, new ShowPermissionActivity.PermissionListener() {
            @Override
            public void permissionGranted() {
                listener(null);
            }

            @Override
            public void permissionDenied(String[] permissions) {
                if (isSucceedCallback) {
                    if ((permissions == null || permissions.length == 0))
                        listener(permissions);
                } else {
                    listener(permissions);
                }
            }
        });
    }

    /**
     * 调用回调
     *
     * @param permissions 没有申请到的权限列表
     */
    private void listener(String[] permissions) {
        if (mOnListener != null) {
            mOnListener.callback(permissions);
        }
    }

    /**
     * 申请权限回调接口
     */
    public interface OnListener {
        /**
         * 回调方法
         *
         * @param permissions 未申请到的权限列表
         */
        void callback(String[] permissions);
    }

    /**
     * 申请权限的开始
     *
     * @param permissions 需要申请权限的列表
     * @return 申请权限操作类
     */
    public static PermissionsApply getPermissionsApply(String... permissions) {
        return new PermissionsApply().setPermissions(permissions);
    }

    /**
     * 申请权限的开始
     *
     * @param context 上下文
     * @return 申请权限操作类
     */
    public static PermissionsApply getPermissionsApply(Context context) {
        return new PermissionsApply().setContext(context);
    }

}
