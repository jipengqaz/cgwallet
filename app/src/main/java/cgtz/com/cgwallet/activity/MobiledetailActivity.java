package cgtz.com.cgwallet.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import cgtz.com.cgwallet.MApplication;
import cgtz.com.cgwallet.R;
import cgtz.com.cgwallet.presenter.SplashPresenter;
import cgtz.com.cgwallet.utility.Constants;
import cgtz.com.cgwallet.utils.LogUtils;
import cgtz.com.cgwallet.utils.Utils;
import cgtz.com.cgwallet.view.ISplashView;
import cgtz.com.cgwallet.widget.ProgressDialog;

/**
 * Created by chen on 2015-10-20.
 * 水费详情界面
 */
public class MobiledetailActivity extends BaseActivity implements View.OnClickListener, ISplashView {
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

    private ImageView ivclose;//Dialog上的关闭按钮
    private EditText etpassword;//密码输入框
    private TextView requiredCost;//需要的金额
    private TextView usableCost;//可用的利息
    private String cuAmount;//当前账单金额
    public String accumulative;//累计收益
    private String mloginPwd;//MD5加密后得支付密码

    private String mOrder;//订单号
    private String ststus;
    private ImageView iv_mode;//支付方式的图标

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // getintDate(); //访问网络获取当前的可用利息值
        showBack(true);
        setTitle("订单详情");
        setBackListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backListener();
            }
        });
        setContentView(R.layout.activity_phone_detail);
        presenter = new SplashPresenter(this);
        init();

        Intent intent = getIntent();
            LogUtils.e("---------拿到的type值-------------", "payType:" + intent.getIntExtra("payType", 1));
            switch (intent.getStringExtra("operator")) {//根据传入的类型设置不同的类型图标
                case "0":
                    typeImage.setImageResource(R.mipmap.mobile);
                    typeImage.setVisibility(View.VISIBLE);
                    payType = "中国移动";
                    break;
                case "1":
                    typeImage.setImageResource(R.mipmap.unicom);
                    typeImage.setVisibility(View.VISIBLE);
                    payType = "中国联通";
                    break;
                case "2":
                    typeImage.setImageResource(R.mipmap.telecom);
                    typeImage.setVisibility(View.VISIBLE);
                    payType = "中国电信";
                    break;
            }
            switch (intent.getIntExtra("payMethod",1)){

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
            type.setText(payType);
            tvSuccess.setText(intent.getStringExtra("pay"));
            tvorderNo.setText(intent.getStringExtra("orderNo"));
            alreadyPay.setText(intent.getStringExtra("pay"));
            tvorderTime.setText(intent.getStringExtra("time"));
            detailApp = (MApplication) getApplication();
            tv_price.setText(intent.getStringExtra("parValue"));

            String number = intent.getStringExtra("userNumber");
            String makeNumber = number.substring(0,3)+"*****"+number.substring(8,number.length());

            tv_phonenumber.setText(makeNumber);


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
        iv_mode= (ImageView) findViewById(R.id.iv_mode);
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
        }
    }

    @Override
    public void startProcessBar() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(MobiledetailActivity.this, R.style.loading_dialog);
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
    public void startNextActivity() {}

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
                Intent intent = new Intent(MobiledetailActivity.this, OrderformActivity.class);
                startActivity(intent);
                MApplication.finishAllActivitys(MainActivity.class.getName());
                finish();
                break;
        }
        return super.onKeyDown(keyCode, event);
    }
    //对返回进行处理（ActionBar返回键）
    public void backListener() {
        Intent intent = new Intent(MobiledetailActivity.this, OrderformActivity.class);
        startActivity(intent);
        MApplication.finishAllActivitys(MainActivity.class.getName());
        finish();
    }
}
