package cgtz.com.cgwallet.client;

import android.os.Handler;

import java.util.HashMap;
import java.util.Map;

import cgtz.com.cgwallet.utility.Constants;
import cgtz.com.cgwallet.utils.CustomTask;

/**
 * 获取各种数据
 * Created by Administrator on 2015/4/25 0025.
 */
public class Get_data {
    /**
     * 获取各种数据更新时间
     * @param handler
     */
    public static void getStartUp(Handler handler){
        Map<String, String> params = new HashMap<String, String>();
        CustomTask task = new CustomTask(handler, Constants.WHAT_STARTUP,
                Constants.URL_STARTUP,
                true,params,true);
        task.execute();
    }
}
