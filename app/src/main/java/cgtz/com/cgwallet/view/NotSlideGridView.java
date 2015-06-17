package cgtz.com.cgwallet.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.GridView;

/**
 * 自定义Gridview
 * 用来取消GridView的滑动效果
 * Created by Administrator on 2014/9/20.
 */
public class NotSlideGridView extends GridView {
    public NotSlideGridView(Context context) {
        super(context, null);
    }
    public NotSlideGridView(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
    }
    public NotSlideGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if(ev.getAction() == MotionEvent.ACTION_MOVE){
            return true;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(
                Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);//让控件不能滑动
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
