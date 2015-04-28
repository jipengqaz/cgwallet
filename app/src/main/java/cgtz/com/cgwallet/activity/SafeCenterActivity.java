package cgtz.com.cgwallet.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.HashMap;

import cgtz.com.cgwallet.MApplication;
import cgtz.com.cgwallet.R;
import cgtz.com.cgwallet.bean.JsonBean;
import cgtz.com.cgwallet.presenter.SplashPresenter;
import cgtz.com.cgwallet.utility.Constants;
import cgtz.com.cgwallet.utils.CustomTask;
import cgtz.com.cgwallet.utils.LogUtils;
import cgtz.com.cgwallet.utils.Utils;
import cgtz.com.cgwallet.view.ISplashView;
import cgtz.com.cgwallet.widget.ProgressDialog;

/**
 * 安全中心
 */
public class SafeCenterActivity extends BaseActivity implements View.OnClickListener,ISplashView{
    private static final String TAG = "SafeCenterActivity";
    private RelativeLayout needNameLayout;
    private RelativeLayout needBankLayout;
    private TextView saveLayout;
    private TextView drawLayout;
    private TextView nameLayout;
    private TextView bankLayout;
    private ImageView nameIcon;
    private ImageView bankIcon;
    private SplashPresenter presenter;
    private ProgressDialog progressDialog;

    private RelativeLayout rl_personal_sodoko_unlock_change,Reset_Passwrod,rl_personal_loginpwd_change;//管理手势,修改交易密码,修改密码
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safe_center);
        MApplication.registActivities(this);//存储该activity
        setTitle("安全中心");
        showBack(true);
        presenter = new SplashPresenter(this);
        init();
        presenter.didFinishLoading(this);
    }

    private void init() {
        needNameLayout = (RelativeLayout) findViewById(R.id.rl_personal_anthen);
        needBankLayout = (RelativeLayout) findViewById(R.id.rl_personal_bankcardbinding);
        rl_personal_sodoko_unlock_change = (RelativeLayout) findViewById(R.id.rl_personal_sodoko_unlock_change);
        Reset_Passwrod = (RelativeLayout) findViewById(R.id.Reset_Passwrod);
        rl_personal_loginpwd_change = (RelativeLayout) findViewById(R.id.rl_personal_loginpwd_change);
        nameLayout = (TextView) findViewById(R.id.tv_personal_center_no_anthen);
        bankLayout = (TextView) findViewById(R.id.tv_personal_center_no_binding);
        nameIcon = (ImageView) findViewById(R.id.name_icon);
        bankIcon = (ImageView) findViewById(R.id.bank_icon);

        rl_personal_loginpwd_change.setOnClickListener(this);
        Reset_Passwrod.setOnClickListener(this);
        rl_personal_sodoko_unlock_change.setOnClickListener(this);
        needBankLayout.setOnClickListener(this);
        needNameLayout.setOnClickListener(this);
        SaveOrDrawMoney();
    }
    /**
     * 跳转存钱和取钱页面
     */
    private void SaveOrDrawMoney(){
        saveLayout = (TextView) findViewById(R.id.layout_save_money);
        drawLayout = (TextView) findViewById(R.id.layout_draw_money);
        saveLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SafeCenterActivity.this,SaveMoneyActivity.class));
            }
        });
        drawLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SafeCenterActivity.this, SaveMoneyActivity.class));
            }
        });
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()){
            case R.id.rl_personal_anthen://实名认证
                if(nameIcon.getVisibility() == View.VISIBLE){
                    startActivity(new Intent(this, SaveMoneyActivity.class)
                            .putExtra("fromName",true));
                }
                break;
            case R.id.rl_personal_bankcardbinding://绑定银行卡
                if(bankIcon.getVisibility() == View.VISIBLE){
                    startActivity(new Intent(this, SaveMoneyActivity.class)
                            .putExtra("fromBank",true));
                }
                break;
            case R.id.rl_personal_sodoko_unlock_change://设置手势
                if(Utils.isLogined()){
                    intent = new Intent(SafeCenterActivity.this,OpenSudokoUnlockActivity.class);
                    startActivity(intent);
                }else{
                    startActivity(new Intent(SafeCenterActivity.this,LoginActivity.class));
                }
                break;
            case R.id.Reset_Passwrod://修改交易密码
                if(Utils.isLogined()){
                intent = new Intent(SafeCenterActivity.this,TradePwdActivity.class);
                intent.putExtra("isSetTradePwd",true);
                startActivity(intent);
                }else{
                    startActivity(new Intent(SafeCenterActivity.this,LoginActivity.class));
                }
                break;
            case R.id.rl_personal_loginpwd_change://修改登录密码
                if(Utils.isLogined()){
                intent = new Intent(SafeCenterActivity.this,ChangeLoginPwdActivity.class);
                startActivity(intent);
                }else{
                    startActivity(new Intent(SafeCenterActivity.this,LoginActivity.class));
                }
                break;
        }
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
    public void startProcessBar() {
        if(progressDialog == null){
            progressDialog = new ProgressDialog(this,R.style.loading_dialog);
        }else if(progressDialog.isShowing()){
            progressDialog.dismiss();
        }
        progressDialog.show();
    }

    @Override
    public void hideProcessBar() {
        progressDialog.dismiss();
    }

    @Override
    public void showNetError() {
        Utils.makeToast(this, "错误");
    }

    @Override
    public void startNextActivity() {
        //服务器数据交互操作
        HashMap<String,String> maps = new HashMap<>();
        maps.put("user_id",Utils.getUserId());
        maps.put("token",Utils.getToken());
        CustomTask task = new CustomTask(mHandler, Constants.WHAT_ACCOUNT_INFO,
                Constants.URL_ACCOUNT_INFO,
                true,maps,true);
        task.execute();
    }

    private MHandler mHandler = new MHandler();

    private class MHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            try{
                int what = msg.what;
                JsonBean jsonBean = (JsonBean) msg.obj;
                int code = jsonBean.getCode();
                String errorMsg = jsonBean.getError_msg();
                JSONObject json = jsonBean.getJsonObject();
                if(code == Constants.DATA_EVENT){
                    hideProcessBar();
                    Utils.makeToast(SafeCenterActivity.this,Constants.ERROR_MSG_CODE+code);
                    return;
                }
                switch (what){
                    case Constants.WHAT_ACCOUNT_INFO://获取个人信息及安全中心内容
                        LogUtils.i(TAG,"获取个人信息及安全中心内容: "+jsonBean.getJsonString());
                        boolean flag = Utils.filtrateCode(SafeCenterActivity.this, jsonBean);
                        if(flag){
                            if(code == -1) {//未实名认证，未绑卡
                                nameLayout.setText(getResources().getString(R.string.no_anthen));
                                bankLayout.setText(getResources().getString(R.string.no_binding));
                                nameIcon.setVisibility(View.VISIBLE);
                                bankIcon.setVisibility(View.VISIBLE);
                            }else if(code == -2){//未真正实名 未绑卡
                                nameLayout.setText(Html.fromHtml(
                                        Utils.getUserNameForStart(json.optString("starName")) + "<br/>"
                                                + Utils.getUserIdentity(json.optString("starIdentity"))));
                                bankLayout.setText(getResources().getString(R.string.no_binding));
                                nameIcon.setVisibility(View.VISIBLE);
                                bankIcon.setVisibility(View.VISIBLE);
                            }else if(code == -3){//未真正实名认证 已绑卡
                                nameLayout.setText(Html.fromHtml(
                                        Utils.getUserNameForStart(json.optString("starName")) + "<br/>"
                                                + Utils.getUserIdentity(json.optString("starIdentity"))));
                                bankLayout.setText(Html.fromHtml(
                                        json.optString("bankName") + "<br/>"
                                                + Utils.getBankStart(json.optString("starCardNumber"))));
                                nameIcon.setVisibility(View.VISIBLE);
                                bankIcon.setVisibility(View.GONE);
                                needBankLayout.setEnabled(false);
                            }else if(code == -4){//已真正实名认证，未绑卡
                                nameLayout.setText(Html.fromHtml(
                                        Utils.getUserNameForStart(json.optString("starName")) + "<br/>"
                                                + Utils.getUserIdentity(json.optString("starIdentity"))));
                                bankLayout.setText(getResources().getString(R.string.no_binding));
                                nameIcon.setVisibility(View.GONE);
                                bankIcon.setVisibility(View.VISIBLE);
                                needNameLayout.setEnabled(false);
                            }else if(code == -5){//已真正实名 已绑卡但是不支持连连
                                nameLayout.setText(Html.fromHtml(
                                        Utils.getUserNameForStart(json.optString("starName")) + "<br/>"
                                                + Utils.getUserIdentity(json.optString("starIdentity"))));
                                bankLayout.setText(Html.fromHtml(
                                        json.optString("bankName") + "<br/>"
                                                + Utils.getBankStart(json.optString("starCardNumber"))));
                                nameIcon.setVisibility(View.GONE);
                                bankIcon.setVisibility(View.VISIBLE);
                                needNameLayout.setEnabled(false);
                            }else if(code == -6){//未真正实名 已绑卡 支持但未绑定连连
                                nameLayout.setText(Html.fromHtml(
                                        Utils.getUserNameForStart(json.optString("starName")) + "<br/>"
                                                + Utils.getUserIdentity(json.optString("starIdentity"))));
                                bankLayout.setText(Html.fromHtml(
                                        json.optString("bankName") + "<br/>"
                                                + Utils.getBankStart(json.optString("starCardNumber"))));
                                nameIcon.setVisibility(View.VISIBLE);
                                bankIcon.setVisibility(View.VISIBLE);
                            }else if(code == -7){//已真正实名认证 已绑定连连 不可修改
                                nameLayout.setText(Html.fromHtml(
                                        Utils.getUserNameForStart(json.optString("starName")) + "<br/>"
                                                + Utils.getUserIdentity(json.optString("starIdentity"))));
                                bankLayout.setText(Html.fromHtml(
                                        json.optString("bankName") + "<br/>"
                                                + Utils.getBankStart(json.optString("starCardNumber"))));
                                nameIcon.setVisibility(View.GONE);
                                bankIcon.setVisibility(View.GONE);
                                needNameLayout.setEnabled(false);
                                needBankLayout.setEnabled(false);
                            }else if(code == -8){//已真正实名认证 支持连连但未绑定连连
                                nameLayout.setText(Html.fromHtml(
                                        Utils.getUserNameForStart(json.optString("starName")) + "<br/>"
                                                + Utils.getUserIdentity(json.optString("starIdentity"))));
                                bankLayout.setText(Html.fromHtml(
                                        json.optString("bankName") + "<br/>"
                                                + Utils.getBankStart(json.optString("starCardNumber"))));
                                nameIcon.setVisibility(View.GONE);
                                bankIcon.setVisibility(View.VISIBLE);
                                needNameLayout.setEnabled(false);
                            }
                            hideProcessBar();
                        }else{
                            Utils.makeToast(SafeCenterActivity.this,errorMsg);
                            hideProcessBar();
                        }
                        break;
                }
            }catch (Exception e){
                hideProcessBar();
                LogUtils.e(TAG,"错误信息："+e.toString());
            }
        }
    }
}
