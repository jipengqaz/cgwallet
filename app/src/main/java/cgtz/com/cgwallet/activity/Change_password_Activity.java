package cgtz.com.cgwallet.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONObject;

import cgtz.com.cgwallet.MApplication;
import cgtz.com.cgwallet.R;
import cgtz.com.cgwallet.bean.JsonBean;
import cgtz.com.cgwallet.client.Modify_trading_password;
import cgtz.com.cgwallet.utility.Constants;
import cgtz.com.cgwallet.utils.LogUtils;
import cgtz.com.cgwallet.utils.MD5Util;
import cgtz.com.cgwallet.utils.Utils;
import cgtz.com.cgwallet.widget.ProgressDialog;

/**
 * 修改交易密码
 * .
 */
public class Change_password_Activity extends BaseActivity{
    private EditText password_1,password_2;
    private EditText code;//短信验证码
    private Button send_message,determine;//短信验证码获取   和确定按钮
    private String TAG = "Change_password_Activity";
    private int mCount = 60;
    private ProgressDialog dialog;
    private Handler mHandler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("修改交易密码");
        showBack(true);
        setContentView(R.layout.activity_change_password);
        MApplication.registActivities(this);//存储该activity
        showBack(true);
        init();
        control();
    }

    Runnable runnable = new Runnable() {//发送短信倒计时
        @Override
        public void run() {
            mCount--;
            if (mCount > 0) {
                send_message.setText(mCount + "秒后重发");
                send_message.setEnabled(false);
                send_message.setBackgroundResource(R.drawable.banned_click);//设置背景
                mHandler.postDelayed(this, 1000);//延迟一秒执行
            } else {
                send_message.setText("重发验证码");
                mHandler.removeCallbacks(this);
                send_message.setBackgroundResource(R.drawable.bg_button_preed);
                send_message.setEnabled(true);
            }
        }
    };
    protected void handlerDown() {//发送短信倒计时
        mCount = 60;
        mHandler.postDelayed(runnable, 0);//立即执行
        Modify_trading_password.Get_SMS(new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                try {
                    JsonBean jsonBean = (JsonBean) msg.obj;
                    int code = jsonBean.getCode();
                    String errorMsg = jsonBean.getError_msg();
                    JSONObject json = null;
                    if(!Utils.filtrateCode(Change_password_Activity.this,jsonBean)){
                        return;
                    }
                    json = jsonBean.getJsonObject();
                    int success = json.optInt("success");
                    if (success == 0) {
                        Utils.makeToast(Change_password_Activity.this, json.optString("msg"));
                    } else if (success == 1) {//发送成功
                        Utils.makeToast(Change_password_Activity.this, json.optString("msg"));
                        if (Constants.IS_TEST) {
                            Utils.makeToast(Change_password_Activity.this, json.optString("mobileCode"));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Utils.makeToast_short(Change_password_Activity.this, getString(R.string.error_exception));
                    finish();
                }
            }
        },1);
    }
    /**
     * 初始化控件
     */
    private void init(){
        Utils.safeCopyWrite(this);//设置安全文案
        password_1 = (EditText) findViewById(R.id.password_1);
        password_2 = (EditText) findViewById(R.id.password_2);
        code = (EditText) findViewById(R.id.code);
        send_message = (Button) findViewById(R.id.send_message);
        determine = (Button) findViewById(R.id.determine);
    }

    /**
     * 填充控件   和添加事件
     */
    private void control(){
        Utils.closeInputMethod(this);
        send_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handlerDown();//发送短信倒计时
            }
        });

        determine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String idlpw = Utils.getLoginPwd(Change_password_Activity.this);
                LogUtils.i(TAG, "保存密码: " + idlpw);
                String pwd_1 = password_1.getText().toString();
                String pwd_2 = password_2.getText().toString();
                String cod = code.getText().toString();
                if (pwd_1.isEmpty() || pwd_1.length() != 6) {
                    Utils.makeToast_short(Change_password_Activity.this, "交易密码输入位数不对");
                } else if (pwd_2.isEmpty() || pwd_2.length() != 6) {
                    Utils.makeToast_short(Change_password_Activity.this, "交易密码输入位数不对");
                } else if (!pwd_2.equals(pwd_1)) {
                    Utils.makeToast_short(Change_password_Activity.this, "两次输入的交易密码不相同");
                } else if (MD5Util.md5(pwd_1).equals(Utils.getLoginPwd(Change_password_Activity.this))) {
                    Utils.makeToast_short(Change_password_Activity.this, getString(R.string.error_login_pwd_same));
                } else if (cod.isEmpty() || cod.length() != 6) {
                    Utils.makeToast_short(Change_password_Activity.this, "验证码输入位数不对");
                } else {
                    if (dialog == null) {
                        dialog = new ProgressDialog(Change_password_Activity.this, R.style.loading_dialog);
                    }
                    dialog.show();
                    Modify_trading_password.ResetPayPass(new Handler() {
                        @Override
                        public void handleMessage(Message msg) {
                            super.handleMessage(msg);
                            if (dialog != null && dialog.isShowing()) {
                                dialog.dismiss();
                            }

                            try {
                                JsonBean jsonBean = (JsonBean) msg.obj;
                                int code = jsonBean.getCode();
                                String errorMsg = jsonBean.getError_msg();
                                JSONObject json = null;
                                if (!Utils.filtrateCode(Change_password_Activity.this, jsonBean)) {
                                    return;
                                }
                                json = jsonBean.getJsonObject();
                                int success = json.optInt("success");
                                if (success == 0) {
                                    Utils.makeToast_short(Change_password_Activity.this, json.optString("msg"));
                                } else if (success == 1) {//修改成功
                                    Utils.makeToast_short(Change_password_Activity.this, json.optString("msg"));
                                    MApplication.destroyActivity(MApplication.getActivityByName(Modify_trade_password_Activity.class.getName()));
                                    MApplication.destroyActivity(MApplication.getActivityByName(TradePwdActivity.class.getName()));
                                    finish();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                LogUtils.e(TAG, "返回数据错误了");
                                Utils.makeToast_short(Change_password_Activity.this, getString(R.string.error_exception));
                                finish();
                            }
                        }
                    }, pwd_1, cod, 12);
                }
            }
        });
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
    }

}
