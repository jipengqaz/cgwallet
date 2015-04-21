package cgtz.com.cgwallet.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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

import java.util.ArrayList;

import cgtz.com.cgwallet.R;
import cgtz.com.cgwallet.adapter.MFragmentPagerAdater;
import cgtz.com.cgwallet.fragment.CgWalletFragment;
import cgtz.com.cgwallet.fragment.MyWalletFragment;
import cgtz.com.cgwallet.presenter.SplashPresenter;
import cgtz.com.cgwallet.utility.Constants;
import cgtz.com.cgwallet.utils.Utils;
import cgtz.com.cgwallet.view.BidirSlidingLayout;
import cgtz.com.cgwallet.view.ISplashView;

/**
 * 首页
 */
public class MainActivity extends FragmentActivity implements ISplashView,View.OnClickListener{
    private static final String TAG = "MainActivity";
    private BidirSlidingLayout bidirSldingLayout;
    private RelativeLayout conter_menu_layout;
    private RelativeLayout main_conter_layout;
    private ImageView showLeftButton;
    private ImageView showRightButton;
    private ViewPager mViewPager;
//    private TextView tvCgWallet;//草根钱包
//    private TextView tvMyWallet;//我的钱包
    private TextView tvLogin;//未登录显示文案或者显示登录的手机号
    private SplashPresenter splashPresenter;
    private ArrayList<Fragment> listFms;
    private int currIndex;//当前页卡编号

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        splashPresenter = new SplashPresenter(this);
        initViews();
        initFragment();
        setViewLinstener();
        bidirSldingLayout.setScrollEvent(mViewPager);
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
                    bidirSldingLayout.initShowRightState();
                    bidirSldingLayout.scrollToRightMenu();
                }
            }
        });

        initShare();
    }
    //获取友盟分享变量
    final UMSocialService mController = UMServiceFactory.getUMSocialService("com.umeng.share");
    private boolean isShare = false;
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


        configuration();
        //分享回调
        final SocializeListeners.SnsPostListener snsPost = new SocializeListeners.SnsPostListener() {
            @Override
            public void onStart() {
                Log.e(TAG,Constants.GESTURES_PASSWORD+"");
                isShare = true;
                Toast.makeText(MainActivity.this, "开始分享.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onComplete(SHARE_MEDIA platform, int eCode, SocializeEntity entity) {
//                if (eCode == 200) {
//                    Toast.makeText(MainActivity.this, "分享成功.", Toast.LENGTH_SHORT).show();
//                } else {
//                    String eMsg = "";
//                    if (eCode == -101) {
//                        eMsg = "没有授权";
//                    }
//                    Toast.makeText(MainActivity.this, "分享失败[" + eCode + "] " +
//                            eMsg, Toast.LENGTH_SHORT).show();
//                }
            }
        };
        View.OnClickListener share_click = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //分享的图片
                UMImage image = new UMImage(MainActivity.this, R.mipmap.icon_bank);
                switch (v.getId()){
                    case R.id.QQ:
                        // 参数1为Context类型对象， 参数2为要分享到的目标平台， 参数3为分享操作的回调接口
                        mController.postShare(MainActivity.this, SHARE_MEDIA.QQ, snsPost);

                        QQShareContent qqShareContent = new QQShareContent();
                        //设置分享文字
                        qqShareContent.setShareContent("来自友盟社会化组件（SDK）让移动应用快速整合社交分享功能 -- QQ");
                        //设置分享title
                        qqShareContent.setTitle("hello, title");
                        //设置分享图片
                        qqShareContent.setShareImage(image);
                        //设置点击分享内容的跳转链接
                        qqShareContent.setTargetUrl("https://www.cgtz.com");
                        mController.setShareMedia(qqShareContent);
                        break;
                    case R.id.qzone:
                        // 参数1为Context类型对象， 参数2为要分享到的目标平台， 参数3为分享操作的回调接口
                        mController.postShare(MainActivity.this, SHARE_MEDIA.QZONE, snsPost);
                        QZoneShareContent qzone = new QZoneShareContent();
                        //设置分享文字
                        qzone.setShareContent("来自友盟社会化组件（SDK）让移动应用快速整合社交分享功能 -- QZone");
                        //设置点击消息的跳转URL
                        qzone.setTargetUrl("https://www.cgtz.com");
                        //设置分享内容的标题
                        qzone.setTitle("QZone title");
                        //设置分享图片
                        qzone.setShareImage(image);
                        mController.setShareMedia(qzone);
                        break;
                    case R.id.sms:
                        // 设置短信分享内容
                        SmsShareContent sms = new SmsShareContent();
                        sms.setShareContent("来自友盟社会化组件（SDK）让移动应用快速整合社交分享功能-短信。http://www.umeng.com/social");
                        sms.setShareImage(image);
                        mController.setShareMedia(sms);
                        break;
                    case R.id.wechat://微信
                        //设置微信好友分享内容
                        WeiXinShareContent weixinContent = new WeiXinShareContent();
                        //设置分享文字
                        weixinContent.setShareContent("来自友盟社会化组件（SDK）让移动应用快速整合社交分享功能，微信");
                        //设置title
                        weixinContent.setTitle("友盟社会化分享组件-微信");
                        //设置分享内容跳转URL
                        weixinContent.setTargetUrl("https://www.cgtz.com");
                        //设置分享图片
                        weixinContent.setShareImage(image);
                        mController.setShareMedia(weixinContent);
                        break;
                    case R.id.wxcircle://朋友圈
                        //设置微信朋友圈分享内容
                        CircleShareContent circleMedia = new CircleShareContent();
                        circleMedia.setShareContent("来自友盟社会化组件（SDK）让移动应用快速整合社交分享功能，朋友圈");
                        //设置朋友圈title
                        circleMedia.setTitle("友盟社会化分享组件-朋友圈");
                        circleMedia.setShareImage(image);
                        circleMedia.setTargetUrl("https://www.cgtz.com");
                        mController.setShareMedia(circleMedia);
                        break;
                    case R.id.sina://新浪
                        //设置新浪SSO handler
                        mController.getConfig().setSsoHandler(new SinaSsoHandler());
                        // 设置分享内容
                        mController.setShareContent("dfafdafsd");
                        //设置分享图片，参数2为本地图片的资源引用
                        mController.setShareMedia(image);
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
        qqSsoHandler.setTargetUrl("https://www.cgtz.com");
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
        bidirSldingLayout = (BidirSlidingLayout) findViewById(R.id.custom_sliding_layout);
        conter_menu_layout = (RelativeLayout) findViewById(R.id.content);
        showLeftButton = (ImageView) findViewById(R.id.show_left_button);
        showRightButton = (ImageView) findViewById(R.id.show_right_button);
        main_conter_layout = (RelativeLayout) findViewById(R.id.main_conter_layout);
        mViewPager = (ViewPager) findViewById(R.id.main_conter_viewpager);
//        tvCgWallet = (TextView) findViewById(R.id.tv_top_title_cg_wallet);
//        tvMyWallet = (TextView) findViewById(R.id.tv_top_title_my_wallet);
        tvLogin = (TextView) findViewById(R.id.tv_goToLogin);
    }

    private void setViewLinstener(){
//        tvCgWallet.setOnClickListener(this);
//        tvMyWallet.setOnClickListener(this);
        tvLogin.setOnClickListener(this);
    }

    private void initFragment(){
        listFms = new ArrayList<>();
        listFms.add(new CgWalletFragment());
        listFms.add(new MyWalletFragment());
        mViewPager.setAdapter(new MFragmentPagerAdater(getSupportFragmentManager(), listFms));
        mViewPager.setCurrentItem(0);
        mViewPager.setOnPageChangeListener(new MyOnPageChangeListener());
        bidirSldingLayout.setMovedLeft(true);
        bidirSldingLayout.setMovedRight(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        splashPresenter.didFinishLoading(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void startProcessBar() {

    }

    @Override
    public void hideProcessBar() {

    }

    @Override
    public void showNetError() {

    }

    @Override
    public void startNextActivity() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
//            case R.id.tv_top_title_cg_wallet://草根钱包
//                if(currIndex == 1){
//                    currIndex = 0;
//                    mViewPager.setCurrentItem(0);
//                    tvCgWallet.setBackgroundResource(R.drawable.bg_main_top_selected);
//                    tvMyWallet.setBackgroundResource(R.drawable.bg_main_top_normal);
//                    tvCgWallet.setTextColor(getResources().getColor(R.color.main_top_selected_text));
//                    tvMyWallet.setTextColor(getResources().getColor(R.color.main_top_normal_text));
//                    bidirSldingLayout.setMovedLeft(true);
//                    bidirSldingLayout.setMovedRight(false);
//                }
//                break;
//            case R.id.tv_top_title_my_wallet://我的钱包
//                if(currIndex == 0){
//                    currIndex = 1;
//                    mViewPager.setCurrentItem(1);
//                    tvCgWallet.setBackgroundResource(R.drawable.bg_main_top_normal);
//                    tvMyWallet.setBackgroundResource(R.drawable.bg_main_top_selected);
//                    tvCgWallet.setTextColor(getResources().getColor(R.color.main_top_normal_text));
//                    tvMyWallet.setTextColor(getResources().getColor(R.color.main_top_selected_text));
//                    bidirSldingLayout.setMovedRight(true);
//                    bidirSldingLayout.setMovedLeft(false);
//                }
//                break;
            case R.id.tv_goToLogin://去登录或者个人信息
                startActivity(new Intent(MainActivity.this,LoginOrRegistActivity.class));
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
            if(position == 1){
//                tvCgWallet.setBackgroundResource(R.drawable.bg_main_top_normal);
//                tvMyWallet.setBackgroundResource(R.drawable.bg_main_top_selected);
//                tvCgWallet.setTextColor(getResources().getColor(R.color.main_top_selected_text));
//                tvMyWallet.setTextColor(getResources().getColor(R.color.main_top_normal_text));
                bidirSldingLayout.setMovedRight(true);
                bidirSldingLayout.setMovedLeft(false);
            }else if(position == 0){
//                tvCgWallet.setBackgroundResource(R.drawable.bg_main_top_selected);
//                tvMyWallet.setBackgroundResource(R.drawable.bg_main_top_normal);
//                tvCgWallet.setTextColor(getResources().getColor(R.color.main_top_normal_text));
//                tvMyWallet.setTextColor(getResources().getColor(R.color.main_top_selected_text));
                bidirSldingLayout.setMovedLeft(true);
                bidirSldingLayout.setMovedRight(false);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            Log.i(TAG,"state: "+state);
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
        Log.e(TAG, "123456" + Constants.GESTURES_PASSWORD);
        if(Utils.getLockPassword(this, "123456")!=""&& Constants.GESTURES_PASSWORD){
            Intent intent  = new Intent();
            intent.setClass(this,GestureVerifyActivity.class);
            startActivity(intent);
        }else{
            Toast.makeText(this, ".............手势密码", Toast.LENGTH_SHORT);
        }
    }
}
