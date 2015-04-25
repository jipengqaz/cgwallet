package cgtz.com.cgwallet.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import org.json.JSONObject;

import java.util.HashMap;

import cgtz.com.cgwallet.MApplication;
import cgtz.com.cgwallet.R;
import cgtz.com.cgwallet.bean.JsonBean;
import cgtz.com.cgwallet.presenter.SplashPresenter;
import cgtz.com.cgwallet.utility.Constants;
import cgtz.com.cgwallet.utils.CustomTask;
import cgtz.com.cgwallet.utils.Utils;
import cgtz.com.cgwallet.view.ISplashView;
import cgtz.com.cgwallet.widget.ProgressDialog;
import cn.jpush.android.api.JPushInterface;

/**
 * 注册页面
 */
public class RegistActivity extends BaseActivity implements ISplashView, View.OnClickListener{
    private EditText registMobile;
    private EditText securityCode;
    private TextView getSecurityCode;
    private Button registNext;
    private String mobile = "";
    private ProgressDialog progressDialog;
    private SplashPresenter presenter;
    private String mobile_code;//验证码
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist);
        setTitle("注册");
        presenter = new SplashPresenter(this);
        MApplication.registActivities(this);//存储该activity
        showBack(true);
        initViews();
    }

    private void initViews(){
        registMobile = (EditText) findViewById(R.id.et_regist_mobile);
        securityCode = (EditText) findViewById(R.id.et_regist_security_code);
        getSecurityCode = (TextView) findViewById(R.id.tv_regist_get_security_code);
        registNext = (Button) findViewById(R.id.btn_regist_next);
        getSecurityCode.setOnClickListener(this);
        registNext.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.tv_regist_get_security_code://获取验证码
                mobile = registMobile.getText().toString().trim();
                if(TextUtils.isEmpty(mobile)){
                    Utils.makeToast(this,"请输入注册手机号");
                }else{
                    presenter.didFinishLoading(this);
                }
                break;
            case R.id.btn_regist_next://下一步
                mobile  = registMobile.getText().toString().trim();
                if(TextUtils.isEmpty(mobile)){
                    Utils.makeToast(this,"请输入注册手机号");
                }else if(TextUtils.isEmpty(mobile_code)){
                    Utils.makeToast(this,"请填写验证码");
                }else{
                    startActivity(new Intent(this,RegistNextActivity.class)
                    .putExtra("mobile",mobile)
                    .putExtra("mobile_code",mobile_code));
                }
                break;
        }
    }

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            try{
                JsonBean jsonBean = (JsonBean) msg.obj;
                int code = jsonBean.getCode();
                String errorMsg = jsonBean.getError_msg();
                if(code == Constants.DATA_EVENT){
                    hideProcessBar();
                    Utils.makeToast(RegistActivity.this,Constants.ERROR_MSG_CODE+code);
                    return;
                }
                int action = msg.what;
                switch (action){
                    case Constants.WHAT_GET_SECURITY_CODE:
                        boolean flag = Utils.filtrateCode(RegistActivity.this,jsonBean);
                        if(flag && code == Constants.OPERATION_FAIL){//数据交互失败
                            Utils.makeToast(RegistActivity.this, errorMsg);
                        }else if(flag && code == Constants.OPERATION_SUCCESS){//数据交互成功
                            JSONObject jsonObject = new JSONObject(jsonBean.getJsonString());
                            if(Constants.IS_TEST){
                                mobile_code = jsonObject.optString("mobile_code");
                                Utils.makeToast(RegistActivity.this,mobile_code);
                            }
                        }else if(flag && code == 2){
                            Utils.makeToast(RegistActivity.this, "手机号已注册，请直接登录");
                        }
                        hideProcessBar();
                        break;
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    };

    @Override
    public void startProcessBar() {
        if(progressDialog == null){
            progressDialog = new ProgressDialog(this,R.style.loading_dialog);
        }
        if(progressDialog.isShowing()){
            progressDialog.dismiss();
        }
        progressDialog.show();
    }

    @Override
    public void hideProcessBar() {
        if(progressDialog != null && progressDialog.isShowing()){
            progressDialog.dismiss();
        }
    }

    @Override
    public void showNetError() {
        Utils.makeToast(this, Constants.IS_EVENT_MSG);
    }

    @Override
    public void startNextActivity() {
        HashMap<String,String> params = new HashMap();
        params.put("mobile", mobile);
        CustomTask task = new CustomTask(mHandler, Constants.WHAT_GET_SECURITY_CODE
                ,Constants.URL_GET_SECURITY_CODE,true,params,true);
        task.execute();
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
}
