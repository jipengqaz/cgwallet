package cgtz.com.cgwallet.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.umeng.analytics.MobclickAgent;

import org.json.JSONObject;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import cgtz.com.cgwallet.MApplication;
import cgtz.com.cgwallet.R;
import cgtz.com.cgwallet.bean.JsonBean;
import cgtz.com.cgwallet.utility.Constants;
import cgtz.com.cgwallet.utils.ChangeLogHelper;
import cgtz.com.cgwallet.utils.LogUtils;
import cgtz.com.cgwallet.utils.MD5Util;
import cgtz.com.cgwallet.utils.Utils;
import cn.jpush.android.api.JPushInterface;

public class StartActivity extends Activity {
    private static final String TAG = "StartActivity";
    TurnHandler turnHandler;
    Handler timerHandler;
    private SwitchPagerTimerTask timerTask;
    private long timerSpace = 1500;
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
        timer.schedule(task, 1500);
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
        turnHandler = new TurnHandler();
        Utils.autoLogin(this,turnHandler);
        //跳转到   主界面
        getonLine();
    }
    private void getonLine(){
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
            }else if(msg.what == Constants.WHAT_LOGIN){
                try{
                    JsonBean jsonBean = (JsonBean) msg.obj;
                    int code = jsonBean.getCode();
                    int action = msg.what;
                    switch (action){
                        case Constants.WHAT_LOGIN:
                            boolean flag = Utils.filtrateCode(StartActivity.this,jsonBean);
                            if(flag && code == Constants.OPERATION_SUCCESS){//数据交互成功
                                JSONObject jsonObject = new JSONObject(jsonBean.getJsonString());
                                JSONObject object = jsonObject.optJSONObject("info");
                                String userId = object.optString("userId");
                                String token = object.optString("token");
                                Utils.saveUserId(userId);
                                Utils.saveToken(token);
                            }
                            break;
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    LogUtils.e(TAG, "activity数据异常");
                }
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
        mHideAnimation.setDuration( 1500 );//设置渐现时间
        mHideAnimation.setFillAfter( true );//设置保留动画完成时的样子
        view.startAnimation(mHideAnimation);//启动动画
    }



}
