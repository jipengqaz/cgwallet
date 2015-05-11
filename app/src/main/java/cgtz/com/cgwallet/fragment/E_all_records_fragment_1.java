package cgtz.com.cgwallet.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
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

import cgtz.com.cgwallet.R;
import cgtz.com.cgwallet.activity.E_wallet_record_activity;
import cgtz.com.cgwallet.bean.JsonBean;
import cgtz.com.cgwallet.client.E_wallet_list;
import cgtz.com.cgwallet.data.E_records;
import cgtz.com.cgwallet.presenter.SplashPresenter;
import cgtz.com.cgwallet.utils.Utils;
import cgtz.com.cgwallet.view.ISplashView;
import cgtz.com.cgwallet.view.RefreshAndLoadMoreListView;


/**
 * 全部记录 转入记录 转出记录
 * Created by Administrator on 2015-3-16.
 */
public class E_all_records_fragment_1 extends BaseFragment implements ISplashView {
    private RefreshAndLoadMoreListView listview;
    private ProjectAdapter1 projectAdapter;
    private ArrayList<E_records> projects;
    private int PAGE_SIZE=10;
    private static boolean aaa = false;//判断是否刷新

    private static final String TAG = "e_all_records_fragment";
    private int type; // 1 为全部记录  2 为转入记录   3 为转出记录
    private int page =0;
    private SplashPresenter presenter;
    private E_wallet_record_activity activity;
    public void setType(int type,E_wallet_record_activity aa){
        this.type = type;
        activity =aa;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view  = inflater.inflate(R.layout.activity_investment_record,null);
        init(view);
        return view;
    }
    /**
     * 初始化
     * @param view
     */
    private void init(View view) {
        presenter = new SplashPresenter(this);
        listview= (RefreshAndLoadMoreListView) view.findViewById(R.id.record_listview);
        projectAdapter = new ProjectAdapter1();
        listview.setAdapter(projectAdapter);
        listview.setLoadMore(true);
        listview.setOnLoadMoreLister(projectAdapter);//加载
        listview.setOnItemClickListener(projectAdapter);//记录单击事件
        listview.setOnRefreshListener(projectAdapter);//刷新
        listview.loadMore();
//        listview.setOnScrollListener(new AbsListView.OnScrollListener() {
//            /**
//             * ListView的状态改变时触发
//             * @param view
//             * @param scrollState
//             */
//            @Override
//            public void onScrollStateChanged(AbsListView view, int scrollState) {
//                switch(scrollState){
//                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE://空闲状态
//
//                        break;
//                    case AbsListView.OnScrollListener.SCROLL_STATE_FLING://滚动状态
//
//                        break;
//                    case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL://触摸后滚动
//
//                        break;
//                }
//            }
//
//            @Override
//            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//                activity.setanimation();
//            }
//        });
    }

    @Override
    public void startProcessBar() {

    }

    @Override
    public void hideProcessBar() {

    }

    @Override
    public void showNetError() {
        Utils.makeToast(getActivity(), "错误");
    }

    @Override
    public void startNextActivity() {
        if(!aaa){
            page +=1;
            E_wallet_list.get_e_wallet_list(projectAdapter.handler, type, page, PAGE_SIZE);
        }else{
            page =1;//刷新时把页数初始化
            E_wallet_list.get_e_wallet_list(projectAdapter.handler, type, 1, PAGE_SIZE);
        }
    }

    class ProjectAdapter1 extends BaseAdapter implements  RefreshAndLoadMoreListView.OnLoadMoreListener,
            AdapterView.OnItemClickListener,  RefreshAndLoadMoreListView.OnRefreshListener{
        public ProjectAdapter1(){projects = new ArrayList<E_records>();}
        public Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                try {
                    JsonBean jsonBean = (JsonBean) msg.obj;
                    if(!Utils.filtrateCode(getActivity(),jsonBean)){
                        listview.onRefreshComplete();
                        return;
                    }
                    JSONObject json = new JSONObject(jsonBean.getJsonString());
                    Log.e(TAG, type + "返回列表数据" + json);
                    if(json.optString("success").equals("1")) {
                        JSONArray list = json.optJSONArray("list");
                        int size = 0;
                        if (list != null) {
                            ArrayList<E_records> inves = new ArrayList<E_records>();
                            if(type == 1 && (aaa ||projects.size() == 0)){//type == 1时是全部记录
                                inves.add(new E_records(json,1));
                            }
                            size = list.length();
                            JSONObject invesJson;
                            for (int i = 0; i < size; i++) {
                                invesJson = list.optJSONObject(i);
                                inves.add(new E_records(invesJson,0));
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
                        }
                    }else{

                    }
                    if(projects.size() ==0 ){
                        listview.noItemTip();
                        listview.noMore(false,false);
                    }
                    listview.onRefreshComplete();
                } catch (JSONException e) {
                    e.printStackTrace();
                    getActivity().finish();
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
            if(type == 1){
                if(position == 0){
                    type1 = 1;
                }else{
                    type1 = 0;
                }
            }
            return type1;
        }
        @Override
        public int getViewTypeCount() {
            return type == 1 ? 2 : 1;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder0 holder;
            ViewHolder1 holder1;
            E_records inves=projects.get(position);
            int layoutType = getItemViewType(position);
            switch (layoutType) {
                case 0:
                    if (null == convertView) {
                        holder = new ViewHolder0();
                        convertView = LinearLayout.inflate(getActivity(), R.layout.item_e_wallet_record, null);
                        holder.name = (TextView) convertView.findViewById(R.id.name);
                        holder.time = (TextView) convertView.findViewById(R.id.time);
                        holder.money = (TextView) convertView.findViewById(R.id.money);
                        holder.state = (TextView) convertView.findViewById(R.id.state);
                        convertView.setTag(holder);
                    } else {
                        holder = (ViewHolder0) convertView.getTag();
                    }
                    holder.name.setText(inves.getOperation());
                    holder.time.setText(inves.getTime());
                    if (inves.getAmount() > 0) {
                        holder.money.setText("+" + inves.getAmount());
                        holder.money.setTextColor(getResources().getColor(R.color.money_asset_text));
                    } else {
                        holder.money.setText(inves.getAmount() + "");
                        holder.money.setTextColor(getResources().getColor(R.color.save_or_draw_money_layout_text));
                    }
                    switch (inves.getProgress()){
                        case "-1"://转出失败
                            holder.state.setText("取钱失败");
                        case "1"://转出中
                            holder.state.setVisibility(View.VISIBLE);
                            break;
                        default:
                            holder.state.setVisibility(View.GONE);
                    }
                    break;
                case 1:
                    if (null == convertView) {
                        holder1 = new ViewHolder1();
                        convertView = LinearLayout.inflate(getActivity(), R.layout.itme_e_wallet_1, null);
                        holder1.eTotal = (TextView) convertView.findViewById(R.id.eTotal);
                        holder1.eBalance = (TextView) convertView.findViewById(R.id.eBalance);
                        holder1.receivableInterest = (TextView) convertView.findViewById(R.id.receivableInterest);
                        convertView.setTag(holder1);
                    } else {
                        holder1 = (ViewHolder1) convertView.getTag();
                    }
                    holder1.eTotal.setText(inves.geteTotal());
                    holder1.eBalance.setText(inves.geteBalance());
                    holder1.receivableInterest.setText(inves.getReceivableInterest());
                    break;
            }
            return convertView;
        }
        protected class ViewHolder0{
            TextView name,time,money,state;

        }
        private class  ViewHolder1{
            TextView eTotal,eBalance,receivableInterest;//本金+利息，本金，利息
        }


        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {//选择item触发事件

        }

        @Override
        public void onLoadMore() {
            aaa = false;
            presenter.didFinishLoading(getActivity());
        }
        @Override
        public void onRefresh() {
            aaa = true;
            presenter.didFinishLoading(getActivity());
        }
    }
}
