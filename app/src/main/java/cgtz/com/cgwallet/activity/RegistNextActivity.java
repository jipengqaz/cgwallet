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
import cgtz.com.cgwallet.bean.JsonBean;
import cgtz.com.cgwallet.presenter.SplashPresenter;
import cgtz.com.cgwallet.utility.Constants;
import cgtz.com.cgwallet.utils.CustomTask;
import cgtz.com.cgwallet.utils.MD5Util;
import cgtz.com.cgwallet.utils.Utils;
import cgtz.com.cgwallet.view.ISplashView;
import cgtz.com.cgwallet.widget.ProgressDialog;

/**
 * 注册输入密码页
 */
public class RegistNextActivity extends BaseActivity implements ISplashView,View.OnClickListener {
    private EditText registPwd;//密码输入框
    private ImageView showPwd;//是否显示密码
    private Button registBtn;//注册按钮
    private TextView lookProtocol;//查看协议
    private String mobile;//手机号
    private String mobile_code;//验证码
    private String mobile_pwd;//设置登录密码
    private ProgressDialog progressDialog;
    private SplashPresenter presenter;
    private boolean isChecked = false;//设置是否显示密码
    private boolean beforeMobile = false;//判断是否忘记密码的操作标示

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist_next);
        setTitle("注册");
        showBack(true);
        MApplication.registActivities(this);
        presenter = new SplashPresenter(this);
        if(savedInstanceState == null){
            mobile = getIntent().getStringExtra("mobile");
            mobile_code = getIntent().getStringExtra("mobile_code");
            beforeMobile = getIntent().getBooleanExtra("beforeMobile",false);
        }
        initViews();
    }

    private void initViews(){
        registPwd = (EditText) findViewById(R.id.et_regist_pwd);//密码输入框
        showPwd = (ImageView) findViewById(R.id.iv_show_pwd);//是否显示密码
        registBtn = (Button) findViewById(R.id.btn_regist);//注册按钮
        lookProtocol = (TextView) findViewById(R.id.tv_regist_protocol);//查看协议
        showPwd.setOnClickListener(this);
        registBtn.setOnClickListener(this);
        lookProtocol.setOnClickListener(this);
        Utils.closeInputMethod(this);//关闭输入键盘

        registPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(TextUtils.isEmpty(editable.toString().trim())){
                    registBtn.setEnabled(false);
                    registBtn.setBackgroundResource(R.drawable.bg_button_no_enabled);
                }else{
                    registBtn.setEnabled(true);
                    registBtn.setBackgroundColor(getResources().getColor(R.color.button_text_can_click));
                }
            }
        });
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
            HashMap<String,String> params = new HashMap();
            params.put("mobile", mobile);
            params.put("verifyCode", mobile_code);
            params.put("password", mobile_pwd);
            params.put("register_method", "9");
            CustomTask task = new CustomTask(mHandler, Constants.WHAT_REGISTER
                    ,beforeMobile?Constants.URL_PASSWORD:Constants.URL_REGISTER,true,params,true);
            task.execute();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.iv_show_pwd://是否显示密码
//                registPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                if(isChecked){//判断现在密码显示状态，正在可见状态
                    //变为不可见
                    showPwd.setImageResource(R.mipmap.icon_regist_no_show_pwd);
                    registPwd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    registPwd.setSelection(registPwd.getText().toString().length());
                    isChecked = false;
                }else{//正在不可见状态
                    //变为可见
                    showPwd.setImageResource(R.mipmap.icon_regist_show_pwd);
                    registPwd.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    registPwd.setSelection(registPwd.getText().toString().length());
                    isChecked = true;
                }
                break;
            case R.id.btn_regist://注册按钮
                mobile_pwd = registPwd.getText().toString().trim();
                if(TextUtils.isEmpty(mobile) || TextUtils.isEmpty(mobile_code)){
                    Utils.makeToast(this,"提交数据错误");
                }else if(TextUtils.isEmpty(mobile_pwd)){
                    Utils.makeToast(this,"请设置登录密码");
                }else if(mobile_pwd.length() >20 || mobile_pwd.length() < 6){
                    Utils.makeToast(this,"密码位数不正确");
                }else{
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
                JsonBean jsonBean = (JsonBean) msg.obj;
                int code = jsonBean.getCode();
                String errorMsg = jsonBean.getError_msg();
                if(code == Constants.DATA_EVENT){
                    hideProcessBar();
                    Utils.makeToast(RegistNextActivity.this,Constants.ERROR_MSG_CODE+code);
                    return;
                }
                int action = msg.what;
                switch (action){
                    case Constants.WHAT_REGISTER://注册结果
                        boolean flag = Utils.filtrateCode(RegistNextActivity.this,jsonBean);
                        if(flag && code == Constants.OPERATION_FAIL){//数据交互失败
                            Utils.makeToast(RegistNextActivity.this, errorMsg);
                        }else if(flag && code == Constants.OPERATION_SUCCESS){//数据交互成功
                            if(beforeMobile){
                                Utils.makeToast(RegistNextActivity.this, errorMsg);
                                Utils.saveMobile(RegistNextActivity.this, mobile);
                                startActivity(new Intent(RegistNextActivity.this, LoginActivity.class)
                                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                            }else{
                                JSONObject jsonObject = new JSONObject(jsonBean.getJsonString());
                                JSONObject info = jsonObject.getJSONObject("info");
                                String userId = info.optString("userId");
                                String token = info.optString("token");
                                Utils.saveMobile(RegistNextActivity.this,mobile);
                                Utils.saveLoginPwd(RegistNextActivity.this,MD5Util.md5(mobile_pwd));
                                Utils.saveUserId(userId);
                                Utils.saveToken(token);
                                startActivity(new Intent(RegistNextActivity.this,MainActivity.class)
                                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                            }
                        }
                        hideProcessBar();
                        break;
                }
            }catch (Exception e){
                e.printStackTrace();
                hideProcessBar();;
            }
        }
    };
    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
