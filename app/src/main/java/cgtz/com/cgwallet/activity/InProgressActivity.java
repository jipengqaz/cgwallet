package cgtz.com.cgwallet.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import cgtz.com.cgwallet.MApplication;
import cgtz.com.cgwallet.R;

/**
 * 处理中页面
 */
public class InProgressActivity extends BaseActivity {
    private boolean isSaveAt;//是否存钱页面跳转，true是的，false不是
    private TextView progressText;
    private TextView progresBanner;
    private TextView lookSaveRecord;//查看存钱记录
    private TextView goToWallet;//查看我的钱包

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_progress);
        MApplication.registActivities(this);//存储该activity
        isSaveAt = getIntent().getBooleanExtra("isSaveAt",true);//是否存钱页面跳转，true是的，false不是
        if(isSaveAt){
            setTitle("存钱");
        }else{
            setTitle("取钱");
        }
        initViews();
        setLinstener();
    }

    private void initViews(){
        progressText = (TextView) findViewById(R.id.tv_progress);
        progresBanner = (TextView) findViewById(R.id.tv_progress_banner);
        lookSaveRecord = (TextView) findViewById(R.id.look_save_record);//查看存钱记录
        goToWallet = (TextView) findViewById(R.id.go_to_wallet);//查看我的钱包
        if(isSaveAt){
            progressText.setText("存钱处理中...");
            progresBanner.setText("请稍后查看钱包资金");
        }else{
            progressText.setText("取钱处理中...");
            progresBanner.setText("最快0秒，最晚一个工作日内转出\n敬请关注草根账户可用余额");
        }
    }

    /**
     * 添加事件
     */
    private void setLinstener(){
//查看存钱记录
        lookSaveRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(InProgressActivity.this,E_wallet_record_activity.class));
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
