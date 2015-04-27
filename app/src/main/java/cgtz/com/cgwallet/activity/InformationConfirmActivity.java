package cgtz.com.cgwallet.activity;

import android.os.Bundle;

import cgtz.com.cgwallet.R;

/**
 * 信息确认页面
 */
public class InformationConfirmActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information_confirm);
        setTitle("存钱");
        showBack(true);
    }


}
