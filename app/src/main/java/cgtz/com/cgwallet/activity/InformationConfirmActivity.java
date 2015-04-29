package cgtz.com.cgwallet.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import cgtz.com.cgwallet.MApplication;
import cgtz.com.cgwallet.R;
import cgtz.com.cgwallet.adapter.BankAdapter;
import cgtz.com.cgwallet.bean.Bank;
import cgtz.com.cgwallet.bean.JsonBean;
import cgtz.com.cgwallet.presenter.SplashPresenter;
import cgtz.com.cgwallet.utility.Constants;
import cgtz.com.cgwallet.utils.CustomTask;
import cgtz.com.cgwallet.utils.LogUtils;
import cgtz.com.cgwallet.utils.MD5Util;
import cgtz.com.cgwallet.utils.Utils;
import cgtz.com.cgwallet.utils.llutils.BaseHelper;
import cgtz.com.cgwallet.utils.llutils.Lianlian;
import cgtz.com.cgwallet.utils.llutils.MobileSecurePayer;
import cgtz.com.cgwallet.utils.llutils.PayOrder;
import cgtz.com.cgwallet.view.ISplashView;
import cgtz.com.cgwallet.widget.CustomDialog;
import cgtz.com.cgwallet.widget.CustomEffectsDialog;
import cgtz.com.cgwallet.widget.ProgressDialog;

/**
 * 信息确认页面
 */
public class InformationConfirmActivity extends BaseActivity implements ISplashView{
    private static final String TAG = "InformationConfirmActivity";
    private boolean onlyUseAccount;//是否余额充足支付
    private boolean isRealleyName;//是否真正实名认证
    private boolean needEdit;//是否需要填写信息
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
    private String startCalculateTime;//开始计算收益时间
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
    private LinearLayout layoutAccountBank;//余额支付和银行卡支付的布局
    private ArrayList<Bank> list = new ArrayList<Bank>();//存放银行名称的
    private boolean noBank = true;//判断是否有可选银行列表
    private boolean b = false;//用于判断银行卡输入时加空格的
    private CustomEffectsDialog cDiaog;//
    private CustomDialog customDialog;//连连回调的错误信息显示dialog
    private String tradePwd;//输入的交易密码
    private ProgressDialog progressDialog;
    private SplashPresenter presenter;
    private int payType = 3;//第三方支付渠道
    private String MD5_KEY;
    private String no_order;
    private String dt_order;
    private String notifyUrl;
    private String no_agree;
    private PayOrder order = null;
    private boolean fromsave;//是否过来  绑卡的
    private String lianlianTest = "0.01";//测试时，连连支付金额

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information_confirm);
        MApplication.registActivities(this);
        getIntentInfo();
        if(fromsave){
            setTitle(Constants.TITLE_BIND_BANK);
        }else{
            setTitle("存钱");
        }
        showBack(true);
        presenter = new SplashPresenter(this);
        initViews();
        fillWidget();
        setListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        closeDialogs();
    }

    private void closeDialogs(){
        hideProcessBar();
        Utils.closeDialog(this,cDiaog);
        Utils.closeDialog(this,customDialog);
    }

    private void getIntentInfo(){
        needEdit = getIntent().getBooleanExtra("needEdit",true);//是否过来  绑卡的
        fromsave = getIntent().getBooleanExtra("fromsave",false);//是否来自实名认证
        onlyUseAccount = getIntent().getBooleanExtra("onlyUseAccount",false);//是否余额充足支付
        isRealleyName = getIntent().getBooleanExtra("isRealleyName", false);//是否真正实名认证
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
        startCalculateTime = getIntent().getStringExtra("startCalculateTime");//开始计算收益时间
        if(!Constants.IS_TEST){
            lianlianTest = useBank;
        }
    }

    /**初始化视图*/
    private void initViews(){
//        UtilityUtils.setPossession_TextView(this);//设置文案  资金安全
        layoutAccountBank = (LinearLayout) findViewById(R.id.investment_payment);
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
        /**
         * 判断是否余额充足支付
         */
        if(onlyUseAccount){
            //余额充足
            LogUtils.i(TAG, "余额充足支付，不显示个人信息");
            layout_need_edit.setVisibility(View.GONE);
            layout_neednot_edit.setVisibility(View.GONE);
            layoutAccountBank.setVisibility(View.GONE);

        }else{
            /**
             * 判断是否需要填写信息，来显示不同布局
             * true 需要填写信息
             * false 不需要填写信息
             */
            if(needEdit){
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
                        edit_username.setSelection(name.length());
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
                        edit_identity.setSelection(identity.length());
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

    private void setListener(){
        Utils.closeInputMethod(this);//关闭输入键盘
        /**edittext添加空格
         *
         */
        edit_bankcard.addTextChangedListener(new TextWatcher() {
            private int lastlen = 0;
            private String lastString = "";
            private int myend = 0;
            private StringBuilder sb = null;

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
                if (b) {
                    lastlen = edit_bankcard.getText().toString().replaceAll(" ", "")
                            .length();
                    b = false;
                    return;
                }
                sb = new StringBuilder();
                myend = edit_bankcard.getSelectionEnd();
                String str = edit_bankcard.getText().toString();
                String strtrim = str.replaceAll(" ", "");
                sb.append(strtrim);
                int len = strtrim.length();
                if (len >= lastlen) {
                    lastString = edit_bankcard.getText().toString();
                    lastlen = len;
                    int j = 0;
                    for (int i = 0; i < len - 1; i++) {
                        if (i == 3 || i == 7 || i == 11 || i == 15 || i == 19) {
                            j++;
                            sb.insert(i + j, " ");
                            b = true;
                        }
                    }
                    if (b) {
                        edit_bankcard.setText(sb.toString());
                        if (sb.charAt(myend - 1) == ' ') {
                            edit_bankcard.setSelection(myend + 1);
                        } else {
                            edit_bankcard.setSelection(myend);
                        }
                    }
                } else {
                    lastString = edit_bankcard.getText().toString();
                    lastlen = len;
                    int j = 0;
                    if (len == 4) {
                        b = true;
                        myend = 4;
                    } else {
                        for (int i = 0; i < len - 1; i++) {
                            if (i == 3 || i == 7 || i == 11 || i == 15 || i == 19) {
                                j++;
                                sb.insert(i + j, " ");
                                b = true;
                            }
                        }
                    }
                    if (b) {
                        edit_bankcard.setText(sb.toString());
                        if (myend > 0 && lastString.charAt(myend - 1) == ' ') {
                            edit_bankcard.setSelection(myend - 1);
                        } else {
                            edit_bankcard.setSelection(myend);
                        }
                    }
                }
            }
        });

        /**选择银行按钮**/
        text_bankname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!noBank) {
                    chooseBank();
                } else {
                    Utils.makeToast(InformationConfirmActivity.this,
                            getResources().getString(R.string.error_no_bank_list));
                }
            }
        });

        /**确定支付按钮**/
        btn_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!onlyUseAccount && needEdit) {
                    //需要重新填写个人信息
                    if (!isRealleyName) {
                        name = edit_username.getText().toString().trim();
                        identity = edit_identity.getText().toString().trim();
                    }
                    bankName = text_bankname.getText().toString().trim();//银行名称
                    bankCard = edit_bankcard.getText().toString().trim().replaceAll(" ", "");//银行卡号
                    LogUtils.i(TAG, "name: " + name + " identity: " + identity + " bankName: " + bankName +
                            " bankCard: "+bankCard);
                    if (TextUtils.isEmpty(name)) {
                        Utils.makeToast(InformationConfirmActivity.this, "用户姓名错误");
                    } else if (TextUtils.isEmpty(identity) || identity.length() < 14
                            || identity.length() > 18) {
                        Utils.makeToast(InformationConfirmActivity.this, "用户身份证号信息错误");
                    } else if (TextUtils.isEmpty(saveMoney)) {
                        Utils.makeToast(InformationConfirmActivity.this, "支付金额错误");
                    } else if (TextUtils.isEmpty(bankName)) {
                        Utils.makeToast(InformationConfirmActivity.this, "银行信息填写错误");
                    } else if (!TextUtils.isEmpty(payLimit) &&
                            Double.parseDouble(saveMoney) > Double.parseDouble(payLimit)) {
                        Utils.makeToast(InformationConfirmActivity.this, "该银行卡单笔最高可支付" + payLimit + "万元");
                    } else {
                        final View layoutTradePwd = LayoutInflater.from(InformationConfirmActivity.this)
                                .inflate(R.layout.layout_tradepwd, null);
                        cDiaog =
                                CustomEffectsDialog.getInstans(InformationConfirmActivity.this);
                        cDiaog.setCustomView(layoutTradePwd, InformationConfirmActivity.this);
                        cDiaog.withTitle("请验证交易密码");
                        cDiaog.withMessage(null);
                        cDiaog.withBtnLineColor(R.color.bg_line);
                        cDiaog.withBtnContentLineColor(R.color.bg_line);
                        cDiaog.withButton1Text("取消");
                        cDiaog.withButton1Click(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                closeDialog();
                            }
                        });
                        cDiaog.withButton2Text("确定");
                        cDiaog.withButton2Click(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                tradePwd = ((EditText) layoutTradePwd.findViewById(R.id.trade_password))
                                        .getText().toString().trim();//交易密码
                                if (TextUtils.isEmpty(tradePwd)) {
                                    Utils.makeToast(InformationConfirmActivity.this, "请输入交易密码");
                                } else {
                                    //生成订单
                                    closeDialog();
                                    presenter.didFinishLoading(InformationConfirmActivity.this);
                                }
                            }
                        });
                        cDiaog.setCancelable(false);
                        cDiaog.setCanceledOnTouchOutside(false);
                        cDiaog.show();
                    }
                } else {
                    final View layoutTradePwd = LayoutInflater.from(InformationConfirmActivity.this)
                            .inflate(R.layout.layout_tradepwd, null);
                    cDiaog =
                            CustomEffectsDialog.getInstans(InformationConfirmActivity.this);
                    cDiaog.setCustomView(layoutTradePwd, InformationConfirmActivity.this);
                    cDiaog.withTitle("请验证交易密码");
                    cDiaog.withMessage(null);
                    cDiaog.withBtnLineColor(R.color.bg_line);
                    cDiaog.withBtnContentLineColor(R.color.bg_line);
                    cDiaog.withButton1Text("取消");
                    cDiaog.withButton1Click(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            closeDialog();
                        }
                    });
                    cDiaog.withButton2Text("确定");
                    cDiaog.withButton2Click(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            tradePwd = ((EditText) layoutTradePwd.findViewById(R.id.trade_password))
                                    .getText().toString().trim();//交易密码
                            if (TextUtils.isEmpty(tradePwd)) {
                                Utils.makeToast(InformationConfirmActivity.this, "请输入交易密码");
                            } else {
                                //生成订单
                                closeDialog();
                                presenter.didFinishLoading(InformationConfirmActivity.this);
                            }
                        }
                    });
                    cDiaog.setCancelable(false);
                    cDiaog.setCanceledOnTouchOutside(false);
                    cDiaog.show();
                }
            }
        });
    }

    private void closeDialog(){
        Utils.closeDialog(this,cDiaog);
    }

    /**
     * 选择银行
     */
    public void chooseBank(){
        LinearLayout choose_bank_card_layout =
                (LinearLayout) LayoutInflater.from(this).inflate(R.layout.activity_choose_bank_card,null);
        final Dialog choose_bank_card_dialog = new Dialog(this,R.style.loading_dialog2);
        Button ensure_btn = (Button) choose_bank_card_layout.findViewById(R.id.btn_confirm);//确定按钮
        ListView listview = (ListView) choose_bank_card_layout.findViewById(R.id.lv_bankname);
        TextView title = (TextView) choose_bank_card_layout.findViewById(R.id.title);//标题

        ensure_btn.setVisibility(View.GONE);//隐藏按钮

        title.setText("请选择银行");
        BankAdapter bAdapter = new BankAdapter(this,list);
        listview.setAdapter(bAdapter);
        listview.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {//存储选择的银行名
                Bank bank = (Bank) parent.getItemAtPosition(position);
                bankName = bank.getName();//银行名
                bankId = bank.getId()+"";//银行卡id
                double pay_limit_test = bank.getPay_limit();
                payLimit = bank.getPay_limit()*10000+"";
                tv_pay_limit_desc.setText("该银行卡单笔最高可支付"+pay_limit_test+"万元");
                tv_pay_limit_desc.setVisibility(View.VISIBLE);
                text_bankname.setText(bankName);
                choose_bank_card_dialog.dismiss();
            }
        });
        choose_bank_card_dialog.setContentView(choose_bank_card_layout);
        choose_bank_card_dialog.show();
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
                if(msg.what == cgtz.com.cgwallet.utils.llutils.Constants.RQF_PAY){
                    //连连sdk内容
                    LogUtils.i(TAG, "连连sdk内容：" + msg.obj.toString());
                    customDialog =
                            new CustomDialog(InformationConfirmActivity.this,R.style.loading_dialog2);
                    JSONObject objContent = BaseHelper.string2JSON(msg.obj.toString());
                    String retCode = objContent.optString("ret_code");
                    String retMsg = objContent.optString("ret_msg");
                    // 先判断状态码，状态码为 成功或处理中 的需要 验签
                    if (cgtz.com.cgwallet.utils.llutils.Constants.RET_CODE_SUCCESS.equals(retCode)
                            || cgtz.com.cgwallet.utils.llutils.Constants.RET_CODE_PROCESS.equals(retCode)) {
                        String resulPay = objContent
                                .optString("result_pay");
                        if (cgtz.com.cgwallet.utils.llutils.Constants.RESULT_PAY_SUCCESS
                                .equalsIgnoreCase(resulPay)
                                || cgtz.com.cgwallet.utils.llutils.Constants.RESULT_PAY_PROCESSING
                                .equalsIgnoreCase(resulPay)) {
                            // TODO 支付成功后续处理
//                                    if(runningDialog != null){
//                                        runningDialog.setMessage("充值成功，正在确认投资记录");
//                                        runningDialog.show();
//                                    }
                            HashMap<String,String> params = new HashMap<>();
                            params.put("user_id",Utils.getUserId()+"");
                            params.put("token",Utils.getToken());
                            params.put("trade_no", no_order);
                            if(isRealleyName){
                                LogUtils.i(TAG,"isAuth is false");
                                params.put("name",name);
                                params.put("identity",identity);
                            }
                            CustomTask task = new CustomTask(mHandler, Constants.WHAT_BANKCARD_LLBIND
                                    ,Constants.URL_BANKCARD_LLBIND,
                                    true,params,true);
                            task.execute();
                        } else {
                            customDialog.setMessage(retMsg);
                            customDialog.setConfirmBtnText("确认");
                            customDialog.show();
                            customDialog.setConfirmListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    closeDialog();
                                    customDialog.dismiss();
                                }
                            });
                        }
                    } else {
                        customDialog.setMessage(retMsg);
                        customDialog.setConfirmBtnText("确认");
                        customDialog.show();
                        customDialog.setConfirmListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                closeDialog();
                                customDialog.dismiss();
                            }
                        });
                    }
                    return;
                }
                JsonBean jsonBean = (JsonBean) msg.obj;
                int code = jsonBean.getCode();
                String errorMsg = jsonBean.getError_msg();
                if(code == Constants.DATA_EVENT){
                    hideProcessBar();
                    Utils.makeToast(InformationConfirmActivity.this, Constants.ERROR_MSG_CODE + code);
                    return;
                }
                int action = msg.what;
                JSONObject json = jsonBean.getJsonObject();
                boolean flag = Utils.filtrateCode(InformationConfirmActivity.this,jsonBean);
                switch (action){
                    case Constants.WHAT_SELECTED_BANK:
                        hideProcessBar();
                        noBank = true;
                        if(flag){
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
                                }
                            }
                            if(list != null && list.size()>0){
                                noBank = false;
                            }
                        }
                        break;
                    case Constants.WHAT_EWALLET_AFFIRMDO://账户余额支付
                        LogUtils.i(TAG, "账户余额支付: " + jsonBean.getJsonString());
                        if(flag){
                            if(code == Constants.OPERATION_FAIL){//数据交互失败
                                Utils.makeToast(InformationConfirmActivity.this, errorMsg);
                            }else if(code == Constants.OPERATION_SUCCESS){//数据交互成功
                                int paying = json.optInt("paying");
                                if(paying == 1){
                                    //支付处理中
                                    hideProcessBar();
                                    startActivity(new Intent(InformationConfirmActivity.this,
                                            InProgressActivity.class)
                                            .putExtra("isSaveAt",true));//跳转到处理中
                                    finish();
                                }else if(paying == 0){
                                    //支付成功
                                    hideProcessBar();
                                    startActivity(new Intent(InformationConfirmActivity.this,
                                            SaveMoneySuccessActivity.class)
                                            .putExtra("saveMoney",saveMoney)
                                            .putExtra("startCalculateTime", startCalculateTime));//跳转到支付成功页面
                                    finish();
                                }
                            }else{
                                hideProcessBar();
                                Utils.makeToast(InformationConfirmActivity.this,errorMsg);
                            }
                        }
                        break;
                    case Constants.WHAT_EWALLET_AFFIRMREDIRECT:
                        //草根钱包使用第三方支付生成订单返回结果数据
                        LogUtils.i(TAG, "草根钱包使用第三方支付生成订单: " + jsonBean.getJsonString());
                        if(flag){
                            if(code == Constants.OPERATION_SUCCESS){//订单生成成功
                                //连连支付流程
                                String tradeNo = json.optString("tradeNo");//订单号
                                HashMap<String,String> params = new HashMap<>();
                                params.put("user_id", Utils.getUserId());
                                params.put("token",Utils.getToken());
                                params.put("money_order",lianlianTest);//(订单金额)
                                params.put("no_order",tradeNo);//订单编号
                                params.put("valid_order","");//订单有效时间
                                params.put("bank_id",bankId);//银行id
                                params.put("card_number",bankCard);//银行卡号
                                params.put("bank_name",bankName);//银行名称
                                params.put("name",name);//姓名
                                params.put("identity",identity);//身份证号
                                if(needEdit){
                                    params.put("is_recharge","1");//需要绑卡传1 已经绑卡了 传0
                                }else{
                                    params.put("is_recharge","0");//需要绑卡传1 已经绑卡了 传0
                                }

                                CustomTask task = new CustomTask(mHandler, Constants.WHAT_SIGNPORT
                                        ,Constants.UTL_SIGNPORT,
                                        true,params,true);
                                task.execute();
                            }else{
                                Utils.makeToast(InformationConfirmActivity.this,errorMsg);
                                hideProcessBar();
                            }
                        }
                        break;
                    case Constants.WHAT_SIGNPORT:
                        LogUtils.i(TAG,"签名返回："+jsonBean.getJsonString());
                        hideProcessBar();
                        if(flag){
                            if(code == Constants.OPERATION_SUCCESS){
                                //签名获取成功
                                MD5_KEY = json.optString("sign");
                                no_order = json.optString("no_order");
                                dt_order  = json.optString("dt_order");
                                notifyUrl = json.optString("notifyUrl");
                                bankTip = json.optString("bank_tip");
                                no_agree = json.optString("no_agree");
                                name  = json.optString("name");
                                identity  = json.optString("identity");
                                if(!TextUtils.isEmpty(no_agree)){
                                    flag = false;
                                }else{
                                    flag = true;
                                }
                                order = Lianlian.constructPreCardPayOrder(
                                        flag, no_order, dt_order, notifyUrl, Utils.getUserId() + "",
                                        identity, name, lianlianTest + ""
                                        , bankCard, MD5_KEY, no_agree
                                );
                                String content4Pay = BaseHelper.toJSONString(order);
                                // 关键 content4Pay 用于提交到支付SDK的订单支付串，如遇到签名错误的情况，
                                // 请将该信息帖给我们的技术支持
                                LogUtils.i(InformationConfirmActivity.class.getSimpleName(), content4Pay);
                                MobileSecurePayer msp = new MobileSecurePayer();
                                boolean bRet = msp.pay(content4Pay, mHandler,
                                        cgtz.com.cgwallet.utils.llutils.Constants.RQF_PAY,
                                        InformationConfirmActivity.this, false);
                                LogUtils.i(InformationConfirmActivity.class.getSimpleName(), String.valueOf(bRet));
                            }else{
                                Utils.makeToast(InformationConfirmActivity.this,errorMsg);
                            }
                        }
                        break;
                    case Constants.WHAT_BANKCARD_LLBIND://预绑成功之后调用 用来银行卡绑定连连
                        LogUtils.i(TAG,"预绑成功之后调用 用来银行卡绑定连连: "+jsonBean.getJsonString());
                        if(flag){
                            if(code == Constants.OPERATION_SUCCESS){//绑定成功
                                HashMap<String,String> params = new HashMap<>();
                                params.put("user_id",Utils.getUserId());
                                params.put("token",Utils.getToken());
                                params.put("tradeNo",no_order);
                                CustomTask task = new CustomTask(mHandler, Constants.WHAT_PAYSTATUS
                                        ,Constants.URL_PAYSTATUS,
                                        true,params,true);
                                task.execute();
                            }else{
                                Utils.makeToast(InformationConfirmActivity.this,errorMsg);
                                hideProcessBar();
                            }
                        }
                        break;
                    case Constants.WHAT_PAYSTATUS://投资时连连通道银行卡充值成功后，返回值判断
                        LogUtils.i(TAG, "连连通道银行卡充值成功后: " + jsonBean.getJsonString());
                        if(flag){
                            if(code == Constants.OPERATION_SUCCESS){
                                int paying = json.optInt("paying");
                                if(paying == 0){
                                    //支付成功
                                    hideProcessBar();
                                    startActivity(new Intent(InformationConfirmActivity.this,
                                            SaveMoneySuccessActivity.class)
                                            .putExtra("startCalculateTime", startCalculateTime)
                                            .putExtra("saveMoney",saveMoney));//跳转到支付成功页面
                                    finish();
                                }else if(paying == 1){
                                    //处理中
                                    hideProcessBar();
                                    startActivity(new Intent(InformationConfirmActivity.this,
                                            InProgressActivity.class)
                                            .putExtra("isSaveAt",true));//跳转到支付处理中
                                    finish();
                                }
                            }else{
                                hideProcessBar();
                                Utils.makeToast(InformationConfirmActivity.this,errorMsg);
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
        if(progressDialog == null){
            progressDialog = new ProgressDialog(this,R.style.loading_dialog);
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
        Utils.makeToast(this, Constants.IS_EVENT_MSG);
    }


    @Override
    public void startNextActivity() {
        /**
         *  填写个人信息时支付
         */
            /**
             * 判断是否只使用余额支付
             */
            if(onlyUseAccount){
                //只使用账户余额支付
                HashMap<String,String> params = new HashMap<>();
                params.put("user_id",Utils.getUserId());
                params.put("token",Utils.getToken());
                params.put("password", MD5Util.md5(tradePwd));//支付密码
                params.put("cap",useAccount);//余额支付数值
                params.put("amount",saveMoney);//转入金额
                params.put("order_from","1");//订单来源 1:android 2:ios
                params.put("card_no", "");//银行卡号
                CustomTask task = new CustomTask(mHandler, Constants.WHAT_EWALLET_AFFIRMDO
                        ,Constants.URL_EWALLET_AFFIRMDO,
                        true,params,true);
                task.execute();
            }else{
                //使用到银行卡支付
                //草根钱包支付
                LogUtils.i(TAG, "草根钱包第三方支付");
                HashMap<String,String> params = new HashMap<>();
                params.put("user_id",Utils.getUserId());
                params.put("token",Utils.getToken());
                params.put("password", MD5Util.md5(tradePwd));//支付密码
                params.put("cap",useAccount);//余额支付数值
                params.put("amount",saveMoney);//转入金额
                params.put("payneeded",useBank);//第三方支付金额
                params.put("pay_method",payType+"");//第三方通道类型 3:连连支付
                params.put("order_from","1");//订单来源 1:android 2:ios
                params.put("bank_id",bankId);//银行id
                params.put("card_no",bankCard);//银行卡号
                CustomTask task = new CustomTask(mHandler, Constants.WHAT_EWALLET_AFFIRMREDIRECT
                        ,Constants.URL_EWALLET_AFFIRMREDIRECT,
                        true,params,true);
                task.execute();
            }

    }
}
