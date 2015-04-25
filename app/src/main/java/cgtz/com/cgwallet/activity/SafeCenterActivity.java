package cgtz.com.cgwallet.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.umeng.analytics.MobclickAgent;

import cgtz.com.cgwallet.MApplication;
import cgtz.com.cgwallet.R;
import cn.jpush.android.api.JPushInterface;

/**
 * 安全中心
 */
public class SafeCenterActivity extends BaseActivity implements View.OnClickListener{

    private RelativeLayout rl_personal_sodoko_unlock_change;//管理手势
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
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        JPushInterface.onResume(this);
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        JPushInterface.onPause(this);
        MobclickAgent.onPause(this);
    }
}
