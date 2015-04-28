package cgtz.com.cgwallet.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;

import org.json.JSONObject;

import cgtz.com.cgwallet.MApplication;
import cgtz.com.cgwallet.R;
import cgtz.com.cgwallet.bean.JsonBean;
import cgtz.com.cgwallet.client.Is_passwrod;
import cgtz.com.cgwallet.utils.LogUtils;
import cgtz.com.cgwallet.utils.Utils;
import cgtz.com.cgwallet.view.ServerMainTainDialog;
import cn.jpush.android.api.JPushInterface;

/**
 * 修改交易密码
 *
 */
public class Modify_trade_password_Activity extends BaseActivity {
    private String TAG = "Modify_trade_password_Activity";
    private EditText pwd_edit;//密码输入框
    private Button next_step;//下一步按钮
    private ServerMainTainDialog dialog;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(dialog!=null &&dialog.isShowing()){
                dialog.dismiss();
            }
            JsonBean jsonBean = (JsonBean) msg.obj;
            int code = jsonBean.getCode();
            String errorMsg = jsonBean.getError_msg();
            JSONObject json = null;
            if(!Utils.filtrateCode(Modify_trade_password_Activity.this,jsonBean)){
                Toast.makeText(Modify_trade_password_Activity.this, errorMsg + "  错误码" + code, Toast.LENGTH_SHORT);
                return;
            }

            try {
                json = jsonBean.getJsonObject();
                int success = json.optInt("success");
                if(success == 0){
                    Utils.makeToast(Modify_trade_password_Activity.this, json.optString("msg"));
                }else if(success == 1){//校验正确
                    startActivity(new Intent(Modify_trade_password_Activity.this,Change_password_Activity.class));
                }
                Log.e(TAG, "" + json);
            }catch (Exception e){
                e.printStackTrace();
                LogUtils.e(TAG, "返回数据错误了");
                finish();
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("修改交易密码");
        setContentView(R.layout.activity_modify_trade_password);
        MApplication.registActivities(this);//存储该activity
        setBackListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        init();
    }

    /**
     * 初始化控件
     */
    private void init(){
        pwd_edit = (EditText) findViewById(R.id.pwd_edit);
        next_step = (Button) findViewById(R.id.next_step);

        next_step.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pwd_edit.getText().toString().length()>=6){
                    if(dialog == null){
                        dialog = new ServerMainTainDialog(Modify_trade_password_Activity.this,R.style.loading_dialog);
                    }
                    dialog.show();
                    Is_passwrod.isPasswrod(handler, pwd_edit.getText().toString(), 111);
                }else{
                    Utils.makeToast(Modify_trade_password_Activity.this, "登录密码位数不对");
                }
            }
        });
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
