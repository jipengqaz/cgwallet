package cgtz.com.cgwallet.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cgtz.com.cgwallet.MApplication;
import cgtz.com.cgwallet.R;
import cgtz.com.cgwallet.bean.JsonBean;
import cgtz.com.cgwallet.client.Earnings_record_Client;
import cgtz.com.cgwallet.presenter.SplashPresenter;
import cgtz.com.cgwallet.utils.AppUtil;
import cgtz.com.cgwallet.utils.Utils;
import cgtz.com.cgwallet.view.ISplashView;
import cgtz.com.cgwallet.view.RefreshAndLoadMoreListView;
import cgtz.com.cgwallet.widget.ProgressDialog;

/**
 * 我的收益记录
 * Created by Administrator on 2015/4/22 0022.
 */
public class Earnings_record extends BaseActivity implements ISplashView {

    private RefreshAndLoadMoreListView listview;
    private ProjectAdapter1 projectAdapter;
    private ArrayList<Map> projects;
    private int PAGE_SIZE=10;
    private static boolean aaa = false;//判断是否刷新
    private ProgressDialog progressDialog;

    private  String TAG = "Earnings_record";
    private int page =0;
    private SplashPresenter presenter;

    private Double minInterest,maxInterest;//最低  最高收益
    private int width;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_earnings_record);
        MApplication.registActivities(this);//存储该activity
        showBack(true);
        setTitle("我的收益");
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        presenter = new SplashPresenter(this);
        width = AppUtil.getScreenDispaly(Earnings_record.this)[0]-20;//获取宽度
        listview= (RefreshAndLoadMoreListView) findViewById(R.id.record_listview);
        projectAdapter = new ProjectAdapter1();
        listview.setAdapter(projectAdapter);
        listview.setLoadMore(true);
        listview.setOnLoadMoreLister(projectAdapter);//加载
        listview.setOnItemClickListener(projectAdapter);//记录单击事件
        listview.setOnRefreshListener(projectAdapter);//刷新
        listview.loadMore();
        if(progressDialog == null){
            progressDialog = new ProgressDialog(this,R.style.loading_dialog);
        }
        Utils.SaveOrDrawMoney(this, progressDialog);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    @Override
    public void startProcessBar() {
    }

    @Override
    public void hideProcessBar() {
    }

    @Override
    public void showNetError() {
        Utils.makeToast(this,"错误");
    }

    @Override
    public void startNextActivity() {
        if(!aaa){
            page +=1;
            Earnings_record_Client.getData(projectAdapter.handler,page,PAGE_SIZE);
        }else{
            page =1;//刷新时把页数初始化
            Earnings_record_Client.getData(projectAdapter.handler,1,PAGE_SIZE);
        }
    }

    class ProjectAdapter1 extends BaseAdapter implements  RefreshAndLoadMoreListView.OnLoadMoreListener,
            AdapterView.OnItemClickListener,  RefreshAndLoadMoreListView.OnRefreshListener{
        public ProjectAdapter1(){projects = new ArrayList<Map>();}
        public Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                try {
                    JsonBean jsonBean = (JsonBean) msg.obj;
                    if(!Utils.filtrateCode(Earnings_record.this,jsonBean)){
                        listview.onRefreshComplete();
                        return;
                    }
                    JSONObject json = new JSONObject(jsonBean.getJsonString());
                    Log.e(TAG, "json" + jsonBean.getJsonString());
                    if(json.optString("success").equals("1")) {
                        JSONArray list = json.optJSONArray("list");
                        int size = 0;
                        maxInterest = json.optDouble("maxInterest");
                        minInterest = json.optDouble("minInterest");
                        ArrayList<Map> inves = new ArrayList<Map>();
                        if((aaa ||projects.size() == 0)){
                            Map map = new HashMap();
                            map.put("receivableInterest",json.optDouble("receivableInterest"));//待转出利息
                            map.put("receivedInterest",json.optDouble("receivedInterest"));//可提利息
                            map.put("totalCount",json.optDouble("interestTotal"));//累计收益
                            inves.add(map);
                        }
                        if (list != null) {
                            size = list.length();
                            JSONObject invesJson;
                            for (int i = 0; i < size; i++) {
                                invesJson = list.optJSONObject(i);
                                Map map = new HashMap();
                                map.put("interest",invesJson.optString("interest"));
                                map.put("date",invesJson.optString("date"));//时间
                                inves.add(map);
                            }
                        }
                            if (aaa) {//判断是否是刷新  是的话清空数据
                                projects.clear();
                            }
                            projects.addAll(inves);
                            listview.onRefreshComplete();
                            listview.onLoadMoreComplete();
                            if (page  >= json.optInt("pageNum")) {
                                listview.noMore(true);
                            }
                            notifyDataSetChanged();
                    }else{

                    }
                    if(projects.size() ==0 ){
                        listview.noItemTip();
                        listview.noMore(false,false);
                    }
                    listview.onRefreshComplete();
                } catch (JSONException e) {
                    e.printStackTrace();
//                    Earnings_record.this.finish();
                }
            }
        };
        @Override
        public int getCount() {
            return projects.size();
        }

        @Override
        public Object getItem(int position) {
            return projects.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }
        @Override
        public int getItemViewType(int position) {
            int type1 = 0;
            // 根据position元素返回View的类型, type值是从0开始排序的
                if(position == 0){
                    type1 = 1;
                }else{
                    type1 = 0;
                }
            return type1;
        }
        @Override
        public int getViewTypeCount() {
            return 2;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder0 holder;
            ViewHolder1 holder1;
            Map map=projects.get(position);
            int layoutType = getItemViewType(position);
            switch (layoutType) {
                case 0:
                    if (null == convertView) {
                        holder = new ViewHolder0();
                        convertView = LinearLayout.inflate(Earnings_record.this, R.layout.item_earnings_record_2, null);
                        holder.money = (TextView) convertView.findViewById(R.id.money);
                        holder.time = (TextView) convertView.findViewById(R.id.time);
                        convertView.setTag(holder);
                    } else {
                        holder = (ViewHolder0) convertView.getTag();
                    }
                    Double interest = Double.parseDouble(map.get("interest").toString());
                    holder.time.setText(map.get("date") + "");
                    holder.money.setText(map.get("interest").toString());
                    Log.e(TAG, width + "     " + (int) ((interest - minInterest) / (maxInterest - minInterest) * (width / 2) + (width / 2)));

                    ViewGroup.LayoutParams layout = holder.money.getLayoutParams();
                    if(interest .equals( minInterest)){
                        layout.width = width/2;
                    }else if(interest.equals(maxInterest)){
                        layout.width = width;
                    }else{
                        layout.width = (int) ((interest - minInterest) / (maxInterest - minInterest) * (width / 2) + (width/2));
                    }
//                    LogUtils.e(TAG,layout.width+"      "+interest  +"    "+maxInterest+"     "+minInterest);
                    holder.money.setLayoutParams(layout);
                    if(position == 1){
                        holder.money.setBackgroundColor(getResources().getColor(R.color.main_bg));
                    }else{
                        holder.money.setBackgroundColor(getResources().getColor(R.color.item_earnings_record_2_2));
                    }
                    break;
                case 1:
                    if (null == convertView) {
                        holder1 = new ViewHolder1();
                        convertView = LinearLayout.inflate(Earnings_record.this, R.layout.item_earnings_record_1, null);
                        holder1.eTotal = (TextView) convertView.findViewById(R.id.eTotal);
                        holder1.eBalance = (TextView) convertView.findViewById(R.id.eBalance);
                        holder1.receivableInterest = (TextView) convertView.findViewById(R.id.receivableInterest);
                        convertView.setTag(holder1);
                    } else {
                        holder1 = (ViewHolder1) convertView.getTag();
                    }
                    holder1.eTotal.setText(map.get("totalCount")+"");
                    holder1.eBalance.setText(map.get("receivedInterest")+"");
                    holder1.receivableInterest.setText(map.get("receivableInterest")+"");
                    break;
            }
            return convertView;
        }
        protected class ViewHolder0{
            TextView time,money;

        }
        private class  ViewHolder1{
            TextView eTotal,eBalance,receivableInterest;//累计收益，可提利息，待提利息
        }


        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {//选择item触发事件

        }

        @Override
        public void onLoadMore() {
            aaa = false;

            presenter.didFinishLoading(Earnings_record.this);
        }
        @Override
        public void onRefresh() {
            aaa = true;
            presenter.didFinishLoading(Earnings_record.this);

        }
    }
    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
