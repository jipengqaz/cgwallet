package cgtz.com.cgwallet.paymoney;


import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.math.BigDecimal;
import java.util.ArrayList;

import cgtz.com.cgwallet.R;

/**
 * Created by Administrator on 2015/12/24.
 */
public class GVAdapter extends BaseAdapter {
    private Context context;
    private  ArrayList<ProductsBean> list;

    public GVAdapter (Context context , ArrayList<ProductsBean> list){
        this.context = context;
        this.list = list;
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView ==null){
            convertView =  View.inflate(context, R.layout.item_gv_adapter,null);
        }
        PayViewHolder holder = PayViewHolder.getPayViewHolder(convertView);


        if (list.get(position).isOnSale()){

            holder.ll_pay.setBackgroundResource(R.drawable.bg_linearlayout_shape);
            holder.tv_item_price.setText(list.get(position).getParValue() + "元");
            holder.tv_item_price.setTextColor(Color.parseColor("#33cc99"));
           // holder.tv_item_payprice.setVisibility(View.VISIBLE);  String cuAmount = new BigDecimal(newAmount).setScale(2).toPlainString();
            String newPrice=new BigDecimal(list.get(position).getSalePrice()).setScale(2).toString();
            holder.tv_item_payprice.setText("售价:" + newPrice+"元");

            holder.tv_item_payprice.setTextColor(Color.parseColor("#33cc99"));
           //list.get(position).setOnSale(false);
        }else{
            holder.tv_item_price.setText(list.get(position).getParValue()+"元");
            holder.tv_item_payprice.setText("暂无");
            holder.ll_pay.setBackgroundResource(R.drawable.bg_linearlayout_shape_gray);
        }

        return convertView;
    }

    static class PayViewHolder{
        TextView tv_item_payprice,tv_item_price;
        LinearLayout ll_pay;
        public PayViewHolder(View convertView){
            tv_item_payprice =(TextView) convertView.findViewById(R.id.tv_item_payprice);
            tv_item_price =(TextView) convertView.findViewById(R.id.tv_item_price);
            ll_pay = (LinearLayout) convertView.findViewById(R.id.ll_pay);
        }

        public static PayViewHolder getPayViewHolder(View convertView){
            PayViewHolder holder = (PayViewHolder) convertView.getTag();
            if(holder ==null){
                holder=new PayViewHolder(convertView);
                convertView.setTag(holder);
            }
            return holder;
        }
    }
}
