package cgtz.com.cgwallet.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.HashMap;

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
 * 存钱页面
 */
public class SaveMoneyActivity extends BaseActivity implements ISplashView{
    private static final String TAG = "SaveMoneyActivity";
    private String assets;//账户余额
    private RelativeLayout assetsLayout;//账户余额布局
    private TextView assetsFigure;//账户余额数字
    private LinearLayout transferHintLayout;//
    private TextView transferHint;//账户余额使用介绍
    private EditText editFigure;//输入金额
    private ImageView deleteEdit;//删除输入内容
    private TextView incomeTimeHint;//收益开始时间
    private TextView safeCopyWrite;//安全文案
    private Button confirmSave;//确认存钱按钮
    private ProgressDialog progressDialog;
    private SplashPresenter presenter;
    private String saveMoney;//输入的存钱金额
    private String assetUseIntruduce;////余额的使用介绍
    private String startCalculateTime;//计算收益时间
    private String minSaveMoney;//起投金额

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_money);
        setTitle("存钱");
        showBack(true);
        presenter = new SplashPresenter(this);
        initViews();
        setListener();
    }

    /**
     * 初始化widget
     */
    private void initViews(){
        assetsLayout = (RelativeLayout) findViewById(R.id.save_money_my_assets);//账户余额布局
        assetsFigure = (TextView) findViewById(R.id.save_money_assets);//账户余额数字
        transferHintLayout = (LinearLayout) findViewById(R.id.save_money_transfer_hint);//
        transferHint = (TextView) findViewById(R.id.save_money_assets_use);//账户余额使用介绍
        editFigure = (EditText) findViewById(R.id.et_save_figure);//输入金额
        deleteEdit = (ImageView) findViewById(R.id.delete_edit);//删除输入内容
        incomeTimeHint = (TextView) findViewById(R.id.income_time_hint);//收益开始时间
        safeCopyWrite = (TextView) findViewById(R.id.safe_copywrite_hint);//安全文案
        confirmSave = (Button) findViewById(R.id.cofirm_save);//确认存钱按钮
    }

    /**
     * 填充widget内容
     */
    private void fillWidget(){
        if(TextUtils.isEmpty(assets)){
            assetsLayout.setVisibility(View.GONE);
            transferHintLayout.setVisibility(View.GONE);
        }else{
            assetsLayout.setVisibility(View.VISIBLE);
            transferHintLayout.setVisibility(View.VISIBLE);
            assetsFigure.setText(assets + " 元");
            transferHint.setText(assetUseIntruduce);
        }
        if(!TextUtils.isEmpty(startCalculateTime)){
            incomeTimeHint.setText(startCalculateTime);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.didFinishLoading(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    /**
     * widget添加事件
     */
    private void setListener(){
        deleteEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editFigure.setText("");
            }
        });
        /**
         * 输入框监听事件
         */
        editFigure.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                /**
                 * 根据输入框中是否存在输入内容，来改变按钮的颜色和是否可点击
                 */
                if(TextUtils.isEmpty(s.toString().trim())){
                    //按钮变为不可点击
                    confirmSave.setEnabled(false);
                    confirmSave.setBackgroundResource(R.drawable.bg_button_no_enabled);
                }else{
                    //按钮变为可点击
                    confirmSave.setEnabled(true);
                    confirmSave.setBackgroundColor(getResources().getColor(R.color.button_text_can_click));
                }
            }
        });
        /**
         * 存入操作
         */
        confirmSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

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
        params.put("user_id",Utils.getUserId());
        params.put("token",Utils.getToken());
        CustomTask task = new CustomTask(mHandler, Constants.WHAT_WALLET_DEPOSIT
                ,Constants.URL_WALLET_DEPOSIT,
                true,params,true);
        task.execute();
    }

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            try{
                JsonBean jsonBean = (JsonBean) msg.obj;
                String objStr = jsonBean.getJsonString();
                LogUtils.i(TAG,"数据："+objStr);
                int code = jsonBean.getCode();
                String errorMsg = jsonBean.getError_msg();
                if(code == Constants.DATA_EVENT){
                    hideProcessBar();
                    Utils.makeToast(SaveMoneyActivity.this,Constants.ERROR_MSG_CODE+code);
                    return;
                }
                int action = msg.what;
                hideProcessBar();
                switch (action){
                    case Constants.WHAT_WALLET_DEPOSIT:
                        boolean flag = Utils.filtrateCode(SaveMoneyActivity.this,jsonBean);
                        if(flag && code == Constants.OPERATION_FAIL){//数据交互失败
                            Utils.makeToast(SaveMoneyActivity.this, errorMsg);
                        }else if(flag && code == Constants.OPERATION_SUCCESS){//数据交互成功
                            JSONObject jsonObject = new JSONObject(jsonBean.getJsonString());
                            assets = jsonObject.optString("capitalAccountBalance");//账户余额
                            assetUseIntruduce = jsonObject.optString("tip");////余额的使用介绍
                            startCalculateTime = jsonObject.optString("startInterestDay");//计算收益时间
                            minSaveMoney = jsonObject.optString("min");//起投金额
                            fillWidget();
                        }
                        break;
                }
            }catch (Exception e){
                e.printStackTrace();
                hideProcessBar();
                LogUtils.e(TAG, "handler 异常");
            }
        }
    };
}
