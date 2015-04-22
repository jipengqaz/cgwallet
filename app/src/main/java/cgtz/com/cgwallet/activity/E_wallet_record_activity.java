package cgtz.com.cgwallet.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


import java.util.ArrayList;
import java.util.List;

import cgtz.com.cgwallet.R;
import cgtz.com.cgwallet.fragment.E_all_records_fragment_1;
import cn.jpush.android.api.JPushInterface;

/**
 * 草根钱包  转入转出流水
 * Created by Administrator on 2015-3-16.
 */
public class E_wallet_record_activity extends  BaseActivity implements View.OnClickListener{

    private static final String TAG = "My_investment_Activity";
    /**
     *
     * 三个个选项按钮
     */
    private Button buttonOne;
    private Button buttonTwo;
    private Button button3;

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

    //当前选中项
    private int currenttab = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_e_record);
        setTitle("草根钱包");
        init();
    }
    /**
     * 初始化
     */
    private void init(){
        buttonOne = (Button) findViewById(R.id.btn_one);
        buttonTwo = (Button) findViewById(R.id.btn_two);
        button3 = (Button) findViewById(R.id.btn_3);

        buttonOne.setOnClickListener(this);
        buttonTwo.setOnClickListener(this);
        button3.setOnClickListener(this);

        viewpager = (ViewPager)  findViewById(R.id.viewpager);
        viewpager.setOffscreenPageLimit(2);//增加viewpager 缓存页面
        fragmentList = new ArrayList<Fragment>();
        e_all_records_fragment = new E_all_records_fragment_1();
        e_all_records_fragment.setType(1);
        e_all_records_fragment1 = new E_all_records_fragment_1();
        e_all_records_fragment1.setType(2);
        e_all_records_fragment2 = new E_all_records_fragment_1();
        e_all_records_fragment2.setType(3);

        fragmentList.add(e_all_records_fragment);
        fragmentList.add(e_all_records_fragment1);
        fragmentList.add(e_all_records_fragment2);

        switch (currenttab){//改变选中项的背景
            case 0:
                buttonOne.setBackgroundResource(R.color.white);
                buttonOne.setTextColor(getResources().getColor(R.color.investment_details_green));
                break;
        }

        viewpager.setAdapter(new MyFrageStatePagerAdapter(getSupportFragmentManager()));
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
        public void finishUpdate(ViewGroup container) {
            super.finishUpdate(container);//这句话要放在最前面，否则会报错
            int currentItem = viewpager.getCurrentItem();
            if(currentItem == currenttab){
                return;
            }

            imageMove(viewpager.getCurrentItem());
            currenttab = viewpager.getCurrentItem();
        }
    }
    private void imageMove(int moveToTab){
        switch(moveToTab){//改变选中项的颜色
            case 0:
                buttonOne.setBackgroundResource(R.color.white);
                buttonOne.setTextColor(getResources().getColor(R.color.investment_details_green));
                break;
            case 1:
                buttonTwo.setBackgroundResource(R.color.white);
                buttonTwo.setTextColor(getResources().getColor(R.color.investment_details_green));
                break;
            case 2:
                button3.setBackgroundResource(R.color.white);
                button3.setTextColor(getResources().getColor(R.color.investment_details_green));
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
    @Override
    protected void onResume() {
        super.onResume();
        JPushInterface.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        JPushInterface.onPause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
