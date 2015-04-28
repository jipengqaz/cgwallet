package cgtz.com.cgwallet.client;

import android.os.Handler;

import java.util.HashMap;
import java.util.Map;

import cgtz.com.cgwallet.utility.Constants;
import cgtz.com.cgwallet.utils.CustomTask;
import cgtz.com.cgwallet.utils.MD5Util;
import cgtz.com.cgwallet.utils.Utils;

/**
 * 取钱接口
 * Created by Administrator on 2015/4/28 0028.
 */
public class Withdraw_money_Client {

    /**\
     * 进入取钱界面  的判断接口
     * @param handler
     * @param code
     */
    public static void getWithdraw_money(Handler handler,int code){
        Map<String ,String > map = new HashMap<String,String>();
        map.put("user_id", Utils.getUserId()+"");
        map.put("token",Utils.getToken()+"");
        CustomTask task = new CustomTask(handler, code,
                Constants.URL_E_WALLET_REDEEM,
                true,map,true);
        task.execute();
    }

    /**
     *
     * @param handler
     * @param amount  取多少钱
     * @param back_id  银行卡id
     * @param password  交易密码
     * @param code      识别码
     */
    public static void getMoney(Handler handler,String amount,String back_id,String password,int code){
        Map<String,String> maps = new HashMap<String, String>();
        maps.put("user_id",Utils.getUserId());
        maps.put("token",Utils.getToken());
        maps.put("amount",amount);//取现金额
        maps.put("card_id",back_id);//银行卡id
        maps.put("password", MD5Util.md5(password));
        CustomTask task = new CustomTask(handler, code,
                Constants.URL_DO_WITHDRAW,
                true,maps,true);
        task.execute();
    }
}
