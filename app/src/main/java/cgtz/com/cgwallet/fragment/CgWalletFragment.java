package cgtz.com.cgwallet.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cgtz.com.cgwallet.R;
import cgtz.com.cgwallet.activity.BindBankActivity;
import cgtz.com.cgwallet.activity.SaveMoneyActivity;
import cgtz.com.cgwallet.activity.TradePwdActivity;
import cgtz.com.cgwallet.activity.WebViewActivity;
import cgtz.com.cgwallet.activity.Withdraw_money;
import cgtz.com.cgwallet.bean.BankCard;
import cgtz.com.cgwallet.bean.JsonBean;
import cgtz.com.cgwallet.client.Withdraw_money_Client;
import cgtz.com.cgwallet.presenter.SplashPresenter;
import cgtz.com.cgwallet.utility.Constants;
import cgtz.com.cgwallet.utils.CustomTask;
import cgtz.com.cgwallet.utils.LogUtils;
import cgtz.com.cgwallet.utils.Utils;
import cgtz.com.cgwallet.view.ISplashView;
import cgtz.com.cgwallet.widget.CustomEffectsDialog;
import cgtz.com.cgwallet.widget.ProgressDialog;

/**
 * 草根钱包
 * Created by Administrator on 2015/4/11.
 */
public class CgWalletFragment extends BaseFragment implements ISplashView,View.OnClickListener{
    private static final String TAG = "CgWalletFragment";
    private ProgressDialog progressDialog;
    private SplashPresenter presenter;
    private String rate;//(草根钱包利率)
    private ArrayList<String> linkName;//（特色描述）
    private ArrayList<String> linkUrl;//(特色链接)
    private TextView rateOfInteger;//利率的整数部分
    private TextView rateOfDecimal;//利率的小数部分
    private LinearLayout drawLayout;//取钱
    private LinearLayout saveLayout;//存钱
    private LinearLayout linkLayout1;//第一个特色布局
    private TextView linkName1;//第一个特色文案
    private LinearLayout linkLayout2;//第二个特色布局
    private TextView linkName2;//第二个特色文案
    private LinearLayout linkLayout3;//第三个特色布局
    private TextView linkName3;//第三个特色文案
    private String link_name;//获取的特色文案
    private String link_url;//获取的特色路径

    private String name;//用户姓名
    private String identity;//用户身份证号
    private int bank_id;//银行卡id
    private String bankCard;//银行卡号
    private String bankName;//银行名称

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LogUtils.e(TAG, "onCreateView");
        presenter = new SplashPresenter(this);
        View layoutView = inflater.inflate(R.layout.layout_cg_wallet, container, false);
        initViews(layoutView);
        setListener();
        return layoutView;
    }

    /**
     * 初始化布局widght
     * @param view
     */
    private void initViews(View view){
        drawLayout = (LinearLayout) view.findViewById(R.id.layout_draw);//取钱
        saveLayout = (LinearLayout) view.findViewById(R.id.layout_save);//存钱
        rateOfInteger = (TextView) view.findViewById(R.id.cg_wallet_rate_integer);//利率的整数部分
        rateOfDecimal = (TextView) view.findViewById(R.id.cg_wallet_rate_decimal);//利率的小数部分
        linkLayout1 = (LinearLayout) view.findViewById(R.id.cg_wallet_link1);//第一个特色布局
        linkName1 = (TextView) view.findViewById(R.id.cg_wallet_link_name1);//第一个特色文案
        linkLayout2 = (LinearLayout) view.findViewById(R.id.cg_wallet_link2);//第二个特色布局
        linkName2 = (TextView) view.findViewById(R.id.cg_wallet_link_name2);//第二个特色文案
        linkLayout3 = (LinearLayout) view.findViewById(R.id.cg_wallet_link3);//第三个特色布局
        linkName3 = (TextView) view.findViewById(R.id.cg_wallet_link_name3);//第三个特色文案
        setData();
    }


    /**
     * 给widget添加事件
     */
    private void setListener(){
        drawLayout.setOnClickListener(this);//取钱
        saveLayout.setOnClickListener(this);//存钱
        linkLayout1.setOnClickListener(this);//第一个特色布局
        linkLayout2.setOnClickListener(this);//第二个特色布局
        linkLayout3.setOnClickListener(this);//第三个特色布局
    }

    /**
     * 填充数据
     */
    private void fillViews(){
        rateOfInteger.setText(rate.substring(0, rate.indexOf(".")));//利率的整数部分
        rateOfDecimal.setText(rate.substring(rate.indexOf(".")));//利率的小数部分
        linkName1.setText(linkName.get(0));//第一个特色文案
        linkName2.setText(linkName.get(1));//第二个特色文案
        linkName3.setText(linkName.get(2));//第三个特色文案
    }

    /**
     * 获取页面数据
     */
    public void setData(){
        presenter.didFinishLoading(getActivity());
    }


    @Override
    public void startProcessBar() {
        if(progressDialog == null){
            progressDialog = new ProgressDialog(getActivity(),R.style.loading_dialog);
        }
        if(progressDialog.isShowing()){
            progressDialog.dismiss();
        }
        progressDialog.show();
    }

    @Override
    public void hideProcessBar() {
        if(progressDialog != null && progressDialog.isShowing()){
            progressDialog.dismiss();
        }
    }

    @Override
    public void showNetError() {
        Utils.makeToast(getActivity(), Constants.IS_EVENT_MSG);
    }

    @Override
    public void startNextActivity() {
        CustomTask task = new CustomTask(mHandler, Constants.WHAT_WALLET_MAIN
                ,Constants.OFFLINE_HTTP,false,null,false);
        task.execute();
    }

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(progressDialog.isShowing()){
                progressDialog.dismiss();
            }
            try{
                JsonBean jsonBean = (JsonBean) msg.obj;
                int code = jsonBean.getCode();
                String errorMsg = jsonBean.getError_msg();
                if(!Utils.filtrateCode(getActivity(),jsonBean)){
                    Utils.makeToast(getActivity(), errorMsg + "  错误码" + code);
                    return;
                }
                int action = msg.what;
                switch (action){
                    case Constants.WHAT_WALLET_MAIN:
                        boolean flag = Utils.filtrateCode(getActivity(),jsonBean);
                        if(flag && code == Constants.OPERATION_FAIL){//数据交互失败
                            Utils.makeToast(getActivity(), errorMsg);
                        }else if(flag && code == Constants.OPERATION_SUCCESS){//数据交互成功
                            JSONObject jsonObject = new JSONObject(jsonBean.getJsonString());
                            rate = jsonObject.optString("rate");//(草根钱包利率)
                            JSONArray feature = jsonObject.getJSONArray("feature");//(草根特色)
                            linkName = new ArrayList<>();//
                            linkUrl = new ArrayList<>();//
                            int size = feature.length();
                            JSONObject object;
                            for(int i=0;i<size;i++){
                                object = feature.getJSONObject(i);
                                linkName.add(object.optString("linkName"));//（特色描述）
                                linkUrl.add(object.optString("linkUrl"));//特色链接)
                            }
                            fillViews();
                        }
                        hideProcessBar();
                        break;
                    case Constants.WHAT_WITHDRAW:
                        JSONObject json = jsonBean.getJsonObject();
                        LogUtils.e(TAG,json+"");
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
                            Intent intent = new Intent(getActivity(), Withdraw_money.class);
                            intent.putExtra("success",success);
                            intent.putExtra("tip",list.optString("tip"));//体现提示
                            intent.putExtra("tip2",list.optString("tip2"));//资金将在1-2个工作日内达到您
                            intent.putExtra("card", card);//银行卡信息
                            intent.putExtra("capitalBalance", list.optString("capitalBalance") + "");//余额
                            startActivity(intent);
                        } else if (success == -3) {//提现黑名单
                            errorDialog(json.optString("msg"));
                        } else if (success == -4) {//未实名认证
                            Utils.makeToast(getActivity(), json.optString("msg"));
                            startActivity(new Intent(getActivity(), SaveMoneyActivity.class)
                                    .putExtra("bindFee", json.optString("bindFee"))
                                    .putExtra("success", success));
                        } else if (success == 0) {
                            Utils.makeToast(getActivity(), json.optString("msg"));
                        } else if (success == -5) {
                            //老用户的实名认证，联系客服修改为最新的实名认证
                            Utils.makeToast(getActivity(), json.optString("msg"));
                        } else if (success == -6) {//已绑卡  未完善信息
                            LogUtils.i("MineFragment", json + "");
                            Utils.makeToast(getActivity(), json.optString("msg"));
                            Intent intent = new Intent(getActivity(), BindBankActivity.class);
                            intent.putExtra("perfect", false);
                            intent.putExtra("tip", json.optJSONObject("list").optString("tip"));//文案
                            intent.putExtra("starIdentity", json.optJSONObject("list").optString("starIdentity"));//身份证
                            intent.putExtra("starName", json.optJSONObject("list").optString("starName"));//姓名
                            intent.putExtra("starBankAccount", json.optJSONObject("list").optString("starBankAccount"));//银行卡号
                            intent.putExtra("card_id", json.optJSONObject("list").optString("card_id"));//银行卡id
                            intent.putExtra("bankName", json.optJSONObject("list").optString("bankName"));//银行
                            intent.putExtra("bank_id", json.optJSONObject("list").optInt("bank_id"));
                            startActivity(intent);
                        } else if (success == -7){//还没设置交易密码
                            Utils.makeToast(getActivity(),
                                    getString(R.string.error_msg_notradepwd));
                            startActivity(new Intent(getActivity(), TradePwdActivity.class)
                                    .putExtra("isSetTradePwd", false));
                        }else if(success == -8){
                            //未真正实名认证 未绑卡
                            name = json.optString("name");//姓名
                            identity = json.optString("identity");//身份证号
                            startActivity(new Intent(getActivity(), SaveMoneyActivity.class)
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
                            startActivity(new Intent(getActivity(), SaveMoneyActivity.class)
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
                            startActivity(new Intent(getActivity(), SaveMoneyActivity.class)
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
                            startActivity(new Intent(getActivity(),SaveMoneyActivity.class)
                                    .putExtra("bindFee",json.optString("bindFee"))
                                    .putExtra("success",json.optInt("success"))
                                    .putExtra("name",name)
                                    .putExtra("identity",identity)
                                    .putExtra("bank_id",bank_id)
                                    .putExtra("bankName",bankName)
                                    .putExtra("bankCard",bankCard));
                        }else {
                            Utils.makeToast(getActivity(), json.optString("msg"));
                        }
                        break;
                }
            }catch (Exception e){
                e.printStackTrace();
                hideProcessBar();
            }
        }
    };
    //不支持连连或联动，需要重新绑定
    private void beforePayDialog(final JSONObject json){
        final CustomEffectsDialog dialog = CustomEffectsDialog.getInstans(getActivity());
        dialog.withTitle(null);
        dialog.withMessage(json.optString("msg"));
        dialog.withMessageColor(getResources().getColor(R.color.dialog_msg_color));
        dialog.withBtnLineColor(R.color.bg_line);
        dialog.withBtnContentLineColor(R.color.bg_line);
        dialog.withButton1Text("暂不绑卡");
        dialog.withButton2Text("绑定新卡");
        dialog.withButton1TextColor(getResources().getColor(R.color.comment_text));
        dialog.withButton2TextColor(getResources().getColor(R.color.main_blue));
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
                    startActivity(new Intent(getActivity(),SaveMoneyActivity.class)
                            .putExtra("bindFee",json.optString("bindFee"))
                            .putExtra("success",json.optInt("success"))
                            .putExtra("name",name)
                            .putExtra("identity",identity));
                }else if(json.optInt("success") == -12){
                    //真正实名认证  已绑卡但是不支持连连
                    name = json.optString("name");//姓名
                    identity = json.optString("identity");//身份证号
                    startActivity(new Intent(getActivity(),SaveMoneyActivity.class)
                            .putExtra("bindFee",json.optString("bindFee"))
                            .putExtra("success",json.optInt("success"))
                            .putExtra("name",name)
                            .putExtra("identity",identity));
                }
            }
        });
        dialog.show();
    }
    /**
     * 提示框
     * @param errorStr
     */
    private void errorDialog(String errorStr){
        final Dialog dialog = new Dialog(getActivity(),R.style.loading_dialog2);
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.custom_dialog,null);
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
    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.layout_draw://取钱
                if(progressDialog ==null){
                    progressDialog = new ProgressDialog(getActivity());
                }
                progressDialog.show();
                Withdraw_money_Client.getWithdraw_money(mHandler,Constants.WHAT_WITHDRAW);
                break;
            case R.id.layout_save://存钱
                startActivity(new Intent(getActivity(), SaveMoneyActivity.class));
                break;
            case R.id.cg_wallet_link1://第一个特色布局
                link_name = null;//获取的特色文案
                link_url = null;//获取的特色路径
                link_name = linkName.get(0);//获取的特色文案
                link_url = linkUrl.get(0);//获取的特色路径
                if(TextUtils.isEmpty(link_url)){

                }else{
                    startActivity(new Intent(getActivity(), WebViewActivity.class)
                            .putExtra("url", link_url)
                            .putExtra("title", link_name));
                }
                break;
            case R.id.cg_wallet_link2://第二个特色布局
                link_name = null;//获取的特色文案
                link_url = null;//获取的特色路径
                link_name = linkName.get(1);//获取的特色文案
                link_url = linkUrl.get(1);//获取的特色路径
                if(TextUtils.isEmpty(link_url)){

                }else{
                    startActivity(new Intent(getActivity(), WebViewActivity.class)
                            .putExtra("url", link_url)
                            .putExtra("title", link_name));
                }
                break;
            case R.id.cg_wallet_link3://第三个特色布局
                link_name = null;//获取的特色文案
                link_url = null;//获取的特色路径
                link_name = linkName.get(2);//获取的特色文案
                link_url = linkUrl.get(2);//获取的特色路径
                if(TextUtils.isEmpty(link_url)){

                }else{
                    startActivity(new Intent(getActivity(), WebViewActivity.class)
                            .putExtra("url", link_url)
                            .putExtra("title", link_name));
                }
                break;
        }
    }
}
