package cgtz.com.cgwallet.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import cgtz.com.cgwallet.R;
import cgtz.com.cgwallet.activity.SaveMoneyActivity;
import cgtz.com.cgwallet.client.Withdraw_money_Client;
import cgtz.com.cgwallet.utility.Constants;
import cgtz.com.cgwallet.widget.ProgressDialog;

/**
 *Ìø×ª´æÇ®ºÍÈ¡Ç®fragment
 * Created by Administrator on 2015/6/17.
 */
public class Save_or_draw_money_Fragment extends BaseFragment{
    private ProgressDialog progressDialog;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view  = inflater.inflate(R.layout.layout_save_or_draw_money,container,false);
        TextView layout_draw_money = (TextView) view.findViewById(R.id.layout_draw_money);
        TextView saveLayout = (TextView) view.findViewById(R.id.layout_save_money);
        saveLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().startActivity(new Intent(getActivity(), SaveMoneyActivity.class));
            }
        });
        if(progressDialog == null){
            progressDialog = new ProgressDialog(getActivity(),R.style.loading_dialog);
        }
        layout_draw_money.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Withdraw_money_Client.getWithdraw_money(getActivity(), Constants.WHAT_WITHDRAW, progressDialog);
            }
        });
        return view;
    }
}
