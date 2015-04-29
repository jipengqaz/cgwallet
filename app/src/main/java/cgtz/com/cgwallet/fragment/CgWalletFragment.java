package cgtz.com.cgwallet.fragment;

import android.app.Dialog;
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import cgtz.com.cgwallet.R;
import cgtz.com.cgwallet.activity.LoginActivity;
import cgtz.com.cgwallet.activity.SaveMoneyActivity;
import cgtz.com.cgwallet.activity.WebViewActivity;
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
 * 草根钱包
 * Created by Administrator on 2015/4/11.
 */
public class CgWalletFragment extends BaseFragment implements ISplashView,View.OnClickListener{
    private static final String TAG = "CgWalletFragment";
    private ProgressDialog progressDialog;
    private SplashPresenter presenter;
    private String rate;//(草根钱包利率)
    private ArrayList<String> linkName;//（特色描述）
    private ArrayList<String> linkUrl;//(特色链接)
    private TextView rateOfInteger;//利率的整数部分
    private TextView rateOfDecimal;//利率的小数部分
    private LinearLayout drawLayout;//取钱
    private LinearLayout saveLayout;//存钱
    private LinearLayout linkLayout1;//第一个特色布局
    private TextView linkName1;//第一个特色文案
    private LinearLayout linkLayout2;//第二个特色布局
    private TextView linkName2;//第二个特色文案
    private LinearLayout linkLayout3;//第三个特色布局
    private TextView linkName3;//第三个特色文案
    private String link_name;//获取的特色文案
    private String link_url;//获取的特色路径

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LogUtils.e(TAG, "onCreateView");
        presenter = new SplashPresenter(this);
        View layoutView = inflater.inflate(R.layout.layout_cg_wallet, container, false);
        initViews(layoutView);
        setListener();
        return layoutView;
    }

    /**
     * 初始化布局widght
     * @param view
     */
    private void initViews(View view){
        drawLayout = (LinearLayout) view.findViewById(R.id.layout_draw);//取钱
        saveLayout = (LinearLayout) view.findViewById(R.id.layout_save);//存钱
        rateOfInteger = (TextView) view.findViewById(R.id.cg_wallet_rate_integer);//利率的整数部分
        rateOfDecimal = (TextView) view.findViewById(R.id.cg_wallet_rate_decimal);//利率的小数部分
        linkLayout1 = (LinearLayout) view.findViewById(R.id.cg_wallet_link1);//第一个特色布局
        linkName1 = (TextView) view.findViewById(R.id.cg_wallet_link_name1);//第一个特色文案
        linkLayout2 = (LinearLayout) view.findViewById(R.id.cg_wallet_link2);//第二个特色布局
        linkName2 = (TextView) view.findViewById(R.id.cg_wallet_link_name2);//第二个特色文案
        linkLayout3 = (LinearLayout) view.findViewById(R.id.cg_wallet_link3);//第三个特色布局
        linkName3 = (TextView) view.findViewById(R.id.cg_wallet_link_name3);//第三个特色文案
        setData();
    }


    /**
     * 给widget添加事件
     */
    private void setListener(){
        drawLayout.setOnClickListener(this);//取钱
        saveLayout.setOnClickListener(this);//存钱
        linkLayout1.setOnClickListener(this);//第一个特色布局
        linkLayout2.setOnClickListener(this);//第二个特色布局
        linkLayout3.setOnClickListener(this);//第三个特色布局
    }

    /**
     * 填充数据
     */
    private void fillViews(){
        rateOfInteger.setText(rate.substring(0, rate.indexOf(".")));//利率的整数部分
        rateOfDecimal.setText(rate.substring(rate.indexOf(".")));//利率的小数部分
        linkName1.setText(linkName.get(0));//第一个特色文案
        linkName2.setText(linkName.get(1));//第二个特色文案
        linkName3.setText(linkName.get(2));//第三个特色文案
    }

    /**
     * 获取页面数据
     */
    public void setData(){
        presenter.didFinishLoading(getActivity());
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
        CustomTask task = new CustomTask(mHandler, Constants.WHAT_WALLET_MAIN
                ,Constants.OFFLINE_HTTP,false,null,false);
        task.execute();
    }

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(progressDialog.isShowing()){
                progressDialog.dismiss();
            }
            try{
                JsonBean jsonBean = (JsonBean) msg.obj;
                int code = jsonBean.getCode();
                String errorMsg = jsonBean.getError_msg();
                if(!Utils.filtrateCode(getActivity(),jsonBean)){
                    Utils.makeToast(getActivity(), errorMsg + "  错误码" + code);
                    return;
                }
                int action = msg.what;
                switch (action){
                    case Constants.WHAT_WALLET_MAIN:
                        boolean flag = Utils.filtrateCode(getActivity(),jsonBean);
                        if(flag && code == Constants.OPERATION_FAIL){//数据交互失败
                            Utils.makeToast(getActivity(), errorMsg);
                        }else if(flag && code == Constants.OPERATION_SUCCESS){//数据交互成功
                            JSONObject jsonObject = new JSONObject(jsonBean.getJsonString());
                            rate = jsonObject.optString("rate");//(草根钱包利率)
                            JSONArray feature = jsonObject.getJSONArray("feature");//(草根特色)
                            linkName = new ArrayList<>();//
                            linkUrl = new ArrayList<>();//
                            int size = feature.length();
                            JSONObject object;
                            for(int i=0;i<size;i++){
                                object = feature.getJSONObject(i);
                                linkName.add(object.optString("linkName"));//（特色描述）
                                linkUrl.add(object.optString("linkUrl"));//特色链接)
                            }
                            fillViews();
                        }
                        hideProcessBar();
                        break;
                }
            }catch (Exception e){
                e.printStackTrace();
                hideProcessBar();
            }
        }
    };

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.layout_draw://取钱
                if(progressDialog ==null){
                    progressDialog = new ProgressDialog(getActivity());
                }
                progressDialog.show();
                Withdraw_money_Client.getWithdraw_money(getActivity(),Constants.WHAT_WITHDRAW,progressDialog);
                break;
            case R.id.layout_save://存钱
                if(Utils.isLogined()){
                    startActivity(new Intent(getActivity(), SaveMoneyActivity.class));
                }else{
                    Utils.makeToast(getActivity(),"请先登录");
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                }

                break;
            case R.id.cg_wallet_link1://第一个特色布局
                link_name = null;//获取的特色文案
                link_url = null;//获取的特色路径
                link_name = linkName.get(0);//获取的特色文案
                link_url = linkUrl.get(0);//获取的特色路径
                if(TextUtils.isEmpty(link_url)){

                }else{
                    startActivity(new Intent(getActivity(), WebViewActivity.class)
                            .putExtra("url", link_url)
                            .putExtra("title", link_name));
                }
                break;
            case R.id.cg_wallet_link2://第二个特色布局
                link_name = null;//获取的特色文案
                link_url = null;//获取的特色路径
                link_name = linkName.get(1);//获取的特色文案
                link_url = linkUrl.get(1);//获取的特色路径
                if(TextUtils.isEmpty(link_url)){

                }else{
                    startActivity(new Intent(getActivity(), WebViewActivity.class)
                            .putExtra("url", link_url)
                            .putExtra("title", link_name));
                }
                break;
            case R.id.cg_wallet_link3://第三个特色布局
                link_name = null;//获取的特色文案
                link_url = null;//获取的特色路径
                link_name = linkName.get(2);//获取的特色文案
                link_url = linkUrl.get(2);//获取的特色路径
                if(TextUtils.isEmpty(link_url)){

                }else{
                    startActivity(new Intent(getActivity(), WebViewActivity.class)
                            .putExtra("url", link_url)
                            .putExtra("title", link_name));
                }
                break;
        }
    }
}
