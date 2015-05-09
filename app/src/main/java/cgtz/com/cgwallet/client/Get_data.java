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
        CustomTask task = new CustomTask(handler, Constants.WHAT_STARTUP,
                Constants.URL_STARTUP,
                true,null,true);
        task.execute();
    }

    /**
     * 获取客服文案信息
     * @param handler
     */
    public static void getKefuTip(Handler handler){
        CustomTask task = new CustomTask(handler, Constants.WHAT_KE_FU,
                Constants.URL_KEFUTIP,
                true,null,true);
        task.execute();
    }

    /**
     * 获取银行分行信息
     * @param handler
     */
    public static void getProvince(Handler handler){
        CustomTask task = new CustomTask(handler, Constants.WHAT_PROVINCES,
                Constants.URL_PROVINCES_CITIES_UPDATE,
                true,null,true);
        task.execute();
    }

    public static void getImage(Handler handler){
        CustomTask task = new CustomTask(handler, 0,
                Constants.URL_API_STARTIMAGE,
                true,null,true);
        task.execute();
    }
}
