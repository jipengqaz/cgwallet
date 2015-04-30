package cgtz.com.cgwallet.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import cgtz.com.cgwallet.R;
import cn.jpush.android.api.JPushInterface;

public class TestJpushActivity extends BaseActivity {
    private Bundle bundle;
    private Button jpush_msg_open_app;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("草根消息");
        setContentView(R.layout.activity_test_jpush);
        jpush_msg_open_app = (Button) findViewById(R.id.jpush_msg_open_app);
        if(savedInstanceState != null){
            bundle = savedInstanceState;
            String title = bundle.getString(JPushInterface.EXTRA_NOTIFICATION_TITLE);
            String content = bundle.getString(JPushInterface.EXTRA_ALERT);
            TextView tv_title = (TextView) findViewById(R.id.title);
            TextView tv_content = (TextView) findViewById(R.id.content);
//            tv_title.setText(title);
            tv_content.setText(Html.fromHtml(content));
        }else if(getIntent() != null){
            bundle = getIntent().getExtras();
            String title = bundle.getString(JPushInterface.EXTRA_NOTIFICATION_TITLE);
            String content = bundle.getString(JPushInterface.EXTRA_ALERT);
            TextView tv_title = (TextView) findViewById(R.id.title);
            TextView tv_content = (TextView) findViewById(R.id.content);
//            tv_title.setText(title);
            tv_content.setText(Html.fromHtml(content));
        }
        jpush_msg_open_app.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TestJpushActivity.this,StartActivity.class));
                finish();
            }
        });
    }
    /**
     * Activity被系统杀死时被调用.
     * 例如:屏幕方向改变时,Activity被销毁再重建;当前Activity处于后台,系统资源紧张将其杀死.
     * 另外,当跳转到其他Activity或者按Home键回到主屏时该方法也会被调用,系统是为了保存当前View组件的状态.
     * 在onPause之前被调用.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState=bundle;
        super.onSaveInstanceState(outState);
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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
