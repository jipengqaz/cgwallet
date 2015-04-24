/**
 *
 */
package cgtz.com.cgwallet.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import cgtz.com.cgwallet.R;
import cgtz.com.cgwallet.activity.GestureEditActivity;
import cgtz.com.cgwallet.bean.JsonBean;
import cgtz.com.cgwallet.client.Is_passwrod;

public class AppUtil {



    /**
     * 获取屏幕分辨率
     * @param context
     * @return
     */
    public static int[] getScreenDispaly(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        //获取屏幕信息
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;;// 手机屏幕的宽度
        int height =dm.heightPixels;// 手机屏幕的高度
        int result[] = { width, height };
        return result;
    }

    /**
     *
     * @param context1  activity
     * @param mInflater (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
     * @param isfinish 用于判断跳到设置页面是否关闭该activity   true 关闭
     */
    public static void isPasswroid(Context context1,LayoutInflater mInflater,boolean isfinish){
        context = context1;
        isFinish = isfinish;
        mDialog = new Dialog(context,
                R.style.loading_dialog2);//填写登录密码提示框
        View linearLayout = mInflater.inflate(R.layout.sodoko_unlock_pwd_dialog,null);
        TextView login_phone = (TextView) linearLayout.findViewById(R.id.tv_sodoko_login_phone);
        final EditText login_pwd = (EditText) linearLayout.findViewById(R.id.et_sodoko_login_pwd);
        TextView confirm = (TextView) linearLayout.findViewById(R.id.tv_sodoko_confirm);
        errorHint = (TextView) linearLayout.findViewById(R.id.tv_sodoko_error_hint);
        mDialog.setContentView(linearLayout);
//        mDialog.setCanceledOnTouchOutside(false);
        mDialog.show();
        login_phone.setText(Utils.getProtectedMobile(Utils.getUserPhone(context)));
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                errorHint.setVisibility(View.VISIBLE);
                errorHint.setText("正在判断登录密码...");
                Is_passwrod.isPasswrod(mHandler, login_pwd.getText().toString());
            }
        });
    }
    private static Dialog mDialog;
    private static Context context;
    private static TextView errorHint;
    private static boolean isFinish;
    private static Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            JsonBean jsonBean = (JsonBean) msg.obj;
            try {
                JSONObject json = new JSONObject(jsonBean.getJsonString());
                String status = json.getString("success");
                if ("0".equals(status)) {
                    errorHint.setText(json.optString("msg"));
                }else{
                    Utils.removePassWord(context, Utils.getUserPhone(context));
                    Intent intent = new Intent(context,GestureEditActivity.class);
                    context.startActivity(intent);
                    mDialog.dismiss();
                    if(isFinish){
                        ((Activity)context).finish();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

}
