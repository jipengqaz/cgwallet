package cgtz.com.cgwallet.client;

import android.os.Handler;

import java.util.HashMap;
import java.util.Map;

import cgtz.com.cgwallet.utility.Constants;
import cgtz.com.cgwallet.utils.CustomTask;

/**
 * Created by Administrator on 2015/4/23 0023.
 */
public class E_wallet_list {

    /**
     *
     * @param handler
     * @param type (全部传1,转入传2，转出传3)
     * @param page  第几页
     * @param limit    每页多少
     */
    public  static void get_e_wallet_list(Handler handler ,int type ,int page,int limit){

//        User user = UserSession.getInstance().getLoginedUser();
        String user_id = "191800033066";
        String token = "CzjBA6gam5rasbbur9GExj0aGn7oHUH98_zPGHdEdKM";
        Map<String, String> params = new HashMap<String, String>();
        params.put("user_id", user_id + "");
        params.put("token", token);
        params.put("type",type+"");
        params.put("page",page+"");
        params.put("limit",limit+"");
        CustomTask jujgeTask = new CustomTask(handler,1, Constants.E_WALLET_LIST, true,params,Constants.ENCONDING);
        jujgeTask.execute();
    }
}
