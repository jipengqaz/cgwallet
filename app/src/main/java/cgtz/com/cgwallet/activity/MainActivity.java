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

import java.util.ArrayList;

import cgtz.com.cgwallet.R;
import cgtz.com.cgwallet.adapter.MFragmentPagerAdater;
import cgtz.com.cgwallet.fragment.CgWalletFragment;
import cgtz.com.cgwallet.fragment.MyWalletFragment;
import cgtz.com.cgwallet.presenter.SplashPresenter;
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
}
