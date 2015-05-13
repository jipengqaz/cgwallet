package cgtz.com.cgwallet.client;

import android.os.Handler;

import java.util.HashMap;

import cgtz.com.cgwallet.utility.Constants;
import cgtz.com.cgwallet.utils.CustomTask;

/**
 * 获取分享内容   和二维码
 * Created by Administrator on 2015/4/27 0027.
 */
public class Get_share_content {

    public static void getContent(Handler handler,String user_id,String token){
            HashMap param=new HashMap();
            param.put("user_id", user_id);
            param.put("token",token);
            CustomTask task = new CustomTask(handler, 0,
                    Constants.URL_SHARE_TIP,
                    true,param,true);
            task.execute();
    }
}
