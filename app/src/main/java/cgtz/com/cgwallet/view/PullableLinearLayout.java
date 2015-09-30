package cgtz.com.cgwallet.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * Created by Administrator on 2015/9/30.
 */
public class PullableLinearLayout extends LinearLayout implements Pullable{
    private boolean canPullUp;
    private boolean canPullDown = true;
    public PullableLinearLayout(Context context) {
        super(context);
    }
    public PullableLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PullableLinearLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    @Override
    public boolean canPullDown() {
            if (getScrollY() == 0)
                return canPullDown = true;
            else
                return canPullDown = false;
    }

    @Override
    public boolean canPullUp() {
        return false;
    }
}
