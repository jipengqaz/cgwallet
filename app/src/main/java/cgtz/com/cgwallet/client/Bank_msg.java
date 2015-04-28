package cgtz.com.cgwallet.client;

import android.os.Handler;

import java.util.HashMap;

import cgtz.com.cgwallet.utility.Constants;
import cgtz.com.cgwallet.utils.CustomTask;
import cgtz.com.cgwallet.utils.Utils;

/**
 * Created by Administrator on 2015/4/28 0028.
 */
public class Bank_msg {
    /**
     *
     * @param handler
     * @param bank_id 银行卡的id
     * @param province 省
     * @param city 市
     * @param branch 支行
     * @return
     */
    public static void update_bank(Handler handler,String bank_id,String province,String city,String branch,int code){
        HashMap param=new HashMap();
        param.put("user_id", Utils.getUserId()+"");
        param.put("token",Utils.getToken()+"");
        param.put("id",bank_id);//银行卡的id
        param.put("province",province);//省
        param.put("city",city);//市
        param.put("branch", branch);//支行
        CustomTask task = new CustomTask(handler, code,
                Constants.URL_UPDATE_BANK,
                true,param,true);
        task.execute();
    }

    /**
     * 获取分行信息
     * 数组第一个为开户省
     * 数组第二个为开户市
     * 数组第三个为开户银行
     * */
    public static void  getBranch(String[] aa,Handler handler,int code){
        HashMap param=new HashMap();
        param.put("province",aa[0]);
        param.put("city",aa[1]);
        param.put("bankName",aa[2]);
        CustomTask task = new CustomTask(handler, code,
                Constants.URL_BRANCH,
                true,param,true);
        task.execute();
    }
}
