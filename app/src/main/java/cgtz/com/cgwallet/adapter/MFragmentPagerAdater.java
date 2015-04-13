package cgtz.com.cgwallet.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

/**
 *  自定义fragmentadapter
 * Created by Administrator on 2015/4/11.
 */
public class MFragmentPagerAdater extends FragmentPagerAdapter {
    private ArrayList<Fragment> lists;
    public MFragmentPagerAdater(FragmentManager fm) {
        super(fm);
    }

    public MFragmentPagerAdater(FragmentManager fm,ArrayList<Fragment> lists) {
        super(fm);
        this.lists = lists;
    }

    @Override
    public Fragment getItem(int position) {
        return lists.get(position);
    }

    @Override
    public int getCount() {
        return lists.size();
    }
}
