package cgtz.com.cgwallet.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.HashMap;

import cgtz.com.cgwallet.MApplication;
import cgtz.com.cgwallet.R;
import cgtz.com.cgwallet.Service.Code_download_Service;
import cgtz.com.cgwallet.bean.JsonBean;
import cgtz.com.cgwallet.presenter.SplashPresenter;
import cgtz.com.cgwallet.utility.Constants;
import cgtz.com.cgwallet.utils.CustomTask;
import cgtz.com.cgwallet.utils.LogUtils;
import cgtz.com.cgwallet.utils.MD5Util;
import cgtz.com.cgwallet.utils.Utils;
import cgtz.com.cgwallet.view.ISplashView;
import cgtz.com.cgwallet.widget.ProgressDialog;

/**
 * 注册页面
 */
public class RegistActivity extends BaseActivity implements ISplashView, View.OnClickListener{
    private static final String TAG = "RegistActivity";
    private EditText registMobile;
    private EditText securityCode;
    private TextView getSecurityCode;
    private String mobile = "";
    private ProgressDialog progressDialog;
    private SplashPresenter presenter;
    private String mobile_code;//验证码
    private boolean beforeMobile = false;//判断是否为忘记密码标示
    private static final int GET_CODE_TIME = 60;//获取验证码后，60秒后才能获取的handler判断值
    private int CODE_TIME = 60;//读秒  60秒
    private static final String TIME_MSG = "秒后重发";
    private EditText registPwd;//密码输入框
    private String mobile_pwd;//设置登录密码
    private ImageView showPwd;//是否显示密码
    private Button registBtn;//注册按钮
    private TextView lookProtocol;//查看协议
    private int  is;//用户判断访问那个接口
    private boolean isChecked = false;//设置是否显示密码
    private ImageView empty_code,empty_phone,empty;//清空验证码输入框，清空手机输入框，清空密码输入框

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist);
        mobile = getIntent().getStringExtra("beforeMobile");
        if(TextUtils.isEmpty(mobile)){
            setTitle("注册");
            beforeMobile = false;
        }else{
            beforeMobile = true;
            setTitle("忘记密码");
        }
        MApplication.registActivities(this);//存储该activity
        presenter = new SplashPresenter(this);
        showBack(true);
        initViews();
    }

    private void initViews(){
        registMobile = (EditText) findViewById(R.id.et_regist_mobile);
        securityCode = (EditText) findViewById(R.id.et_regist_security_code);
        getSecurityCode = (TextView) findViewById(R.id.tv_regist_get_security_code);
        registPwd = (EditText) findViewById(R.id.et_regist_pwd);//密码输入框
        showPwd = (ImageView) findViewById(R.id.iv_show_pwd);//是否显示密码
        registBtn = (Button) findViewById(R.id.btn_regist);//注册按钮
        lookProtocol = (TextView) findViewById(R.id.tv_regist_protocol);//查看协议
        empty_code = (ImageView) findViewById(R.id.empty_code);//清空验证码输入框
        empty_phone = (ImageView) findViewById(R.id.empty_phone);//清空手机输入框
        empty = (ImageView) findViewById(R.id.empty);//清空密码输入框

        if(beforeMobile){//如果是修改密码
            lookProtocol.setVisibility(View.GONE);
            registBtn.setText("修改密码");
        }
        empty_code.setOnClickListener(this);
        empty_phone.setOnClickListener(this);
        empty.setOnClickListener(this);
        showPwd.setOnClickListener(this);
        registBtn.setOnClickListener(this);
        lookProtocol.setOnClickListener(this);


        getSecurityCode.setOnClickListener(this);
        if(beforeMobile){
            registMobile.setText(mobile);
            registMobile.setSelection(mobile.length());
            if(mobile.length()==11){//设置获取验证码按钮可点
                getSecurityCode.setEnabled(true);
                getSecurityCode.setBackgroundResource(R.color.main_bg);
            }else{
                getSecurityCode.setEnabled(false);
                getSecurityCode.setBackgroundResource(R.color.bg_get_security_code);
            }
        }
        Utils.closeInputMethod(this);
        registMobile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.toString().trim().length()>0){//设置是否显示清空按钮
                    empty_phone.setVisibility(View.VISIBLE);
                }else{
                    empty_phone.setVisibility(View.GONE);
                }

                if (editable.toString().trim().length() == 11 ) {
                    mHandler.removeMessages(GET_CODE_TIME);
                    getSecurityCode.setText(getResources().getString(R.string.hint_regist_get_security_code));
                    CODE_TIME = 60;
                    getSecurityCode.setEnabled(true);
                    getSecurityCode.setBackgroundResource(R.color.main_bg);
                } else {
                    getSecurityCode.setEnabled(false);
                    getSecurityCode.setBackgroundResource(R.color.bg_get_security_code);
                }
                setButton();//设置按钮可点
            }
        });
        registPwd.addTextChangedListener(new TextWatcher() {//设置密码
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().trim().length()>0){
                    empty.setVisibility(View.VISIBLE);
                }else{
                    empty.setVisibility(View.GONE);
                }
                setButton();//设置按钮可点
            }
        });
        securityCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.toString().trim().length()>0){
                    empty_code.setVisibility(View.VISIBLE);
                }else{
                    empty_code.setVisibility(View.GONE);
                }
                setButton();//设置按钮可点
            }
        });
    }

    private void setButton(){
        if(securityCode.getText().toString().length() == 6 && registPwd.getText().toString().length()>=6
                && registMobile.getText().toString().length() == 11 ){
            registBtn.setEnabled(true);
            registBtn.setBackgroundResource(R.drawable.bg_button_preed);

        }else{
            registBtn.setEnabled(false);
            registBtn.setBackgroundResource(R.drawable.bg_button_no_enabled);
        }
    }
    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.empty_phone://清空验证码输入框，清空手机输入框，清空密码输入框
                registMobile.setText("");
                break;
            case R.id.empty_code:
                securityCode.setText("");
                break;
            case R.id.empty:
                registPwd.setText("");
                break;
            case R.id.iv_show_pwd://是否显示密码
//                registPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                if(isChecked){//判断现在密码显示状态，正在可见状态
                    //变为不可见
                    showPwd.setImageResource(R.mipmap.icon_regist_no_show_password);
                    registPwd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    registPwd.setSelection(registPwd.getText().toString().length());
                    isChecked = false;
                }else{//正在不可见状态
                    //变为可见
                    showPwd.setImageResource(R.mipmap.icon_regist_show_password);
                    registPwd.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    registPwd.setSelection(registPwd.getText().toString().length());
                    isChecked = true;
                }
                break;
            case R.id.tv_regist_get_security_code://获取验证码
                mobile = registMobile.getText().toString().trim();
                if(TextUtils.isEmpty(mobile)){
                    Utils.makeToast(this,"请输入注册手机号");
                }else{
                    is =1;
                    presenter.didFinishLoading(this);
                }
                break;
//            case R.id.btn_regist_next://下一步
//                mobile  = registMobile.getText().toString().trim();
//                mobile_code = securityCode.getText().toString().trim();
//                if(TextUtils.isEmpty(mobile)){
//                    Utils.makeToast(this,"请输入注册手机号");
//                }else if(TextUtils.isEmpty(mobile_code)){
//                    Utils.makeToast(this,"请填写验证码");
//                }else{
//                    startActivity(new Intent(this,RegistNextActivity.class)
//                    .putExtra("mobile",mobile)
//                    .putExtra("mobile_code", mobile_code)
//                    .putExtra("beforeMobile", beforeMobile));
//                }
//                break;
            case R.id.btn_regist://注册按钮
                mobile  = registMobile.getText().toString().trim();
                mobile_code = securityCode.getText().toString().trim();
                mobile_pwd = registPwd.getText().toString().trim();
                if(TextUtils.isEmpty(mobile)){
                    Utils.makeToast(this,"请输入注册手机号");
                }else if(TextUtils.isEmpty(mobile_code)){
                    Utils.makeToast(this,"请填写验证码");
                }else if(TextUtils.isEmpty(mobile_pwd)){
                    Utils.makeToast(this,"请设置登录密码");
                }else if(mobile_pwd.length() >20 || mobile_pwd.length() < 6){
                    Utils.makeToast(this,"密码位数不正确");
                }else{
                    is = 2;
                    presenter.didFinishLoading(this);
                }
                break;
            case R.id.tv_regist_protocol://查看草根协议
                startActivity(new Intent(this, WebViewActivity.class)
                        .putExtra("url", Constants.URL_CG_WALLET_PROTOCOL)
                        .putExtra("title", "草根钱包协议"));
                break;
        }
    }


    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            try{
                int action = msg.what;
                if(action == GET_CODE_TIME){
                    if(CODE_TIME == 0){
                        getSecurityCode.setText(getResources().getString(R.string.hint_regist_get_security_code));
                        getSecurityCode.setBackgroundResource(R.color.main_bg);
                        getSecurityCode.setEnabled(true);
                        CODE_TIME = 60;
                    }else{
                        --CODE_TIME;
                        LogUtils.i(TAG, "时间time：" + CODE_TIME);
                        getSecurityCode.setBackgroundResource(R.color.bg_get_security_code);
                        getSecurityCode.setText(CODE_TIME + TIME_MSG);
                        getSecurityCode.setEnabled(false);
                        mHandler.sendEmptyMessageDelayed(GET_CODE_TIME, 1000);
                    }
                    return;
                }
                JsonBean jsonBean = (JsonBean) msg.obj;
                int code = jsonBean.getCode();
                String errorMsg = jsonBean.getError_msg();
                if(code == Constants.DATA_EVENT){
                    hideProcessBar();
                    Utils.makeToast(RegistActivity.this,Constants.ERROR_MSG_CODE+code);
                    return;
                }
                switch (action){
                    case Constants.WHAT_GET_SECURITY_CODE://获取验证码
                        boolean flag = Utils.filtrateCode(RegistActivity.this,jsonBean);
                        if(flag && code == Constants.OPERATION_FAIL){//数据交互失败
                            Utils.makeToast(RegistActivity.this, errorMsg);
                        }else if(flag && code == Constants.OPERATION_SUCCESS){//数据交互成功
                            JSONObject jsonObject = new JSONObject(jsonBean.getJsonString());
                            getSecurityCode.setText(CODE_TIME + TIME_MSG);
                            getSecurityCode.setEnabled(false);
                            mHandler.sendEmptyMessageDelayed(GET_CODE_TIME, 1000);
                            if(Constants.IS_TEST){
//                                mobile_code = jsonObject.optString("mobile_code");
                                Utils.makeToast(RegistActivity.this,jsonObject.optString("mobile_code"));
                            }
                        }else if(flag && code == 2){
                            Utils.makeToast(RegistActivity.this, "手机号已注册，请直接登录");
                            setResult(10,getIntent().putExtra("mobile",registMobile.getText().toString().trim()));
                            finish();
                        }
                        hideProcessBar();
                        break;
                    case Constants.WHAT_REGISTER://注册结果
                        boolean flag1 = Utils.filtrateCode(RegistActivity.this,jsonBean);
                        if(flag1 && code == Constants.OPERATION_FAIL){//数据交互失败
                            Utils.makeToast(RegistActivity.this, errorMsg);
                        }else if(flag1 && code == Constants.OPERATION_SUCCESS){//数据交互成功
                            if(beforeMobile){
                                Utils.makeToast(RegistActivity.this, errorMsg);
                                Utils.saveMobile(RegistActivity.this, mobile);
                                startActivity(new Intent(RegistActivity.this, LoginActivity.class)
                                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                            }else{
                                JSONObject jsonObject = new JSONObject(jsonBean.getJsonString());
                                JSONObject info = jsonObject.getJSONObject("info");
                                String userId = info.optString("userId");
                                String token = info.optString("token");
                                Utils.saveMobile(RegistActivity.this,mobile);
                                Utils.saveLoginPwd(RegistActivity.this, MD5Util.md5(mobile_pwd));
                                Utils.saveUserId(userId);
                                Utils.saveToken(token);
                                if(Utils.getisLockPassWord(RegistActivity.this,mobile)==0){//判断该账号是否是第一次登录该手机
                                    Utils.saveisLockPassWord(RegistActivity.this,mobile,1);
                                }
                                startService(new Intent(RegistActivity.this, Code_download_Service.class));//开启获取分享数据的服务
                                startActivity(new Intent(RegistActivity.this, MainActivity.class)
                                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                            }
                        }
                        hideProcessBar();
                        break;
                }
            }catch (Exception e){
                e.printStackTrace();
                hideProcessBar();
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
        HashMap<String,String> params;
        CustomTask task;
        switch (is){
            case 1:
            //获取验证码
//            getSecurityCode.setText(CODE_TIME + TIME_MSG);
////        getSecurityCode.setTextColor(getResources().getColor(R.color.white));
//            getSecurityCode.setEnabled(false);
//            mHandler.sendEmptyMessageDelayed(GET_CODE_TIME, 1000);
            params = new HashMap();
            params.put("mobile", mobile);
            task = new CustomTask(mHandler, Constants.WHAT_GET_SECURITY_CODE
                    , beforeMobile ? Constants.URL_FORGET_PWD_CODE : Constants.URL_GET_SECURITY_CODE,
                    true, params, true);
            task.execute();
                break;
            case 2:
            //注册
            params = new HashMap();
            params.put("mobile", mobile);
            params.put("verifyCode", mobile_code);
            params.put("password", mobile_pwd);
            params.put("register_method", "9");
                task = new CustomTask(mHandler, Constants.WHAT_REGISTER
                    ,beforeMobile?Constants.URL_PASSWORD:Constants.URL_REGISTER,true,params,true);
            task.execute();
            break;
            case 3://修改密码

                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeMessages(GET_CODE_TIME);
    }
    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
