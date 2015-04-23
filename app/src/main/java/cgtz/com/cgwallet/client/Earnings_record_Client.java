package cgtz.com.cgwallet.client;

import android.os.Handler;

import java.util.HashMap;

import cgtz.com.cgwallet.utility.Constants;
import cgtz.com.cgwallet.utils.CustomTask;

/**
 * 我的收益获取数据
 * Created by Administrator on 2015/4/22 0022.
 */
public class Earnings_record_Client {

    public static void getData(Handler mHandler,int page,int limit){
        //服务器数据交互操作
        HashMap<String,String> maps = new HashMap<>();
        maps.put("user_id","191800033066");
        maps.put("token", "CzjBA6gam5rasbbur9GExqyFcil8NWwCuFcYBMzx_Oo");
        maps.put("page", page + "");
        maps.put("limit", limit + "");
        CustomTask task = new CustomTask(mHandler, 0,
                Constants.URL_INTEREST_HISTORY,
                true,maps,true);
        task.execute();
    }
}
