package cgtz.com.cgwallet.widget;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.List;

import cgtz.com.cgwallet.R;
import cgtz.com.cgwallet.activity.LoginActivity;
import cgtz.com.cgwallet.bean.LoginMobileBean;
import cgtz.com.cgwallet.utils.LogUtils;
import cgtz.com.cgwallet.utils.Utils;

/**
 * 自定义登录手机号列表
 * Created by ryan on 15/6/18.
 */
public class LoginPopupwindow extends PopupWindow {
    private static final String TAG = "LoginPopupwindow";
    private EditText mobileView;
    private LoginActivity context;
    private List<LoginMobileBean> lists;
    private View parentView;
    public LoginPopupwindow(final LoginActivity context,View parentView, final EditText mobileView,List<LoginMobileBean> lists) {
        this.context = context;
        this.parentView = parentView;
        this.mobileView = mobileView;
        this.lists = lists;
        LayoutInflater inflater =
                (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contextView = inflater.inflate(R.layout.layout_login_mobile_popupwindow,null);
        ListView listView = (ListView) contextView.findViewById(R.id.login_popwindow);
        PopAdapter adapter = new PopAdapter(context,lists);
        listView.setAdapter(adapter);
        //获取输入框外层布局的宽度
        int parentWidth = parentView.getWidth();
        LogUtils.e(TAG,"parentwidth:"+parentWidth);
        this.setContentView(contextView);
        this.setWidth(parentWidth);//设置popupwindow宽度
        this.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);//设置高度
        //popupwindow可点击
        this.setFocusable(true);//
        this.setOutsideTouchable(true);
        this.update();//刷新状态
        //实力化半透明颜色
        ColorDrawable cDrawable = new ColorDrawable(00000000);
        // 点back键和其他地方使其消失,设置了这个才能触发OnDismisslistener ，设置其他控件变化等操作
        this.setBackgroundDrawable(cDrawable);
        // 设置SelectPicPopupWindow弹出窗体动画效果
//        this.setAnimationStyle();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LoginMobileBean mobileBean = (LoginMobileBean) parent.getAdapter().getItem(position);
                context.beforeMobile = mobileBean.getMobile();
                mobileView.setText(Utils.getHasStarsMobile(mobileBean.getMobile()));
                hidePop();
            }
        });
    }

    public void showPop(){
        this.showAsDropDown(parentView);
    }

    public void hidePop(){
        this.dismiss();
    }


    private class PopAdapter extends BaseAdapter{
        private List<LoginMobileBean> lists;
        private LayoutInflater inflater;
        public PopAdapter(Context context,List<LoginMobileBean> lists){
            this.lists = lists;
            inflater =
                    (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        @Override
        public int getCount() {
            return lists != null?lists.size():0;
        }

        @Override
        public Object getItem(int position) {
            return lists != null?lists.get(position):null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = new ViewHolder();
            TextView mobileText;
            if(convertView == null){
                convertView = inflater.inflate(R.layout.item_login_popwindwo,null);
                mobileText = (TextView) convertView.findViewById(R.id.tv_login_popwindow_item_text);
                viewHolder.mobileText = mobileText;
                convertView.setTag(viewHolder);
            }else{
                viewHolder = (ViewHolder) convertView.getTag();
                mobileText = viewHolder.mobileText;
            }
            if(lists != null){
                LoginMobileBean bean = lists.get(position);
                mobileText.setText(Utils.getHasStarsMobile(bean.getMobile()));
            }
            return convertView;
        }

        class ViewHolder{
            public TextView mobileText;
        }
    }
}
