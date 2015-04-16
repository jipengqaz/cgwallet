package cgtz.com.cgwallet.utils;

import android.content.Context;

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
}
