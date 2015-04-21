package cgtz.com.cgwallet.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

import cgtz.com.cgwallet.master.RippleManager;

/**
 * 点击后有波纹效果的布局
 * Created by Administrator on 2015/4/20.
 */
public class RippleLayout extends LinearLayout {
    private RippleManager mRippleManager = new RippleManager();

    public RippleLayout(Context context) {
        this(context, null);
    }

    public RippleLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0, 0);
    }

    public RippleLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public RippleLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr, defStyleRes);
    }


    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes){
        applyStyle(context, attrs, defStyleAttr, defStyleRes);
    }

    public void applyStyle(int resId){
        applyStyle(getContext(), null, 0, resId);
    }

    private void applyStyle(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes){
        mRippleManager.onCreate(this, context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        if(l == mRippleManager)
            super.setOnClickListener(l);
        else{
            mRippleManager.setOnClickListener(l);
            setOnClickListener(mRippleManager);
        }
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        boolean result = super.onTouchEvent(event);
        return  mRippleManager.onTouchEvent(event) || result;
    }
}
