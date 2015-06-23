package cgtz.com.cgwallet.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import cgtz.com.cgwallet.MApplication;
import cgtz.com.cgwallet.R;
import cgtz.com.cgwallet.activity.E_wallet_record_activity;
import cgtz.com.cgwallet.activity.Earnings_record;
import cgtz.com.cgwallet.activity.LoginActivity;
import cgtz.com.cgwallet.bean.JsonBean;
import cgtz.com.cgwallet.presenter.SplashPresenter;
import cgtz.com.cgwallet.utility.Constants;
import cgtz.com.cgwallet.utils.CustomTask;
import cgtz.com.cgwallet.utils.LogUtils;
import cgtz.com.cgwallet.utils.Utils;
import cgtz.com.cgwallet.view.ISplashView;
import cgtz.com.cgwallet.view.NotSlideGridView;
import cgtz.com.cgwallet.widget.ProgressDialog;

/**
 * 我的钱包
 * Created by Administrator on 2015/6/17.
 */
public class My_wallet_new_Fragment extends BaseFragment implements ISplashView,View.OnClickListener{
    private static final String TAG = "My_wallet_new_Fragment";
    private TextView walletEarnings;//今日收益
    private LinearLayout assetsLayout;//我的资产layout
    private TextView walletAssets;//我的资产
    private LinearLayout accumulativeLayout;//累计收益layout
    private TextView walletAccumulative;//累计收益
    private ProgressDialog progressDialog;
    private SplashPresenter presenter;
    private String earnings;//今日收益
    private String assets;//我的资产
    private String accumulative;//累计收益
    private int screenWidth;
    private boolean goLogin = false;//判断是否去登录的标志
    private LinearLayout today_earnings;//今日收益
    private SwipeRefreshLayout mSwipeLayout;//下拉刷新控件
    private View layoutView;
    private NotSlideGridView function;//所有的选项


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        screenWidth = getResources().getDisplayMetrics().widthPixels;
        presenter = new SplashPresenter(this);
        layoutView = inflater.inflate(R.layout.layout_my_wallet_new,container,false);
        initViews(layoutView);
//        setData(true);
        setListener();
        return layoutView;
    }

    public void clearFocus(){
        if(assetsLayout != null){
            assetsLayout.setEnabled(false);
        }
        //累计收益layout
        if(accumulativeLayout != null){
            accumulativeLayout.setEnabled(false);
        }
        //今日收益layout
        if(today_earnings != null){
            today_earnings.setEnabled(false);
        }
        //选项
        if(function != null){
            function.setEnabled(false);
        }
        //下拉刷新
        if(mSwipeLayout != null){
            mSwipeLayout.setEnabled(false);
        }
    }

    public void requetFocus(){
        //我的资产layout
        if(assetsLayout != null){
            assetsLayout.setEnabled(true);
        }
        //累计收益layout
        if(accumulativeLayout != null){
            accumulativeLayout.setEnabled(true);
        }
        //今日收益layout
        if(today_earnings != null){
            today_earnings.setEnabled(true);
        }
        //选项
        if(function != null){
            function.setEnabled(true);
        }
        //下拉刷新
        if(mSwipeLayout != null){
            mSwipeLayout.setEnabled(true);
        }
    }
    public void setData(boolean flag){
        if(flag){
            presenter.didFinishLoading(getActivity());
        }
    }
    /**
     * widget设置事件
     */
    private void setListener(){
        assetsLayout.setOnClickListener(this);//我的资产layout
        accumulativeLayout.setOnClickListener(this);//累计收益layout
        today_earnings.setOnClickListener(this);//今日收益layout
    }
    /**
     * 初始化widget
     * @param view
     */
    private void initViews(View view){
        accumulativeLayout = (LinearLayout) view.findViewById(R.id.wallet_accumulative_layout);//累计收益layout
        walletAccumulative = (TextView) view.findViewById(R.id.wallet_accumulative_earnings);//累计收益
        today_earnings = (LinearLayout) view.findViewById(R.id.today_earnings);//今日收益
        assetsLayout = (LinearLayout) view.findViewById(R.id.wallet_assets_layout);//我的资产layout
        walletAssets = (TextView) view.findViewById(R.id.wallet_my_assets);//我的资产
        function = (NotSlideGridView) view.findViewById(R.id.function)  ;//列表选项
        walletEarnings = (TextView) view.findViewById(R.id.wallet_earnings);

        //生成动态数组，并且转入数据
        ArrayList<HashMap<String, Object>> lstImageItem = new ArrayList<HashMap<String, Object>>();
        int [] image = {R.mipmap.icon_zhuanru,R.mipmap.icon_zhuanchu,R.mipmap.icon_leijishouyi};
        String[] name = {"转入记录","转出记录","累计收益"};
        for(int i=0;i<image.length;i++)
        {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("ItemImage",image[i]);//添加图像资源的ID
            map.put("ItemText", name[i]);//添加文案
            lstImageItem.add(map);
        }
        MyAdapter myAdapter = new MyAdapter(getActivity(),lstImageItem);
        //添加并且显示
        function.setAdapter(myAdapter);
        //添加消息处理
        function.setOnItemClickListener(myAdapter);


        mSwipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.id_swipe_ly);//下拉刷新控件
        mSwipeLayout.setColorSchemeResources(R.color.main_bg);
        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeLayout.setRefreshing(true);
                LogUtils.e("Swipe", "刷新");
                if (TextUtils.isEmpty(Utils.getUserId()) || TextUtils.isEmpty(Utils.getToken())) {//判断是否登录
                    goLogin = true;
                    Utils.makeToast(getActivity(), Constants.NEED_LOGIN);
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                } else {
                    HashMap<String, String> params = new HashMap<>();
                    params.put("user_id", Utils.getUserId());
                    params.put("token", Utils.getToken());
                    CustomTask task = new CustomTask(mHandler, Constants.WHAT_WALLET_DETAIL
                            , Constants.URL_WALLET_DETAIL, true, params, true);
                    task.execute();
                }
            }
        });
    }

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            try{

                mSwipeLayout.setRefreshing(false);

                JsonBean jsonBean = (JsonBean) msg.obj;
                int code = jsonBean.getCode();
                String errorMsg = jsonBean.getError_msg();
                if(code == Constants.DATA_EVENT){
                    hideProcessBar();
                    Utils.makeToast(getActivity(),Constants.ERROR_MSG_CODE+code);
                    return;
                }
                int action = msg.what;
                switch (action){
                    case Constants.WHAT_WALLET_DETAIL:
                        boolean flag = Utils.filtrateCode(getActivity(),jsonBean);
                        if(flag && code == Constants.OPERATION_FAIL){//数据交互失败
                            Utils.makeToast(getActivity(), errorMsg);
                        }else if(flag && code == Constants.OPERATION_SUCCESS){//数据交互成功
                            JSONObject jsonObject = new JSONObject(jsonBean.getJsonString());
                            earnings = jsonObject.optString("todayInterest");//今日收益
                            assets = jsonObject.optString("eTotal");//我的资产
                            accumulative = jsonObject.optString("interestTotal");//累计收益
//                            identity = jsonObject.optString("starIdentity");//身份证号
//                            bankCord = jsonObject.optString("starCardNumber");//银行卡号
                            fillViews();
                        }
                        hideProcessBar();
                        break;
                }
            }catch (Exception e){
                e.printStackTrace();
                hideProcessBar();
                LogUtils.e(TAG,"handler 异常");
            }
        }
    };

    /**
     * widght填充数据
     */
    private void fillViews(){
        walletEarnings.setText(earnings);//今日收益
        walletAssets.setText(assets);//我的资产
        walletAccumulative.setText(accumulative);//累计收益
    }
    /**
     * 自定义适配器
     */
    class MyAdapter  extends BaseAdapter implements AdapterView.OnItemClickListener{
        private Context context;
        private LayoutInflater layoutInflater;
        private ArrayList<HashMap<String, Object>> list;
        //构造方法，参数list传递的就是这一组数据的信息
        public MyAdapter(Context context,ArrayList<HashMap<String, Object>> list)
        {
            this.context = context;
            layoutInflater = LayoutInflater.from(context);
            this.list = list;
        }
        @Override
        public int getCount() {
            return this.list!=null? this.list.size(): 0 ;
        }

        @Override
        public Object getItem(int position) {
            return this.list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            HashMap<String, Object> map = list.get(position);
            if (null == convertView) {
                holder = new ViewHolder();
                convertView = layoutInflater.inflate(R.layout.itme_function, null);
                holder.ItemText = (TextView) convertView.findViewById(R.id.ItemText);
                holder.icon_image = (ImageView) convertView.findViewById(R.id.icon_image);
                holder.icon_image_n = (ImageView) convertView.findViewById(R.id.icon_image_n);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            if(map.get("ItemText").equals("")){
                holder.ItemText.setVisibility(View.GONE);
                holder.icon_image.setVisibility(View.GONE);
                holder.icon_image_n.setVisibility(View.VISIBLE);
                holder.icon_image_n.setImageResource((Integer) map.get("ItemImage"));
            }else{
                holder.ItemText.setText(map.get("ItemText") + "");
                holder.icon_image.setImageResource((Integer) map.get("ItemImage"));
            }
            return convertView;
        }
        protected class ViewHolder{
            TextView ItemText;
            ImageView icon_image,icon_image_n;
        }
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            switch (position) {
                case 0://存钱记录
                    startActivity(new Intent(getActivity(), E_wallet_record_activity.class).putExtra("choose", 1));
                    break;
                case 1://取钱记录
                    startActivity(new Intent(getActivity(), E_wallet_record_activity.class).putExtra("choose", 2));
                    break;
                case 2://累计收益

            }
        }
    }
    @Override
    public void startProcessBar() {
        if(progressDialog == null){
            progressDialog = new ProgressDialog(getActivity(),R.style.loading_dialog);
        }
        if(progressDialog.isShowing()){
            progressDialog.dismiss();
        }
        progressDialog.show();
    }

    @Override
    public void hideProcessBar() {
        if(progressDialog != null && progressDialog.isShowing()){
            progressDialog.dismiss();
        }
    }

    @Override
    public void showNetError() {
        Utils.makeToast(getActivity(), Constants.IS_EVENT_MSG);
    }

    @Override
    public void startNextActivity() {
        if(TextUtils.isEmpty(Utils.getUserId()) || TextUtils.isEmpty(Utils.getToken())){//判断是否登录
            goLogin = true;
            hideProcessBar();
            if(!Utils.getIsMask(getActivity())){

            }else{
                Utils.makeToast(getActivity(), Constants.NEED_LOGIN);
                startActivity(new Intent(getActivity(), LoginActivity.class));
            }
        }else{
            HashMap<String,String> params = new HashMap<>();
            params.put("user_id", Utils.getUserId());
            params.put("token", Utils.getToken());
            CustomTask task = new CustomTask(mHandler, Constants.WHAT_WALLET_DETAIL
                    ,Constants.URL_WALLET_DETAIL,true,params,true);
            task.execute();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(getUserVisibleHint()){
            setData(true);
        }else{
            goLogin = false;
            setData(false);
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        setData(goLogin && MApplication.goLogin);
    }
    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.wallet_assets_layout://我的资产layout
                startActivity(new Intent(getActivity(),E_wallet_record_activity.class));
                break;
            case R.id.today_earnings://今日收益和累计收益是同一个页面  所有  不用break
            case R.id.wallet_accumulative_layout://累计收益layout
                startActivity(new Intent(getActivity(), Earnings_record.class));
                break;
        }
    }

}
