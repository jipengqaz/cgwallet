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

import java.util.HashMap;

import cgtz.com.cgwallet.R;
import cgtz.com.cgwallet.presenter.SplashPresenter;
import cgtz.com.cgwallet.utility.Constants;
import cgtz.com.cgwallet.utils.CustomTask;
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
        CustomTask task = new CustomTask(mHandler, Constants.WHAT_GET_SECURITY_CODE
                ,Constants.URL_GET_SECURITY_CODE,
                true,params,true);
        task.execute();
    }

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {

        }
    };
}
