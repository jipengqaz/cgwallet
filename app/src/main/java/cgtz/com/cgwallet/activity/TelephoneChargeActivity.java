package cgtz.com.cgwallet.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

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
 * Created by 朋 on 2015/12/23.
 */
public class TelephoneChargeActivity extends BaseActivity implements View.OnClickListener,ISplashView {
    private LinearLayout iv_pay;
    private ProgressDialog progressDialog;
    private SplashPresenter presenter;
    private static Context context;
    private static ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showBack(true);
        setTitle("充值");
        MApplication.registActivities(this);
        setContentView(R.layout.activity_telephone_chaarge);
        presenter = new SplashPresenter(this);
       // presenter.didFinishLoading(this);
         iv_pay = (LinearLayout) findViewById(R.id.iv_pay);
        iv_pay.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_pay:
                presenter.didFinishLoading(this);
                Payment_money(TelephoneChargeActivity.this, Constants.WHAT_WITHDRAW, progressDialog);

                break;
        }
    }

    /**
     * 进入支付界面 的判断接口
     *
     * @param code
     */
    public void Payment_money(Context contextPay, int code, ProgressDialog dialogPay) {
        context = contextPay;
        dialog = dialogPay;
        Map<String, String> map = new HashMap<>();
        map.put("user_id", Utils.getUserId() + "");
        map.put("token", Utils.getToken() + "");
        CustomTask task = new CustomTask(paymentHandler, code,
                Constants.URL_E_WALLET_REDEEM,
                true, map, true);
        task.execute();
    }

    private Handler paymentHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            try {
                JsonBean jsonBean = (JsonBean) msg.obj;
                if (!Utils.filtrateCode(context, jsonBean)) {
                    return;
                }
                JSONObject json = jsonBean.getJsonObject();
                int success = json.optInt("success");
                if (success == -7) {//还没设置交易密码
                    Utils.makeToast(context,
                            context.getString(R.string.error_msg_notradepwd));
                    context.startActivity(new Intent(context, TradePwdActivity.class)
                            .putExtra("isSetTradePwd", false));
                }else{
                    LogUtils.e("<<<<<<<密码<<<<<<", "已设置交易密码");
                    Intent intent = new Intent(TelephoneChargeActivity.this,PayMoneyActivity.class);
                    startActivity(intent);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    public void startProcessBar() {
        if(progressDialog == null){
            progressDialog = new ProgressDialog(TelephoneChargeActivity.this, R.style.loading_dialog);
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

    }

    @Override
    public void startNextActivity() {

    }
}
