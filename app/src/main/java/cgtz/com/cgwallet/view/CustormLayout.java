package cgtz.com.cgwallet.view;

import android.content.Context;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.RelativeLayout;

import cgtz.com.cgwallet.utils.LogUtils;

/**
 * 自定义布局，两边带左右滑动菜单
 * Created by Administrator on 2015/4/9.
 */
public class CustormLayout extends RelativeLayout implements View.OnTouchListener{
    private static final String TAG = "CustormLayout";
    public static final int SNAP_VELOCITY = 200;//滚动显示和隐藏左侧布局时，手指滑动需要达到的速度
    public static final int DO_NOTHING = 0;//滑动状态的一种，表示未进行任何滑动
    public static final int SHOW_LEFT_MENU = 1;//滑动状态的一种，表示正在滑出左侧菜单
    public static final int SHOW_RIGHT_MENU = 2;//滑动状态的一种，表示正在滑出右侧菜单
    public static final int HIDE_LEFT_MENU = 3;//滑动状态的一种，表示正在隐藏左侧菜单
    public static final int HIDE_RIGHT_MENU = 4;//滑动状态的一种，表示正在隐藏右侧菜单
    private int slideState;//记录当前的滑动状态
    private int touchSlop;//在被判定为滚动之前用户手指可以移动的最大值
    private float xDown;//记录手指按下时的纵坐标。
    private float yDown;//记录手指按下时的横坐标
    private float xMove;//记录手指移动时的横坐标
    private float yMove;//记录手指移动时的纵坐标
    private float xUp;//记录手机抬起时的横坐标
    private boolean isLeftMenuVisible;//左侧菜单当前是显示还是隐藏。只有完全显示或隐藏时才会更改此值，滑动过程中此值无效
    private boolean isRightMenuVisible;//右侧菜单当前是显示还是隐藏。只有完全显示或隐藏时才会更改此值，滑动过程中此值无效
    private boolean isSliding;//是否正在滑动

    private View leftLayout;//左侧布局
    private View rightLayout;//右侧布局
    private View conterLayout;//中间布局
    private MarginLayoutParams leftParams;
    private MarginLayoutParams rightParams;
    private RelativeLayout.LayoutParams contentLayoutParams;
    private VelocityTracker mVelocityTracker;//用于计算手指滑动的速度
    private View mBindView;//用于监听滑动事件的View
    private int bothSides = 200;//两边布局的宽度
    private int screenWidth;//屏幕宽度
    private double bothSidesPercent = 2/3;//两边布局宽度占屏幕宽度比例
    private Context context;
    private boolean showLeft = false;//判断是否显示左边布局
    private boolean showRight = false;//判断是否显示右边布局

    public CustormLayout(Context context) {
        this(context, null);
    }
    public CustormLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public CustormLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        bothSides = (int) (screenWidth * bothSidesPercent);
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    /**
     * 绑定监听滑动事件的View。
     * @param bindView
     */
    public void setScrollEvent(View bindView){
        mBindView = bindView;
        mBindView.setOnTouchListener(this);
    }

    /**
     * 将界面滚动到左侧菜单界面，滚动速度设定为-30.
     */
    public void scrollToLeftMenu(){
        new LeftMenuScrollTask().equals(-30);
    }

    /**
     * 将界面滚动到右侧菜单界面，滚动速度设定为-30.
     */
    public void scrollToRightMenu(){
        new RightMenuScrollTask().equals(-30);
    }

    /**
     * 将界面从左侧菜单滚动到内容界面，滚动速度设定为30.
     */
    public void scrollToConterFromLeftMenu(){
        new LeftMenuScrollTask().equals(30);
    }

    /**
     * 将界面从右侧菜单滚动到内容界面，滚动速度设定为30.
     */
    public void scrollToContenrFromRightMenu(){
        new RightMenuScrollTask().equals(30);
    }

    /**
     * 左侧菜单是否完全显示出来，滑动过程中此值无效。
     * @return 左侧菜单完全显示返回true，否则返回false。
     */
    public boolean isLeftLayoutVisible(){
        return isLeftMenuVisible;
    }

    /**
     * 右侧菜单是否完全显示出来，滑动过程中此值无效。
     * @return 右侧菜单完全显示返回true，否则返回false。
     */
    public boolean isRightLayoutVisible(){
        return isRightMenuVisible;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if(changed){
            //获取左侧布局
            leftLayout =  getChildAt(0);
            leftParams = (MarginLayoutParams) leftLayout.getLayoutParams();
            //获取右侧布局
            rightLayout =  getChildAt(1);
            rightParams = (MarginLayoutParams) rightLayout.getLayoutParams();
            //获取中间布局
            conterLayout =  getChildAt(2);
            contentLayoutParams = (LayoutParams) conterLayout.getLayoutParams();
            contentLayoutParams.width = screenWidth;
            conterLayout.setLayoutParams(contentLayoutParams);
//            leftParams.width = bothSides;
//            leftLayout.setLayoutParams(leftParams);
//            rightParams.width = bothSides;
//            rightLayout.setLayoutParams(rightParams);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        creatVelocityTracker(event);//创建VelocityTracker对象，并将触摸事件加入到VelocityTracker当中
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                xDown = event.getRawX();
                yDown = event.getRawY();
                //将滑动状态初始化为DO_NOTHING
                slideState = DO_NOTHING;
                break;
            case MotionEvent.ACTION_MOVE:
                xMove = event.getRawX();
                yMove = event.getRawY();
                //手指移动时，对按下时的坐标，计算移动距离
                int moveDistanceX = (int) (xMove - xDown);
                int moveDistanceY = (int) (yMove - yDown);
                //检查当前的滑动状态
                checkSlideState(moveDistanceX,moveDistanceY);
                switch (slideState){
                    case SHOW_LEFT_MENU:
                        //显示左侧菜单
                        contentLayoutParams.rightMargin = -moveDistanceX;
                        checkLeftMenuBorder();
                        conterLayout.setLayoutParams(contentLayoutParams);
                        break;
                    case HIDE_LEFT_MENU:
                        contentLayoutParams.rightMargin = -leftParams.width - moveDistanceX;
                        checkLeftMenuBorder();
                        conterLayout.setLayoutParams(contentLayoutParams);
                        break;
                    case SHOW_RIGHT_MENU:
                        contentLayoutParams.leftMargin = moveDistanceX;
                        checkRightMenuBorder();
                        conterLayout.setLayoutParams(contentLayoutParams);
                        break;
                    case HIDE_RIGHT_MENU:
                        contentLayoutParams.leftMargin = -rightParams.width + moveDistanceX;
                        checkRightMenuBorder();
                        conterLayout.setLayoutParams(contentLayoutParams);
                        break;
                    default:
                        break;
                }
                break;
            case MotionEvent.ACTION_UP:
                LogUtils.e(TAG, "come in action_up");
                xUp = event.getRawX();
                int upDistanceX = (int) (xUp - xDown);
                if(isSliding){
                    //手指抬起时，进行  判断当前手势的意图
                    switch (slideState){
                        case SHOW_LEFT_MENU:
                            //显示左侧菜单
                            if(shouldScrollToLeftMenu()){
                                scrollToLeftMenu();
                            }else{
                                scrollToConterFromLeftMenu();
                            }
                            break;
                        case HIDE_LEFT_MENU:
                            if(shouldScrollToContentFromLeftMenu()){
                                scrollToConterFromLeftMenu();
                            }else{
                                scrollToLeftMenu();
                            }
                            break;
                        case SHOW_RIGHT_MENU:
                            if(shouldScrollToRightMenu()){
                                scrollToRightMenu();
                            }else{
                                scrollToContenrFromRightMenu();
                            }
                            break;
                        case HIDE_RIGHT_MENU:
                            if(shouldScrollToContentFromRightMenu()){
                                scrollToContenrFromRightMenu();
                            }else{
                                scrollToRightMenu();
                            }
                            break;
                        default:
                            break;
                    }
                }else if(upDistanceX < touchSlop && isLeftMenuVisible){
                    // 当左侧菜单显示时，如果用户点击一下内容部分，则直接滚动到内容界面
                    scrollToConterFromLeftMenu();
                }else if(upDistanceX < touchSlop && isRightMenuVisible){
                    // 当右侧菜单显示时，如果用户点击一下内容部分，则直接滚动到内容界面
                    scrollToContenrFromRightMenu();
                }
                recycleVelocityTracker();
                break;
        }
        if(v.isEnabled()){
            if(isSliding){
                //正在滑动时，让控件得不到焦点
                unFocusBindView();
                return true;
            }
            if(isLeftMenuVisible || isRightMenuVisible){
                //当左侧或右侧布局显示时，将绑定控件的事件屏蔽掉
                return true;
            }
            return false;
        }
        return true;
    }

    /**
     * 使用可以获得焦点的控件在滑动的时候失去焦点。
     */
    private void unFocusBindView(){
        if(mBindView != null){
            mBindView.setPressed(false);
            mBindView.setFocusable(false);
            mBindView.setFocusableInTouchMode(false);
        }
    }

    /**
     * 回收VelocityTracker对象。
     */
    private void recycleVelocityTracker(){
        mVelocityTracker.recycle();
        mVelocityTracker = null;
    }

    /**
     * 判断是否应该从右侧菜单滚动到内容布局，如果手指移动距离大于右侧菜单宽度的1/2，或者手指移动速度大于SNAP_VELOCITY，
     *  就认为应该从右侧菜单滚动到内容布局。
     * @return 如果应该从右侧菜单滚动到内容布局返回true，否则返回false。
     */
    private boolean shouldScrollToContentFromRightMenu(){
        return xUp - xDown > rightParams.width / 2 || getScrollVelocity() > SNAP_VELOCITY;
    }

    /**
     * 判断是否应该滚动将右侧菜单展示出来。如果手指移动距离大于右侧菜单宽度的1/2，或者手指移动速度大于SNAP_VELOCITY，
     * 就认为应该滚动将右侧菜单展示出来。
     *
     * @return 如果应该将右侧菜单展示出来返回true，否则返回false。
     */
    private boolean shouldScrollToRightMenu(){
        return xDown - xUp > rightParams.width / 2 || getScrollVelocity() > SNAP_VELOCITY;
    }

    /**
     * 判断是否应该从左侧菜单滚动到内容布局，如果手指移动距离大于左侧菜单宽度的1/2，或者手指移动速度大于SNAP_VELOCITY，
     * 就认为应该从左侧菜单滚动到内容布局。
     * @return 如果应该从左侧菜单滚动到内容布局返回true，否则返回false。
     */
    private boolean shouldScrollToContentFromLeftMenu(){
        return xDown - xUp > leftParams.width / 2 || getScrollVelocity() > SNAP_VELOCITY;
    }

    /**
     * 判断是否应该滚动将左侧菜单展示出来。如果手指移动距离大于左侧菜单宽度的1/2，或者手指移动速度大于SNAP_VELOCITY，
     * 就认为应该滚动将左侧菜单展示出来。
     * @return 如果应该将左侧菜单展示出来返回true，否则返回false。
     */
    private boolean shouldScrollToLeftMenu(){
        return xUp - xDown > leftParams.width / 2 || getScrollVelocity() > SNAP_VELOCITY;
    }

    /**
     * 获取手指在绑定布局上的滑动速度。
     * @return 滑动速度，以每秒钟移动了多少像素值为单位
     */
    private int getScrollVelocity(){
        mVelocityTracker.computeCurrentVelocity(1000);
        int velocity = (int) mVelocityTracker.getXVelocity();
        return Math.abs(velocity);
    }

    /**
     * 在滑动过程中检查右侧菜单的边界值，防止绑定布局滑出屏幕。
     */
    private void checkRightMenuBorder(){
        if(contentLayoutParams.leftMargin > 0){
            contentLayoutParams.leftMargin = 0;
        }else if(contentLayoutParams.leftMargin < - rightParams.width){
            contentLayoutParams.leftMargin = - rightParams.width;
        }
    }

    /**
     * 在滑动过程中检查左侧菜单的边界值，防止绑定布局滑出屏幕。
     */
    private void checkLeftMenuBorder(){
        if(contentLayoutParams.rightMargin > 0){
            contentLayoutParams.rightMargin = 0;
        }else if(contentLayoutParams.rightMargin < -leftParams.width){
            contentLayoutParams.rightMargin = -leftParams.width;
        }
    }

    /**
     * 根据手指移动的距离，判断当前用户的滑动意图，然后给slideState赋值成相应的滑动状态值。
     * @param moveDistanceX 横向移动的距离
     * @param moveDistanceY 纵向移动的距离
     */
    private void checkSlideState(int moveDistanceX,int moveDistanceY){
        if(isLeftMenuVisible){
            if(!isSliding && Math.abs(moveDistanceX) >= touchSlop && moveDistanceX < 0){
                isSliding = true;
                slideState = HIDE_LEFT_MENU;
            }
        }else if(isRightMenuVisible){
            if(!isSliding && Math.abs(moveDistanceX) >= touchSlop && moveDistanceX > 0){
                isSliding = true;
                slideState = HIDE_RIGHT_MENU;
            }
        }else {
            if(!isSliding && Math.abs(moveDistanceX) >= touchSlop && moveDistanceX > 0
                    && Math.abs(moveDistanceY) < touchSlop){
                isSliding = true;
                slideState = SHOW_LEFT_MENU;
                contentLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT,0);
                contentLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                conterLayout.setLayoutParams(contentLayoutParams);
                // 如果用户想要滑动左侧菜单，将左侧菜单显示，右侧菜单隐藏
                leftLayout.setVisibility(VISIBLE);
                rightLayout.setVisibility(GONE);
            }else if(!isSliding && Math.abs(moveDistanceX) >= touchSlop && moveDistanceX < 0
                    && Math.abs(moveDistanceY) < touchSlop){
                isSliding = true;
                slideState = SHOW_RIGHT_MENU;
                contentLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT,0);
                contentLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                conterLayout.setLayoutParams(contentLayoutParams);
                // 如果用户想要滑动右侧菜单，将右侧菜单显示，左侧菜单隐藏
                leftLayout.setVisibility(GONE);
                rightLayout.setVisibility(VISIBLE);
            }
        }
    }

    /**
     * 创建VelocityTracker对象，并将触摸事件加入到VelocityTracker当中
     * @param event 右侧布局监听控件的滑动事件
     */
    private void creatVelocityTracker(MotionEvent event){
        if(mVelocityTracker == null){
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
    }

    private void sleep(int time){
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    class LeftMenuScrollTask extends AsyncTask<Integer,Integer,Integer>{
        @Override
        protected Integer doInBackground(Integer... params) {
            int rightMargin = contentLayoutParams.rightMargin;
            //根据传入的速度来滚动界面，当滚动到达界值时，跳出循环
            while(true){
                rightMargin = rightMargin + params[0];
                if(rightMargin < -leftParams.width){
                    rightMargin = -leftParams.width;
                    break;
                }
                if(rightMargin > 0){
                    rightMargin = 0;
                    break;
                }
                publishProgress(rightMargin);
                // 为了要有滚动效果产生，每次循环使线程睡眠一段时间，这样肉眼才能够看到滚动动画。
                sleep(15);
            }
            if(params[0] > 0){
                isLeftMenuVisible = false;
            }else{
                isLeftMenuVisible = true;
            }
            isSliding = false;
            return rightMargin;
        }

        @Override
        protected void onProgressUpdate(Integer... rightMargin) {
            contentLayoutParams.rightMargin = rightMargin[0];
            conterLayout.setLayoutParams(contentLayoutParams);
            unFocusBindView();
        }

        @Override
        protected void onPostExecute(Integer rightMargin) {
            contentLayoutParams.rightMargin = rightMargin;
            conterLayout.setLayoutParams(contentLayoutParams);
        }
    }

    class RightMenuScrollTask extends AsyncTask<Integer,Integer,Integer>{
        @Override
        protected Integer doInBackground(Integer... params) {
            int leftMargin = contentLayoutParams.leftMargin;
            //根据传入的速度来滚动界面，当滚动到达界值时，跳出循环
            while(true){
                leftMargin = leftMargin + params[0];
                if(leftMargin < -rightParams.width){
                    leftMargin = -rightParams.width;
                    break;
                }
                if(leftMargin > 0){
                    leftMargin = 0;
                    break;
                }
                publishProgress(leftMargin);
                // 为了要有滚动效果产生，每次循环使线程睡眠一段时间，这样肉眼才能够看到滚动动画。
                sleep(15);
            }
            if(params[0] > 0){
                isRightMenuVisible = false;
            }else{
                isRightMenuVisible = true;
            }
            isSliding = false;
            return leftMargin;
        }

        @Override
        protected void onProgressUpdate(Integer... leftMargin) {
            contentLayoutParams.leftMargin = leftMargin[0];
            conterLayout.setLayoutParams(contentLayoutParams);
            unFocusBindView();
        }

        @Override
        protected void onPostExecute(Integer leftMargin) {
            contentLayoutParams.leftMargin = leftMargin;
            conterLayout.setLayoutParams(contentLayoutParams);
        }
    }
}
