package cgtz.com.cgwallet.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.HashMap;

import cgtz.com.cgwallet.MApplication;
import cgtz.com.cgwallet.R;
import cgtz.com.cgwallet.activity.E_wallet_record_activity;
import cgtz.com.cgwallet.activity.Earnings_record;
import cgtz.com.cgwallet.activity.LoginActivity;
import cgtz.com.cgwallet.activity.SaveMoneyActivity;
import cgtz.com.cgwallet.bean.JsonBean;
import cgtz.com.cgwallet.client.Withdraw_money_Client;
import cgtz.com.cgwallet.presenter.SplashPresenter;
import cgtz.com.cgwallet.utility.Constants;
import cgtz.com.cgwallet.utils.CustomTask;
import cgtz.com.cgwallet.utils.LogUtils;
import cgtz.com.cgwallet.utils.Utils;
import cgtz.com.cgwallet.view.ISplashView;
import cgtz.com.cgwallet.widget.ProgressDialog;

/**
 * 我的钱包
 * Created by Administrator on 2015/4/11.
 */
public class MyWalletFragment extends BaseFragment implements ISplashView,View.OnClickListener{
    private static final String TAG = "MyWalletFragment";
    private LinearLayout layoutAuther;//实名认证layout
    private LinearLayout layoutBank;//绑定银行卡layout
    private LinearLayout layoutDraw;//取钱layout
    private LinearLayout layoutSave;//存钱layout
    private LinearLayout layoutDrawRecord;//取钱记录layout
    private LinearLayout layoutSaveRecord;//存钱记录layout
    private TextView walletEarnings;//今日收益
    private LinearLayout assetsLayout;//我的资产layout
    private TextView walletAssets;//我的资产
    private LinearLayout accumulativeLayout;//累计收益layout
    private TextView walletAccumulative;//累计收益
    private TextView walletIdentity;//身份证号
    private TextView walletBankCord;//银行卡号
    private TextView walletNoIdentity;//未实名认证
    private TextView walletNoBank;//未绑卡
    private ProgressDialog progressDialog;
    private SplashPresenter presenter;
    private String earnings;//今日收益
    private String assets;//我的资产
    private String accumulative;//累计收益
    private String identity;//身份证号
    private String bankCord;//银行卡号
    private int screenWidth;
    private boolean goLogin = false;//判断是否去登录的标志
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        screenWidth = getResources().getDisplayMetrics().widthPixels;
        presenter = new SplashPresenter(this);
        View layoutView = inflater.inflate(R.layout.layout_my_wallet,container,false);
        initViews(layoutView);
        setWidgetAttrs();
        setListener();
        return layoutView;
    }

    /**
     * 初始化widget
     * @param view
     */
    private void initViews(View view){
        layoutAuther = (LinearLayout) view.findViewById(R.id.layout_auther);//实名认证
        layoutBank = (LinearLayout) view.findViewById(R.id.layout_bank);//绑定银行卡
        layoutDraw = (LinearLayout) view.findViewById(R.id.layout_draw);//取钱
        layoutSave = (LinearLayout) view.findViewById(R.id.layout_save);//存钱
        layoutDrawRecord = (LinearLayout) view.findViewById(R.id.layout_draw_record);//取钱记录
        layoutSaveRecord = (LinearLayout) view.findViewById(R.id.layout_save_record);//存钱记录
        walletEarnings = (TextView) view.findViewById(R.id.wallet_earnings);//今日收益
        assetsLayout = (LinearLayout) view.findViewById(R.id.wallet_assets_layout);//我的资产layout
        walletAssets = (TextView) view.findViewById(R.id.wallet_my_assets);//我的资产
        accumulativeLayout = (LinearLayout) view.findViewById(R.id.wallet_accumulative_layout);//累计收益layout
        walletAccumulative = (TextView) view.findViewById(R.id.wallet_accumulative_earnings);//累计收益
        walletIdentity = (TextView) view.findViewById(R.id.wallet_identity);//身份证号
        walletBankCord = (TextView) view.findViewById(R.id.wallet_bank_card);//银行卡号
        walletNoIdentity = (TextView) view.findViewById(R.id.wallet_no_identity);//未实名认证
        walletNoBank = (TextView) view.findViewById(R.id.wallet_no_bank);//未绑卡
    }

    /**
     * 个别widget适配
     */
    private void setWidgetAttrs(){
        LinearLayout.LayoutParams autherParams = (LinearLayout.LayoutParams) layoutAuther.getLayoutParams();
        autherParams.width = screenWidth/2;
        layoutAuther.setLayoutParams(autherParams);
        LinearLayout.LayoutParams bankParams = (LinearLayout.LayoutParams) layoutBank.getLayoutParams();
        bankParams.width = screenWidth/2;
        layoutBank.setLayoutParams(bankParams);
        LinearLayout.LayoutParams drawParams = (LinearLayout.LayoutParams) layoutDraw.getLayoutParams();
        drawParams.width = screenWidth/2;
        layoutDraw.setLayoutParams(drawParams);
        LinearLayout.LayoutParams drawRecordParams = (LinearLayout.LayoutParams) layoutDrawRecord.getLayoutParams();
        drawRecordParams.width = screenWidth/2;
        layoutDrawRecord.setLayoutParams(drawRecordParams);
        LinearLayout.LayoutParams saveParams = (LinearLayout.LayoutParams) layoutSave.getLayoutParams();
        saveParams.width = screenWidth/2;
        layoutSave.setLayoutParams(saveParams);
        LinearLayout.LayoutParams saveRecordParams = (LinearLayout.LayoutParams) layoutSaveRecord.getLayoutParams();
        saveRecordParams.width = screenWidth/2;
        layoutSaveRecord.setLayoutParams(saveRecordParams);
    }

    /**
     * widget设置事件
     */
    private void setListener(){
        layoutAuther.setOnClickListener(this);//实名认证
        layoutBank.setOnClickListener(this);//绑定银行卡
        layoutDraw.setOnClickListener(this);//取钱
        layoutSave.setOnClickListener(this);//存钱
        layoutDrawRecord.setOnClickListener(this);//取钱记录
        layoutSaveRecord.setOnClickListener(this);//存钱记录
        assetsLayout.setOnClickListener(this);//我的资产layout
        accumulativeLayout.setOnClickListener(this);//累计收益layout
        walletAccumulative.setOnClickListener(this);//累计收益
    }

    /**
     * widght填充数据
     */
    private void fillViews(){
        walletEarnings.setText(earnings);//今日收益
        walletAssets.setText(assets);//我的资产
        walletAccumulative.setText(accumulative);//累计收益
        if(TextUtils.isEmpty(identity)){
            walletNoIdentity.setVisibility(View.GONE);
            walletIdentity.setText("未实名认证");//未实名认证
        }else{
            walletNoIdentity.setVisibility(View.VISIBLE);
            walletIdentity.setText(identity);//身份证号
        }
        if(TextUtils.isEmpty(bankCord)){
            walletNoBank.setVisibility(View.GONE);
            walletBankCord.setText("未绑卡");//未绑卡
        }else{
            walletNoBank.setVisibility(View.VISIBLE);
            walletBankCord.setText(bankCord);//银行卡号
        }
    }

    public void setData(boolean flag){
        if(flag){
            presenter.didFinishLoading(getActivity());
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(getUserVisibleHint()){
            setData(true);
        }else{
            goLogin = false;
            setData(false);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setData(goLogin && MApplication.goLogin);
    }

    @Override
    public void startProcessBar() {
        if(progressDialog == null){
            progressDialog = new ProgressDialog(getActivity(),R.style.loading_dialog);
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
        Utils.makeToast(getActivity(), Constants.IS_EVENT_MSG);
    }

    @Override
    public void startNextActivity() {
        if(TextUtils.isEmpty(Utils.getUserId()) || TextUtils.isEmpty(Utils.getToken())){//判断是否登录
            goLogin = true;
            hideProcessBar();
            Utils.makeToast(getActivity(),Constants.NEED_LOGIN);
            startActivity(new Intent(getActivity(), LoginActivity.class));
        }else{
            HashMap<String,String> params = new HashMap<>();
            params.put("user_id", Utils.getUserId());
            params.put("token", Utils.getToken());
            CustomTask task = new CustomTask(mHandler, Constants.WHAT_WALLET_DETAIL
                    ,Constants.URL_WALLET_DETAIL,true,params,true);
            task.execute();
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.layout_auther://实名认证
                if(TextUtils.isEmpty(identity)){
                    startActivity(new Intent(getActivity(), SaveMoneyActivity.class)
                    .putExtra("fromName",true));
                }
                break;
            case R.id.layout_bank://绑定银行卡
                if(TextUtils.isEmpty(bankCord)){
                    startActivity(new Intent(getActivity(), SaveMoneyActivity.class)
                            .putExtra("fromBank",true));
                }
                break;
            case R.id.layout_draw://取钱
                if(Utils.isLogined()){
                    Withdraw_money_Client.getWithdraw_money(getActivity(), Constants.WHAT_WITHDRAW, progressDialog);
                }else{
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                }
                break;
            case R.id.layout_save://存钱
                if(Utils.isLogined()){
                    startActivity(new Intent(getActivity(), SaveMoneyActivity.class));
                }else{
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                }
                break;
            case R.id.layout_draw_record://取钱记录
                startActivity(new Intent(getActivity(),E_wallet_record_activity.class));
                break;
            case R.id.layout_save_record://存钱记录
                startActivity(new Intent(getActivity(),E_wallet_record_activity.class));
                break;
            case R.id.wallet_assets_layout://我的资产layout
                startActivity(new Intent(getActivity(),E_wallet_record_activity.class));
                break;
            case R.id.wallet_accumulative_layout://累计收益layout
                startActivity(new Intent(getActivity(), Earnings_record.class));
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
                    Utils.makeToast(getActivity(),Constants.ERROR_MSG_CODE+code);
                    return;
                }
                int action = msg.what;
                switch (action){
                    case Constants.WHAT_WALLET_DETAIL:
                        boolean flag = Utils.filtrateCode(getActivity(),jsonBean);
                        if(flag && code == Constants.OPERATION_FAIL){//数据交互失败
                            Utils.makeToast(getActivity(), errorMsg);
                        }else if(flag && code == Constants.OPERATION_SUCCESS){//数据交互成功
                            JSONObject jsonObject = new JSONObject(jsonBean.getJsonString());
                            earnings = jsonObject.optString("todayInterest");//今日收益
                            assets = jsonObject.optString("eTotal");//我的资产
                            accumulative = jsonObject.optString("interestTotal");//累计收益
                            identity = jsonObject.optString("starIdentity");//身份证号
                            bankCord = jsonObject.optString("starCardNumber");//银行卡号
                            fillViews();
                        }
                        hideProcessBar();
                        break;
                }
            }catch (Exception e){
                e.printStackTrace();
                hideProcessBar();
                LogUtils.e(TAG,"handler 异常");
            }
        }
    };
}
