package cgtz.com.cgwallet.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

import cgtz.com.cgwallet.MApplication;
import cgtz.com.cgwallet.R;
import cgtz.com.cgwallet.fragment.E_all_records_fragment_1;
import cgtz.com.cgwallet.utils.Utils;
import cgtz.com.cgwallet.widget.ProgressDialog;

/**
 * 草根钱包  转入转出流水
 * Created by Administrator on 2015-3-16.
 */
public class E_wallet_record_activity extends  BaseActivity implements View.OnClickListener{

    private static final String TAG = "E_wallet_record_activity";
    /**
     *
     * 三个个选项按钮
     */
    private Button buttonOne;
    private Button buttonTwo;
    private Button button3;

    private View green_sliders;//滚动条
    /**
     * 作为页面容器的ViewPager
     */
    private ViewPager viewpager;
    /**
     * 页面集合
     */
    private List<Fragment> fragmentList;

    /**
     * 三个个Fragment
     */
    private E_all_records_fragment_1 e_all_records_fragment;
    private E_all_records_fragment_1 e_all_records_fragment1;
    private E_all_records_fragment_1 e_all_records_fragment2;
    private ProgressDialog progressDialog;
    //屏幕宽度
    int screenWidth;


    //当前选中项
    private int currenttab = 0;

    private LinearLayout animation;//存钱  取钱布局按钮
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_e_record);
        MApplication.registActivities(this);//存储该activity
        showBack(true);
        setTitle("钱包资金流水");
        init();
    }
    /**
     * 初始化
     */
    private void init(){
        screenWidth=getResources().getDisplayMetrics().widthPixels;
        buttonOne = (Button) findViewById(R.id.btn_one);
        buttonTwo = (Button) findViewById(R.id.btn_two);
        button3 = (Button) findViewById(R.id.btn_3);
        green_sliders = findViewById(R.id.green_sliders);
        animation = (LinearLayout) findViewById(R.id.animation);
        RelativeLayout.LayoutParams imageParams=new RelativeLayout.LayoutParams(screenWidth/3, ViewGroup.LayoutParams.WRAP_CONTENT);
        green_sliders.setLayoutParams(imageParams);
        if(progressDialog == null){
            progressDialog = new ProgressDialog(this,R.style.loading_dialog);
        }

        buttonOne.setOnClickListener(this);
        buttonTwo.setOnClickListener(this);
        button3.setOnClickListener(this);

        viewpager = (ViewPager)  findViewById(R.id.viewpager);
        viewpager.setOffscreenPageLimit(2);//增加viewpager 缓存页面
        fragmentList = new ArrayList<Fragment>();
        e_all_records_fragment = new E_all_records_fragment_1();
        e_all_records_fragment.setType(1,this);
        e_all_records_fragment1 = new E_all_records_fragment_1();
        e_all_records_fragment1.setType(2,this);
        e_all_records_fragment2 = new E_all_records_fragment_1();
        e_all_records_fragment2.setType(3, this);

        fragmentList.add(e_all_records_fragment);
        fragmentList.add(e_all_records_fragment1);
        fragmentList.add(e_all_records_fragment2);

        switch (currenttab){//改变选中项的背景
            case 0:
                buttonOne.setBackgroundResource(R.color.white);
                buttonOne.setTextColor(getResources().getColor(R.color.save_or_draw_money_layout_text));
                break;
        }

        viewpager.setAdapter(new MyFrageStatePagerAdapter(getSupportFragmentManager()));
        viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                imageMove(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        changeView(getIntent().getIntExtra("choose",0));
    }
    /**
     * 定义自己的ViewPager适配器
     * 也可以使用FragmentPagerAdapter,关于这两者之间的区别，可以自己在百度搜一下
     */
    class MyFrageStatePagerAdapter extends FragmentStatePagerAdapter {

        public MyFrageStatePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            return fragmentList.get(i);
        }
        @Override
        public int getCount() {
            return fragmentList.size();
        }


        @Override
        public void startUpdate(ViewGroup container) {
            super.startUpdate(container);
            int currentItem = viewpager.getCurrentItem();
            if(currentItem == currenttab){
                return;
            }

//            imageMove(viewpager.getCurrentItem());
            currenttab = viewpager.getCurrentItem();
        }

        @Override
        public void finishUpdate(ViewGroup container) {
            super.finishUpdate(container);//这句话要放在最前面，否则会报错
        }
    }
    private void imageMove(int moveToTab){
        int startPosition=0;
        int movetoPosition=0;

        startPosition=currenttab*(screenWidth/3);
        movetoPosition=moveToTab*(screenWidth/3);
        //平移动画
        TranslateAnimation translateAnimation=new TranslateAnimation(startPosition,movetoPosition, 0, 0);
        translateAnimation.setFillAfter(true);
        translateAnimation.setDuration(200);
        green_sliders.startAnimation(translateAnimation);
        switch(moveToTab){//改变选中项的颜色
            case 0:
                buttonOne.setBackgroundResource(R.color.white);
                buttonOne.setTextColor(getResources().getColor(R.color.save_or_draw_money_layout_text));
                break;
            case 1:
                buttonTwo.setBackgroundResource(R.color.white);
                buttonTwo.setTextColor(getResources().getColor(R.color.save_or_draw_money_layout_text));
                break;
            case 2:
                button3.setBackgroundResource(R.color.white);
                button3.setTextColor(getResources().getColor(R.color.save_or_draw_money_layout_text));
        }
        switch (currenttab){//改变上一个选中项的颜色
            case 0:
                buttonOne.setBackgroundResource(R.color.bg_projectlist_more);
                buttonOne.setTextColor(getResources().getColor(R.color.remond_take_turns_text1));
                break;
            case 1:
                buttonTwo.setBackgroundResource(R.color.bg_projectlist_more);
                buttonTwo.setTextColor(getResources().getColor(R.color.remond_take_turns_text1));
                break;
            case 2:
                button3.setBackgroundResource(R.color.bg_projectlist_more);
                button3.setTextColor(getResources().getColor(R.color.remond_take_turns_text1));
                break;
        }

    }

    //手动设置ViewPager要显示的视图
    private void changeView(int desTab){
        viewpager.setCurrentItem(desTab,true);
        imageMove(desTab);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_one:
                changeView(0);
                break;
            case R.id.btn_two:
                changeView(1);
                break;
            case R.id.btn_3:
                changeView(2);
                break;
        }
    }
    int i=0;
    public  void setanimation(){//取钱和存钱位移动画
        if(i==0){
        i++;
        TranslateAnimation animationOpen =    new TranslateAnimation(0,0,58,58);
        animationOpen.setDuration(10000);
            animationOpen.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    i--;
                    E_wallet_record_activity.this.animation.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        animation.setAnimation(animationOpen);
        }

    }
    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
