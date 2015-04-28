package cgtz.com.cgwallet.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import cgtz.com.cgwallet.MApplication;
import cgtz.com.cgwallet.R;
import cgtz.com.cgwallet.utils.Utils;

/**
 * 安全中心
 */
public class SafeCenterActivity extends BaseActivity implements View.OnClickListener{

    private RelativeLayout rl_personal_sodoko_unlock_change,Reset_Passwrod,rl_personal_loginpwd_change;//管理手势,修改交易密码,修改密码
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safe_center);
        MApplication.registActivities(this);//存储该activity
        showBack(true);
        setTitle("安全中心");
        showBack(true);
        init();
    }

    private void init() {
        rl_personal_sodoko_unlock_change = (RelativeLayout) findViewById(R.id.rl_personal_sodoko_unlock_change);
        Reset_Passwrod = (RelativeLayout) findViewById(R.id.Reset_Passwrod);
        rl_personal_loginpwd_change = (RelativeLayout) findViewById(R.id.rl_personal_loginpwd_change);
        rl_personal_loginpwd_change.setOnClickListener(this);
        Reset_Passwrod.setOnClickListener(this);
        rl_personal_sodoko_unlock_change.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()){
            case R.id.rl_personal_sodoko_unlock_change://设置手势
                if(Utils.isLogined()){
                intent = new Intent(SafeCenterActivity.this,OpenSudokoUnlockActivity.class);
                startActivity(intent);
                }else{
                    startActivity(new Intent(SafeCenterActivity.this,LoginActivity.class));
                }
                break;
            case R.id.Reset_Passwrod://修改交易密码
                if(Utils.isLogined()){
                intent = new Intent(SafeCenterActivity.this,TradePwdActivity.class);
                intent.putExtra("isSetTradePwd",true);
                startActivity(intent);
                }else{
                    startActivity(new Intent(SafeCenterActivity.this,LoginActivity.class));
                }
                break;
            case R.id.rl_personal_loginpwd_change://修改登录密码
                if(Utils.isLogined()){
                intent = new Intent(SafeCenterActivity.this,ChangeLoginPwdActivity.class);
                startActivity(intent);
                }else{
                    startActivity(new Intent(SafeCenterActivity.this,LoginActivity.class));
                }
                break;
        }
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
