package cgtz.com.cgwallet.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cgtz.com.cgwallet.MApplication;
import cgtz.com.cgwallet.R;
import cgtz.com.cgwallet.utils.Ke_Fu_data;
import cgtz.com.cgwallet.utils.Utils;
import cgtz.com.cgwallet.widget.CustomEffectsDialog;
import cgtz.com.cgwallet.widget.ProgressDialog;

/**
 * 更多页面
 */
public class MenuMoreActivity extends BaseActivity implements View.OnClickListener{
    private RelativeLayout aboutMine;//关于我们
    private RelativeLayout feedBack;//意见反馈
    private RelativeLayout contactMine;//联系我们
    private CustomEffectsDialog ceffectDialog;
    private ProgressDialog progressDialog;
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
        if(progressDialog == null){
            progressDialog = new ProgressDialog(this,R.style.loading_dialog);
        }
        Utils.SaveOrDrawMoney(this, progressDialog);
    }

    private void setListener(){
        aboutMine.setOnClickListener(this);
        feedBack.setOnClickListener(this);
        contactMine.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.about_mine://关于我们
                startActivity(new Intent(MenuMoreActivity.this,AboutCompanyActivity.class));
                break;
            case R.id.feed_back://意见反馈
                startActivity(new Intent(MenuMoreActivity.this,FeedBackActivity.class));
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Utils.closeDialog(this,ceffectDialog);
    }
}
