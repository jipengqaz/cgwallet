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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.HashMap;

import cgtz.com.cgwallet.MApplication;
import cgtz.com.cgwallet.R;
import cgtz.com.cgwallet.bean.JsonBean;
import cgtz.com.cgwallet.presenter.SplashPresenter;
import cgtz.com.cgwallet.utility.Constants;
import cgtz.com.cgwallet.utils.CustomTask;
import cgtz.com.cgwallet.utils.Ke_Fu_data;
import cgtz.com.cgwallet.utils.LogUtils;
import cgtz.com.cgwallet.utils.MD5Util;
import cgtz.com.cgwallet.utils.Utils;
import cgtz.com.cgwallet.view.ISplashView;
import cgtz.com.cgwallet.widget.ProgressDialog;

/**
 * 登录或注册页面
 */
public class LoginActivity extends BaseActivity implements ISplashView,View.OnClickListener{
    private static final String TAG = "LoginActivity";
    private ImageView ivNoPhone;//重新输入手机号登录
    private LinearLayout layoutHavePhone;//已有手机号登录
    private TextView tvLoginPhone;//显示已有的手机号
    private EditText etLoginPhone;//输入登录手机号码
    private EditText etLoginPwd;//输入登录密码
    private TextView tvServicePhone;//显示客服电话
    private Button btnLogin;//登录按钮
    private TextView tvRegistAccount;//注册账户
    private TextView tvForgetPwd;//忘记密码
    private TextView showEditsMobile;//显示输入的手机号码
    private SplashPresenter presenter;
    private ProgressDialog progressDialog;
    private boolean showHavePhone = false;
    private String loginPhone;//登录手机号
    private String loginPwd;//登录密码
    private String beforeMobile;//之前登录过的手机号

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showBack(true);
        MApplication.registActivities(this);//存储该activity
        beforeMobile = Utils.getUserPhone(this);
        if(TextUtils.isEmpty(beforeMobile)){
            setRightText(null);
        }else{
            setRightText("切换账户");
        }

        setRightListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivNoPhone.setVisibility(View.VISIBLE);
                layoutHavePhone.setVisibility(View.GONE);
                etLoginPhone.setVisibility(View.VISIBLE);
                showHavePhone = true;//重新填写手机号
                setRightText(null);//重新填写手机号
                etLoginPwd.setText("");
            }
        });
        setBackListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (showHavePhone) {
                    changeLoginLayout();
                    setRightText("切换账户");
                    showHavePhone = false;
                } else {
                    MApplication.setGoLogin(false);
                    finish();
                }
            }
        });
        setContentView(R.layout.activity_login_or_regist);
        presenter = new SplashPresenter(this);
        initViews();
        setListener();
    }

    private void initViews(){
        ivNoPhone = (ImageView) findViewById(R.id.iv_no_phone);//重新输入手机号登录
        layoutHavePhone = (LinearLayout) findViewById(R.id.layout_have_phone);//已有手机号登录
        tvLoginPhone = (TextView) findViewById(R.id.tv_login_phone);//显示已有的手机号
        etLoginPhone = (EditText) findViewById(R.id.login_edit_phone);//输入登录手机号码
        etLoginPwd = (EditText) findViewById(R.id.login_edit_pwd);//输入登录密码
        tvServicePhone = (TextView) findViewById(R.id.tv_show_service_phone);//显示客服电话
        btnLogin = (Button) findViewById(R.id.login_button_finish);//登录按钮
        tvRegistAccount = (TextView) findViewById(R.id.tv_regist_account);//注册账户
        tvForgetPwd = (TextView) findViewById(R.id.tv_forget_pwd);//忘记密码
        showEditsMobile = (TextView) findViewById(R.id.show_edits_mobile);//显示输入的手机号
        tvServicePhone.setText(Ke_Fu_data.getPhone(this));
        changeLoginLayout();
    }

    /**
     * 根据上次登录的手机号，改变布局
     */
    private void changeLoginLayout(){
        if(TextUtils.isEmpty(beforeMobile)){//未登录过
            setRightText(null);
            ivNoPhone.setVisibility(View.VISIBLE);
            layoutHavePhone.setVisibility(View.GONE);
            etLoginPhone.setVisibility(View.VISIBLE);
        }else{//登录过，已有手机号
            ivNoPhone.setVisibility(View.GONE);
            layoutHavePhone.setVisibility(View.VISIBLE);
            etLoginPhone.setVisibility(View.GONE);
            tvLoginPhone.setText(Utils.getHasStarsMobile(beforeMobile));
        }
    }

    private void setListener(){
        Utils.closeInputMethod(this);//关闭输入键盘
        btnLogin.setOnClickListener(this);
        tvRegistAccount.setOnClickListener(this);
        tvForgetPwd.setOnClickListener(this);
        etLoginPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(TextUtils.isEmpty(charSequence.toString().trim())){
                    showEditsMobile.setVisibility(View.GONE);
                    showEditsMobile.setText("");
                }else{
                    showEditsMobile.setVisibility(View.VISIBLE);
                    showEditsMobile.setText(charSequence.toString().trim());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @Override
    public void startProcessBar() {
        if(progressDialog == null){
            progressDialog = new ProgressDialog(LoginActivity.this,R.style.loading_dialog);
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
        HashMap<String,String> params = new HashMap<>();
        params.put("username",loginPhone);
        params.put("password", MD5Util.md5(loginPwd));
        CustomTask task = new CustomTask(mHandler,Constants.WHAT_LOGIN,Constants.URL_LOGIN,true,params,true);
        task.execute();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.tv_regist_account://注册用户
                startActivity(new Intent(LoginActivity.this,RegistActivity.class));
                break;
            case R.id.login_button_finish://登录
                if(etLoginPhone.getVisibility() == View.VISIBLE){//判断手机号填写控件是否隐藏
                    //为隐藏就使用输入的手机号
                    loginPhone = etLoginPhone.getText().toString();
                }else{
                    //隐藏了就使用之前登录过的手机号
                    loginPhone = beforeMobile;
                }
                loginPwd = etLoginPwd.getText().toString();
                if(TextUtils.isEmpty(loginPhone)){
                    Utils.makeToast(LoginActivity.this,getResources().getString(R.string.error_need_phone));
                }else if(TextUtils.isEmpty(loginPwd)){
                    Utils.makeToast(LoginActivity.this,getResources().getString(R.string.error_need_pwd));
                }else{
                    presenter.didFinishLoading(LoginActivity.this);
                }
                break;
            case R.id.tv_forget_pwd://忘记密码
                if(etLoginPhone.getVisibility() == View.VISIBLE){//判断手机号填写控件是否隐藏
                    //为隐藏就使用输入的手机号
                    loginPhone = etLoginPhone.getText().toString();
                }else{
                    //隐藏了就使用之前登录过的手机号
                    loginPhone = beforeMobile;
                }
                startActivity(new Intent(LoginActivity.this,RegistActivity.class)
                            .putExtra("beforeMobile",loginPhone));
                break;
        }
    }

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            try{
                JsonBean jsonBean = (JsonBean) msg.obj;
                LogUtils.i(TAG,"登录返回内容："+jsonBean.getJsonString());
                int code = jsonBean.getCode();
                String errorMsg = jsonBean.getError_msg();
                if(code == Constants.DATA_EVENT){
                    Utils.makeToast(LoginActivity.this,Constants.ERROR_MSG_CODE+code);
                    return;
                }
                int action = msg.what;
                switch (action){
                    case Constants.WHAT_LOGIN:
                        boolean flag = Utils.filtrateCode(LoginActivity.this,jsonBean);
                        if(flag && code == Constants.OPERATION_FAIL){//数据交互失败
                            Utils.makeToast(LoginActivity.this, errorMsg);
                        }else if(flag && code == Constants.OPERATION_SUCCESS){//数据交互成功
                            JSONObject jsonObject = new JSONObject(jsonBean.getJsonString());
                            JSONObject object = jsonObject.optJSONObject("info");
                            String userId = object.optString("userId");
                            String token = object.optString("token");
                            String mobile = object.optString("mobile");
                            Utils.saveUserId(userId);
                            Utils.saveToken(token);
                            Utils.saveMobile(LoginActivity.this, mobile);
                            Utils.saveLoginPwd(LoginActivity.this, MD5Util.md5(loginPwd));
                            hideProcessBar();
                            finish();
                        }
                        hideProcessBar();
                        break;
                }
            }catch (Exception e){
                e.printStackTrace();
                LogUtils.e(TAG,"activity数据异常");
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
