package cgtz.com.cgwallet.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cgtz.com.cgwallet.R;

/**
 * �自定义主activity
 * Created by Administrator on 2015/4/9.
 */
public class BaseActivity extends ActionBarActivity {
    private static final String TAG = "BaseActivity";
    private ActionBar actionBar;
    private LinearLayout backLayout;
    private TextView titleView;
    private ImageView iconBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initBar();
    }

    private void initBar(){
        actionBar = getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayUseLogoEnabled(false);
        actionBar.setIcon(android.R.color.transparent);
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.color.action_bar_bg));
        actionBar.setTitle("草根");
        View actionBarWeek = RelativeLayout.inflate(this, R.layout.layout_action_bar, null);
        backLayout = (LinearLayout) actionBarWeek.findViewById(R.id.action_bar_back_layout);
        titleView = (TextView) actionBarWeek.findViewById(R.id.action_bar_title);
        iconBack = (ImageView) actionBarWeek.findViewById(R.id.action_bar_left_icon);
        backLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        actionBar.setCustomView(actionBarWeek);
    }

    public void setTitle(String msg){
        if(msg != null){
            titleView.setText(msg);
        }
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
}
