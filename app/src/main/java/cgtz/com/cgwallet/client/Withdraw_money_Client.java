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
 * 取钱接口
 * Created by Administrator on 2015/4/28 0028.
 */
public class Withdraw_money_Client {
    private static String TAG = "Withdraw_money_Client";
    private static Context context;
    private static String name;//用户姓名
    private static String identity;//用户身份证号
    private static int bank_id;//银行卡id
    private static String bankCard;//银行卡号
    private static String bankName;//银行名称
    private static ProgressDialog dialog;
    private static Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(dialog.isShowing()){
                dialog.dismiss();
            }
            try{
                JsonBean jsonBean = (JsonBean) msg.obj;
                int code = jsonBean.getCode();
                String errorMsg = jsonBean.getError_msg();
                if(!Utils.filtrateCode(context,jsonBean)){
                    Utils.makeToast(context, errorMsg + "  错误码" + code);
                    return;
                }
                JSONObject json = jsonBean.getJsonObject();
                LogUtils.e(TAG, json + "");
                int success = json.optInt("success");
                if (1 == success || success == -2) {
                    JSONObject list = json.optJSONObject("list");
                    JSONArray bankCard = list.optJSONArray("bankCard");//银行卡信息
                    BankCard card = null;
                    try {
                        card = new BankCard(bankCard.getJSONObject(0));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Intent intent = new Intent(context, Withdraw_money.class);
                    intent.putExtra("success",success);
                    intent.putExtra("tip",list.optString("tip"));//体现提示
                    intent.putExtra("tip2",list.optString("tip2"));//资金将在1-2个工作日内达到您
                    intent.putExtra("card", card);//银行卡信息
                    intent.putExtra("capitalBalance", list.optString("capitalBalance") + "");//余额
                    context.startActivity(intent);
                } else if (success == -3) {//提现黑名单
                    errorDialog(json.optString("msg"));
                } else if (success == -4) {//未实名认证
                    Utils.makeToast(context, json.optString("msg"));
                    context.startActivity(new Intent(context, SaveMoneyActivity.class)
                            .putExtra("bindFee", json.optString("bindFee"))
                            .putExtra("success", success));
                } else if (success == 0) {
                    Utils.makeToast(context, json.optString("msg"));
                } else if (success == -5) {
                    //老用户的实名认证，联系客服修改为最新的实名认证
                    Utils.makeToast(context, json.optString("msg"));
                } else if (success == -6) {//已绑卡  未完善信息
                    LogUtils.i("MineFragment", json + "");
                    Utils.makeToast(context, json.optString("msg"));
                    Intent intent = new Intent(context, BindBankActivity.class);
                    intent.putExtra("perfect", false);
                    intent.putExtra("tip", json.optJSONObject("list").optString("tip"));//文案
                    intent.putExtra("starIdentity", json.optJSONObject("list").optString("starIdentity"));//身份证
                    intent.putExtra("starName", json.optJSONObject("list").optString("starName"));//姓名
                    intent.putExtra("starBankAccount", json.optJSONObject("list").optString("starBankAccount"));//银行卡号
                    intent.putExtra("card_id", json.optJSONObject("list").optString("card_id"));//银行卡id
                    intent.putExtra("bankName", json.optJSONObject("list").optString("bankName"));//银行
                    intent.putExtra("bank_id", json.optJSONObject("list").optInt("bank_id"));
                    context.startActivity(intent);
                } else if (success == -7){//还没设置交易密码
                    Utils.makeToast(context,
                            context.getString(R.string.error_msg_notradepwd));
                    context.startActivity(new Intent(context, TradePwdActivity.class)
                            .putExtra("isSetTradePwd", false));
                }else if(success == -8){
                    //未真正实名认证 未绑卡
                    name = json.optString("name");//姓名
                    identity = json.optString("identity");//身份证号
                    context.startActivity(new Intent(context, SaveMoneyActivity.class)
                            .putExtra("bindFee", json.optString("bindFee"))
                            .putExtra("success", success)
                            .putExtra("name", name)
                            .putExtra("identity", identity));
                }else if(success == -9){
                    //未真正实名认证  已绑卡但是不支持连连
                    beforePayDialog(json);
                }else if(success == -10){
                    //未真正实名 已绑卡支持连连但未绑定连连
                    name = json.optString("name");//姓名
                    identity = json.optString("identity");//身份证号
                    bank_id = json.optJSONObject("list").optInt("bank_id");
                    bankName = json.optJSONObject("list").optString("bankName");
                    bankCard = json.optJSONObject("list").optString("fullCardNumber");
                    context.startActivity(new Intent(context, SaveMoneyActivity.class)
                            .putExtra("bindFee", json.optString("bindFee"))
                            .putExtra("success", success)
                            .putExtra("name", name)
                            .putExtra("identity", identity)
                            .putExtra("bank_id", bank_id)
                            .putExtra("bankName", bankName)
                            .putExtra("bankCard", bankCard));
                }else if(success == -11){
                    //真正实名认证,未绑卡
                    name = json.optString("name");//姓名
                    identity = json.optString("identity");//身份证号
                    context.startActivity(new Intent(context, SaveMoneyActivity.class)
                            .putExtra("bindFee", json.optString("bindFee"))
                            .putExtra("success", success)
                            .putExtra("name", name)
                            .putExtra("identity", identity));
                }else if(success == -12){
                    //真正实名认证  已绑卡但是不支持连连
                    beforePayDialog(json);
                }else if(success == -13){
                    //真正实名认证  已绑卡支持连连但未绑定连连
                    name = json.optString("name");//姓名
                    identity = json.optString("identity");//身份证号
                    bank_id = json.optJSONObject("list").optInt("bank_id");
                    bankName = json.optJSONObject("list").optString("bankName");
                    bankCard = json.optJSONObject("list").optString("fullCardNumber");
                    context.startActivity(new Intent(context, SaveMoneyActivity.class)
                            .putExtra("bindFee", json.optString("bindFee"))
                            .putExtra("success", json.optInt("success"))
                            .putExtra("name", name)
                            .putExtra("identity", identity)
                            .putExtra("bank_id", bank_id)
                            .putExtra("bankName", bankName)
                            .putExtra("bankCard", bankCard));
                }else {
                    Utils.makeToast(context, json.optString("msg"));
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    };
    //不支持连连或联动，需要重新绑定
    private static void beforePayDialog(final JSONObject json){
        final CustomEffectsDialog dialog = CustomEffectsDialog.getInstans(context);
        dialog.withTitle(null);
        dialog.withMessage(json.optString("msg"));
        dialog.withMessageColor(context.getResources().getColor(R.color.dialog_msg_color));
        dialog.withBtnLineColor(R.color.bg_line);
        dialog.withBtnContentLineColor(R.color.bg_line);
        dialog.withButton1Text("暂不绑卡");
        dialog.withButton2Text("绑定新卡");
        dialog.withButton1TextColor(context.getResources().getColor(R.color.comment_text));
        dialog.withButton2TextColor(context.getResources().getColor(R.color.main_blue));
        dialog.withButton1Click(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.withButton2Click(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                //不支持连连，需要重新绑定
                if(json.optInt("success") == -9){
                    //未真正实名认证  已绑卡但是不支持连连
                    name = json.optString("name");//姓名
                    identity = json.optString("identity");//身份证号
                    context.startActivity(new Intent(context, SaveMoneyActivity.class)
                            .putExtra("bindFee", json.optString("bindFee"))
                            .putExtra("success", json.optInt("success"))
                            .putExtra("name", name)
                            .putExtra("identity", identity));
                }else if(json.optInt("success") == -12){
                    //真正实名认证  已绑卡但是不支持连连
                    name = json.optString("name");//姓名
                    identity = json.optString("identity");//身份证号
                    context.startActivity(new Intent(context, SaveMoneyActivity.class)
                            .putExtra("bindFee", json.optString("bindFee"))
                            .putExtra("success", json.optInt("success"))
                            .putExtra("name", name)
                            .putExtra("identity", identity));
                }
            }
        });
        dialog.show();
    }
    /**
     * 提示框
     * @param errorStr
     */
    private static void errorDialog(String errorStr){
        final Dialog dialog = new Dialog(context,R.style.loading_dialog2);
        View view = LayoutInflater.from(context).inflate(R.layout.custom_dialog,null);
        TextView dialog_msg = (TextView) view.findViewById(R.id.dialog_msg);
        TextView dialog_confirm = (TextView) view.findViewById(R.id.dialog_confirm);
        dialog_msg.setText(errorStr);
        dialog_confirm.setText("确定");
        dialog.setContentView(view);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        dialog_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }
    /**\
     * 进入取钱界面  的判断接口
     * @param code
     */
    public static void getWithdraw_money(Context context1,int code,ProgressDialog dialog1){
        context = context1;
        dialog = dialog1;
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
