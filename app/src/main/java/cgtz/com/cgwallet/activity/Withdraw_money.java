package cgtz.com.cgwallet.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONObject;

import cgtz.com.cgwallet.MApplication;
import cgtz.com.cgwallet.R;
import cgtz.com.cgwallet.bean.BankCard;
import cgtz.com.cgwallet.bean.JsonBean;
import cgtz.com.cgwallet.client.Withdraw_money_Client;
import cgtz.com.cgwallet.utils.LogUtils;
import cgtz.com.cgwallet.utils.Utils;
import cgtz.com.cgwallet.widget.CustomEffectsDialog;
import cgtz.com.cgwallet.widget.ProgressDialog;

/**
 * 取钱界面
 * Created by Administrator on 2015/4/28 0028.
 */
public class Withdraw_money  extends BaseActivity implements View.OnClickListener{
    private  String TAG = "Withdraw_money";

    private BankCard card;//银行卡信息
    private String capitalBalance;//可用余额
    private String tip,tip2;//提示文案
    private int success;//判断值
    private TextView text_tip2,text_tip1;//显示文案
    private Button apply_withdraw;//取钱按钮
    private ImageView delete_edit;//删除输入数据
    private EditText with_draw_num;//取钱金额
    private TextView available_balance;//可取金额
    private TextView bank_tail,bank_name;//银行卡尾号，银行名
    private ImageView bank_icon;//银行图标
    private LinearLayout withdraw_box;
    private InputMethodManager imm;
    private String withdrawAmount;//输入的提现金额;
    private ProgressDialog pDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showBack(true);
        MApplication.registActivities(this);
        setContentView(R.layout.withdraw_money);
        setTitle("取钱");
        if(savedInstanceState !=null ){//从savedInstanceState获取数据
            capitalBalance =savedInstanceState.getString("capitalBalance");
            card = (BankCard) savedInstanceState.getSerializable("card");
            tip = savedInstanceState.getString("tip");
            tip2 = savedInstanceState.getString("tip2");
            success = savedInstanceState.getInt("success",1);
        }else{
            capitalBalance = getIntent().getStringExtra("capitalBalance");
            card = (BankCard) getIntent().getSerializableExtra("card");
            tip = getIntent().getStringExtra("tip");
            tip2 =getIntent().getStringExtra("tip2");
            success = getIntent().getIntExtra("success",1);
        }
        init();
        assignment();
    }

    /**
     * 初始化控件
     */
    private void init(){
        text_tip2 = (TextView) findViewById(R.id.text_tip2);
        text_tip1 = (TextView) findViewById(R.id.text_tip1);
        apply_withdraw = (Button) findViewById(R.id.apply_withdraw);
        delete_edit = (ImageView) findViewById(R.id.delete_edit);
        with_draw_num = (EditText) findViewById(R.id.with_draw_num);
        available_balance = (TextView) findViewById(R.id.available_balance);
        bank_tail = (TextView) findViewById(R.id.bank_tail);
        bank_name = (TextView) findViewById(R.id.bank_name);
        bank_icon = (ImageView) findViewById(R.id.bank_icon);
        withdraw_box = (LinearLayout) findViewById(R.id.withdraw_box);
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);//获取输入键盘
    }

    /**
     * 给控件赋值
     */
    private void assignment(){
        text_tip2.setText(tip2);
        text_tip1.setText(tip);
        bank_tail.setText("尾号 "+card.getCardLast());
        bank_name.setText(card.getBankName());
        available_balance.setText(capitalBalance + " 元");
        bank_icon.setImageResource(BankCard.getBankIcon(Integer.parseInt(card.getBankId())));
        if(success == -2){
            apply_withdraw.setEnabled(false);
            apply_withdraw.setBackgroundResource(R.drawable.banned_click);
            apply_withdraw.setText("今天您已经提现过一次，请明天再来");
        }
        delete_edit.setOnClickListener(this);
        apply_withdraw.setOnClickListener(this);

        /**
         * 屏幕点击，隐藏键盘
         */
        withdraw_box.setOnClickListener(this);

        with_draw_num.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                String str = charSequence.toString();
                if(str.length()>0){
                    delete_edit.setVisibility(View.VISIBLE);
                }else{
                    delete_edit.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(pDialog.isShowing()){
                pDialog.dismiss();
            }
            JsonBean jsonBean = (JsonBean) msg.obj;
            int code = jsonBean.getCode();
            String errorMsg = jsonBean.getError_msg();
            if(!Utils.filtrateCode(Withdraw_money.this,jsonBean)){
                Utils.makeToast(Withdraw_money.this, errorMsg + "  错误码" + code);
                return;
            }
            JSONObject json = jsonBean.getJsonObject();
            LogUtils.e(TAG,json+"");
            String status = json.optString("success");
            if ("0".equals(status)) {
                Utils.makeToast(Withdraw_money.this, json.optString("msg"));
            }else if(status.equals("-1")){
                Utils.makeToast(Withdraw_money.this, json.optString("msg"));
            } else if(status.equals("1")) {
                Utils.makeToast(Withdraw_money.this, json.optString("msg"));
                /**提现请求成功， 跳转提现成功页面*/
                startActivity(new Intent(Withdraw_money.this, InProgressActivity.class).putExtra("isSaveAt",false));
                finish();
            }
        }
    };
    @Override
    public void onClick(View v) {
    switch(v.getId()){
        case R.id.delete_edit://清空输入框数据
            with_draw_num.setText("");
            delete_edit.setVisibility(View.GONE);
            break;
        case R.id.withdraw_box:
            if (imm != null && imm.isActive()) {
                imm.hideSoftInputFromWindow(withdraw_box.getWindowToken(), 0);
            }
            break;
        case R.id.apply_withdraw:
            withdrawAmount = with_draw_num.getText().toString();
            if(withdrawAmount.isEmpty()){
                Utils.makeToast(Withdraw_money.this, "请输入所要提现金额");
            }else if(Double.parseDouble(withdrawAmount) <=0){
                Utils.makeToast(Withdraw_money.this, "提现金额不可为0元");
            }else if(Double.parseDouble(withdrawAmount) > Double.parseDouble(capitalBalance)){
                Utils.makeToast(Withdraw_money.this, "输入金额大于可提现金额");
            }else{
                final View layoutTradePwd = LayoutInflater.from(Withdraw_money.this)
                        .inflate(R.layout.layout_tradepwd,null);
                final CustomEffectsDialog cDiaog =
                        CustomEffectsDialog.getInstans(Withdraw_money.this);
                cDiaog.setCustomView(layoutTradePwd, Withdraw_money.this);
                cDiaog.withTitle("请验证交易密码");
                cDiaog.withMessage(null);
                cDiaog.withBtnLineColor(R.color.bg_line);
                cDiaog.withBtnContentLineColor(R.color.bg_line);
                cDiaog.withButton1Text("取消");
                cDiaog.withButton1Click(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        cDiaog.dismiss();
                    }
                });
                cDiaog.withButton2Text("确定");
                cDiaog.withButton2Click(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String tradepwd = ((EditText) layoutTradePwd.findViewById(R.id.trade_password))
                                .getText().toString().trim();//交易密码
                        if (TextUtils.isEmpty(tradepwd)) {
                            Utils.makeToast(Withdraw_money.this, "请输入交易密码");
                        } else {
                            if (pDialog ==null) {
                                pDialog = new ProgressDialog(Withdraw_money.this);
                            }
                            pDialog.show();
                            Withdraw_money_Client.getMoney(handler, withdrawAmount, card.getCard_id(), tradepwd,1);
                            cDiaog.dismiss();
                        }

                    }
                });
                cDiaog.setCancelable(false);
                cDiaog.setCanceledOnTouchOutside(false);
                cDiaog.show();
            }
            break;
     }
    }
    /**
     * Activity被系统杀死时被调用.
     * 例如:屏幕方向改变时,Activity被销毁再重建;当前Activity处于后台,系统资源紧张将其杀死.
     * 另外,当跳转到其他Activity或者按Home键回到主屏时该方法也会被调用,系统是为了保存当前View组件的状态.
     * 在onPause之前被调用.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("capitalBalance", capitalBalance);
        outState.putSerializable("card", card);
        outState.putString("tip", tip);
        outState.putInt("success",success);
        outState.putString("tip2",tip2);
    }


}
