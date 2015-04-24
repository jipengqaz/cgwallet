package cgtz.com.cgwallet.utils;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import cgtz.com.cgwallet.MApplication;
import cgtz.com.cgwallet.bean.JsonBean;
import cgtz.com.cgwallet.utility.Constants;
import cn.jpush.android.data.r;

/**
 * 工具类
 * Created by Administrator on 2015/4/10.
 */
public class Utils {

    /**
     * 获取登录的id
     * @return
     */
    public static String getUserId(){
        return MApplication.getUser_id();
//        SharedUtils sharedUtils = new SharedUtils(context, Constants.CONFIG);
//        return sharedUtils.getString(Constants.LOGIN_USER_ID,"");
    }

    /**
     * 保存登录后的id
     * @param userId
     */
    public static void saveUserId(String userId){
        MApplication.setUser_id(userId);
//        SharedUtils sharedUtils = new SharedUtils(context, Constants.CONFIG);
//        sharedUtils.saveString(Constants.LOGIN_USER_ID, userId);
    }
    /**
     * 获取登录的token
     * @return
     */
    public static String getToken(){
        return MApplication.getToken();
//        SharedUtils sharedUtils = new SharedUtils(context, Constants.CONFIG);
//        return sharedUtils.getString(Constants.LOGIN_TOKEN,"");
    }

    /**
     * 保存登录后的token
     * @param token
     */
    public static void saveToken(String token){
        MApplication.setToken(token);
//        SharedUtils sharedUtils = new SharedUtils(context, Constants.CONFIG);
//        sharedUtils.saveString(Constants.LOGIN_TOKEN, token);
    }

    /**
     * 保存登录手机号
     * @param context
     * @param mobile
     */
    public static void saveMobile(Context context,String mobile){
        SharedUtils sharedUtils = new SharedUtils(context, Constants.CONFIG);
        sharedUtils.saveString(Constants.LOGIN_PHONE, mobile);
    }
    /**
     * 获取登录密码
     * @param context
     * @return
     */
    public static String getLoginPwd(Context context){
        SharedUtils sharedUtils = new SharedUtils(context, Constants.CONFIG);
        return sharedUtils.getString(Constants.LOGIN_PASSWORD,"");
    }
    /**
     * 保存登录密码
     * @param context
     * @param pwd
     */
    public static void saveLoginPwd(Context context,String pwd){
        SharedUtils sharedUtils = new SharedUtils(context, Constants.CONFIG);
        sharedUtils.saveString(Constants.LOGIN_PASSWORD, pwd);
    }
    /**
     * 获取登录的手机号
     * @param context
     * @return
     */
    public static String getUserPhone(Context context){
        SharedUtils sharedUtils = new SharedUtils(context, Constants.CONFIG);
        return sharedUtils.getString(Constants.LOGIN_PHONE,"");
    }

    /**
     * 退出账号,删除登录密码  清空  该账户的数据
     * @param context
     * @return
     */
    public static void loginExit(Context context){
        SharedUtils sharedUtils = new SharedUtils(context,Constants.CONFIG);
        sharedUtils.remove(Constants.LOGIN_PASSWORD);
//        sharedUtils.remove(Constants.LOGIN_TOKEN);
        MApplication.setToken("");
        MApplication.setUser_id("");
    }

    /**
     * 设置用户手机号   中间4位为*号
     * @param phoneNumber  手机号吗
     * @return
     */
    public static String getProtectedMobile(String phoneNumber) {
        if (TextUtils.isEmpty(phoneNumber) || phoneNumber.length() < 11) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        builder.append(phoneNumber.subSequence(0, 3));
        builder.append("****");
        builder.append(phoneNumber.subSequence(7, 11));
        return builder.toString();
    }

    /**
     * 删除手势密码
     * @param context
     * @param phone
     */
    public static void removePassWord(Context context,String phone){
        SharedUtils sharedUtils = new SharedUtils(context,Constants.CONFIG_GESTURE);
        sharedUtils.remove(phone);
    }

    /**
     * 保存手势密码
     * @param context
     * @param phone
     * @param pwd
     */
    public static void saveLockPassWord(Context context,String phone,String pwd){
        SharedUtils sharedUtils = new SharedUtils(context,Constants.CONFIG_GESTURE);
        sharedUtils.saveString(phone, pwd);
    }

    /**
     * 获取手势密码
     * @param context
     * @param phone
     * @return
     */
    public static String getLockPassword(Context context,String phone){
        SharedUtils sharedUtils = new SharedUtils(context,Constants.CONFIG_GESTURE);
        return sharedUtils.getString(phone,"");
    }

    /**
     * 显示toast信息
     * @param context
     * @param msg
     */
    public static void makeToast(Context context,String msg){
        Toast.makeText(context,msg,Toast.LENGTH_LONG).show();
    }

    /**
     * 过滤code结果
     * @param jsonBean
     */
    public static boolean filtrateCode(Context context,JsonBean jsonBean){
        LogUtils.i("JsonBean", "过滤code结果：" + jsonBean.getJsonString());
        int code = jsonBean.getCode();//code判断值
        String errorMsg = jsonBean.getError_msg();//错误信息
        if(code == Constants.NO_DATA || code == Constants.IS_EVENT
                || code == Constants.NEED_LOGIN_AGAIN
                || code == Constants.SERVICE_MAINTAIN){
            Utils.makeToast(context,errorMsg);
            return false;
        }else{
            return true;
        }
    }

    /**
     * 关闭开启的弹窗
     * @param dialog
     */
    public static void closeDialog(Context context,Dialog dialog){
        if(dialog != null && dialog.isShowing()){
            dialog.cancel();
        }
    }

    /**
     * 手机号码添加星号
     * @param text
     * @return
     */
    public static String getHasStarsMobile(String text){
        if(TextUtils.isEmpty(text)){
            return "";
        }else{
            return text.substring(0,3)+"*****"+text.substring(8);
        }
    }
}
