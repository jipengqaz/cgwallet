package cgtz.com.cgwallet.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cgtz.com.cgwallet.MApplication;
import cgtz.com.cgwallet.R;
import cgtz.com.cgwallet.Service.Code_download_Service;
import cgtz.com.cgwallet.bean.JsonBean;
import cgtz.com.cgwallet.bean.LoginMobileBean;
import cgtz.com.cgwallet.presenter.SplashPresenter;
import cgtz.com.cgwallet.utility.Constants;
import cgtz.com.cgwallet.utils.CustomTask;
import cgtz.com.cgwallet.utils.Ke_Fu_data;
import cgtz.com.cgwallet.utils.LogUtils;
import cgtz.com.cgwallet.utils.MD5Util;
import cgtz.com.cgwallet.utils.Utils;
import cgtz.com.cgwallet.view.ISplashView;
import cgtz.com.cgwallet.widget.LoginPopupwindow;
import cgtz.com.cgwallet.widget.ProgressDialog;

/**
 * 登录或注册页面
 */
public class LoginActivity extends BaseActivity implements ISplashView,View.OnClickListener{
    private static final String TAG = "LoginActivity";
    private EditText etLoginPhone;//输入登录手机号码
    private EditText etLoginPwd;//输入登录密码
    private Button btnLogin;//登录按钮
    private TextView tvRegistAccount;//注册账户
    private TextView tvForgetPwd;//忘记密码
//    private TextView showEditsMobile;//显示输入的手机号码
    private ImageView changeMobile;//手机号输入框后的图标
    private SplashPresenter presenter;
    private ProgressDialog progressDialog;
    private String loginPhone;//登录手机号
    private String loginPwd;//登录密码
    private ImageView showPwd;//是否显示密码
    public String beforeMobile;//之前登录过的手机号
    private boolean isChecked = false;//设置是否显示密码
    private ImageView empty;//清空数据
    public boolean isStarMobile = false;//用于判断是否为星号手机号
    public boolean isEditMobileFocus = false;//判断手机输入框是否获取到焦点,默认没有获取到
    private List<LoginMobileBean> listBeans;//登录过的手机号集合
    private LinearLayout mobileParent;//输入框外层布局
    private LoginPopupwindow loginPop;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == 10){//用于用户注册时    已注册账号  直接返回该页面  并输入手机号
            etLoginPhone.setVisibility(View.VISIBLE);
            etLoginPwd.setText("");
            etLoginPhone.setText(data.getStringExtra("mobile"));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("登录");
        showBack(true);
        setBackListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backMain();
            }
        });
        MApplication.registActivities(this);//存储该activity
        beforeMobile = Utils.getUserPhone(this);
        Utils.loginExit(this);
        setContentView(R.layout.activity_login_or_regist);
        presenter = new SplashPresenter(this);
        initViews();
        setListener();
    }

    private void initViews(){
        etLoginPhone = (EditText) findViewById(R.id.login_edit_phone);//输入登录手机号码
        etLoginPwd = (EditText) findViewById(R.id.login_edit_pwd);//输入登录密码
        btnLogin = (Button) findViewById(R.id.login_button_finish);//登录按钮
        tvRegistAccount = (TextView) findViewById(R.id.tv_regist_account);//注册账户
        tvForgetPwd = (TextView) findViewById(R.id.tv_forget_pwd);//忘记密码
        changeMobile = (ImageView) findViewById(R.id.empty_change_mobile);//手机号输入框后面的图标
        mobileParent = (LinearLayout) findViewById(R.id.phone_layout);//输入框外层布局
//        showEditsMobile = (TextView) findViewById(R.id.show_edits_mobile);//显示输入的手机号
        showPwd = (ImageView) findViewById(R.id.iv_show_pwd);//是否显示密码
        empty = (ImageView) findViewById(R.id.empty);//清空密码输入框数据
        fillViews();
    }

    /**
     * 填充页面内容
     */
    private void fillViews(){
        if(!TextUtils.isEmpty(beforeMobile)){
            isStarMobile = true;
            etLoginPhone.setText(Utils.getHasStarsMobile(beforeMobile));
            etLoginPhone.setSelection(etLoginPhone.getText().toString().trim().length());
        }
        listBeans = DataSupport.order("id desc").limit(3).find(LoginMobileBean.class);
    }

    private void setListener(){
        Utils.closeInputMethod(this);//关闭输入键盘
        btnLogin.setOnClickListener(this);
        tvRegistAccount.setOnClickListener(this);
        tvForgetPwd.setOnClickListener(this);
        showPwd.setOnClickListener(this);
        empty.setOnClickListener(this);
        changeMobile.setOnClickListener(this);
        etLoginPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String str = s.toString();
                if (str.length() > 0) {
                    //按钮变为可点击
                    empty.setVisibility(View.VISIBLE);
                } else {
                    empty.setVisibility(View.GONE);
                }
                if(str.length()>=6){
                    btnLogin.setTextColor(getResources().getColor(R.color.login_btn_can_click_text));
                    btnLogin.setEnabled(true);
                }else{
                    btnLogin.setTextColor(getResources().getColor(R.color.login_btn_text));
                    btnLogin.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        etLoginPhone.setOnFocusChangeListener(new View.OnFocusChangeListener() {//监听焦点事件
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    //如果手机号输入框获取到焦点
                    isEditMobileFocus = true;
                    changeMobile.setImageResource(R.mipmap.icon_empty);
                } else {
                    //手机号输入框没有获取到焦点
                    isEditMobileFocus = false;
                    changeMobile.setImageResource(R.mipmap.icon_change_mobile);
                }
            }
        });

        etLoginPhone.addTextChangedListener(new TextWatcher() {//监听输入事件
            private int lastlen = 0;
            private StringBuilder sb = null;

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i4, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                LogUtils.d(TAG, "手机号改变");
                isStarMobile = false;
                if (TextUtils.isEmpty(etLoginPhone.getText().toString().trim())) {
//                    showEditsMobile.setVisibility(View.GONE);
//                    showEditsMobile.setText("");
                } else {
                    sb = new StringBuilder();
                    String str = etLoginPhone.getText().toString();
                    sb.append(str);
                    int len = str.length();
                    if (len >= lastlen) {
                        lastlen = len;
                        int j = 0;
                        for (int i = 0; i < len - 1; i++) {
                            if (i == 2 || i == 6) {
                                j++;
                                sb.insert(i + j, " ");
                            }
                        }
                    } else {
                        lastlen = len;
                        int j = 0;
                        if (len == 3) {

                        } else {
                            for (int i = 0; i < len - 1; i++) {
                                if (i == 2 || i == 6) {
                                    j++;
                                    sb.insert(i + j, " ");
                                }
                            }
                        }

                    }
//                    showEditsMobile.setText(sb.toString());
//                    showEditsMobile.setVisibility(View.VISIBLE);
                }
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
            case R.id.empty://清空密码输入框数据
                etLoginPwd.setText("");
                break;
            case R.id.iv_show_pwd://是否显示密码
//                registPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                if(isChecked){//判断现在密码显示状态，正在可见状态
                    //变为不可见
                    showPwd.setImageResource(R.mipmap.icon_regist_no_show_password);
                    etLoginPwd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    etLoginPwd.setSelection(etLoginPwd.getText().toString().length());
                    isChecked = false;
                }else{//正在不可见状态
                    //变为可见
                    showPwd.setImageResource(R.mipmap.icon_regist_show_password);
                    etLoginPwd.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    etLoginPwd.setSelection(etLoginPwd.getText().toString().length());
                    isChecked = true;
                }
                break;
            case R.id.tv_regist_account://注册用户
                startActivityForResult(new Intent(LoginActivity.this,RegistActivity.class),100);
                break;
            case R.id.login_button_finish://登录
//                if(!isStarMobile){//判断是否输入手机号
//                    //输入手机号
//                    loginPhone = etLoginPhone.getText().toString();
//                }else{
//                    //隐藏了就使用之前登录过的手机号
//                    loginPhone = beforeMobile;
//                }
                loginPhone = etLoginPhone.getText().toString();
                if(loginPhone.lastIndexOf("*") != -1){
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
//                if(etLoginPhone.getVisibility() == View.VISIBLE){//判断手机号填写控件是否隐藏
//                    //为隐藏就使用输入的手机号
//                    loginPhone = etLoginPhone.getText().toString();
//                }else{
//                    //隐藏了就使用之前登录过的手机号
//                    loginPhone = beforeMobile;
//                }
                loginPhone = etLoginPhone.getText().toString();
                if(loginPhone.lastIndexOf("*") != -1){
                    loginPhone = beforeMobile;
                }
                startActivity(new Intent(LoginActivity.this,RegistActivity.class)
                            .putExtra("beforeMobile",loginPhone));
                break;
            case R.id.empty_change_mobile://是否显示登录过的手机号
                if(isEditMobileFocus){
                    etLoginPhone.setText("");//删除手机号输入框中的内容
                }else{
                    //显示登录过的手机号列表
                    if(listBeans != null && listBeans.size() > 0){
                        loginPop = new LoginPopupwindow(this,mobileParent,etLoginPhone,listBeans);
                        loginPop.showPop();
                    }

                }
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
                            if(Utils.getisLockPassWord(LoginActivity.this,mobile)==0){//判断该账号是否是第一次登录该手机
                                Utils.saveisLockPassWord(LoginActivity.this,mobile,1);
                            }
                            startService(new Intent(LoginActivity.this, Code_download_Service.class)
                                    .putExtra("userId",userId).putExtra("token",token));//开启获取分享数据的服务
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

    /**
     * 用于关闭处理main以外的activity
     */
    private void backMain(){
        MApplication.setGoLogin(false);
        MApplication.finishAllActivitys(MainActivity.class.getName());//关掉除了主页面以外的所有页面
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            backMain();
        }
        return super.onKeyDown(keyCode, event);
    }
}
