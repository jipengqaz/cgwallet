package cgtz.com.cgwallet.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cgtz.com.cgwallet.MApplication;
import cgtz.com.cgwallet.R;
import cgtz.com.cgwallet.bean.BankCard;
import cgtz.com.cgwallet.bean.JsonBean;
import cgtz.com.cgwallet.utility.Constants;
import cgtz.com.cgwallet.utils.CustomTask;
import cgtz.com.cgwallet.utils.Ke_Fu_data;
import cgtz.com.cgwallet.utils.LogUtils;
import cgtz.com.cgwallet.utils.Utils;
import cgtz.com.cgwallet.widget.CustomEffectsDialog;
import cgtz.com.cgwallet.widget.ProgressDialog;
import cn.jpush.android.api.JPushInterface;

/**
 * 绑定的银行卡列表页面
 * Created by Administrator on 2014/10/17.
 */
public class BankCardListActivity extends BaseActivity {
    private static final String TAG = "BankCardListActivity";
    private ListView lv_bankcard;
    private List<BankCard> lists;
    private BankCardAdapter bcAdapter;
    private CustomTask bankTask;
    private ProgressDialog pDialog;
    private boolean flag = false;//用于判断是否从个人中心页面跳转过来
    private CustomEffectsDialog effectsDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("银行卡");
        MApplication.registActivities(this);
        setContentView(R.layout.activity_bankcard_list);
        if(savedInstanceState != null){
            flag = savedInstanceState.getBoolean("person",false);
        }else{
            flag = getIntent().getBooleanExtra("person",false);
        }
        showBack(true);
        lists = new ArrayList<BankCard>();
        if(Utils.isLogined()){
            init();
            setViewListener();
            pDialog = new ProgressDialog(this, R.style.loading_dialog);
            pDialog.setMessage("正在加载绑定银行卡...");
            pDialog.show();
            Map<String,String> maps = new HashMap<String, String>();
            maps.put("user_id",Utils.getUserId()+"");
            maps.put("token",Utils.getToken());
            bankTask = new CustomTask(mHanlder, Constants.WHAT_BANK_LIST_CASE,
                    Constants.URL_BANK_LIST,
                    true,maps,true);
            bankTask.execute();
        }else{
            startActivity(new Intent(this, LoginActivity.class));
        }
    }

    /**
     * 初始化视图
     */
    private void init(){
        lv_bankcard = (ListView) findViewById(R.id.lv_bankcard_list);
    }

    /**
     * 视图添加事件
     */
    private void setViewListener(){
//        if(!flag){
//            lv_bankcard.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                @Override
//                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                    BankCard bank = (BankCard) adapterView.getItemAtPosition(i);
//                    Intent intent = new Intent(BankCardListActivity.this,WithdrawActivity.class);
//                    intent.putExtra("bank",bank);
//                    setResult(RESULT_OK,intent);
//                    finish();
//                }
//            });
//        }
    }

    private Handler mHanlder = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            JsonBean jsonBean = (JsonBean) msg.obj;
            if(!Utils.filtrateCode(BankCardListActivity.this,jsonBean)){
                return;
            }
            JSONObject json = jsonBean.getJsonObject();
            switch (msg.what){
                case Constants.WHAT_BANK_LIST_CASE://获取绑定银行卡列表返回信息分析
                    try {
                        closeDialog();//关闭进度Dialog
                        LogUtils.i(TAG, "获取绑定银行卡列表返回信息分析: " + json);
                        if(bankTask != null && bankTask.getStatus() == AsyncTask.Status.RUNNING){
                            bankTask.cancel(true);
                        }
                            String status = json.getString("success");
                                if ("0".equals(status)) {
                                    //获取绑定银行卡失败
                                    if(flag){
                                        flag = false;
                                        finish();
                                    }else{
                                        Intent intent = new Intent(BankCardListActivity.this,MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);
                                        finish();
                                    }
                                    Utils.makeToast(BankCardListActivity.this,json.optString("msg"));
                                } else if(status.equals("1")){
                                    //获取绑定银行卡成功
                                    JSONArray array = json.getJSONArray("cardinfo");
                                    BankCard bankCard = null;
                                    lists.clear();
                                    if(array != null && array.length()>0){
                                        for(int i = 0;i<array.length();i++){
                                            JSONObject cardinfo = array.getJSONObject(i);
                                            bankCard = new BankCard(cardinfo);
                                            lists.add(bankCard);
                                        }
                                        bcAdapter = new BankCardAdapter(BankCardListActivity.this);
                                        lv_bankcard.setAdapter(bcAdapter);
                                    }else{
                                        if(flag){
                                            flag = false;
                                            finish();
                                        }else{
                                            Intent intent = new Intent(BankCardListActivity.this,MainActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }
                            }
                    } catch (Exception e) {
                        e.printStackTrace();
                        finish();
                    }
                    break;
                case Constants.WHAT_BANKCARD_UNBIND:
                    //注销银行卡返回确认信息
                    try{
                        closeDialog();//关闭进度Dialog
                        LogUtils.i(TAG,"注销银行卡返回确认信息: "+msg.obj.toString());
                        if(json != null){
                            int success = json.optInt("success");
                            if(success == 1){
                                Utils.makeToast_short(BankCardListActivity.this,
                                        json.optString("msg"));
                                finish();
                            }else if(success == -1){
                                Utils.makeToast_short(BankCardListActivity.this,
                                        json.optString("tips"));
                            }
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                        Utils.makeToast_short(BankCardListActivity.this, getString(R.string.error_exception));
                        finish();
                    }
                    break;
            }
        }
    };

    class BankCardAdapter extends BaseAdapter{
        private LayoutInflater mInflater;

        public BankCardAdapter(Context context){
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return lists == null?0:lists.size();
        }

        @Override
        public Object getItem(int i) {
            return lists == null?null:lists.get(i);
        }

        @Override
        public long getItemId(int i) {
            return lists == null?0:i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            Holder holder = null;
            if(view == null){
                holder = new Holder();
                view = mInflater.inflate(R.layout.item_binding_bankcard,null);
                holder.bankIcon = (ImageView) view.findViewById(R.id.iv_binding_bank_icon);
                holder.bankName = (TextView) view.findViewById(R.id.tv_binding_bank_name);
                holder.bankType = (TextView) view.findViewById(R.id.tv_binding_bank_type);
                holder.bankCard = (TextView) view.findViewById(R.id.tv_binding_bank_card);
                holder.logOut = (TextView) view.findViewById(R.id.tv_binding_logout);
                view.setTag(holder);
            }else{
                holder = (Holder) view.getTag();
            }
            if(lists != null){
                final BankCard bb = lists.get(i);

                holder.bankName.setText(Utils.isEmpty(bb.getBankName()));
                holder.bankCard.setText("尾号"+bb.getCardNumber().substring(bb.getCardNumber().length()-4));
                if(bb.getBankIcon(Integer.parseInt(bb.getBankId())) != 0){
                    holder.bankIcon.setImageResource(bb.getBankIcon(Integer.parseInt(bb.getBankId())));
                }
                if(flag){
                    holder.logOut.setVisibility(View.VISIBLE);
                }

                holder.logOut.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        effectsDialog = CustomEffectsDialog.getInstans(BankCardListActivity.this);
                        effectsDialog.withTitle("提示 ");
                        effectsDialog.withMessageColor(getResources().getColor(R.color.dialog_msg_color));
                        effectsDialog.withBtnLineColor(R.color.bg_line);
                        if (bb.getLlMark() == 0 && bb.getUmpMark() == 0) {
                            effectsDialog.withMessage(getString(R.string.bank_not_logout_msg_ok));
                            effectsDialog.withButton1Text("取消");
                            effectsDialog.withButton1Click(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    effectsDialog.dismiss();
                                }
                            });
                            effectsDialog.withBtnContentLineColor(R.color.bg_line);
                            effectsDialog.withButton2Text("确定注销");
                            effectsDialog.withButton2Click(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    //注销操作
                                    closeDialog();
                                    if (pDialog == null) {
                                        pDialog = new ProgressDialog(BankCardListActivity.this,
                                                R.style.loading_dialog);
                                    }
                                    pDialog.setMessage("银行卡注销操作中");
                                    pDialog.show();
                                    Map<String, String> maps = new HashMap<String, String>();
                                    maps.put("user_id", Utils.getUserId());
                                    maps.put("token", Utils.getToken());
                                    maps.put("card_id", bb.getCard_id());
                                    CustomTask task = new CustomTask(mHanlder, Constants.WHAT_BANKCARD_UNBIND,
                                            Constants.URL_BANKCARD_UNBIND,
                                            true, maps, true);
                                    task.execute();
                                }
                            });
                        } else {
                            effectsDialog.withMessage(getString(R.string.bank_not_logout_msg) + Ke_Fu_data.getPhone(BankCardListActivity.this));
                            effectsDialog.withBtnLineColor(R.color.bg_line);
                            effectsDialog.withBtnContentLineColor(R.color.bg_line);
                            effectsDialog.withButton1Text("取消");
                            effectsDialog.withButton1Click(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    effectsDialog.dismiss();
                                }
                            });
                            effectsDialog.withButton2Text("拨号");
                            effectsDialog.withButton2Click(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent();
                                    intent.setAction("android.intent.action.DIAL");
                                    intent.setData(Uri.parse("tel:" + Ke_Fu_data.getPhone(BankCardListActivity.this)));
                                    startActivity(intent);
                                    effectsDialog.dismiss();
                                }
                            });
                            effectsDialog.setCanceledOnTouchOutside(false);
                            effectsDialog.setCancelable(false);
                        }
                        effectsDialog.show();
                    }
                });
            }
            return view;
        }

        private class Holder{
            private TextView bankName;
            private TextView bankType;
            private TextView bankCard;
            private TextView logOut;
            private ImageView bankIcon;
        }
    }
    /**
     * Activity被系统杀死时被调用.
     * 例如:屏幕方向改变时,Activity被销毁再重建;当前Activity处于后台,系统资源紧张将其杀死.
     * 另外,当跳转到其他Activity或者按Home键回到主屏时该方法也会被调用,系统是为了保存当前View组件的状态.
     * 在onPause之前被调用.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean("person",flag);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        JPushInterface.onResume(this);
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        JPushInterface.onPause(this);
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {

        if(effectsDialog != null && effectsDialog.isShowing()){
            effectsDialog.dismiss();
        }
        super.onDestroy();
    }

    /**
     * 关闭进度Dialog
     */
    private void closeDialog(){
        if(pDialog!=null && pDialog.isShowing()){
            pDialog.dismiss();
        }
    }

}
