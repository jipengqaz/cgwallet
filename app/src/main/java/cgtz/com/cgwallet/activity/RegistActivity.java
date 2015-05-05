package cgtz.com.cgwallet.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONObject;

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
import cgtz.com.cgwallet.widget.ProgressDialog;

/**
 * 注册页面
 */
public class RegistActivity extends BaseActivity implements ISplashView, View.OnClickListener{
    private static final String TAG = "RegistActivity";
    private EditText registMobile;
    private EditText securityCode;
    private TextView getSecurityCode;
    private Button registNext;
    private String mobile = "";
    private ProgressDialog progressDialog;
    private SplashPresenter presenter;
    private String mobile_code;//验证码
    private boolean beforeMobile = false;//判断是否为忘记密码标示
    private static final int GET_CODE_TIME = 60;//获取验证码后，60秒后才能获取的handler判断值
    private int CODE_TIME = 60;//读秒  60秒
    private static final String TIME_MSG = "秒后重发";
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
        registNext = (Button) findViewById(R.id.btn_regist_next);
        getSecurityCode.setOnClickListener(this);
        registNext.setOnClickListener(this);
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
                if(editable.toString().trim().length()==11){
                    getSecurityCode.setEnabled(true);
                    getSecurityCode.setBackgroundResource(R.color.main_bg);
                }else{
                    getSecurityCode.setEnabled(false);
                    getSecurityCode.setBackgroundResource(R.color.bg_get_security_code);
                }
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
                if(TextUtils.isEmpty(editable.toString().trim())){
                    registNext.setEnabled(false);
                    registNext.setBackgroundResource(R.drawable.bg_button_no_enabled);
                }else{
                    registNext.setEnabled(true);
                    registNext.setBackgroundResource(R.drawable.bg_button_preed);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.tv_regist_get_security_code://获取验证码
                mobile = registMobile.getText().toString().trim();
                if(TextUtils.isEmpty(mobile)){
                    Utils.makeToast(this,"请输入注册手机号");
                }else{
                    presenter.didFinishLoading(this);
                }
                break;
            case R.id.btn_regist_next://下一步
                mobile  = registMobile.getText().toString().trim();
                mobile_code = securityCode.getText().toString().trim();
                if(TextUtils.isEmpty(mobile)){
                    Utils.makeToast(this,"请输入注册手机号");
                }else if(TextUtils.isEmpty(mobile_code)){
                    Utils.makeToast(this,"请填写验证码");
                }else{
                    startActivity(new Intent(this,RegistNextActivity.class)
                    .putExtra("mobile",mobile)
                    .putExtra("mobile_code", mobile_code)
                    .putExtra("beforeMobile", beforeMobile));
                }
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
                    case Constants.WHAT_GET_SECURITY_CODE:
                        boolean flag = Utils.filtrateCode(RegistActivity.this,jsonBean);
                        if(flag && code == Constants.OPERATION_FAIL){//数据交互失败
                            mHandler.removeMessages(GET_CODE_TIME);
                            CODE_TIME = 60;
                            getSecurityCode.setText(getResources().getString(R.string.hint_regist_get_security_code));
                            getSecurityCode.setBackgroundResource(R.color.main_bg);
                            getSecurityCode.setEnabled(true);
                            Utils.makeToast(RegistActivity.this, errorMsg);
                        }else if(flag && code == Constants.OPERATION_SUCCESS){//数据交互成功
                            JSONObject jsonObject = new JSONObject(jsonBean.getJsonString());
                            if(Constants.IS_TEST){
//                                mobile_code = jsonObject.optString("mobile_code");
                                Utils.makeToast(RegistActivity.this,jsonObject.optString("mobile_code"));
                            }
                        }else if(flag && code == 2){
                            mHandler.removeMessages(GET_CODE_TIME);
                            CODE_TIME = 60;
                            getSecurityCode.setText(getResources().getString(R.string.hint_regist_get_security_code));
                            getSecurityCode.setBackgroundResource(R.color.main_bg);
                            getSecurityCode.setEnabled(true);
                            Utils.makeToast(RegistActivity.this, "手机号已注册，请直接输入");
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
        getSecurityCode.setText(CODE_TIME + TIME_MSG);
//        getSecurityCode.setTextColor(getResources().getColor(R.color.white));
        getSecurityCode.setEnabled(false);
        mHandler.sendEmptyMessageDelayed(GET_CODE_TIME,1000);
        HashMap<String,String> params = new HashMap();
        params.put("mobile", mobile);
        CustomTask task = new CustomTask(mHandler, Constants.WHAT_GET_SECURITY_CODE
                ,beforeMobile?Constants.URL_FORGET_PWD_CODE:Constants.URL_GET_SECURITY_CODE,
                true,params,true);
        task.execute();
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
