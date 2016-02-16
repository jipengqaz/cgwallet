package cgtz.com.cgwallet.paymoney;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import cgtz.com.cgwallet.R;

/**
 * Created by 朋 on 2015/12/31.
 */
public class DialogAdapter extends BaseAdapter {
    public DialogAdapter(Context context, List<LinkManBean> list) {
        this.context = context;
        this.list = list;
    }

    private Context context;
    private List<LinkManBean> list;

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup viewGroup) {
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item_lv_adapter, null);
        }
        final DialogViewHolder viewHolder = DialogViewHolder.getDialogViewHolder(convertView);
        viewHolder.tv_name.setText(list.get(position).getUserName());
        viewHolder.tv_number.setText(list.get(position).getUserNumber());
        viewHolder.cb_box.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (checkClick != null) {
                    checkClick.click(position);
                }
            }
        });

        return convertView;
    }

    public interface CheckboxClickLisener {
        void click(int position);
    }

    private CheckboxClickLisener checkClick;

    public void setCheckboxClickLisener(CheckboxClickLisener lisener) {
        this.checkClick = lisener;
    }

    static class DialogViewHolder {
        TextView tv_name, tv_number;
        LinearLayout ll_phone;
        CheckBox cb_box;

        public DialogViewHolder(View convertView) {
            tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            tv_number = (TextView) convertView.findViewById(R.id.tv_number);
            ll_phone = (LinearLayout) convertView.findViewById(R.id.ll_phone);
            cb_box = (CheckBox) convertView.findViewById(R.id.cb_box);

        }

        public static DialogViewHolder getDialogViewHolder(View convertView) {
            DialogViewHolder holder = (DialogViewHolder) convertView.getTag();
            if (holder == null) {
                holder = new DialogViewHolder(convertView);
                convertView.setTag(holder);
            }
            return holder;
        }
    }
}
