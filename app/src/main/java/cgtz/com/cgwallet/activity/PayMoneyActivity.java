package cgtz.com.cgwallet.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.LogRecord;

import cgtz.com.cgwallet.MApplication;
import cgtz.com.cgwallet.R;
import cgtz.com.cgwallet.bean.JsonBean;
import cgtz.com.cgwallet.paymoney.ContactManager;
import cgtz.com.cgwallet.paymoney.CussorAdapter;
import cgtz.com.cgwallet.paymoney.DialogAdapter;
import cgtz.com.cgwallet.paymoney.GVAdapter;
import cgtz.com.cgwallet.paymoney.LinkManBean;
import cgtz.com.cgwallet.paymoney.MainPresenter;
import cgtz.com.cgwallet.paymoney.MyGridView;
import cgtz.com.cgwallet.paymoney.Phone;
import cgtz.com.cgwallet.paymoney.PhoneBean;
import cgtz.com.cgwallet.paymoney.PhoneInfo;
import cgtz.com.cgwallet.paymoney.ProductsBean;
import cgtz.com.cgwallet.presenter.SplashPresenter;
import cgtz.com.cgwallet.utility.Constants;
import cgtz.com.cgwallet.utils.CustomTask;
import cgtz.com.cgwallet.utils.LogUtils;
import cgtz.com.cgwallet.utils.MD5Util;
import cgtz.com.cgwallet.utils.Utils;
import cgtz.com.cgwallet.view.ISplashView;
import cgtz.com.cgwallet.widget.ProgressDialog;

public  class PayMoneyActivity extends BaseActivity implements  View.OnClickListener, ISplashView ,AdapterView.OnItemClickListener{
    //
    private String Tag = "PayMoneyActivity";
    private Button mBt_flow;
    MyGridView mGv_pay;
    EditText mEt_num;
    TextView mTv_address,tv_city;
    MainPresenter mMainPresenter;
    private ImageView iv_delete,iv_linkman;
    private SplashPresenter presenter;
    private ProgressDialog progressDialog;
    private String username,usernumber;
    private AlertDialog dialogPhone;
    private ListView lv_dialog;
    private List<LinkManBean> linkManBeanList = new ArrayList<LinkManBean>();
    private List<PhoneBean> phoneList = new ArrayList<PhoneBean>();
    private boolean isChecked = false;
    private  ArrayList<ProductsBean> productsBeanList  = new ArrayList<ProductsBean>();

    private PopupWindow popupWindow;
    private AlertDialog showlog;
    private ImageView ivclose;//Dialog上的关闭按钮
    private EditText etpassword;//密码输入框
    private TextView requiredCost;//需要的金额
    private boolean tag;//判断是不是从订单列表页进来的
    private String orderFormNo;//订单列表传过来的订单号
    private String orderAmount;//订单列表传过来的订单金额
    private TextView usableCost;//可用的利息
    private String cuAmount;//当前账单金额
    public String accumulative;//累计收益
    private String mloginPwd;//MD5加密后得支付密码

    private String mOrder;//订单号
    private String mTime;//下单时间

    private static Context context;
    private static ProgressDialog dialog;
    private CheckBox cb_select;
    private TextView tv_agree;
    //访问服务器得到的数据

    private TextView rateText;//利息文案
    private LinearLayout jdPay;
    private ImageView downArrow;
    private TextView notEnough;
    private RadioButton ivChecked;

    String area, operator,areaCode,salePrice,parValue;
    MyAdapter adapter;
    int length;//这是输入框的长度
    private  ArrayList<PhoneInfo> phoneInfos = new ArrayList<>();
    ArrayList<Phone> arrayList = new ArrayList<>();
    private boolean isInput = true;

    private boolean isOption = true;

    private boolean isJdpay = false;
    private boolean isCkeck = false;
    private TextView helpcenter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showBack(true);
        setTitle("话费充值");
        MApplication.registActivities(this);
        setContentView(R.layout.activity_paymoney);

        getintDate();//访问网络获取当前的可用利息值
        presenter = new SplashPresenter(this);
        presenter.didFinishLoading(this);
        initView();
        initListener();//给按钮设置点击事件
        initPresenter();//给gridview设置数据

        phoneInfos = ContactManager.getData(this,phoneInfos);//这是拿到所有电话号码和姓名的集合

        mEt_num.addTextChangedListener(newWatcher);

    }



    private void initListener() {
        iv_delete.setOnClickListener(this);
        iv_linkman.setOnClickListener(this);
        mEt_num.setOnClickListener(this);
        helpcenter.setOnClickListener(this);
    }

    //初始化控件
    public void initView() {


        iv_delete = (ImageView) findViewById(R.id.iv_delete);
        iv_linkman = (ImageView) findViewById(R.id.iv_linkman);
        mBt_flow = ((Button) findViewById(R.id.bt_flow));
        mGv_pay = ((MyGridView) findViewById(R.id.gv_pay));
        mGv_pay.setSelector(new ColorDrawable(Color.TRANSPARENT));
        mEt_num = ((EditText) findViewById(R.id.et_num));
        mEt_num.addTextChangedListener(watcher);

        mTv_address = ((TextView) findViewById(R.id.tv_address));
        String userPhone = editPhone(Utils.getUserPhone(this));

        mEt_num.setText(userPhone);//默认显示用户绑定的电话号码
        mEt_num.setSelection(userPhone.length());//让光标在最后
        mTv_address.setText("当前绑定号码");
        tv_city = ((TextView) findViewById(R.id.tv_city));
        helpcenter = (TextView) findViewById(R.id.help_center);

        //  mEt_num.addTextChangedListener(watcher);

//        presenter.didFinishLoading(this);
//        getServiceData(Utils.getUserPhone(this));
        //getServiceData("18668050004");


//        ContentResolver contentResolver = getContentResolver();
//        String[] colmuns = new String[] { ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME };
//        Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, colmuns,
//                null, null, null);
//        //LogUtils.e("<<<<<<<<<<<<<<<",cursor.getString(1));
//        CussorAdapter adapter = new CussorAdapter(this,cursor,0,mEt_num.getText().toString());
////       String num =  adapter.convertToString(cursor2).toString();
////        LogUtils.e("<<<<<<<<<<<<<<",num);
//
//       mEt_num.setAdapter(adapter);

//        mEt_num.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
//              //  Object obj = adapterView.getItemAtPosition(position);
//                TextView tv_num = (TextView) view.findViewById(R.id.tv_num);
//                TextView tv_name = (TextView) view.findViewById(R.id.tv_name);
//
//                String num = editPhone(tv_num.getText().toString());
////                if (num.replace(" ","").equals(Utils.getUserPhone(PayMoneyActivity.this))){
////                    mTv_address.setText("当前绑定号码");
////                    mEt_num.setText(Utils.getUserPhone(PayMoneyActivity.this));
////                }
//                mEt_num.setText(num);
//                mEt_num.setSelection(num.length());//让光标在最后
//                mTv_address.setText(tv_name.getText().toString());
//
//            }
//        });



        cb_select = (CheckBox) findViewById(R.id.cb_select);
        cb_select.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {//根据CheckBox是否选中，给“点击支付”按钮设置点击事件
                if (!isChecked) {
                    initPresenter();
                    iv_linkman.setEnabled(false);
                    mEt_num.setEnabled(false);
                    iv_delete.setEnabled(false);
                    Utils.makeToast(PayMoneyActivity.this, "请先阅读并同意手机充值服务协议!");

                } else {
                    iv_linkman.setEnabled(true);
                    mEt_num.setEnabled(true);
                    iv_delete.setEnabled(true);

                    presenter.didFinishLoading(PayMoneyActivity.this);
                    getServiceData(mEt_num.getText().toString().replace(" ", ""));
                }
            }
        });

        tv_agree = (TextView) findViewById(R.id.tv_agreement);

        tv_agree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                    跳转到缴费服务协议的H5页面
                startActivity(new Intent(PayMoneyActivity.this, WebViewActivity.class).putExtra("url", Constants.ONLINE_HTTP + "show/paymentMobileAgreement").putExtra("title", "话费充值协议"));
            }
        });



    }

    private void initPresenter() {
        mMainPresenter = new MainPresenter(this, mGv_pay);
        ArrayList<ProductsBean> payMoneys = mMainPresenter.setData();
//        mMainPresenter.setAdapter(mGv_pay,new GVAdapter(this,payMoneys));
        GVAdapter adapter =  new GVAdapter(this, payMoneys);
        mGv_pay.setAdapter(adapter);
        mGv_pay.setEnabled(false);


    }

    private TextWatcher newWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence c, int i, int i1, int i2) {
            String number = mEt_num.getText().toString().replace(" ", "");
            if (c.toString().length()>=5&&c.toString().length()<9){
                arrayList.clear();

                for (int j = 0 ;j<phoneInfos.size(); j++){
                    if (phoneInfos.get(j).getUserNumber().startsWith(number)) {
//                tv_month.setText(phoneInfoArrayList.get(position).getUserNumber());
//                tv_balance.setText(phoneInfoArrayList.get(position).getUserName());
                       // LogUtils.e("222222222222222222222","c.toString()"+c.toString());

                        Phone phone = new Phone();
                        phone.setUserNumber(phoneInfos.get(j).getUserNumber());
                        phone.setUserName(phoneInfos.get(j).getUserName());
                        arrayList.add(phone);


                    }
                }


                initPopupWindow();

            }

            if (c.toString().length()>=10&&c.toString().length()<13){
                arrayList.clear();

                for (int j = 0 ;j<phoneInfos.size(); j++){
                    if (phoneInfos.get(j).getUserNumber().startsWith(number)) {
//                tv_month.setText(phoneInfoArrayList.get(position).getUserNumber());
//                tv_balance.setText(phoneInfoArrayList.get(position).getUserName());
                        //LogUtils.e("222222222222222222222","c.toString()"+c.toString());

                        Phone phone = new Phone();
                        phone.setUserNumber(phoneInfos.get(j).getUserNumber());
                        phone.setUserName(phoneInfos.get(j).getUserName());
                        arrayList.add(phone);


                    }
                }


                initPopupWindow();

            }






        }

        @Override
        public void afterTextChanged(Editable editable) {


        }
    };



    private final TextWatcher watcher = new TextWatcher() {
        private CharSequence beforeCharSequence;
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            this.beforeCharSequence = charSequence.toString();
        }

        @Override
        public void onTextChanged(CharSequence c, int start, int before, int count) {
//            ProductsBean productsBean = new ProductsBean();
//            productsBean.setOnSale(false);
//            productsBeanList.add(productsBean);
            int beforeLength = beforeCharSequence.length();
            int onLength = c.length();




            if (onLength == 1){
                if (!c.toString().startsWith("1")){
                    mEt_num.setText("");
                }
            }

            if(beforeLength < onLength) {
                if (isInput && isOption){

                    if(beforeLength == 3 || beforeLength == 8) {//添加号码
                        String mobileNumber = beforeCharSequence + " " + c.charAt(onLength-1);
                        mEt_num.setText(mobileNumber);
                        mEt_num.setSelection(mEt_num.getText().toString().length());
                    }
                }else{
                    isInput = true;
                    isOption = true;
                }
            } else if(beforeLength > onLength) {//这是删减号码
                if(onLength == 4 || onLength == 9) {
                    String mobileNumber = c.subSequence(0,onLength-1).toString();
                    mEt_num.setText(mobileNumber);
                    mEt_num.setSelection(mEt_num.getText().toString().length());
                }

            }
            if (onLength == 13) {
                LogUtils.e("---------------------13131313131313131----------------", "count:" + count);
                final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mEt_num.getWindowToken(),
                        0);


                // mEt_num.setFocusable(false);

//                mEt_num.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//
//                        mEt_num.setFocusableInTouchMode(true);
//                        mEt_num.requestFocus();
//                        iv_delete.setVisibility(View.VISIBLE);
//                    }
//                });
                //iv_delete.setVisibility(View.GONE);


            }
            if (onLength==0){
                iv_delete.setVisibility(View.GONE);
                initPresenter();
            }else {
                iv_delete.setVisibility(View.VISIBLE);
                initPresenter();
            }


        }

        @Override
        public void afterTextChanged(Editable s) {
            if(s.length() == 13){
                //mEt_num.dismissDropDown();
                String num = mEt_num.getText().toString().replace(" ","");
                if (num.equals(Utils.getUserPhone(PayMoneyActivity.this))){
                    mTv_address.setText("当前绑定号码");
                    mTv_address.setTextColor(Color.parseColor("#83d62f"));
                    //tv_city.setTextColor(Color.parseColor("#83d62f"));
                }else {
                    String name = ContactManager.getName(PayMoneyActivity.this, num, phoneList);
                    if (name == ""){
                        mTv_address.setText("不在通讯录");
                        mTv_address.setTextColor(Color.parseColor("#ff9900"));
                        //tv_city.setTextColor(Color.parseColor("#ff9900"));
                    }else{
                        mTv_address.setText(name);
                        mTv_address.setTextColor(Color.parseColor("#000000"));
                        //tv_city.setTextColor(Color.parseColor("#000000"));
                    }
                }


                presenter.didFinishLoading(PayMoneyActivity.this);
                getServiceData(num.replace(" ", ""));

            }else{
                mTv_address.setText("");
                tv_city.setText("");
                initPresenter();

            }

        }
    };


    @Override
    public void startProcessBar() {
        if(progressDialog == null){
            progressDialog = new ProgressDialog(PayMoneyActivity.this, R.style.loading_dialog);
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


    /**
     *
     *这是使电话号码中间添加空格
     */
    public String editPhone(String phone){
        StringBuffer buffer = new StringBuffer();
        char[] ch = phone.toCharArray();
        phone =  buffer.append(ch[0]).append(ch[1]).append(ch[2]).append(' ').append(ch[3]).append(ch[4]).append(ch[5]).append(ch[6]).append(' ').append(ch[7])
                .append(ch[8]).append(ch[9]).append(ch[10]).toString();
        buffer.delete(0,buffer.length());
        return  phone;

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_delete:
                mEt_num.setText("");//清空输入框数据
                initPresenter();

                break;
            case R.id.iv_linkman:
                startActivityForResult(new Intent(
                        Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI), 0);

                // mEt_num.addTextChangedListener(watcher);
                break;
//            case R.id.et_num:
//               mEt_num.setFocusable(true);
//                mEt_num.setEnabled(true);
//                mEt_num.setSelection(mEt_num.getText().toString().length());
//                iv_delete.setVisibility(View.VISIBLE);
//                break;
            case R.id.iv_close:
                Utils.HideSoftKeyboardDialog(PayMoneyActivity.this, etpassword);
                showlog.dismiss();
                break;
            case R.id.help_center:
                startActivity(new Intent(PayMoneyActivity.this, WebViewActivity.class).putExtra("url", Constants.ONLINE_HTTP + "show/HelpMobile").putExtra("title", "话费充值帮助中心"));
                break;
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
//ContentProvider展示数据类似一个单个数据库表
//ContentResolver实例带的方法可实现找到指定的ContentProvider并获取到ContentProvider的数据
            ContentResolver reContentResolverol = getContentResolver();
            //URI,每个ContentProvider定义一个唯一的公开的URI,用于指定到它的数据集
            Uri contactData = data.getData();
            //查询就是输入URI等参数,其中URI是必须的,其他是可选的,如果系统能找到URI对应的ContentProvider将返回一个Cursor对象.
            Cursor cursor = managedQuery(contactData, null, null, null, null);
            cursor.moveToFirst();
            //获得DATA表中的名字
            username = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            //条件为联系人ID
            String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));




            // 获得DATA表中的电话号码，条件为联系人ID,因为手机号码可能会有多个
            Cursor phone = reContentResolverol.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId,
                    null,
                    null);

            linkManBeanList.clear();

            while (phone.moveToNext()) {
                LinkManBean bean = new LinkManBean();
                usernumber = phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));


                if (ContactManager.processMobileNumber(usernumber).startsWith("1")&&usernumber.length()>=11){
                    usernumber=ContactManager.processMobileNumber(usernumber);

                    bean.setUserName(username);
                    bean.setUserNumber(usernumber);

                    linkManBeanList.add(bean);
                }

            }

            if (linkManBeanList.size()>1){

                initDialog();

            }else {
                isOption = false;
                for (int i = 0 ; i <linkManBeanList.size();i++){
                    usernumber = linkManBeanList.get(i).getUserNumber();
                    username = linkManBeanList.get(i).getUserName();
                }
                linkManBeanList.clear();
                if (usernumber.startsWith("+86")){
                    usernumber = usernumber.substring(3, usernumber.length());

                }


//                    usernumber = usernumber.replace(" ","");
//                    usernumber = usernumber.replace("-","");
                usernumber = ContactManager.processMobileNumber(usernumber);
                if (usernumber.startsWith("1")&&usernumber.length() == 11){
                    usernumber = editPhone(usernumber);
                    mEt_num.setText(usernumber);

                    mEt_num.setSelection(usernumber.length());
                    //clean(true);
                    mTv_address.setText(username);
                    mTv_address.setTextColor(Color.parseColor("#000000"));
                    initPresenter();
                    tv_city.setText("");
                    getServiceData(usernumber.replace(" ", ""));
                    presenter.didFinishLoading(PayMoneyActivity.this);
                }
            }

        }

    }

    //这是有多个电话的选择框
    private void initDialog() {

        isOption = false;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = View.inflate(this, R.layout.dialog_lianxiren,null);
        dialogPhone = builder.create();
        dialogPhone.setView(view,0,0,0,0);
        lv_dialog = (ListView) view.findViewById(R.id.lv_dialog);
        DialogAdapter adapter = new DialogAdapter(PayMoneyActivity.this,linkManBeanList);
        adapter.setCheckboxClickLisener(new DialogAdapter.CheckboxClickLisener(){
            @Override
            public void click(int position) {
                usernumber = linkManBeanList.get(position).getUserNumber();
//                usernumber = usernumber.replace(" ", "");
//                usernumber = usernumber.replace("-", "");
                usernumber = ContactManager.processMobileNumber(usernumber);
                usernumber = editPhone(usernumber);
                mEt_num.setText(usernumber);
                mEt_num.setSelection(usernumber.length());
                username = linkManBeanList.get(position).getUserName();
                mTv_address.setText(username);
                mTv_address.setTextColor(Color.parseColor("#000000"));
                dialogPhone.dismiss();
                linkManBeanList.clear();

                initPresenter();
                tv_city.setText("");
                getServiceData(usernumber.replace(" ", ""));
                presenter.didFinishLoading(PayMoneyActivity.this);
            }
        });
        lv_dialog.setAdapter(adapter);
        dialogPhone.show();
        lv_dialog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {

                final CheckBox cb_box = (CheckBox) view.findViewById(R.id.cb_box);
                cb_box.setClickable(true);


                // cb_box.setChecked(true);
                usernumber = linkManBeanList.get(i).getUserNumber();
//                usernumber = usernumber.replace(" ", "");
//                usernumber = usernumber.replace("-", "");
                usernumber = ContactManager.processMobileNumber(usernumber);
                usernumber = editPhone(usernumber);
                username = linkManBeanList.get(i).getUserName();
                mEt_num.setText(usernumber);
                mEt_num.setSelection(usernumber.length());
                mTv_address.setText(username);
                mTv_address.setTextColor(Color.parseColor("#000000"));
                dialogPhone.dismiss();
                linkManBeanList.clear();

                initPresenter();
                tv_city.setText("");
                getServiceData(usernumber.replace(" ", ""));
                presenter.didFinishLoading(PayMoneyActivity.this);
            }
        });


    }

    public void getServiceData(String mobileNumber) {
        //服务器数据交互操作
        HashMap<String, String> maps = new HashMap<>();
        maps.put("user_id", Utils.getUserId());
        maps.put("token", Utils.getToken());
        maps.put("mobileNumber", mobileNumber );
        //这里参数的含义

        CustomTask task = new CustomTask(mHandler, Constants.WHAT_QUERY_PAYPHONE,
                Constants.SEAWAY_QUERY_PAYPHONE,
                true, maps, true);
        task.execute();
    }
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            try {
                JsonBean jsonBean = (JsonBean) msg.obj;
                int code = jsonBean.getCode();
                String errorMsg = jsonBean.getError_msg();
                if (code == Constants.DATA_EVENT) {
                    hideProcessBar();
//                    Utils.makeToast(PaymentActivitySelf.this, Constants.ERROR_MSG_CODE + code);
                    return;
                }
                int action = msg.what;
                switch (action) {
                    case Constants.WHAT_QUERY_PAYPHONE:
                        boolean flag = Utils.filtrateCode(PayMoneyActivity.this, jsonBean);
                        if (flag && code == Constants.OPERATION_FAIL) {//数据交互失败
//                            Utils.makeToast(PaymentActivitySelf.this, errorMsg);
                        } else if (flag && code == Constants.OPERATION_SUCCESS) {//数据交互成功

//                            JSONObject jsonObject = new JSONObject(jsonBean.getJsonString());
                            JSONObject json = jsonBean.getJsonObject();
                            final JSONObject payPhone = json.optJSONObject("result");


                            area = payPhone.getString("area");
                            areaCode = payPhone.getString("areaCode");
                            operator = payPhone.getString("operator");

                            productsBeanList.clear();

                            JSONArray array =  payPhone.getJSONArray("products");


                            for (int i = 0;i<array.length();i++){
                                ProductsBean productsBean = new ProductsBean();
                                boolean onSale = array.getJSONObject(i).getBoolean("onSale");
                                productsBean.setOnSale(onSale);
                                if (onSale){

                                    productsBean.setSalePrice(array.getJSONObject(i).getString("salePrice"));
                                }
                                productsBean.setParValue(array.getJSONObject(i).getString("parValue"));
                                productsBeanList.add(productsBean);
                            }

                            if (mEt_num.getText().toString().length()==13){
                                tv_city.setText("("+payPhone.getString("operatorName")+")");
                                GVAdapter gvAdapter = new GVAdapter(PayMoneyActivity.this,productsBeanList);
                                mGv_pay.setAdapter(gvAdapter);

                                mGv_pay.setEnabled(true);

                                mGv_pay.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                                        if (productsBeanList.get(position).getSalePrice()!=null){
                                        parValue = productsBeanList.get(position).getParValue();
                                        //salePrice = productsBeanList.get(position).getSalePrice();
                                        Payment_money(PayMoneyActivity.this, Constants.WHAT_WITHDRAW, progressDialog);
                                        String newAmount = productsBeanList.get(position).getSalePrice();//这是售价
                                        cuAmount = new BigDecimal(newAmount).setScale(2).toString();
                                        //2015年11月23日17:53:02 测试 是否需要加判断
                                        if (!(cuAmount == null) && !(accumulative == null)) {
//                                            LogUtils.e("-----------难道走了这里？2-----------","这2");
                                            if (Double.valueOf(cuAmount).doubleValue() > Double.valueOf(accumulative).doubleValue()) {
                                                //Utils.makeToast_short(WaterqueryActivity.this, "“可用收益”不足");
                                                //2016年1月8日18:30:36  新加京东支付后的弹窗逻辑
                                                showDialog(area, areaCode, operator, parValue, mEt_num.getText().toString().replace(" ", ""));
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

                                            } else {
//                                                LogUtils.e("-----------难道走了这里？3-----------","这3");
                                                showDialog(area, areaCode, operator, parValue, mEt_num.getText().toString().replace(" ", ""));
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
                                                            if (!etpassword.hasFocus()) {
                                                                InputMethodManager inputmanger = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                                                inputmanger.hideSoftInputFromWindow(etpassword.getWindowToken(), 0);
                                                            }
                                                        }
                                                    }
                                                });
                                            }
                                        }
                                    }
                                    }
                                });
                            }else{
                                initPresenter();
                            }


                        }
                        hideProcessBar();
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
                hideProcessBar();
                LogUtils.e("PaymentActivitySelf", "handler 异常");
            }
        }
    };

    public void showDialog(final String area,  final String areaCode, final String operator, final String parValue, final String mobileNumber) {
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
                if (isChecked) {
                    isJdpay = true;
                    isCkeck =true;
                    showlog.dismiss();
                    presenter.didFinishLoading(PayMoneyActivity.this);
                    inputData(area, areaCode, operator, parValue, mEt_num.getText().toString().replace(" ", ""));

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




            requiredCost.setText(cuAmount + "元");

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
                    Utils.HideSoftKeyboardDialog(PayMoneyActivity.this, etpassword);
                    showlog.dismiss();
                    presenter.didFinishLoading(PayMoneyActivity.this);
                    inputData(area, areaCode, operator, parValue, mEt_num.getText().toString().replace(" ", ""));//下订单
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
                        boolean flag = Utils.filtrateCode(PayMoneyActivity.this, jsonBean);
                        if (flag && code == Constants.OPERATION_FAIL) {//数据交互失败
//                            Utils.makeToast(WaterqueryActivity.this, errorMsg);
                        } else if (flag && code == Constants.OPERATION_SUCCESS) {//数据交互成功
                            JSONObject jsonObject = new JSONObject(jsonBean.getJsonString());
                            accumulative = jsonObject.optString("receivableInterest");//可用收益



//                            LogUtils.e("-------------有没有取到利息值---------------", "accumulative：" + accumulative);

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
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };


//                    area	地区，例如：北京
//                    areaCode	地区编码
//                    operator	运营商编码，0：中国移动，1：中国联通，2：中国电信
//                    parValue	面值，单位元
//                    mobileNumber	11位手机号码
    /**
     *话费下订单
     */
    public void inputData(String area,  String areaCode, String operator, String parValue, String mobileNumber) {
        //服务器数据交互操作
        HashMap<String, String> maps = new HashMap<>();
        maps.put("user_id", Utils.getUserId());
        maps.put("token", Utils.getToken());
        maps.put("area", area );
        maps.put("areaCode", areaCode);
        maps.put("operator", operator);
        maps.put("parValue", parValue);
        maps.put("mobileNumber", mobileNumber);

//这里参数的含义
        CustomTask task = new CustomTask(payHandler, Constants.WHAT_PHONE_ORDER,
                Constants.SEAWAY_MOBILE_ORDER,
                true, maps, true);
        task.execute();
    }

    /**
     * 话费下订单（点击支付按钮后访问网络生成订单所用到的handler）
     */
    private Handler payHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            try {
                JsonBean jsonBean = (JsonBean) msg.obj;
                int code = jsonBean.getCode();
                //String errorMsg = jsonBean.getError_msg();
                String detSuccess = jsonBean.getJsonObject().optString("success");
                String toserrorMsg = jsonBean.getJsonObject().optString("errorMsg");
                if (code == Constants.DATA_EVENT) {
//                    Utils.makeToast(getApplicationContext(), Constants.ERROR_MSG_CODE + code);
                    return;
                }
                int action = msg.what;
                switch (action) {
                    case Constants.WHAT_PHONE_ORDER:
                        boolean flag = Utils.filtrateCode(getApplicationContext(), jsonBean);
                        if (flag && code == Constants.OPERATION_FAIL) {//数据交互失败
//                            Utils.makeToast(getApplicationContext(), errorMsg);
                            hideProcessBar();
                            Utils.makeToast_short(getApplicationContext(),toserrorMsg);

                        } else if (flag && code == Constants.OPERATION_SUCCESS) {//数据交互成功

                                JSONObject json = jsonBean.getJsonObject();
                                JSONObject payNo = json.optJSONObject("result");
                                mTime = payNo.getString("gmtCreated");//用户下订单的时间
                                mOrder = payNo.getString("orderNo");//用户的订单号

                            if (isJdpay){
                                detailData(mOrder,"2");



                            }else{
                                //判断是否有收益
                                if (!(cuAmount == null) && !(accumulative == null)) {
//                                            LogUtils.e("-----------难道走了这里？2-----------","这2");
                                    if (Double.valueOf(cuAmount).doubleValue() > Double.valueOf(accumulative).doubleValue()) {
                                        hideProcessBar();
                                        Utils.makeToast_short(PayMoneyActivity.this, "“可用收益”不足");
                                        return ;
                                    }
                                }
                                detailData(mOrder, "1", mloginPwd);
                            }


                        }
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
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

//这里参数的含义
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
            jsonObject.put("successUrl", Constants.ONLINE_HTTP+"util/paySuccess");
            jsonObject.put("failUrl", Constants.ONLINE_HTTP+"util/payFail");
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
//                            Utils.makeToast(PayMoneyActivity.this, toserrorMsg);
                            Utils.customToast(PayMoneyActivity.this,toserrorMsg, Toast.LENGTH_LONG);
                        } else if (flag && code == Constants.OPERATION_SUCCESS) {//数据交互成功
                            JSONObject json = jsonBean.getJsonObject();

                            JSONObject payNo = json.optJSONObject("result");

                            // String success = json.getString("success");

                            if (detSuccess.equals("1")) {
                                if (isJdpay) {
                                    //2016年1月25日18:22:37  京东支付
                                    JSONObject charge = payNo.getJSONObject("charge");
                                    String charge2 = charge.toString().replace("+", "%2B");
                                    //http://172.16.34.188:45680/wallet2/seaway/jdPay?charge={dsadsa
                                    Intent intent =new Intent(PayMoneyActivity.this, WebView.class);
                                    intent.putExtra("orderFrom",false);//这是判断是否从订单列表传过来
                                    intent.putExtra("code", 0);
                                    intent.putExtra("reCharge",true);
                                    intent.putExtra("pay", cuAmount + "元");
                                    intent.putExtra("operator", operator);
                                    intent.putExtra("orderNo", mOrder);
                                    intent.putExtra("time", mTime);
                                    intent.putExtra("parValue",parValue+"元");
                                    intent.putExtra("userNumber",mEt_num.getText().toString().replace(" ",""));
                                    intent.putExtra("payMethod",2);
                                    intent.putExtra("url", Constants.ONLINE_HTTP+"show/jdPay?charge=" + charge2);
                                    intent.putExtra("title", "京东支付");
                                    startActivity(intent);
//Constants.OFFLINE_HTTP+"show/jdPay?charge=" + newCharge
                                }else{

                                    Intent intent = new Intent(PayMoneyActivity.this, MobiledetailActivity.class);
                                    intent.putExtra("code", 0);
                                    intent.putExtra("pay", cuAmount + "元");
                                    intent.putExtra("operator", operator);
                                    intent.putExtra("orderNo", mOrder);
                                    intent.putExtra("time", mTime);
                                    intent.putExtra("parValue",parValue+"元");
                                    intent.putExtra("userNumber",mEt_num.getText().toString().replace(" ",""));
                                    intent.putExtra("payMethod",1);
                                    //把用户选择的城市传递给订单详情界面
                                    //intent.putExtra("twoUnit", twoUnit);
                                    startActivity(intent);
                                    // presenter.didFinishLoading(PayMoneyActivity.this);
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


    private void initPopupWindow() {

        ListView contentView = new ListView(this);
        contentView.setDividerHeight(1);
        contentView.setBackgroundResource(R.drawable.listview_background);


        adapter = new MyAdapter(arrayList);

        contentView.setAdapter(adapter);
        if (arrayList.size() <= 3) {
            //ActionBar.LayoutParams.WRAP_CONTENT   条目小于3条时随条目的个数自使用高度
            popupWindow = new PopupWindow(contentView,ActionBar.LayoutParams.MATCH_PARENT , ActionBar.LayoutParams.WRAP_CONTENT);
        } else {
            //当条目大于3条时给它固定高度让它可以进行滑动
            popupWindow = new PopupWindow(contentView,ActionBar.LayoutParams.MATCH_PARENT, 560);
        }


        contentView.setOnItemClickListener(this);



        popupWindow.setOutsideTouchable(true);
        // 设置空白的背景
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
//      设置显示PopupWindow的位置位于View的相对位置
        popupWindow.showAsDropDown(mEt_num, 0, 20);
    }




    //将点击的条目显示在EditText上
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        // 当条目被点击时, 获取内容, 显示到EditText

        isInput = false;
        String usernumber = arrayList.get(position).getUserNumber();
//        usernumber = usernumber.replace(" ", "");
//        usernumber = usernumber.replace("-", "");
        usernumber = ContactManager.processMobileNumber(usernumber);
        if (usernumber.startsWith("+86")){
            usernumber = usernumber.substring(3, usernumber.length());

        }
        usernumber = editPhone(usernumber);
        String username = arrayList.get(position).getUserName();
        mEt_num.setText(usernumber);
        length =  mEt_num.getText().length();
       // LogUtils.e("llllllllllllllllllllll", "mEt_num.getText().length()" + mEt_num.getText().length());
        mEt_num.setSelection(usernumber.length());
        mTv_address.setText(username);
        if (usernumber.replace(" ","").equals(Utils.getUserPhone(PayMoneyActivity.this))){
            mTv_address.setTextColor(Color.parseColor("#83d62f"));
        }else{
            mTv_address.setTextColor(Color.parseColor("#000000"));
        }
        popupWindow.dismiss();



        initPresenter();
        tv_city.setText("");
        presenter.didFinishLoading(PayMoneyActivity.this);
        getServiceData(usernumber.replace(" ", ""));


    }

    //数据适配器的填充，并且实现当数据不存在时，隐藏悬浮框
    class MyAdapter extends BaseAdapter {
        ArrayList<Phone> phoneInfoArrayList = new ArrayList<>();
        public MyAdapter(ArrayList<Phone> infos) {
            this.phoneInfoArrayList = infos;

        }

        @Override
        public int getCount() {
            return phoneInfoArrayList.size();
        }



        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view;
            if (convertView == null) {
                view = View.inflate(PayMoneyActivity.this, R.layout.item_think, null);
            } else {
                view = convertView;
            }
            TextView tv_month = (TextView) view.findViewById(R.id.tv_month);
            TextView tv_balance = (TextView) view.findViewById(R.id.tv_balance);

            tv_month.setText(phoneInfoArrayList.get(position).getUserNumber());
            tv_balance.setText(phoneInfoArrayList.get(position).getUserName());
           // LogUtils.e("aaaaaa     size     aaaaaaa", "arrayList.get(position).getUserNumber()" + arrayList.get(position).getUserNumber());


            return view;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }
    }


    @Override
    protected void onStart() {
        //LogUtils.e("lllllllllll","onStart");
        if (Utils.getUserId() != "" && Utils.getLockPassword(this, Utils.getUserPhone(this)) != "" && Constants.GESTURES_PASSWORD) {//用于判断是否进入手势输入页面
            Intent intent = new Intent();
            intent.setClass(this, GestureVerifyActivity.class);
            startActivity(intent);
        }
        super.onStart();
    }

    @Override
    protected void onRestart() {
        Constants.GESTURES_PASSWORD = false;
        //LogUtils.e("lllllllllll","onRestart");
        super.onRestart();
    }

    @Override
    protected void onPause() {
        Constants.GESTURES_PASSWORD = false;
        //LogUtils.e("lllllllllll","onPause");
        super.onPause();
    }


}
