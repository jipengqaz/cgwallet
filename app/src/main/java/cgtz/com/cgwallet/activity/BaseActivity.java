package cgtz.com.cgwallet.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import cgtz.com.cgwallet.R;
import cgtz.com.cgwallet.utility.Constants;
import cgtz.com.cgwallet.utils.Utils;
import cn.jpush.android.api.JPushInterface;

import com.networkbench.agent.impl.NBSAppAgent;

/**
 * �自定义主activity
 * Created by Administrator on 2015/4/9.
 */
public class BaseActivity extends ActionBarActivity {
    private static final String TAG = "BaseActivity";
    private ActionBar actionBar;
    private LinearLayout backLayout;
    private TextView titleView;
    protected TextView rightText;
    private ImageView iconBack;
    protected ImageView locationIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initBar();
        NBSAppAgent.setLicenseKey("0856e9d72bbd4420ba1d0ff66de71df6").withLocationServiceEnabled(true).start(this);
    }

    private void initBar(){
        actionBar = getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayUseLogoEnabled(false);
        actionBar.setIcon(android.R.color.transparent);
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.color.new_top_title));
        actionBar.setTitle("草根");
        actionBar.setElevation(0);
        View actionBarWeek = RelativeLayout.inflate(this, R.layout.layout_action_bar, null);
        backLayout = (LinearLayout) actionBarWeek.findViewById(R.id.action_bar_back_layout);
        titleView = (TextView) actionBarWeek.findViewById(R.id.action_bar_title);
        rightText = (TextView) actionBarWeek.findViewById(R.id.action_bar_right_text);
        iconBack = (ImageView) actionBarWeek.findViewById(R.id.action_bar_left_icon);
        locationIcon = (ImageView) actionBarWeek.findViewById(R.id.actionbar_location_icon);
        backLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        actionBar.setCustomView(actionBarWeek);
    }

    public void setBackListener(View.OnClickListener listener){
        backLayout.setOnClickListener(listener);
    }

    public void setTitle(String msg){
        if(msg != null){
            titleView.setText(msg);
        }
    }

    public void showBack(boolean flag){
        if(flag){
            backLayout.setVisibility(View.VISIBLE);
        }else{
            backLayout.setVisibility(View.GONE);
        }
    }

    public void setRightText(String text){
        if(!TextUtils.isEmpty(text)){
            rightText.setText(text);
            rightText.setVisibility(View.VISIBLE);
        }else{
            rightText.setVisibility(View.GONE);
        }
    }

    public void setRightListener(View.OnClickListener listener){
        if(rightText != null)
            rightText.setOnClickListener(listener);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
            default:
                break;
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        JPushInterface.onResume(this);
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        JPushInterface.onPause(this);
        MobclickAgent.onPause(this);
        Constants.GESTURES_PASSWORD =false;
    }

    @Override
    protected void onStop() {
        super.onStop();
        Constants.GESTURES_PASSWORD =true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Constants.GESTURES_PASSWORD =false;
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if(Utils.getUserId() != "" && Utils.getLockPassword(this, Utils.getUserPhone(this))!=""&& Constants.GESTURES_PASSWORD  ){
            Intent intent  = new Intent();
            intent.setClass(this,GestureVerifyActivity.class);
            startActivity(intent);
        }
    }

    /**
     * 重写关闭软键盘的方法
     */
    @Override
    public void finish() {
        Utils.HideSoftKeyboard(this);
        super.finish();
    }
}
