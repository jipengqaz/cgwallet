package cgtz.com.cgwallet.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import cgtz.com.cgwallet.MApplication;
import cgtz.com.cgwallet.R;
import cgtz.com.cgwallet.bean.JsonBean;
import cgtz.com.cgwallet.client.Is_passwrod;
import cgtz.com.cgwallet.utils.AppUtil;
import cgtz.com.cgwallet.utils.Utils;

/**
 * 选择手势密码是否开启
 * Created by Administrator on 2014/10/15.
 */
public class OpenSudokoUnlockActivity extends BaseActivity {
    private static final String TAG = "OpenSudokoUnlockActivity";
    private ImageView tv_openLock;
    private LinearLayout tv_againSet;
    private boolean flag;
    private boolean mIsDestroy = false;//判断Activity是否被关闭，防止系统销毁了Activity，还去加载Dialog的错误
    private LayoutInflater mInflater;
    private TextView errorHint;//输入登录密码错误提示
    private static Dialog mDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("管理手势密码");
        setContentView(R.layout.activity_open_sudoko_unlock);
        MApplication.registActivities(this);//存储该activity
        showBack(true);
        init();
        setViewsListener();


        mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    /**
     * 初始化视图
     */
    private void init(){
        tv_openLock = (ImageView) findViewById(R.id.tv_open_lock);
        tv_againSet = (LinearLayout) findViewById(R.id.tv_open_sudoko_unlock_again);
        flag = Utils.getLockPassword(this,Utils.getUserPhone(this))!="";

        if(!flag){
            tv_openLock.setImageResource(R.mipmap.lock_open);
            tv_againSet.setVisibility(View.VISIBLE);
        }else{
            tv_openLock.setImageResource(R.mipmap.lock_close);
            tv_againSet.setVisibility(View.GONE);
        }
    }
    /**
     * 视图添加事件
     */
    private void setViewsListener(){
        /**
         * 手势密码的开启和关闭事件
         */
        tv_openLock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flag = Utils.getLockPassword(OpenSudokoUnlockActivity.this, Utils.getUserPhone(OpenSudokoUnlockActivity.this)) != "";
                Log.e("aaaa", flag + "");
                if (!flag) {
                    Intent intent = new Intent(OpenSudokoUnlockActivity.this, GestureEditActivity.class);
                    startActivity(intent);
                } else {
                    mDialog = new Dialog(OpenSudokoUnlockActivity.this,
                            R.style.loading_dialog2);//填写登录密码提示框
                    View linearLayout = mInflater.inflate(R.layout.sodoko_unlock_pwd_dialog, null);
                    TextView login_phone = (TextView) linearLayout.findViewById(R.id.tv_sodoko_login_phone);
                    final EditText login_pwd = (EditText) linearLayout.findViewById(R.id.et_sodoko_login_pwd);
                    TextView confirm = (TextView) linearLayout.findViewById(R.id.tv_sodoko_confirm);
                    errorHint = (TextView) linearLayout.findViewById(R.id.tv_sodoko_error_hint);
                    mDialog.setContentView(linearLayout);
//        mDialog.setCanceledOnTouchOutside(false);
                    mDialog.show();
                    login_phone.setText(Utils.getProtectedMobile(Utils.getUserPhone(OpenSudokoUnlockActivity.this)));
                    confirm.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            errorHint.setVisibility(View.VISIBLE);
                            errorHint.setText("正在判断登录密码...");
                            Is_passwrod.isPasswrod(mHandler, login_pwd.getText().toString(), 0);
                        }
                    });
                }
            }
        });
        /**
         * 重置密码
         */
        tv_againSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog();
            }
        });
    }

    /**
     * 用于取消手势时判断登录密码的返回参数
     */
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            JsonBean jsonBean = (JsonBean) msg.obj;
            try {
                JSONObject json = new JSONObject(jsonBean.getJsonString());
                String status = json.getString("success");
                if ("0".equals(status)) {
                    errorHint.setText(json.optString("msg"));
                }else{
                    Utils.removePassWord(OpenSudokoUnlockActivity.this,Utils.getUserPhone(OpenSudokoUnlockActivity.this));
                    tv_openLock.setImageResource(R.mipmap.lock_close);
                    tv_againSet.setVisibility(View.GONE);
                    mDialog.dismiss();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };
    @Override
    protected void onResume() {
        super.onResume();
        flag = Utils.getLockPassword(this,Utils.getUserPhone(this))!="";
        if(flag){
            tv_openLock.setImageResource(R.mipmap.lock_open);
            tv_againSet.setVisibility(View.VISIBLE);
        }else{
            tv_openLock.setImageResource(R.mipmap.lock_close);
            tv_againSet.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mIsDestroy = true;
    }

    /**
     * 修改手势密码，登录用户密码
     */
    private void getDialog(){
        if(mIsDestroy){
            return;
        }
        AppUtil.isPasswroid(this, mInflater, false);
    }
}
