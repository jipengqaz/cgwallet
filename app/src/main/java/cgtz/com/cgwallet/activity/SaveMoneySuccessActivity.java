package cgtz.com.cgwallet.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import cgtz.com.cgwallet.MApplication;
import cgtz.com.cgwallet.R;

/**
 * 存钱结果页面
 */
public class SaveMoneySuccessActivity extends BaseActivity {
    private TextView saveFigure;
    private TextView calculateTime;
    private TextView lookSaveRecord;//查看存钱记录
    private TextView goToWallet;//查看我的钱包
    private ImageView imgProgress;
    private String startCalculateTime;//开始计算收益时间
    private String saveMoney;//存钱数值

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_money_success);
        MApplication.registActivities(this);
        setTitle("存钱结果");
        startCalculateTime = getIntent().getStringExtra("startCalculateTime");
        saveMoney = getIntent().getStringExtra("saveMoney");
        initViews();
        setListener();
    }

    private void initViews(){
        saveFigure = (TextView) findViewById(R.id.tv_save_figure);
        calculateTime = (TextView) findViewById(R.id.tv_calculate_time);
        imgProgress = (ImageView) findViewById(R.id.iv_progress);
        lookSaveRecord = (TextView) findViewById(R.id.look_save_record);//查看存钱记录
        goToWallet = (TextView) findViewById(R.id.go_to_wallet);//查看我的钱包
        if(!TextUtils.isEmpty(saveMoney)){
            saveFigure.setText(saveMoney);
        }

        if(!TextUtils.isEmpty(startCalculateTime)){
            calculateTime.setText(startCalculateTime);
        }
    }

    /**
     * 添加事件
     */
    private void setListener(){
        //查看存钱记录
        lookSaveRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SaveMoneySuccessActivity.this,E_wallet_record_activity.class));
                MApplication.destroyActivity(MApplication.getActivityByName(SaveMoneyActivity.class.getName()));
                finish();
            }
        });
        //查看我的钱包
        goToWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SaveMoneySuccessActivity.this,MainActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                );
                finish();
            }
        });
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
