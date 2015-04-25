package cgtz.com.cgwallet.activity;

import android.os.Bundle;

import cgtz.com.cgwallet.R;

public class SaveMoneyActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_money);
        setTitle("存钱");
        showBack(true);
    }

}
