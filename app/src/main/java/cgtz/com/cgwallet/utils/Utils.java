package cgtz.com.cgwallet.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

import cgtz.com.cgwallet.MApplication;
import cgtz.com.cgwallet.R;
import cgtz.com.cgwallet.activity.LoginActivity;
import cgtz.com.cgwallet.activity.MainActivity;
import cgtz.com.cgwallet.activity.SaveMoneyActivity;
import cgtz.com.cgwallet.activity.WebViewActivity;
import cgtz.com.cgwallet.bean.JsonBean;
import cgtz.com.cgwallet.bean.LoginMobileBean;
import cgtz.com.cgwallet.client.Withdraw_money_Client;
import cgtz.com.cgwallet.utility.Constants;
import cgtz.com.cgwallet.widget.ProgressDialog;
import cgtz.com.cgwallet.widget.ServerMainTainDialog;

/**
 * 工具类
 * Created by Administrator on 2015/4/10.
 */
public class Utils {

    /**
     * 获取登录的id
     * @return
     */
    public static String getUserId(){
        return MApplication.getUser_id();
//        SharedUtils sharedUtils = new SharedUtils(context, Constants.CONFIG);
//        return sharedUtils.getString(Constants.LOGIN_USER_ID,"");
    }

    /**
     * 保存登录后的id
     * @param userId
     */
    public static void saveUserId(String userId){
        MApplication.setUser_id(userId);
//        SharedUtils sharedUtils = new SharedUtils(context, Constants.CONFIG);
//        sharedUtils.saveString(Constants.LOGIN_USER_ID, userId);
    }
    /**
     * 获取登录的token
     * @return
     */
    public static String getToken(){
        return MApplication.getToken();
//        SharedUtils sharedUtils = new SharedUtils(context, Constants.CONFIG);
//        return sharedUtils.getString(Constants.LOGIN_TOKEN,"");
    }

    /**
     * 保存登录后的token
     * @param token
     */
    public static void saveToken(String token){
        MApplication.setToken(token);
//        SharedUtils sharedUtils = new SharedUtils(context, Constants.CONFIG);
//        sharedUtils.saveString(Constants.LOGIN_TOKEN, token);
    }

    /**
     * 保存登录手机号
     * @param context
     * @param mobile
     */
    public static void saveMobile(Context context,String mobile){
        LoginMobileBean loginMobileBean = new LoginMobileBean();
        loginMobileBean.setMobile(mobile);
        loginMobileBean.save();
        SharedUtils sharedUtils = new SharedUtils(context, Constants.CONFIG);
        sharedUtils.saveString(Constants.LOGIN_PHONE, mobile);
    }
    /**
     * 获取登录密码
     * @param context
     * @return
     */
    public static String getLoginPwd(Context context){
        SharedUtils sharedUtils = new SharedUtils(context, Constants.CONFIG);
        return sharedUtils.getString(Constants.LOGIN_PASSWORD,"");
    }
    /**
     * 保存登录密码
     * @param context
     * @param pwd
     */
    public static void saveLoginPwd(Context context,String pwd){
        SharedUtils sharedUtils = new SharedUtils(context, Constants.CONFIG);
        sharedUtils.saveString(Constants.LOGIN_PASSWORD, pwd);
    }
    /**
     * 获取登录的手机号
     * @param context
     * @return
     */
    public static String getUserPhone(Context context){
        SharedUtils sharedUtils = new SharedUtils(context, Constants.CONFIG);
        return sharedUtils.getString(Constants.LOGIN_PHONE,"");
    }

    /**
     * 获取带星号手机号
     * @param context
     * @return
     */
    public static String getMobileOfStar(Context context){
        return getHasStarsMobile(getUserPhone(context));
    }

    /**
     * 退出账号,删除登录密码  清空  该账户的数据
     * @param context
     * @return
     */
    public static void loginExit(Context context){
        Start_update_value.saveShare(context,"","");
        SharedUtils sharedUtils = new SharedUtils(context,Constants.CONFIG);
        sharedUtils.remove(Constants.LOGIN_PASSWORD);
//        sharedUtils.remove(Constants.LOGIN_TOKEN);
        MApplication.setToken("");
        MApplication.setUser_id("");
        MApplication.setIsSetTrade(false);
    }


    /**
     * 设置用户手机号   中间4位为*号
     * @param phoneNumber  手机号吗
     * @return
     */
    public static String getProtectedMobile(String phoneNumber) {
        if (TextUtils.isEmpty(phoneNumber) || phoneNumber.length() < 11) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        builder.append(phoneNumber.subSequence(0, 3));
        builder.append("****");
        builder.append(phoneNumber.subSequence(7, 11));
        return builder.toString();
    }

    /**
     * 删除手势密码
     * @param context
     * @param phone
     */
    public static void removePassWord(Context context,String phone){
        SharedUtils sharedUtils = new SharedUtils(context,Constants.CONFIG_GESTURE);
        sharedUtils.remove(phone);
    }

    /**
     * 保存手势密码
     * @param context
     * @param phone
     * @param pwd
     */
    public static void saveLockPassWord(Context context,String phone,String pwd){
        SharedUtils sharedUtils = new SharedUtils(context,Constants.CONFIG_GESTURE);
        sharedUtils.saveString(phone, pwd);
    }

    /**
     * 获取手势密码
     * @param context
     * @param phone
     * @return
     */
    public static String getLockPassword(Context context,String phone){
        SharedUtils sharedUtils = new SharedUtils(context,Constants.CONFIG_GESTURE);
        return sharedUtils.getString(phone,"");
    }

    /**
     * 存储是否显示过遮罩层
     * @param context
     * @param ismask
     */
    public static void saveIsMask(Context context,boolean ismask){
        SharedUtils sharedUtils = new SharedUtils(context,Constants.CONFIG_GESTURE);
        sharedUtils.saveBoolean("ismask", ismask);
    }

    /**
     * 获取是否显示过遮罩层
     * @param context
     * @return
     */
    public static boolean getIsMask(Context context){
        SharedUtils sharedUtils = new SharedUtils(context,Constants.CONFIG_GESTURE);
        return sharedUtils.getBoolean("ismask", false);
    }
    /**
     *
     * 存储登录后是否是否提示用户过设置手势密码
     * @param context
     * @param phone
     * @return
     */
    public static void  saveisLockPassWord(Context context,String phone,int  isLock){
        SharedUtils sharedUtils = new SharedUtils(context,Constants.CONFIG_GESTURE);
        sharedUtils.saveInt(phone + "isLock", isLock);
    }
    /**
     *
     * 获取登录后是否是否提示用户过设置手势密码
     * @param context
     * @param phone
     * @return int 返回0 是该账户没有在该手机登录过    1是登录了  没有提示设置过手势   2是已经提示过了
     */
    public static int getisLockPassWord(Context context,String phone){
        SharedUtils sharedUtils = new SharedUtils(context,Constants.CONFIG_GESTURE);
        return sharedUtils.getFloat(phone + "isLock", 0);
    }
    /**
     * 显示toast信息
     * @param context
     * @param msg
     */
    public static void makeToast(Context context,String msg){
        Toast.makeText(context,msg,Toast.LENGTH_LONG).show();
    }
    /**
     * 显示toast信息
     * @param context
     * @param msg
     */
    public static void makeToast_short(Context context,String msg){
        Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
    }
    /**
     * 判断SD卡是否存在
     * @return
     */
    public static boolean isHaveSD(){
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            return true;
        }else{
            return false;
        }
    }
    /**
     * 过滤code结果
     * @param jsonBean
     */
    public static boolean filtrateCode(Context context,JsonBean jsonBean){
        LogUtils.i("JsonBean", "过滤code结果：" + jsonBean.getJsonString());
        int code = jsonBean.getCode();//code判断值
        String errorMsg = jsonBean.getError_msg();//错误信息
        if(code == Constants.NO_DATA || code == Constants.IS_EVENT){
            Utils.makeToast(context,errorMsg);
            return false;
        }else if(code == Constants.NEED_LOGIN_AGAIN){//需要重新登录
            loginExit(context);
            context.startActivity(new Intent(context, LoginActivity.class));
            if(!MainActivity.class.getName().equals(((Activity)context).getClass().getName())){
                ((Activity)context).finish();
            }
            return false;
        }else if(code == Constants.SERVICE_MAINTAIN){//服务器正在维护
            maintainDialog(context);
            return false;
        }else{
            return true;
        }
    }


    /**
     * 判断内容是否完整
     * @param str
     * @return
     */
    public static String isEmpty(String str){
        if(TextUtils.isEmpty(str)){
            return "";
        }else{
            return str;
        }
    }

    /**
     * 服务器维护弹窗
     * @param context
     */
    public static void maintainDialog(Context context){
        final ServerMainTainDialog maintainDialog =
                ServerMainTainDialog.getInstans(context);
        maintainDialog.setCancelable(false);
        maintainDialog.setCanceledOnTouchOutside(false);
        maintainDialog.withMaintainIconClick(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                maintainDialog.dismiss();
                System.exit(0);
            }
        });
        maintainDialog.show();
    }

    /**
     * 关闭开启的弹窗
     * @param dialog
     */
    public static void closeDialog(Context context,Dialog dialog){
        if(dialog != null && dialog.isShowing()){
            dialog.cancel();
        }
    }

    /**
     * 手机号码添加星号
     * @param text
     * @return
     */
    public static String getHasStarsMobile(String text){
        if(TextUtils.isEmpty(text)){
            return "";
        }else{
            return text.substring(0,3)+"*****"+text.substring(8);
        }
    }

    /**
     * 自动登录
     * @param context
     * @param handler
     */
    public static void autoLogin(Context context,Handler handler){
        String mobile = getUserPhone(context);
        String pwd = getLoginPwd(context);
        if(!TextUtils.isEmpty(mobile) && !TextUtils.isEmpty(pwd)){
            HashMap<String,String> params = new HashMap<>();
            params.put("username",mobile);
            params.put("password", pwd);
            CustomTask task = new CustomTask(handler,Constants.WHAT_LOGIN,Constants.URL_LOGIN,true,params,true);
            task.execute();
        }
    }

    /**
     * 是否登录
     * @return
     */
    public static boolean isLogined(){
        if(TextUtils.isEmpty(getUserId()) || TextUtils.isEmpty(getToken())){
            return false;
        }else{
            return true;
        }
    }

    /**
     * 判断是否设置了交易密码
     * @return
     */
    public static boolean isSetTradePwd(){
        if(TextUtils.isEmpty(getUserId()) || TextUtils.isEmpty(getToken()) || MApplication.isSetTrade()){
            return false;
        }else{
            return true;
        }
    }
    /**
     * 获取带星的用户姓名
     * @param username
     * @return
     */
    public static String getUserNameForStart(String username){
        return username.substring(0,1)+"*"+(username.length()>2?username.substring(2):"");
    }
    /**
     * 获取带星的身份证号
     * @param identity
     * @return
     */
    public static String getUserIdentity(String identity){
        StringBuffer sb = new StringBuffer();
        return identity.length() == 15?
                identity.substring(0,6)+"******"+identity.substring(12)
                :
                identity.substring(0,6)+"*********"+identity.substring(15);
    }
    /**
     * 获取带星的银行卡号
     * @param bankcard
     * @return
     */
    public static String getBankStart(String bankcard){
        StringBuffer sb = new StringBuffer();
        for(int a = 1;a <bankcard.length()-3;a++){
            sb.append("*");
            if(a % 4 == 0){
                sb.append(" ");
            }
        }
        sb.append(" "+bankcard.substring(bankcard.length()-4));
        return sb.toString();
    }

    /**
     * 获取版本号
     * @return 当前应用的版本号
     */
    public static String getVersion(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            String version = info.versionName;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return "0";
        }
    }


    /**
     * 点击屏幕，关闭输入键盘
     * @param context
     */
    public static void closeInputMethod(final Activity context){
        final InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        context.findViewById(R.id.box).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (imm != null && imm.isActive()) {
                    imm.hideSoftInputFromWindow(context.findViewById(R.id.box).getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }
        });
    }

    /**
     * 设置安全信息
     * @param context
     */
    public static void safeCopyWrite(Activity context){
        TextView text = (TextView) context.findViewById(R.id.tv_banner);
        if(text!=null){
        text.setText(Ke_Fu_data.getSafe(context));
        }
    }
    /**
     * 设置安全信息
     * @param context
     */
    public static void safeCopyWrite(Activity context,View view){
        TextView text = (TextView) view.findViewById(R.id.tv_banner);
        if(text!=null){
            text.setText(Ke_Fu_data.getSafe(context));
        }
    }

    /**
     * 获取渠道名
     * @param ctx 此处习惯性的设置为activity，实际上context就可以
     * @return 如果没有获取成功，那么返回值为空
     */
    public static String getChannelName(Activity ctx) {
        if (ctx == null) {
            return null;
        }
        String channelName = null;
        try {
            PackageManager packageManager = ctx.getPackageManager();
            if (packageManager != null) {
                //注意此处为ApplicationInfo 而不是 ActivityInfo,因为友盟设置的meta-data是在application标签中，而不是某activity标签中，所以用ApplicationInfo
                ApplicationInfo applicationInfo = packageManager.getApplicationInfo(ctx.getPackageName(), PackageManager.GET_META_DATA);
                if (applicationInfo != null) {
                    if (applicationInfo.metaData != null) {
                        channelName = applicationInfo.metaData.getString("UMENG_CHANNEL");
                    }
                }

            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return channelName;
    }


    /**
     2  * 获取版本code
     3  * @return 当前应用的版本code
     4  */
    public static int getVersion1(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            int version = info.versionCode;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
    /**
     * 草根钱包介绍
     */
    public static void goToSloganIntruduce(final Context context,View view){
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(context, WebViewActivity.class)
                        .putExtra("url", Constants.URL_WALLET_SLOGAN)
                        .putExtra("title", "草根钱包介绍"));
                MApplication.destroyActivity(MApplication.getActivityByName(SaveMoneyActivity.class.getName()));
                ((Activity)context).finish();
            }
        });
    }

}
