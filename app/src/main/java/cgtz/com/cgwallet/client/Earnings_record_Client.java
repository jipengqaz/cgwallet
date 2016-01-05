package cgtz.com.cgwallet.client;

import android.os.Handler;

import java.util.HashMap;

import cgtz.com.cgwallet.utility.Constants;
import cgtz.com.cgwallet.utils.CustomTask;
import cgtz.com.cgwallet.utils.Utils;

/**
 * 我的收益获取数据
 * Created by Administrator on 2015/4/22 0022.
 */
public class Earnings_record_Client {
//方法中参数的含义
    public static void getData(Handler mHandler,int page,int limit){
        //服务器数据交互操作
        HashMap<String,String> maps = new HashMap<>();
        maps.put("user_id", Utils.getUserId());
        maps.put("token", Utils.getToken());
        maps.put("page", page + "");
        maps.put("limit", limit + "");
//这里参数的含义
        CustomTask task = new CustomTask(mHandler, 0,
                Constants.URL_INTEREST_HISTORY,
                true,maps,true);
        task.execute();
    }
}
