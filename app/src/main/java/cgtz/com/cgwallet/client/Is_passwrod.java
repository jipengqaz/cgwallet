package cgtz.com.cgwallet.client;


import android.os.Handler;

import java.util.HashMap;
import java.util.Map;

import cgtz.com.cgwallet.utility.Constants;
import cgtz.com.cgwallet.utils.CustomTask;
import cgtz.com.cgwallet.utils.MD5Util;
import cgtz.com.cgwallet.utils.Utils;

/**
 * Created by Administrator on 2015/4/24 0024.
 */
public class Is_passwrod {

    /**
     * 判断登录密码是否正确
     * @param handler
     * @param passwrod 登录密码
     */
    public static void isPasswrod(Handler handler,String passwrod,int code){
        Map<String,String> maps = new HashMap<String, String>();
        maps.put("user_id", Utils.getUserId()+"");
        maps.put("token",Utils.getToken()+"");
        maps.put("password", MD5Util.md5(passwrod));
        CustomTask task = new CustomTask(handler, code,
                Constants.AGAINLOGIN_URL,
                true,maps,true);
        task.execute();
    }

    public static void changLoginpwd(Handler handler, String originalpwd,String newpwd,String confirmpwd,int code){
        Map<String, String> maps = new HashMap<String, String>();
        maps.put("user_id", Utils.getUserId() + "");
        maps.put("token", Utils.getToken() + "");
        maps.put("old_password", MD5Util.md5(originalpwd));
        maps.put("password", MD5Util.md5(newpwd));
        maps.put("confirm_pass", MD5Util.md5(confirmpwd));
        CustomTask task = new CustomTask(handler, code,
                Constants.URL_CHANGE_LOGINPWD,
                true,maps,true);
        task.execute();
    }
}
