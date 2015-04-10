package cgtz.com.cgwallet.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

/**
 * �自定义主activity
 * Created by Administrator on 2015/4/9.
 */
public class BaseActivity extends Activity {
    private static final String TAG = "BaseActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
    }
}
