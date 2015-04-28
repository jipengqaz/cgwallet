package cgtz.com.cgwallet.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import cgtz.com.cgwallet.MApplication;
import cgtz.com.cgwallet.R;

/**
 * 安全中心
 */
public class SafeCenterActivity extends BaseActivity implements View.OnClickListener{

    private RelativeLayout rl_personal_sodoko_unlock_change,Reset_Passwrod;//管理手势,修改交易密码
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
        Reset_Passwrod.setOnClickListener(this);
        rl_personal_sodoko_unlock_change.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()){
            case R.id.rl_personal_sodoko_unlock_change:
                intent = new Intent(SafeCenterActivity.this,OpenSudokoUnlockActivity.class);
                startActivity(intent);
                break;
            case R.id.Reset_Passwrod:
                intent = new Intent(SafeCenterActivity.this,TradePwdActivity.class);
                intent.putExtra("isSetTradePwd",true);
                startActivity(intent);
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
