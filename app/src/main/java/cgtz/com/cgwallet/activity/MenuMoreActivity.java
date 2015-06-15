package cgtz.com.cgwallet.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;
import com.umeng.update.UpdateStatus;

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
    private RelativeLayout versionUpdate;//版本升级
    private CustomEffectsDialog ceffectDialog;
    private TextView version;//版本号
    private TextView serviceMobile;//客服电话号码
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
        versionUpdate = (RelativeLayout) findViewById(R.id.version_update);
        version = (TextView) findViewById(R.id.version);
        serviceMobile = (TextView) findViewById(R.id.service_mobile);
        TextView ke_fu_tip = (TextView) findViewById(R.id.ke_fu_tip);
        ke_fu_tip.setText(Ke_Fu_data.getWorkTime(this));
        Utils.safeCopyWrite(this);//设置安全文案
        serviceMobile.setText(Ke_Fu_data.getPhone(MenuMoreActivity.this));
    }

    private void setListener(){
        aboutMine.setOnClickListener(this);
        feedBack.setOnClickListener(this);
        contactMine.setOnClickListener(this);
        versionUpdate.setOnClickListener(this);
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
                if(!Utils.isLogined()){
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
            case R.id.version_update://版本升级
                UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {
                    @Override
                    public void onUpdateReturned(int updateStatus, UpdateResponse updateInfo) {
                        switch (updateStatus) {
                            case UpdateStatus.Yes: // has update
                                UmengUpdateAgent.showUpdateDialog(MenuMoreActivity.this, updateInfo);
                                break;
                            case UpdateStatus.No: // has no update
                                Toast.makeText(MenuMoreActivity.this, "没有更新", Toast.LENGTH_SHORT).show();
                                break;
                            case UpdateStatus.NoneWifi: // none wifi
                                Toast.makeText(MenuMoreActivity.this, "没有wifi连接， 只在wifi下更新", Toast.LENGTH_SHORT).show();
                                break;
                            case UpdateStatus.Timeout: // time out
                                Toast.makeText(MenuMoreActivity.this, "超时", Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                });
                UmengUpdateAgent.forceUpdate(MApplication.getInstance());
                break;
        }
    }
    private int iii = 0;
    private long waitTime = 1000;
    private long touchTime = 0;
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
