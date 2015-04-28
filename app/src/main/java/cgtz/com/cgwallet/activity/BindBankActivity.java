package cgtz.com.cgwallet.activity;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cgtz.com.cgwallet.R;
import cgtz.com.cgwallet.adapter.BankAdapter;
import cgtz.com.cgwallet.bean.Bank;
import cgtz.com.cgwallet.bean.BankCard;
import cgtz.com.cgwallet.bean.JsonBean;
import cgtz.com.cgwallet.client.Bank_msg;
import cgtz.com.cgwallet.utility.Constants;
import cgtz.com.cgwallet.utils.Utils;
import cgtz.com.cgwallet.widget.ProgressDialog;

/**
 * Created by Administrator on 2015/4/28 0028.
 */
public class BindBankActivity extends BaseActivity implements View.OnClickListener{

    private ArrayList<Bank> provinces_list,cities_list,bank_list;//存放省   和市的list   开户行
    private String bank_branch_name,bank_branch_name_test;//存放支行全名
    private String bankname;//存放选择item
    private SharedPreferences preferences;//存放省和市的XML
    private ProgressDialog pd;
    private ImageView bank_image;
    private TextView card_id;//显示身份证和姓名的
    private String pwd;//用户交易密码
    private Dialog bank_dialog;//银行卡确认和输入交易密码弹窗
    private Button determine;//确定
    private TextView bank_name,province,the_city,bank;//选择银行   银行卡号   省    市   开户行
    private EditText with_draw_num;//银行卡号
    private TextView back_text_1;//文案
    private String title2;//存放是选什么的
    private TextView testview;//存放点击的控件
    private Button ensure_btn;//选择银行确定
    private ListView listview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("完善银行卡");
        setContentView(R.layout.activity_bind_bank_card);
        init();
        assignment();
    }

    private void assignment() {
        with_draw_num.setFocusable(false);
        bank_name.setFocusable(false);
        back_text_1.setText(getIntent().getStringExtra("tip"));//设置文案
        with_draw_num.setText(getIntent().getStringExtra("starBankAccount"));//设置银行卡号
        bank_name.setText(getIntent().getStringExtra("bankName"));//设置银行名
        bank_image.setImageResource(BankCard.getBankIcon(getIntent().getIntExtra("bank_id", 0)));
        bank_image.setVisibility(View.VISIBLE);//显示银行图标
        card_id.setText(getIntent().getStringExtra("starName") + "（" + getIntent().getStringExtra("starIdentity") + "）");//设置用户信息
    }

    /**
     * 初始化控件
     */
    private void init() {
        bank_name = (TextView) findViewById(R.id.bank_name);//选择银行
        with_draw_num = (EditText) findViewById(R.id.with_draw_num);//银行卡号
        province = (TextView) findViewById(R.id.province);//省
        the_city = (TextView) findViewById(R.id.the_city);//市
        bank = (TextView) findViewById(R.id.bank);//开户行
        determine = (Button) findViewById(R.id.determine);//确定
        bank_image = (ImageView) findViewById(R.id.bank_image);//银行卡图标
        card_id = (TextView) findViewById(R.id.card_id);//显示身份证和姓名的
        back_text_1 = (TextView) findViewById(R.id.back_text_1);//文案
    }
    /**
     * 解析Json数字
     * */
    private ArrayList<Bank> getProvince(ArrayList<Bank> banklist,String key, SharedPreferences preferences) {
        if(banklist != null && banklist.size()>0){
            banklist.clear();
        }
        String aa = preferences.getString(key, null);
        if (aa != null) {
            try {
                JSONArray array = new JSONArray(aa);
                int arr = array.length();
                for (int i = 0; i < arr; i++) {//解析Json数组
                    Bank test = new Bank();
                    test.setName(array.optString(i));
                    banklist.add(test);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return banklist;
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case  R.id.province://省
                choose_Bank_Card(R.id.province,provinces_list);
                break;
            case  R.id.the_city://市
                if(cities_list!=null){
                    choose_Bank_Card(R.id.the_city,cities_list);
                }else{
                    Utils.makeToast(BindBankActivity.this, "请先选择开户省");
                }
                break;
            case  R.id.bank://开户行
                if(bank_list!=null && !bank_list.isEmpty()){
                    choose_Bank_Card(R.id.bank,bank_list);
                }else{
                    Utils.makeToast(BindBankActivity.this, "请选择市");
                }
                break;
            case R.id.determine://确定
                    if(with_draw_num.getText().toString().length()< 14){//判断是否填写银行卡号
                        Utils.makeToast(BindBankActivity.this, "请正确填写你的银行卡号");
                    }else{
                            if("点击选择".equals(province.getText())){//判断是否选择开户省
                                Utils.makeToast(BindBankActivity.this, "请选择开户省");
                            }else{
                                if("点击选择".equals(the_city.getText())){//判断是否选择开户市
                                    Utils.makeToast(BindBankActivity.this, "请选择开户市");
                                }else{
                                    if("点击选择".equals(bank.getText())){
                                        Utils.makeToast(BindBankActivity.this, "请选择开户支行");
                                    }else{
                                            if (pd == null)
                                                pd = new ProgressDialog(BindBankActivity.this, R.style.loading_dialog);//显示加载弹窗
                                            if (pd != null) {
                                                pd.setMessage("加载中.....");
                                                pd.show();
                                            }
                                            //完善银行卡信息用的
                                            Bank_msg.update_bank(handler, getIntent().getStringExtra("card_id"), province.getText().toString(),
                                                    the_city.getText().toString(), bank_branch_name, Constants.WHAT_UPDATE_BANK);
                                    }
                                }
                            }
                }
                break;
        }
    }
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(pd.isShowing()){
                pd.dismiss();
            }
            JsonBean jsonBean = (JsonBean) msg.obj;
            int code = jsonBean.getCode();
            String errorMsg = jsonBean.getError_msg();
            if(!Utils.filtrateCode(BindBankActivity.this,jsonBean)){
                Utils.makeToast(BindBankActivity.this, errorMsg + "  错误码" + code);
                return;
            }
            int action = msg.what;
            JSONObject json = null;
            switch (action){
                case  Constants.WHAT_UPDATE_BANK://完善银行卡信息
                    json = jsonBean.getJsonObject();
                    if(json != null){
                        int success = json.optInt("success");
                        if(success == 1){
                            Utils.makeToast(BindBankActivity.this, json.optString("msg"));
                            finish();
                        }else{
                            Utils.makeToast(BindBankActivity.this, json.optString("msg"));
                        }
                    }else{
                        Utils.makeToast(BindBankActivity.this, "获取数据失败");
                    }
                    break;
                case Constants.WHAT_BRANCH://获取城市支行
                    json = jsonBean.getJsonObject();
                    if (json != null) {
                        bank_list.clear();//清空list中的数据
                        int success = json.optInt("success");
                        if (success == 1) {
                            JSONArray array = json.optJSONArray("branches");
                            int arr = array.length();
                            for (int i = 0; i < arr; i++) {
                                Bank ban = new Bank();
                                JSONObject test = array.optJSONObject(i);
                                ban.setName(test.optString("show"));//存储显示的简称
                                ban.setValue(test.optString("value"));//存储全称
                                bank_list.add(ban);
                            }
                        } else {
                            testview.setText(bankname+"（该市无支行）");
                            Utils.makeToast(BindBankActivity.this, "请先选择其他市,该市无支行");
                        }
                    } else {
                        Utils.makeToast(BindBankActivity.this, "获取失败，请重新选择获取");
                    }
                    break;
            }
        }
    };
    /**
     选择弹出框
     */
    private void choose_Bank_Card(final int i,ArrayList<Bank> list_array){
        bankname=null;//初始化
        bank_branch_name = null;//初始化
        testview = (TextView) findViewById(i);
        LinearLayout choose_bank_card_layout =
                (LinearLayout) LayoutInflater.from(BindBankActivity.this).inflate(R.layout.activity_choose_bank_card,null);
        final Dialog choose_bank_card_dialog = new Dialog(BindBankActivity.this,R.style.loading_dialog2);
        ensure_btn = (Button) choose_bank_card_layout.findViewById(R.id.btn_confirm);//确定按钮
        listview = (ListView) choose_bank_card_layout.findViewById(R.id.lv_bankname);
        TextView title = (TextView) choose_bank_card_layout.findViewById(R.id.title);//标题
        switch (i){
            case  R.id.province://省
                title2="请选择开户省";
                break;
            case  R.id.the_city://市
                title2="请选择开户市";
                break;
            case  R.id.bank://开户行
                title2="请选择开户行";
                break;
        }
        title.setText(title2);
        BankAdapter bAdapter = new BankAdapter(this,list_array);
        listview.setAdapter(bAdapter);
        listview.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {//存储选择的银行名
                Bank bank1 = (Bank) parent.getItemAtPosition(position);
                bankname = bank1.getName();
                    bank_branch_name_test = bank1.getValue();//获得开户行的全称

                testview.setText(bankname);
                switch (i) {
                    case R.id.province://省
                        cities_list = getProvince(cities_list, province.getText() + "", preferences);//获得当前选择省下的市
                        the_city.setText("点击选择");//还原为初始状态
                        bank.setText("点击选择");//选择开户行还原为初始状态
                        bank_list.clear();//清空开户行的list中的数据
                        break;
                    case R.id.the_city://市
                        bank.setText("点击选择");//还原为初始状态
                        pd = new ProgressDialog(BindBankActivity.this, R.style.loading_dialog);//加载界面
                        if (pd != null) {
                            pd.setMessage("正在获取该市支行...");
                            pd.show();
                        }
                        String[] aa = {province.getText() + "", the_city.getText() + "", bank_name.getText() + ""};
                        Bank_msg.getBranch(aa,handler,Constants.WHAT_BRANCH);//获取该市分行
                        break;
                    case R.id.bank://开户行
                        bank_branch_name = bank_branch_name_test;
                        break;
                }
                choose_bank_card_dialog.dismiss();
            }

        });
        //将列表中选中的银行填写到选择银行的TextView中
        choose_bank_card_dialog.setContentView(choose_bank_card_layout);
        choose_bank_card_dialog.show();
        ensure_btn.setVisibility(View.GONE);
        ensure_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bankname!=null){
                    testview.setText(bankname);
                }else{
                    Utils.makeToast(BindBankActivity.this, title2);
                }
            }
        });
    }
}
