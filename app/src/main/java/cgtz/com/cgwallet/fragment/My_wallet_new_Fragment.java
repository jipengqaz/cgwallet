package cgtz.com.cgwallet.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import cgtz.com.cgwallet.MApplication;
import cgtz.com.cgwallet.R;
import cgtz.com.cgwallet.activity.E_wallet_record_activity;
import cgtz.com.cgwallet.activity.Earnings_record;
import cgtz.com.cgwallet.activity.LoginActivity;
import cgtz.com.cgwallet.activity.MainActivity;
import cgtz.com.cgwallet.activity.SaveMoneyActivity;
import cgtz.com.cgwallet.activity.WaterqueryActivity;
import cgtz.com.cgwallet.activity.WebViewActivity;
import cgtz.com.cgwallet.adapter.BannerViewPagerAdapter;
import cgtz.com.cgwallet.bean.JsonBean;
import cgtz.com.cgwallet.client.Withdraw_money_Client;
import cgtz.com.cgwallet.domain.DetailInfo;
import cgtz.com.cgwallet.presenter.SplashPresenter;
import cgtz.com.cgwallet.utility.Constants;
import cgtz.com.cgwallet.utils.CustomTask;
import cgtz.com.cgwallet.utils.DensityUtil;
import cgtz.com.cgwallet.utils.Ke_Fu_data;
import cgtz.com.cgwallet.utils.LogUtils;
import cgtz.com.cgwallet.utils.Utils;
import cgtz.com.cgwallet.view.ISplashView;
import cgtz.com.cgwallet.view.NotSlideGridView;
import cgtz.com.cgwallet.view.PullToRefreshLayout;
import cgtz.com.cgwallet.view.bannerview.BannerLayout;
import cgtz.com.cgwallet.view.graphview.CustomLabelFormatter;
import cgtz.com.cgwallet.view.graphview.GraphView;
import cgtz.com.cgwallet.view.graphview.GraphViewSeries;
import cgtz.com.cgwallet.view.graphview.LineGraphView;
import cgtz.com.cgwallet.widget.ProgressDialog;

/**
 * 我的钱包
 * Created by Administrator on 2015/6/17.
 */
public class My_wallet_new_Fragment extends BaseFragment implements ISplashView, View.OnClickListener {
    private static final String TAG = "My_wallet_new_Fragment";
    private TextView walletEarnings;//今日收益
    private LinearLayout assetsLayout;//我的资产layout
    private TextView walletAssets;//我的资产
    //    private LinearLayout accumulativeLayout;//可用收益layout
    private TextView walletAccumulative;//可用收益
    private ProgressDialog progressDialog;
    private SplashPresenter presenter;
    private String earnings;//今日收益
    private String assets;//我的资产
    public String accumulative;//累计收益
    private String rate;//利率
    private int screenWidth;
    private boolean goLogin = true;//判断是否去登录的标志
    private LinearLayout today_earnings;//今日收益
    //    private SwipeRefreshLayout mSwipeLayout;//下拉刷新控件
    private LinearLayout walletRateLayout;//利率layout
    //2016年1月4日13:25:22 测试    private TextView walletRate;//钱包利率
    private String url = Constants.WALLET_INTRODUCE;//钱包简介路径
    private String link_name = "钱包简介";
    private View layoutView;
    private LinearLayout drawMoney;//转出
    private LinearLayout saveMoney;//转入
    private Activity mActivity;

    private LinearLayout linechart;
    private GraphView graphView;

    private TextView amount;//总金额
    private JSONArray rateArray;//七天浮动利率数组
    private String[] stringRateArray;//String数组类型的七日浮动利率

    private ArrayList<String> imgArrays = new ArrayList<>();//拿到所有订单号的集合
    private ArrayList<String> urlArrays = new ArrayList<>();//拿到所有状态的集合

/*    private long now = new Date().getTime();//获取当前时间的毫秒值
    private long t = 86400000;//一天的毫秒值*/

    /*private double oneRate=Double.parseDouble(stringRateArray[1]);
    private double twoRate=Double.parseDouble(stringRateArray[2]);
    private double threeRate=Double.parseDouble(stringRateArray[3]);
    private double fourRate=Double.parseDouble(stringRateArray[4]);
    private double fiveRate=Double.parseDouble(stringRateArray[5]);
    private double sixRate=Double.parseDouble(stringRateArray[6]);
    private double sevenRate=Double.parseDouble(stringRateArray[7]);
    //最近7天的数据，其中24d是今天的数据，double类型，服务器若是返回String，需要装箱成double
    private double[] data = {oneRate*10, twoRate*10, threeRate*10, fourRate*10, fiveRate*10, sixRate*10, sevenRate*10};*/

    //最近7天的数据，其中24d是今天的数据，double类型，服务器若是返回String，需要装箱成double   private double[] data = {6d, 7d, 10d, 7d, 8d, 7d,7d};
    //private double[] data = {6d, 6d, 7d, 7d, 7d, 6d,7d};

    /*String str1 = "6.00", str2 = "7.00", str3 = "6.00", str4 = "7.00",
            str5 = "7.65", str6 = "7.65", str7 = "7.65";*/

    /**
     * 下拉刷新组件
     */
    // TODO
//    public PullToRefreshLayout mPtrl;
    public PullToRefreshScrollView mPtrl;
    private boolean isOne = false;//用于判断是否才启动程序
    /*    private String str1;
        private String str2;
        private String str3;
        private String str4;
        private String str5;
        private String str6;
        private String str7;*/
    private BannerLayout bl_banner;
    private BannerViewPagerAdapter adapter;
    private String imageData;//图片的数据
    private ImageView iv_spread;
    //    private double[] data;//七日年化收益率装箱成double类型

//    private NotSlideGridView function;//所有的选项
//    private TextView wallet_tip;//钱包提示

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        screenWidth = getResources().getDisplayMetrics().widthPixels;
        presenter = new SplashPresenter(this);
        layoutView = inflater.inflate(R.layout.test, container, false);
//        layoutView = inflater.inflate(R.layout.layout_my_wallet_new,container,false);
        initViews(layoutView);
//        initBannerData();  轮播图 暂不使用

//        setData(true);
        setListener();
        return layoutView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;// 绑定到Activity
    }
//2015年12月23日23:56:17  测试


    public void clearFocus() {
        if (assetsLayout != null) {
            assetsLayout.setEnabled(false);
        }
        /*//累计收益layout
        if(accumulativeLayout != null){
            accumulativeLayout.setEnabled(false);
        }*/
        //今日收益layout
        if (today_earnings != null) {
            today_earnings.setEnabled(false);
        }
        //利率layout
        if (walletRateLayout != null) {
            walletRateLayout.setEnabled(false);
        }
        //下拉刷新
        if (mPtrl != null) {
            mPtrl.setEnabled(false);
        }
        //转入
        if (saveMoney != null) {
            saveMoney.setEnabled(false);
        }
        //转出
        if (drawMoney != null) {
            drawMoney.setEnabled(false);
        }
    }

    public void requetFocus() {
        //我的资产layout
        if (assetsLayout != null) {
            assetsLayout.setEnabled(true);
        }
        /*//累计收益layout
        if(accumulativeLayout != null){
            accumulativeLayout.setEnabled(true);
        }*/
        //今日收益layout
        if (today_earnings != null) {
            today_earnings.setEnabled(true);
        }
        //利率layout
        if (walletRateLayout != null) {
            walletRateLayout.setEnabled(true);
        }
        //下拉刷新
        if (mPtrl != null) {
            mPtrl.setEnabled(true);
        }
        //转入
        if (saveMoney != null) {
            saveMoney.setEnabled(true);
        }
        //转出
        if (drawMoney != null) {
            drawMoney.setEnabled(true);
        }
    }

    public void setData(boolean flag) {
        if (flag) {
//            presenter.didFinishLoading(getActivity());
            presenter.didFinishLoading(mActivity);
        }
    }

    /**
     * widget设置事件
     */
    private void setListener() {
        assetsLayout.setOnClickListener(this);//我的资产layout
//        accumulativeLayout.setOnClickListener(this);//累计收益layout
        today_earnings.setOnClickListener(this);//今日收益layout
        walletRateLayout.setOnClickListener(this);//利率layout
        drawMoney.setOnClickListener(this);//转出
        saveMoney.setOnClickListener(this);//转入

        iv_spread.setOnClickListener(this);//活动图片
        // TODO
//        mPtrl.setOnRefreshListener(new AutoPullListener());//下拉刷新

        mPtrl.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        mPtrl.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ScrollView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
                if (mPtrl.isHeaderShown()) {
                    // 下拉刷新操作
                    new Handler() {
                        @Override
                        public void handleMessage(Message msg) {
                            // 千万别忘了告诉控件刷新完毕了
                            My_wallet_new_Fragment.this.onUpdateData(mPtrl);
//                    mPtrl.refreshFinish(PullToRefreshLayout.SUCCEED);
                        }
                    }.sendEmptyMessageDelayed(0, 0);
                } else {
                    // 加载操作
                    new Handler() {
                        @Override
                        public void handleMessage(Message msg) {
                            // 千万别忘了告诉控件加载完毕了
//                    pullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                        }
                    }.sendEmptyMessageDelayed(0, 2000);
                }
            }
        });
    }

    /**
     * 初始化widget
     *
     * @param view
     */
    private void initViews(View view) {
        linechart = (LinearLayout) view.findViewById(R.id.line_chart);
//        accumulativeLayout = (LinearLayout) view.findViewById(R.id.wallet_accumulative_layout);//累计收益layout
        walletAccumulative = (TextView) view.findViewById(R.id.wallet_accumulative_earnings);//累计收益
        today_earnings = (LinearLayout) view.findViewById(R.id.today_earnings);//今日收益layout
        walletEarnings = (TextView) view.findViewById(R.id.wallet_earnings);//今日收益
        assetsLayout = (LinearLayout) view.findViewById(R.id.wallet_assets_layout);//我的资产layout
        walletAssets = (TextView) view.findViewById(R.id.wallet_my_assets);//我的资产
        walletRateLayout = (LinearLayout) view.findViewById(R.id.wallet_rate_layout);//利率layout
        //2016年1月4日13:24:23 测试 walletRate = (TextView) view.findViewById(R.id.wallet_rate);//钱包利率
        // TODO
        mPtrl = (PullToRefreshScrollView) view.findViewById(R.id.refresh_view);//刷新控件
        drawMoney = (LinearLayout) view.findViewById(R.id.wallet_draw_money);//转出
        saveMoney = (LinearLayout) view.findViewById(R.id.wallet_save_money);//转入

        amount = (TextView) view.findViewById(R.id.new_text);//总金额
        iv_spread = (ImageView) view.findViewById(R.id.iv_spread);//活动图片
        //bl_banner = (BannerLayout) view.findViewById(R.id.banner);//轮播图 暂未使用
//        wallet_tip = (TextView) view.findViewById(R.id.wallet_tip);//钱包提示
//        function = (NotSlideGridView) view.findViewById(R.id.function)  ;//列表选项

//        //生成动态数组，并且转入数据
//        ArrayList<HashMap<String, Object>> lstImageItem = new ArrayList<>();
//        int [] image = {R.mipmap.icon_zhuanru,R.mipmap.icon_zhuanchu,R.mipmap.icon_leijishouyi};
//        String[] name = {"转入记录","转出记录","累计收益"};
//        for(int i=0;i<image.length;i++)
//        {
//            HashMap<String, Object> map = new HashMap<String, Object>();
//            map.put("ItemImage",image[i]);//添加图像资源的ID
//            map.put("ItemText", name[i]);//添加文案
//            lstImageItem.add(map);
//        }
//        MyAdapter myAdapter = new MyAdapter(getActivity(),lstImageItem);
//        //添加并且显示
//        function.setAdapter(myAdapter);
//        //添加消息处理
//        function.setOnItemClickListener(myAdapter);


//        mSwipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.id_swipe_ly);//下拉刷新控件
//        mSwipeLayout.setColorSchemeResources(R.color.main_bg);
//        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                mSwipeLayout.setRefreshing(true);
//                LogUtils.e("Swipe", "刷新");
//                if (!Utils.isLogined()) {//判断是否登录
//                    goLogin = true;
//                    Utils.makeToast(getActivity(), Constants.NEED_LOGIN);
//                    startActivity(new Intent(getActivity(), LoginActivity.class));
//                } else {
//                    HashMap<String, String> params = new HashMap<>();
//                    params.put("user_id", Utils.getUserId());
//                    params.put("token", Utils.getToken());
//                    CustomTask task = new CustomTask(mHandler, Constants.WHAT_WALLET_DETAIL
//                            , Constants.URL_WALLET_DETAIL, true, params, true);
//                    task.execute();
//                }
//            }
//        });
    }

/*//2015年12月29日11:31:39  轮播图的adapter
    private void initBannerData() {
        adapter = new BannerViewPagerAdapter(getActivity(),imgArrays,urlArrays);
        bl_banner.bannerViewPager.setAdapter(adapter);
        bl_banner.initPagerIndicator();
        bl_banner.showPageIndicator();
    }*/


    /**
     * 刷新我的资产数据
     */
    public void onUpdateData(Object... params) {
        if (params != null && params.length > 0) {
            // TODO
            this.mPtrl = (PullToRefreshScrollView) params[0];
        }
        if (!Utils.isLogined()) {//判断是否登录
            goLogin = true;
            Utils.makeToast(getActivity(), Constants.NEED_LOGIN);
            startActivity(new Intent(getActivity(), LoginActivity.class));
            hideProcessBar();
        } else {
            HashMap<String, String> params1 = new HashMap<>();
            params1.put("user_id", Utils.getUserId());
            params1.put("token", Utils.getToken());
            CustomTask task = new CustomTask(mHandler, Constants.WHAT_WALLET_DETAIL
                    , Constants.URL_WALLET_DETAIL, true, params1, true);
            task.execute();
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            try {
//                mSwipeLayout.setRefreshing(false);

                JsonBean jsonBean = (JsonBean) msg.obj;
                int code = jsonBean.getCode();
                String errorMsg = jsonBean.getError_msg();
                if (code == Constants.DATA_EVENT) {
                    hideProcessBar();
                    Utils.makeToast(getActivity(), Constants.ERROR_MSG_CODE + code);
                    return;
                }
                int action = msg.what;
                switch (action) {
                    case Constants.WHAT_WALLET_DETAIL:
                        boolean flag = Utils.filtrateCode(getActivity(), jsonBean);
                        if (flag && code == Constants.OPERATION_FAIL) {//数据交互失败
                            Utils.makeToast(getActivity(), errorMsg);
                        } else if (flag && code == Constants.OPERATION_SUCCESS) {//数据交互成功
                            JSONObject jsonObject = new JSONObject(jsonBean.getJsonString());
                            earnings = jsonObject.optString("todayInterest");//今日收益
                            LogUtils.e("-----------怎么没了呢-----------", "earnings：" + earnings);
                            assets = jsonObject.optString("eTotal");//我的资产
                            accumulative = jsonObject.optString("receivableInterest");//可用收益
                            LogUtils.e("-----------是否为可用收益------------", "accumulative：" + accumulative + assets);
//原                         rate = "6"+"%";//由于接口没有利率数据
                            rate = jsonObject.optString("annualized") + "%";
//                            accumulative = jsonObject.optString("interestTotal");//累计收益
//                            identity = jsonObject.optString("starIdentity");//身份证号
//                            bankCord = jsonObject.optString("starCardNumber");//银行卡号
                            BigDecimal bigAssets = new BigDecimal(assets);   //创建BigDecimal对象
                            BigDecimal bigAccumulative = new BigDecimal(accumulative);
                            BigDecimal bigInterest = bigAssets.add(bigAccumulative); //BigDecimal运算
                            amount.setText(bigInterest.toPlainString()+"  ");//总金额
                            //amount.setText((Double.parseDouble(assets) + Double.parseDouble(accumulative)) + "  ");//总金额
                            fillViews();
                        }
                        hideProcessBar();
                        break;
                    case Constants.WHAT_QUERY_SEVENDAYSRATES:
                        JSONObject json = jsonBean.getJsonObject();
                        JSONObject sevenResult = json.optJSONObject("result");
//                        String maxRate=sevenResult.optString("maxRate");//最大的利率数值
                        String midRate = sevenResult.optString("midRate");//中间的利率数值
//                        String minRate=sevenResult.optString("minRate");//最小的利率数值
                        rateArray = sevenResult.optJSONArray("rates");//七天浮动利率数组
                        //把JSONArray类型的数据转换成String类型的数组
                        List<String> listRate = new ArrayList<>();
                        for (int i = 0; i < rateArray.length(); i++) {
                            listRate.add(rateArray.getString(i));
                        }
                        stringRateArray = listRate.toArray(new String[listRate.size()]);
                        LogUtils.e("------------- 打印打印打印------------", "stringArray:" + stringRateArray[3]);
//                        str1 = stringRateArray[0];
//                        str2 = stringRateArray[1];
//                        str3 = stringRateArray[2];
//                        str4 = stringRateArray[3];
//                        str5 = stringRateArray[4];
//                        str6 = stringRateArray[5];
//                        str7 = stringRateArray[6];
                        initLineChartView();//保持线程数据同步
                        LogUtils.e("-------------数据数据数据数据数据-----------------", "sevenResult:" + sevenResult);
                        LogUtils.e("--------------中间数值---------------------------", "midRate：" + midRate);
                        LogUtils.e("-------------数据数组数据数组数据数组------------", "rateArray:" + rateArray);
                        break;
                    //2015年12月28日18:55:52  测试
                }
            } catch (Exception e) {
                e.printStackTrace();
                hideProcessBar();
                LogUtils.e(TAG, "handler 异常");
            }
        }
    };

    /**
     * widght填充数据
     */
    private void fillViews() {
//        if(!TextUtils.isEmpty(Ke_Fu_data.getWalletTip(getActivity()))){
//            wallet_tip.setText(Ke_Fu_data.getWalletTip(getActivity()));
//        }
        Utils.safeCopyWrite(getActivity());//设置安全文案
        walletEarnings.setText(earnings);//今日收益
        walletAssets.setText(assets);//我的资产
        walletAccumulative.setText(accumulative);//累计收益
        //  walletRate.setText(rate);//钱包利率  2016年1月4日13:24:45 已不使用
    }

    public void empty() {
        walletEarnings.setText("0.00");//今日收益
        walletAssets.setText("0.00");//我的资产
        walletAccumulative.setText("0.00");//累计收益
    }

    //    /**
//     * 自定义适配器
//     */
//    class MyAdapter  extends BaseAdapter implements AdapterView.OnItemClickListener{
//        private Context context;
//        private LayoutInflater layoutInflater;
//        private ArrayList<HashMap<String, Object>> list;
//        //构造方法，参数list传递的就是这一组数据的信息
//        public MyAdapter(Context context,ArrayList<HashMap<String, Object>> list)
//        {
//            this.context = context;
//            layoutInflater = LayoutInflater.from(context);
//            this.list = list;
//        }
//        @Override
//        public int getCount() {
//            return this.list!=null? this.list.size(): 0 ;
//        }
//
//        @Override
//        public Object getItem(int position) {
//            return this.list.get(position);
//        }
//
//        @Override
//        public long getItemId(int position) {
//            return position;
//        }
//
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//            ViewHolder holder;
//            HashMap<String, Object> map = list.get(position);
//            if (null == convertView) {
//                holder = new ViewHolder();
//                convertView = layoutInflater.inflate(R.layout.itme_function, null);
//                holder.ItemText = (TextView) convertView.findViewById(R.id.ItemText);
//                holder.icon_image = (ImageView) convertView.findViewById(R.id.icon_image);
//                holder.icon_image_n = (ImageView) convertView.findViewById(R.id.icon_image_n);
//                convertView.setTag(holder);
//            } else {
//                holder = (ViewHolder) convertView.getTag();
//            }
//            if(map.get("ItemText").equals("")){
//                holder.ItemText.setVisibility(View.GONE);
//                holder.icon_image.setVisibility(View.GONE);
//                holder.icon_image_n.setVisibility(View.VISIBLE);
//                holder.icon_image_n.setImageResource((Integer) map.get("ItemImage"));
//            }else{
//                holder.ItemText.setText(map.get("ItemText") + "");
//                holder.icon_image.setImageResource((Integer) map.get("ItemImage"));
//            }
//            return convertView;
//        }
//        protected class ViewHolder{
//            TextView ItemText;
//            ImageView icon_image,icon_image_n;
//        }
//        @Override
//        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//            switch (position) {
//                case 0://存钱记录
//                    startActivity(new Intent(getActivity(), E_wallet_record_activity.class).putExtra("choose", 1));
//                    break;
//                case 1://取钱记录
//                    startActivity(new Intent(getActivity(), E_wallet_record_activity.class).putExtra("choose", 2));
//                    break;
//                case 2://累计收益
//                    startActivity(new Intent(getActivity(), Earnings_record.class));
//                    break;
//
//            }
//        }
//    }
    @Override
    public void startProcessBar() {
//        if(progressDialog == null){
//            progressDialog = new ProgressDialog(getActivity(),R.style.loaNding_dialog);
//        }
//        if(progressDialog.isShowing()){
//            progressDialog.dismiss();
//        }
//        progressDialog.show();
        if (isOne) {
            // TODO
//            mPtrl.autoRefresh();

        }
    }

    @Override
    public void hideProcessBar() {
//        if(progressDialog != null && progressDialog.isShowing()){
//            progressDialog.dismiss();
//        }
        if (mPtrl != null && isOne) {
            // TODO
//            mPtrl.refreshFinish(PullToRefreshLayout.SUCCEED);
            mPtrl.onRefreshComplete();
        } else {
            isOne = true;
        }
    }

    @Override
    public void showNetError() {
//        Utils.makeToast(getActivity(), Constants.IS_EVENT_MSG);
        Utils.makeToast(mActivity, Constants.IS_EVENT_MSG);
    }

    @Override
    public void startNextActivity() {
        if (TextUtils.isEmpty(Utils.getUserId()) || TextUtils.isEmpty(Utils.getToken())) {//判断是否登录
            goLogin = true;
            hideProcessBar();
            if (!Utils.getIsMask(getActivity())) {

            } else {
                Utils.makeToast(getActivity(), Constants.NEED_LOGIN);
                startActivity(new Intent(getActivity(), LoginActivity.class));
            }
        } else {
            HashMap<String, String> params = new HashMap<>();
            params.put("user_id", Utils.getUserId());
            params.put("token", Utils.getToken());
            CustomTask task = new CustomTask(mHandler, Constants.WHAT_WALLET_DETAIL
                    , Constants.URL_WALLET_DETAIL, true, params, true);
            task.execute();
        }
        //  七日年化收益率接口 2015年12月21日10:54:13
        sevenDaysRates();

//        imgArrays.clear();
//        urlArrays.clear();
        //  轮播图接口 2016年1月6日10:54:22
        getImage();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint()) {
            setData(true);
        } else {
            goLogin = false;
            setData(false);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setData(goLogin && MApplication.goLogin);
//        fl_restart.invalidate();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.wallet_assets_layout://我的资产layout
                startActivity(new Intent(getActivity(), E_wallet_record_activity.class));
                break;
            case R.id.today_earnings://今日收益和累计收益是同一个页面  所有  不用break
            case R.id.wallet_accumulative_layout://累计收益layout
                startActivity(new Intent(getActivity(), Earnings_record.class));
                break;
            case R.id.wallet_rate_layout://钱包简介
                startActivity(new Intent(getActivity(), Earnings_record.class));
                /*startActivity(new Intent(getActivity(), WebViewActivity.class).putExtra("url", url).putExtra("title", link_name));*/
                break;
            case R.id.wallet_draw_money://转出
                if (progressDialog == null) {
                    progressDialog = new ProgressDialog(getActivity(), R.style.loading_dialog);
                }
                Withdraw_money_Client.getWithdraw_money(getActivity(), Constants.WHAT_WITHDRAW, progressDialog);
                break;
            case R.id.wallet_save_money://转入
                getActivity().startActivity(new Intent(getActivity(), SaveMoneyActivity.class));
                break;
            case R.id.iv_spread://活动图片
//                if (!(urlArrays.get(0)==null)){}
                    startActivity(new Intent(getActivity(), WebViewActivity.class).putExtra("url", urlArrays.get(0)).putExtra("title", "活动中心"));
                break;
        }
    }


    /*private void initData() {
        GraphViewSeries exampleSeries =
                new GraphViewSeries("", new GraphViewSeries.GraphViewSeriesStyle(Color.rgb(250, 98, 65), 7),//最后一个参数是画出来的折线的宽度
                        new GraphView.GraphViewData[]{new GraphView.GraphViewData(now - 6 * t, data[0]),//若今天是周日，这个就是周一
                                new GraphView.GraphViewData(now - 5 * t, data[1]), new GraphView.GraphViewData(now - 4 * t, data[2]),
                                new GraphView.GraphViewData(now - 3 * t, data[3]), new GraphView.GraphViewData(now - 2 * t, data[4]),
                                new GraphView.GraphViewData(now - 1 * t, data[5]), new GraphView.GraphViewData(now, data[6])});

        graphView = new LineGraphView(getActivity(), "");

        ((LineGraphView) graphView).setDrawBackground(true);
        // 线条色
//        ((LineGraphView) graphView).setBackgroundColor(Color.argb(128, 254, 232, 226));
        (graphView).setBackgroundColor(Color.alpha(0));
        ((LineGraphView) graphView).setDataPointsRadius(0);

        // 字体色
        int fontColor = Color.parseColor("#9B9A9B");
        // 风格色
        graphView.getGraphViewStyle().setGridColor(Color.parseColor("#EEEEEE"));//表格的横竖线的颜色
        graphView.getGraphViewStyle().setHorizontalLabelsColor(fontColor);
        graphView.getGraphViewStyle().setVerticalLabelsColor(fontColor);
        // x轴标签数
        graphView.getGraphViewStyle().setNumHorizontalLabels(7);
        // y轴标签数
        graphView.getGraphViewStyle().setNumVerticalLabels(8);
        // 字号
        graphView.getGraphViewStyle().setTextSize(DensityUtil.sp2px(getActivity(), 6));
        graphView.getGraphViewStyle().setVerticalLabelsAlign(Paint.Align.RIGHT);
        graphView.getGraphViewStyle().setVerticalLabelsWidth(DensityUtil.sp2px(getActivity(), 20));

        graphView.addSeries(exampleSeries);
        //将x轴的毫秒值格式化成日期
        final SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd", Locale.CHINESE);
        graphView.setCustomLabelFormatter(new CustomLabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX) {
                    Date d = new Date((long) value);
                    return dateFormat.format(d);
                }
                return null;
            }
        });

        将构建好的视图添加到根布局中 代码里预

        //将构建好的视图添加到根布局里，在代码里预留一个LinearLayout就行
        lltable.addView(graphView);

    }
*/

    /**
     * 绘制折线
     * now - 6 * t, new BigDecimal(stringRateArray[0]).multiply(decimal100).doubleValue()
     * now - 5 * t, Double.parseDouble(stringRateArray[1]) * 100
     * now, Double.parseDouble(stringRateArray[6]) * 100)
     */
    private void initLineChartView() {
        final BigDecimal decimal100 = new BigDecimal(100);
        long now = new Date().getTime();// 当前时间
        long t = 86400000;
        GraphViewSeries exampleSeries = new GraphViewSeries("",
                new GraphViewSeries.GraphViewSeriesStyle(Color.rgb(250, 98, 65), DensityUtil
                        .dip2px(getActivity(), 3)),// 折线颜色(深蓝色)0, 113, 185
                new GraphView.GraphViewData[]{
                        new GraphView.GraphViewData(now - 6 * t, new BigDecimal(stringRateArray[0]).multiply(decimal100).doubleValue()),
                        new GraphView.GraphViewData(now - 5 * t, new BigDecimal(stringRateArray[1]).multiply(decimal100).doubleValue()),
                        new GraphView.GraphViewData(now - 4 * t, new BigDecimal(stringRateArray[2]).multiply(decimal100).doubleValue()),
                        new GraphView.GraphViewData(now - 3 * t, new BigDecimal(stringRateArray[3]).multiply(decimal100).doubleValue()),
                        new GraphView.GraphViewData(now - 2 * t, new BigDecimal(stringRateArray[4]).multiply(decimal100).doubleValue()),
                        new GraphView.GraphViewData(now - 1 * t, new BigDecimal(stringRateArray[5]).multiply(decimal100).doubleValue()),
                        new GraphView.GraphViewData(now, new BigDecimal(stringRateArray[6]).multiply(decimal100).doubleValue())});
        GraphView graphView;
        graphView = new LineGraphView(getActivity(), "");
        ((LineGraphView) graphView).setDrawBackground(true);

        //((LineGraphView) graphView).setBackgroundColor(Color.rgb(191, 230, 248));// 选择的背景颜色(淡蓝色)
        //((LineGraphView) graphView).setBackgroundColor(Color.parseColor("#200292D7"));// 选择的背景颜色(淡蓝色)
        (graphView).setBackgroundColor(Color.alpha(0));//折线下方的背景
        ((LineGraphView) graphView).setDataPointsRadius(0);

        /** 字体色 */
        int fontColor = Color.parseColor("#808080");
        // 风格色//表格线颜色
        graphView.getGraphViewStyle().setGridColor(Color.parseColor("#D8DDE3"));
        graphView.getGraphViewStyle().setHorizontalLabelsColor(fontColor);
        graphView.getGraphViewStyle().setVerticalLabelsColor(fontColor);
        // x轴标签数
        graphView.getGraphViewStyle().setNumHorizontalLabels(7);
        // y轴标签数
        graphView.getGraphViewStyle().setNumVerticalLabels(7);
        //
        // 隐藏y轴标签
        graphView.setShowVerticalLabels(false);
        // 字号 yx轴
        graphView.getGraphViewStyle().setTextSize(
                DensityUtil.dip2px(getActivity(), 8));
        // 图标利率数值字号
        graphView.getGraphViewStyle().setTextSizeDot(
                DensityUtil.dip2px(getActivity(), 15));
        // 虚拟字体 为了空间大一点
        // graphView.getGraphViewStyle().setTextSizeDots(
        // DensityUtil.dip2px(MyDemoActivity.this, 16));
        graphView.getGraphViewStyle().setVerticalLabelsAlign(Paint.Align.RIGHT);
        graphView.getGraphViewStyle().setVerticalLabelsWidth(
                DensityUtil.dip2px(getActivity(), 37));// 设置宽度(两列的宽度)

        graphView.addSeries(exampleSeries);

        final SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd",
                Locale.CHINESE);
        graphView.setCustomLabelFormatter(new CustomLabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX) {
                    Date d = new Date((long) value);
                    return dateFormat.format(d);
                }
                return null;
            }
        });
        linechart.addView(graphView);//将七日年化收益率的图标
    }


    //只需要传入userID和token值，返回七日年化收益率和最大最小值
    public void sevenDaysRates() {
        //服务器数据交互操作
        HashMap<String, String> maps = new HashMap<>();
        maps.put("user_id", Utils.getUserId());
        maps.put("token", Utils.getToken());
        CustomTask task = new CustomTask(mHandler, Constants.WHAT_QUERY_SEVENDAYSRATES,
                Constants.UTIL_QUERY_SEVENDAYSRATES,
                true, maps, true);
        task.execute();
    }

    //2015年12月28日18:52:38  测试
    public void getImage() {
        //服务器数据交互操作
        HashMap<String, String> maps = new HashMap<>();
        maps.put("user_id", Utils.getUserId());
        maps.put("token", Utils.getToken());
        CustomTask task = new CustomTask(imHandler, Constants.WHAT_QUERY_IMAGEDATA,
                Constants.QUERY_IMAGEDATA,
                true, maps);
        task.execute();
    }
    /**
     * 访问轮播图的接口，拿到当前接口的数据所用到的handler
     */
    private Handler imHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            try {
                JsonBean jsonBean = (JsonBean) msg.obj;
                int code = jsonBean.getCode();
                if (code == Constants.DATA_EVENT) {
                    hideProcessBar();
                    return;
                }
                int action = msg.what;
                switch (action) {
                    case Constants.WHAT_QUERY_IMAGEDATA:
//                        boolean flag = Utils.filtrateCode(getActivity(), jsonBean);
//                        if (flag && code == Constants.OPERATION_FAIL) {//数据交互失败
//                        } else if (flag && code == Constants.OPERATION_SUCCESS) {//数据交互成功
                            JSONObject jsonObject = new JSONObject(jsonBean.getJsonString());
                            JSONArray imageData=jsonObject.optJSONArray("data");
                            LogUtils.e("-------------图片信息图片信息---------------", "imageData：" + imageData);

                        imgArrays.clear();//每次请求数据前先刷新一次 避免多次填充
                        urlArrays.clear();

                        for (int i = 0; i <imageData.length(); i++){
                            //拿到所有的img和url，并把他们放在集合中
                            String mImage=imageData.getJSONObject(i).getString("img");
                            LogUtils.e("-------------imgimgimgimgimgimgimgimg---------------", "mImage：" + mImage);
                            imgArrays.add(mImage);
                            String mUrl=imageData.getJSONObject(i).getString("url");
                            LogUtils.e("-------------urlurlurlurlurlurlurlurl---------------", "mUrl：" + mUrl);
                            urlArrays.add(mUrl);
                        }
//                      ImageLoader加载图片  2015年12月29日14:09:27
                        ImageLoader.getInstance().displayImage(imgArrays.get(0), iv_spread);
                        //adapter.notifyDataSetChanged();//填充数据变化后去刷新数据  暂未使用
                        LogUtils.e("-------------imgArraysimgArraysimgArraysimgArrays---------------", "imgArrays：" + imgArrays);
                        LogUtils.e("-------------urlArraysurlArraysurlArraysurlArrays---------------", "urlArrays：" + urlArrays);
//                        }
                        hideProcessBar();
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
                hideProcessBar();
                LogUtils.e("new_Fragment", "handler 异常");
            }
        }
    };


    /**
     * 自动下拉
     */
    class AutoPullListener implements PullToRefreshLayout.OnRefreshListener {

        @Override
        public void onRefresh(final PullToRefreshLayout pullToRefreshLayout) {
            // 下拉刷新操作
            new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    // 千万别忘了告诉控件刷新完毕了
                    My_wallet_new_Fragment.this.onUpdateData(pullToRefreshLayout);
//                    mPtrl.refreshFinish(PullToRefreshLayout.SUCCEED);
                }
            }.sendEmptyMessageDelayed(0, 0);
        }

        @Override
        public void onLoadMore(final PullToRefreshLayout pullToRefreshLayout) {
            // 加载操作
            new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    // 千万别忘了告诉控件加载完毕了
//                    pullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                }
            }.sendEmptyMessageDelayed(0, 2000);
        }
    }
}
