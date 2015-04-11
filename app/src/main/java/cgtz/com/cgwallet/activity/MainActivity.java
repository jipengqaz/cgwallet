package cgtz.com.cgwallet.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import java.util.ArrayList;

import cgtz.com.cgwallet.R;
import cgtz.com.cgwallet.adapter.MFragmentPagerAdater;
import cgtz.com.cgwallet.fragment.CgWalletFragment;
import cgtz.com.cgwallet.fragment.MyWalletFragment;
import cgtz.com.cgwallet.presenter.SplashPresenter;
import cgtz.com.cgwallet.utils.LogUtils;
import cgtz.com.cgwallet.view.BidirSlidingLayout;
import cgtz.com.cgwallet.view.ISplashView;

/**
 * 首页
 */
public class MainActivity extends FragmentActivity implements ISplashView{
    private static final String TAG = "MainActivity";
    private BidirSlidingLayout bidirSldingLayout;
    private RelativeLayout conter_menu_layout;
    private LinearLayout main_conter_layout;
    private ImageView showLeftButton;
    private ImageView showRightButton;
    private ViewPager mViewPager;
    private SplashPresenter splashPresenter;
    private ArrayList<Fragment> listFms;
    private int currIndex;//当前页卡编号
    /**
     * 记录手指按下时的横坐标。
     */
    private float xDown;

    /**
     * 记录手指按下时的纵坐标。
     */
    private float yDown;

    /**
     * 记录手指移动时的横坐标。
     */
    private float xMove;

    /**
     * 记录手指移动时的纵坐标。
     */
    private float yMove;

    /**
     * 记录手机抬起时的横坐标。
     */
    private float xUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        splashPresenter = new SplashPresenter(this);
        initViews();
        initFragment();
        bidirSldingLayout.setScrollEvent(main_conter_layout);
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
        main_conter_layout = (LinearLayout) findViewById(R.id.main_conter_layout);
        mViewPager = (ViewPager) findViewById(R.id.main_conter_viewpager);
    }

    private void initFragment(){
        listFms = new ArrayList<>();
        listFms.add(new CgWalletFragment());
        listFms.add(new MyWalletFragment());
        mViewPager.setAdapter(new MFragmentPagerAdater(getSupportFragmentManager(), listFms));
        mViewPager.setCurrentItem(0);
        mViewPager.setOnPageChangeListener(new MyOnPageChangeListener());
        mViewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        // 手指按下时，记录按下时的坐标
                        xDown = event.getRawX();
                        yDown = event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        xMove = event.getRawX();
                        yMove = event.getRawY();
                        // 手指移动时，对比按下时的坐标，计算出移动的距离。
                        int moveDistanceX = (int) (xMove - xDown);
                        int moveDistanceY = (int) (yMove - yDown);
                        LogUtils.i(TAG,"xmove: "+moveDistanceX);
                        break;
                    case MotionEvent.ACTION_UP:

                        break;
                }
                return false;
            }
        });
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
}
