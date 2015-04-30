package cgtz.com.cgwallet.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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

import org.json.JSONObject;

import java.util.ArrayList;

import cgtz.com.cgwallet.MApplication;
import cgtz.com.cgwallet.R;
import cgtz.com.cgwallet.bean.JsonBean;
import cgtz.com.cgwallet.client.Get_share_content;
import cgtz.com.cgwallet.fragment.CgWalletFragment;
import cgtz.com.cgwallet.fragment.MyWalletFragment;
import cgtz.com.cgwallet.presenter.SplashPresenter;
import cgtz.com.cgwallet.utility.Constants;
import cgtz.com.cgwallet.utils.HttpUtils;
import cgtz.com.cgwallet.utils.LogUtils;
import cgtz.com.cgwallet.utils.Utils;
import cgtz.com.cgwallet.view.BidirSlidingLayout;
import cgtz.com.cgwallet.view.ISplashView;
import cgtz.com.cgwallet.widget.ProgressDialog;
import cn.jpush.android.api.JPushInterface;

/**
 * 首页
 */
public class MainActivity extends FragmentActivity implements ISplashView,View.OnClickListener{
    private static final String TAG = "MainActivity";
    private BidirSlidingLayout bidirSldingLayout;
    private RelativeLayout conter_menu_layout;
    private RelativeLayout main_conter_layout;
    private LinearLayout layoutCgWallet;//底部的草根钱包
    private LinearLayout layoutMyWallet;//底部的我的钱包
    private ImageView showLeftButton;
    private ImageView showRightButton;
//    private ViewPager mViewPager;
    private LinearLayout menuSafeCenter;//安全中心
    private LinearLayout menuHelpCenter;//帮助中心
    private LinearLayout menuMore;//更多
    private TextView tvLogin;//未登录显示文案或者显示登录的手机号
    private TextView tvShowLoginMobile;//显示登录的手机号
    private LinearLayout layotExit;//退出登录
    private SplashPresenter splashPresenter;
    private ArrayList<Fragment> listFms;
    private int currIndex;//当前页卡编号
    private ProgressDialog progressDialog;
    private CgWalletFragment cgWalletFragment;
    private MyWalletFragment myWalletFragment;
    private ImageView bottomLineSelected;//底部的白线
    private LinearLayout layoutBottom;//底部的选项
    private Fragment[] fragments;
    private LinearLayout centerWallet;
    private int screenWith;
    private LinearLayout.LayoutParams params;
    private ImageView cgWalletIcon;
    private ImageView myWalletIcon;
    private TextView cgWalletText;
    private TextView myWalletText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        MApplication.registActivities(this);//存储该activity
        splashPresenter = new SplashPresenter(this);

        screenWith = getResources().getDisplayMetrics().widthPixels;
        initViews();
        setFragment();
//        initFragment();
        setViewLinstener();
        bidirSldingLayout.setScrollEvent(findViewById(R.id.center_wallet));
        showLeftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bidirSldingLayout.isLeftLayoutVisible()) {
                    bidirSldingLayout.scrollToContentFromLeftMenu();
                } else {
                    bidirSldingLayout.initShowLeftState();
                    bidirSldingLayout.scrollToLeftMenu();
                }
            }
        });
        showRightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bidirSldingLayout.isRightLayoutVisible()) {
                        bidirSldingLayout.scrollToContentFromRightMenu();
                } else {
                    if(Utils.isLogined()) {
                    Get_share_content.getContent(handler);
                    bidirSldingLayout.initShowRightState();
                    bidirSldingLayout.scrollToRightMenu();
                    }else{
                        startActivity(new Intent(MainActivity.this,LoginActivity.class));
                    }
                }
            }
        });
        initShare();
    }
    //获取友盟分享变量
    private final UMSocialService mController = UMServiceFactory.getUMSocialService("com.umeng.share");
    private String content="",url="";//分享内容  和  分享链接
    private ImageView Qr_code;//二维码
    private Handler handler = new Handler(){//获取分享内容的
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
                    JsonBean jsonBean = (JsonBean) msg.obj;
                    int code = jsonBean.getCode();
                    String errorMsg = jsonBean.getError_msg();
                    JSONObject json = null;
                    if(!Utils.filtrateCode(MainActivity.this,jsonBean)){
                        Toast.makeText(MainActivity.this,errorMsg+"  错误码"+code,Toast.LENGTH_SHORT);
                        return;
                    }
                    json = jsonBean.getJsonObject();
                    if(json.optInt("success") == 1) {
                        LogUtils.e(TAG, json + "");
                        content = json.optString("content");
                        url = json.optString("url");
                        qrcode = json.optString("qrcode");
                        new Thread(connectNet).start();
                    }
                    break;
                case 1:
                    if (bitmap != null) {
                        Qr_code.setImageBitmap(bitmap);// 显示获取的二维码
                    }
                    break;
            }
        }
    };
    private boolean isShare = false;
    private Bitmap bitmap = null;
    private String qrcode;
    //获取二维码的线程
    private Runnable connectNet = new Runnable(){
        @Override
        public void run() {
            //取得的是byte数组, 从byte数组生成bitmap
            byte[] data = new byte[0];
            try {
                data = HttpUtils.getImage(qrcode);
                if (data != null) {
                    bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);// bitmap
                } else {

                }
                handler.sendEmptyMessage(1);
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
                Toast.makeText(MainActivity.this, "分享成功", Toast.LENGTH_SHORT)
                        .show();
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
                        aa = mController.getConfig().getSsoHandler(HandlerRequestCode.QZONE_REQUEST_CODE).isClientInstalled();
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
                        }else{
                        }
                        break;
                    case R.id.qzone:

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
                        break;
                    case R.id.sms:
                        // 设置短信分享内容
                        SmsShareContent sms = new SmsShareContent();
                        sms.setShareContent(content);
                        mController.setShareMedia(sms);
                        // 参数1为Context类型对象， 参数2为要分享到的目标平台， 参数3为分享操作的回调接口
                        mController.postShare(MainActivity.this, SHARE_MEDIA.SMS, mShareListener);
                        break;
                    case R.id.wechat://微信
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
                        break;
                    case R.id.wxcircle://朋友圈
                        //设置微信朋友圈分享内容
                        CircleShareContent circleMedia = new CircleShareContent();
                        circleMedia.setShareContent(content);
                        //设置朋友圈title
                        circleMedia.setTitle("草根投资");
                        circleMedia.setShareImage(image);
                        circleMedia.setTargetUrl(url);
                        mController.setShareMedia(circleMedia);
                        // 参数1为Context类型对象， 参数2为要分享到的目标平台， 参数3为分享操作的回调接口
                        mController.postShare(MainActivity.this, SHARE_MEDIA.WEIXIN_CIRCLE, mShareListener);
                        break;
                    case R.id.sina://新浪
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

    private void initViews(){
        cgWalletIcon = (ImageView) findViewById(R.id.cg_wallet_icon);
        myWalletIcon = (ImageView) findViewById(R.id.my_wallet_icon);
        cgWalletText = (TextView) findViewById(R.id.cg_wallet_text);
        myWalletText = (TextView) findViewById(R.id.my_wallet_text);
        centerWallet = (LinearLayout) findViewById(R.id.center_wallet);//钱包外部布局
        layoutCgWallet = (LinearLayout) findViewById(R.id.layout_cg_wallet);//底部的草根钱包
        layoutMyWallet = (LinearLayout) findViewById(R.id.layout_my_wallet);//底部的我的钱包
        bidirSldingLayout = (BidirSlidingLayout) findViewById(R.id.custom_sliding_layout);
        conter_menu_layout = (RelativeLayout) findViewById(R.id.content);
        showLeftButton = (ImageView) findViewById(R.id.show_left_button);
        showRightButton = (ImageView) findViewById(R.id.show_right_button);
//        main_conter_layout = (RelativeLayout) findViewById(R.id.main_conter_layout);
//        mViewPager = (ViewPager) findViewById(R.id.main_conter_viewpager);
        tvLogin = (TextView) findViewById(R.id.tv_goToLogin);
        menuSafeCenter = (LinearLayout) findViewById(R.id.left_menu_safe_center);
        menuHelpCenter = (LinearLayout) findViewById(R.id.left_menu_help_center);
        menuMore = (LinearLayout) findViewById(R.id.left_menu_more);
        tvShowLoginMobile = (TextView) findViewById(R.id.tv_show_login_mobile);
        layotExit = (LinearLayout) findViewById(R.id.left_menu_login_out);
        layoutBottom = (LinearLayout) findViewById(R.id.layout_bottom);
        bottomLineSelected = (ImageView) findViewById(R.id.wallet_bottom_line_selected);//底部的选中
        params = (LinearLayout.LayoutParams) bottomLineSelected.getLayoutParams();
        params.width = screenWith/2;
        bottomLineSelected.setLayoutParams(params);
    }

    private void setViewLinstener(){
        tvLogin.setOnClickListener(this);
        menuSafeCenter.setOnClickListener(this);
        menuHelpCenter.setOnClickListener(this);
        menuMore.setOnClickListener(this);
        layotExit.setOnClickListener(this);
        layoutCgWallet.setOnClickListener(this);//底部的草根钱包
        layoutMyWallet.setOnClickListener(this);//底部的我的钱包
    }

    /**
     * 初始化fragment
     */
    private void setFragment(){
        fragments = new Fragment[2];
        fragments[0] = getSupportFragmentManager().findFragmentById(R.id.menu_center_fragment1);
        fragments[1] = getSupportFragmentManager().findFragmentById(R.id.menu_center_fragment2);
        cgWalletFragment = (CgWalletFragment) fragments[0];
        myWalletFragment = (MyWalletFragment) fragments[1];
        getSupportFragmentManager().beginTransaction().hide(fragments[1]).hide(fragments[0]).show(fragments[0]).commit();
    }

    public void layoutClick(int type){
        switch (type){
            case R.id.layout_cg_wallet://显示草根钱包页面
                currIndex = 1;
                getSupportFragmentManager().beginTransaction()
                        .hide(fragments[0])
                        .hide(fragments[1])
                        .show(fragments[0]).commit();
                lineToLeft();
                break;
            case R.id.layout_my_wallet://显示我的钱包页面
                if(!Utils.isLogined()){
                    startActivity(new Intent(MainActivity.this,LoginActivity.class));
                }else{
                    currIndex = 2;
                    getSupportFragmentManager().beginTransaction()
                            .hide(fragments[0])
                            .hide(fragments[1])
                            .show(fragments[1]).commit();
                    lineToRight();
                    myWalletFragment.setData(true);
                }
                break;
        }

    }

    /**
     * 滑动到右边
     */
    private void lineToRight(){
        cgWalletIcon.setImageResource(R.mipmap.icon_cg_wallet_normal);
        myWalletIcon.setImageResource(R.mipmap.icon_my_wallet_selected);
        cgWalletText.setTextColor(getResources().getColor(R.color.layout_bottom_transfer_right_text));
        myWalletText.setTextColor(getResources().getColor(R.color.layout_bottom_transfer_left_text));
        layoutCgWallet.setBackgroundResource(R.mipmap.bg_wallet_normal);
        layoutMyWallet.setBackgroundResource(R.mipmap.bg_wallet_selected);
        LineAnimTask task = new LineAnimTask();
        task.execute(50);
    }

    /**
     * 滑动到左边
     */
    private void lineToLeft(){
        cgWalletIcon.setImageResource(R.mipmap.icon_wallet);
        myWalletIcon.setImageResource(R.mipmap.icon_my_wallet);
        cgWalletText.setTextColor(getResources().getColor(R.color.layout_bottom_transfer_left_text));
        myWalletText.setTextColor(getResources().getColor(R.color.layout_bottom_transfer_right_text));
        layoutCgWallet.setBackgroundResource(R.mipmap.bg_wallet_selected);
        layoutMyWallet.setBackgroundResource(R.mipmap.bg_wallet_normal);
        LineAnimTask task = new LineAnimTask();
        task.execute(-50);

    }

    private void initFragment(){
        listFms = new ArrayList<>();
        cgWalletFragment = new CgWalletFragment();
        myWalletFragment = new MyWalletFragment();
//        FragmentTransaction transaction = fragmentManager.beginTransaction();
//        transaction.replace(R.id.menu_center_fragment, cgWalletFragment);
//        transaction.addToBackStack(null);
//        transaction.commit();
//        listFms.add(cgWalletFragment);
//        listFms.add(myWalletFragment);
//        mViewPager.setAdapter(new MFragmentPagerAdater(getSupportFragmentManager(), listFms));
//        mViewPager.setCurrentItem(0);
//        mViewPager.setOnPageChangeListener(new MyOnPageChangeListener());
//        bidirSldingLayout.setMovedLeft(true);
//        bidirSldingLayout.setMovedRight(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(Utils.isLogined() && TextUtils.isEmpty(Utils.getToken())){
            LogUtils.i(TAG, "MApplication.goLogin 为 true");
            layoutClick(R.id.layout_cg_wallet);
            setLeftMenuInfo(0);//未登录
//            currIndex = 0;
//            mViewPager.setCurrentItem(currIndex);
        }else{
            if(currIndex == 2){
                myWalletFragment.setData(true);
            }
        }
        String userMobile = Utils.getUserPhone(this);
        LogUtils.i(TAG, "islogin: " + Utils.isLogined() + " mobile: " + Utils.getUserPhone(this));
        if(Utils.isLogined()){
            tvShowLoginMobile.setText(Utils.getHasStarsMobile(userMobile));
            setLeftMenuInfo(1);//已登录
        }else{
            setLeftMenuInfo(0);//未登录
        }
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
        super.onDestroy();
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
            layotExit.setVisibility(View.VISIBLE);
            tvShowLoginMobile.setVisibility(View.VISIBLE);
            tvLogin.setVisibility(View.GONE);
        }else if(type == 0){//未登录
            layotExit.setVisibility(View.GONE);
            tvShowLoginMobile.setVisibility(View.GONE);
            tvLogin.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.layout_cg_wallet://显示草根钱包页面
                currIndex = 0;
                layoutClick(v.getId());
//                mViewPager.setCurrentItem(currIndex);
                break;
            case R.id.layout_my_wallet://显示我的钱包页面
                currIndex = 1;
                layoutClick(v.getId());
//                mViewPager.setCurrentItem(currIndex);
                break;
            case R.id.left_menu_safe_center://安全中心
                if(Utils.isLogined()){//判断是否登录
                    startActivity(new Intent(this,SafeCenterActivity.class));
                }else{
                    startActivity(new Intent(this,LoginActivity.class));
                }
                break;
            case R.id.left_menu_help_center://帮助中心
                startActivity(new Intent(this,WebViewActivity.class)
                .putExtra("url","https://d5ds88.cgtz.com/version/notice/FAQ")
                .putExtra("title","帮助中心"));
                break;
            case R.id.left_menu_more://更多
                startActivity(new Intent(this,MenuMoreActivity.class));
                break;
            case R.id.tv_goToLogin://去登录或者个人信息
                startActivity(new Intent(this,LoginActivity.class));
                break;
            case R.id.left_menu_login_out://退出登录
                Utils.loginExit(this);
                setLeftMenuInfo(0);
                break;
        }
    }

    /**
     * viewpager页面变化监听器
     */
    public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener{

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            currIndex = position;
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(isShare){
            isShare = false;
        }else{
        Constants.GESTURES_PASSWORD = true;
        }
    }

    @Override
    protected void onStart() {
        super.onRestart();
        if(Utils.getLockPassword(this, Utils.getUserPhone(this))!=""&& Constants.GESTURES_PASSWORD && Utils.getUserId() != ""){
            Intent intent  = new Intent();
            intent.setClass(this,GestureVerifyActivity.class);
            startActivity(intent);
        }else{
//            Utils.makeToast(this, "手势密码");
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
            params.leftMargin = leftMargin[0];
            bottomLineSelected.setLayoutParams(params);
        }

        @Override
        protected void onPostExecute(Integer leftMargin) {
            params.leftMargin = leftMargin;
            bottomLineSelected.setLayoutParams(params);
        }
    }

    private void sleep(int time){
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}
