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
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import cgtz.com.cgwallet.MApplication;
import cgtz.com.cgwallet.R;
import cgtz.com.cgwallet.Service.Image_download_Service;
import cgtz.com.cgwallet.Service.Provinces_download_Service;
import cgtz.com.cgwallet.bean.JsonBean;
import cgtz.com.cgwallet.client.Get_data;
import cgtz.com.cgwallet.utility.Constants;
import cgtz.com.cgwallet.utils.ChangeLogHelper;
import cgtz.com.cgwallet.utils.Ke_Fu_data;
import cgtz.com.cgwallet.utils.LogUtils;
import cgtz.com.cgwallet.utils.Start_update_value;
import cgtz.com.cgwallet.utils.Utils;
import cn.jpush.android.api.JPushInterface;

public class StartActivity extends Activity {
    private  String TAG = "StartActivity";
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
            rl_start.setImageDrawable(new BitmapDrawable(getResources(),bitmap));
            setAlpha(rl_start);
        }else{
            rl_start.setImageResource(R.mipmap.loading);
            setAlpha(rl_start);
        }
        //获取
        Get_data.getStartUp(handler);
        //跳转到   主界面
        getonLine();
    }

    /**
     * 根据接口返回数据判断   启动图  客服信息   和分行信息  是否需要更新
     * @param json
     */
    private void getJudge_Update(JSONObject json){
        Map<String,String> map = Start_update_value.getUpdateTime(StartActivity.this);
        String imageUpdate = json.optString("imageUpdate");//启动图图片更新时间
        kefuUpdate = json.optString("kefuUpdate");//客服信息更新时间
        String provinceCityUpdate = json.optString("provinceCityUpdate");//分行信息更新时间
        if(!map.get(Start_update_value.KEY_IMAGE_UPDATE).equals(imageUpdate)){
            startService(new Intent(this, Image_download_Service.class).putExtra("imageUpdate", imageUpdate));//开启获取启动图的服务
        }
        if(!map.get(Start_update_value.KEY_KEFU_UPDATE).equals(kefuUpdate)){//判断是否更新客服数据文案
            Get_data.getKefuTip(handler);
        }

        if(!map.get(Start_update_value.KEY_CITY_UPDATE).equals(provinceCityUpdate)){
            startService(new Intent(this,Provinces_download_Service.class).putExtra("provinceCityUpdate",provinceCityUpdate));//开启获取银行分行信息的服务

        }
    }
    private String kefuUpdate;
    /**
     * 存储客服相关信息
     * @param json
     */
    private void setKe_FU(JSONObject json){
        Map<String ,String> map = new HashMap<String,String>();
        map.put(Ke_Fu_data.KEY_CONTENT,json.optString("content"));
        map.put(Ke_Fu_data.KEY_PHONE,json.optString("phone_number"));
        map.put(Ke_Fu_data.KEY_WORK_TIME,json.optString("work_time"));
        map.put(Ke_Fu_data.KEY_SAFE, json.optString("possession_safe"));
        Ke_Fu_data.saveKe_fu_data(this, map);//存储获取的数据
        Start_update_value.saveKeFuUpdate(this, kefuUpdate);//存储更新判断值
    }
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            JsonBean jsonBean = (JsonBean) msg.obj;
            int code = jsonBean.getCode();
            String errorMsg = jsonBean.getError_msg();
            JSONObject  json = null;
            if(!Utils.filtrateCode(StartActivity.this,jsonBean)){
                Toast.makeText(StartActivity.this,errorMsg+"  错误码"+code,Toast.LENGTH_SHORT);
                return;
            }
            try {
                LogUtils.e(TAG, "1111" + jsonBean.getJsonString() + "     " + errorMsg + "   " + code + Utils.filtrateCode(StartActivity.this, jsonBean));
                json = new JSONObject(jsonBean.getJsonString());

            } catch (JSONException e) {
                e.printStackTrace();
            }

            if(code == Constants.DATA_EVENT){
                Utils.makeToast(StartActivity.this, Constants.ERROR_MSG_CODE + code+errorMsg);
                return;
            }
            if(json.optInt("success") == 1){
                switch (msg.what){
                    case Constants.WHAT_STARTUP://数据更新时间
                        getJudge_Update(json);
                        break;
                    case Constants.WHAT_KE_FU://获取客服数据
                        setKe_FU(json);
                        break;
                }
            }else{

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
        mHideAnimation.setDuration( 1500 );//设置渐现时间
        mHideAnimation.setFillAfter( true );//设置保留动画完成时的样子
        view.startAnimation(mHideAnimation);//启动动画
    }



}
