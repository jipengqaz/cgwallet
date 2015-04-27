package cgtz.com.cgwallet.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Environment;
import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;
import android.widget.Toast;

import java.util.HashMap;

import cgtz.com.cgwallet.MApplication;
import cgtz.com.cgwallet.activity.LoginActivity;
import cgtz.com.cgwallet.bean.JsonBean;
import cgtz.com.cgwallet.utility.Constants;

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
        MApplication.setIsSetTrade(false);
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
     * 判断SD卡是否存在
     * @return
     */
    public static boolean isHaveSD(){
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            return true;
        }else{
            return false;
        }
    }
    /**
     * 过滤code结果
     * @param jsonBean
     */
    public static boolean filtrateCode(Context context,JsonBean jsonBean){
        LogUtils.i("JsonBean", "过滤code结果：" + jsonBean.getJsonString());
        int code = jsonBean.getCode();//code判断值
        String errorMsg = jsonBean.getError_msg();//错误信息
        if(code == Constants.NO_DATA || code == Constants.IS_EVENT){
            Utils.makeToast(context,errorMsg);
            return false;
        }else if(code == Constants.NEED_LOGIN_AGAIN){//需要重新登录
            context.startActivity(new Intent(context, LoginActivity.class));
            ((Activity)context).finish();
            return false;
        }else if(code == Constants.SERVICE_MAINTAIN){//服务器正在维护

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

    /**
     * 自动登录
     * @param context
     * @param handler
     */
    public static void autoLogin(Context context,Handler handler){
        String mobile = getUserPhone(context);
        String pwd = getLoginPwd(context);
        if(!TextUtils.isEmpty(mobile) && !TextUtils.isEmpty(pwd)){
            HashMap<String,String> params = new HashMap<>();
            params.put("username",mobile);
            params.put("password", pwd);
            CustomTask task = new CustomTask(handler,Constants.WHAT_LOGIN,Constants.URL_LOGIN,true,params,true);
            task.execute();
        }
    }

    /**
     * 是否登录
     * @return
     */
    public static boolean isLogined(){
        if(TextUtils.isEmpty(getUserId()) || TextUtils.isEmpty(getToken())){
            return false;
        }else{
            return true;
        }
    }

    /**
     * 判断是否设置了交易密码
     * @return
     */
    public static boolean isSetTradePwd(){
        if(TextUtils.isEmpty(getUserId()) || TextUtils.isEmpty(getToken()) || MApplication.isSetTrade()){
            return false;
        }else{
            return true;
        }
    }
    /**
     * 获取带星的用户姓名
     * @param username
     * @return
     */
    public static String getUserNameForStart(String username){
        return username.substring(0,1)+"*"+(username.length()>2?username.substring(2):"");
    }
    /**
     * 获取带星的身份证号
     * @param identity
     * @return
     */
    public static String getUserIdentity(String identity){
        StringBuffer sb = new StringBuffer();
        return identity.length() == 15?
                identity.substring(0,6)+"******"+identity.substring(12)
                :
                identity.substring(0,6)+"*********"+identity.substring(15);
    }
}
