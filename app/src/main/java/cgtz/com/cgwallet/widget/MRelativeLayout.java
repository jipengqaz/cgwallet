package cgtz.com.cgwallet.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

import cgtz.com.cgwallet.utils.LogUtils;

/**
 * 自定义控件
 * 对于滑动时，是否拦截子控件的touch事件
 * Created by Administrator on 2015/4/23.
 */
public class MRelativeLayout extends RelativeLayout {
    private static final String TAG = "MRelativeLayout";
    private float xDistance;
    private float yDistance;
    private float xLast;
    private float yLast;
    private int currIndex;//记录viewpager滑动的子页下标
    private boolean showLeftMenu = false;//判断是否已显示左边菜单
    private boolean showRightMenu = false;//判断是否已显示右边菜单

    public MRelativeLayout(Context context) {
        super(context);
    }

    public MRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setCurrIndex(int currIndex){
        this.currIndex = currIndex;
    }

    public void setxDistance(float xDistance) {
        this.xDistance = xDistance;
    }

    public void setShowLeftMenu(boolean showLeftMenu) {
        this.showLeftMenu = showLeftMenu;
    }

    public void setShowRightMenu(boolean showRightMenu) {
        this.showRightMenu = showRightMenu;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        switch (action){
            case MotionEvent.ACTION_DOWN:
                xDistance = yDistance = 0f;
                xLast = ev.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                final float curX = ev.getX();
                xDistance = curX - xLast;
                if(xDistance > 0 && currIndex == 0){
                    LogUtils.e(TAG,"xDistance > 0 && currIndex == 0");
                    return true;
                }else if(xDistance < 0 && currIndex == 1){
                    LogUtils.e(TAG,"xDistance < 0 && currIndex == 1");
                    return true;
                }else if(showLeftMenu && xDistance < 0 && currIndex == 0){
                    return true;
                }else if(showRightMenu && xDistance > 0 && currIndex == 1){
                    return true;
                }
            case MotionEvent.ACTION_UP:

                break;
        }
        return super.onInterceptTouchEvent(ev);
    }
}
