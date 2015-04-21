package cgtz.com.cgwallet.utils;

import android.content.Context;
import android.widget.Toast;

import cgtz.com.cgwallet.utility.Constants;

/**
 * Created by Administrator on 2015/4/10.
 */
public class Utils {
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
     * 退出账号,删除登录密码
     * @param context
     * @return
     */
    public static void loginOut(Context context){
        SharedUtils sharedUtils = new SharedUtils(context,Constants.CONFIG);
        sharedUtils.remove(Constants.MOBILE_PASSWORD);
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
        Toast.makeText(context,msg,Toast.LENGTH_LONG);
    }
}
