package cgtz.com.cgwallet.widget;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import com.nineoldandroids.view.ViewHelper;

import cgtz.com.cgwallet.MApplication;
import cgtz.com.cgwallet.R;
import cgtz.com.cgwallet.activity.LoginActivity;
import cgtz.com.cgwallet.activity.MainActivity;
import cgtz.com.cgwallet.utils.LogUtils;
import cgtz.com.cgwallet.utils.ScreenUtils;
import cgtz.com.cgwallet.utils.Utils;


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


	private boolean once = false;


	private ViewGroup mMenu;
	private ViewGroup mContent;
	private ViewGroup mRightMenu;
	private ViewGroup leftLayout1;
	private ViewGroup leftLayout2;
	private ViewGroup leftLayout3;


	private int menuType;//用来存储显示的是哪一个菜单页面  0，左边 1，右边
	private static final int SHOW_LEFT_MENU = -1;//显示左边菜单
	private static final int SHOW_RIGHT_MENU = 1;//显示右边菜单
	private static final int HIDE_LEFT_MENU = -2;//隐藏左边菜单
	private static final int HIDE_RIGHT_MENU = 2;//隐藏右边菜单
	private static final int NEED_TO_LOGIN = 3;//去登录
	private static final int NO_MENU_TOGGLE = 0;//没有菜单
	private boolean isShowLeftMenu = false;
	private boolean isShowRightMenu = false;
	private MainActivity bindActivity;//绑定的Activity
	private int rightSlidingMenu;//右边菜单显示时，向左滑动的距离

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

	public SlidingMenu(Context context){
		this(context, null, 0);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
		/**
		 * 显示的设置一个宽度
		 */
		if (!once){
			LinearLayout wrapper = (LinearLayout) getChildAt(0);
			mMenu = (ViewGroup) wrapper.getChildAt(0);
			mContent = (ViewGroup) wrapper.getChildAt(1);
			mRightMenu = (ViewGroup) wrapper.getChildAt(2);
			ViewGroup leftLayout = (ViewGroup) mMenu.getChildAt(0);
			leftLayout1 = (ViewGroup) ((ViewGroup) leftLayout.getChildAt(1)).getChildAt(0);
			leftLayout2 = (ViewGroup) ((ViewGroup) leftLayout.getChildAt(1)).getChildAt(1);
			leftLayout3 = (ViewGroup) ((ViewGroup) leftLayout.getChildAt(1)).getChildAt(2);

//			mMenuWidth = mScreenWidth - mMenuRightPadding;
			mMenuWidth = mScreenWidth -(mScreenWidth/2-100) ;
			rightSlidingMenu = mMenuWidth*2;
			mHalfMenuWidth = mMenuWidth/2-10;
			mMenu.getLayoutParams().width = mMenuWidth;
			mContent.getLayoutParams().width = mScreenWidth;
			mRightMenu.getLayoutParams().width = mMenuWidth;
		}
		Log.e("sliding","mMenuWidth: "+mMenuWidth+" screenWidth: " + mScreenWidth);
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		if (changed){
			// 将菜单隐藏
			this.scrollTo(mMenuWidth, 0);
			once = true;
			menuType = NO_MENU_TOGGLE;
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev){
		int action = ev.getAction();
		switch (action){
			case MotionEvent.ACTION_MOVE:
				LogUtils.e(TAG,"scrollX: "+getScrollX());
				changeWhichMenu(getScrollX());
				break;
			case MotionEvent.ACTION_UP:
				// Up时，进行判断，如果显示区域大于菜单宽度一半则完全显示，否则隐藏
				int scrollX = getScrollX();
//				changeWhichMenu(scrollX);
				switch (menuType){
					case SHOW_LEFT_MENU:
						//显示左边菜单
						setMenuFocus();
						isShowLeftMenu = true;
						isShowRightMenu = false;
						this.smoothScrollTo(0,0);
						focusToggle(false);
						break;
					case SHOW_RIGHT_MENU:
						//显示右边菜单
						setMenuFocus();
						isShowLeftMenu = false;
						isShowRightMenu = true;
						this.smoothScrollTo(rightSlidingMenu, 0);
						bindActivity.initShareData();//设置分享数据
						focusToggle(false);
						break;
					case HIDE_LEFT_MENU:
						//隐藏左边菜单
						setMenuFocus();
						isShowLeftMenu = false;
						isShowRightMenu = false;
						this.smoothScrollTo(mMenuWidth, 0);
						focusToggle(true);
						break;
					case HIDE_RIGHT_MENU:
						//隐藏右边菜单
						setMenuFocus();
						isShowRightMenu = false;
						isShowLeftMenu = false;
						this.smoothScrollTo(mMenuWidth, 0);
						focusToggle(true);
						break;
					case NEED_TO_LOGIN:
						//去登录
						setMenuFocus();
						isShowRightMenu = false;
						isShowLeftMenu = false;
						this.smoothScrollTo(mMenuWidth,0);
						focusToggle(true);
						bindActivity.startActivity(new Intent(bindActivity, LoginActivity.class));
						break;
					case NO_MENU_TOGGLE:
						setMenuFocus();
						isShowRightMenu = false;
						isShowLeftMenu = false;
						this.smoothScrollTo(mMenuWidth, 0);
						focusToggle(true);
						break;
				}
				return true;
		}
		return super.onTouchEvent(ev);
	}

	public void setBindActivity(MainActivity activity){
		bindActivity = activity;
	}

	/***
	 * 根据滑动的方向和滑动的距离，判断是否显示左右菜单
	 * @param scrollX
	 */
	private void changeWhichMenu(int scrollX){
		LogUtils.e(TAG,"传递的scrollX: "+scrollX);
		if(scrollX >= 0 && scrollX <= mMenuWidth - mHalfMenuWidth && !isShowLeftMenu && !isShowRightMenu){
			//向右滑动，滑动距离大于菜单宽度，左右两边菜单都没有显示，允许显示左边菜单，
			menuType = SHOW_LEFT_MENU;
		}else if(Utils.isLogined() && mHalfMenuWidth+mMenuWidth <= scrollX && scrollX <= rightSlidingMenu
				&& !isShowLeftMenu && !isShowRightMenu){
			//手指向左滑动，滑动距离大于菜单宽度，左右菜单都未显示，允许显示右边菜单
			menuType = SHOW_RIGHT_MENU;
		}else if(!Utils.isLogined() && mHalfMenuWidth+mMenuWidth <= scrollX && scrollX <= rightSlidingMenu
				&& !isShowLeftMenu && !isShowRightMenu){
			//向左滑动时，判断是否登录过，没有登录时，去登录
			menuType = NEED_TO_LOGIN;
		}else if(scrollX >= mHalfMenuWidth && isShowLeftMenu && !isShowRightMenu){
			//手指向左滑动，滑动距离大于菜单宽度，左边菜单显示，右边菜单未显示，隐藏左边菜单
			menuType = HIDE_LEFT_MENU;
		}else if(scrollX >= mHalfMenuWidth && !isShowLeftMenu && isShowRightMenu){
			//手指向右滑动，滑动距离大于菜单宽度，左边菜单未显示，右边菜单显示，隐藏右边菜单
			menuType = HIDE_RIGHT_MENU;
		}else if(scrollX > mMenuWidth - mHalfMenuWidth && scrollX <= mMenuWidth
				&& !isShowLeftMenu && !isShowRightMenu){
			menuType = HIDE_LEFT_MENU;
		}else if(Utils.isLogined() && mMenuWidth < scrollX && scrollX < mHalfMenuWidth+mMenuWidth
				&& !isShowLeftMenu && !isShowRightMenu){
			menuType = HIDE_RIGHT_MENU;
		}
	}

	/**
	 * 改变view焦点
	 * @param flag
	 */
	private void focusToggle(boolean flag){
		if(flag){
			// 有焦点
			bindActivity.requetFocus();
		}else{
			// 无焦点
			bindActivity.clearFocus();
		}
	}

	/**
	 * 打开左边菜单
	 */
	public void showLeftMenu(){
		LogUtils.i(TAG, "showLeftMenu");
		if(isShowLeftMenu){
			return;
		}
		menuType = SHOW_LEFT_MENU;
		setMenuFocus();
		this.smoothScrollTo(0, 0);
		isShowLeftMenu = true;
		isShowRightMenu = false;
		focusToggle(false);
	}

	/**
	 * 隐藏左边菜单
	 */
	public void hideLeftmenu(){
		LogUtils.i(TAG, "hideLeftmenu");
		if(isShowLeftMenu){
			menuType = HIDE_LEFT_MENU;
			setMenuFocus();
			this.smoothScrollTo(mMenuWidth,0);
			isShowLeftMenu = false;
			isShowRightMenu = false;
			focusToggle(true);
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
			bindActivity.initShareData();//设置分享数据
			showRightMenu();
		}
	}

	/**
	 * 显示右边菜单
	 */
	public void showRightMenu(){
		if(isShowRightMenu){
			return;
		}
		menuType = SHOW_RIGHT_MENU;
		setMenuFocus();
		this.smoothScrollTo(rightSlidingMenu,0);
		isShowRightMenu = true;
		isShowLeftMenu = false;
		focusToggle(false);
	}

	/**
	 * 隐藏右边菜单
	 */
	public void hideRightMenu(){
		if(isShowRightMenu){
			menuType = HIDE_RIGHT_MENU;
			setMenuFocus();
			this.smoothScrollTo(mMenuWidth,0);
			isShowRightMenu = false;
			isShowLeftMenu = false;
			focusToggle(true);
		}
	}

	private void setMenuFocus(){
		switch (menuType){
			case SHOW_LEFT_MENU:
				//显示左边菜单
				leftLayout1.setEnabled(true);
				leftLayout2.setEnabled(true);
				leftLayout3.setEnabled(true);

				mMenu.setFocusable(true);
				mRightMenu.setFocusable(false);
				mMenu.setFocusableInTouchMode(true);
				mRightMenu.setFocusableInTouchMode(false);
				break;
			case SHOW_RIGHT_MENU:
				//显示右边菜单
				leftLayout1.setEnabled(false);
				leftLayout2.setEnabled(false);
				leftLayout3.setEnabled(false);
				mMenu.setFocusable(false);
				mRightMenu.setFocusable(true);
				mMenu.setFocusableInTouchMode(false);
				mRightMenu.setFocusableInTouchMode(true);
				break;
			case HIDE_LEFT_MENU:
				//隐藏左边菜单
				leftLayout1.setEnabled(false);
				leftLayout2.setEnabled(false);
				leftLayout3.setEnabled(false);
				mMenu.setFocusable(false);
				mRightMenu.setFocusable(false);
				mMenu.setFocusableInTouchMode(false);
				mRightMenu.setFocusableInTouchMode(false);
				break;
			case HIDE_RIGHT_MENU:
				//隐藏右边菜单
				leftLayout1.setEnabled(false);
				leftLayout2.setEnabled(false);
				leftLayout3.setEnabled(false);
				mMenu.setFocusable(false);
				mRightMenu.setFocusable(false);
				mMenu.setFocusableInTouchMode(false);
				mRightMenu.setFocusableInTouchMode(false);
				break;
			case NEED_TO_LOGIN:
				//去登录
				leftLayout1.setEnabled(false);
				leftLayout2.setEnabled(false);
				leftLayout3.setEnabled(false);
				mMenu.setFocusable(false);
				mRightMenu.setFocusable(false);
				mMenu.setFocusableInTouchMode(false);
				mRightMenu.setFocusableInTouchMode(false);
				break;
		}

	}

	/**
	 * 计算滑动方向
	 * @param x
	 */
	private void scrollChangeToggle(int x){
		LogUtils.i(TAG,"menutype: "+ menuType);
		if(menuType == SHOW_LEFT_MENU || menuType == HIDE_LEFT_MENU){
			LogUtils.i(TAG,"左边菜单操作");
			//向右滑动，显示左边菜单
			LogUtils.e(TAG,"左边菜单操作,x: "+x);
			float scale = x * 1.0f / mMenuWidth;
			float leftScale = 1 - 0.2f * scale;
			float rightScale = 0.8f + scale * 0.2f;
			LogUtils.i("Sliding","左: leftScale: "+leftScale+" rightScale: "+rightScale+" scale: "+scale);
			ViewHelper.setScaleX(mMenu, leftScale);
			ViewHelper.setScaleY(mMenu, leftScale);
			ViewHelper.setAlpha(mMenu, 0.6f + 0.4f * (1 - scale));
			ViewHelper.setTranslationX(mMenu, mMenuWidth * scale * 0.6f);

			ViewHelper.setPivotX(mContent, 0);
			ViewHelper.setPivotY(mContent, mContent.getHeight() / 2);
			ViewHelper.setScaleX(mContent, rightScale);
			ViewHelper.setScaleY(mContent, rightScale);

		}else if(menuType == SHOW_RIGHT_MENU || menuType == HIDE_RIGHT_MENU || menuType == NEED_TO_LOGIN){
			LogUtils.i(TAG,"右边菜单操作");
			//向左滑动，显示右边菜单

			LogUtils.e(TAG,"右边菜单操作,x: "+x + " mMenuWidth: "+mMenuWidth);
			float scale = 1 - x * 1.0f / rightSlidingMenu;
			float leftScale = 0.8f + scale * 0.4f;
			float rightScale = 1 - 0.4f * scale;
			LogUtils.i("Sliding","右：leftScale: "+leftScale+" rightScale: "+rightScale+" scale: "+scale);
			ViewHelper.setScaleX(mRightMenu, rightScale);
			ViewHelper.setScaleY(mRightMenu, rightScale);
			ViewHelper.setAlpha(mRightMenu, 0.6f + 0.4f * scale);
			ViewHelper.setTranslationX(mRightMenu, mMenuWidth * scale * 0.6f);

			ViewHelper.setPivotX(mContent, mScreenWidth);
			ViewHelper.setPivotY(mContent, mContent.getHeight() / 2);
			ViewHelper.setScaleX(mContent, leftScale);
			ViewHelper.setScaleY(mContent, leftScale);

		}
	}

	@Override
	protected void onScrollChanged(int x, int y, int oldx, int oldy){
		super.onScrollChanged(x, y, oldx, oldy);
		LogUtils.e(TAG, "onScrollChanged x: " + x + " oldx: " + oldx);
		scrollChangeToggle(x);
	}

	@Override
	public void computeScroll() {
		LogUtils.i(TAG,"执行 computeScroll");
		super.computeScroll();
	}
}
