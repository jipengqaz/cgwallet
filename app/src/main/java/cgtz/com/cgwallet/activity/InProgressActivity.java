package cgtz.com.cgwallet.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import cgtz.com.cgwallet.R;

/**
 * 处理中页面
 */
public class InProgressActivity extends BaseActivity {
    private boolean isSaveAt;//是否存钱页面跳转，true是的，false不是
    private TextView progressText;
    private TextView progresBanner;
    private Button iKnowButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_progress);
        isSaveAt = getIntent().getBooleanExtra("isSaveAt",true);//是否存钱页面跳转，true是的，false不是
        if(isSaveAt){
            setTitle("存钱");
        }else{
            setTitle("取钱");
        }
        initViews();
    }

    private void initViews(){
        progressText = (TextView) findViewById(R.id.tv_progress);
        progresBanner = (TextView) findViewById(R.id.tv_progress_banner);
        iKnowButton = (Button) findViewById(R.id.button_progress);
        if(isSaveAt){
            progressText.setText("存钱处理中");
        }else{
            progressText.setText("取钱处理中");
        }
    }
}
