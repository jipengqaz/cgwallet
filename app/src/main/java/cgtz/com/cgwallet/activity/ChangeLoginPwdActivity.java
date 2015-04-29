package cgtz.com.cgwallet.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import cgtz.com.cgwallet.MApplication;
import cgtz.com.cgwallet.R;
import cgtz.com.cgwallet.bean.JsonBean;
import cgtz.com.cgwallet.client.Is_passwrod;
import cgtz.com.cgwallet.utils.Utils;
import cgtz.com.cgwallet.widget.ProgressDialog;

/**
 * 修改登录密码
 * Created by Administrator on 2014/10/17.
 */
public class ChangeLoginPwdActivity extends BaseActivity {
    private static final String TAG = "ChangeLoginPwdActivity";
    private EditText et_originalpwd;
    private EditText et_newlpwd;
    private EditText et_confirmlpwd;
    private Button btn_change_confirm;
    private ProgressDialog dialog;//进度条
    private String newpwd;
    private String mobile_phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("修改登录密码");
        setContentView(R.layout.activity_change_loginpwd);
        MApplication.registActivities(this);
        init();
        showBack(true);
        dialog = new ProgressDialog(this,R.style.loading_dialog);
        setViesListener();
    }

    /**
     * 初始化视图
     */
    private void init(){
        et_originalpwd = (EditText) findViewById(R.id.et_originalpwd);
        et_newlpwd = (EditText) findViewById(R.id.et_newlpwd);
        et_confirmlpwd = (EditText) findViewById(R.id.et_confirmlpwd);
        btn_change_confirm = (Button) findViewById(R.id.btn_change_confirm);
    }

    /**
     * 视图添加事件
     */
    private void setViesListener(){
        Utils.closeInputMethod(this);
        btn_change_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String originalpwd = et_originalpwd.getText().toString();
                newpwd = et_newlpwd.getText().toString().trim();
                String confirmpwd = et_confirmlpwd.getText().toString();
                if (TextUtils.isEmpty(originalpwd)) {
                    Utils.makeToast(ChangeLoginPwdActivity.this, "请填写原登录密码");
                } else if (TextUtils.isEmpty(newpwd)) {
                    Utils.makeToast(ChangeLoginPwdActivity.this, "新密码不能为空");
                } else if (!TextUtils.isEmpty(newpwd) && newpwd.length() < 6) {
                    Utils.makeToast(ChangeLoginPwdActivity.this, "密码长度不能小于6位");
                    et_newlpwd.setText("");
                    et_confirmlpwd.setText("");
                } else if (TextUtils.isEmpty(confirmpwd)) {
                    Utils.makeToast(ChangeLoginPwdActivity.this, "确认密码不能为空");
                } else if (!TextUtils.isEmpty(newpwd) && !newpwd.equals(confirmpwd)) {
                    Utils.makeToast(ChangeLoginPwdActivity.this, "确认密码不一致");
                    et_newlpwd.setText("");
                    et_confirmlpwd.setText("");
                } else {
                    dialog.setMessage("密码修改中...");
                    dialog.show();
                    Is_passwrod.changLoginpwd(mHandler, originalpwd, newpwd, confirmpwd, 11);
                }
            }
        });
    }

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(dialog!=null &&dialog.isShowing()){
                dialog.dismiss();
            }
            JsonBean jsonBean = (JsonBean) msg.obj;
            int code = jsonBean.getCode();
            String errorMsg = jsonBean.getError_msg();
            JSONObject json = null;
            if(!Utils.filtrateCode(ChangeLoginPwdActivity.this,jsonBean)){
                Toast.makeText(ChangeLoginPwdActivity.this, errorMsg + "  错误码" + code, Toast.LENGTH_SHORT);
                return;
            }
            try {
                json = jsonBean.getJsonObject();
                String status = json.getString("success");
                if ("0".equals(status)) {
                    Utils.makeToast_short(ChangeLoginPwdActivity.this, json.optString("msg"));
                    et_originalpwd.setText("");
                    et_newlpwd.setText("");
                    et_confirmlpwd.setText("");
                } else if("1".equals(status)){
                    //密码修改成功
                    Utils.saveLoginPwd(ChangeLoginPwdActivity.this, newpwd);
                    String mssg = json.optString("msg");
                    Utils.makeToast_short(ChangeLoginPwdActivity.this, mssg);
                    finish();
                }
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        }
    };

    /**
     * Activity被系统杀死时被调用.
     * 例如:屏幕方向改变时,Activity被销毁再重建;当前Activity处于后台,系统资源紧张将其杀死.
     * 另外,当跳转到其他Activity或者按Home键回到主屏时该方法也会被调用,系统是为了保存当前View组件的状态.
     * 在onPause之前被调用.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
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
