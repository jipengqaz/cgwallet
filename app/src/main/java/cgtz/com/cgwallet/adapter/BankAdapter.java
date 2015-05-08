package cgtz.com.cgwallet.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import cgtz.com.cgwallet.R;
import cgtz.com.cgwallet.bean.Bank;
import cgtz.com.cgwallet.bean.BankCard;


/**
 * 绑定银行卡  银行列表适配器
 * Created by Administrator on 2014/10/18.
 */
public class BankAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater mInflater;
    private ArrayList<Bank> lists;
    public BankAdapter(Context context, ArrayList<Bank> lists){
        this.context = context;
        this.lists = lists;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return lists == null? 0:lists.size();
    }

    @Override
    public Object getItem(int i) {
        return lists == null?null:lists.get(i);
    }

    @Override
    public long getItemId(int i) {
        return lists == null?0:i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Holder holder = null;
        if(view == null){
            holder = new Holder();
            view = mInflater.inflate(R.layout.bank_card_list,null);
            holder.bankName = (TextView) view.findViewById(R.id.bank);
            holder.bank_image = (ImageView) view.findViewById(R.id.bank_image);
            view.setTag(holder);
        }else{
            holder = (Holder) view.getTag();
        }
        Bank bank = lists.get(i);
        holder.bankName.setText(bank.getName());
        if(bank.getId()!=0){
            holder.bank_image.setImageResource(BankCard.getBankIcon(bank.getId()));
            holder.bank_image.setVisibility(View.VISIBLE);
        }
        return view;
    }

    private class Holder{
        private TextView bankName;
        private ImageView bank_image;
    }
}
