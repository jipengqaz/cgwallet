package cgtz.com.cgwallet.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import cgtz.com.cgwallet.MApplication;
import cgtz.com.cgwallet.R;
import cgtz.com.cgwallet.utility.Constants;
import cgtz.com.cgwallet.utils.Ke_Fu_data;
import cgtz.com.cgwallet.utils.Utils;
import cgtz.com.cgwallet.widget.CustomEffectsDialog;

/**
 * 更多页面
 */
public class MenuMoreActivity extends BaseActivity implements View.OnClickListener{
    private RelativeLayout aboutMine;//关于我们
    private RelativeLayout feedBack;//意见反馈
    private RelativeLayout contactMine;//联系我们
    private CustomEffectsDialog ceffectDialog;
    private TextView version;//版本号
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_more);
        MApplication.registActivities(this);//存储该activity
        setTitle("更多");
        showBack(true);
        initViews();
        setListener();
    }

    private void initViews(){
        aboutMine = (RelativeLayout) findViewById(R.id.about_mine);
        feedBack = (RelativeLayout) findViewById(R.id.feed_back);
        contactMine = (RelativeLayout) findViewById(R.id.contact_mine);
        version = (TextView) findViewById(R.id.version);
        Utils.safeCopyWrite(this);//设置安全文案
    }

    private void setListener(){
        aboutMine.setOnClickListener(this);
        feedBack.setOnClickListener(this);
        contactMine.setOnClickListener(this);
        version.setText("当前版本: v " + Utils.getVersion(this));
        version.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.about_mine://关于我们
                startActivity(new Intent(this, WebViewActivity.class)
                        .putExtra("url", Constants.URL_WALLET_SLOGAN)
                        .putExtra("title", "草根钱包介绍"));
                break;
            case R.id.feed_back://意见反馈
                if(Utils.isLogined()){
                    Utils.makeToast(this,Constants.NEED_LOGIN);
                    startActivity(new Intent(MenuMoreActivity.this, LoginActivity.class));
                }else{
                    startActivity(new Intent(MenuMoreActivity.this,FeedBackActivity.class));
                }
                break;
            case R.id.contact_mine://联系我们
                if(ceffectDialog == null){
                    ceffectDialog = CustomEffectsDialog.getInstans(MenuMoreActivity.this);
                }else{
                    ceffectDialog.dismiss();
                }
                ceffectDialog.setCancelable(false);
                ceffectDialog.setCanceledOnTouchOutside(false);
                ceffectDialog.withTitle(null);
                ceffectDialog.withMessage(Ke_Fu_data.getPhone(MenuMoreActivity.this));
                ceffectDialog.withBtnLineColor(R.color.bg_line);
                ceffectDialog.withBtnContentLineColor(R.color.bg_line);
                ceffectDialog.withButton1Text("取消");
                ceffectDialog.withButton1Click(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ceffectDialog.dismiss();
                    }
                });
                ceffectDialog.withButton2Text("拨号");
                ceffectDialog.withButton2Click(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setAction("android.intent.action.DIAL");
                        intent.setData(Uri.parse("tel:" + Ke_Fu_data.getPhone(MenuMoreActivity.this)));
                        startActivity(intent);
                        ceffectDialog.dismiss();
                    }
                });
                ceffectDialog.show();
                break;
            case R.id.version:

                long currentTime = System.currentTimeMillis();
                if ((currentTime - touchTime) >= waitTime || iii<=10) {
                    if((currentTime - touchTime) >= waitTime){
                        iii = 0;
                    }
                    iii+=1;
                    touchTime = currentTime;
                } else {
                    touchTime = 0;
                    Toast.makeText(this, "渠道号："+Utils.getChannelName(this)+"   版本code："+Utils.getVersion1(this), Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
    int iii = 0;
    long waitTime = 1000;
    long touchTime = 0;
    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Utils.closeDialog(this,ceffectDialog);
    }
}
