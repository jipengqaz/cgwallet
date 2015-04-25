package cgtz.com.cgwallet.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import cgtz.com.cgwallet.MApplication;
import cgtz.com.cgwallet.R;
import cgtz.com.cgwallet.bean.JsonBean;
import cgtz.com.cgwallet.client.Get_data;
import cgtz.com.cgwallet.utility.Constants;
import cgtz.com.cgwallet.utils.ChangeLogHelper;
import cgtz.com.cgwallet.utils.Utils;
import cn.jpush.android.api.JPushInterface;

public class StartActivity extends Activity {
    private static final String TAG = "StartActivity";
    TurnHandler turnHandler;
    Handler timerHandler;
    private SwitchPagerTimerTask timerTask;
    private long timerSpace = 3000;
    private LinearLayout ll_start;
    private ImageView rl_start;
    private File file = new File(Constants.IMG_FILE_PATH);

    /**
     * 用于延迟跳转到网络未连接提示页面
     */
    private void start(){
        TimerTask task = new TimerTask(){
            public void run(){
//                    startActivity(new Intent(StartActivity.this,No_notwork_Activity.class));
                finish();
            }
        };
        Timer timer = new Timer();
        timer.schedule(task, 2000);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        MobclickAgent.setDebugMode(Constants.IS_TEST);
        MobclickAgent.updateOnlineConfig(this);
        MApplication.registActivities(this);//存储该activity
        rl_start = (ImageView) findViewById(R.id.rl_start);
        if(file.exists()){
            Bitmap bitmap = BitmapFactory.decodeFile(Constants.IMG_FILE_PATH);
            rl_start.setImageDrawable(new BitmapDrawable(bitmap));
            setAlpha(rl_start);
        }else{
            rl_start.setImageResource(R.mipmap.loading);
            setAlpha(rl_start);
        }
//        getData();
        //跳转到   主界面
        getonLine();
    }
    private void getData(){
        Get_data.getStartUp(handler);
    }
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.e(TAG, "数据" + msg.obj);
            JsonBean jsonBean = (JsonBean) msg.obj;
            int code = jsonBean.getCode();
            String errorMsg = jsonBean.getError_msg();
            JSONObject  json;
            try {
                Log.e(TAG,"1111"+ jsonBean.getJsonString());
                json = new JSONObject(jsonBean.getJsonString());

            } catch (JSONException e) {
                e.printStackTrace();
            }

            if(code == Constants.DATA_EVENT){
                Utils.makeToast(StartActivity.this, Constants.ERROR_MSG_CODE + code+errorMsg);
                return;
            }
            switch (msg.what){
                case Constants.WHAT_STARTUP:

                break;

            }
        }
    };

    private void getonLine(){
        turnHandler = new TurnHandler();
        timerTask = new SwitchPagerTimerTask();
        timerHandler = new Handler();
        timerHandler.postDelayed(timerTask, timerSpace);
    }
    class SwitchPagerTimerTask implements Runnable {
        @Override
        public void run() {
            turnHandler.sendEmptyMessage(1);
        }
    }
    class TurnHandler extends android.os.Handler {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                if (!ChangeLogHelper.isTheSameVersion(StartActivity.this)) {
                    startActivity(new Intent(StartActivity.this, ScreenSlideActivity.class));
                } else {
                    startActivity(new Intent(StartActivity.this, MainActivity.class));
                }
                finish();
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
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

    private void setAlpha(View view){
        AlphaAnimation mHideAnimation = new AlphaAnimation(0.0f, 1.0f);//设置从不见到见
        mHideAnimation.setDuration( 2000 );//设置渐现时间
        mHideAnimation.setFillAfter( true );//设置保留动画完成时的样子
        view.startAnimation(mHideAnimation);//启动动画
    }



}
