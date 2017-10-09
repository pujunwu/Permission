package com.junwu.permission;

/**
 * ===============================
 * 描    述：回调相关处理
 * 作    者：pjw
 * 创建日期：2017/9/29 9:10
 * 所有接口都用HashMap存储是防止双击的情况发生，或者多次调用
 * ===============================
 */
public class Callback {

    //处理结束回调接口
    private static OnPermissionListener sOnPermissionListener = null;
    //显示提示框回调接口,如果为空则显示默认提示框
    private static OnShowRationaleListener sOnShowRationaleListener = null;

    /********************************权限申请完成回调***********************************/

    static void addOnPermissionListener(String key, OnPermissionListener listener) {
        sOnPermissionListener = listener;
    }

    static OnPermissionListener getOnPermissionListener(String key) {
        return sOnPermissionListener;
    }

    /**
     * 回调接口
     */
    interface OnPermissionListener {
        /**
         * 回调方法
         *
         * @param permissions 未获取到的权限
         */
        void onCallback(String[] permissions);
    }

    /********************************显示提示框回调************************************/

    static void addOnShowRationaleListener(String key, OnShowRationaleListener listener) {
        sOnShowRationaleListener = listener;
    }

    static OnShowRationaleListener getOnShowRationaleListener(String key) {
        return sOnShowRationaleListener;
    }

    /**
     * 显示提示框
     */
    public interface OnShowRationaleListener {
        /**
         * 显示提示框
         *
         * @param listener 回调接口
         */
        void onShowRationale(String[] deniedPermissions,OnCallbackListener listener);
    }

    /********************************提示框按钮点击回调************************************/

    /**
     * 提示用户是否跳转到系统权限管理界面
     */
    public interface OnCallbackListener {
        /**
         * 取消
         */
        void onNegative();

        /**
         * 确定
         */
        void onPsitive();
    }

    /*****************************权限申请操作完成，回调申请结果*****************************/
    /**
     * 申请权限回调接口
     */
    public interface OnPermissionCallbackListener {
        /**
         * 回调方法
         *
         * @param permissions 未申请到的权限列表
         */
        void onCallback(String[] permissions);
    }

    public interface OnSuccessErrorListener {
        /**
         * 申请成功回调接口
         */
        void onSuccess();

        /**
         * 申请失败回调接口
         *
         * @param permissions 为申请到的权限
         */
        void onError(String[] permissions);
    }

}
