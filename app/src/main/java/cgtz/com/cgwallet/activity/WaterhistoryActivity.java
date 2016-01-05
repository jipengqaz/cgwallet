package cgtz.com.cgwallet.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import cgtz.com.cgwallet.R;
import cgtz.com.cgwallet.bean.JsonBean;
import cgtz.com.cgwallet.domain.DetailInfo;
import cgtz.com.cgwallet.presenter.SplashPresenter;
import cgtz.com.cgwallet.utility.Constants;
import cgtz.com.cgwallet.utils.CustomTask;
import cgtz.com.cgwallet.utils.LogUtils;
import cgtz.com.cgwallet.utils.Utils;
import cgtz.com.cgwallet.view.ISplashView;
import cgtz.com.cgwallet.widget.ProgressDialog;

/**
 * 水费历史账单界面
 * Created by chen on 2015-10-27.
 */
public class WaterhistoryActivity extends BaseActivity implements View.OnClickListener, ISplashView {

    private ListView lvhistory;
    private ArrayList<DetailInfo> list;
    private MyAdapter adapter;
    private SplashPresenter presenter;
    private ProgressDialog progressDialog;
    private TextView historyGone;//暂无账单记录

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showBack(true);
        setTitle("水费");
        setContentView(R.layout.activity_water_history);
        init();
        presenter = new SplashPresenter(this);
        presenter.didFinishLoading(WaterhistoryActivity.this);
    }

    public void init() {
        lvhistory = (ListView) findViewById(R.id.lv_history);
        lvhistory.setVisibility(View.GONE);
        list = new ArrayList<>();
        historyGone = (TextView) findViewById(R.id.tv_history_gone);
        adapter = new MyAdapter();
        lvhistory.setAdapter(adapter);
    }

    private class MyAdapter extends BaseAdapter {

        /**
         * 返回一个数字,表示listview中显示条目的个数
         */
        @Override
        public int getCount() {
            return list.size();
        }

        /**
         * 根据位置得到item的界面
         * position 位置,当前条目在listview中的索引位置,从0开始
         * convertView 放在缓存中的view对象
         * parent 条目的父级控件
         */
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View view = null;
            if (convertView != null) {
                view = convertView;
            } else {
                view = View.inflate(WaterhistoryActivity.this, R.layout.item_histoy_list, null);
            }

            // 给view界面中的控件填充数据
            TextView tv_money = (TextView) view.findViewById(R.id.tv_money);//金额
            TextView tv_state = (TextView) view.findViewById(R.id.tv_state);//状态
            TextView tv_data = (TextView) view.findViewById(R.id.tv_data);//时间

            //  得到list集合中一个对象
            DetailInfo info = list.get(position);
            tv_money.setText(info.getValue());
            tv_state.setText(info.getState());
            tv_data.setText(info.getTime());

            //  返回view对象
            return view;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void startProcessBar() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(WaterhistoryActivity.this, R.style.loading_dialog);
        }
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        progressDialog.show();
    }

    @Override
    public void hideProcessBar() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void showNetError() {
        Utils.makeToast(this, Constants.IS_EVENT_MSG);
    }

    @Override
    public void startNextActivity() {
        if (!(getIntent().getIntExtra("orderType", 4) == 4)) {
            int orderType = getIntent().getIntExtra("orderType", 1);
            switch (orderType) {
                case 1:
                    setTitle("水费");
                    getData(mHandler, 1, 1);
                    break;
                case 2:
                    setTitle("电费");
                    getData(mHandler, 2, 1);
                    break;
                case 3:
                    setTitle("燃气费");
                    getData(mHandler, 3, 1);
                    break;
            }
        }
        if (!(getIntent().getStringExtra("title") == null)) {
            setTitle(getIntent().getStringExtra("title"));
            String title = getIntent().getStringExtra("title");
            switch (title) {
                case "水费":
                    getData(mHandler, 1, 1);
                    break;
                case "电费":
                    getData(mHandler, 2, 1);
                    break;
                case "燃气费":
                    getData(mHandler, 3, 1);
                    break;
            }
//        getData(mHandler, 1, 1);
        }
    }

    public static void getData(Handler mHandler, int type, int page) {
        //服务器数据交互操作
        HashMap<String, String> maps = new HashMap<>();
        maps.put("user_id", Utils.getUserId());
        maps.put("token", Utils.getToken());
        maps.put("type", type + "");
        maps.put("page", page + "");
        //这里参数的含义
        CustomTask task = new CustomTask(mHandler, Constants.WHAT_QUERY_ONETYPEORDER,
                Constants.SEAWAY_QUERY_ONETYPEORDER,
                true, maps, true);
        task.execute();
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            try {
                JsonBean jsonBean = (JsonBean) msg.obj;
                int code = jsonBean.getCode();
                String errorMsg = jsonBean.getError_msg();
                if (code == Constants.DATA_EVENT) {
                    hideProcessBar();
//                    Utils.makeToast(WaterhistoryActivity.this,Constants.ERROR_MSG_CODE+code);
                    return;
                }
                int action = msg.what;
                switch (action) {
                    case Constants.WHAT_QUERY_ONETYPEORDER:
                        boolean flag = Utils.filtrateCode(WaterhistoryActivity.this, jsonBean);
                        if (flag && code == Constants.OPERATION_FAIL) {//数据交互失败
//                            Utils.makeToast(WaterhistoryActivity.this, errorMsg);
                        } else if (flag && code == Constants.OPERATION_SUCCESS) {//数据交互成功
//                            JSONObject jsonObject = new JSONObject(jsonBean.getJsonString());
                            JSONObject json = jsonBean.getJsonObject();
                            JSONObject result = json.optJSONObject("result");
                            JSONArray pageList = result.getJSONArray("pageList");
                            if (pageList.length() == 0) {
                                lvhistory.setVisibility(View.GONE);
                            } else {
                                historyGone.setVisibility(View.GONE);
                                lvhistory.setVisibility(View.VISIBLE);
                                for (int i = 0; i < pageList.length(); i++) {
                                    JSONObject jsonObject = pageList.getJSONObject(i);
                                    DetailInfo info = new DetailInfo();
                                    info.setState("已缴费");
/*                                switch (jsonObject.getInt("status")){
                                    case 1:
                                        info.setState("待支付");
                                        break;
                                    case 2:
                                        info.setState("处理中");
                                        break;
                                    case 3:
                                        info.setState("已缴费");
                                        break;
                                    case -1:
                                        info.setState("已取消");
                                        break;
                                    case -2:
                                        info.setState("缴费失败");
                                        break;
                                }*/
//                                info.setTime(jsonObject.getString("gmtCreated"));
                                    String newDate = jsonObject.getString("gmtCreated").substring(0, 10);
                                    info.setTime(newDate);
                                    info.setValue(jsonObject.getString("amount"));
                                    list.add(info);
                                }
                            }
                            adapter.notifyDataSetChanged();
//                            LogUtils.e("-----------------历史订单------------------", "result:" + result);
                        }
                        hideProcessBar();
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
                hideProcessBar();
                LogUtils.e("WaterhistoryActivity", "handler 异常");
            }
        }
    };

}
