package cgtz.com.cgwallet.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import cgtz.com.cgwallet.R;

/**
 * 存钱或取钱结果页面
 */
public class SaveMoneySuccessActivity extends BaseActivity {
    private TextView saveFigure;
    private TextView calculateTime;
    private Button iKnowButton;
    private ImageView imgProgress;
    private boolean isSaveAt;//是否存钱页面跳转，true是的，false不是

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_money_success);
        isSaveAt = getIntent().getBooleanExtra("isSaveAt",true);//是否存钱页面跳转，true是的，false不是
        if(isSaveAt){
            setTitle("存钱结果");
        }else{
            setTitle("取钱结果");
        }
        initViews();
    }

    private void initViews(){
        saveFigure = (TextView) findViewById(R.id.tv_save_figure);
        calculateTime = (TextView) findViewById(R.id.tv_calculate_time);
        iKnowButton = (Button) findViewById(R.id.button_iknow);
        imgProgress = (ImageView) findViewById(R.id.iv_progress);
        if(isSaveAt){
            imgProgress.setImageResource(R.mipmap.icon_save_progress);
        }else{
            imgProgress.setImageResource(R.mipmap.icon_draw_progress);
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
