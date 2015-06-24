package cgtz.com.cgwallet.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.bean.HandlerRequestCode;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners;
import com.umeng.socialize.media.QQShareContent;
import com.umeng.socialize.media.QZoneShareContent;
import com.umeng.socialize.media.SmsShareContent;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.sso.SmsHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.sso.UMSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.umeng.socialize.weixin.media.CircleShareContent;
import com.umeng.socialize.weixin.media.WeiXinShareContent;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import cgtz.com.cgwallet.MApplication;
import cgtz.com.cgwallet.R;
import cgtz.com.cgwallet.fragment.CgWalletFragment;
import cgtz.com.cgwallet.fragment.CgWallet_web_fragment;
import cgtz.com.cgwallet.fragment.MyWalletFragment;
import cgtz.com.cgwallet.fragment.My_wallet_new_Fragment;
import cgtz.com.cgwallet.utility.Constants;
import cgtz.com.cgwallet.utils.Ke_Fu_data;
import cgtz.com.cgwallet.utils.LogUtils;
import cgtz.com.cgwallet.utils.Start_update_value;
import cgtz.com.cgwallet.utils.Utils;
import cgtz.com.cgwallet.view.ISplashView;
import cgtz.com.cgwallet.widget.ProgressDialog;
import cgtz.com.cgwallet.widget.SlidingMenu;
import cn.jpush.android.api.JPushInterface;

/**
 * 首页
 */
public class MainActivity extends FragmentActivity implements ISplashView,View.OnClickListener{
    private static final String TAG = "MainActivity";
//    private LinearLayout layoutCgWallet;//底部的草根钱包
//    private LinearLayout layoutMyWallet;//底部的我的钱包
    private ImageView showLeftButton;
    private ImageView showRightButton;
    private LinearLayout menuSafeCenter;//安全中心
    private LinearLayout menuHelpCenter;//帮助中心
    private LinearLayout menuMore;//更多
    private LinearLayout menuCgtz;//跳转草根投资
    private TextView tvLogin;//未登录显示文案或者显示登录的手机号
    private TextView tvShowLoginMobile;//显示登录的手机号
    private ImageView image_Login;//登录上面的图片
    private LinearLayout layotExit;//退出登录
    private int currIndex;//当前页卡编号
    private ProgressDialog progressDialog;
//    private CgWalletFragment cgWalletFragment;
//    private MyWalletFragment myWalletFragment;
    private CgWallet_web_fragment cgWalletFragment;
    private My_wallet_new_Fragment myWalletFragment;
//    private ImageView bottomLineSelected;//底部的白线
    private int screenWith;
    private LinearLayout.LayoutParams params;
//    private ImageView cgWalletIcon;
//    private ImageView myWalletIcon;
//    private TextView cgWalletText;
//    private TextView myWalletText;
    private TextView my_wallet_button;//title  我的钱包 按钮
    private TextView cg_wallet_button;//title  草根钱包 按钮
    private SlidingMenu mMenu;
    private int Value = 0;
    private FragmentManager fm;

    private static String MY_WALLET = "my";
    private static String CG_WALLET = "cg";
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        MApplication.registActivities(this);//存储该activity
        fm = getSupportFragmentManager();

        screenWith = getResources().getDisplayMetrics().widthPixels;
        initViews();
        setViewLinstener();
        mMenu.setBindActivity(this);
        showLeftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMenu.leftToggle();
            }
        });
        showRightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utils.isLogined()) {
                    mMenu.rightToggle();
                } else {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                }
            }
        });
        if(!Utils.isLogined()){
//            if(!Utils.getIsMask(this)){//判断是否显示过遮罩层
//                LogUtils.e(TAG, "aaa   " + Utils.getIsMask(this));
//                showDialog();
//            }
            showDialog();
        }else{
            setFragment();
        }
        initShare();
    }
    //获取友盟分享变量
    private final UMSocialService mController = UMServiceFactory.getUMSocialService("com.umeng.share");
    private String content="",url="";//分享内容  和  分享链接
    private ImageView Qr_code;//二维码

    private Bitmap bitmap = null;
    private String res;
    /**
     * 设置分享  显示数据
     */
    public void initShareData(){
        HashMap<String,String> map = Start_update_value.getShare(this);
        String date = map.get(Start_update_value.KEY_SHARE);
        try {
            JSONObject json = new JSONObject(date);
            LogUtils.e(TAG,json+"");
            content = json.optString("content");
            url=json.optString("url");

            res = map.get(Start_update_value.KEY_QR_CODE);
            mHandler.post(connectNet);
//            byte[] qr_code = res.getBytes("UTF-8");
//            bitmap = BitmapFactory.decodeByteArray(qr_code, 0, qr_code.length);
//            Qr_code.setImageBitmap(bitmap);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    //获取二维码的线程
    private Runnable connectNet = new Runnable(){
        @Override
        public void run() {
            //取得的是byte数组, 从byte数组生成bitmap
            try {
                byte[] qr_code = res.getBytes("ISO8859-1");
                if (qr_code != null) {
                    bitmap = BitmapFactory.decodeByteArray(qr_code, 0, qr_code.length);
                    Qr_code.setImageBitmap(bitmap);
                } else {

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
        //分享回调
    /**
     * 分享监听器
     */
    private SocializeListeners.SnsPostListener mShareListener = new SocializeListeners.SnsPostListener() {

        @Override
        public void onStart() {

        }

        @Override
        public void onComplete(SHARE_MEDIA platform, int stCode,
                               SocializeEntity entity) {
            if (stCode == 200) {
//                Toast.makeText(MainActivity.this, "分享成功", Toast.LENGTH_SHORT)
//                        .show();
            } else {
//                Toast.makeText(MainActivity.this,
//                        "分享失败 : error code : " + stCode, Toast.LENGTH_SHORT)
//                        .show();
            }
        }
    };
    /**
     * 初始化分享页面
     */
    private void initShare() {
        LinearLayout QQ,qzone,sms,wechat,wxcircle,sina;
        TextView rules;//推荐规则
        QQ = (LinearLayout) findViewById(R.id.QQ);
        qzone = (LinearLayout) findViewById(R.id.qzone);
        sms = (LinearLayout) findViewById(R.id.sms);
        wechat = (LinearLayout) findViewById(R.id.wechat);
        wxcircle = (LinearLayout) findViewById(R.id.wxcircle);
        sina = (LinearLayout) findViewById(R.id.sina);
        rules = (TextView) findViewById(R.id.rules);
        Qr_code = (ImageView) findViewById(R.id.Qr_code);

        configuration();

        View.OnClickListener share_click = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //分享的图片
                UMImage image = new UMImage(MainActivity.this, R.mipmap.icon_logo);
                boolean aa ;
                switch (v.getId()){
                    case R.id.QQ:
                        //判断是否有安装
                        aa = mController.getConfig().getSsoHandler(HandlerRequestCode.QQ_REQUEST_CODE).isClientInstalled();
                        if(aa){
                            QQShareContent qqShareContent = new QQShareContent();
                            //设置分享文字
                            qqShareContent.setShareContent(content);
                            //设置分享title
                            qqShareContent.setTitle("草根投资");
                            //设置分享图片
                            qqShareContent.setShareImage(image);
                            //设置点击分享内容的跳转链接
                            qqShareContent.setTargetUrl(url);
                            mController.setShareMedia(qqShareContent);
                            // 参数1为Context类型对象， 参数2为要分享到的目标平台， 参数3为分享操作的回调接口
                            mController.postShare(MainActivity.this, SHARE_MEDIA.QQ, mShareListener);
                            mMenu.rightToggle();
                        }else{
                            Utils.makeToast(MainActivity.this,"您未安装该应用,请安装后分享！");
                        }
                        break;
                    case R.id.qzone:
                        //判断是否有安装
                        aa = mController.getConfig().getSsoHandler(HandlerRequestCode.QZONE_REQUEST_CODE).isClientInstalled();
                        if(aa){
                            QZoneShareContent qzone = new QZoneShareContent();
                            //设置分享文字
                            qzone.setShareContent(content);
                            //设置点击消息的跳转URL
                            qzone.setTargetUrl(url);
                            //设置分享内容的标题
                            qzone.setTitle("草根投资");
                            //设置分享图片
                            qzone.setShareImage(image);
                            mController.setShareMedia(qzone);
                            // 参数1为Context类型对象， 参数2为要分享到的目标平台， 参数3为分享操作的回调接口
                            mController.postShare(MainActivity.this, SHARE_MEDIA.QZONE, mShareListener);
                            mMenu.rightToggle();
                        }else{
                            Utils.makeToast(MainActivity.this,"您未安装该应用,请安装后分享！");
                        }
                        break;
                    case R.id.sms:
                        // 设置短信分享内容
                        SmsShareContent sms = new SmsShareContent();
                        sms.setShareContent(content);
                        mController.setShareMedia(sms);
                        // 参数1为Context类型对象， 参数2为要分享到的目标平台， 参数3为分享操作的回调接口
                        mController.postShare(MainActivity.this, SHARE_MEDIA.SMS, mShareListener);
                        mMenu.rightToggle();
                        break;
                    case R.id.wechat://微信
                        //判断是否有安装
                        aa = mController.getConfig().getSsoHandler(HandlerRequestCode.WX_REQUEST_CODE).isClientInstalled();
                        if(aa){
                            //设置微信好友分享内容
                            WeiXinShareContent weixinContent = new WeiXinShareContent();
                            //设置分享文字
                            weixinContent.setShareContent(content);
                            //设置title
                            weixinContent.setTitle("草根投资");
                            //设置分享内容跳转URL
                            weixinContent.setTargetUrl(url);
                            //设置分享图片
                            weixinContent.setShareImage(image);
                            mController.setShareMedia(weixinContent);
                            // 参数1为Context类型对象， 参数2为要分享到的目标平台， 参数3为分享操作的回调接口
                            mController.postShare(MainActivity.this, SHARE_MEDIA.WEIXIN, mShareListener);
                            mMenu.rightToggle();
                        }else{
                            Utils.makeToast(MainActivity.this,"您未安装该应用,请安装后分享！");
                        }
                        break;
                    case R.id.wxcircle://朋友圈
                        //判断是否有安装
                        aa = mController.getConfig().getSsoHandler(HandlerRequestCode.WX_CIRCLE_REQUEST_CODE).isClientInstalled();
                        if(aa){
                            //设置微信朋友圈分享内容
                            CircleShareContent circleMedia = new CircleShareContent();
                            circleMedia.setShareContent(content);
                            //设置朋友圈title
                            circleMedia.setTitle(content);
                            circleMedia.setShareImage(image);
                            circleMedia.setTargetUrl(url);
                            mController.setShareMedia(circleMedia);
                            // 参数1为Context类型对象， 参数2为要分享到的目标平台， 参数3为分享操作的回调接口
                            mController.postShare(MainActivity.this, SHARE_MEDIA.WEIXIN_CIRCLE, mShareListener);
                            mMenu.rightToggle();
                        }else{
                            Utils.makeToast(MainActivity.this,"您未安装该应用,请安装后分享！");
                        }
                        break;
                    case R.id.sina://新浪
                        //判断是否有安装
                            //设置新浪SSO handler
                            mController.getConfig().setSsoHandler(new SinaSsoHandler());
                            // 设置分享内容
                            mController.setShareContent(content);
                            //设置分享图片，参数2为本地图片的资源引用
                            mController.setShareMedia(image);
                            // 参数1为Context类型对象， 参数2为要分享到的目标平台， 参数3为分享操作的回调接口
                            mController.postShare(MainActivity.this, SHARE_MEDIA.SINA, mShareListener);
                        break;
                    case R.id.rules://分享规则
                        startActivity(new Intent(MainActivity.this, WebViewActivity.class)
                                .putExtra("url", "https://d5ds88.cgtz.com/version/notice/rule")
                                .putExtra("title", "推荐规则"));
                        mMenu.rightToggle();
                        break;
                }
            }
        };
        QQ.setOnClickListener(share_click);
        qzone.setOnClickListener(share_click);
        sms.setOnClickListener(share_click);
        wechat.setOnClickListener(share_click);
        wxcircle.setOnClickListener(share_click);
        sina.setOnClickListener(share_click);
        rules.setOnClickListener(share_click);
    }
    /**
     * @功能描述 : 添加微信平台分享
     * @return
     */
    private void addWXPlatform() {
        // 注意：在微信授权的时候，必须传递appSecret
        // wx967daebe835fbeac是你在微信开发平台注册应用的AppID, 这里需要替换成你注册的AppID
        String appId = "wxeb53bcb5100bf596";
        String appSecret = "828340e2d160e8194641a16484ae86ea";
        // 添加微信平台
        UMWXHandler wxHandler = new UMWXHandler(MainActivity.this, appId, appSecret);
        wxHandler.addToSocialSDK();

        // 支持微信朋友圈
        UMWXHandler wxCircleHandler = new UMWXHandler(MainActivity.this, appId, appSecret);
        wxCircleHandler.setToCircle(true);
        wxCircleHandler.addToSocialSDK();
    }
    /**
     * @功能描述 : 添加QQ平台支持 QQ分享的内容， 包含四种类型， 即单纯的文字、图片、音乐、视频. 参数说明 : title, summary,
     *       image url中必须至少设置一个, targetUrl必须设置,网页地址必须以"http://"开头 . title :
     *       要分享标题 summary : 要分享的文字概述 image url : 图片地址 [以上三个参数至少填写一个] targetUrl
     *       : 用户点击该分享时跳转到的目标地址 [必填] ( 若不填写则默认设置为友盟主页 )
     * @return
     */
    private void addQQQZonePlatform() {
        String appId = "1104528482";
        String appKey = "AF0BwUSvJAREbbAF";
        // 添加QQ支持, 并且设置QQ分享内容的target url
        UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(MainActivity.this,
                appId, appKey);
        qqSsoHandler.addToSocialSDK();

        // 添加QZone平台
        QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(MainActivity.this, appId, appKey);
        qZoneSsoHandler.addToSocialSDK();
    }
    /**
     * 添加短信平台</br>
     */
    private void addSMS() {
        // 添加短信
        SmsHandler smsHandler = new SmsHandler();
        smsHandler.addToSocialSDK();
    }
    /**
     * 添加分享平台
     */
    private void configuration() {
        mController.getConfig().closeToast();//禁止toast
        // 添加短信平台
        addSMS();
        // 添加微信平台
        addWXPlatform();
        // 添加QQ平台
        addQQQZonePlatform();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /**使用SSO授权必须添加如下代码 */
        UMSsoHandler ssoHandler = mController.getConfig().getSsoHandler(requestCode) ;
        if(ssoHandler != null){
            ssoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }

    /**
     * 显示遮罩层的  dialog
     */
    private Dialog dialog_main;
    private void showDialog(){
        LinearLayout dialog_layout =
                (LinearLayout) LayoutInflater.from(this).inflate(R.layout.dialot_main_mask_layer, null);
        if(dialog_main == null){
            dialog_main = new Dialog(this, R.style.dialog_main);
        }
        if(dialog_main.isShowing()){
            dialog_main.dismiss();
        }
        dialog_main.setCancelable(false);//禁止返回键关闭
        dialog_main.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if(keyCode == KeyEvent.KEYCODE_BACK){
                    long digTime = System.currentTimeMillis();
                    LogUtils.i(TAG,"digTime:"+digTime);
                    long currentTime = System.currentTimeMillis();
                    if ((currentTime - touchTime) >= waitTime) {
                        touchTime = currentTime;
                    } else {
                        MApplication.finishAllActivitys();
                        finish();
                    }
//                    MApplication.finishAllActivitys();
//                    finish();
                }
                return true;
            }
        });

        TextView text = (TextView) dialog_layout.findViewById(R.id.tv_banner);//设置安全文案
        text.setText(Ke_Fu_data.getSafe(this));

        View.OnClickListener click = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.start_money://开始生钱
                        Utils.saveIsMask(MainActivity.this, true);//存储是否显示遮罩层的判断值
                        dialog_main.dismiss();
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                        break;
                    case R.id.understand://了解草根钱包

                        break;
                }
            }
        };
        Button start_money = (Button) dialog_layout.findViewById(R.id.start_money);
        TextView understand = (TextView) dialog_layout.findViewById(R.id.understand);

        understand.setOnClickListener(click);
        start_money.setOnClickListener(click);
        dialog_main.setContentView(dialog_layout);
        dialog_main.show();
    }

    private void initViews(){
        mMenu = (SlidingMenu) findViewById(R.id.id_menu);
//        cgWalletIcon = (ImageView) findViewById(R.id.cg_wallet_icon);
//        myWalletIcon = (ImageView) findViewById(R.id.my_wallet_icon);
//        cgWalletText = (TextView) findViewById(R.id.cg_wallet_text);
//        myWalletText = (TextView) findViewById(R.id.my_wallet_text);
//        layoutCgWallet = (LinearLayout) findViewById(R.id.layout_cg_wallet);//底部的草根钱包
//        layoutMyWallet = (LinearLayout) findViewById(R.id.layout_my_wallet);//底部的我的钱包
        showLeftButton = (ImageView) findViewById(R.id.show_left_button);
        showRightButton = (ImageView) findViewById(R.id.show_right_button);
        tvLogin = (TextView) findViewById(R.id.tv_goToLogin);
        menuSafeCenter = (LinearLayout) findViewById(R.id.left_menu_safe_center);
        menuHelpCenter = (LinearLayout) findViewById(R.id.left_menu_help_center);
        menuMore = (LinearLayout) findViewById(R.id.left_menu_more);
        menuCgtz = (LinearLayout) findViewById(R.id.left_menu_cgtz);
        tvShowLoginMobile = (TextView) findViewById(R.id.tv_show_login_mobile);
        image_Login = (ImageView) findViewById(R.id.image_Login);
        layotExit = (LinearLayout) findViewById(R.id.left_menu_login_out);
//        bottomLineSelected = (ImageView) findViewById(R.id.wallet_bottom_line_selected);//底部的选中
//        params = (LinearLayout.LayoutParams) bottomLineSelected.getLayoutParams();
//        params.width = screenWith/2;
//        bottomLineSelected.setLayoutParams(params);

        my_wallet_button = (TextView) findViewById(R.id.my_wallet_button);//title 我的钱包
        cg_wallet_button = (TextView) findViewById(R.id.cg_wallet_button);//title 草根钱包
    }

    public void clearFocus(){
        LogUtils.i(TAG, "mainactivity clearFocus");
//        if(layoutCgWallet != null){
//            layoutCgWallet.setEnabled(false);
//        }
//        if(layoutMyWallet != null){
//            layoutMyWallet.setEnabled(false);
//        }
        if(cgWalletFragment != null){
            cgWalletFragment.clearFocus();
        }
        if(myWalletFragment != null){
            myWalletFragment.clearFocus();
        }
    }

    public void requetFocus(){
//        if(layoutCgWallet != null){
//            layoutCgWallet.setEnabled(true);
//        }
//        if(layoutMyWallet != null){
//            layoutMyWallet.setEnabled(true);
//        }
        if(cgWalletFragment != null){
            cgWalletFragment.requetFocus();
        }
        if(myWalletFragment != null){
            myWalletFragment.requetFocus();
        }
    }

    private void setViewLinstener(){
        tvLogin.setOnClickListener(this);
        menuSafeCenter.setOnClickListener(this);
        menuHelpCenter.setOnClickListener(this);
        menuMore.setOnClickListener(this);
        menuCgtz.setOnClickListener(this);
        layotExit.setOnClickListener(this);
        cg_wallet_button.setOnClickListener(this);
        my_wallet_button.setOnClickListener(this);
//        layoutCgWallet.setOnClickListener(this);//底部的草根钱包
//        layoutMyWallet.setOnClickListener(this);//底部的我的钱包
    }

    /**
     * 初始化fragment
     */
    private void setFragment(){
        layoutClick(R.id.my_wallet_button);
    }

    public void layoutClick(int type){
        FragmentTransaction ft = fm.beginTransaction();
        hideFragment(ft);
        switch (type){
            case R.id.my_wallet_button://显示我的钱包页面
                currIndex = 1;
                lineToLeft();
                if(Utils.isLogined()){
                    if(myWalletFragment != null) {
                        LogUtils.e(TAG, "show myWalletFragment");
                        if(fm.findFragmentByTag(MY_WALLET) != null && fm.findFragmentByTag(MY_WALLET).isAdded()){
                            ft.show(fm.findFragmentByTag(MY_WALLET));
                        }else{
                            ft.add(R.id.menu_center_framelayout,myWalletFragment,MY_WALLET);
                        }
                        myWalletFragment.setData(true);
                    }else{
                        LogUtils.e(TAG,"new a myWalletFragment");
                        myWalletFragment = new My_wallet_new_Fragment();
                        if(fm.findFragmentByTag(MY_WALLET) != null &&fm.findFragmentByTag(MY_WALLET).isAdded()){
                            ft.show(fm.findFragmentByTag(MY_WALLET));
                        }else{
                            ft.add(R.id.menu_center_framelayout,myWalletFragment,MY_WALLET);
                        }
                    }
                    ft.commitAllowingStateLoss();

//                    if(!Utils.getIsMask(this)){//判断是否显示过遮罩层
//                        if(myWalletFragment != null) {
//                            LogUtils.e(TAG, "show myWalletFragment");
//                            if(fm.findFragmentByTag(MY_WALLET) != null && fm.findFragmentByTag(MY_WALLET).isAdded()){
//                                ft.show(fm.findFragmentByTag(MY_WALLET));
//                            }else{
//                                ft.add(R.id.menu_center_framelayout,myWalletFragment,MY_WALLET);
//                            }
////                        ft.show(fm.findFragmentByTag(MY_WALLET));
////                        ft.show(myWalletFragment);
//                            myWalletFragment.setData(true);
//                        }else{
//                            LogUtils.e(TAG,"new a myWalletFragment");
//                            myWalletFragment = new My_wallet_new_Fragment();
//                            if(fm.findFragmentByTag(MY_WALLET) != null &&fm.findFragmentByTag(MY_WALLET).isAdded()){
//                                ft.show(fm.findFragmentByTag(MY_WALLET));
//                            }else{
//                                ft.add(R.id.menu_center_framelayout,myWalletFragment,MY_WALLET);
//                            }
////                        ft.add(R.id.menu_center_framelayout,myWalletFragment,MY_WALLET);
//                        }
//                        myWalletFragment.setData(true);
//                        ft.commitAllowingStateLoss();
//                    }else{
//                        if(myWalletFragment != null) {
//                            if(fm.findFragmentByTag(MY_WALLET) != null && fm.findFragmentByTag(MY_WALLET).isAdded()){
//                                ft.show(fm.findFragmentByTag(MY_WALLET));
//                            }else{
//                                ft.add(R.id.menu_center_framelayout,myWalletFragment,MY_WALLET);
//                            }
//                        }else{
//                            myWalletFragment = new My_wallet_new_Fragment();
//                            if(fm.findFragmentByTag(MY_WALLET) != null &&fm.findFragmentByTag(MY_WALLET).isAdded()){
//                                ft.show(fm.findFragmentByTag(MY_WALLET));
//                            }else{
//                                ft.add(R.id.menu_center_framelayout,myWalletFragment,MY_WALLET);
//                            }
//                        }
//                        myWalletFragment.setData(true);
//                        ft.commitAllowingStateLoss();
////                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
//                    }
                }else{
                    showDialog();
                    if(myWalletFragment != null) {
                        if(fm.findFragmentByTag(MY_WALLET) != null && fm.findFragmentByTag(MY_WALLET).isAdded()){
                            ft.show(fm.findFragmentByTag(MY_WALLET));
                        }else{
                            ft.add(R.id.menu_center_framelayout,myWalletFragment,MY_WALLET);
                        }
                    }else{
                        myWalletFragment = new My_wallet_new_Fragment();
                        if(fm.findFragmentByTag(MY_WALLET) != null &&fm.findFragmentByTag(MY_WALLET).isAdded()){
                            ft.show(fm.findFragmentByTag(MY_WALLET));
                        }else{
                            ft.add(R.id.menu_center_framelayout,myWalletFragment,MY_WALLET);
                        }
                    }
                    ft.commitAllowingStateLoss();
//                    if(myWalletFragment != null) {
//                        LogUtils.e(TAG, "show myWalletFragment");
//                        if(fm.findFragmentByTag(MY_WALLET) != null && fm.findFragmentByTag(MY_WALLET).isAdded()){
//                            ft.show(fm.findFragmentByTag(MY_WALLET));
//                        }else{
//                            ft.add(R.id.menu_center_framelayout,myWalletFragment,MY_WALLET);
//                        }
////                        ft.show(fm.findFragmentByTag(MY_WALLET));
////                        ft.show(myWalletFragment);
//                        myWalletFragment.setData(true);
//                    }else{
//                        LogUtils.e(TAG,"new a myWalletFragment");
//                        myWalletFragment = new My_wallet_new_Fragment();
//                        if(fm.findFragmentByTag(MY_WALLET) != null &&fm.findFragmentByTag(MY_WALLET).isAdded()){
//                            ft.show(fm.findFragmentByTag(MY_WALLET));
//                        }else{
//                            ft.add(R.id.menu_center_framelayout,myWalletFragment,MY_WALLET);
//                        }
////                        ft.add(R.id.menu_center_framelayout,myWalletFragment,MY_WALLET);
//                    }
//                    ft.commitAllowingStateLoss();
                }

                break;
            case R.id.cg_wallet_button://显示草根钱包页面
                lineToRight();
                currIndex = 2;
                if(cgWalletFragment != null){
                    if(fm.findFragmentByTag(CG_WALLET) != null
                            && fm.findFragmentByTag(CG_WALLET).isAdded()){
                        ft.show(fm.findFragmentByTag(CG_WALLET));
                    }else{
                        ft.add(R.id.menu_center_framelayout,cgWalletFragment,CG_WALLET);
                    }
                    ft.show(fm.findFragmentByTag(CG_WALLET));
//                    ft.show(cgWalletFragment);
                    cgWalletFragment.reload();
                }else{
                    cgWalletFragment = new CgWallet_web_fragment("https://www.cgtz.com/");
                    if(fm.findFragmentByTag(CG_WALLET) != null
                            && fm.findFragmentByTag(CG_WALLET).isAdded()){
                        ft.show(fm.findFragmentByTag(CG_WALLET));
                    }else{
                        ft.add(R.id.menu_center_framelayout,cgWalletFragment,CG_WALLET);
                    }
//                    ft.add(R.id.menu_center_framelayout,cgWalletFragment,CG_WALLET);
                }
                ft.commitAllowingStateLoss();
                break;
        }
    }

    private void hideFragment(android.support.v4.app.FragmentTransaction ft){
        if(cgWalletFragment != null){
            ft.hide(fm.findFragmentByTag(CG_WALLET));
//            ft.hide(cgWalletFragment);
        }
        if(myWalletFragment != null){
            ft.hide(fm.findFragmentByTag(MY_WALLET));
//            ft.hide(myWalletFragment);
        }
    }

    /**
     * 草根钱包
     */
    private void lineToRight(){
        cg_wallet_button.setTextColor(getResources().getColor(R.color.main_bg));
        cg_wallet_button.setBackgroundResource(R.drawable.bg_main_btn_right_back_on);
        my_wallet_button.setTextColor(getResources().getColor(R.color.white));
        my_wallet_button.setBackgroundResource(R.drawable.bg_main_btn_left_back_off);

//        cgWalletIcon.setImageResource(R.mipmap.icon_cg_wallet_normal);
//        myWalletIcon.setImageResource(R.mipmap.icon_my_wallet_selected);
//        cgWalletText.setTextColor(getResources().getColor(R.color.layout_bottom_transfer_right_text));
//        myWalletText.setTextColor(getResources().getColor(R.color.layout_bottom_transfer_left_text));
//        layoutCgWallet.setBackgroundResource(R.mipmap.bg_wallet_normal);
//        layoutMyWallet.setBackgroundResource(R.mipmap.bg_wallet_selected);
//        LineAnimTask task = new LineAnimTask();
//        task.execute(50);
    }

    /**
     * 滑动到左边
     */
    private void lineToLeft(){
        my_wallet_button.setTextColor(getResources().getColor(R.color.main_bg));
        my_wallet_button.setBackgroundResource(R.drawable.bg_main_btn_left_back_on);
        cg_wallet_button.setTextColor(getResources().getColor(R.color.white));
        cg_wallet_button.setBackgroundResource(R.drawable.bg_main_btn_right_back_off);

//        cgWalletIcon.setImageResource(R.mipmap.icon_wallet);
//        myWalletIcon.setImageResource(R.mipmap.icon_my_wallet);
//        cgWalletText.setTextColor(getResources().getColor(R.color.layout_bottom_transfer_left_text));
//        myWalletText.setTextColor(getResources().getColor(R.color.layout_bottom_transfer_right_text));
//        layoutCgWallet.setBackgroundResource(R.mipmap.bg_wallet_selected);
//        layoutMyWallet.setBackgroundResource(R.mipmap.bg_wallet_normal);
//        LineAnimTask task = new LineAnimTask();
//        task.execute(-50);


    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(Utils.isLogined()){
            LogUtils.i(TAG, "Utils.isLogined 为 true");
            if (currIndex == 1){
                layoutClick(R.id.my_wallet_button);
            }else{
                layoutClick(R.id.cg_wallet_button);
            }
//            showDialog();
//            layoutClick(R.id.my_wallet_button);
//            if(!Utils.getIsMask(this)){//判断是否显示过遮罩层
//
//            }else{
//                startActivity(new Intent(this,LoginActivity.class));
//                setLeftMenuInfo(0);//未登录
//            }
        }else{
            LogUtils.i(TAG, "Utils.isLogined 为 false");
//            showDialog();
            layoutClick(R.id.my_wallet_button);
//            if (currIndex == 1){
//                layoutClick(R.id.my_wallet_button);
//            }else{
//                layoutClick(R.id.cg_wallet_button);
//            }
        }
            String userMobile = Utils.getUserPhone(this);
            LogUtils.i(TAG, "islogin: " + Utils.isLogined() + " mobile: " + Utils.getUserPhone(this));
            if(Utils.isLogined()){
                tvShowLoginMobile.setText(Utils.getHasStarsMobile(userMobile));
                setLeftMenuInfo(1);//已登录
            }else{
                setLeftMenuInfo(0);//未登录
            }
            if(Utils.getisLockPassWord(this,Utils.getUserPhone(this)) == 1){//判断该账号是否是第一次登录该手机
                Utils.saveisLockPassWord(this,Utils.getUserPhone(this),2);
                setPassWord();
            }
            if(Value != 0){
                switch (Value){
                    case Constants.WHAT_IS_MY://显示我的钱包
    //                    if(!Utils.isLogined()){
    //                        startActivity(new Intent(MainActivity.this,LoginActivity.class));
    //                    }else{
    //                        currIndex = 2;
    //                        layoutClick(R.id.layout_my_wallet);
    //                    }
    //                    break;
                    case 2:

                        break;
                    case 3:

                        break;
                }
                Value = 0;
            }

    JPushInterface.onResume(this);
    MobclickAgent.onResume(this);
}
    /**
     * 设置是否使用手势密码
     * */
    private void setPassWord(){
        LayoutInflater mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = mInflater.inflate(R.layout.dialog_firstlogin_setlock,null);
        TextView leftView = (TextView) layout.findViewById(R.id.tv_first_login_left);
        TextView rightView = (TextView) layout.findViewById(R.id.tv_first_login_right);

        final Dialog dialog =
                new Dialog(this,R.style.loading_dialog2);
        dialog.setContentView(layout);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        leftView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        rightView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Intent lockIntent = new Intent(MainActivity.this, GestureEditActivity.class);
                startActivity(lockIntent);
            }
        });
    }

    @Override
    public void startProcessBar() {
        if(progressDialog == null){
            progressDialog = new ProgressDialog(this,R.style.loading_dialog);
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
        Utils.makeToast(this, Constants.IS_EVENT_MSG);
    }

    @Override
    public void startNextActivity() {

    }

    /**
     * 根据登录状态，改变左边菜单栏的控件显示
     * @param type
     */
    private void setLeftMenuInfo(int type){
        if(type == 1){//已登录
            LogUtils.i(TAG,"已登录");
            layotExit.setVisibility(View.VISIBLE);
            tvShowLoginMobile.setVisibility(View.VISIBLE);
            image_Login.setImageResource(R.mipmap.icon_yes_login);
            tvLogin.setVisibility(View.GONE);
        }else if(type == 0){//未登录
            if(tvLogin.getVisibility() == View.GONE){
                LogUtils.i(TAG, "未登录");
                tvLogin.setVisibility(View.VISIBLE);
            }
            layotExit.setVisibility(View.GONE);
            tvShowLoginMobile.setVisibility(View.GONE);
            image_Login.setImageResource(R.mipmap.icon_no_login);
        }else{
            LogUtils.i(TAG,"未进入登录状况的判断");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.cg_wallet_button://显示草根钱包页面
                if(SlidingMenu.isshow){
                    if(currIndex == 2){
                        break;
                    }
                    currIndex = 2;
                    layoutClick(v.getId());
                }
                break;
            case R.id.my_wallet_button://显示我的钱包页面
                if(SlidingMenu.isshow){
                    if(currIndex == 1){
                        break;
                    }
                    currIndex = 1;
                    layoutClick(v.getId());
                }
                break;
            case R.id.left_menu_safe_center://安全中心
                if(Utils.isLogined()){//判断是否登录
                    startActivity(new Intent(this,SafeCenterActivity.class));
                }else{
                    startActivity(new Intent(this,LoginActivity.class));
                }
                mMenu.leftToggle();
                break;
            case R.id.left_menu_help_center://帮助中心
                startActivity(new Intent(this,WebViewActivity.class)
                .putExtra("url","https://d5ds88.cgtz.com/version/e/detail")
                .putExtra("title","帮助中心"));
                mMenu.leftToggle();
                break;
            case R.id.left_menu_cgtz://草根投资
                mMenu.leftToggle();
                startActivity(new Intent(this,WebViewActivity.class)
                        .putExtra("url","http://m.cgtz.com/")
                        .putExtra("title","草根投资"));
                break;
            case R.id.left_menu_more://更多
                startActivity(new Intent(this,MenuMoreActivity.class));
                mMenu.leftToggle();
                break;
            case R.id.tv_goToLogin://去登录或者个人信息
                startActivity(new Intent(this,LoginActivity.class));
                mMenu.leftToggle();
                break;
            case R.id.left_menu_login_out://退出登录
                Utils.loginExit(this);
                setLeftMenuInfo(0);
                startActivity(new Intent(this, LoginActivity.class));
                break;
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }



    class LineAnimTask extends AsyncTask<Integer, Integer, Integer> {

        @Override
        protected Integer doInBackground(Integer... speed) {
            int leftMargin = params.leftMargin;
            // 根据传入的速度来滚动界面，当滚动到达边界值时，跳出循环。
            while (true) {
                leftMargin = leftMargin + speed[0];
                if(leftMargin > screenWith/2){
                    leftMargin = screenWith/2;
                    break;
                }
                if(leftMargin < 0){
                    leftMargin = 0;
                    break;
                }
                publishProgress(leftMargin);
                // 为了要有滚动效果产生，每次循环使线程睡眠一段时间，这样肉眼才能够看到滚动动画。
                sleep(15);
            }
            return leftMargin;
        }

        @Override
        protected void onProgressUpdate(Integer... leftMargin) {
//            params.leftMargin = leftMargin[0];
//            bottomLineSelected.setLayoutParams(params);
        }

        @Override
        protected void onPostExecute(Integer leftMargin) {
//            params.leftMargin = leftMargin;
//            bottomLineSelected.setLayoutParams(params);
        }
    }

    private void sleep(int time){
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
//        LogUtils.e(TAG,Utils.getUserId()+"     "+ Utils.getLockPassword(this, Utils.getUserPhone(this))+"      "+Constants.GESTURES_PASSWORD );
        if(Utils.getUserId() != "" && Utils.getLockPassword(this, Utils.getUserPhone(this))!=""&& Constants.GESTURES_PASSWORD  ){//用于判断是否进入手势输入页面
            Intent intent  = new Intent();
            intent.setClass(this,GestureVerifyActivity.class);
            startActivity(intent);
        }else{
//            Toast.makeText(this, "未设置手势密码", Toast.LENGTH_SHORT);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Constants.GESTURES_PASSWORD =true;
    }
    @Override
    protected void onPause() {
        super.onPause();
        JPushInterface.onPause(this);
        MobclickAgent.onPause(this);
        Constants.GESTURES_PASSWORD =false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(connectNet);
        closeDialog();
    }

    private void closeDialog(){
        if(dialog_main != null && dialog_main.isShowing()){
            dialog_main.dismiss();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    public void ValueforActivity(int event) {
        Value = event;
    }

    long waitTime = 2000;
    long touchTime = 0;
    @Override
    public void onBackPressed() {
        long currentTime = System.currentTimeMillis();
        if ((currentTime - touchTime) >= waitTime) {
            Toast.makeText(this, "再按一次退出应用", Toast.LENGTH_SHORT).show();
            touchTime = currentTime;
        } else {
            MApplication.finishAllActivitys();
            finish();
        }
    }
}
