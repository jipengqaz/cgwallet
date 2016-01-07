package cgtz.com.cgwallet.activity;

import android.content.Intent;
import android.os.Bundle;
/*import android.os.Handler;
import android.os.Message;*/
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

/*
import org.json.JSONObject;

import java.util.HashMap;
*/


import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;

import cgtz.com.cgwallet.R;
/*import cgtz.com.cgwallet.bean.JsonBean;
import cgtz.com.cgwallet.utility.Constants;
import cgtz.com.cgwallet.utils.CustomTask;
import cgtz.com.cgwallet.utils.LogUtils;
import cgtz.com.cgwallet.utils.Utils;*/
import cgtz.com.cgwallet.bean.JsonBean;
import cgtz.com.cgwallet.presenter.SplashPresenter;
import cgtz.com.cgwallet.utility.Constants;
import cgtz.com.cgwallet.utils.CustomTask;
import cgtz.com.cgwallet.utils.LogUtils;
import cgtz.com.cgwallet.utils.Utils;
import cgtz.com.cgwallet.view.ISplashView;
import cgtz.com.cgwallet.widget.ProgressDialog;

/**
 * Created by chen on 2015-10-29.收益明细界面
 */
public class EarningsDetailActivity extends BaseActivity implements View.OnClickListener, ISplashView {

    private TextView interone;//利息明细1
    private TextView balone;//对应本金1
    private TextView rateone;//利率1
    private TextView intertwo;//利息明细2
    private TextView baltwo;//对应本金2
    private TextView ratetwo;//利率2
    private LinearLayout llinter;//第二行的布局
    private TextView tvdata;//日期
    private TextView tvmoney;//当前利息
    private String data;//当前条目的日期
    private String money;//当前条目的利息
    private SplashPresenter presenter;
    private ProgressDialog progressDialog;
    private LinearLayout llinterThree;//第三行布局
    private LinearLayout llinterFour;//第四行布局
    private LinearLayout llinterFive;//第五行布局
    private TextView interthree, balthree, ratethree;
    private TextView interfour, balfour, ratefour;
    private TextView interfive, balfive, ratefive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showBack(true);
        setTitle("收益明细");
        setContentView(R.layout.activity_earningsdetail);
        presenter = new SplashPresenter(this);
//        拿到Earnings_record界面传递过来的时间和利息
        Intent intent = getIntent();
        data = intent.getStringExtra("data");
//        LogUtils.e("--------有没有拿到该条目的时间---------","data："+data);
        money = intent.getStringExtra("money");
//        LogUtils.e("--------有没有拿到该条目的利息--------", "money：" + money);
        init();
        presenter.didFinishLoading(EarningsDetailActivity.this);
    }

    public void init() {
        llinter = (LinearLayout) findViewById(R.id.ll_inter);//第二行布局
        llinterThree = (LinearLayout) findViewById(R.id.ll_inter_three);//第三行布局
        llinterFour = (LinearLayout) findViewById(R.id.ll_inter_four);//第四行布局
        llinterFive = (LinearLayout) findViewById(R.id.ll_inter_five);//第五行布局
        interone = (TextView) findViewById(R.id.tv_inter_one);//利息明细1
        balone = (TextView) findViewById(R.id.tv_bal_one);//对应本金1
        rateone = (TextView) findViewById(R.id.tv_rate_one);//利率1
        intertwo = (TextView) findViewById(R.id.tv_inter_two);//利息明细2
        baltwo = (TextView) findViewById(R.id.tv_bal_two);//对应本金2
        ratetwo = (TextView) findViewById(R.id.tv_rate_two);//利率2
        interthree = (TextView) findViewById(R.id.tv_inter_three);//利息明细3
        balthree = (TextView) findViewById(R.id.tv_bal_three);//对应本金3
        ratethree = (TextView) findViewById(R.id.tv_rate_three);//利率3
        interfour = (TextView) findViewById(R.id.tv_inter_four);//利息明细4
        balfour = (TextView) findViewById(R.id.tv_bal_four);//对应本金4
        ratefour = (TextView) findViewById(R.id.tv_rate_four);//利率4
        interfive = (TextView) findViewById(R.id.tv_inter_five);//利息明细5
        balfive = (TextView) findViewById(R.id.tv_bal_five);//对应本金5
        ratefive = (TextView) findViewById(R.id.tv_rate_five);//利率5
        tvdata = (TextView) findViewById(R.id.tv_data);//日期
        tvdata.setText(data);
        tvmoney = (TextView) findViewById(R.id.tv_money);//当前利息
        tvmoney.setText(money);
    }

    //获取收益详情的数据交互
    public static void getEarningsDetail(Handler mHandler, String date, int page) {
        //服务器数据交互操作
        HashMap<String, String> maps = new HashMap<>();
        maps.put("user_id", Utils.getUserId());
        maps.put("token", Utils.getToken());
        maps.put("date", date);
        maps.put("page", page + "");
        CustomTask task = new CustomTask(mHandler, Constants.WHAT_QUERY_INTERESTDETAIL,
                Constants.SEAWAY_QUERY_INTERESTDETAIL,
                true, maps, true);
        task.execute();
    }

    private Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            try {
                JsonBean jsonBean = (JsonBean) msg.obj;
                int code = jsonBean.getCode();
                String errorMsg = jsonBean.getError_msg();
                if (code == Constants.DATA_EVENT) {
                    hideProcessBar();
//                    Utils.makeToast(EarningsDetailActivity.this, Constants.ERROR_MSG_CODE + code);
                    return;
                }
                int action = msg.what;
                switch (action) {
                    case Constants.WHAT_QUERY_INTERESTDETAIL:
                        boolean flag = Utils.filtrateCode(EarningsDetailActivity.this, jsonBean);
                        if (flag && code == Constants.OPERATION_FAIL) {//数据交互失败
//                            Utils.makeToast(EarningsDetailActivity.this, errorMsg);
                        } else if (flag && code == Constants.OPERATION_SUCCESS) {//数据交互成功
//                            JSONObject jsonObject = new JSONObject(jsonBean.getJsonString());
                            JSONObject json = jsonBean.getJsonObject();
                            JSONObject result = json.optJSONObject("result");
//                            LogUtils.e("Myresult", "result:" + result);
                            JSONArray pageList = result.getJSONArray("pageList");//拿到"pageList"字段
//                            interone.setText(pageList.getJSONObject(0).optString("interest"));
                            String interestStr = pageList.getJSONObject(0).optString("interest");
                            BigDecimal interest = new BigDecimal(interestStr).setScale(5);
                            interone.setText(interest.toPlainString());

                            String balanceStr = pageList.getJSONObject(0).optString("balance");
                            LogUtils.e("------------------bbbbbbb-----------------","balanceStr:"+balanceStr);
                            BigDecimal balance = new BigDecimal(balanceStr).setScale(2);
                            balone.setText(balance.toPlainString());
//                            rateone.setText(pageList.getJSONObject(0).getString("annualizedRate"));
                            String rate = pageList.getJSONObject(0).optString("annualizedRate");
                            final BigDecimal decimal100 = new BigDecimal(100);
//2016年1月6日17:45:39  错误方法    String newRate = rate.substring(rate.length() - 1, rate.length());
//格式化数据 去掉数据末尾没用的0     NumberFormat.getInstance().format(值)
//用BigDecimal的方式去给他乘以100   new BigDecimal(rate).multiply(decimal100)
                            rateone.setText(NumberFormat.getInstance().format(new BigDecimal(rate).multiply(decimal100)) + "%");
                            if (pageList.length() >= 2) {//目前用的方法，如果有第二条数据，就显示第二行的布局
                                llinter.setVisibility(View.VISIBLE);
                                String interestTwoStr = pageList.getJSONObject(1).optString("interest");
                                BigDecimal interestTwo = new BigDecimal(interestTwoStr).setScale(5);
                                intertwo.setText(interestTwo.toPlainString());
                                String balanceTwoStr = pageList.getJSONObject(1).optString("balance");
                                BigDecimal balanceTwo = new BigDecimal(balanceTwoStr).setScale(2);
                                baltwo.setText(balanceTwo.toPlainString());
                                String rate1 = pageList.getJSONObject(1).optString("annualizedRate");
//                                String newRate1 = rate1.substring(rate1.length() - 1, rate1.length());
                                ratetwo.setText(NumberFormat.getInstance().format(new BigDecimal(rate1).multiply(decimal100)) + "%");
                            }
                            if (pageList.length() >= 3) {//目前用的方法，如果有第三条数据，就显示第三行的布局
                                llinterThree.setVisibility(View.VISIBLE);
                                String interestThreeStr = pageList.getJSONObject(2).optString("interest");
                                BigDecimal interestThree = new BigDecimal(interestThreeStr).setScale(5);
                                interthree.setText(interestThree.toPlainString());
                                String balanceThreeStr = pageList.getJSONObject(2).optString("balance");
                                BigDecimal balanceThree = new BigDecimal(balanceThreeStr).setScale(2);
                                balthree.setText(balanceThree.toPlainString());
                                String rate2 = pageList.getJSONObject(2).optString("annualizedRate");
//                                String newRate2 = rate2.substring(rate2.length() - 1, rate2.length());
                                ratethree.setText(NumberFormat.getInstance().format(new BigDecimal(rate2).multiply(decimal100)) + "%");
                            }
                            if (pageList.length() >= 4) {//目前用的方法，如果有第四条数据，就显示第四行的布局
                                llinterFour.setVisibility(View.VISIBLE);
                                String interestFourStr = pageList.getJSONObject(3).optString("interest");
                                BigDecimal interestFour = new BigDecimal(interestFourStr).setScale(5);
                                interfour.setText(interestFour.toPlainString());
                                String balanceFourStr = pageList.getJSONObject(3).optString("balance");
                                BigDecimal balanceFour = new BigDecimal(balanceFourStr).setScale(2);
                                balfour.setText(balanceFour.toPlainString());
                                String rate3 = pageList.getJSONObject(3).optString("annualizedRate");
//                                String newRate3 = rate3.substring(rate3.length() - 1, rate3.length());
                                ratefour.setText(NumberFormat.getInstance().format(new BigDecimal(rate3).multiply(decimal100)) + "%");
                            }
                            if (pageList.length() >= 5) {//目前用的方法，如果有第五条数据，就显示第五行的布局
                                llinterFive.setVisibility(View.VISIBLE);
                                String interestFiveStr = pageList.getJSONObject(4).optString("interest");
                                BigDecimal interestFive = new BigDecimal(interestFiveStr).setScale(5);
                                interfive.setText(interestFive.toPlainString());
                                String balanceFiveStr = pageList.getJSONObject(4).optString("balance");
                                BigDecimal balanceFive = new BigDecimal(balanceFiveStr).setScale(2);
                                balfive.setText(balanceFive.toPlainString());
                                String rate4 = pageList.getJSONObject(4).optString("annualizedRate");
//                                String newRate4 = rate4.substring(rate4.length() - 1, rate4.length());
                                ratefive.setText(NumberFormat.getInstance().format(new BigDecimal(rate4).multiply(decimal100)) + "%");
                            }
                        }
                        hideProcessBar();
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
                hideProcessBar();
                LogUtils.e("EarningsDetailActivity", "handler 异常");
            }
        }
    };

    @Override
    public void onClick(View v) {

    }

    @Override
    public void startProcessBar() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(EarningsDetailActivity.this, R.style.loading_dialog);
        }
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        progressDialog.show();
    }

    @Override
    public void hideProcessBar() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void showNetError() {
        Utils.makeToast(this, Constants.IS_EVENT_MSG);
    }

    @Override
    public void startNextActivity() {
        getEarningsDetail(myHandler, data, 0);
    }
}
