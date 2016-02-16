package cgtz.com.cgwallet.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import cgtz.com.cgwallet.MApplication;
import cgtz.com.cgwallet.R;
import cgtz.com.cgwallet.bean.JsonBean;
import cgtz.com.cgwallet.presenter.SplashPresenter;
import cgtz.com.cgwallet.utility.Constants;
import cgtz.com.cgwallet.utils.CustomTask;
import cgtz.com.cgwallet.utils.LogUtils;
import cgtz.com.cgwallet.utils.MD5Util;
import cgtz.com.cgwallet.utils.Utils;
import cgtz.com.cgwallet.view.ISplashView;
import cgtz.com.cgwallet.widget.ProgressDialog;

/**
 * Created by chen on 2015-10-20.
 * 水费详情界面
 */
public class OrderMobiledetailActivity extends BaseActivity implements View.OnClickListener, ISplashView {
    private Button phonecall;//联系客服的按钮
    private TextView tvSuccess;//缴纳的费用
    private  TextView type;//水煤电的类型
    private  TextView tv_price;//这是充值多少话费的金额
    private ImageView typeImage;//水煤电类型的图标
    private TextView tvorderNo;//订单号
    private TextView alreadyPay;//支付方式 利息支付的金额
    private TextView tvorderTime;//下单时间
    private TextView tvcity;//当前缴费的城市
    private TextView chargeUnit;//当前缴费的单位
    private TextView tv_phonenumber;
    private String payType;//缴费类型
    private MApplication detailApp;
    private String amount;//金额
    private String gmtCreated;//下单时间
    private String orderNo;//订单号
    private String unitName;//缴费单位名
    private String cityName;//城市名
    private int dtype;//水 煤 电对应编号
    private SplashPresenter presenter;
    private ProgressDialog progressDialog;
    private TextView tv_mtStatus;//拿到“处理中...”的控件

    private AlertDialog showlog;
    private ImageView ivclose;//Dialog上的关闭按钮
    private EditText etpassword;//密码输入框
    private TextView requiredCost;//需要的金额
    private TextView usableCost;//可用的利息
    public String accumulative;//累计收益
    private String mloginPwd;//MD5加密后得支付密码


    private String ststus;
    private int parValue;
    private ImageView iv_mode;//支付方式的图标
    Intent intent;

    private String mOrder;//订单号
    private String status;//状态
//2016年1月15日16:45:46  新加
    private TextView rateText;//利息文案
    private LinearLayout jdPay;
    private ImageView downArrow;
    private TextView notEnough;
    private RadioButton ivChecked;

    private boolean isJdpay = false;
    private boolean isCkeck = false;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showBack(true);
        setTitle("订单详情");
        setBackListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backListener();
            }
        });
        setContentView(R.layout.activity_order_phone_detail);
        presenter = new SplashPresenter(this);
        init();
        presenter.didFinishLoading(this);
        getintDate();//访问网络获取当前的可用利息值
         intent = getIntent();
        System.out.println("<<<<<<<<<<<<<<" + intent.getBooleanExtra("orderFormTab", false));
        // LogUtils.e("<<<<<<<<",intent.getBooleanExtra("orderFormTab", false));
        //status = intent.getStringExtra("status");
      //getData(intent.getStringExtra("orderFormNo")); //访问网络拿去需要的数据

        if (intent.getStringExtra("status").equals("1")) {//从订单列表传过来的待支付订单
//            int type1 = intent.getIntExtra("type", 0);
//            switch (type1) {
//                case 4:
//                    typeImage.setImageResource(R.mipmap.mobile);
//                    payType = "中国移动";
//                    break;
//                case 5:
//                    typeImage.setImageResource(R.mipmap.unicom);
//                    payType = "中国联通";
//                    break;
//                case 6:
//                    typeImage.setImageResource(R.mipmap.telecom);
//                    payType = "中国电信";
//                    break;
//            }
//            type.setText(payType);
            mOrder = intent.getStringExtra("orderFormNo");
            getData(mOrder);
//            tvorderNo.setText(mOrder);
//            tvorderTime.setText(intent.getStringExtra("time"));
//            LogUtils.e("----------------------amamamamamamma-----------------", "intent.getStringExtra(amount):" + intent.getStringExtra("amount"));
//            tv_price.setText(intent.getStringExtra("amount") + "元");
//            System.out.println("intent.getStringExtra(\"amount\")<<<<<<<<<<<"+intent.getStringExtra("amount"));
//           // showDialog();
//            tv_mtStatus.setText("");

        } else {
            String order = intent.getStringExtra("userNo");
            tv_mtStatus.setText("");
            presenter.didFinishLoading(OrderMobiledetailActivity.this);
            getDataSever(order);
        }

    }
//


    private void init() {
        phonecall = (Button) findViewById(R.id.phone_call);//“联系客服”的按钮
        phonecall.setOnClickListener(this);
        tvSuccess = (TextView) findViewById(R.id.tv_success);//缴纳的费用
        type = (TextView) findViewById(R.id.tv_type);//类型（水煤电）
        typeImage = (ImageView) findViewById(R.id.iv_typeImage);//类型图标
        tvorderNo = (TextView) findViewById(R.id.tv_order_no);//订单号
        alreadyPay = (TextView) findViewById(R.id.tv_already_pay);//支付方式 利息支付的金额
        tvorderTime = (TextView) findViewById(R.id.tv_orderTime);//下单时间
        tvcity = (TextView) findViewById(R.id.tv_city);//当前城市
        chargeUnit = (TextView) findViewById(R.id.tv_charge_unit);//当前缴费单位
        tv_mtStatus = (TextView) findViewById(R.id.mtStatus);//拿到“处理中...”的控件
        tv_price = (TextView) findViewById(R.id.tv_price);
        tv_phonenumber = (TextView) findViewById(R.id.tv_phonenumber);
        iv_mode = (ImageView) findViewById(R.id.iv_mode);
    }
//对“联系客服”按钮进行监听
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.phone_call:
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + "4008770890"));
                startActivity(intent);
                break;
            case R.id.iv_close:
                Utils.HideSoftKeyboardDialog(OrderMobiledetailActivity.this, etpassword);
                showlog.dismiss();
                break;
        }
    }

    @Override
    public void startProcessBar() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(OrderMobiledetailActivity.this, R.style.loading_dialog);
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
//        Intent intent = getIntent();
//        getData(intent.getStringExtra("userNo"));
    }
//从订单列表除“待支付”外的条目--->订单详情
//从订单列表除“待支付”外的条目--->订单详情
public void getData(String orderNo) {
    //服务器数据交互操作
    HashMap<String, String> maps = new HashMap<>();
    maps.put("user_id", Utils.getUserId());
    maps.put("token", Utils.getToken());
    maps.put("orderNo", orderNo);
//这里参数的含义
    CustomTask task = new CustomTask(dataHandler, Constants.WHAT_QUERY_ORDERDETAIL,
            Constants.SEAWAY_QUERY_ORDERDETAIL,
            true, maps, true);
    task.execute();
}


    private Handler dataHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            try {
                JsonBean jsonBean = (JsonBean) msg.obj;
                int code = jsonBean.getCode();
                String errorMsg = jsonBean.getError_msg();
                if (code == Constants.DATA_EVENT) {
//                    Utils.makeToast(getApplicationContext(), Constants.ERROR_MSG_CODE + code);
                    return;
                }
                int action = msg.what;
                switch (action) {
                    case Constants.WHAT_QUERY_ORDERDETAIL:
                        boolean flag = Utils.filtrateCode(getApplicationContext(), jsonBean);
                        if (flag && code == Constants.OPERATION_FAIL) {//数据交互失败
//                            Utils.makeToast(getApplicationContext(), errorMsg);
                        } else if (flag && code == Constants.OPERATION_SUCCESS) {//数据交互成功
                            JSONObject json = jsonBean.getJsonObject();
                            JSONObject mdetailMsg = json.optJSONObject("result");
                            JSONObject succMsg = json.optJSONObject("success");
//                            LogUtils.e("----------------访问网络是否成功111-----------", "succMsg：" + succMsg);
//                            LogUtils.e("-----------------用户账单详情信息111-------------", "arrearageMsg" + mdetailMsg);
                            amount = mdetailMsg.getString("amount");//账单金额

                            //2016年1月15日17:22:18  测试
                            //判断是否有收益
                            if (!(amount == null) && !(accumulative == null)) {
                                //LogUtils.e("-----------难道走了这里？2-----------","这2");
                                if (Double.valueOf(amount).doubleValue() > Double.valueOf(accumulative).doubleValue()) {
                                    hideProcessBar();
                                    //2016年1月15日16:53:10  新加京东支付后的弹窗逻辑
                                    showDialog();
                                    etpassword.setVisibility(View.GONE);//密码框隐藏
                                    jdPay.setVisibility(View.VISIBLE);//显示京东支付
                                    rateText.setTextColor(getResources().getColor(R.color.darker_gray));//利息置灰
                                    usableCost.setTextColor(getResources().getColor(R.color.darker_gray));//利息置灰
                                    downArrow.setImageResource(R.mipmap.ico_back_up);
                                    downArrow.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if (jdPay.isShown()) {
                                                jdPay.setVisibility(View.GONE);
                                                downArrow.setImageResource(R.mipmap.ico_back_down);
                                            } else {
                                                jdPay.setVisibility(View.VISIBLE);
                                                downArrow.setImageResource(R.mipmap.ico_back_up);
                                                etpassword.setVisibility(View.GONE);
                                            }
                                        }
                                    });
                                    return ;
                                } else {
                                    //LogUtils.e("-----------难道走了这里？3-----------","这3");
                                    showDialog();
                                    notEnough.setVisibility(View.GONE);
                                    downArrow.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if (jdPay.isShown()) {
                                                jdPay.setVisibility(View.GONE);
                                                downArrow.setImageResource(R.mipmap.ico_back_down);
                                                etpassword.setVisibility(View.VISIBLE);
                                            } else {
                                                jdPay.setVisibility(View.VISIBLE);
                                                downArrow.setImageResource(R.mipmap.ico_back_up);
                                                etpassword.setVisibility(View.GONE);
                                                if (!etpassword.hasFocus()){
                                                    InputMethodManager inputmanger = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                                    inputmanger.hideSoftInputFromWindow(etpassword.getWindowToken(), 0);
                                                }
                                            }
                                        }
                                    });
                                }
                            }

                            gmtCreated = mdetailMsg.getString("gmtCreated");//缴费时间
                            orderNo = mdetailMsg.getString("orderNo");//账单号
                            parValue = mdetailMsg.optInt("parValue");
                            String mobileNumber = mdetailMsg.getString("mobileNumber");
                            dtype = mdetailMsg.getInt("type");//水煤电类型
                            tvSuccess.setText( amount + "元");//缴费金额
                            tvorderNo.setText(orderNo);//设置订单号

                            type.setText(amount+"元");
                            tv_price.setText(parValue+"元");
                            tvorderNo.setText(orderNo);
                            //alreadyPay.setText("利息支付" + intent.getStringExtra("pay"));
                            //tvorderTime.setText(intent.getStringExtra("time"));
                            //detailApp = (MApplication) getApplication();
                            tv_price.setText(parValue+"元");

                            String number = mobileNumber.substring(0,3)+"*****"+mobileNumber.substring(8,mobileNumber.length());


                            tv_phonenumber.setText(number);


                            int orderStatus=mdetailMsg.optInt("status");//缴费方式

                            if (orderStatus==1) {
                                tv_mtStatus.setText("待支付");
                                tv_mtStatus.setTextColor(getResources().getColor(R.color.main_red));
                            }else if (orderStatus==2){
                                tv_mtStatus.setText("处理中...");
                            }else if(orderStatus==3){
                                tv_mtStatus.setText("已充值");
                                tv_mtStatus.setTextColor(getResources().getColor(R.color.main_bg));
                            }else if(orderStatus==-1){
                                tv_mtStatus.setText("已取消");
                                tv_mtStatus.setTextColor(getResources().getColor(R.color.main_red));
                            }else {
                                tv_mtStatus.setText("充值失败");
                                tv_mtStatus.setTextColor(getResources().getColor(R.color.main_red));
                            }
//                            alreadyPay.setText("利息支付" + amount + "元");
                            tvorderTime.setText(gmtCreated);
//                            tvcity.setText(cityName);
//                            chargeUnit.setText(unitName);

                            switch (dtype) {
                                case 1:
                                    typeImage.setImageResource(R.mipmap.ico_water_);
                                    type.setText(payType = "水费");
                                    break;
                                case 2:
                                    typeImage.setImageResource(R.mipmap.ico_dian_);
                                    type.setText(payType = "电费");
                                    break;
                                case 3:
                                    typeImage.setImageResource(R.mipmap.ico_ranqi_);
                                    type.setText(payType = "燃气费");
                                    break;
                                case 4:
                                    typeImage.setImageResource(R.mipmap.mobile);
                                    typeImage.setVisibility(View.VISIBLE);
                                    type.setText(payType = "中国移动");
                                    break;
                                case 5:
                                    typeImage.setImageResource(R.mipmap.unicom);
                                    typeImage.setVisibility(View.VISIBLE);
                                    type.setText(payType = "中国联通");
                                    break;
                                case 6:
                                    typeImage.setImageResource(R.mipmap.telecom);
                                    typeImage.setVisibility(View.VISIBLE);
                                    type.setText(payType = "中国电信");
                                    break;
                            }
//                            showDialog();
                        }
                        hideProcessBar();
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
                LogUtils.e("WaterdetailActivity", "handler 异常");
            }
        }
    };


    public void getDataSever(String orderNo) {
        //服务器数据交互操作
        HashMap<String, String> maps = new HashMap<>();
        maps.put("user_id", Utils.getUserId());
        maps.put("token", Utils.getToken());
        maps.put("orderNo", orderNo);
//这里参数的含义
        CustomTask task = new CustomTask(dataSeverHandler, Constants.WHAT_QUERY_ORDERDETAIL,
                Constants.SEAWAY_QUERY_ORDERDETAIL,
                true, maps, true);
        task.execute();
    }


    private Handler dataSeverHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            try {
                JsonBean jsonBean = (JsonBean) msg.obj;
                int code = jsonBean.getCode();
                String errorMsg = jsonBean.getError_msg();
                if (code == Constants.DATA_EVENT) {
//                    Utils.makeToast(getApplicationContext(), Constants.ERROR_MSG_CODE + code);
                    return;
                }
                int action = msg.what;
                switch (action) {
                    case Constants.WHAT_QUERY_ORDERDETAIL:
                        boolean flag = Utils.filtrateCode(getApplicationContext(), jsonBean);
                        if (flag && code == Constants.OPERATION_FAIL) {//数据交互失败
//                            Utils.makeToast(getApplicationContext(), errorMsg);
                        } else if (flag && code == Constants.OPERATION_SUCCESS) {//数据交互成功
                            JSONObject json = jsonBean.getJsonObject();
                            JSONObject mdetailMsg = json.optJSONObject("result");
                            JSONObject succMsg = json.optJSONObject("success");
//                            LogUtils.e("----------------访问网络是否成功111-----------", "succMsg：" + succMsg);
//                            LogUtils.e("-----------------用户账单详情信息111-------------", "arrearageMsg" + mdetailMsg);
                            amount = mdetailMsg.getString("amount");//账单金额

                            gmtCreated = mdetailMsg.getString("gmtCreated");//缴费时间
                            orderNo = mdetailMsg.getString("orderNo");//账单号
                            parValue = mdetailMsg.optInt("parValue");//
                            String mobileNumber = mdetailMsg.getString("mobileNumber");
                            int orderStatus=mdetailMsg.optInt("status");//缴费方式

                            if (orderStatus!=-1){
                                int paymentMethod = mdetailMsg.getInt("paymentMethod");
                                switch (paymentMethod){
                                    case 1:
                                        iv_mode.setVisibility(View.VISIBLE);
                                        iv_mode.setImageResource(R.mipmap.dollar);
                                        alreadyPay.setText(amount+"元");
                                        break;
                                    case 2:
                                        iv_mode.setVisibility(View.VISIBLE);
                                        iv_mode.setImageResource(R.mipmap.logo_jd);
                                        alreadyPay.setText(amount+"元");
                                        break;

                                }
                            }
//                            unitName = mdetailMsg.getString("unitName");//缴费单位名称
//                            cityName = mdetailMsg.getString("cityName");//当前城市名
                            dtype = mdetailMsg.getInt("type");//水煤电类型
                            tvSuccess.setText(amount + "元");//缴费金额
                            tvorderNo.setText(orderNo);//设置订单号

                            type.setText(amount+"元");
                            tv_price.setText(parValue+"元");
                            tvorderNo.setText(orderNo);
                            //alreadyPay.setText("利息支付" + intent.getStringExtra("pay"));
                            //tvorderTime.setText(intent.getStringExtra("time"));
                            //detailApp = (MApplication) getApplication();
                            //tv_price.setText(parValue+"元");

                            String number = mobileNumber.substring(0,3)+"*****"+mobileNumber.substring(8,mobileNumber.length());

                            tv_phonenumber.setText(number);

                            if (orderStatus==2){
                                tv_mtStatus.setText("处理中...");
                                tv_mtStatus.setTextColor(getResources().getColor(R.color.black));
                            }else if(orderStatus==3){
                                tv_mtStatus.setText("已充值");
                                tv_mtStatus.setTextColor(getResources().getColor(R.color.main_bg));
                            }else if(orderStatus==-1){
                                tv_mtStatus.setText("已取消");
                                tv_mtStatus.setTextColor(getResources().getColor(R.color.main_red));
                            }else {
                                tv_mtStatus.setText("充值失败");
                                tv_mtStatus.setTextColor(getResources().getColor(R.color.main_red));
                            }
//                            alreadyPay.setText("利息支付" + amount + "元");
                            tvorderTime.setText(gmtCreated);
//                            tvcity.setText(cityName);
//                            chargeUnit.setText(unitName);

                            switch (dtype) {
                                case 1:
                                    typeImage.setImageResource(R.mipmap.ico_water_);
                                    type.setText(payType = "水费");
                                    break;
                                case 2:
                                    typeImage.setImageResource(R.mipmap.ico_dian_);
                                    type.setText(payType = "电费");
                                    break;
                                case 3:
                                    typeImage.setImageResource(R.mipmap.ico_ranqi_);
                                    type.setText(payType = "燃气费");
                                    break;
                                case 4:
                                    typeImage.setImageResource(R.mipmap.mobile);
                                    typeImage.setVisibility(View.VISIBLE);
                                    type.setText(payType = "中国移动");
                                    break;
                                case 5:
                                    typeImage.setImageResource(R.mipmap.unicom);
                                    typeImage.setVisibility(View.VISIBLE);
                                    type.setText(payType = "中国联通");
                                    break;
                                case 6:
                                    typeImage.setImageResource(R.mipmap.telecom);
                                    typeImage.setVisibility(View.VISIBLE);
                                    type.setText(payType = "中国电信");
                                    break;
                            }

                        }
                        hideProcessBar();
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
                LogUtils.e("WaterdetailActivity", "handler 异常");
            }
        }
    };







/*//  问题无法解决，无法实现从当前界面跳转后跳转到
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent intent=new Intent(WaterdetailActivity.this,OrderformActivity.class);
        startActivity(intent);
    }*/


    //对返回进行处理（手机的返回键）
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                Intent intent = new Intent(OrderMobiledetailActivity.this, OrderformActivity.class);
                startActivity(intent);
                MApplication.finishAllActivitys(MainActivity.class.getName());
                finish();
                break;
        }
        return super.onKeyDown(keyCode, event);
    }
    //对返回进行处理（ActionBar返回键）
    public void backListener() {
        Intent intent = new Intent(OrderMobiledetailActivity.this, OrderformActivity.class);
        startActivity(intent);
        MApplication.finishAllActivitys(MainActivity.class.getName());
        finish();
    }


    public void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = View.inflate(this, R.layout.dialog_password, null);
        showlog = builder.create();
        showlog.setView(view, 0, 0, 0, 0);
//      show.setView(passwordLayout, 0, 0, 0, 0);
        showlog.show();
        ivclose = (ImageView) view.findViewById(R.id.iv_close);
//        Log.e("close.......", "ivclose = " + ivclose);
        ivclose.setOnClickListener(this);
        etpassword = (EditText) view.findViewById(R.id.et_password);//密码框
        requiredCost = (TextView) view.findViewById(R.id.tv_required_cost);//这是应该付钱的数目
        jdPay = (LinearLayout) view.findViewById(R.id.ll_jd_pay);//京东支付布局
        notEnough = (TextView) view.findViewById(R.id.tv_not_enough);//'利息不足'的文案

        rateText = (TextView) view.findViewById(R.id.tv_rate_text);//利息文案
        usableCost = (TextView) view.findViewById(R.id.tv_usable_cost);//利息数值

        ivChecked = (RadioButton) view.findViewById(R.id.iv_checked);//第三方支付的checked   https://wallethelp.sinaapp.com/help.html
        ivChecked.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    isJdpay = true;
                    isCkeck =true;
                    showlog.dismiss();
                    presenter.didFinishLoading(OrderMobiledetailActivity.this);
                    detailData(mOrder, "2");

                }
            }
        });
        downArrow = (ImageView) view.findViewById(R.id.iv_down_arrow);//弹出选择第三方支付的箭头
/*        downArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (jdPay.isShown()){
                    jdPay.setVisibility(View.GONE);
                    downArrow.setImageResource(R.mipmap.ico_back_down);
                }else {
                    jdPay.setVisibility(View.VISIBLE);
                    downArrow.setImageResource(R.mipmap.ico_back_up);
                }
            }
        });*/


        LogUtils.e("-----------------aaaaaaaaaaa------------", "amount:" + amount);
        requiredCost.setText(amount+"元");
        usableCost = (TextView) view.findViewById(R.id.tv_usable_cost);
//      拿到首页此时的可用利息
        usableCost.setText(accumulative + "元");

        etpassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            //监听文本框改变之后
            @Override
            public void afterTextChanged(Editable s) {
                String password = etpassword.getText().toString();
                mloginPwd = MD5Util.md5(password);
                if (password.length() == 6) {
                    Utils.HideSoftKeyboardDialog(OrderMobiledetailActivity.this, etpassword);
                    showlog.dismiss();

                    //输入支付密码对话框消失后调用此方法向服务器请求数据
                    LogUtils.e("--------------mmmmmmmmmmmmmmmmm------------------------", "mOrder:" + mOrder + "------" + mloginPwd);
                    presenter.didFinishLoading(OrderMobiledetailActivity.this);
                    detailData(mOrder, "1", mloginPwd);

                }
            }
        });
    }


    /**
     * 访问首页利息接口，拿到当前利息的接口数据
     */
    public void getintDate() {
        HashMap<String, String> params1 = new HashMap<>();
        params1.put("user_id", Utils.getUserId());
        params1.put("token", Utils.getToken());
        CustomTask task = new CustomTask(dHandler, Constants.WHAT_WALLET_DETAIL
                , Constants.URL_WALLET_DETAIL, true, params1, true);
        task.execute();
    }

    /**
     * 访问首页利息接口，拿到当前利息的接口数据所用到的handler
     */
    private Handler dHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            try {
//                mSwipeLayout.setRefreshing(false);

                JsonBean jsonBean = (JsonBean) msg.obj;
                int code = jsonBean.getCode();
                String errorMsg = jsonBean.getError_msg();
                if (code == Constants.DATA_EVENT) {
                    hideProcessBar();
//                    Utils.makeToast(WaterqueryActivity.this, Constants.ERROR_MSG_CODE + code);
                    return;
                }
                int action = msg.what;
                switch (action) {
                    case Constants.WHAT_WALLET_DETAIL:
                        boolean flag = Utils.filtrateCode(OrderMobiledetailActivity.this, jsonBean);
                        if (flag && code == Constants.OPERATION_FAIL) {//数据交互失败
//                            Utils.makeToast(WaterqueryActivity.this, errorMsg);
                        } else if (flag && code == Constants.OPERATION_SUCCESS) {//数据交互成功
                            JSONObject jsonObject = new JSONObject(jsonBean.getJsonString());
                            accumulative = jsonObject.optString("receivableInterest");//可用收益
                        }
                        hideProcessBar();
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
                hideProcessBar();
                LogUtils.e("WaterqueryActivity", "handler 异常");
            }
        }
    };


    /**
     * 支付订单(输入密码后的数据传递与解析，支付密码完成后访问网络的请求)
     */
    public void detailData(String orderNo, String paymentMethod, String payPassword) {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("payPassword", payPassword);
        } catch (JSONException e) {
            e.printStackTrace();
        }



        //服务器数据交互操作
        HashMap<String, String> maps = new HashMap<>();
        maps.put("user_id", Utils.getUserId());
        maps.put("token", Utils.getToken());
        maps.put("orderNo", orderNo);
        maps.put("paymentMethod", paymentMethod);
        maps.put("paymentParams", jsonObject.toString());




        CustomTask task = new CustomTask(detailHandler, Constants.WHAT_PAYORDER,
                Constants.SEAWAY_PAYORDER,
                true, maps, true);
        task.execute();
    }


    /**
     * 支付订单(输入密码后的数据传递与解析，支付密码完成后访问网络的请求)  2016年1月25日18:07:35  京东支付
     */
    public void detailData(String orderNo, String paymentMethod) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("channel", "jdpayWap");
            jsonObject.put("successUrl", Constants.OFFLINE_HTTP+"util/payFail");
            jsonObject.put("failUrl", Constants.OFFLINE_HTTP+"util/payFail");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //服务器数据交互操作
        HashMap<String, String> maps = new HashMap<>();
        maps.put("user_id", Utils.getUserId());
        maps.put("token", Utils.getToken());
        maps.put("orderNo", orderNo);
        maps.put("paymentMethod", paymentMethod);
        maps.put("paymentParams", jsonObject.toString());

        CustomTask task = new CustomTask(detailHandler, Constants.WHAT_PAYORDER,
                Constants.SEAWAY_PAYORDER,
                true, maps, true);
        task.execute();
    }


    /**
     * 支付订单(输入密码后的数据传递与解析，支付密码完成后访问网络的请求所用到的handler)
     */
    private Handler detailHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            try {
                JsonBean jsonBean = (JsonBean) msg.obj;
                int code = jsonBean.getCode();
                String errorMsg = jsonBean.getError_msg();
                String detSuccess = jsonBean.getJsonObject().optString("success");
                String toserrorMsg = jsonBean.getJsonObject().optString("errorMsg");
                if (code == Constants.DATA_EVENT) {
//                    Utils.makeToast(getApplicationContext(), Constants.ERROR_MSG_CODE + code);
                    return;
                }
                int action = msg.what;
                switch (action) {
                    case Constants.WHAT_PAYORDER:
                        boolean flag = Utils.filtrateCode(getApplicationContext(), jsonBean);
                        if (flag && code == Constants.OPERATION_FAIL) {//数据交互失败
                            // Utils.makeToast(getApplicationContext(), errorMsg);
                            hideProcessBar();
                            Utils.makeToast_short(OrderMobiledetailActivity.this, toserrorMsg);
                            LogUtils.e("------------111111111111111111111111111----------", "toserrorMsg：");
                        } else if (flag && code == Constants.OPERATION_SUCCESS) {//数据交互成功
                            LogUtils.e("------------2222222222222222222222222222----------", "toserrorMsg：");
                            JSONObject json = jsonBean.getJsonObject();

                            JSONObject payNo = json.optJSONObject("result");
                            if (detSuccess.equals("1")) {
                                if (isJdpay) {
                                    JSONObject charge = payNo.getJSONObject("charge");
                                    String charge2 = charge.toString().replace("+", "%2B");
                                    Intent intent =new Intent(OrderMobiledetailActivity.this, WebView.class);
                                    intent.putExtra("orderFrom",true);//这是判断是否从订单列表传过来
                                    intent.putExtra("reCharge",true);
                                    intent.putExtra("userNo",mOrder);
                                    intent.putExtra("url", Constants.ONLINE_HTTP+"show/jdPay?charge=" + charge2);
                                    intent.putExtra("title", "京东支付");
                                    startActivity(intent);

                                    finish();
                                }else{

                                    getDataSever(mOrder);//这是处理非待支付订单的请求

                                }

                                isJdpay = false;
                                isCkeck = false;




                            }
                            hideProcessBar();



                        }
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
                LogUtils.e("WaterqueryActivity", "handler 异常");
            }

        }
    };

}
