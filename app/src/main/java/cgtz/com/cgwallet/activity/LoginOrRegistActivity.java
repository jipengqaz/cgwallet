package cgtz.com.cgwallet.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import cgtz.com.cgwallet.R;
import cgtz.com.cgwallet.presenter.SplashPresenter;
import cgtz.com.cgwallet.utility.Constants;
import cgtz.com.cgwallet.utils.Utils;
import cgtz.com.cgwallet.view.ISplashView;
import cgtz.com.cgwallet.widget.ProgressDialog;

/**
 * 登录或注册页面
 */
public class LoginOrRegistActivity extends BaseActivity implements ISplashView,View.OnClickListener{
    private ImageView ivNoPhone;//重新输入手机号登录
    private LinearLayout layoutHavePhone;//已有手机号登录
    private TextView tvLoginPhone;//显示已有的手机号
    private EditText etLoginPhone;//输入登录手机号码
    private EditText etLoginPwd;//输入登录密码
    private TextView tvServicePhone;//显示客服电话
    private Button btnLogin;//登录按钮
    private TextView tvRegistAccount;//注册账户
    private TextView tvForgetPwd;//忘记密码
    private SplashPresenter presenter;
    private ProgressDialog progressDialog;
    private boolean showHavePhone = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("登录");
        showBack(true);
        setRightText("切换账户");
        setRightListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivNoPhone.setVisibility(View.GONE);
                layoutHavePhone.setVisibility(View.VISIBLE);
                showHavePhone = true;
                setRightText("");
            }
        });
        setBackListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(showHavePhone){
                    ivNoPhone.setVisibility(View.VISIBLE);
                    layoutHavePhone.setVisibility(View.GONE);
                    showHavePhone = false;
                }else{
                    finish();
                }
            }
        });
        setContentView(R.layout.activity_login_or_regist);
        presenter = new SplashPresenter(this);
        initViews();
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
    }

    private void setListener(){
        btnLogin.setOnClickListener(this);
        tvRegistAccount.setOnClickListener(this);
        tvForgetPwd.setOnClickListener(this);
    }

    @Override
    public void startProcessBar() {
        if(progressDialog == null){
            progressDialog = new ProgressDialog(LoginOrRegistActivity.this,R.style.loading_dialog);
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

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.login_button_finish://登录

                break;
        }
    }
}
