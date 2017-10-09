package com.junwu.permission;

import android.content.Context;
import android.util.Log;

import com.junwu.permission.utils.ContextUtil;
import com.junwu.permission.utils.DI;
import com.junwu.permission.utils.PermissionUtil;

/**
 * ===============================
 * 描    述：权限申请
 * 作    者：pjw
 * 创建日期：2017/9/7 17:28
 * ===============================
 */
public class PermissionsApply {

    //权限申请参数类
    private PermissionParam mParam;
    private Callback.OnPermissionCallbackListener mOnPermissionCallbackListener;//回调接口
    private Callback.OnSuccessErrorListener mSuccessErrorListener;//回调接口
    //显示框回调接口，如果设置了，在需要提示用户是否进入系统权限管理界面时会调用此接口
    //如果需要自定义提示框，可在onShowRationale方法中完成，用户点击确定或取消时调用OnCallbackListener此接口即可
    //如果不设置则根据needGotoSetting参数判断显示出默认提示框
    private Callback.OnShowRationaleListener mOnShowRationaleListener;

    /**
     * 构造函数，只能在当前包可见
     *
     * @param mParam 权限申请参数
     */
    PermissionsApply(PermissionParam mParam) {
        this.mParam = mParam;
    }

    public PermissionsApply setOnPermissionCallbackListener(Callback.OnPermissionCallbackListener onListener) {
        mOnPermissionCallbackListener = onListener;
        return this;
    }

    public PermissionsApply setOnShowRationaleListener(Callback.OnShowRationaleListener onShowRationaleListener) {
        mOnShowRationaleListener = onShowRationaleListener;
        return this;
    }

    public PermissionsApply setOnSuccessErrorListener(Callback.OnSuccessErrorListener successErrorListener) {
        mSuccessErrorListener = successErrorListener;
        return this;
    }

    /**
     * 发起申请权限，必须调用当前方法，否则权限申请不会有任何反馈
     */
    public void request() {
        if (!PermissionUtil.isNeedPermission()) {
            listener(null);
            return;
        }
        if (mParam == null) {
            Log.d("PermissionsApp", "申请权限参数为空" + new DI().funLog(2));
            return;
        }
        Context context = mParam.mContext;
        if (context == null) {
            context = ContextUtil.getContext();
        }
        if (context == null) {
            Log.d("PermissionsApp", "context不能为空，请调用setContext()或者ApplicationUtil.setApplication()方法设置context()" + new DI().funLog(2));
            listener(mParam.permissions);
            return;
        }
        if (mParam.permissions == null || mParam.permissions.length == 0) {
            Log.d("PermissionsApp", "没有需要申请的权限，请调用setPermissions()方法设置需要申请的权限" + new DI().funLog(2));
            listener(null);
            return;
        }
        //设置回调接口
        Callback.addOnPermissionListener(this.toString(), new Callback.OnPermissionListener() {
            @Override
            public void onCallback(String[] permissions) {
                listener(permissions);
            }
        });
        if (mOnShowRationaleListener != null) {
            Callback.addOnShowRationaleListener(this.toString(), mOnShowRationaleListener);
        }
        //启动申请权限
        ShowPermissionActivity.start(context, mParam.permissions, mParam.title, mParam.message,
                mParam.negativeButton, mParam.psitiveButton,
                mParam.isShowDialog, this.toString());
    }

    /**
     * 调用回调
     *
     * @param permissions 没有申请到的权限列表
     */
    private void listener(String[] permissions) {
        if (mOnPermissionCallbackListener != null) {
            mOnPermissionCallbackListener.onCallback(permissions);
        } else if (mSuccessErrorListener != null) {
            if (permissions == null) {
                mSuccessErrorListener.onSuccess();
            } else {
                mSuccessErrorListener.onError(permissions);
            }
        }
    }

//    /**
//     * 申请权限的开始
//     *
//     * @param permissions 需要申请权限的列表
//     * @return 申请权限操作类
//     */
//    public static PermissionsApply getPermissionsApply(String... permissions) {
//        return getPermissionsApply(new PermissionParam().setPermissions(permissions));
//    }
//
//    /**
//     * 申请权限
//     *
//     * @param param 申请权限配置的参数
//     * @return 申请权限操作类
//     */
//    public static PermissionsApply getPermissionsApply(PermissionParam param) {
//        return new PermissionsApply(param);
//    }

}
