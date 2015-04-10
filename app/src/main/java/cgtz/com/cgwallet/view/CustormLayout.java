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
 * �Զ��岼�֣����ߴ����һ����˵�
 * Created by Administrator on 2015/4/9.
 */
public class CustormLayout extends RelativeLayout implements View.OnTouchListener{
    private static final String TAG = "CustormLayout";
    public static final int SNAP_VELOCITY = 200;//������ʾ��������಼��ʱ����ָ������Ҫ�ﵽ���ٶ�
    public static final int DO_NOTHING = 0;//����״̬��һ�֣���ʾδ�����κλ���
    public static final int SHOW_LEFT_MENU = 1;//����״̬��һ�֣���ʾ���ڻ������˵�
    public static final int SHOW_RIGHT_MENU = 2;//����״̬��һ�֣���ʾ���ڻ����Ҳ�˵�
    public static final int HIDE_LEFT_MENU = 3;//����״̬��һ�֣���ʾ�����������˵�
    public static final int HIDE_RIGHT_MENU = 4;//����״̬��һ�֣���ʾ���������Ҳ�˵�
    private int slideState;//��¼��ǰ�Ļ���״̬
    private int touchSlop;//�ڱ��ж�Ϊ����֮ǰ�û���ָ�����ƶ������ֵ
    private float xDown;//��¼��ָ����ʱ�������ꡣ
    private float yDown;//��¼��ָ����ʱ�ĺ�����
    private float xMove;//��¼��ָ�ƶ�ʱ�ĺ�����
    private float yMove;//��¼��ָ�ƶ�ʱ��������
    private float xUp;//��¼�ֻ�̧��ʱ�ĺ�����
    private boolean isLeftMenuVisible;//���˵���ǰ����ʾ�������ء�ֻ����ȫ��ʾ������ʱ�Ż���Ĵ�ֵ�����������д�ֵ��Ч
    private boolean isRightMenuVisible;//�Ҳ�˵���ǰ����ʾ�������ء�ֻ����ȫ��ʾ������ʱ�Ż���Ĵ�ֵ�����������д�ֵ��Ч
    private boolean isSliding;//�Ƿ����ڻ���

    private View leftLayout;//��಼��
    private View rightLayout;//�Ҳ಼��
    private View conterLayout;//�м䲼��
    private MarginLayoutParams leftParams;
    private MarginLayoutParams rightParams;
    private RelativeLayout.LayoutParams contentLayoutParams;
    private VelocityTracker mVelocityTracker;//���ڼ�����ָ�������ٶ�
    private View mBindView;//���ڼ��������¼���View
    private int bothSides = 200;//���߲��ֵĿ��
    private int screenWidth;//��Ļ���
    private double bothSidesPercent = 2/3;//���߲��ֿ��ռ��Ļ��ȱ���
    private Context context;
    private boolean showLeft = false;//�ж��Ƿ���ʾ��߲���
    private boolean showRight = false;//�ж��Ƿ���ʾ�ұ߲���

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
     * �󶨼��������¼���View��
     * @param bindView
     */
    public void setScrollEvent(View bindView){
        mBindView = bindView;
        mBindView.setOnTouchListener(this);
    }

    /**
     * ��������������˵����棬�����ٶ��趨Ϊ-30.
     */
    public void scrollToLeftMenu(){
        new LeftMenuScrollTask().equals(-30);
    }

    /**
     * ������������Ҳ�˵����棬�����ٶ��趨Ϊ-30.
     */
    public void scrollToRightMenu(){
        new RightMenuScrollTask().equals(-30);
    }

    /**
     * ����������˵����������ݽ��棬�����ٶ��趨Ϊ30.
     */
    public void scrollToConterFromLeftMenu(){
        new LeftMenuScrollTask().equals(30);
    }

    /**
     * ��������Ҳ�˵����������ݽ��棬�����ٶ��趨Ϊ30.
     */
    public void scrollToContenrFromRightMenu(){
        new RightMenuScrollTask().equals(30);
    }

    /**
     * ���˵��Ƿ���ȫ��ʾ���������������д�ֵ��Ч��
     * @return ���˵���ȫ��ʾ����true�����򷵻�false��
     */
    public boolean isLeftLayoutVisible(){
        return isLeftMenuVisible;
    }

    /**
     * �Ҳ�˵��Ƿ���ȫ��ʾ���������������д�ֵ��Ч��
     * @return �Ҳ�˵���ȫ��ʾ����true�����򷵻�false��
     */
    public boolean isRightLayoutVisible(){
        return isRightMenuVisible;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if(changed){
            //��ȡ��಼��
            leftLayout =  getChildAt(0);
            leftParams = (MarginLayoutParams) leftLayout.getLayoutParams();
            //��ȡ�Ҳ಼��
            rightLayout =  getChildAt(1);
            rightParams = (MarginLayoutParams) rightLayout.getLayoutParams();
            //��ȡ�м䲼��
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
        creatVelocityTracker(event);//����VelocityTracker���󣬲��������¼����뵽VelocityTracker����
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                xDown = event.getRawX();
                yDown = event.getRawY();
                //������״̬��ʼ��ΪDO_NOTHING
                slideState = DO_NOTHING;
                break;
            case MotionEvent.ACTION_MOVE:
                xMove = event.getRawX();
                yMove = event.getRawY();
                //��ָ�ƶ�ʱ���԰���ʱ�����꣬�����ƶ�����
                int moveDistanceX = (int) (xMove - xDown);
                int moveDistanceY = (int) (yMove - yDown);
                //��鵱ǰ�Ļ���״̬
                checkSlideState(moveDistanceX,moveDistanceY);
                switch (slideState){
                    case SHOW_LEFT_MENU:
                        //��ʾ���˵�
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
                    //��ָ̧��ʱ������  �жϵ�ǰ���Ƶ���ͼ
                    switch (slideState){
                        case SHOW_LEFT_MENU:
                            //��ʾ���˵�
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
                    // �����˵���ʾʱ������û����һ�����ݲ��֣���ֱ�ӹ��������ݽ���
                    scrollToConterFromLeftMenu();
                }else if(upDistanceX < touchSlop && isRightMenuVisible){
                    // ���Ҳ�˵���ʾʱ������û����һ�����ݲ��֣���ֱ�ӹ��������ݽ���
                    scrollToContenrFromRightMenu();
                }
                recycleVelocityTracker();
                break;
        }
        if(v.isEnabled()){
            if(isSliding){
                //���ڻ���ʱ���ÿؼ��ò�������
                unFocusBindView();
                return true;
            }
            if(isLeftMenuVisible || isRightMenuVisible){
                //�������Ҳ಼����ʾʱ�����󶨿ؼ����¼����ε�
                return true;
            }
            return false;
        }
        return true;
    }

    /**
     * ʹ�ÿ��Ի�ý���Ŀؼ��ڻ�����ʱ��ʧȥ���㡣
     */
    private void unFocusBindView(){
        if(mBindView != null){
            mBindView.setPressed(false);
            mBindView.setFocusable(false);
            mBindView.setFocusableInTouchMode(false);
        }
    }

    /**
     * ����VelocityTracker����
     */
    private void recycleVelocityTracker(){
        mVelocityTracker.recycle();
        mVelocityTracker = null;
    }

    /**
     * �ж��Ƿ�Ӧ�ô��Ҳ�˵����������ݲ��֣������ָ�ƶ���������Ҳ�˵���ȵ�1/2��������ָ�ƶ��ٶȴ���SNAP_VELOCITY��
     *  ����ΪӦ�ô��Ҳ�˵����������ݲ��֡�
     * @return ���Ӧ�ô��Ҳ�˵����������ݲ��ַ���true�����򷵻�false��
     */
    private boolean shouldScrollToContentFromRightMenu(){
        return xUp - xDown > rightParams.width / 2 || getScrollVelocity() > SNAP_VELOCITY;
    }

    /**
     * �ж��Ƿ�Ӧ�ù������Ҳ�˵�չʾ�����������ָ�ƶ���������Ҳ�˵���ȵ�1/2��������ָ�ƶ��ٶȴ���SNAP_VELOCITY��
     * ����ΪӦ�ù������Ҳ�˵�չʾ������
     *
     * @return ���Ӧ�ý��Ҳ�˵�չʾ��������true�����򷵻�false��
     */
    private boolean shouldScrollToRightMenu(){
        return xDown - xUp > rightParams.width / 2 || getScrollVelocity() > SNAP_VELOCITY;
    }

    /**
     * �ж��Ƿ�Ӧ�ô����˵����������ݲ��֣������ָ�ƶ�����������˵���ȵ�1/2��������ָ�ƶ��ٶȴ���SNAP_VELOCITY��
     * ����ΪӦ�ô����˵����������ݲ��֡�
     * @return ���Ӧ�ô����˵����������ݲ��ַ���true�����򷵻�false��
     */
    private boolean shouldScrollToContentFromLeftMenu(){
        return xDown - xUp > leftParams.width / 2 || getScrollVelocity() > SNAP_VELOCITY;
    }

    /**
     * �ж��Ƿ�Ӧ�ù��������˵�չʾ�����������ָ�ƶ�����������˵���ȵ�1/2��������ָ�ƶ��ٶȴ���SNAP_VELOCITY��
     * ����ΪӦ�ù��������˵�չʾ������
     * @return ���Ӧ�ý����˵�չʾ��������true�����򷵻�false��
     */
    private boolean shouldScrollToLeftMenu(){
        return xUp - xDown > leftParams.width / 2 || getScrollVelocity() > SNAP_VELOCITY;
    }

    /**
     * ��ȡ��ָ�ڰ󶨲����ϵĻ����ٶȡ�
     * @return �����ٶȣ���ÿ�����ƶ��˶�������ֵΪ��λ
     */
    private int getScrollVelocity(){
        mVelocityTracker.computeCurrentVelocity(1000);
        int velocity = (int) mVelocityTracker.getXVelocity();
        return Math.abs(velocity);
    }

    /**
     * �ڻ��������м���Ҳ�˵��ı߽�ֵ����ֹ�󶨲��ֻ�����Ļ��
     */
    private void checkRightMenuBorder(){
        if(contentLayoutParams.leftMargin > 0){
            contentLayoutParams.leftMargin = 0;
        }else if(contentLayoutParams.leftMargin < - rightParams.width){
            contentLayoutParams.leftMargin = - rightParams.width;
        }
    }

    /**
     * �ڻ��������м�����˵��ı߽�ֵ����ֹ�󶨲��ֻ�����Ļ��
     */
    private void checkLeftMenuBorder(){
        if(contentLayoutParams.rightMargin > 0){
            contentLayoutParams.rightMargin = 0;
        }else if(contentLayoutParams.rightMargin < -leftParams.width){
            contentLayoutParams.rightMargin = -leftParams.width;
        }
    }

    /**
     * ������ָ�ƶ��ľ��룬�жϵ�ǰ�û��Ļ�����ͼ��Ȼ���slideState��ֵ����Ӧ�Ļ���״ֵ̬��
     * @param moveDistanceX �����ƶ��ľ���
     * @param moveDistanceY �����ƶ��ľ���
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
                // ����û���Ҫ�������˵��������˵���ʾ���Ҳ�˵�����
                leftLayout.setVisibility(VISIBLE);
                rightLayout.setVisibility(GONE);
            }else if(!isSliding && Math.abs(moveDistanceX) >= touchSlop && moveDistanceX < 0
                    && Math.abs(moveDistanceY) < touchSlop){
                isSliding = true;
                slideState = SHOW_RIGHT_MENU;
                contentLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT,0);
                contentLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                conterLayout.setLayoutParams(contentLayoutParams);
                // ����û���Ҫ�����Ҳ�˵������Ҳ�˵���ʾ�����˵�����
                leftLayout.setVisibility(GONE);
                rightLayout.setVisibility(VISIBLE);
            }
        }
    }

    /**
     * ����VelocityTracker���󣬲��������¼����뵽VelocityTracker����
     * @param event �Ҳ಼�ּ����ؼ��Ļ����¼�
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
            //���ݴ�����ٶ����������棬�����������ֵʱ������ѭ��
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
                // Ϊ��Ҫ�й���Ч��������ÿ��ѭ��ʹ�߳�˯��һ��ʱ�䣬�������۲��ܹ���������������
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
            //���ݴ�����ٶ����������棬�����������ֵʱ������ѭ��
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
                // Ϊ��Ҫ�й���Ч��������ÿ��ѭ��ʹ�߳�˯��һ��ʱ�䣬�������۲��ܹ���������������
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
