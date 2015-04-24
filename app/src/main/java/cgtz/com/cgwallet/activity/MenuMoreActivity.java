package cgtz.com.cgwallet.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import cgtz.com.cgwallet.MApplication;
import cgtz.com.cgwallet.R;

/**
 * 更多页面
 */
public class MenuMoreActivity extends BaseActivity implements View.OnClickListener{
    private RelativeLayout aboutMine;//关于我们
    private RelativeLayout feedBack;//意见反馈
    private RelativeLayout contactMine;//联系我们
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
                break;
        }
    }
}
