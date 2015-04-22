package cgtz.com.cgwallet.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import cgtz.com.cgwallet.R;
import cgtz.com.cgwallet.view.ISplashView;

/**
 * 登录或注册页面
 */
public class LoginOrRegistActivity extends BaseActivity implements ISplashView{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("登录/注册");
        showBack(true);
        setContentView(R.layout.activity_login_or_regist);
    }

    @Override
    public void startProcessBar() {

    }

    @Override
    public void hideProcessBar() {

    }

    @Override
    public void showNetError() {

    }

    @Override
    public void startNextActivity() {

    }
}
