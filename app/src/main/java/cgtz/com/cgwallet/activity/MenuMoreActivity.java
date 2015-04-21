package cgtz.com.cgwallet.activity;

import android.os.Bundle;

import cgtz.com.cgwallet.R;

/**
 * 更多页面
 */
public class MenuMoreActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_more);
        setTitle("更多");
        showBack(true);
    }


}
