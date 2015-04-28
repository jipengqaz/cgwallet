package cgtz.com.cgwallet.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cgtz.com.cgwallet.MApplication;
import cgtz.com.cgwallet.R;
import cgtz.com.cgwallet.utils.Utils;

/**
 * 安全中心
 */
public class SafeCenterActivity extends BaseActivity implements View.OnClickListener{
    private RelativeLayout needNameLayout;
    private RelativeLayout needBankLayout;
    private TextView saveLayout;
    private TextView drawLayout;

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
        needNameLayout = (RelativeLayout) findViewById(R.id.rl_personal_anthen);
        needBankLayout = (RelativeLayout) findViewById(R.id.rl_personal_bankcardbinding);
        rl_personal_sodoko_unlock_change = (RelativeLayout) findViewById(R.id.rl_personal_sodoko_unlock_change);
        Reset_Passwrod = (RelativeLayout) findViewById(R.id.Reset_Passwrod);
        rl_personal_loginpwd_change = (RelativeLayout) findViewById(R.id.rl_personal_loginpwd_change);
        rl_personal_loginpwd_change.setOnClickListener(this);
        Reset_Passwrod.setOnClickListener(this);
        rl_personal_sodoko_unlock_change.setOnClickListener(this);
        needBankLayout.setOnClickListener(this);
        needNameLayout.setOnClickListener(this);
        SaveOrDrawMoney();
    }
    /**
     * 跳转存钱和取钱页面
     */
    private void SaveOrDrawMoney(){
        saveLayout = (TextView) findViewById(R.id.layout_save_money);
        drawLayout = (TextView) findViewById(R.id.layout_draw_money);
        saveLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SafeCenterActivity.this,SaveMoneyActivity.class));
            }
        });
        drawLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SafeCenterActivity.this,SaveMoneyActivity.class));
            }
        });
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()){
            case R.id.rl_personal_sodoko_unlock_change://设置手势
                if(Utils.isLogined()){
            case R.id.rl_personal_anthen://实名认证
                startActivity(new Intent(this, SaveMoneyActivity.class)
                        .putExtra("fromName",true));
                break;
            case R.id.rl_personal_bankcardbinding://绑定银行卡
                startActivity(new Intent(this, SaveMoneyActivity.class)
                        .putExtra("fromBank",true));

                break;
            case R.id.rl_personal_sodoko_unlock_change:
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
