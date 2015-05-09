package cgtz.com.cgwallet.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import cgtz.com.cgwallet.MApplication;
import cgtz.com.cgwallet.R;
import cgtz.com.cgwallet.activity.MainActivity;
import cgtz.com.cgwallet.utils.LogUtils;
import cgtz.com.cgwallet.utils.ScreenUtils;


public class SlidingMenu extends HorizontalScrollView{
	private static final String TAG = "SlidingMenu";
	/**
	 * 屏幕宽度
	 */
	private int mScreenWidth;
	/**
	 * dp
	 */
	private int mMenuRightPadding;
	/**
	 * 菜单的宽度
	 */
	private int mMenuWidth;
	private int mHalfMenuWidth;

	private boolean isOpen;

	private boolean once = false;

	private boolean showRightMenu =false;//是否显示右边菜单
	private boolean isLeft = true;//是否操作左边菜单

	private ViewGroup mMenu;
	private ViewGroup mContent;
	private ViewGroup mRightMenu;

	private int menuType;//用来存储显示的是哪一个菜单页面  0，左边 1，右边
	private static final int SHOW_LEFT_MENU = -1;//显示左边菜单
	private static final int SHOW_RIGHT_MENU = 1;//显示右边菜单
	private static final int HIDE_LEFT_MENU = -2;//隐藏左边菜单
	private static final int HIDE_RIGHT_MENU = 2;//隐藏右边菜单
	private static final int NO_MENU_SHOW = 0;//不做任何操作
	private boolean isShowLeftMenu = false;
	private boolean isShowRightMenu = false;

	public SlidingMenu(Context context, AttributeSet attrs)
	{
		this(context, attrs, 0);

	}

	public SlidingMenu(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		mScreenWidth = ScreenUtils.getScreenWidth(context);

		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.SlidingMenu);
		mMenuRightPadding = (int) a.getDimension(R.styleable.SlidingMenu_rightPadding,300);// 默认为10DP
//		int n = a.getIndexCount();
//		for (int i = 0; i < n; i++)
//		{
//			int attr = a.getIndex(i);
//			switch (attr)
//			{
//			case R.styleable.SlidingMenu_rightPadding:
//				// 默认50
//				mMenuRightPadding = a.getDimensionPixelSize(attr,
//						(int) TypedValue.applyDimension(
//								TypedValue.COMPLEX_UNIT_DIP, 50f,
//								getResources().getDisplayMetrics()));// 默认为10DP
//				break;
//			}
//		}
		a.recycle();
	}

	public SlidingMenu(Context context)
	{
		this(context, null, 0);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		/**
		 * 显示的设置一个宽度
		 */
		if (!once)
		{
			LinearLayout wrapper = (LinearLayout) getChildAt(0);
			mMenu = (ViewGroup) wrapper.getChildAt(0);
			mContent = (ViewGroup) wrapper.getChildAt(1);
			mRightMenu = (ViewGroup) wrapper.getChildAt(2);

//			mMenuWidth = mScreenWidth - mMenuRightPadding;
			mMenuWidth = mScreenWidth -(mScreenWidth/2-100) ;
			mHalfMenuWidth = mMenuWidth / 2;
			mMenu.getLayoutParams().width = mMenuWidth;
			mContent.getLayoutParams().width = mScreenWidth;
			mRightMenu.getLayoutParams().width = mMenuWidth;

		}
		Log.e("sliding","mMenuWidth: "+mMenuWidth+" screenWidth: "+mScreenWidth);
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b)
	{
		super.onLayout(changed, l, t, r, b);
		if (changed)
		{
			// 将菜单隐藏
			this.scrollTo(mMenuWidth, 0);
			once = true;
			showRightMenu = false;
			menuType = NO_MENU_SHOW;
		}
	}
	@Override
	public boolean onTouchEvent(MotionEvent ev)
	{
		int action = ev.getAction();
		switch (action){
			// Up时，进行判断，如果显示区域大于菜单宽度一半则完全显示，否则隐藏
			case MotionEvent.ACTION_UP:
				int scrollX = getScrollX();
				LogUtils.e(TAG,"scrollX: "+scrollX);
				changeWhichMenu(scrollX);
				switch (menuType){
					case SHOW_LEFT_MENU:
						//显示左边菜单
						isShowLeftMenu = true;
						isShowRightMenu = false;
						this.smoothScrollTo(0,0);
						return true;
					case SHOW_RIGHT_MENU:
						//显示右边菜单
						isShowLeftMenu = false;
						isShowRightMenu = true;
						this.smoothScrollTo(mMenuWidth*2,0);
						return true;
					case HIDE_LEFT_MENU:
						//隐藏左边菜单
						isShowLeftMenu = false;
						isShowRightMenu = false;
						this.smoothScrollTo(mMenuWidth,0);
						return true;
					case HIDE_RIGHT_MENU:
						//隐藏右边菜单
						isShowRightMenu = false;
						isShowLeftMenu = false;
						this.smoothScrollTo(mMenuWidth,0);
						return true;
				}
//				if (scrollX > mHalfMenuWidth){
//					isLeft = true;
//					this.smoothScrollTo(mMenuWidth, 0);
//					isOpen = false;
//				} else{
//					isLeft = true;
//					this.smoothScrollTo(0, 0);
//					isOpen = true;
//				}
				return true;
		}
		return super.onTouchEvent(ev);
	}

	@Override
	public void computeScroll() {
		super.computeScroll();
	}

	/***
	 * 根据滑动的方向和滑动的距离，判断是否显示左右菜单
	 * @param scrollX
	 */
	private void changeWhichMenu(int scrollX){
		if(scrollX >= 0 && scrollX < mMenuWidth && !isShowLeftMenu && !isShowRightMenu){
			//向右滑动，滑动距离大于菜单宽度，左右两边菜单都没有显示，允许显示左边菜单，
			menuType = SHOW_LEFT_MENU;
		}else if(mMenuWidth < scrollX && scrollX <= (mMenuWidth*2)
				&& !isShowLeftMenu && !isShowRightMenu){
			//手指向左滑动，滑动距离大于菜单宽度，左右菜单都未显示，允许显示右边菜单
			menuType = SHOW_RIGHT_MENU;
		}else if(scrollX >= 0 && isShowLeftMenu && !isShowRightMenu){
			//手指向左滑动，滑动距离大于菜单宽度，左边菜单显示，右边菜单未显示，隐藏左边菜单
			menuType = HIDE_LEFT_MENU;
		}else if(scrollX > 0 && !isShowLeftMenu && isShowRightMenu){
			//手指向右滑动，滑动距离大于菜单宽度，左边菜单未显示，右边菜单显示，隐藏右边菜单
			menuType = HIDE_RIGHT_MENU;
		}
	}

	/**
	 * 打开左边菜单
	 */
	public void showLeftMenu(){
		if(isShowLeftMenu){
			return;
		}
		this.smoothScrollTo(0,0);
		isShowLeftMenu = true;
	}

	public void hideLeftmenu(){
		if(isShowLeftMenu){
			this.smoothScrollTo(mMenuWidth,0);
			isShowLeftMenu = false;
		}
	}


	/**
	 * 打开菜单
	 */
	public void openMenu()
	{
		if (isOpen)
			return;
		this.smoothScrollTo(0, 0);
		isOpen = true;
	}

	/**
	 * 关闭菜单
	 */
	public void closeMenu(){
		isLeft = true;
		if (isOpen)
		{
			this.smoothScrollTo(mMenuWidth, 0);
			isOpen = false;
		}
	}

	/**
	 * 切换菜单状态
	 */
	public void leftToggle(){
		if (isShowLeftMenu){
			hideLeftmenu();
		} else{
			showLeftMenu();
		}
	}

	/**
	 * 右边菜单开关
	 */
	public void rightToggle(){
		if(isShowRightMenu){
			hideRightMenu();
		}else{
			showRightMenu();
			((MainActivity)MApplication.getActivityByName(MainActivity.class.getName())).initShareData();//设置分享  显示数据
		}
	}

	/**
	 * 显示右边菜单
	 */
	public void showRightMenu(){
		if(isShowRightMenu){
			return;
		}
		this.smoothScrollTo(mMenuWidth*2,0);
		isShowRightMenu = true;
	}

	/**
	 * 隐藏右边菜单
	 */
	public void hideRightMenu(){
		if(isShowRightMenu){
			this.smoothScrollTo(mMenuWidth,0);
			isShowRightMenu = false;
		}
	}

	/**
	 * 关闭右侧菜单
	 */
	private void closeRightMenu(){
		if (isShowRightMenu){
			this.smoothScrollTo(mMenuWidth, 0);
			showRightMenu = false;
		}
	}

	/**
	 * 打开右侧菜单
	 */
	private void openRightMenu(){
		if (showRightMenu){
			return;
		}
		this.smoothScrollTo((mMenuWidth+mScreenWidth), 0);
		showRightMenu = true;
	}

	@Override
	protected void onScrollChanged(int x, int y, int oldx, int oldy){
		super.onScrollChanged(x, y, oldx, oldy);
//		if(isShowLeftMenu && !isShowRightMenu){
//			LogUtils.e(TAG,"左边菜单操作,x: "+x);
//			float scale = x * 1.0f / mMenuWidth;
//			float leftScale = 1 - 0.3f * scale;
//			float rightScale = 0.8f + scale * 0.2f;
//			LogUtils.i("Sliding","左: leftScale: "+leftScale+" rightScale: "+rightScale+" open: "+isOpen);
//			ViewHelper.setScaleX(mMenu, leftScale);
//			ViewHelper.setScaleY(mMenu, leftScale);
//			ViewHelper.setAlpha(mMenu, 0.6f + 0.4f * (1 - scale));
//			ViewHelper.setTranslationX(mMenu, mMenuWidth * scale * 0.6f);
//
//			ViewHelper.setPivotX(mContent, 0);
//			ViewHelper.setPivotY(mContent, mContent.getHeight() / 2);
//			ViewHelper.setScaleX(mContent, rightScale);
//			ViewHelper.setScaleY(mContent, rightScale);
//		}
//		else if(isShowRightMenu && !isShowLeftMenu){
//			LogUtils.e(TAG,"右边菜单操作");
//			float scale = x * 1.0f / (mMenuWidth*2);
//			float rightScale = 1 - 0.3f * scale;
//			float leftScale = 0.8f + scale * 0.2f;
//			LogUtils.i("Sliding","右：leftScale: "+leftScale+" rightScale: "+rightScale);
//			ViewHelper.setScaleX(mRightMenu, rightScale);
//			ViewHelper.setScaleY(mRightMenu, rightScale);
//			ViewHelper.setAlpha(mRightMenu, 0.6f + 0.4f * (1 - scale));
//			ViewHelper.setTranslationX(mRightMenu, mMenuWidth * scale * 0.6f);
//
//			ViewHelper.setPivotX(mContent, 0);
//			ViewHelper.setPivotY(mContent, mContent.getHeight() / 2);
//			ViewHelper.setScaleX(mContent, leftScale);
//			ViewHelper.setScaleY(mContent, leftScale);
//		}
//		if(isLeft){
//			float scale = x * 1.0f / mMenuWidth;
//			float leftScale = 1 - 0.3f * scale;
//			float rightScale = 0.8f + scale * 0.2f;
//			LogUtils.i("Sliding","左: leftScale: "+leftScale+" rightScale: "+rightScale+" open: "+isOpen);
//			ViewHelper.setScaleX(mMenu, leftScale);
//			ViewHelper.setScaleY(mMenu, leftScale);
//			ViewHelper.setAlpha(mMenu, 0.6f + 0.4f * (1 - scale));
//			ViewHelper.setTranslationX(mMenu, mMenuWidth * scale * 0.6f);
//
//			ViewHelper.setPivotX(mContent, 0);
//			ViewHelper.setPivotY(mContent, mContent.getHeight() / 2);
//			ViewHelper.setScaleX(mContent, rightScale);
//			ViewHelper.setScaleY(mContent, rightScale);
//		}
//		else{
//			float scale = x * 1.0f / (mMenuWidth+mScreenWidth);
//			float rightScale = 1 - 0.3f * scale;
//			float leftScale = 0.8f + scale * 0.2f;
//			LogUtils.i("Sliding","右：leftScale: "+leftScale+" rightScale: "+rightScale);
//			ViewHelper.setScaleX(mRightMenu, rightScale);
//			ViewHelper.setScaleY(mRightMenu, rightScale);
//			ViewHelper.setAlpha(mRightMenu, 0.6f + 0.4f * (1 - scale));
//			ViewHelper.setTranslationX(mRightMenu, mMenuWidth * scale * 0.6f);
//
//			ViewHelper.setPivotX(mContent, 0);
//			ViewHelper.setPivotY(mContent, mContent.getHeight() / 2);
//			ViewHelper.setScaleX(mContent, leftScale);
//			ViewHelper.setScaleY(mContent, leftScale);
//		}
	}
}
