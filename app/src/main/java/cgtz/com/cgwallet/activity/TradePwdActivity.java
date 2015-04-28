package cgtz.com.cgwallet.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cgtz.com.cgwallet.MApplication;
import cgtz.com.cgwallet.R;
import cgtz.com.cgwallet.bean.JsonBean;
import cgtz.com.cgwallet.utility.Constants;
import cgtz.com.cgwallet.utils.CustomTask;
import cgtz.com.cgwallet.utils.LogUtils;
import cgtz.com.cgwallet.utils.MD5Util;
import cgtz.com.cgwallet.utils.Utils;
import cgtz.com.cgwallet.view.ServerMainTainDialog;

/**
 * 设置交易密码页面
 */
public class TradePwdActivity extends BaseActivity {
    private static final String TAG = "TradePwdActivity";
    private InputMethodManager im;
    private LinearLayout trade_pwd_parent;
    private EditText et_trade_pwd;
    private EditText et_trade_pwd_again;
    private Button trade_pwd_confirm;
    private LinearLayout layout_seted_trade_pwd;
    private boolean isSetTradePwd;
    private ServerMainTainDialog dialog;

    /**
     * Activity被系统杀死时被调用.
     * 例如:屏幕方向改变时,Activity被销毁再重建;当前Activity处于后台,系统资源紧张将其杀死.
     * 另外,当跳转到其他Activity或者按Home键回到主屏时该方法也会被调用,系统是为了保存当前View组件的状态.
     * 在onPause之前被调用.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("isSetTradePwd", isSetTradePwd);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null){
            isSetTradePwd = savedInstanceState.getBoolean("isSetTradePwd", false);//用于判断是否不设交易密码
        }else{
            isSetTradePwd = getIntent().getBooleanExtra("isSetTradePwd", false);//用于判断是否不设交易密码
        }
        if(isSetTradePwd){
            setTitle("交易密码");
            setRightText("修改");
            setRightListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(TradePwdActivity.this, Modify_trade_password_Activity.class));
                }
            });
        }else{
            setTitle("设置交易密码");
        }
        setContentView(R.layout.activity_trade_pwd);
        MApplication.registActivities(this);//存储该activity
        im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        initView();
        setViewListener();
    }

    /**
     * 初始化视图
     */
    private void initView(){
        trade_pwd_parent = (LinearLayout) findViewById(R.id.trade_pwd_parent);
        et_trade_pwd = (EditText) findViewById(R.id.trade_pwd);
        et_trade_pwd_again = (EditText) findViewById(R.id.trade_pwd_again);
        trade_pwd_confirm = (Button) findViewById(R.id.trade_pwd_confirm);
        layout_seted_trade_pwd = (LinearLayout) findViewById(R.id.layout_seted_trade_pwd);
        if(isSetTradePwd){
            trade_pwd_parent.setVisibility(View.GONE);
            layout_seted_trade_pwd.setVisibility(View.VISIBLE);
        }else{
            trade_pwd_parent.setVisibility(View.VISIBLE);
            layout_seted_trade_pwd.setVisibility(View.GONE);
        }
    }

    /**
     * 视图添加事件
     */
    private void setViewListener(){
        /**
         * 点击屏幕，消失键盘
         */
        trade_pwd_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (im != null && im.isActive()) {
                    im.hideSoftInputFromWindow(trade_pwd_parent.getWindowToken(), 0);
                }
            }
        });
        /**
         * 确定按钮
         */
        trade_pwd_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pwd1 = et_trade_pwd.getText().toString().trim();
                String pwd2 = et_trade_pwd_again.getText().toString().trim();
                if (pwd1.length() == 0) {
                    Utils.makeToast(TradePwdActivity.this, getString(R.string.please_edite_pwd));
                } else if (pwd1.length() > 0 && pwd1.length() < 6) {
                    Utils.makeToast(TradePwdActivity.this, getString(R.string.error_pwd_length));
                } else if (!pwd1.equals(pwd2)) {
                    Utils.makeToast(TradePwdActivity.this, getString(R.string.error_two_pwd_not_same));
                } else if (MD5Util.md5(pwd1).equals(Utils.getLoginPwd(TradePwdActivity.this))) {
                    Utils.makeToast(TradePwdActivity.this, getString(R.string.error_login_pwd_same));
                } else {
                    if (dialog == null) {
                        dialog = new ServerMainTainDialog(TradePwdActivity.this, R.style.loading_dialog);
                    } else if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    dialog.show();
                    Map<String, String> maps = new HashMap<String, String>();
                    maps.put("user_id", Utils.getUserId());
                    maps.put("token", Utils.getToken());
                    maps.put("password", MD5Util.md5(pwd1));
//                    maps.put("confirm_pass",MD5Util.md5(pwd2));
                    LogUtils.i(TAG, "pwd: " + pwd1 + "    md51: " + MD5Util.md5(pwd1) + " md52：" + MD5Util.md5(pwd2));
                    CustomTask jujgeTask = new CustomTask(handler, 1, Constants.URL_PAY_SETPASS, true, maps, Constants.ENCONDING);
                    jujgeTask.execute();
                }
            }
        });
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            JsonBean jsonBean = (JsonBean) msg.obj;
            int code = jsonBean.getCode();
            String errorMsg = jsonBean.getError_msg();
            JSONObject json = null;
            if(!Utils.filtrateCode(TradePwdActivity.this,jsonBean)){
                Toast.makeText(TradePwdActivity.this, errorMsg + "  错误码" + code, Toast.LENGTH_SHORT);
                return;
            }
            json = jsonBean.getJsonObject();
            if(json != null){
                int success = json.optInt("success");
                if(success == 1){
                    Utils.makeToast(TradePwdActivity.this, json.optString("msg"));
                    finish();//不用跳转  直接关闭该页面
                }else{
                    Utils.makeToast(TradePwdActivity.this, json.optString("msg"));
                }
            }
        }
    };


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
    }

}
