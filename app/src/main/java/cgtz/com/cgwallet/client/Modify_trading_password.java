package cgtz.com.cgwallet.client;

import android.os.Handler;

import java.util.HashMap;
import java.util.Map;

import cgtz.com.cgwallet.utility.Constants;
import cgtz.com.cgwallet.utils.CustomTask;
import cgtz.com.cgwallet.utils.MD5Util;
import cgtz.com.cgwallet.utils.Utils;

/**
 * Created by Administrator on 2015/1/21.
 */
public class Modify_trading_password {
    /**
     * 获取短信验证码
     * @param handler
     */
    public static void Get_SMS(Handler handler,int code){

        String  user_id = Utils.getUserId();
        String token = Utils.getToken();
        Map<String, String> params = new HashMap<String, String>();
        params.put("user_id", user_id + "");
        params.put("token", token);
        CustomTask task = new CustomTask(handler, code,
                Constants.URL_GET_CODE,
                true,params,true);
        task.execute();
    }

    /**
     * 修改交易密码
     * @param handler  handler
     * @param password 重设交易密码密码
     * @param mobile_code  短信验证码
     * @param code handler 识别码
     */
    public static void ResetPayPass(Handler handler,String password,String mobile_code,int code){
        String  user_id = Utils.getUserId();
        String token = Utils.getToken();
        Map<String, String> params = new HashMap<String, String>();
        params.put("user_id", user_id + "");
        params.put("token", token);
        params.put("password", MD5Util.md5(password));
        params.put("mobile_code", mobile_code);
        CustomTask task = new CustomTask(handler, code,
                Constants.URL_RESETPAYPASS,
                true,params,true);
        task.execute();
    }


}
