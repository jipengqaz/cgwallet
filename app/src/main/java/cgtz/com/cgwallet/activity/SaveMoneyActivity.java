package cgtz.com.cgwallet.activity;

import android.os.Bundle;

import cgtz.com.cgwallet.R;

/**
 * 存钱页面
 */
public class SaveMoneyActivity extends BaseActivity {
    private static final String TAG = "SaveMoneyActivity";
    private String assets;//账户余额
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_money);
        setTitle("存钱");
        showBack(true);

    }

}
