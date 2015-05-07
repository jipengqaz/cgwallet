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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.HashMap;

import cgtz.com.cgwallet.MApplication;
import cgtz.com.cgwallet.R;
import cgtz.com.cgwallet.bean.JsonBean;
import cgtz.com.cgwallet.presenter.SplashPresenter;
import cgtz.com.cgwallet.utility.Constants;
import cgtz.com.cgwallet.utils.CustomTask;
import cgtz.com.cgwallet.utils.LogUtils;
import cgtz.com.cgwallet.utils.Utils;
import cgtz.com.cgwallet.view.ISplashView;
import cgtz.com.cgwallet.widget.CustomEffectsDialog;
import cgtz.com.cgwallet.widget.ProgressDialog;

/**
 * 存钱页面
 */
public class SaveMoneyActivity extends BaseActivity implements ISplashView{
    private static final String TAG = "SaveMoneyActivity";
    private String assets;//账户余额
    private RelativeLayout assetsLayout;//账户余额布局
    private TextView assetsFigure;//账户余额数字
    private LinearLayout transferHintLayout;//
    private TextView transferHint;//账户余额使用介绍
    private EditText editFigure;//输入金额
    private ImageView deleteEdit;//删除输入内容
    private TextView incomeTimeHint;//收益开始时间
    private Button confirmSave;//确认存钱按钮
    private ProgressDialog progressDialog;
    private SplashPresenter presenter;
    private String saveMoney;//输入的存钱金额
    private String assetUseIntruduce="";////余额的使用介绍
    private String startCalculateTime="";//计算收益时间
    private String minSaveMoney = "1";//起投金额
    private Dialog payTypeDialog;//选择支付方式
    private Button balanceBtn;
    private Button bankCardBtn;
    private TextView payMoney;
    private TextView avaliableBalance;
    private LinearLayout bankCardLayout;
    private TextView bankCardMoney;
    private CheckBox checkbox;
    private LinearLayout linear;
    private int isSetTrade = 0;//是否设置了交易密码  0 未设置，1 设置过
    private String useAccount;//使用账户金额
    private String useBank;//使用银行卡支付金额
    private int taskType = 0;// 接口访问的判断值
    private boolean onlyUseAccount = true;//是否余额充足支付
    private boolean isRealleyName;//是否真正实名认证
    private String name;//姓名
    private String identity;//身份证号
    private String bankName;//银行名称
    private String bankCard;//银行卡号
    private String payLimit;//银行卡单笔限额
    private String bankId;//用户绑定的银行卡id
    private String payLimitIntruduce;//银行卡单笔限额描述
    private String bankTip;//银行给出的提示内容
    private String lastBankCordNum;//银行卡后四位
    private CustomEffectsDialog dialog;
    private boolean fromsave;//是否过来  绑卡的
    private boolean needEdit;//是否需要填写信息 true yes, false no
    private DecimalFormat df = new DecimalFormat("#0.00");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_money);
        MApplication.registActivities(this);
        fromsave = getIntent().getBooleanExtra("fromsave",false);
        if( fromsave){
            setTitle(Constants.TITLE_BIND_BANK);
        }else{
            setTitle("存钱");
        }
        showBack(true);
        presenter = new SplashPresenter(this);
        initViews();
        setListener();
    }

    /**
     * 初始化widget
     */
    private void initViews(){
        Utils.safeCopyWrite(this);//设置安全文案
        assetsLayout = (RelativeLayout) findViewById(R.id.save_money_my_assets);//账户余额布局
        assetsFigure = (TextView) findViewById(R.id.save_money_assets);//账户余额数字
        transferHintLayout = (LinearLayout) findViewById(R.id.save_money_transfer_hint);//
        transferHint = (TextView) findViewById(R.id.save_money_assets_use);//账户余额使用介绍
        editFigure = (EditText) findViewById(R.id.et_save_figure);//输入金额
        deleteEdit = (ImageView) findViewById(R.id.delete_edit);//删除输入内容
        incomeTimeHint = (TextView) findViewById(R.id.income_time_hint);//收益开始时间
        confirmSave = (Button) findViewById(R.id.cofirm_save);//确认存钱按钮
    }

    /**
     * 填充widget内容
     */
    private void fillWidget(){
        if(fromsave){//判断是否是来绑卡的   是的话设置默认投资  1元
            editFigure.setText("1");
            editFigure.setSelection(editFigure.getText().toString().trim().length());
            assets = "0.00";
        }
        if(TextUtils.isEmpty(assets) || assets.equals("0.00")){
            assetsLayout.setVisibility(View.GONE);
            transferHintLayout.setVisibility(View.GONE);
        }else{
            assetsLayout.setVisibility(View.VISIBLE);
            transferHintLayout.setVisibility(View.VISIBLE);
            assetsFigure.setText(assets + " 元");
            transferHint.setText(assetUseIntruduce);
        }
        if(!TextUtils.isEmpty(startCalculateTime)){
            incomeTimeHint.setText(startCalculateTime);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        taskType = 0;
        presenter.didFinishLoading(this);
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
        Utils.closeDialog(this,dialog);
        Utils.closeDialog(this,payTypeDialog);
    }

    /**
     * widget添加事件
     */
    private void setListener(){
        Utils.closeInputMethod(this);//关闭输入键盘
        deleteEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editFigure.setText("");
            }
        });
        /**
         * 输入框监听事件
         */
        editFigure.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                /**
                 * 根据输入框中是否存在输入内容，来改变按钮的颜色和是否可点击
                 */
                String str = s.toString().trim();
                if (TextUtils.isEmpty(str)) {
                    //按钮变为不可点击
                    confirmSave.setEnabled(false);
                    confirmSave.setBackgroundResource(R.drawable.bg_button_no_enabled);
                    deleteEdit.setVisibility(View.GONE);
                } else {
                    if(str.length()>1){
                        if(str.subSequence(0, 1).equals("0")){//判断是否大于0
                            editFigure.setText(str.substring(1));
                            editFigure.setSelection(str.substring(1).length());
                        }
                    }
                    //按钮变为可点击
                    confirmSave.setEnabled(true);
                    confirmSave.setBackgroundResource(R.drawable.bg_button_preed);
                    deleteEdit.setVisibility(View.VISIBLE);
                }
            }
        });
        /**
         * 存入操作
         */
        confirmSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveMoney = editFigure.getText().toString().trim();
                if (TextUtils.isEmpty(saveMoney)) {
                    Utils.makeToast(SaveMoneyActivity.this, "请输入要存入的金额");
                } else if (Double.parseDouble(saveMoney) < Double.parseDouble(minSaveMoney)) {
                    Utils.makeToast(SaveMoneyActivity.this, "存入金额不能小于" + minSaveMoney);
                } else {
                    //判断是否设置交易密码
                    if (isSetTrade == 1) {
                        //判断是否有余额  没有直接跳到 支付页面 选择支付方式
                        if (!TextUtils.isEmpty(assets) && !assets.equals("0.00")) {//&& Double.parseDouble(saveMoney) <= Double.parseDouble(assets)
                            showSelectedPayType();
                        } else {
                            useBank = saveMoney;
                            needEdit = true;
                            onlyUseAccount = false;
                            useAccount="0.00";
//                            payMethod();
                            setBeforePay();
                        }
                    } else {
                        //未设置交易密码
                        Utils.makeToast(SaveMoneyActivity.this, "请设置交易密码");
                        startActivity(new Intent(SaveMoneyActivity.this,TradePwdActivity.class));
                    }
                }
            }
        });
    }

    /**
     * 用于切换在未有余额时，是否直接支付还是弹窗确认
     */
    private void payMethod(){
        Intent intent = new Intent(this,InformationConfirmActivity.class);
        intent.putExtra("isRealleyName",isRealleyName);//是否真正实名认证
        intent.putExtra("name",name);//姓名
        intent.putExtra("identity",identity);//身份证号
        intent.putExtra("bankName",bankName);//银行名称
        intent.putExtra("bankCard",bankCard);//银行卡号
        intent.putExtra("payLimit",payLimit);//银行卡单笔限额
        intent.putExtra("bankId",bankId);//用户绑定的银行卡id
        intent.putExtra("payLimitIntruduce",payLimitIntruduce);//银行卡单笔限额描述
        intent.putExtra("bankTip",bankTip);//银行给出的提示内容
        intent.putExtra("lastBankCordNum",lastBankCordNum);//银行卡后四位
        intent.putExtra("saveMoney",saveMoney);//存入金额
        intent.putExtra("useAccount",useAccount);//使用的余额数值
        intent.putExtra("useBank",useBank);//使用的银行卡支付金额
        intent.putExtra("onlyUseAccount",onlyUseAccount);//是否余额充足支付
        intent.putExtra("startCalculateTime",startCalculateTime);//收益开始计算时间
        intent.putExtra("needEdit",needEdit);//是否需要填写信息
        intent.putExtra("fromsave",fromsave);//是否过来  绑卡的

        startActivity(intent);
    }

    /**
     * 选择支付方式
     */
    private void showSelectedPayType(){
        if(payTypeDialog == null){
            LinearLayout payDialogLayout =
                    (LinearLayout) LayoutInflater.from(this)
                            .inflate(R.layout.layout_pay_type, null);
            payTypeDialog = new Dialog(this, R.style.loading_dialog2);
            payTypeDialog.setContentView(payDialogLayout);
            payMoney = (TextView) payDialogLayout.findViewById(R.id.dialog_payment_paymoney);//支付金额
            avaliableBalance =
                    (TextView) payDialogLayout.findViewById(R.id.dialog_payment_avaliable_balance);//账户余额
            bankCardLayout =
                    (LinearLayout) payDialogLayout.findViewById(R.id.dialog_payment_layout_bankcard);//还需银行卡支付布局
            bankCardMoney =
                    (TextView) payDialogLayout.findViewById(R.id.dialog_payment_bankcard_money1);//还需银行卡支付
            balanceBtn = (Button) payDialogLayout.findViewById(R.id.dialog_payment_btn_balance);//立即支付按钮
            bankCardBtn = (Button) payDialogLayout.findViewById(R.id.dialog_payment_btn_bankcard);//取消按钮
            checkbox = (CheckBox) payDialogLayout.findViewById(R.id.yes_no_balance);//是否用余额支付选项
            linear = (LinearLayout) payDialogLayout.findViewById(R.id.yes_no_balance1);//是否用余额支付选项
        }else{
            closeDialog();
        }

        checkbox.setChecked(true);

        payMoney.setText(saveMoney);//支付金额
        if(Double.parseDouble(saveMoney) > Double.parseDouble(assets)){
            onlyUseAccount = false;
            String bankNeedPay = df.format(Double.parseDouble(saveMoney) - Double.parseDouble(assets));
            avaliableBalance.setText(assets);//使用账户金额
            bankCardMoney.setText(bankNeedPay);//银行卡支付金额
        }else{
            onlyUseAccount = true;
            avaliableBalance.setText(saveMoney);//使用账户金额
            bankCardMoney.setText("0.00");//银行卡支付金额
        }

        bankCardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeDialog();
            }
        });

        balanceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeDialog();
                if (checkbox.isChecked()) {
                    //余额和银行卡支付
                    useAccount = avaliableBalance.getText().toString().trim();
                    useBank = bankCardMoney.getText().toString().trim();
                    setBeforePay();
                } else {
                    //银行卡支付
                    useBank = bankCardMoney.getText().toString().trim();
                    useAccount = "0";
                    setBeforePay();
                }
            }
        });

        checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkbox.isChecked()) {//选中使用账户余额
                    if (Double.parseDouble(saveMoney) > Double.parseDouble(assets)) {
                        onlyUseAccount = false;
                        String bankNeedPay = df.format(Double.parseDouble(saveMoney) - Double.parseDouble(assets));
                        avaliableBalance.setText(assets);//使用账户金额
                        bankCardMoney.setText(bankNeedPay);//银行卡支付金额
                    } else {
                        onlyUseAccount = true;
                        avaliableBalance.setText(saveMoney);//使用账户金额
                        bankCardMoney.setText("0.00");//银行卡支付金额
                    }
                } else {//未选中使用账户余额
                    onlyUseAccount = false;
                    avaliableBalance.setText("0.00");//使用账户金额
                    bankCardMoney.setText(saveMoney);
                }
            }
        });
        payTypeDialog.show();
    }

    private void setBeforePay(){
        taskType = 1;
        presenter.didFinishLoading(this);
    }

    private void closeDialog(){
        Utils.closeDialog(this, payTypeDialog);
    }

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
        if(taskType == 0){
            //页面进入获取数据
            HashMap<String,String> params = new HashMap();
            params.put("user_id",Utils.getUserId());
            params.put("token",Utils.getToken());
            CustomTask task = new CustomTask(mHandler, Constants.WHAT_WALLET_DEPOSIT
                    ,Constants.URL_WALLET_DEPOSIT,
                    true,params,true);
            task.execute();
        }else if(taskType == 1){
            //银行卡支付时，判断是否需要绑定银行卡
            HashMap<String,String> params = new HashMap();
            params.put("user_id",Utils.getUserId());
            params.put("token",Utils.getToken());
            CustomTask task = new CustomTask(mHandler, Constants.WHAT_BEFORE_PAY
                    ,Constants.URL_BEFORE_PAY,
                    true,params,true);
            task.execute();
        }
    }

    private void beforePayDialog(final JSONObject json){
        dialog = CustomEffectsDialog.getInstans(this);
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
                payTypeDialog.dismiss();
            }
        });
        dialog.withButton2Click(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                //不支持连连，需要重新绑定
                if(json.optInt("success") == -4){
                    //未真正实名 已绑卡 但是不支持连连
                    needEdit = true;
                    isRealleyName = false;
                    name = json.optString("name");//用户姓名
                    identity = json.optString("identity");//用户身份证号
                    payMethod();
                }else if(json.optInt("success") == -7){
                    //已真正实名认证  已绑卡 不支持连连
                    needEdit = true;
                    isRealleyName = true;
                    name = json.optString("name");//用户姓名
                    identity = json.optString("identity");//用户身份证号
                    payMethod();
                }
            }
        });
        dialog.show();
    }

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            try{
                needEdit = true;
                isRealleyName = false;//是否真正实名认证
                JsonBean jsonBean = (JsonBean) msg.obj;
                int code = jsonBean.getCode();
                String errorMsg = jsonBean.getError_msg();
                if(code == Constants.DATA_EVENT){
                    hideProcessBar();
                    Utils.makeToast(SaveMoneyActivity.this,Constants.ERROR_MSG_CODE+code);
                    return;
                }
                int action = msg.what;
                hideProcessBar();
                boolean flag = Utils.filtrateCode(SaveMoneyActivity.this,jsonBean);
                switch (action){
                    case Constants.WHAT_WALLET_DEPOSIT:
                        if(flag && code == Constants.OPERATION_FAIL){//数据交互失败
                            Utils.makeToast(SaveMoneyActivity.this, errorMsg);
                        }else if(flag && code == Constants.OPERATION_SUCCESS){//数据交互成功
                            JSONObject jsonObject = jsonBean.getJsonObject();
                            assets = jsonObject.optString("capitalAccountBalance");//账户余额
                            assetUseIntruduce = jsonObject.optString("tip");////余额的使用介绍
                            startCalculateTime = jsonObject.optString("startInterestDay");//计算收益时间
                            minSaveMoney = jsonObject.optString("min");//起投金额
                            isSetTrade = jsonObject.optInt("payPassSet");//是否设置过交易密码
                            fillWidget();
                        }
                        break;
                    case Constants.WHAT_BEFORE_PAY://银行卡是否绑定
                        LogUtils.i(TAG,"银行卡是否绑定: "+jsonBean.getJsonString());
                        JSONObject json = jsonBean.getJsonObject();
                        if(flag){
                            if(code == -1){
                                //未设置交易密码
                                needEdit = true;
                                isRealleyName = false;//是否真正实名认证
                                startActivity(new Intent(SaveMoneyActivity.this,TradePwdActivity.class));
                            }else if(code == -2){
                                //未实名认证未绑卡
                                needEdit = true;
                                isRealleyName = false;//是否真正实名认证
                                payMethod();
                            }else if(code == -3){
                                //未真正实名 未绑卡
                                needEdit = true;
                                isRealleyName = false;//是否真正实名认证
                                name = json.optString("name");//姓名
                                identity = json.optString("identity");//身份证号
                                payMethod();
                            }else if(code == -4){
                                //未真正实名 已绑卡 但是不支持连连
                                beforePayDialog(json);
                            }else if(code == -5){
                                //未真正实名 已绑卡 支持连连 但未绑定连连
                                needEdit = true;//是否需要填写信息
                                isRealleyName = false;//是否真正实名认证
                                name = json.optString("name");//姓名
                                identity = json.optString("identity");//身份证号
                                bankName = json.optString("bankName");//银行名称
                                bankCard = json.optString("fullCardNumber");//银行卡号
                                payLimit = json.optString("pay_limit");//银行卡单笔限额
                                bankId = json.optString("bankId");//用户绑定的银行卡id
                                payLimitIntruduce = json.optString("pay_limit_desc");//银行卡单笔限额描述
                                payMethod();
                            }else if(code == -6){
                                // 已真正实名认证  未绑卡
                                needEdit = true;//是否需要填写信息
                                isRealleyName = true;//是否真正实名认证
                                name  = json.optString("name");//姓名
                                identity = json.optString("identity");//身份证号
                                payMethod();
                            }else if(code == -7){
                                //已真正实名认证  已绑卡 不支持连连
                                beforePayDialog(json);
                            }else if(code == -8){
                                //已真正实名认证  已绑卡 支持连连  但未绑定连连
                                needEdit = true;//是否需要填写信息
                                isRealleyName = true;//是否真正实名认证
                                name = json.optString("name");//姓名
                                identity = json.optString("identity");//身份证号
                                bankName = json.optString("bankName");//银行名称
                                bankCard = json.optString("fullCardNumber");//银行卡号
                                payLimit = json.optString("pay_limit");//银行卡单笔限额
                                bankId = json.optString("bankId");//用户绑定的银行卡id
                                payLimitIntruduce = json.optString("pay_limit_desc");//银行卡单笔限额描述
                                payMethod();
                            }else if(code == Constants.OPERATION_SUCCESS){
                                //可以直接支付
                                needEdit = false;//是否需要填写信息
                                isRealleyName = true;//是否真正实名认证
                                name = json.optString("name");//姓名
                                identity = json.optString("identity");//身份证号
                                bankName = json.optString("bankName");//银行名称
                                bankCard = json.optString("fullCardNumber");//银行卡号
                                payLimit = json.optString("pay_limit");//银行卡单笔限额
                                bankId = json.optString("bankId");//用户绑定的银行卡id
                                payLimitIntruduce = json.optString("pay_limit_desc");//银行卡单笔限额描述
                                bankTip = json.optString("bank_tip");//银行给出的提示内容
                                lastBankCordNum = json.optString("last4number");//银行卡后四位
                                payMethod();
                            }else if(code == Constants.OPERATION_FAIL){
                                //出错
                                needEdit = true;//是否需要填写信息
                                isRealleyName = false;//是否真正实名认证
                                Utils.makeToast(SaveMoneyActivity.this,errorMsg);
                            }
                        }else{
                            Utils.makeToast(SaveMoneyActivity.this,errorMsg);
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
}
