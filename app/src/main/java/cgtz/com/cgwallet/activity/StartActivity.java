package cgtz.com.cgwallet.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;

import org.json.JSONObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.MemoryHandler;

import cgtz.com.cgwallet.MApplication;
import cgtz.com.cgwallet.R;
import cgtz.com.cgwallet.bean.JsonBean;
import cgtz.com.cgwallet.Service.Image_download_Service;
import cgtz.com.cgwallet.Service.Provinces_download_Service;
import cgtz.com.cgwallet.bean.JsonBean;
import cgtz.com.cgwallet.client.Get_data;
import cgtz.com.cgwallet.utility.Constants;
import cgtz.com.cgwallet.utils.ChangeLogHelper;
import cgtz.com.cgwallet.utils.CustomTask;
import cgtz.com.cgwallet.utils.HttpUtils;
import cgtz.com.cgwallet.utils.LogUtils;
import cgtz.com.cgwallet.utils.MD5Util;
import cgtz.com.cgwallet.utils.Utils;
import cgtz.com.cgwallet.utils.Ke_Fu_data;
import cgtz.com.cgwallet.utils.LogUtils;
import cgtz.com.cgwallet.utils.Start_update_value;
import cgtz.com.cgwallet.utils.Utils;
import cgtz.com.cgwallet.widget.ServerMainTainDialog;
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
    private MHandler handler;
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
        MApplication.setJpushRegistid(JPushInterface.getRegistrationID(getApplicationContext()));
        try {
            ApplicationInfo info = this.getPackageManager()
                    .getApplicationInfo(
                            getPackageName()
                            , PackageManager.GET_META_DATA);
            String msg = info.metaData.getString("UMENG_CHANNEL");
            MApplication.setChannel(msg);
            TelephonyManager tm=(TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
            MApplication.setImiId(tm.getDeviceId());
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        MainActivity activity = (MainActivity) MApplication.getActivityByName(MainActivity.class.getName());
        if( activity != null && !activity.isFinishing()){
            finish();
            return;
        }

        turnHandler = new TurnHandler();
        handler = new MHandler();
        rl_start = (ImageView) findViewById(R.id.rl_start);
        if(file.exists()){
            Bitmap bitmap = BitmapFactory.decodeFile(Constants.IMG_FILE_PATH);
            rl_start.setImageDrawable(new BitmapDrawable(getResources(),bitmap));
            setAlpha(rl_start);
        }else{
            rl_start.setImageResource(R.mipmap.loading);
            setAlpha(rl_start);
        }
        VersionTask task = new VersionTask(this);
        task.execute();
        //获取
        Get_data.getStartUp(handler);
        Utils.autoLogin(this,turnHandler);
        //跳转到   主界面
//        getonLine();
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
        if(!map.get(Start_update_value.KEY_IMAGE_UPDATE).equals(imageUpdate) || !file.exists()){
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
        HashMap<String ,String> map = new HashMap<>();
        map.put(Ke_Fu_data.KEY_CONTENT,json.optString("content"));
        map.put(Ke_Fu_data.KEY_PHONE,json.optString("phone_number"));
        map.put(Ke_Fu_data.KEY_WORK_TIME,json.optString("work_time"));
        map.put(Ke_Fu_data.KEY_SAFE, json.optString("possession_safe"));
        Ke_Fu_data.saveKe_fu_data(this, map);//存储获取的数据
        Start_update_value.saveKeFuUpdate(this, kefuUpdate);//存储更新判断值
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

    /**
     *
     */
    private class MHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            JsonBean jsonBean = (JsonBean) msg.obj;
            int code = jsonBean.getCode();
            String errorMsg = jsonBean.getError_msg();
            JSONObject  json = null;
            if(!Utils.filtrateCode(StartActivity.this,jsonBean)){
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
    }

    /**
     * 判断是否需要跟新
     * */
    class VersionTask extends AsyncTask<String,Void,String> {
        private Context context;
        public VersionTask(Context context){
            this.context = context;
        }
        @Override
        protected String doInBackground(String... params) {
            String result;
            if(Constants.IS_TEST){
                result = HttpUtils.HttpGet(Constants.VERSION_UPDATE + "?version=" + Utils.getVersion(context)
                        + "&type=android", "UTF-8");
            }else {
                result = HttpUtils.HttpsGet(Constants.VERSION_UPDATE + "?version=" + Utils.getVersion(context)
                        + "&type=android", "UTF-8");
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                if(TextUtils.isEmpty(result) || result.equals("event")){
                    getonLine();
                }else{
                    JSONObject json = new JSONObject(result);
                    if(json!=null) {
                        String success = json.optString("success");
                        if (success.equals("1")) {
                            String code = json.optString("code");
                            if (code.equals("200")) {
                                JSONObject obj = json.optJSONObject("info");
                                int forceUpgrade = json.optInt("forceUpgrade");//判断是否强制升级
                                if(obj == null){
                                    getonLine();
                                    return;
                                }
                                final String downurl = obj.optString("downurl");//获取下载路径
                                String notice = obj.optString("notice");//获取最新版本更新的内容
                                String version = obj.optString("version");//获取改变的版本号
                                if(TextUtils.isEmpty(downurl)
                                        || TextUtils.isEmpty(notice)
                                        || TextUtils.isEmpty(version)){
                                    getonLine();
                                    return;
                                }
                                LinearLayout dialogLayout =
                                        (LinearLayout) LayoutInflater.from(StartActivity.this)
                                                .inflate(R.layout.update_tip, null);
                                final Dialog mDialog =
                                        new Dialog(StartActivity.this, R.style.loading_dialog2);
                                mDialog.setContentView(dialogLayout);
                                mDialog.setCanceledOnTouchOutside(false);
                                mDialog.setCancelable(false);
                                TextView update_version = (TextView) dialogLayout.findViewById(R.id.update_version);//版本号
                                TextView update_version_size = (TextView) dialogLayout.findViewById(R.id.update_version_size);//版本号
                                TextView update_notice = (TextView) dialogLayout.findViewById(R.id.update_notice);//跟新内容
                                TextView update_tip_button = (TextView) dialogLayout.findViewById(R.id.update_tip_button);//更新按钮
                                TextView update_button_cancel = (TextView) dialogLayout.findViewById(R.id.update_button_cancel);//取消按钮
                                View update_center_line = dialogLayout.findViewById(R.id.update_center_line);//中间竖线
                                if(forceUpgrade == 1){
                                    //强制升级
                                    LogUtils.i(TAG,"强制升级");
                                    update_button_cancel.setVisibility(View.GONE);
                                    update_center_line.setVisibility(View.GONE);
                                }else if(forceUpgrade == 0){
                                    //不强制升级
                                    LogUtils.i(TAG,"不强制升级");
                                    update_button_cancel.setVisibility(View.VISIBLE);
                                    update_center_line.setVisibility(View.VISIBLE);
                                }
                                update_version.setText("最新版本：" + version);
                                update_version_size.setText("当前版本：" + Utils.getVersion(StartActivity.this));
                                update_notice.setText(Html.fromHtml(notice));
                                mDialog.show();

                                update_tip_button.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Uri uri = Uri.parse(downurl);
                                        Intent intent = new Intent(Intent.ACTION_VIEW,uri);
                                        startActivity(intent);
                                        finish();
                                        mDialog.dismiss();
                                    }
                                });

                                update_button_cancel.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        getonLine();
                                        mDialog.dismiss();
                                    }
                                });
                            } else {
                                getonLine();
                            }
                        }else if(json.optString("action").equals("maintain")){
                            //系统维护中
                            final ServerMainTainDialog maintainDialog =
                                    ServerMainTainDialog.getInstans(StartActivity.this);
                            maintainDialog.setCancelable(false);
                            maintainDialog.setCanceledOnTouchOutside(false);
                            maintainDialog.withMaintainIconClick(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    maintainDialog.dismiss();
                                    System.exit(0);
                                }
                            });
                            maintainDialog.show();
                        }else {
                            getonLine();
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
                LogUtils.e(TAG, "返回数据错误了");
                getonLine();
            }
        }
    }

}
