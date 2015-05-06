package cgtz.com.cgwallet.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import cgtz.com.cgwallet.MApplication;
import cgtz.com.cgwallet.R;
import cgtz.com.cgwallet.utils.Utils;

/**
 * 处理中页面
 */
public class InProgressActivity extends BaseActivity {
    private boolean isSaveAt;//是否存钱页面跳转，true是的，false不是
    private TextView progressText;
    private TextView progresBanner;
    private TextView lookSaveRecord;//查看存钱记录
    private TextView goToWallet;//查看我的钱包
    private ImageView walletSlogan;//草根钱包介绍
    private int choose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_progress);
        MApplication.registActivities(this);//存储该activity
        isSaveAt = getIntent().getBooleanExtra("isSaveAt",true);//是否存钱页面跳转，true是的，false不是
        if(isSaveAt){
            setTitle("存钱");
            choose = 1;
        }else{
            setTitle("取钱");
            choose = 2;
        }
        initViews();
        setLinstener();
    }

    private void initViews(){
        progressText = (TextView) findViewById(R.id.tv_progress);
        progresBanner = (TextView) findViewById(R.id.tv_progress_banner);
        lookSaveRecord = (TextView) findViewById(R.id.look_save_record);//查看存钱记录
        goToWallet = (TextView) findViewById(R.id.go_to_wallet);//查看我的钱包
        walletSlogan = (ImageView) findViewById(R.id.iv_slogan);//钱包口号
        if(isSaveAt){
            progressText.setText("存钱处理中...");
            progresBanner.setText("请稍后查看钱包资金");
        }else{
            progressText.setText("取钱处理中...");
            lookSaveRecord.setText("查看取钱记录");
            progresBanner.setText("资金将在１－２个工作日内到达您银行卡");
        }
    }

    /**
     * 添加事件
     */
    private void setLinstener(){

        Utils.goToSloganIntruduce(this, walletSlogan);
        //查看存钱记录
        lookSaveRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(InProgressActivity.this,E_wallet_record_activity.class).putExtra("choose",choose));
                MApplication.destroyActivity(MApplication.getActivityByName(SaveMoneyActivity.class.getName()));
                finish();
            }
        });
        //查看我的钱包
        goToWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(InProgressActivity.this,MainActivity.class)
                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                );
                finish();
            }
        });
    }
}
