package cgtz.com.cgwallet.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * 自定义Viewpager
 * 增加控制viewpager的滑动和禁止滑动切换功能
 * Created by Administrator on 2015/4/22.
 */
public class MViewPager extends ViewPager {
    private float xDistance;
    private float yDistance;
    private float xLast;
    private float yLast;
    public MViewPager(Context context) {
        super(context);
    }
    public MViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return super.onTouchEvent(ev);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
//        boolean ret = super.dispatchTouchEvent(ev);
//        if(ret){
//            requestDisallowInterceptTouchEvent(true);
//        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
//        int action = ev.getAction();
//        switch (action){
//            case MotionEvent.ACTION_DOWN:
//                xDistance = yDistance = 0f;
//                xLast = ev.getX();
//                yLast = ev.getY();
//                break;
//            case MotionEvent.ACTION_MOVE:
//                final float curX = ev.getX();
//                final float cury = ev.getY();
//                xDistance += Math.abs(curX - xLast);
//                yDistance += Math.abs(cury - yLast);
//                xLast = curX;
//                yLast = cury;
//                if(xDistance > yDistance){
//                    return false;
//                }
//        }
        return super.onInterceptTouchEvent(ev);
    }
}
