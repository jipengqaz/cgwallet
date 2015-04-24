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

    public static void isPasswrod(Handler handler,String passwrod){
        Map<String,String> maps = new HashMap<String, String>();
        maps.put("user_id", Utils.getUserId()+"");
        maps.put("token",Utils.getToken()+"");
        maps.put("password", MD5Util.md5(passwrod));
        CustomTask task = new CustomTask(handler, 0,
                Constants.AGAINLOGIN_URL,
                true,maps,true);
        task.execute();
    }
}
