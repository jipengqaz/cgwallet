package cgtz.com.cgwallet.activity;

import android.os.Bundle;

import cgtz.com.cgwallet.R;

/**
 * 登录或注册页面
 */
public class LoginOrRegistActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("登录/注册");
        showBack(true);
        setContentView(R.layout.activity_login_or_regist);
    }
}
