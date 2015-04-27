package cgtz.com.cgwallet.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import cgtz.com.cgwallet.R;
import cgtz.com.cgwallet.bean.Bank;
import cgtz.com.cgwallet.bean.JsonBean;
import cgtz.com.cgwallet.utility.Constants;
import cgtz.com.cgwallet.utils.CustomTask;
import cgtz.com.cgwallet.utils.LogUtils;
import cgtz.com.cgwallet.utils.Utils;
import cgtz.com.cgwallet.view.ISplashView;

/**
 * 信息确认页面
 */
public class InformationConfirmActivity extends BaseActivity implements ISplashView{
    private static final String TAG = "InformationConfirmActivity";
    private boolean onlyUseAccount;//是否余额充足支付
    private boolean isRealleyName;//是否真正实名认证
    private boolean isRelleyBank;//是否真正绑卡或者支持连连支付
    private String name;//姓名
    private String identity;//身份证号
    private String bankName;//银行名称
    private String bankCard;//银行卡号
    private String payLimit;//银行卡单笔限额
    private String bankId;//用户绑定的银行卡id
    private String payLimitIntruduce;//银行卡单笔限额描述
    private String bankTip;//银行给出的提示内容
    private String lastBankCordNum;//银行卡后四位
    private String saveMoney;//存入金额
    private String useAccount;//使用的余额数值
    private String useBank;//使用的银行卡支付金额
    private TextView stilledMoney;//支付金额
    private TextView backhint;//银行信息提示
    private TextView invest_name;//投资人姓名
    private TextView invest_identity;//身份证号
    private TextView invest_bankcard;//银行卡号
    private TextView invest_bank_id;
    private TextView tv_pay_limit_desc;//银行卡单笔限额提示布局
    private LinearLayout layout_need_edit;//需要填写信息的布局
    private EditText edit_username;//输入或者显示用户姓名
    private EditText edit_identity;//输入或显示身份证
    private TextView text_bankname;//选择银行卡
    private EditText edit_bankcard;//输入银行卡号
    private LinearLayout layout_neednot_edit;//不需要填写信息的布局
    private Button btn_finish;//完成按钮
    private LinearLayout bank_layout;//银行信息布局
    private TextView invester_bank_pay;//银行卡支付金额
    private TextView invester_balance;//余额支付金额
    private ArrayList<Bank> list = new ArrayList<Bank>();//存放银行名称的
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information_confirm);
        setTitle("存钱");
        showBack(true);
        getIntentInfo();
        initViews();
        fillWidget();
    }

    private void getIntentInfo(){
        onlyUseAccount = getIntent().getBooleanExtra("onlyUseAccount",false);//是否余额充足支付
        isRealleyName = getIntent().getBooleanExtra("isRealleyName",false);//是否真正实名认证
        isRelleyBank = getIntent().getBooleanExtra("isRelleyBank",false);//是否真正绑卡或者支持连连支付
        name = getIntent().getStringExtra("name");//姓名
        identity = getIntent().getStringExtra("identity");//身份证号
        bankName = getIntent().getStringExtra("bankName");//银行名称
        bankCard = getIntent().getStringExtra("bankCard");//银行卡号
        payLimit = getIntent().getStringExtra("payLimit");//银行卡单笔限额
        bankId = getIntent().getStringExtra("bankId");//用户绑定的银行卡id
        payLimitIntruduce = getIntent().getStringExtra("payLimitIntruduce");//银行卡单笔限额描述
        bankTip = getIntent().getStringExtra("bankTip");//银行给出的提示内容
        lastBankCordNum = getIntent().getStringExtra("lastBankCordNum");//银行卡后四位
        saveMoney = getIntent().getStringExtra("saveMoney");//存入金额
        useAccount = getIntent().getStringExtra("useAccount");//使用的余额数值
        useBank = getIntent().getStringExtra("useBank");//使用的银行卡支付金额
    }

    /**初始化视图*/
    private void initViews(){
//        UtilityUtils.setPossession_TextView(this);//设置文案  资金安全
        invester_bank_pay = (TextView) findViewById(R.id.invester_bank_pay);//银行卡支付金额
        invester_balance = (TextView) findViewById(R.id.invester_balance);//余额支付金额
        bank_layout = (LinearLayout) findViewById(R.id.bank_layout);//银行卡信息布局
        stilledMoney = (TextView) findViewById(R.id.stilled_money);
        invest_name = (TextView) findViewById(R.id.invester_name);
        invest_identity = (TextView) findViewById(R.id.invest_identity);
        invest_bankcard = (TextView) findViewById(R.id.invester_backcard);//银行卡号
        invest_bank_id = (TextView) findViewById(R.id.invest_bank_id);//开户银行
        btn_finish = (Button) findViewById(R.id.invest_finish);
        backhint = (TextView) findViewById(R.id.invest_pay_backhint);
        tv_pay_limit_desc = (TextView) findViewById(R.id.pay_limit_desc);

        layout_need_edit = (LinearLayout) findViewById(R.id.layout_invest_pay_need_edit);//需要填写信息的布局
        edit_username = (EditText) findViewById(R.id.et_investment_pay_username);//输入或者显示用户姓名
        edit_identity = (EditText) findViewById(R.id.et_investment_pay_identity);//输入或显示身份证
        text_bankname = (TextView) findViewById(R.id.tv_investment_pay_bankname);//选择银行卡
        edit_bankcard = (EditText) findViewById(R.id.et_investment_pay_bankcard);//输入银行卡号
        layout_neednot_edit = (LinearLayout) findViewById(R.id.layout_invest_pay_neednot_edit);//不需要填写信息的布局
        getBankList();//获取支持的银行列表
    }

    private void fillWidget(){
        if(!isRealleyName || !isRelleyBank){
            //需要重新填写个人信息
            layout_need_edit.setVisibility(View.VISIBLE);
            layout_neednot_edit.setVisibility(View.GONE);
            if(!TextUtils.isEmpty(name)){
                //输入或者显示用户姓名
                if(isRealleyName){
                    edit_username.setText(Utils.getUserNameForStart(name));
                    edit_username.setEnabled(false);
                }else{
                    edit_username.setText(name);
                    edit_username.setEnabled(true);
                }
            }
            if(!TextUtils.isEmpty(identity)){
                //输入或显示身份证
                if(isRealleyName){
                    edit_identity.setText(Utils.getUserIdentity(identity));
                    edit_identity.setEnabled(false);
                }else{
                    edit_identity.setText(identity);
                    edit_identity.setEnabled(true);
                }
            }
            if(!TextUtils.isEmpty(bankName)){
                //选择银行卡
                text_bankname.setText(bankName);
            }
            if(!TextUtils.isEmpty(bankCard)){
                //输入银行卡号
                edit_bankcard.setText(bankCard);
            }
        }else{
            //不需要重新填写个人信息
            LogUtils.i(TAG,"不需要重新填写个人信息");
            if(onlyUseAccount){
                //是否余额充足支付
                LogUtils.i(TAG,"余额充足支付，不显示个人信息");
                layout_need_edit.setVisibility(View.GONE);
                layout_neednot_edit.setVisibility(View.GONE);
            }else{
                //余额不充足
                LogUtils.i(TAG,"余额不充足支付");
                layout_need_edit.setVisibility(View.GONE);
                layout_neednot_edit.setVisibility(View.VISIBLE);
                invest_name.setText(TextUtils.isEmpty(name)?"":Utils.getUserNameForStart(name) );
                invest_identity.setText(TextUtils.isEmpty(identity)?"":Utils.getUserIdentity(identity) );
                invest_bankcard.setText(TextUtils.isEmpty(lastBankCordNum)?"":"尾号"+lastBankCordNum );
                invest_bank_id.setText(TextUtils.isEmpty(bankName)?"":bankName);
                backhint.setText(TextUtils.isEmpty(bankTip)?"":bankTip);
            }
        }
        stilledMoney.setText(saveMoney+"");
        if(!TextUtils.isEmpty(payLimitIntruduce)
                && (!TextUtils.isEmpty(bankName)
                || !TextUtils.isEmpty(bankCard))){
            tv_pay_limit_desc.setText(payLimitIntruduce);
            tv_pay_limit_desc.setVisibility(View.VISIBLE);
        }else{
            tv_pay_limit_desc.setVisibility(View.GONE);
        }
        if(!TextUtils.isEmpty(useBank)){
            //使用含有银行卡支付
            bank_layout.setVisibility(View.VISIBLE);
        }else{
            //使用不含有银行卡支付
            bank_layout.setVisibility(View.GONE);
        }
        invester_balance.setText(useAccount+"");
        invester_bank_pay.setText(useBank+"");
    }

    /**
     * 获取银行卡列表
     */
    private void getBankList(){
        CustomTask task = new CustomTask(mHandler, Constants.WHAT_SELECTED_BANK
                ,Constants.URL_SELECTED_BANK,
                false,null,true);
        task.execute();
    }

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            try{
                isRealleyName = false;//是否真正实名认证
                isRelleyBank = false;//是否真正绑卡或者支持连连支付
                JsonBean jsonBean = (JsonBean) msg.obj;
                int code = jsonBean.getCode();
                String errorMsg = jsonBean.getError_msg();
                if(code == Constants.DATA_EVENT){
                    hideProcessBar();
                    Utils.makeToast(InformationConfirmActivity.this, Constants.ERROR_MSG_CODE + code);
                    return;
                }
                int action = msg.what;
                hideProcessBar();
                JSONObject json = jsonBean.getJsonObject();
                boolean flag = Utils.filtrateCode(InformationConfirmActivity.this,jsonBean);
                switch (action){
                    case Constants.WHAT_WALLET_DEPOSIT:
                        if(flag && code == Constants.OPERATION_FAIL){//数据交互失败
                            Utils.makeToast(InformationConfirmActivity.this, errorMsg);
                        }else if(flag && code == Constants.OPERATION_SUCCESS){//数据交互成功

                        }
                        break;
                    case Constants.WHAT_SELECTED_BANK:
                        JSONArray banks = json.optJSONArray("banks");
                        int bank = banks.length();
                        for (int i = 0; i < bank; i++) {
                            try {
                                JSONObject temp = (JSONObject) banks.get(i);
                                Bank bankk = new Bank();
                                bankk.setId(temp.optInt("code"));//银行卡
                                bankk.setName(temp.optString("name"));
                                bankk.setNeedBranch(temp.optInt("needBranch"));
                                bankk.setPriority(temp.optString("priority"));//判断用那种支付的识别码
                                bankk.setBindFee(temp.optString("bindFee"));//银行卡扣的1分钱
                                bankk.setPay_limit(temp.optDouble("pay_limit"));
                                list.add(bankk);
                            } catch (JSONException e) {
                                e.printStackTrace();
                                LogUtils.e(TAG, "获取提现银行错误");
                                finish();
                            }
                        }
                        break;
                }
            }catch (Exception e){
                e.printStackTrace();
                hideProcessBar();
                LogUtils.e(TAG, "handler 异常");
            }
        }
    };


    @Override
    public void startProcessBar() {

    }

    @Override
    public void hideProcessBar() {

    }

    @Override
    public void showNetError() {

    }

    @Override
    public void startNextActivity() {

    }
}
