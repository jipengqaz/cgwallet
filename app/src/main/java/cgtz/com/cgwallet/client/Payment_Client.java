package cgtz.com.cgwallet.client;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cgtz.com.cgwallet.R;
import cgtz.com.cgwallet.activity.BindBankActivity;
import cgtz.com.cgwallet.activity.SaveMoneyActivity;
import cgtz.com.cgwallet.activity.TradePwdActivity;
import cgtz.com.cgwallet.activity.Withdraw_money;
import cgtz.com.cgwallet.bean.BankCard;
import cgtz.com.cgwallet.bean.JsonBean;
import cgtz.com.cgwallet.utility.Constants;
import cgtz.com.cgwallet.utils.CustomTask;
import cgtz.com.cgwallet.utils.LogUtils;
import cgtz.com.cgwallet.utils.MD5Util;
import cgtz.com.cgwallet.utils.Utils;
import cgtz.com.cgwallet.widget.CustomEffectsDialog;
import cgtz.com.cgwallet.widget.ProgressDialog;

/**
 * Created by chen on 2015-11-25.
 */
public class Payment_Client {
    private static String TAG = "Withdraw_money_Client";
    private static Context context;
    private static ProgressDialog dialog;
    //  根据获取到的success值去判断相关的逻辑
    private static Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            try {
                JsonBean jsonBean = (JsonBean) msg.obj;
                if (!Utils.filtrateCode(context, jsonBean)) {
                    return;
                }
                JSONObject json = jsonBean.getJsonObject();
                LogUtils.e(TAG, json + "");
                int success = json.optInt("success");
                if (success == -7) {//还没设置交易密码
                    Utils.makeToast(context,
                            context.getString(R.string.error_msg_notradepwd));
                    context.startActivity(new Intent(context, TradePwdActivity.class)
                            .putExtra("isSetTradePwd", false));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    /**
     *
     * 进入取钱界面  的判断接口
     *
     * @param code
     */
    public static void getPayment_money(Context context1, int code, ProgressDialog dialog1) {
        context = context1;
        dialog = dialog1;
        Map<String, String> map = new HashMap<>();
        map.put("user_id", Utils.getUserId() + "");
        map.put("token", Utils.getToken() + "");
        CustomTask task = new CustomTask(handler, code,
                Constants.URL_E_WALLET_REDEEM,
                true, map, true);
        task.execute();
    }
}


