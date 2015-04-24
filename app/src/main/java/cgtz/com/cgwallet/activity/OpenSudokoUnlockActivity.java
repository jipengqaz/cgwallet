package cgtz.com.cgwallet.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import cgtz.com.cgwallet.R;
import cn.jpush.android.api.JPushInterface;

/**
 * 选择手势密码是否开启
 * Created by Administrator on 2014/10/15.
 */
public class OpenSudokoUnlockActivity extends BaseActivity {
    private static final String TAG = "OpenSudokoUnlockActivity";
    private ImageView tv_openLock;
    private LinearLayout tv_againSet;
    private boolean flag;
    private LayoutInflater mInflater;
    private String phone;
    private Dialog mDialog;
    private TextView errorHint;
    private String username;
    private boolean mIsDestroy = false;//判断Activity是否被关闭，防止系统销毁了Activity，还去加载Dialog的错误
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("设置手势密码");
        setContentView(R.layout.activity_open_sudoko_unlock);
        init();
        setViewsListener();
    }
    /**
     * 初始化视图
     */
    private void init(){
        tv_openLock = (ImageView) findViewById(R.id.tv_open_lock);
        tv_againSet = (LinearLayout) findViewById(R.id.tv_open_sudoko_unlock_again);
        if(flag){
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
                Intent intent = new Intent(OpenSudokoUnlockActivity.this,GestureEditActivity.class);
                startActivity(intent);
            }
        });
        /**
         * 重置密码
         */
        tv_againSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        JPushInterface.onResume(this);
        MobclickAgent.onResume(this);
//        flag = (Boolean)usp.getMsg(Constants.BOOLEAN,
//                Constants.LOCK_ISOPEN + phone,false);
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
        JPushInterface.onPause(this);
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mIsDestroy = true;
//        if(loginTask != null && loginTask.getStatus() == AsyncTask.Status.RUNNING){
//            loginTask.cancel(true);
//        }
    }

    /**
     * 修改手势密码，登录用户密码
     */
    private void getDialog(){
        if(mIsDestroy){
            return;
        }
//        mDialog = new Dialog(OpenSudokoUnlockActivity.this,
//                R.style.loading_dialog_right_angle);//填写登录密码提示框
//        View linearLayout = mInflater.inflate(R.layout.sodoko_unlock_pwd_dialog,null);
//        TextView login_phone = (TextView) linearLayout.findViewById(R.id.tv_sodoko_login_phone);
//        final EditText login_pwd = (EditText) linearLayout.findViewById(R.id.et_sodoko_login_pwd);
//        TextView confirm = (TextView) linearLayout.findViewById(R.id.tv_sodoko_confirm);
//        errorHint = (TextView) linearLayout.findViewById(R.id.tv_sodoko_error_hint);
//        mDialog.setContentView(linearLayout);
//        mDialog.setCanceledOnTouchOutside(false);
//        mDialog.show();
//        login_phone.setText(UtilityUtils.subStarPhone(phone));
//        confirm.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                errorHint.setVisibility(View.VISIBLE);
//                errorHint.setText("正在判断登录密码...");
//                User user = UserSession.getInstance().getLoginedUser();
//                Map<String,String> maps = new HashMap<String, String>();
//                maps.put("user_id",user.getUserID()+"");
//                maps.put("token",user.getToken()+"");
//                maps.put("password",MD5Util.md5(login_pwd.getText().toString()));
//                loginTask = new ClientAsyncTask(
//                        Constants.AGAINLOGIN,
//                        Constants.AGAINLOGIN_URL, mHandler);
//                loginTask.execute(maps);
//                Password_judgment_Client.Password_judgment(mHandler, MD5Util.md5(login_pwd.getText().toString()), Constants.AGAINLOGIN);
//            }
//        });
    }

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
//            UtilityUtils.isNeedFinish(msg.obj,false,OpenSudokoUnlockActivity.this);
//            switch (msg.what){
//                case Constants.AGAINLOGIN:
//                    try {
//                        DebugUtils.i(TAG,"result = "+msg.obj.toString());
//                        if(loginTask != null && loginTask.getStatus() == AsyncTask.Status.RUNNING){
//                            loginTask.cancel(true);
//                        }
//                        if (UtilityUtils.isHaveMsg(OpenSudokoUnlockActivity.this,msg.obj)) {
//                            JSONObject json = new JSONObject(msg.obj.toString());
//                            String status = json.getString("success");
//                            if ("0".equals(status)) {
//                                //输入密码不正确
//                                String action = json.optString("action");
//                                if ("login".equalsIgnoreCase(action)) {
//                                    UserSession.getInstance().logout();
//                                    UtilityUtils.startLoginActivity(OpenSudokoUnlockActivity.this,true);//登录
//                                    return;
//                                }else if("maintain".equals(action)){
//                                    //系统维护中
//                                    final ServerMainTainDialog maintainDialog =
//                                            ServerMainTainDialog.getInstans(OpenSudokoUnlockActivity.this);
//                                    maintainDialog.setCancelable(false);
//                                    maintainDialog.setCanceledOnTouchOutside(false);
//                                    maintainDialog.withMaintainIconClick(new View.OnClickListener() {
//                                        @Override
//                                        public void onClick(View view) {
//                                            maintainDialog.dismiss();
//                                            System.exit(0);
//                                        }
//                                    });
//                                    maintainDialog.show();
//                                    return;
//                                }
//                                errorHint.setText(json.optString("msg"));
//                            }else {
//                                //密码正确
//                                if(flag){
//                                    tv_openLock.setImageResource(R.drawable.lock_close);
//                                    tv_againSet.setVisibility(View.GONE);
//                                    usp.putMsg(Constants.STRING, Constants.LOCK_DRAW_PWD + phone, "");
//                                    usp.putMsg(Constants.BOOLEAN, Constants.LOCK_ISOPEN + phone, false);
//                                    mDialog.dismiss();
//                                }else{
//                                    Intent intent = new Intent(OpenSudokoUnlockActivity.this,
//                                            SetSudokoUnlockActivity.class);
//                                    intent.putExtra(Constants.ISAGGIN,true);//重置手势
//                                    intent.putExtra(Constants.USERNAME,username);
//                                    intent.putExtra(Constants.MOBILEPHONE, phone);
//                                    startActivity(intent);
//                                    mDialog.dismiss();
//                                }
//                            }
//                        }
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                        finish();
//                    }
//                    break;
//            }
        }
    };
}
