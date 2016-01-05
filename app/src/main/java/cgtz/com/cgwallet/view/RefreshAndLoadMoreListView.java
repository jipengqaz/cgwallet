package cgtz.com.cgwallet.view;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import cgtz.com.cgwallet.R;


public class RefreshAndLoadMoreListView extends ListView implements AbsListView.OnScrollListener {

    private static final String TAG = "listview";

    private static final String DATE_FORMAT = "MM-dd HH:mm";

    private final static int RELEASE_To_REFRESH = 0;
    private final static int PULL_To_REFRESH = 1;
    private final static int REFRESHING = 2;
    private final static int DONE = 3;
    private final static int LOADING = 4;

    // 实际的padding的距离与界面上偏移距离的比例
    private final static int RATIO = 3;

    private LayoutInflater inflater;

    private LinearLayout headView;

    private LinearLayout firstHeadView;

    private TextView tipsTextview;
    private TextView lastUpdatedTextView;
    private ImageView arrowImageView;
    private ProgressBar progressBar;

    private RotateAnimation animation;
    private RotateAnimation reverseAnimation;

    // 用于保证startY的值在一个完整的touch事件中只被记录一次
    private boolean isRecored;

    private int headContentWidth;
    private int headContentHeight;

    private int startY;
    private int firstItemIndex;

    private int state;

    private boolean isBack;

    private OnRefreshListener refreshListener;

    private boolean isRefreshable;

    private int lastItem;

    private boolean isRefresh;

    private boolean noMore;

    public RefreshAndLoadMoreListView(Context context) {
        super(context);
        init(context);
    }

    public RefreshAndLoadMoreListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public RefreshAndLoadMoreListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        //setCacheColorHint(context.getResources().getColor(R.color.transparent));
        inflater = LayoutInflater.from(context);

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1)
            setFadingEdgeLength(0);

        firstHeadView = new LinearLayout(context);
        android.widget.AbsListView.LayoutParams layoutParams = new android.widget.AbsListView.LayoutParams(LayoutParams.MATCH_PARENT, android.widget.AbsListView.LayoutParams.WRAP_CONTENT);
        firstHeadView.setLayoutParams(layoutParams);
        addHeaderView(firstHeadView);

        headView = (LinearLayout) inflater.inflate(R.layout.list_refresh_head, null);

        arrowImageView = (ImageView) headView
                .findViewById(R.id.head_arrowImageView);
        arrowImageView.setMinimumWidth(70);
        arrowImageView.setMinimumHeight(50);
        progressBar = (ProgressBar) headView
                .findViewById(R.id.head_progressBar);
        tipsTextview = (TextView) headView.findViewById(R.id.head_tipsTextView);
        lastUpdatedTextView = (TextView) headView
                .findViewById(R.id.head_lastUpdatedTextView);

        measureView(headView);
        headContentHeight = headView.getMeasuredHeight();
        headContentWidth = headView.getMeasuredWidth();

        headView.setPadding(0, -1 * headContentHeight, 0, 0);
        headView.invalidate();

        Log.v("size", "width:" + headContentWidth + " height:"
                + headContentHeight);

        addHeaderView(headView, null, false);
        setOnScrollListener(this);

        animation = new RotateAnimation(0, -180,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        animation.setInterpolator(new LinearInterpolator());
        animation.setDuration(250);
        animation.setFillAfter(true);

        reverseAnimation = new RotateAnimation(-180, 0,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        reverseAnimation.setInterpolator(new LinearInterpolator());
        reverseAnimation.setDuration(200);
        reverseAnimation.setFillAfter(true);

        state = DONE;
        isRefreshable = false;
        setFooterDividersEnabled(false);
        empty(true);
        empty(false);
    }

    public void onScroll(AbsListView listView, int firstVisiableItem, int visibleItemCount,
                         int totalItemCount) {
        firstItemIndex = firstVisiableItem;

        lastItem = firstVisiableItem + visibleItemCount;
    }

    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (!noMore && !isRefresh && lastItem == getAdapter().getCount() && scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
            loadMore();
        }
        if (isRefresh && scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
            isRefresh = false;
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            startY = (int) event.getY();
        }
        return super.onInterceptTouchEvent(event);
    }

    public boolean onTouchEvent(MotionEvent event) {

        if (isRefreshable) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (firstItemIndex == 0 && !isRecored) {
                        isRecored = true;
                        startY = (int) event.getY();
                        Log.v(TAG, "在down时候记录当前位置‘");
                    }
                    break;

                case MotionEvent.ACTION_UP:

                    if (state != REFRESHING && state != LOADING) {
                        if (state == DONE) {
                            // 什么都不做
                        }
                        if (state == PULL_To_REFRESH) {
                            state = DONE;
                            changeHeaderViewByState();

                            onHitTop();

                            Log.v(TAG, "由下拉刷新状态，到done状态");
                        }
                        if (state == RELEASE_To_REFRESH) {
                            state = REFRESHING;
                            changeHeaderViewByState();
                            onRefresh();
                            Log.v(TAG, "由释放刷新状态，到done状态");
                        }
                    }

                    isRecored = false;
                    isBack = false;

                    break;

                case MotionEvent.ACTION_MOVE:
                    int tempY = (int) event.getY();

                    if (!isRecored && firstItemIndex == 0) {
                        Log.v(TAG, "在move时候记录下位置");
                        isRecored = true;
                        startY = tempY;
                    }

                    if (state != REFRESHING && isRecored && state != LOADING) {

                        // 保证在设置padding的过程中，当前的位置一直是在head，否则如果当列表超出屏幕的话，当在上推的时候，列表会同时进行滚动

                        // 可以松手去刷新了
                        if (state == RELEASE_To_REFRESH) {

                            setSelection(0);

                            // 往上推了，推到了屏幕足够掩盖head的程度，但是还没有推到全部掩盖的地步
                            if (((tempY - startY) / RATIO < headContentHeight)
                                    && (tempY - startY) > 0) {
                                state = PULL_To_REFRESH;
                                changeHeaderViewByState();

                                Log.v(TAG, "由释放刷新状态转变到下拉刷新状态");
                            }
                            // 一下子推到顶了
                            else if (tempY - startY <= 0) {
                                state = DONE;
                                changeHeaderViewByState();

                                Log.v(TAG, "由释放刷新状态转变到done状态");
                            }
                            // 往下拉了，或者还没有上推到屏幕顶部掩盖head的地步
                            else {
                                // 不用进行特别的操作，只用更新paddingTop的值就行了
                            }
                        }
                        // 还没有到达显示释放刷新的时候,DONE或者是PULL_To_REFRESH状态
                        if (state == PULL_To_REFRESH) {

                            setSelection(0);

                            // 下拉到可以进入RELEASE_TO_REFRESH的状态
                            if ((tempY - startY) / RATIO >= headContentHeight) {
                                state = RELEASE_To_REFRESH;
                                isBack = true;
                                changeHeaderViewByState();

                                Log.v(TAG, "由done或者下拉刷新状态转变到释放刷新");
                            }
                            // 上推到顶了
                            else if (tempY - startY <= 0) {
                                state = DONE;
                                changeHeaderViewByState();

                                onHitTop();

                                Log.v(TAG, "由DOne或者下拉刷新状态转变到done状态");
                            }
                        }

                        // done状态下
                        if (state == DONE) {
                            if (tempY - startY > 0) {
                                state = PULL_To_REFRESH;
                                changeHeaderViewByState();

                                onPullDownFromTop();
                            }
                        }

                        // 更新headView的size
                        if (state == PULL_To_REFRESH) {
                            headView.setPadding(0, -1 * headContentHeight
                                    + (tempY - startY) / RATIO, 0, 0);

                        }

                        // 更新headView的paddingTop
                        if (state == RELEASE_To_REFRESH) {
                            headView.setPadding(0, (tempY - startY) / RATIO
                                    - headContentHeight, 0, 0);
                        }

                    }

                    break;
            }
        }

        return super.onTouchEvent(event);
    }

    /**
     * 从最高点往下拉的瞬间
     * */
    public void onPullDownFromTop() {}

    /**
     * 下拉回推或者下拉松手 ListView 到达普通状态最顶点(即不包括下拉时 Header 的高度的顶点)
     * */
    public void onHitTop() {}

    // 当状态改变时候，调用该方法，以更新界面
    private void changeHeaderViewByState() {
        switch (state) {
            case RELEASE_To_REFRESH:
                arrowImageView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                tipsTextview.setVisibility(View.VISIBLE);
                lastUpdatedTextView.setVisibility(View.VISIBLE);

                arrowImageView.clearAnimation();
                arrowImageView.startAnimation(animation);

                tipsTextview.setText("释放刷新");

                Log.v(TAG, "当前状态，释放刷新");
                break;
            case PULL_To_REFRESH:
                isRefresh = true;
                progressBar.setVisibility(View.GONE);
                tipsTextview.setVisibility(View.VISIBLE);
                lastUpdatedTextView.setVisibility(View.VISIBLE);
                arrowImageView.clearAnimation();
                arrowImageView.setVisibility(View.VISIBLE);
                // 是由RELEASE_To_REFRESH状态转变来的
                if (isBack) {
                    isBack = false;
                    arrowImageView.clearAnimation();
                    arrowImageView.startAnimation(reverseAnimation);

                    tipsTextview.setText("下拉刷新");
                } else {
                    tipsTextview.setText("下拉刷新");
                }
                Log.v(TAG, "当前状态，下拉刷新");
                break;

            case REFRESHING:

                headView.setPadding(0, 0, 0, 0);

                progressBar.setVisibility(View.VISIBLE);
                arrowImageView.clearAnimation();
                arrowImageView.setVisibility(View.GONE);
                tipsTextview.setText("正在刷新...");
                lastUpdatedTextView.setVisibility(View.VISIBLE);

                Log.v(TAG, "当前状态,正在刷新...");
                break;
            case DONE:
                headView.setPadding(0, -1 * headContentHeight, 0, 0);

                progressBar.setVisibility(View.GONE);
                arrowImageView.clearAnimation();
//                arrowImageView.setImageResource(R.drawable.arrow_down);
                tipsTextview.setText("下拉刷新");
                lastUpdatedTextView.setVisibility(View.VISIBLE);

                Log.v(TAG, "当前状态，done");
                break;
        }
    }

    public void resetHeader() {
        state = DONE;
        headView.setPadding(0, -1 * headContentHeight, 0, 0);
    }

    public void setOnRefreshListener(OnRefreshListener refreshListener) {
        this.refreshListener = refreshListener;
        isRefreshable = true;
    }

    public interface OnRefreshListener {
        public void onRefresh();
    }

    public void onRefreshComplete() {
        state = DONE;
        SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT);
        String date = format.format(new Date());
        lastUpdatedTextView.setText("最近更新: " + date);
        changeHeaderViewByState();
        if (mLoadMoreView != null)
            mLoadMoreView.setVisibility(VISIBLE);
    }

    private void onRefresh() {
        setNoMore(false);
        if (mLoadMoreView != null)
            mLoadMoreView.setVisibility(GONE);
        if (refreshListener != null) {
            refreshListener.onRefresh();
        }
    }

    public void refresh() {
        if (isLoadingMore)
            return;
        setNoMore(false);
        if (refreshListener != null) {
            refreshListener.onRefresh();
        }
        if (mLoadMoreView != null)
            mLoadMoreView.setVisibility(GONE);
    }

    // 此方法直接照搬自网络上的一个下拉刷新的demo，此处是“估计”headView的width以及height
    private void measureView(View child) {
        ViewGroup.LayoutParams p = child.getLayoutParams();
        if (p == null) {
            p = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.FILL_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        int childWidthSpec = ViewGroup.getChildMeasureSpec(0,
                0 + 0, p.width);
        int lpHeight = p.height;
        int childHeightSpec;
        if (lpHeight > 0) {
            childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight, MeasureSpec.EXACTLY);
        } else {
            childHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        }
        child.measure(childWidthSpec, childHeightSpec);
    }

    public void setAdapter(BaseAdapter adapter) {
        SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT);
        String date = format.format(new Date());
        lastUpdatedTextView.setText("最近更新: " + date);
        super.setAdapter(adapter);
    }

    public boolean isNoMore() {
        return noMore;
    }

    public void setNoMore(boolean noMore) {
        this.noMore = noMore;
    }

    private View mLoadMoreView;
    private OnLoadMoreListener mOnLoadMoreLister;
    private Boolean isLoadingMore = false;

    public interface OnLoadMoreListener {
        public void onLoadMore();
    }

    public void setOnLoadMoreLister(OnLoadMoreListener listener) {
        mOnLoadMoreLister = listener;
    }

    public View getLoadMoreFootView() {
        if (mLoadMoreView == null) {
            mLoadMoreView = inflater.inflate(R.layout.list_loadmore_foot, null);
            mLoadMoreView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!isNoMore())
                        loadMore();
                }
            });
        }
        return mLoadMoreView;
    }
//2015年12月28日16:02:09  测试
    public View newGetLoadMoreFootView() {
        if (mLoadMoreView == null) {
            mLoadMoreView = inflater.inflate(R.layout.list_loadmore_foot, null);
            mLoadMoreView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!isNoMore())
                        newLoadMore();
                }
            });
        }
        return mLoadMoreView;
    }

    public void setLoadMore(boolean more) {
        if (more) {
            if (this.getFooterViewsCount() == 0) {
                this.addFooterView(getLoadMoreFootView());
            }else{
                if (mLoadMoreView != null) {
                    mLoadMoreView.setVisibility(VISIBLE);
                }
            }
        } else {
            if (mLoadMoreView != null)
                this.removeFooterView(mLoadMoreView);
        }
    }
//2015年12月28日16:01:09  测试
public void newSetLoadMore(boolean more) {
    if (more) {
        if (this.getFooterViewsCount() == 0) {
            this.addFooterView(newGetLoadMoreFootView());
        }else{
            if (mLoadMoreView != null) {
                mLoadMoreView.setVisibility(VISIBLE);
            }
        }
    } else {
        if (mLoadMoreView != null)
            this.removeFooterView(mLoadMoreView);
    }
}


    public void onLoadMoreComplete() {
        isLoadingMore = false;
        if (mLoadMoreView != null) {
            TextView tipsView = (TextView) mLoadMoreView.findViewById(R.id.foot_tipsTextView);
            tipsView.setText(R.string.click_to_see_more);
            ProgressBar progressView = (ProgressBar) mLoadMoreView.findViewById(R.id.foot_progressBar);
            progressView.setVisibility(GONE);
        }
    }

    public void onLoadMoreError() {
        isLoadingMore = false;
        if (mLoadMoreView != null) {
            TextView tipsView = (TextView) mLoadMoreView.findViewById(R.id.foot_tipsTextView);
            tipsView.setText(R.string.load_error);
            ProgressBar progressView = (ProgressBar) mLoadMoreView.findViewById(R.id.foot_progressBar);
            progressView.setVisibility(GONE);
        }
    }

    public void noItemTip(String msg) {
        if (mLoadMoreView != null) {
            ImageView tipsView1 = (ImageView) mLoadMoreView.findViewById(R.id.no_transfer_project);//用于显示图片
            tipsView1.setVisibility(GONE);
            TextView tipsView = (TextView) mLoadMoreView.findViewById(R.id.foot_tipsTextView);
            tipsView.setText(msg);
            tipsView.setVisibility(VISIBLE);
            tipsView.setTextColor(getResources().getColor(R.color.tab_line));
            tipsView.setTextSize(14);
            ProgressBar progressView = (ProgressBar) mLoadMoreView.findViewById(R.id.foot_progressBar);
            progressView.setVisibility(GONE);
        }
    }

    /**
     * 设置没有项目是的显示图片
     */
    public void noItemTip(){
        if (mLoadMoreView != null) {
            ImageView tipsView1 = (ImageView) mLoadMoreView.findViewById(R.id.no_transfer_project);//用于显示图片
            tipsView1.setVisibility(VISIBLE);
            TextView tipsView = (TextView) mLoadMoreView.findViewById(R.id.foot_tipsTextView);
            tipsView.setVisibility(GONE);
            ProgressBar progressView = (ProgressBar) mLoadMoreView.findViewById(R.id.foot_progressBar);
            progressView.setVisibility(GONE);
        }
    }

    /**
     *
     * @param nomore  是否显示下标
     * @param notips  用于显示线条  false不显示
     */

    public void noMore(boolean nomore,boolean notips) {
        isLoadingMore = false;

        noMore = nomore;

        if (mLoadMoreView != null) {
            if (getDividerHeight() != 0 && notips)
                mLoadMoreView.findViewById(R.id.end_line).setVisibility(VISIBLE);
            else {
                mLoadMoreView.findViewById(R.id.end_line).setVisibility(GONE);
            }
            TextView tipsView = (TextView) mLoadMoreView.findViewById(R.id.foot_tipsTextView);
            if (nomore) {
                tipsView.setText(R.string.no_more);
                mLoadMoreView.findViewById(R.id.line_left).setVisibility(VISIBLE);
                mLoadMoreView.findViewById(R.id.line_right).setVisibility(VISIBLE);
                mLoadMoreView.findViewById(R.id.no_transfer_project).setVisibility(GONE);//隐藏图片
            } else {
                tipsView.setText(R.string.click_to_see_more);
                mLoadMoreView.findViewById(R.id.line_left).setVisibility(GONE);
                mLoadMoreView.findViewById(R.id.line_right).setVisibility(GONE);
            }
            ProgressBar progressView = (ProgressBar) mLoadMoreView.findViewById(R.id.foot_progressBar);
            progressView.setVisibility(GONE);
            mLoadMoreView.findViewById(R.id.tv_go_submit).setVisibility(GONE);
        }
    }
    public void noMore(boolean nomore) {
        isLoadingMore = false;

        noMore = nomore;

        if (mLoadMoreView != null) {
            if (getDividerHeight() != 0)
                mLoadMoreView.findViewById(R.id.end_line).setVisibility(VISIBLE);
            TextView tipsView = (TextView) mLoadMoreView.findViewById(R.id.foot_tipsTextView);
            if (nomore) {
                tipsView.setText(R.string.no_more);
                mLoadMoreView.findViewById(R.id.line_left).setVisibility(VISIBLE);
                mLoadMoreView.findViewById(R.id.line_right).setVisibility(VISIBLE);
                mLoadMoreView.findViewById(R.id.no_transfer_project).setVisibility(GONE);//隐藏图片
            } else {
                tipsView.setText(R.string.click_to_see_more);
                mLoadMoreView.findViewById(R.id.line_left).setVisibility(GONE);
                mLoadMoreView.findViewById(R.id.line_right).setVisibility(GONE);
            }
            ProgressBar progressView = (ProgressBar) mLoadMoreView.findViewById(R.id.foot_progressBar);
            progressView.setVisibility(GONE);
            mLoadMoreView.findViewById(R.id.tv_go_submit).setVisibility(GONE);
        }
    }

//    2015年12月28日13:50:54  测试
public void newNoMore(boolean nomore) {
    isLoadingMore = false;
    noMore = nomore;
    if (mLoadMoreView != null) {
        if (getDividerHeight() != 0)
            mLoadMoreView.findViewById(R.id.end_line).setVisibility(VISIBLE);
        TextView tipsView = (TextView) mLoadMoreView.findViewById(R.id.foot_tipsTextView);
        if (nomore) {
            tipsView.setText(R.string.new_no_more);
            mLoadMoreView.findViewById(R.id.line_left).setVisibility(VISIBLE);
            mLoadMoreView.findViewById(R.id.line_right).setVisibility(VISIBLE);
            mLoadMoreView.findViewById(R.id.no_transfer_project).setVisibility(GONE);//隐藏图片
        } else {
            tipsView.setText(R.string.click_to_see_more);
            mLoadMoreView.findViewById(R.id.line_left).setVisibility(GONE);
            mLoadMoreView.findViewById(R.id.line_right).setVisibility(GONE);
        }
        ProgressBar progressView = (ProgressBar) mLoadMoreView.findViewById(R.id.foot_progressBar);
        progressView.setVisibility(GONE);
        mLoadMoreView.findViewById(R.id.tv_go_submit).setVisibility(GONE);
    }
}

    public void noMore(boolean noMore, int endText, boolean showGoSubmit) {
        String endTextStr = getResources().getString(endText);
        noMore(noMore, endTextStr, showGoSubmit);
    }

    public void noMore(boolean noMore, String endText, boolean showGoSubmit) {
        noMore(noMore);
        if (mLoadMoreView != null) {
            TextView tipsView = (TextView) mLoadMoreView.findViewById(R.id.foot_tipsTextView);
            if (noMore && endText != null) {
                tipsView.setText(endText);
                mLoadMoreView.findViewById(R.id.line_left).setVisibility(GONE);
                mLoadMoreView.findViewById(R.id.line_right).setVisibility(GONE);
            }
            if (showGoSubmit) {
                mLoadMoreView.findViewById(R.id.tv_go_submit).setVisibility(VISIBLE);
                mLoadMoreView.findViewById(R.id.tv_go_submit).setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (getContext() == null) return;
//                        Intent intent = new Intent(getContext(), SourceActivity.class);
//                        getContext().startActivity(intent);
                    }
                });
            } else {
                mLoadMoreView.findViewById(R.id.tv_go_submit).setVisibility(GONE);
            }
        }
    }

    public void loadMore() {
        if (isLoadingMore)
            return;
        isLoadingMore = true;
        if (mLoadMoreView != null) {
            TextView tipsView = (TextView) mLoadMoreView.findViewById(R.id.foot_tipsTextView);
            tipsView.setVisibility(VISIBLE);
            tipsView.setText("正在加载...");
            ProgressBar progressView = (ProgressBar) mLoadMoreView.findViewById(R.id.foot_progressBar);
            progressView.setVisibility(VISIBLE);
            mLoadMoreView.findViewById(R.id.line_left).setVisibility(GONE);
            mLoadMoreView.findViewById(R.id.line_right).setVisibility(GONE);
        }
        if (emptyView != null) {
            emptyView.setVisibility(GONE);
        }
        if (mOnLoadMoreLister != null) {
            mOnLoadMoreLister.onLoadMore();
        }
    }
//2015年12月28日14:47:02  测试
    public void newLoadMore() {
        if (isLoadingMore)
            return;
        isLoadingMore = true;
        if (mLoadMoreView != null) {
            TextView tipsView = (TextView) mLoadMoreView.findViewById(R.id.foot_tipsTextView);
            tipsView.setVisibility(VISIBLE);
            tipsView.setText("");
//            ProgressBar progressView = (ProgressBar) mLoadMoreView.findViewById(R.id.foot_progressBar);
//            progressView.setVisibility(VISIBLE);
            mLoadMoreView.findViewById(R.id.line_left).setVisibility(GONE);
            mLoadMoreView.findViewById(R.id.line_right).setVisibility(GONE);
        }
        if (emptyView != null) {
            emptyView.setVisibility(GONE);
        }
        if (mOnLoadMoreLister != null) {
            mOnLoadMoreLister.onLoadMore();
        }
    }

    protected View emptyView;
    protected TextView emptyTipView, emptyGoSubmit;
    protected ImageView emptyIconView;

    public void empty(boolean empty, String msg, int emptyICon) {
        empty(empty);
        if (empty)
            emptyTipView.setText(msg);
        if (emptyICon != 0) {
            emptyIconView.setVisibility(VISIBLE);
            emptyIconView.setImageResource(emptyICon);
        }
    }


    public void empty(boolean empty, String msg) {
        empty(empty);
        if (empty)
            emptyTipView.setText(msg);
    }

    public void empty(boolean empty, int msgID) {
        empty(empty);
        if (empty)
            emptyTipView.setText(msgID);
    }

    public void empty(boolean empty, int msgID, int emptyICon) {
        empty(empty);
        if (empty)
            emptyTipView.setText(msgID);
        if (emptyICon != 0) {
            emptyIconView.setVisibility(VISIBLE);
            emptyIconView.setImageResource(emptyICon);
        }
    }

    public void empty(boolean empty, int msgID, int emptyICon, boolean showGoSubmit) {
        empty(empty, msgID, emptyICon);
        emptyGoSubmit.setVisibility(showGoSubmit ? View.VISIBLE : View.GONE);
    }

    View emptyViewFrame;
    public void empty(boolean empty) {
        if (empty) {
            if (emptyView == null) {
                emptyViewFrame = View.inflate(getContext(), R.layout.list_empty_head, null);
                emptyView = emptyViewFrame.findViewById(R.id.search_empty_view);
                emptyTipView = (TextView) emptyView.findViewById(R.id.empty_view);
                emptyGoSubmit = (TextView) emptyView.findViewById(R.id.tv_go_submit);
                emptyIconView = (ImageView) emptyView.findViewById(R.id.empty_icon);

                emptyGoSubmit.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (getContext() == null) return;
//                        Intent intent = new Intent(getContext(), SourceActivity.class);
//                        getContext().startActivity(intent);
                    }
                });

                addHeaderView(emptyViewFrame, null, false);
            }
            emptyView.setVisibility(VISIBLE);
            emptyTipView.setVisibility(VISIBLE);
            if (mLoadMoreView != null) {
                mLoadMoreView.setVisibility(GONE);
            }
        } else {
            if (emptyView != null) {
                emptyView.setVisibility(GONE);
            }
            if (emptyTipView != null) {
                emptyTipView.setVisibility(GONE);
            }
            if (mLoadMoreView != null) {
                mLoadMoreView.setVisibility(VISIBLE);
            }
        }
    }

    public Boolean getIsLoadingMore() {
        return isLoadingMore;
    }

    public void setIsLoadingMore(Boolean isLoadingMore) {
        this.isLoadingMore = isLoadingMore;
    }

    public LinearLayout getFirstHeadView() {
        return firstHeadView;
    }
}
