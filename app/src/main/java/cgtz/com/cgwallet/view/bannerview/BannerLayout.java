package cgtz.com.cgwallet.view.bannerview;

import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import cgtz.com.cgwallet.R;

public class BannerLayout extends LinearLayout {
	private Context context;
	private LayoutInflater inflater;
	public AutoScrollViewPager bannerViewPager;
	private CirclePageIndicator pageIndicator;
	private int mTouchSlop2;
	private float mPrevX;

	public BannerLayout(Context context) {
		this(context, null);
		
	}

	public BannerLayout(Context context, AttributeSet attrs) {
		this(context, attrs,0);
		
	}
	
	public BannerLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
		mTouchSlop2 = ViewConfiguration.get(context).getScaledTouchSlop();
		inflater = LayoutInflater.from(context);
		setUpViews();
	}

	private void setUpViews() {
		inflater.inflate(R.layout.layout_banner_indicator, this);
		bannerViewPager = (AutoScrollViewPager) findViewById(R.id.banner_viewpager);
		bannerViewPager.setCycle(true);
		bannerViewPager.setInterval(3000);
		bannerViewPager.startAutoScroll();
		pageIndicator = (CirclePageIndicator) findViewById(R.id.banner_indicator);

	}

	public void initPagerIndicator() {
		pageIndicator.setViewPager(bannerViewPager);
	}

	public void hidePageIndicator() {
		if (pageIndicator != null) {
			pageIndicator.setVisibility(View.GONE);
		}
	}

	public void showPageIndicator() {
		if (pageIndicator != null) {
			pageIndicator.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		// LZLog.pLog("BannerLayout","dispatchTouchEvent mTouchSlop2:" +
		// mTouchSlop2);
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mPrevX = MotionEvent.obtain(event).getX();
			// LZLog.pLog("BannerLayout","prevX:" + mPrevX);
			break;
		case MotionEvent.ACTION_MOVE:
			final float eventX = event.getX();
			// LZLog.pLog("BannerLayout","eventX:" + eventX);
			float xDiff = Math.abs(eventX - mPrevX);
			// LZLog.pLog("BannerLayout","xDiff" + xDiff);
			if (xDiff < mTouchSlop2) {
				getParent().requestDisallowInterceptTouchEvent(false);
			}
		}
		return super.dispatchTouchEvent(event);
	}
}
