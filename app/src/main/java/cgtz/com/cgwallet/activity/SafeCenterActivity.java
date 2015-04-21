package cgtz.com.cgwallet.activity;

import android.os.Bundle;

import cgtz.com.cgwallet.R;

/**
 * 安全中心
 */
public class SafeCenterActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safe_center);
        setTitle("安全中心");
        showBack(true);
    }

}
