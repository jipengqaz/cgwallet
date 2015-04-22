package cgtz.com.cgwallet.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.ScrollView;

/**
 * 自定义Scrollview
 * 解决viewpager与scrollview焦点冲突问题
 * Created by Administrator on 2015/4/22.
 */
public class MScrollView extends ScrollView {
    GestureDetector gestureDetector;
    public MScrollView(Context context) {
        super(context);
        gestureDetector = new GestureDetector(context,new YScrollDetactor());
        setFadingEdgeLength(0);
    }
    public MScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        gestureDetector = new GestureDetector(context,new YScrollDetactor());
        setFadingEdgeLength(0);
    }
    public MScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        gestureDetector = new GestureDetector(context,new YScrollDetactor());
        setFadingEdgeLength(0);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return super.onInterceptTouchEvent(ev) && gestureDetector.onTouchEvent(ev);
    }

    class YScrollDetactor extends GestureDetector.SimpleOnGestureListener{
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if(Math.abs(distanceX) > Math.abs(distanceY)){
                return true;
            }
            return false;
        }
    }
}
